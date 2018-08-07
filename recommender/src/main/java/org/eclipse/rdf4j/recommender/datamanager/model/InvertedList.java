/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.datamanager.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class implements an inverted list and is a wrapper of an array 
 * of UserRatings. The idea is to resize the inner array if an element 
 * is inserted and there's no space for it to store it. 
 */
public class InvertedList implements Serializable {
        /*--------*
	 * Static *
	 *--------*/  
    
        private static final long serialVersionUID = 44L;

        /*--------*
	 * Fields *
	 *--------*/
        
        private IndexedUserRating[] userRatingsArray;
        private int nextFreeIndex;

        /*--------------*
         * Constructors *
         *--------------*/

        public InvertedList() {
                this.userRatingsArray = new IndexedUserRating[100];
                nextFreeIndex = 0;
        }

        public InvertedList(IndexedUserRating[] urArray) {
                this.userRatingsArray = urArray;
                nextFreeIndex = userRatingsArray.length;
        }

        /*---------*
         * Methods *
         *---------*/   

        /**
         * Like the get(i) method of an ArrayList.
         * @param index
         * @return 
         */
        public IndexedUserRating get(int index) {
            return userRatingsArray[index];

        }

        /**
         * Returns the inner array.
         * @return 
         */
        //Gets the inner array
        public IndexedUserRating[] getInnerArray(){
                return userRatingsArray;
        }

        /**
         * Like the size() method of an ArrayList.
         * @return 
         */
        public int size() {
                return nextFreeIndex;
        }

        /**
         * Gets the next free index.
         * @return 
         */
        public int getNextFreeIndex(){
                return nextFreeIndex;
        }


        /**
         * Inserts a IndexedUserRating into the array and resizes if necessary.
         * @param ur
         */ 
        public void insert(IndexedUserRating ur) {
                if (nextFreeIndex >=  userRatingsArray.length) {
                        IndexedUserRating[] newUserRatingsArray = new IndexedUserRating[userRatingsArray.length + 50];
                        for (int i = 0; i < userRatingsArray.length; i++) {
                                newUserRatingsArray[i] = userRatingsArray[i];
                        }
                        userRatingsArray = newUserRatingsArray;
                }
                userRatingsArray[nextFreeIndex] = ur;
                nextFreeIndex++;
        }

        /**
         * This method removes unused cells from the inner array.
         */        
        public void compactArray() {
                userRatingsArray = Arrays.copyOf(userRatingsArray, nextFreeIndex);
        }

        /**
         * Sorts the inner array (operation required by several operations
         * like merging lists).
         * Ideally this method should be invoked when all elements have
         * been inserted.
         * @return 
         */            
        public InvertedList compactAndSortArray() {
                userRatingsArray = Arrays.copyOf(userRatingsArray, nextFreeIndex);
                Arrays.sort(userRatingsArray);
                return this;
        }

        @Override
        public String toString(){
                String formedString = "{ ";
                for (IndexedUserRating ur: userRatingsArray) {
                        if (ur != null) {
                                formedString = formedString + ur.toString() + ", ";
                        }
                }
                formedString = formedString.substring(0, formedString.length() - 2);
                formedString = formedString + " }";
                return formedString;
        }

        @Override
        public boolean equals(Object obj){
                InvertedList dur = (InvertedList)obj;
                if (this.size() != dur.size())
                        return false;
                for (int i = 0; i < dur.size(); i++) {
                        if (this.get(i).getUserId() != dur.get(i).getUserId() ||
                            this.get(i).getRating() != dur.get(i).getRating() )
                                    return false;
                }
                return true;
        }
}