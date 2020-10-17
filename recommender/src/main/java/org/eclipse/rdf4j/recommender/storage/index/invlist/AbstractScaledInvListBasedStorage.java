/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.util.ListOperations;

/**
 * The abstract class implements the interface of ScaledInvListBasedStorage. 
 */
public abstract class AbstractScaledInvListBasedStorage extends AbstractInvListBasedStorage implements ScaledInvListBasedStorage{        
        /*--------*
	 * Fields *
	 *--------*/
    
        //Counts the number of resources.
        private int removedLists = 0;
        private int topListCounter = 0;
                
        /*--------------*
	 * Final fields *
	 *--------------*/
        
        private final String topStringURL = "http://recsesame.org#Top";
        
        
        /*-----------------*
	 * Data Structures *
	 *-----------------*/        

        //Implementation of inverted lists.
        /*private  Map<PairRatingItem, InvertedList> twoRatInvLists 
                = new HashMap<PairRatingItem, InvertedList>(10000);*/
        //New Structure
        private  Map<IndexedRatedRes, InvertedList> oneRatInvLists
                = new HashMap<IndexedRatedRes, InvertedList>(10000);
        //Stores the top rated resources in the whole dataset (for optimization
        //purposes)        
        private Set<IndexedRatedRes> topRatedItems = new HashSet(100);
        //Stores for subset of top rated resources a key for accessing a precomputed list.
        private Map<Set<IndexedRatedRes>, IndexedRatedRes> topResKeyResMap 
                = new HashMap<Set<IndexedRatedRes>, IndexedRatedRes>(100);
                
        
        /*---------*
	 * Methods *
	 *---------*/
                
        @Override
        public InvertedList getInvertedListOfItem(int indexOfItem) {
                throw new UnsupportedOperationException();
        }
        
        @Override
        public InvertedList[] getInvertedListsOfUser(int userId) {
                Set<IndexedRatedRes> ratResSet = getIndexedRatedResOfUser(userId);
                InvertedList[] durArray = new InvertedList[ratResSet.size()];
                InvertedList  singleList =  null;                        
                        
                int index = 0;
                
                for (IndexedRatedRes ratRes: ratResSet) {
                        singleList = oneRatInvLists.get(ratRes);
                        if (singleList != null) {
                                durArray[index] = oneRatInvLists.get(ratRes);
                                index++;
                        }
                }                                                
                return durArray;
        }
        
        @Override
        public void createInvertedListForUser(int indexOfUser) {
                Set<IndexedRatedRes> ratedResources = getIndexedRatedResOfUser(indexOfUser);
                InvertedList userRatingArray = null;               
                //Building inverted list
                for (IndexedRatedRes ratRes: ratedResources) {         
                        double rating1 = ratRes.getRating();
                        double rating2 = 1;
                        IndexedRatedRes riIndex = null;
                        
                        while (rating2 <= 5)  {
                                riIndex = new IndexedRatedRes(ratRes.getResourceId(), rating2);
                                
                                if (oneRatInvLists.containsKey(riIndex)) {
                                        userRatingArray = oneRatInvLists.get(riIndex);
                                } else {
                                        userRatingArray = new InvertedList();
                                        oneRatInvLists.put(riIndex, userRatingArray);
                                }
                                //Multiplications are done directly stored in the inverted list
                                userRatingArray.insert(new IndexedUserRating(indexOfUser, rating1 * rating2));           
                                rating2++;
                        }              
                }
        }
        
        @Override
        public void compactAndSortInvertedLists() {
                /*
                for (InvertedList ur: twoRatInvLists.values()) {
                        ur.compactAndSortArray();
                }
                for (InvertedList ur: oneRatInvLists.values()) {
                        ur.compactAndSortArray();
                }
                */
                for (IndexedRatedRes rRes: oneRatInvLists.keySet()) {
                        InvertedList ur = oneRatInvLists.get(rRes);
                        ur.compactAndSortArray();
                }
        }
        
                        
        @Override
        public void addTopRatedRes(IndexedRatedRes ratedRes) {
                topRatedItems.add(ratedRes);
        }
        
        @Override
        public void precomputeInvListsPartialDotProducts() {
                int numberOfElements = 0;
                
                if (topRatedItems.size() > 0)
                        precomputeTopScaledInvListsPartialDotProducts();             
                //twoRatInvLists = new HashMap<PairRatingItem, InvertedList>(1);
                                
                for (InvertedList ur: oneRatInvLists.values()) {
                        numberOfElements = numberOfElements + ur.size();                        
                }                
                //12 bytes = 4 bytes for integer + 8 bytes for double.
        }
        
        public void precomputeTopScaledInvListsPartialDotProducts() {
                Set<IndexedRatedRes> currentUserRes = null;
                Set<IndexedRatedRes> subSetOfTopRes = null;
                
                int numberOfRemovedLists = 0;                
                //New strategy: for each user we will identify which top resources
                //the user has and replace them with a precomputed product   
                int numberOfRes = oneRatInvLists.size();
                for (Integer currentUserId: getAllUserIndexes()) {
                       
                        currentUserRes = getIndexedRatedResOfUser(currentUserId);                                                
                        numberOfRemovedLists = numberOfRemovedLists + currentUserRes.size();

                        subSetOfTopRes = new HashSet(topRatedItems.size());
                        for (IndexedRatedRes topResource: topRatedItems) {
                                if (currentUserRes.contains(topResource)) {
                                        subSetOfTopRes.add(topResource);
                                }
                        }
                                                
                        if (subSetOfTopRes.size() > 1) {
                                replaceTopLists(currentUserId, subSetOfTopRes);
                        }                        
                        numberOfRemovedLists = numberOfRemovedLists - getIndexedRatedResOfUser(currentUserId).size();                        
                }
        }
        
        public void replaceTopLists(int userId, Set<IndexedRatedRes> topLists){
                Set<IndexedRatedRes> userRatedRes = getIndexedRatedResOfUser(userId);
                IndexedRatedRes keyRes = null;                
                
                if (topResKeyResMap.containsKey(topLists)) {
                        keyRes = topResKeyResMap.get(topLists);
                        //Deleting old lists
                        for (IndexedRatedRes res: topLists) {
                                userRatedRes.remove(res);
                                removedLists++;
                        }
                        //Adding new list
                        userRatedRes.add(keyRes);
                        removedLists--;
                        //userRatedItemsMap.put(userId, userRatedRes);
                        storeRatedResources(userId, userRatedRes);
                } else {
                        String newResourceURI = topStringURL + topListCounter;
                        topListCounter++;
                        double squareRatingSum = 0.0;
                        int newResourceId = 0;
                        double newResourceRating = 0;
                        InvertedList dotProduct = null;
                        InvertedList[] tempInvList = new InvertedList[topLists.size()];
                        IndexedRatedRes newResource = null;
                        
                        int index = 0;
                        //We need to create a new Resource
                        for (IndexedRatedRes res: topLists) {       
                                squareRatingSum = squareRatingSum
                                        + (res.getRating() * res.getRating());
                                tempInvList[index] = oneRatInvLists.get(res);
                                index++;
                        }                                          
                        dotProduct = ListOperations.mergeLists(tempInvList);
                        dotProduct.compactAndSortArray();
                        newResourceId = createIndex(newResourceURI);
                        newResourceRating = Math.sqrt(squareRatingSum);
                        newResource = new IndexedRatedRes(newResourceId, newResourceRating);
                        oneRatInvLists.put(newResource, dotProduct);
                        topResKeyResMap.put(topLists, newResource);
                        replaceTopLists(userId, topLists);
                }
        }             
             
        @Override
        public void resetStorage(){
                super.resetStorage();
                topListCounter = 0;
                removedLists = 0;
                topRatedItems = new HashSet(100);
                topResKeyResMap = new HashMap<Set<IndexedRatedRes>, IndexedRatedRes>(100);
                oneRatInvLists = new HashMap<IndexedRatedRes, InvertedList>(10000);
        }
        
        //SOME GETTERS FOR TEST PURPOSES
        @Override
        public Map getResInvertedLists() {
                return oneRatInvLists;
        }
        
}
