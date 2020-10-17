/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;

/**
 * Interface of an index-based storage for a recommender. Users and items,
 * both represented by RDF resources are indexed.
 */
public interface IndexBasedStorage extends Storage {
        
        /**
         * Given a certain index it returns the URI.
         * If the URI has not been indexed null is returned.
         * @param index
         * @return 
         */
        public String getURI(int index);
        
        /**
         * Creates an index for the given resource and stores it.
         * If the resource is new it returns the created index.
         * If the resource has been already indexed, then the method doesn't do
         * anything and returns the index which has been already stored.
         * @param URI 
         * @return  
         */
        public int createIndex(String URI);
    
        /**
         * If a given resource has been indexed, then method returns its index.
         * Otherwise it returns -1.
         * @param URI
         * @return 
         */
        public int getIndexOf(String URI);
        
        /**
         * Returns the indexes of all users stored in the system.
         * @return 
         */
        public Set<Integer> getAllUserIndexes();
        
        /**
         * It stores a set of rated resources for a given user.
         * @param indexOfUser
         * @param resSet
         */
        public void storeRatedResources(int indexOfUser, Set<IndexedRatedRes> resSet);
                
        /**
         * This method assumes that both indexes already exits in the system.
         * It adds a new rated resource to the set of rated resources that a
         * user.
         * @param indexOfUser
         * @param ratRes
         */
        public void addIndexedRatedRes(int indexOfUser, IndexedRatedRes ratRes);
        
        /**
         * Returns a set of rated resources for a given user.
         * @param indexOfUser the index of a user
         * @return 
         */
        public Set<IndexedRatedRes> getIndexedRatedResOfUser(int indexOfUser);     
        
        /**
         * It stores the L2-Norm of all the rated resources of the user.
         * @param indexOfUser 
         * @param l2Norm 
         */
        public void storeL2NormOfUser(int indexOfUser, double l2Norm);
        
        /**
         * Returns the l2-norm of the items consumed by a given user.
         * @param indexOfUser
         * @return 
         */
        public double getL2NormOfUser(int indexOfUser);
        
        /**
         * Stores the minimum rating that can be found in the data set.
         * @param rating 
         */
        public void storeDatasetMinRating(double rating);
        
        /**
         * Stores the maximum rating that can be found in the data set.
         * @param rating 
         */
        public void storeDatasetMaxRating(double rating);            
        
        /**
         * For those implementations that require closing resources, e.g.
         * db.close() in MapDB.
         */
        //public void close();
        
        //FOR DEBUGGING PURPOSES
        public int getResourceCounter();        
        public List<String> getResourceList();
        public Map<String, Integer> getResourceIdMap();
        public Map<Integer, Set<IndexedRatedRes>> getUserRatedItemsMap();
        public Map<Integer, Double> getUsersL2NormsMap();
        public Double getDatasetMinRating();
        public Double getDatasetMaxRating();
}
