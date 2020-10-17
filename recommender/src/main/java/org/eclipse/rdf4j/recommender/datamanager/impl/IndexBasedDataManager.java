/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager.impl;

import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import com.google.common.collect.MinMaxPriorityQueue;
import org.eclipse.rdf4j.recommender.util.IndexedRatedResRatingComparator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.recommender.config.CfRecConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.config.SilVsmUcfRecConfig;
import org.eclipse.rdf4j.recommender.config.VsmCfRecConfig;
import org.eclipse.rdf4j.recommender.datamanager.AbstractIndexBasedDataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.storage.index.invlist.InvListBasedStorage;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.storage.index.CfIndexBasedStorage;
import org.eclipse.rdf4j.recommender.storage.index.invlist.ScaledInvListBasedStorage;
import org.eclipse.rdf4j.recommender.storage.index.invlist.impl.CfInvListBasedStorage;
import org.eclipse.rdf4j.recommender.storage.index.invlist.impl.CfScaledInvListBasedStorage;
import org.eclipse.rdf4j.recommender.util.ListOperations;
import org.eclipse.rdf4j.repository.RepositoryException;


public final class IndexBasedDataManager extends AbstractIndexBasedDataManager{
        /*--------*
	 * Fields *
	 *--------*/
    
        /**
         * To compare Indexed rated resources only based on the 
         * "rating" without taking into account the rated item.
         */ 
        private final Comparator<IndexedRatedRes> comparatorOfIrrBasedOnRating = new IndexedRatedResRatingComparator();


        /*-------------*
	 * Constructor *
	 *-------------*/
        
        /**
         * The constructor has an important role. Depending on the configuration
         * it creates the appropriate storage.
         * @param config 
         */
        public IndexBasedDataManager(RecConfig config) {
                super(config);
                                
                switch(config.getRecParadigm()) {
                        case CONTENT_BASED:
                                //TODO
                        break;
                        case USER_COLLABORATIVE_FILTERING:
                                switch(config.getRecStorage()) {
                                        case INVERTED_LISTS:
                                                setStorage(new CfInvListBasedStorage());
                                        break;
                                        case SCALED_INVERTED_LISTS:
                                                setStorage(new CfScaledInvListBasedStorage());
                                        break;
                                }
                        break;
                }                            
        }
        
        @Override
        public void initStorage() throws RecommenderException {
                
                //Do part of the work by invoking superclass' method.
                super.initStorage(); 
                
                Long start = null;
                Long end = null;
                Long connectionTime = null;
                
                //The following step applies only to VSM.
                if (getRecConfig() instanceof VsmCfRecConfig 
                        && ((VsmCfRecConfig)getRecConfig()).getSimMetric() == RecSimMetric.COSINE) {
                    
                        //computes the l-norm for each user.
                        start = System.currentTimeMillis();
                        computeL2Norm();
                        end = System.currentTimeMillis();
                        connectionTime = end - start;                
                        System.out.println("Time to compute the l2-norms: "
                                + (connectionTime) + "ms (ca. " + (connectionTime) / 1000 
                                + " secs).");
                        System.out.println("L2-NORM COMPUTATION... COMPLETED");
                }
                                
                //normalize again based on the similarity function selected
                /*
                start = System.currentTimeMillis();
                normalizeRatingsForSimilarityComputation(con);
                end = System.currentTimeMillis();
                connectionTime = end - start;
                System.out.println("Time to normalize the ratings using the l2-norms: "
                        + (connectionTime) + "ms (ca. " + (connectionTime) / 1000 
                        + " secs).");
                */
                
                
                if (getRecConfig().getRecStorage() == RecStorage.INVERTED_LISTS ||
                        getRecConfig().getRecStorage() == RecStorage.SCALED_INVERTED_LISTS) {
                        //builds the inverted index from the stored and preprocessed data
                        //this applies to both kinds of storage, inv lists and scaled
                        //inverted lists.
                        start = System.currentTimeMillis();
                        buildInvertedLists();
                        end = System.currentTimeMillis();
                        connectionTime = end - start;
                        System.out.println("Time to build the inverted lists: "
                                + (connectionTime) + "ms (ca. " + (connectionTime) / 1000 
                                + " secs).");
                        System.out.println("INV LISTS... COMPLETED");                        

                        //builds the inverted index from the stored and preprocessed data
                        start = System.currentTimeMillis();
                        optimizeLists();
                        end = System.currentTimeMillis();
                        connectionTime = end - start;                
                        System.out.println("Time to optimize the lists and precompute TOP lists: "
                                + (connectionTime) + "ms (ca. " + (connectionTime) / 1000 
                                + " secs).");
                        System.out.println("OPTIMIZATION OF INV LISTS... COMPLETED");                    
                }                
        }                

        @Override
        public void preprocessWithRatings() {
            
                Long start;
                Long end;
                Long connectionTime;
            
                if (getRecConfig().getRecParadigm()== RecParadigm.USER_COLLABORATIVE_FILTERING) {
                        //FIXED
                        //builds user neighnborhoods
                        start = System.currentTimeMillis();
                        buildUserNeiborhood();
                        end = System.currentTimeMillis();
                        connectionTime = end - start;
                        System.out.println("Time to compute the neighborhood: "
                                + (connectionTime) + "ms (ca. " + (connectionTime) / 1000 
                                + " secs).");
                        System.out.println("COMPUTATION OF NEIGHBORHOODS... COMPLETED");                               
                }                   
        }
        
        @Override
        public void preprocessWithoutRatings() {
                Long start;
                Long end;
                Long connectionTime;
            
                if (getRecConfig().getRecParadigm()== RecParadigm.USER_COLLABORATIVE_FILTERING) {
                        //FIXED
                        //builds user neighnborhoods
                        start = System.currentTimeMillis();
                        buildUserNeiborhood();
                        end = System.currentTimeMillis();
                        connectionTime = end - start;
                        System.out.println("Time to compute the neighborhood: "
                                + (connectionTime) + "ms (ca. " + (connectionTime) / 1000 
                                + " secs).");
                        System.out.println("COMPUTATION OF NEIGHBORHOODS... COMPLETED");                               
                }
        }
        
        @Override
        public void populateStorage() throws RecommenderException {
                
                //Do part of the work by invoking superclass' method.
                super.populateStorage();
            
                String itemResource = null;
                Double ratingResource = null;
                TupleQuery tupleQuery = null;
                TupleQueryResult result = null;       
                String counter = null;
                String top5Query = null;
                
                if (getRecConfig().getRecStorage() == RecStorage.SCALED_INVERTED_LISTS) {
                        try {
                                ScaledInvListBasedStorage silStorage = (ScaledInvListBasedStorage)getStorage();
                                SilVsmUcfRecConfig silConfig =  (SilVsmUcfRecConfig)getRecConfig();
                                top5Query =
                                        "SELECT " + getRecConfig().getRecEntity(RecEntity.RAT_ITEM) + " "
                                        + getRecConfig().getRecEntity(RecEntity.RATING) + " "
                                        + "(COUNT(" + getRecConfig().getRecEntity(RecEntity.USER) + ") as ?numUsers) \n"
                                        + "WHERE { \n"
                                        + getRecConfig().getRatGraphPattern()
                                        + "} "
                                        + "GROUP BY " + getRecConfig().getRecEntity(RecEntity.RAT_ITEM) + " " 
                                        + getRecConfig().getRecEntity(RecEntity.RATING) + " "
                                        + "ORDER BY DESC(?numUsers) "
                                        + "LIMIT " + silConfig.getNumberOfTopRatings()
                                        + "";

                                //Processing Top ratings
                                tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL,
                                        top5Query);
                                result = tupleQuery.evaluate();
                                /*
                                if (result.hasNext() == false)
                                throw new RecommenderException("GRAPH PATTERN IS NOT MATCHING TO ANYTHING");
                                */

                                while (result.hasNext()) {
                                        BindingSet bs = result.next();
                                        //We can access each of the variables configured:
                                        //userResource = bs.getValue(config.getRecEntity(RecEntity.USER).replace("?", "")).stringValue();
                                        itemResource = bs.getValue(getRecConfig().getRecEntity(RecEntity.RAT_ITEM).replace("?", "")).stringValue();
                                        ratingResource = Double.parseDouble(bs.getValue(getRecConfig().getRecEntity(RecEntity.RATING).replace("?", "")).stringValue());
                                        counter = bs.getValue("numUsers").stringValue();

                                        if (itemResource != null && ratingResource != null) {
                                            silStorage.addTopRatedRes(new IndexedRatedRes(getStorage().getIndexOf(itemResource), ratingResource.doubleValue()));
                                        }
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
                                keySet = getStorage().getAllUserIndexes();
                                keySetIt = keySet.iterator();
                                
                                while (keySetIt.hasNext()){
                                        Integer currentUserId = keySetIt.next();
                                        Double ratingSquareSum = 0.0;
                                        Double l2Norm = 0.0;
                                        IndexedRatedRes newRes = null;
                                        Set<IndexedRatedRes> resSet = getStorage().getIndexedRatedResOfUser(currentUserId);

                                        for (IndexedRatedRes currentRes : resSet){
                                                ratingSquareSum = ratingSquareSum + Math.pow(currentRes.getRating(), 2);
                                        }
                                        l2Norm = Math.sqrt(ratingSquareSum);
                                        //finally we replace the list with the normalized list
                                        getStorage().storeL2NormOfUser(currentUserId, l2Norm);
                                }                                
                        break;
                        //TODO cased PEARSON CORRELATION                        //TODO cased PEARSON CORRELATION                        //TODO cased PEARSON CORRELATION                        //TODO cased PEARSON CORRELATION
                }     
        }
        
        //It normalizes the rating based on a given strategy
        /*
        protected void normalizeRatingsForSimilarityComputation(RepositoryConnection con) {
                Set<Integer> keySet = null;
                Iterator<Integer> keySetIt = null;
                
                switch(config.getSimMetric()) {
                        case COSINE:
                                //we need to iterate over UserRatedItemsMap and divide each of the ratings
                                //by the l-2 norm of the vector
                                keySet = storage.getAllUserIndexes();
                                keySetIt = keySet.iterator();
                                
                                while (keySetIt.hasNext()){
                                        Integer currentUserId = keySetIt.next();
                                        IndexedRatedRes newRes = null;
                                        Set<RatedResource> resSet = storage.getRatedItemsOfUser(currentUserId);
                                        Set<RatedResource> normResSet =  new HashSet<RatedResource>(resSet.size());
                                       
                                        for (IndexedRatedRes currentRes : resSet){
                                                //newRes = new IndexedRatedRes(currentRes.getResourceId(), 
                                                //        RoundingUtility.round((currentRes.getRating() / l2Norm), config.getDecimalPlaces())  );
                                                newRes = new IndexedRatedRes(currentRes.getResourceId(), 
                                                        (currentRes.getRating() / storage.getL2NormOfUser(currentUserId)));
                                                normResSet.add(newRes);
                                        }
                                        //finally we replace the list with the normalized list
                                        storage.storeRatedResources(currentUserId, normResSet);
                                }                                
                        break;
                        //TODO cased PEARSON CORRELATION
                }              
        }
        */
        
        /**
         * Builds inverted lists. Just for storages based on inverted lists.
         */
        protected void buildInvertedLists() {
                InvListBasedStorage invStorage = (InvListBasedStorage)getStorage();
                Set<Integer> keySet = null;
                Iterator<Integer> keySetIt = null;
                
                //we need to iterate over UserRatedItemsMap
                keySet = getStorage().getAllUserIndexes();
                keySetIt = keySet.iterator();

                //for each user.
                while (keySetIt.hasNext()){
                        Integer currentUserId = keySetIt.next();                        
                        invStorage.createInvertedListForUser(currentUserId);
                }
        }
        
        /**
         * Performs different kinds of optimizations to the lists.
         */
        protected void optimizeLists() {
                compactAndSortInvertedLists();
                if (getRecConfig().getRecStorage() == RecStorage.SCALED_INVERTED_LISTS) {
                        ScaledInvListBasedStorage silStorage = (ScaledInvListBasedStorage)getStorage();
                        silStorage.precomputeInvListsPartialDotProducts();
                }
        }
        
        /**
         * Removes unassigned cells of the arrays in the inverted lists.
         */
        protected void compactAndSortInvertedLists() {
                InvListBasedStorage ilStorage = (InvListBasedStorage)getStorage();
                ilStorage.compactAndSortInvertedLists();
        }
        
        /** 
         * K-nearest-algorithm to compute the neighborhood.
         */
        //TODO the code is not compact enough
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
                        
                if (getRecConfig().getRecStorage() == RecStorage.INVERTED_LISTS) {
                        CfInvListBasedStorage cfSilStorage = (CfInvListBasedStorage) getStorage();
                        //Classic neighborhood computed with inverted indexes
                        //I need to iterate over all users and over all rated items
                        //of each user.
                        
                        //We start getting all users
                        usersIdSet = getStorage().getAllUserIndexes();                
                        usersIdIt = usersIdSet.iterator();

                        //For each user...
                        while (usersIdIt.hasNext()) {
                                currentUserId = usersIdIt.next();
                                //I load the set of rated items.
                                userRatedResSet = getStorage().getIndexedRatedResOfUser(currentUserId);
                                invListsOfUser = new InvertedList[userRatedResSet.size()];
                                userRatings = new double[userRatedResSet.size()];
                                userRatedResIt = userRatedResSet.iterator();

                                int numberOfLists = 0;
                                while (userRatedResIt.hasNext()) {
                                        currentRatRes = userRatedResIt.next();
                                        invList = cfSilStorage.getInvertedListOfItem(currentRatRes.getResourceId());
                                        invListsOfUser[numberOfLists] = invList;
                                        userRatings[numberOfLists] = currentRatRes.getRating();
                                        numberOfLists++;
                                }

                                //long startForUser = System.currentTimeMillis();                        
                                neighborhood =  ListOperations.computeNeighborhood(
                                        currentUserId, userRatings,
                                        getStorage().getUsersL2NormsMap(),
                                        invListsOfUser,
                                        cfConfig.getNeighborhoodSize(),
                                        getRecConfig().getDecimalPlaces());
                                //long endForUser = System.currentTimeMillis();

                                neighborhoodArray = neighborhood.toArray(new IndexedRatedRes[neighborhood.size()]);
                                Arrays.sort(neighborhoodArray, comparatorOfIrrBasedOnRating);
                                cfSilStorage.storeNeighborhood(currentUserId, neighborhoodArray);                        
                        }
                }
                
                if (getRecConfig().getRecStorage() == RecStorage.SCALED_INVERTED_LISTS) {                    
                        CfScaledInvListBasedStorage cfSilStorage = (CfScaledInvListBasedStorage) getStorage();
                                                
                        //Classic neighborhood computed with inverted indexes
                        //I need to iterate over all users and over all rated items
                        //of each user.
                        int neighborhoodsize = cfConfig.getNeighborhoodSize();
                        int decimalPlaces = getRecConfig().getDecimalPlaces();

                        //We start getting all users
                        usersIdSet = getStorage().getAllUserIndexes();                
                        usersIdIt = usersIdSet.iterator();
                        
                        //For each user...
                        while (usersIdIt.hasNext()) {
                                currentUserId = usersIdIt.next();  
                                invListsUser = cfSilStorage.getInvertedListsOfUser(currentUserId);                                     

                                //Efficient method
                                //long startForUser = System.currentTimeMillis();
                                neighborhood =  ListOperations.computeSilNeighborhood(
                                        //currentUserId, userRatings, 
                                        currentUserId, 
                                        getStorage().getUsersL2NormsMap(),
                                        invListsUser,
                                        neighborhoodsize,
                                        decimalPlaces);
                                //long endForUser = System.currentTimeMillis();

                                neighborhoodArray = neighborhood.toArray(new IndexedRatedRes[neighborhoodsize]);
                                Arrays.sort(neighborhoodArray, comparatorOfIrrBasedOnRating);
                                cfSilStorage.storeNeighborhood(currentUserId, neighborhoodArray);
                        }
                }                                
        }
        
        @Override
        public Set<RatedResource> getNeighbors(String URI) throws RecommenderException {
                Set<RatedResource> neighborhood = new HashSet<RatedResource>();
                
                int indexOfResource = getStorage().getIndexOf(URI);

                if (indexOfResource == -1){
                        throw new RecommenderException("Resource was not found");
                }
                
                CfIndexBasedStorage cfStorage = (CfIndexBasedStorage)getStorage();
                IndexedRatedRes[] neighborhoodIndArray =  cfStorage.getNeighborhood(indexOfResource);
                RatedResource neighborRatRes =  null;
                
                for (IndexedRatedRes neighbor: neighborhoodIndArray) {
                        if (neighbor != null) {
                                neighborRatRes = new RatedResource(getStorage().getURI(neighbor.getResourceId()), neighbor.getRating());
                                neighborhood.add(neighborRatRes);
                        }
                }
                return neighborhood;
        }
        
        @Override
        public Set<String> getRecCandidates(String userURI) throws RecommenderException {
                Set<String> recCandidates = new HashSet<String>();
                Set<IndexedRatedRes> neighborIndRatedRes;
                
                //Retrieve the index of user
                int indexOfUser = getStorage().getIndexOf(userURI);
                //If this cannot be retrieved then throw an exception
                if (indexOfUser == -1){
                        throw new RecommenderException("User resource was not found");
                }
                
                if (getRecConfig().getRecParadigm()== RecParadigm.USER_COLLABORATIVE_FILTERING) {
                        CfInvListBasedStorage cfSilStorage = (CfInvListBasedStorage) getStorage();

                        if (hasPreprocessed()) {
                                //We have to collect the candidate set from the neighbors
                                for (IndexedRatedRes neighbor: cfSilStorage.getNeighborhood(indexOfUser)) {
                                        neighborIndRatedRes = cfSilStorage.getIndexedRatedResOfUser(neighbor.getResourceId());

                                        for (IndexedRatedRes candidate: neighborIndRatedRes) {
                                                recCandidates.add(cfSilStorage.getURI(candidate.getResourceId()));
                                        }
                                } 
                        } else {
                                //TODO
                        }
                }
                return recCandidates;
        }                                     

        @Override
        public double getResRelativeImportance(String userURI, String itemURI) 
                        throws RecommenderException {
                throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        public void releaseResources() {}
}
