/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist;

import java.util.Map;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.storage.IndexBasedStorage;
/**
 * Interface of an inverted lists-based storage for a recommender.
 */
public interface InvListBasedStorage extends IndexBasedStorage {
        
        /**
         * Returns an array of user ratings, i.e. pairs made of users' IDs and
         * ratings (the rating given to the indexed item).
         * @param indexOfItem The index of an Item
         * @return 
         */
        public InvertedList getInvertedListOfItem(int indexOfItem);     
        
        /**
         * Returns an array of the inverted lists of a given user.
         * @param userId
         * @return 
         */
        public InvertedList[] getInvertedListsOfUser(int userId);
        
        /**
         * Creates an inverted list for the user whose ID is specified as an 
         * argument.
         * @param currentUserId 
         */
        public void createInvertedListForUser(int currentUserId);
                
        /**
         * Compacts and sorts each of the inverted lists.
         */
        public void compactAndSortInvertedLists();        
        
        //FOR DEBUGGING PURPOSES
        public Map getResInvertedLists();
}
