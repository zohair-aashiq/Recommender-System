/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.datamanager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.config.VsmCfRecConfig;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.storage.Storage;
import org.eclipse.rdf4j.recommender.storage.index.spark.impl.CfSparkStorage;
import org.eclipse.rdf4j.recommender.storage.index.spark.SparkStorage;
import org.eclipse.rdf4j.recommender.util.IndexedRatedResRatingComparator;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import com.google.common.collect.MinMaxPriorityQueue;
import org.eclipse.rdf4j.recommender.config.CfRecConfig;
import org.eclipse.rdf4j.recommender.util.ListOperations;
import scala.Tuple2;

// TODO SparkDataManager or SparkBasedDataManager ?
public class SparkDataManager implements DataManager {

    /**
     * It requires a configuration and a storage.
     */
    private final RecConfig config;
    private SparkStorage storage;
    private SailRepository sailRep = null;
    private RepositoryConnection con = null; 
    private boolean hasPreprocessed = false;
    
    /**
     * To compare Indexed rated resources only based on the 
     * "rating" without taking into account the rated item.
     */ 
    private final Comparator<IndexedRatedRes> comparatorOfIrrBasedOnRating = new IndexedRatedResRatingComparator();
        
    public SparkDataManager(RecConfig config) {
        this.config = config;   
        storage = new CfSparkStorage();
    }
    
    @Override
    public void setStorage(Storage storage) {
        if( !(storage instanceof SparkStorage) )
            throw new RecommenderException("GIVEN STORAGE IS NOT A SPARKSTORAGE!");
        this.storage = (SparkStorage) storage;
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public void init(SailRepository repository) throws RecommenderException {
        try {                
            this.sailRep = repository;
            con = repository.getConnection();
            initStorage();
            con.close();                                      
        } catch (RepositoryException ex) {
                throw new RecommenderException(ex);
        }
    }

    @Override
    public void initStorage() throws RecommenderException {

        //populate data structure
        Long start = System.currentTimeMillis();
        populateStorage();
        Long end = System.currentTimeMillis();
        Long connectionTime = end - start;            
        System.out.println("Time to populate data structures: " + (connectionTime) 
                + "ms (ca. " + (connectionTime) / 1000.0 + " secs).");                
        System.out.println("DATA STRUCTURES POPULATED... COMPLETED");

        //normalize ratings directly on datastructure
        start = System.currentTimeMillis();
        normalizeRatings();
        end = System.currentTimeMillis();
        connectionTime = end - start;                
        System.out.println("Time to normalize the ratings: " + (connectionTime) 
                + "ms (ca. " + (connectionTime) / 1000.0 + " secs).");                        
        System.out.println("RATINGS NORMALIZATION... COMPLETED");  
        
        
        Long startL2 = null;
        Long endL2 = null;
        Long connectionTimeL2 = null;

        //The following step applies only to VSM.
        if (getRecConfig() instanceof VsmCfRecConfig 
            && ((VsmCfRecConfig)getRecConfig()).getSimMetric() == RecSimMetric.COSINE) {

            //computes the l-norm for each user.
            startL2 = System.currentTimeMillis();
            computeL2Norm();
            endL2 = System.currentTimeMillis();
            connectionTimeL2 = endL2 - startL2;                
            System.out.println("Time to compute the l2-norms: " + (connectionTimeL2) 
                    + "ms (ca. " + (connectionTimeL2) / 1000 + " secs).");
            System.out.println("L2-NORM COMPUTATION... COMPLETED");
        }        
        

        // Builds the inverted index from the stored and preprocessed data this applies 
        // to both kinds of storage, inv lists and scaled inverted lists.
        start = System.currentTimeMillis();
        buildInvertedLists();
        end = System.currentTimeMillis();
        connectionTime = end - start;
        System.out.println("Time to build and optimize the inverted lists: " + (connectionTime) 
            + "ms (ca. " + (connectionTime) / 1000 + " secs).");
        System.out.println("INV LISTS... COMPLETED");
    }
    
    /**
     * It calculates the l2 norm for each user and stores this information
     * for later retrieval. Useful only for VSM with COSINE as the 
     * similarity metric. 
     */
    //TODO does this applies to content-based too?
    protected void computeL2Norm() {
        VsmCfRecConfig vsmConfig = (VsmCfRecConfig)getRecConfig();
        Set<Integer> keySet = null;
        Iterator<Integer> keySetIt = null;

        switch(vsmConfig.getSimMetric()) {
            case COSINE:
                //we need to iterate over UserRatedItemsMap and divide each of the ratings
                //by the l-2 norm of the vector
                keySet = storage.getAllUserIndexes();
                keySetIt = keySet.iterator();
                
                List<Tuple2<Integer,Double>> l2NormOfUserList = new ArrayList<>();
                Map<Integer, Set<IndexedRatedRes>> existingTupleMap = storage.getUserRatedItemsMap();
                
                while (keySetIt.hasNext()) {
                    Integer currentUserId = keySetIt.next();
                    Double ratingSquareSum = 0.0;
                    Double l2Norm = 0.0;
                    IndexedRatedRes newRes = null;
                    Set<IndexedRatedRes> resSet = existingTupleMap.get(currentUserId);

                    for (IndexedRatedRes currentRes : resSet){
                            ratingSquareSum = ratingSquareSum + Math.pow(currentRes.getRating(), 2);
                    }
                    l2Norm = Math.sqrt(ratingSquareSum);
                    //finally we replace the list with the normalized list
                    l2NormOfUserList.add(new Tuple2(currentUserId, l2Norm));
                }                                
                storage.setL2NormsOfUsers(l2NormOfUserList);
            break;
            //TODO cased PEARSON CORRELATION          
        }     
    }

    @Override
    public void preprocess() throws RecommenderException {
        try {
            Long start = System.currentTimeMillis();
            
            if (config.getRecEntity(RecEntity.RATING) != null) {
                preprocessWithRatings();
            } 
            else {
                preprocessWithoutRatings();
            }
            
            con.close();
            hasPreprocessed = true;
            
            Long end = System.currentTimeMillis();
            Long connectionTime = end - start; 
            System.out.println("Time to preprocess: " + (connectionTime) 
                    + "ms (ca. " + (connectionTime) / 1000.0 + " secs).");                        
        System.out.println("PREPROCESSING... COMPLETED");  
        } catch (RepositoryException ex) {
                throw new RecommenderException(ex);
        }
    }

    @Override
    public boolean hasPreprocessed() {
        return hasPreprocessed;
    }

    @Override
    public void preprocessWithRatings() {

        if (getRecConfig().getRecParadigm()== RecParadigm.USER_COLLABORATIVE_FILTERING) {            
            //builds user neighborhoods
            Long start = System.currentTimeMillis();
            buildUserNeiborhood();
            Long end = System.currentTimeMillis();
            Long connectionTime = end - start;
            System.out.println("Time to compute the neighborhood: " + (connectionTime) 
                    + "ms (ca. " + (connectionTime) / 1000 + " secs).");
            System.out.println("COMPUTATION OF NEIGHBORHOODS... COMPLETED");                               
        }  
        else {
            // TODO what to do?
        }
    }

    @Override
    public void preprocessWithoutRatings() {

        if (getRecConfig().getRecParadigm()== RecParadigm.USER_COLLABORATIVE_FILTERING) {
            //builds user neighborhoods
            Long start = System.currentTimeMillis();
            buildUserNeiborhood();
            Long end = System.currentTimeMillis();
            Long connectionTime = end - start;
            System.out.println("Time to compute the neighborhood: " + (connectionTime) 
                    + "ms (ca. " + (connectionTime) / 1000 + " secs).");
            System.out.println("COMPUTATION OF NEIGHBORHOODS... COMPLETED");                               
        }
    }

    
    /** 
     * K-nearest-algorithm to compute the neighborhood.
     */
    protected void buildUserNeiborhood() {
        
        CfRecConfig cfConfig = (CfRecConfig)getRecConfig();

        Set<Integer> usersIdSet = null;
        Iterator<Integer> usersIdIt = null;
        Integer currentUserId;
        //User's rated resources
        Set<IndexedRatedRes> userRatedResSet = null;
        Iterator<IndexedRatedRes> userRatedResIt = null;
        IndexedRatedRes currentRatRes = null;
        InvertedList invList = null;
        //Neighborhood
        IndexedRatedRes neighbor = null;
        MinMaxPriorityQueue<IndexedRatedRes> neighborhood = null;   
        //To compute neighborhood
        InvertedList[] invListsOfUser = null;
        InvertedList[] invListsUser = null;
        double[] userRatings = null;
        IndexedRatedRes[] neighborhoodArray =  null; 

        CfSparkStorage cfSparkStorage = (CfSparkStorage) storage;
        //Classic neighborhood computed with inverted indexes
        //I need to iterate over all users and over all rated items
        //of each user.

        //We start getting all users
        usersIdSet = storage.getAllUserIndexes();                
        usersIdIt = usersIdSet.iterator();
        Map<Integer, Set<IndexedRatedRes>> existingTupleMap2 = storage.getUserRatedItemsMap();
        Map<Integer, Double> usersL2NormsMap = storage.getUsersL2NormsMap();
                
        //For each user...
        while (usersIdIt.hasNext()) {
            currentUserId = usersIdIt.next();
            //I load the set of rated items.
            userRatedResSet = existingTupleMap2.get(currentUserId);
            invListsOfUser = new InvertedList[userRatedResSet.size()];
            userRatings = new double[userRatedResSet.size()];
            userRatedResIt = userRatedResSet.iterator();

            int numberOfLists = 0;
            while (userRatedResIt.hasNext()) {
                currentRatRes = userRatedResIt.next();
                invList = cfSparkStorage.getInvertedListOfItem(currentRatRes.getResourceId());
                invListsOfUser[numberOfLists] = invList;
                userRatings[numberOfLists] = currentRatRes.getRating();
                numberOfLists++;
            }

            //long startForUser = System.currentTimeMillis();                        
            neighborhood =  ListOperations.computeNeighborhood(
                currentUserId, userRatings,
                usersL2NormsMap,
                invListsOfUser,
                cfConfig.getNeighborhoodSize(),
                getRecConfig().getDecimalPlaces());
            //long endForUser = System.currentTimeMillis();

            neighborhoodArray = neighborhood.toArray(new IndexedRatedRes[neighborhood.size()]);
            Arrays.sort(neighborhoodArray, comparatorOfIrrBasedOnRating);
            cfSparkStorage.storeNeighborhood(currentUserId, neighborhoodArray);                       
        
        }           
    }
    
    
    
    @Override
    public void populateStorage() throws RecommenderException {
        
        try {
            String userResource = null;
            String itemResource = null;
            Double ratingResource = -1.0;
            TupleQuery tupleQuery = null;
            TupleQueryResult result = null;       
            String preprocessingSPARQLQuery = null;
            int resourceCounter = 0;        
            List<Tuple2<Integer,String>> resourceToAddList = new ArrayList<>();
            List<Tuple2<Integer,Set<IndexedRatedRes>>> ratedItemsToAddList = new ArrayList<>();
        
            //Execute query to get and preprocess the data.
            //It might be the case that a user has rated an item several times. 
            //In that case we consider the average of the ratings
            //assigned to the same object

            //TODO
            //This query uses GROUP BY which requires a lot of memory to compute.
            //Therefore I'll change this and use a normal query instead.
            /*
            String preprocessingSPARQLQuery =
            "SELECT " + config.getRecEntity(RecEntity.USER) + " "
            + config.getRecEntity(RecEntity.RAT_ITEM) + " "
            + "(AVG(xsd:double(" + config.getRecEntity(RecEntity.RATING) + ")) as ?avgRat) \n"
            + "WHERE {\n"
            +       config.getRatGraphPattern()
            + "}"
            + "GROUP BY " + config.getRecEntity(RecEntity.USER) + " "
            + config.getRecEntity(RecEntity.RAT_ITEM) + " ";
            */
            
            //HERE THE FOUR CASES ARE REFLECTED:
            //Case 1) Ratings are used.
            //Case 2) Only positive feedback is provided
            //Case 3) Only negative feedback is provided //not supported yet 
            //(at the moment an exception is thrown for this kind of configuration)
            //Case 4) Both, positive and negative feedback is provided.
            
            /*
            if (config.getRecEntity(RecEntity.RATING) != null) {
                preprocessingSPARQLQuery =
                    "SELECT " + config.getRecEntity(RecEntity.USER) + "\n"
                    + config.getRecEntity(RecEntity.RAT_ITEM) + "\n"
                    + config.getRecEntity(RecEntity.RATING) + "\n"
                    + "WHERE {\n"
                    +       config.getRatGraphPattern()
                    + "}";
            } 
            else {
                preprocessingSPARQLQuery =
                    "SELECT " + config.getRecEntity(RecEntity.USER) + "\n"
                    + config.getRecEntity(RecEntity.RAT_ITEM) + "\n"
                    + "WHERE {\n"
                    +       config.getRatGraphPattern()
                    + "}";
            }
            */
            
            //Case 1)
            if (config.getRecEntity(RecEntity.RATING) != null) {
                    preprocessingSPARQLQuery =
                            "SELECT " + config.getRecEntity(RecEntity.USER) + "\n"
                            + config.getRecEntity(RecEntity.RAT_ITEM) + "\n"
                            + config.getRecEntity(RecEntity.RATING) + "\n"
                            + "WHERE {\n"
                            +       config.getRatGraphPattern()
                            + "}";
            } 
            //Case 2)
            else if (config.getRecEntity(RecEntity.POS_ITEM) != null &&
                            config.getRecEntity(RecEntity.NEG_ITEM) == null) {
                    preprocessingSPARQLQuery =
                            "SELECT " + config.getRecEntity(RecEntity.USER) + "\n"
                            + config.getRecEntity(RecEntity.POS_ITEM) + "\n"
                            + "WHERE {\n"
                            +       config.getPosGraphPattern()
                            + "}";
            }
            //Case 3)
            else if (config.getRecEntity(RecEntity.NEG_ITEM) != null &&
                            config.getRecEntity(RecEntity.POS_ITEM) == null) {
                    throw new RecommenderException("HAVING ONLY NEGATIVE FEEDBACK IS NOT SUPPORTED YET.");
            }
            //Case 4)
            else if (config.getRecEntity(RecEntity.NEG_ITEM) != null &&
                            config.getRecEntity(RecEntity.POS_ITEM) != null) {
                    throw new RecommenderException("HAVING BOTH; POSITIVE AND NEGATIVE FEEDBACK IS NOT SUPPORTED YET.");
            }

            tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, preprocessingSPARQLQuery);
            result = tupleQuery.evaluate();

            if (result.hasNext() == false)
                throw new RecommenderException("GRAPH PATTERN IS NOT MATCHING TO ANYTHING");

            while (result.hasNext()) {
                /*
                BindingSet bs = result.next();
                //We can access each of the variables configured:
                userResource = bs.getValue(config.getRecEntity(RecEntity.USER).replace("?", "")).stringValue();
                itemResource = bs.getValue(config.getRecEntity(RecEntity.RAT_ITEM).replace("?", "")).stringValue();
                if (config.getRecEntity(RecEntity.RATING) != null) {
                    ratingResource = Double.parseDouble(bs.getValue(config.getRecEntity(RecEntity.RATING).
                        replace("?", "")).stringValue());
                } else ratingResource = 1.0;
                */
                
                BindingSet bs = result.next();
                //We can access each of the variables configured:
                userResource = bs.getValue(config.getRecEntity(RecEntity.USER).replace("?", "")).stringValue();

                if (config.getRecEntity(RecEntity.RATING) != null) {
                        itemResource = bs.getValue(config.getRecEntity(RecEntity.RAT_ITEM).replace("?", "")).stringValue();
                        ratingResource = Double.parseDouble(bs.getValue(config.getRecEntity(RecEntity.RATING).
                                replace("?", "")).stringValue());
                } else if (config.getRecEntity(RecEntity.POS_ITEM) != null 
                                && config.getRecEntity(RecEntity.NEG_ITEM) == null) {
                        itemResource = bs.getValue(config.getRecEntity(RecEntity.POS_ITEM).replace("?", "")).stringValue();
                        ratingResource = 1.0;
                } else if (config.getRecEntity(RecEntity.NEG_ITEM) != null 
                                && config.getRecEntity(RecEntity.POS_ITEM) == null) {
                        //TODO
                } else if (config.getRecEntity(RecEntity.POS_ITEM) != null 
                                && config.getRecEntity(RecEntity.NEG_ITEM) != null) {
                        //TODO
                }

                //For the query with GROUP BY
                //ratingResource = Double.parseDouble(bs.getValue("avgRat").stringValue());
                                                
                // TODO discuss: this part can be changed to a file so that big data can be used
                int userId = -1;
                int itemId = -1;
                        
                for( int i = 0 ; i < resourceToAddList.size() ; i++ ) {
                    if( resourceToAddList.get(i)._2().equals(userResource) ) {
                        userId = resourceToAddList.get(i)._1();
                    }
                }                        
                for( int i = 0 ; i < resourceToAddList.size() ; i++ ) {
                    if( resourceToAddList.get(i)._2().equals(itemResource) ) {
                        itemId = resourceToAddList.get(i)._1();
                    }
                }

                if( userId == -1 ) {
                    userId = resourceCounter++;
                    resourceToAddList.add(new Tuple2(userId,userResource));
                }                
                if( itemId == -1 ) {
                    itemId = resourceCounter++;
                    resourceToAddList.add(new Tuple2(itemId,itemResource));
                }
                
                //we need to index the found resources or retrieve the used index     
                IndexedRatedRes ratRes = new IndexedRatedRes(itemId, ratingResource);
                
                boolean userFoundFlag = false;
                for( int i = 0 ; i < ratedItemsToAddList.size() ; i++ ) {
                    if( ratedItemsToAddList.get(i)._1() == userId ) {
                        ratedItemsToAddList.get(i)._2.add(ratRes);
                        userFoundFlag = true;
                        break;
                    }
                }
                if(!userFoundFlag) {
                    Set<IndexedRatedRes> newSet = new HashSet<>();
                    newSet.add(ratRes);
                    ratedItemsToAddList.add(new Tuple2(userId,newSet));
                }                
            }
            
            storage.setRatedResources(ratedItemsToAddList);
            storage.setResources(resourceToAddList);
            storage.setResourceCounter(resourceCounter);
            
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            throw new RecommenderException(ex);
        }
    }

    @Override
    public void normalizeRatings() {
        
        //Normalization:
        //We need to normalize the ratings according to the strategy selected:
        Set<Integer> keySet = null;
        Iterator<Integer> keySetIt = null;

        switch(config.getRecRatingNormStrategy()) {
            case MEAN_CENTERING:                                    
                //we need to iterate over UserRatedItemsMap
                keySet = storage.getAllUserIndexes();
                
                keySetIt = keySet.iterator();
                List<Tuple2<Integer,Set<IndexedRatedRes>>> newTupleList = new ArrayList<>();
                Map<Integer, Set<IndexedRatedRes>> existingTupleMap = storage.getUserRatedItemsMap();
                
                while (keySetIt.hasNext()){
                    
                    Integer currentUserId = keySetIt.next();
                    Double ratingSum = 0.0;
                    Integer numberOfRatings = 0;
                    Double userRatingAverage = 0.0;
                    IndexedRatedRes newRes = null;
                    Set<IndexedRatedRes> resSet = existingTupleMap.get(currentUserId);
                    Set<IndexedRatedRes> normResSet =  new HashSet<>(resSet.size());

                    for (IndexedRatedRes currentRes : resSet){
                        ratingSum = ratingSum + currentRes.getRating();
                        numberOfRatings++;
                    }
                    
                    userRatingAverage = ratingSum / numberOfRatings;
                    
                    for (IndexedRatedRes currentRes : resSet){
                        //newRes = new IndexedRatedRes(currentRes.getResourceId(), 
                        //        RoundingUtility.round((currentRes.getRating() - userRatingAverage), config.getDecimalPlaces()));
                        newRes = new IndexedRatedRes(currentRes.getResourceId(), 
                            (currentRes.getRating() - userRatingAverage));
                        normResSet.add(newRes);
                    }
                    //finally we replace the list with the normalized list
                    newTupleList.add(new Tuple2(currentUserId,normResSet));
                }
                
                storage.setRatedResources(newTupleList);
                
                break;
            case Z_SCORE:
                //we need to iterate over UserRatedItemsMap
                keySet = storage.getAllUserIndexes();
                keySetIt = keySet.iterator();
                List<Tuple2<Integer,Set<IndexedRatedRes>>> newTupleList2 = new ArrayList<>();
                Map<Integer, Set<IndexedRatedRes>> existingTupleMap2 = storage.getUserRatedItemsMap();
                
                while (keySetIt.hasNext()) {
                    Integer currentUserId = keySetIt.next();
                    Double ratingSum = 0.0;
                    Double sumOfSquares = 0.0;
                    Integer numberOfRatings = 0;
                    Double userRatingAverage = 0.0;                                                       
                    Double standardDeviation = 0.0;
                    IndexedRatedRes newRes = null;
                    Set<IndexedRatedRes> resSet = existingTupleMap2.get(currentUserId);
                    Set<IndexedRatedRes> normResSet =  new HashSet<>(resSet.size());

                    for (IndexedRatedRes currentRes : resSet){
                        ratingSum = ratingSum + currentRes.getRating();
                        numberOfRatings++;
                    }
                    
                    userRatingAverage = ratingSum / numberOfRatings;  
                    
                    for (IndexedRatedRes currentRes : resSet){
                        sumOfSquares = sumOfSquares + Math.pow(currentRes.getRating() - userRatingAverage, 2);
                    }
                    
                    standardDeviation = Math.sqrt(sumOfSquares / numberOfRatings);       
                    
                    for (IndexedRatedRes currentRes : resSet){
                        //newRes = new IndexedRatedRes(currentRes.getResourceId(), 
                        //        RoundingUtility.round(((currentRes.getRating() - userRatingAverage) / standardDeviation), config.getDecimalPlaces()));
                        newRes = new IndexedRatedRes(currentRes.getResourceId(), 
                                ((currentRes.getRating() - userRatingAverage) / standardDeviation));
                        normResSet.add(newRes);
                    }
                    
                    //finally we replace the list with the normalized list
                    newTupleList2.add(new Tuple2(currentUserId,normResSet));
                }
                
                storage.setRatedResources(newTupleList2);
                
            break;
        }
    }

    @Override
    public Set<RatedResource> getRatedResources(String userURI) throws RecommenderException {
        
        Set<RatedResource> ratResSet = new HashSet<>();
        int indexOfUser = storage.getIndexOf(userURI);

        // If this cannot be retrieved then throw an exception
        if (indexOfUser == -1){
            throw new RecommenderException("User resource was not found");
        }

        RatedResource ratRes = null;

        Set<IndexedRatedRes> indRatRes = storage.getIndexedRatedResOfUser(indexOfUser);
        for (IndexedRatedRes irr: indRatRes) {
            ratRes = new RatedResource(storage.getURI(irr.getResourceId()), irr.getRating());
            ratResSet.add(ratRes);
        }
        return ratResSet;
    }

    @Override
    public double getRatingAverageOfUser(String userURI) throws RecommenderException {
        
        // TODO Computing average should be also pushed to the preprocessing phase
        int indexOfUser = storage.getIndexOf(userURI);
        // If this cannot be retrieved then throw an exception
        if (indexOfUser == -1){
                throw new RecommenderException("User resource was not found");
        }

        int numberOfRatedRes = 0;
        double ratingSum = 0.0;
        Set<IndexedRatedRes> ratResources = storage.getIndexedRatedResOfUser(indexOfUser);

        for (IndexedRatedRes rr: ratResources) {
                ratingSum = ratingSum + rr.getRating();
                numberOfRatedRes++;
        }
        if (numberOfRatedRes == 0) 
            return 0;
        
        return ratingSum / (double) numberOfRatedRes;
    }

    @Override
    public Set<RatedResource> getNeighbors(String URI) throws RecommenderException {
        
        Set<RatedResource> neighborhood = new HashSet<>();

        int indexOfResource = storage.getIndexOf(URI);

        if (indexOfResource == -1){
            throw new RecommenderException("Resource was not found");
        }

        CfSparkStorage cfStorage = (CfSparkStorage) storage;
        IndexedRatedRes[] neighborhoodIndArray =  cfStorage.getNeighborhood(indexOfResource);
        RatedResource neighborRatRes =  null;

        for (IndexedRatedRes neighbor: neighborhoodIndArray) {
            if (neighbor != null) {
                neighborRatRes = new RatedResource(storage.getURI(neighbor.getResourceId()), neighbor.getRating());
                neighborhood.add(neighborRatRes);
            }
        }
        
        return neighborhood;
    }

    @Override
    public Set<String> getConsumedResources(String userURI) throws RecommenderException {
        
        Set<String> consumedResources = new HashSet<>();

        // Retrieve the index of user
        int indexOfUser = storage.getIndexOf(userURI);
        // If this cannot be retrieved then throw an exception
        if (indexOfUser == -1){
            throw new RecommenderException("User resource was not found");
        }                                 

        Set<IndexedRatedRes> indexedConsumedRes = storage.getIndexedRatedResOfUser(indexOfUser);

        if (indexedConsumedRes == null || indexedConsumedRes.size() < 1){
            throw new RecommenderException("User has not explicit ratings");                        
        }  

        indexedConsumedRes.stream().forEach((irr) -> {
            consumedResources.add(storage.getURI(irr.getResourceId()));
        });
        return consumedResources;
    }

    /**
     * Builds inverted lists. Just for storages based on inverted lists.
     */
    protected void buildInvertedLists() {
        
        Map<Integer, InvertedList> resInvertedLists = new HashMap<>();   
        
        Set<Integer> keySet = null;
        Iterator<Integer> keySetIt = null;

        //we need to iterate over UserRatedItemsMap
        keySet = storage.getAllUserIndexes();
        keySetIt = keySet.iterator();
        Map<Integer, Set<IndexedRatedRes>> existingTupleMap = storage.getUserRatedItemsMap();
        
        //for each user.
        while (keySetIt.hasNext()){
            Integer currentUserId = keySetIt.next();            
            
            Set<IndexedRatedRes> ratedResources = existingTupleMap.get(currentUserId);
            InvertedList userRatingArray = null;
            //Building inverted list
            for (IndexedRatedRes ratRes: ratedResources) {         
                    if (resInvertedLists.containsKey(ratRes.getResourceId())) {
                        userRatingArray = resInvertedLists.get(ratRes.getResourceId());     
                    } 
                    else {
                        userRatingArray = new InvertedList();
                        resInvertedLists.put(ratRes.getResourceId(), userRatingArray);
                    }
                    
                    userRatingArray.insert(new IndexedUserRating(currentUserId, ratRes.getRating()));
            }
        }
        resInvertedLists.values().stream().forEach((ur) -> {
            ur.compactAndSortArray();
        });
        
        List<Tuple2<Integer, InvertedList>> invListsToAdd = new ArrayList<>();
        for (Map.Entry<Integer, InvertedList> entry : resInvertedLists.entrySet()) {
            invListsToAdd.add(new Tuple2(entry.getKey(),entry.getValue()));
        }
        storage.addInvLists(invListsToAdd);
    }
        
    @Override
    public Set<String> getRecCandidates(String userURI) throws RecommenderException {
        
        Set<String> recCandidates = new HashSet<>();
        Set<IndexedRatedRes> neighborIndRatedRes;

        //Retrieve the index of user
        int indexOfUser = storage.getIndexOf(userURI);
        
        //If this cannot be retrieved then throw an exception
        if (indexOfUser == -1){
            throw new RecommenderException("User resource was not found");
        }

        if (getRecConfig().getRecParadigm()== RecParadigm.USER_COLLABORATIVE_FILTERING) {
            CfSparkStorage cfSilStorage = (CfSparkStorage) storage;

            if (hasPreprocessed()) {
                //We have to collect the candidate set from the neighbors
                for (IndexedRatedRes neighbor: cfSilStorage.getNeighborhood(indexOfUser)) {
                    neighborIndRatedRes = cfSilStorage.getIndexedRatedResOfUser(neighbor.getResourceId());

                    for (IndexedRatedRes candidate: neighborIndRatedRes) {
                        recCandidates.add(cfSilStorage.getURI(candidate.getResourceId()));
                    }
                } 
            } 
            else {
                //TODO ask
            }
        }
        
        return recCandidates;
    }

    @Override
    public double getResRelativeImportance(String node1, String node2) throws RecommenderException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RecConfig getRecConfig() {
        return config;
    }

    @Override
    public RepositoryConnection getConnection() {
        return con;
    }

    @Override
    public void releaseResources() {
        storage.stopSpark();
    }
}
