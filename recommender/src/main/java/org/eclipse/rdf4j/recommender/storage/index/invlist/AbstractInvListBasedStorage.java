/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.storage.index.AbstractIndexBasedStorage;

/**
 * The abstract class implements the interface of InvListBasedStorage.
 */
public abstract class AbstractInvListBasedStorage extends AbstractIndexBasedStorage implements InvListBasedStorage{        
        /*-----------------*
	 * Data Structures *
	 *-----------------*/          
    
        //Implementation of inverted lists.
        private  Map<Integer, InvertedList> resInvertedLists 
                = new HashMap<Integer, InvertedList>(10000);      
        
        /*---------*
	 * Methods *
	 *---------*/
                        
        @Override
        public InvertedList getInvertedListOfItem(int indexOfItem) {
                return resInvertedLists.get(indexOfItem);
        }
                        
        @Override
        public InvertedList[] getInvertedListsOfUser(int userId) {
                throw new UnsupportedOperationException();
        }
        
        @Override
        public void createInvertedListForUser(int indexOfUser) {
                Set<IndexedRatedRes> ratedResources = getIndexedRatedResOfUser(indexOfUser);
                InvertedList userRatingArray = null;
                //Building inverted list
                for (IndexedRatedRes ratRes: ratedResources) {         
                        if (resInvertedLists.containsKey(ratRes.getResourceId())) {
                                userRatingArray = resInvertedLists.get(ratRes.getResourceId());     
                        } else {
                                userRatingArray = new InvertedList();
                                resInvertedLists.put(ratRes.getResourceId(), userRatingArray);
                        }
                        userRatingArray.insert(new IndexedUserRating(indexOfUser, ratRes.getRating()));
                }
        }
        
        @Override
        public void compactAndSortInvertedLists() {
                for (InvertedList ur: resInvertedLists.values()) {
                        ur.compactAndSortArray();
                }
        }
        
        @Override
        public void resetStorage() {
                super.resetStorage();
                resInvertedLists = new HashMap<Integer, InvertedList>(10000);
        }
        
        //SOME GETTERS FOR TEST PURPOSES        
        @Override
        public Map getResInvertedLists() {
                return resInvertedLists;
        }        
}
