/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager;

import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.storage.IndexBasedStorage;
import org.eclipse.rdf4j.recommender.storage.Storage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;


public abstract class AbstractIndexBasedDataManager implements DataManager{
        /*--------*
	 * Fields *
	 *--------*/     
        /**
         * It requires a configuration and a storage.
         */
        private final RecConfig config;
        private IndexBasedStorage storage;
        public SailRepository sailRep = null;
        private RepositoryConnection con = null; 
        private boolean hasPreprocessed = false;


        /*-------------*
	 * Constructor *
	 *-------------*/
        
        /**
         * The constructor has an important role. This only keeps track of the 
         * configuration. Concrete classes will also have to create the storage
         * depending on the configured approach.
         * @param config 
         */
        public AbstractIndexBasedDataManager(RecConfig config) {
                this.config = config;                                                          
        }
        
        @Override
        public IndexBasedStorage getStorage() {
                return storage;
        }
        
        @Override
        public void setStorage(Storage storage) {
                this.storage = (IndexBasedStorage)storage;
        }
        
        @Override
        public void init(SailRepository sailRep) throws RecommenderException{                
                try {
                        this.sailRep = sailRep;
                        con = sailRep.getConnection();
                        initStorage();
                        con.close();
                } catch (RepositoryException ex) {
                        throw new RecommenderException(ex);
                }
        }        
        
        
        @Override
        public void initStorage() throws RecommenderException {                
                String logLine = new String();
                //Before normalizing the ratings we need to get the MIN
                //and MAX rating in the hole dataset.
                //seekMinRating(con);
                //seekMaxRating(con);
                
                //populate data structure
                Long start = System.currentTimeMillis();
                populateStorage();
                Long end = System.currentTimeMillis();
                Long connectionTime = end - start;            
                System.out.println("Time to populate data structures: "
                        + (connectionTime) + "ms (ca. " + (connectionTime) / 1000.0 
        		+ " secs).");                
                System.out.println("DATA STRUCTURES POPULATED... COMPLETED");
                
                //normalize ratings directly on datastructure
                start = System.currentTimeMillis();
                normalizeRatings();
                end = System.currentTimeMillis();
                connectionTime = end - start;                
                System.out.println("Time to normalize the ratings: "
        		+ (connectionTime) + "ms (ca. " + (connectionTime) / 1000.0 
        		+ " secs).");                        
                System.out.println("RATINGS NORMALIZATION... COMPLETED");  
        }
                
        @Override
        public void preprocess() throws RecommenderException {
                try {
                        Long start = System.currentTimeMillis();
                        if (config.getRecEntity(RecEntity.RATING) != null) {
                                preprocessWithRatings();
                        } else preprocessWithoutRatings();
                        con.close();
                        hasPreprocessed = true;
                        Long end = System.currentTimeMillis();
                        Long connectionTime = end - start; 
                        System.out.println("Time to preprocess: "
        		+ (connectionTime) + "ms (ca. " + (connectionTime) / 1000.0 
        		+ " secs).");                        
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
        public void populateStorage() throws RecommenderException {
                try {
                        String userResource = null;
                        String itemResource = null;
                        Double ratingResource = -1.0;
                        TupleQuery tupleQuery = null;
                        TupleQueryResult result = null;       
                        String preprocessingSPARQLQuery = null;

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
                        
                        tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
                                preprocessingSPARQLQuery);
                        result = tupleQuery.evaluate();

                        if (result.hasNext() == false)
                                throw new RecommenderException("GRAPH PATTERN IS NOT MATCHING TO ANYTHING");

                        while (result.hasNext()) {
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
                            int userId = 0;
                            int itemId = 0;
                            //we need to index the found resources or retrieve the used index
                            userId = storage.createIndex(userResource);
                            itemId = storage.createIndex(itemResource);
                            IndexedRatedRes ratRes = new IndexedRatedRes(itemId, ratingResource);
                            storage.addIndexedRatedRes(userId, ratRes);
                        }                                
                } catch (RepositoryException ex) {
                        throw new RecommenderException(ex);
                } catch (MalformedQueryException ex) {
                        throw new RecommenderException(ex);
                } catch (QueryEvaluationException ex) {
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
                                while (keySetIt.hasNext()){
                                        Integer currentUserId = keySetIt.next();
                                        Double ratingSum = 0.0;
                                        Integer numberOfRatings = 0;
                                        Double userRatingAverage = 0.0;
                                        IndexedRatedRes newRes = null;
                                        Set<IndexedRatedRes> resSet = storage.getIndexedRatedResOfUser(currentUserId);
                                        Set<IndexedRatedRes> normResSet =  new HashSet<IndexedRatedRes>(resSet.size());

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
                                        storage.storeRatedResources(currentUserId, normResSet);
                                }
                        break;
                        case Z_SCORE:
                                //we need to iterate over UserRatedItemsMap
                                keySet = storage.getAllUserIndexes();
                                keySetIt = keySet.iterator();
                                while (keySetIt.hasNext()){
                                        Integer currentUserId = keySetIt.next();
                                        Double ratingSum = 0.0;
                                        Double sumOfSquares = 0.0;
                                        Integer numberOfRatings = 0;
                                        Double userRatingAverage = 0.0;                                                       
                                        Double standardDeviation = 0.0;
                                        IndexedRatedRes newRes = null;
                                        Set<IndexedRatedRes> resSet = storage.getIndexedRatedResOfUser(currentUserId);
                                        Set<IndexedRatedRes> normResSet =  new HashSet<IndexedRatedRes>(resSet.size());

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
                                        storage.storeRatedResources(currentUserId, normResSet);
                                }
                        break;
                }
        }
        
        
        @Override
        public Set<RatedResource> getRatedResources(String userURI) throws RecommenderException {
                Set<RatedResource> ratResSet = new HashSet<RatedResource>();
                int indexOfUser = storage.getIndexOf(userURI);
                
                //If this cannot be retrieved then throw an exception
                if (indexOfUser == -1){
                        throw new RecommenderException("User resource was not found");
                }
                
                RatedResource ratRes = null;
                
                Set<IndexedRatedRes> indRatRes = getStorage().getIndexedRatedResOfUser(indexOfUser);
                for (IndexedRatedRes irr: indRatRes) {
                        ratRes = new RatedResource(getStorage().getURI(irr.getResourceId()), irr.getRating());
                        ratResSet.add(ratRes);
                }
                return ratResSet;
        }
        
                
        //TODO Computing average should be also pushed to the preprocessing phase
        @Override
        public double getRatingAverageOfUser(String userURI) throws RecommenderException{
                int indexOfUser = storage.getIndexOf(userURI);
                //If this cannot be retrieved then throw an exception
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
                if (numberOfRatedRes == 0) return 0;
                return ratingSum / (double)numberOfRatedRes;
        }
        
        @Override
        public Set<String> getConsumedResources(String userURI) throws RecommenderException {
                Set<String> consumedResources = new HashSet<String>();
                
                //Retrieve the index of user
                int indexOfUser = getStorage().getIndexOf(userURI);
                //If this cannot be retrieved then throw an exception
                if (indexOfUser == -1){
                        throw new RecommenderException("User resource was not found");
                }                                 
                
                Set<IndexedRatedRes> indexedConsumedRes 
                        = getStorage().getIndexedRatedResOfUser(indexOfUser);
                
                if (indexedConsumedRes == null || indexedConsumedRes.size() < 1){
                        throw new RecommenderException("User has not explicit ratings");                        
                }  
                
                for (IndexedRatedRes irr: indexedConsumedRes) {
                        consumedResources.add(getStorage().getURI(irr.getResourceId()));
                }
                return consumedResources;
        }                                
        
        @Override
        public RecConfig getRecConfig(){
                return config;
        }
        
        @Override
        public RepositoryConnection getConnection() {
                return con;
        }
        
        //Gets the overall MIN rating in the whole dataset by means of a
        //SPARQL query from the repository.
        protected void seekMinRating(RepositoryConnection con) throws RecommenderException {                        
                try {
                        String minRatingSPARQLQuery =
                                "SELECT (MIN(xsd:double("+ config.getRecEntity(RecEntity.RATING)
                                + ")) as ?minRat )\n"
                                + " WHERE {\n"
                                + config.getRatGraphPattern()                        
                                + "\n}";

                        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
                                minRatingSPARQLQuery);
                        TupleQueryResult result = tupleQuery.evaluate();                        
                        if (result.hasNext()) {
                            BindingSet bs = result.next();
                            String minRatingString =  bs.getValue("minRat").stringValue();
                            storage.storeDatasetMinRating(Double.parseDouble(minRatingString));                   
                        }
                } catch (RepositoryException ex) {
                        throw new RecommenderException(ex);
                } catch (MalformedQueryException ex) {
                        throw new RecommenderException(ex);
                } catch (QueryEvaluationException ex) {
                        throw new RecommenderException(ex);
                }
        }
        
        //Gets the overall MAX rating in the whole dataset by means of a
        //SPARQL query from the repository.
        protected void seekMaxRating(RepositoryConnection con) throws RecommenderException {

                try {
                        String maxRatingSPARQLQuery =
                                "SELECT (MAX(xsd:double("+ config.getRecEntity(RecEntity.RATING)
                                + ")) as ?maxRat )\n"
                                + "WHERE {\n"
                                + config.getRatGraphPattern()
                                + "}";

                        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
                                maxRatingSPARQLQuery);
                        TupleQueryResult result = tupleQuery.evaluate();
                        if (result.hasNext()) {
                            BindingSet bs = result.next();
                            String maxRatingString =  bs.getValue("maxRat").stringValue();
                            storage.storeDatasetMaxRating(Double.parseDouble(maxRatingString));
                        }
                } catch (RepositoryException ex) {
                        throw new RecommenderException(ex);
                } catch (MalformedQueryException ex) {
                        throw new RecommenderException(ex);
                } catch (QueryEvaluationException ex) {
                        throw new RecommenderException(ex);
                }
        }        
}
