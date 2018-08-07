/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.storage.IndexBasedStorage;

/**
 * The abstract class implements the contract of IndexBasedStorage.
 */
public abstract class AbstractIndexBasedStorage implements IndexBasedStorage {
    
        /*--------*
	 * Fields *
	 *--------*/
    
        //Counts the number of resources.
        private int resourceCounter = 0;
        private double minRating  = -1.0;
        private double maxRating  = 1.0;
                        
        /*-----------------*
	 * Data Structures *
	 *-----------------*/    
        
        //Array that stores all resource URIs.
        private String[] resourceList = new String[10000];
        //KEY is a URI (resource) and value is the index of the array in which 
        //the resource is stored.
        private Map<String, Integer> resourceIdMap = new HashMap<String, Integer>(10000);      
        //KEY is the ID of a user. Value is a list of rated resources.
        private  Map<Integer, Set<IndexedRatedRes>> userRatedItemsMap
                = new HashMap<Integer, Set<IndexedRatedRes>>(10000);
        //Each user has a set of rated resources. In order to speed up the computation 
        //of similarities, each rating is divided by the l-norm. This Maps stores them
        //in order to be able to return non normalized ratings.
        private Map<Integer, Double> usersL2Norms
                = new HashMap<Integer, Double>(10000);
        
        /*---------*
	 * Methods *
	 *---------*/       
        
        @Override
        public String getURI(int index) {
                return resourceList[index];
        }
        
        @Override
        public int createIndex(String URI) {
                int indexOfResource = -1;
                if (resourceIdMap.containsKey(URI)) {
                                indexOfResource = resourceIdMap.get(URI);
                } else {
                        indexOfResource = resourceCounter;
                        resourceIdMap.put(URI, indexOfResource);
                        if (resourceCounter >= resourceList.length) {
                                resourceList = expandArray(resourceList);
                        }                                                
                        resourceList[resourceCounter] = URI;
                        resourceCounter++;
                }
                return indexOfResource;
        }
        
        @Override
        public int getIndexOf(String URI) {
                if (resourceIdMap.containsKey(URI))
                        return resourceIdMap.get(URI);
                else return -1;
        }
        
        @Override
        public Set<Integer> getAllUserIndexes() {
                return userRatedItemsMap.keySet();
        }
                
        @Override
        public void storeRatedResources(int indexOfUser, Set<IndexedRatedRes> ratedResources) {
                userRatedItemsMap.put(indexOfUser, ratedResources);               
        }
                
        @Override
        public void addIndexedRatedRes(int indexOfUser, IndexedRatedRes ratRes) {
                Set<IndexedRatedRes> resSet = null;
                if (userRatedItemsMap.containsKey(indexOfUser)) {
                        resSet = userRatedItemsMap.get(indexOfUser);
                        resSet.add(ratRes);
                } else {
                        resSet = new HashSet<IndexedRatedRes>();
                        resSet.add(ratRes);
                        //this.storeRatedResources(indexOfUser, resSet);
                }                
                userRatedItemsMap.put(indexOfUser, resSet);
                /*
                DynUserRatingArray userRatingArray = null;
                if (resInvertedLists.containsKey(ratRes.getResourceId())) {
                        userRatingArray = resInvertedLists.get(ratRes.getResourceId());                  
                } else {
                        userRatingArray = new DynUserRatingArray();
                        resInvertedLists.put(ratRes.getResourceId(), userRatingArray);
                }
                userRatingArray.insertId(new UserRating(indexOfUser, ratRes.getRating()));
                */
        }
        
        @Override
        public Set<IndexedRatedRes> getIndexedRatedResOfUser(int indexOfUser) {                
                if (userRatedItemsMap.containsKey(indexOfUser)) {
                        return userRatedItemsMap.get(indexOfUser);
                }
                return null;
        }
        
        @Override
        public void storeL2NormOfUser(int indexOfUser, double l2Norm) {
                usersL2Norms.put(indexOfUser, l2Norm);
        }
                
        @Override
        public double getL2NormOfUser(int indexOfUser) {
                return usersL2Norms.get(indexOfUser);
        }    
        
        @Override
        public void storeDatasetMinRating(double minRating) {
                this.minRating = minRating;
        }
        
        @Override
        public void storeDatasetMaxRating(double maxRating) {
                this.maxRating = maxRating;
        }
        
        @Override
        public void resetStorage(){
                resourceCounter = 0;
                minRating  = -1.0;
                maxRating  = 1.0;                
                resourceList = new String[10000];
                resourceIdMap = new HashMap<String, Integer>(10000);
                userRatedItemsMap = new HashMap<Integer, Set<IndexedRatedRes>>(10000);
                usersL2Norms = new HashMap<Integer, Double>(10000);
        }
        
        /*
        @Override
        public void close() {
                //Nothing to close.
        }
        */
                    
        //SOME GETTERS FOR TEST PURPOSES
        @Override
        public int getResourceCounter() {
                return resourceCounter;
        }

        @Override
        public List<String> getResourceList() {
                return new ArrayList<String>(Arrays.asList(resourceList));
        }

        @Override
        public Map<String, Integer> getResourceIdMap() {
                return resourceIdMap;
        }
        
        @Override
        public Map<Integer, Set<IndexedRatedRes>> getUserRatedItemsMap() {
                return userRatedItemsMap;
        }
        
        @Override
        public Map<Integer, Double> getUsersL2NormsMap() {
                return usersL2Norms;
        }        

        @Override
        public Double getDatasetMinRating() {
                return minRating;
        }

        @Override
        public Double getDatasetMaxRating() {
                return maxRating;
        }
        
        //Copies old string array into new string array with double length
        public String[] expandArray(String[] array) {
                String[] newArray = new String[array.length * 2];
                for (int i = 0; i < array.length; i++) {
                        newArray[i] = array[i];
                }
                return newArray;
        }
        
        //Copies old int array into new int array with double length
        public Object[] expandArray(Object[] array) {
                Object[] newArray = new Object[array.length * 2];
                for (int i = 0; i < array.length; i++) {
                        newArray[i] = array[i];
                }
                return newArray;
        }
}
