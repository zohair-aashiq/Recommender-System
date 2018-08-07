/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.util;

import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;

/**
 * Class responsible for calculating similarities between vectors based on cosine
 * distance, i.e. the similarity is based on the formula:
 * sim(v1, v2) = cos angle(v1, v2)
 */
public class VectorOperations {
    
        /*---------*
	 * Methods *
	 *---------*/
                
        /**
         * Method returns a similarity score in a range [0, 1].
         * The two arrays contains RatedResources (x,y), where x is the ID of
         * an item and y is an assigned rating.
         * Asymptotically optimal exponential-binary-search algorithm
         * ASSUMPTIONS
         * - An array does not contain an item rated multiple times.
         * - Arrays are ordered based on the IDs of rated items.
         * - Arrays have been divided by the l2-norm and therefore normalized
         * @param list1
         * @param list2
         * @return 
         */
        public static Double computeSimilarityOfNormalizedSortedVectors(IndexedRatedRes[] list1, IndexedRatedRes[] list2) {
                IndexedRatedRes[] smallerList;
                IndexedRatedRes[] largerList;
                int exponent = 0;
                int base = 2;
                Double similarityScore = 0.0;
                int indexOfSmallerList = 0;
                
                if (list1.length < list2.length) {
                        smallerList = list1;
                        largerList = list2;
                } else {
                        smallerList = list2;
                        largerList = list1;
                }
                                
                int currentMinIndexInLargerList = 0;
                int currentMaxIndexInLargerList = 0;

                while (indexOfSmallerList < smallerList.length && 
                                currentMinIndexInLargerList < largerList.length) {
                        
                        exponent = 0;
                        currentMaxIndexInLargerList = currentMinIndexInLargerList;
                        Integer valueToSearch = smallerList[indexOfSmallerList].getResourceId();
                        
                        //Find the new upper bound
                        while (currentMaxIndexInLargerList < largerList.length && 
                                        valueToSearch > largerList[currentMaxIndexInLargerList].getResourceId()) {
                                
                                currentMinIndexInLargerList = currentMaxIndexInLargerList;
                                currentMaxIndexInLargerList = currentMaxIndexInLargerList 
                                        + (int) Math.pow(base, exponent);
                                exponent++;
                        }
                        
                        if (currentMaxIndexInLargerList >= largerList.length) {
                                currentMaxIndexInLargerList = largerList.length -1;
                                if (valueToSearch > largerList[currentMaxIndexInLargerList].getResourceId()) {
                                        //No chance to find anything else
                                        break;
                                }
                        }
                             
                        if (valueToSearch.equals(largerList[currentMaxIndexInLargerList].getResourceId())) {
                                similarityScore = similarityScore 
                                        + (smallerList[indexOfSmallerList].getRating()
                                            * largerList[currentMaxIndexInLargerList].getRating());
                                currentMinIndexInLargerList = currentMaxIndexInLargerList + 1;
                        } else {
                                int resultPosition = binarySearch(largerList, valueToSearch,
                                        currentMinIndexInLargerList, currentMaxIndexInLargerList);
                                if (resultPosition != -1) {
                                        currentMinIndexInLargerList = resultPosition + 1;
                                        similarityScore = similarityScore 
                                                + (smallerList[indexOfSmallerList].getRating()
                                                * largerList[resultPosition].getRating());                            
                                }
                        }
                        indexOfSmallerList++;
                }
                
                /*
                while (indexOfSmallerList < smallerList.length || indexOfLargerList < largerList.length) {
                        while (indexOfSmallerList < smallerList.length -1 && 
                                        smallerList[indexOfSmallerList].getResourceId()< largerList[indexOfLargerList].getResourceId()) {
                                indexOfSmallerList++;                                
                        }
                        if (smallerList[indexOfSmallerList].getResourceId().equals(largerList[indexOfLargerList].getResourceId())) {
                                similarityScore = similarityScore + 
                                        (smallerList[indexOfSmallerList].getRating() * largerList[indexOfLargerList].getRating());
                                indexOfSmallerList++;
                                indexOfLargerList++;
                                continue;
                        }
                        while (indexOfLargerList < largerList.length -1 && 
                                        largerList[indexOfLargerList].getResourceId()< smallerList[indexOfSmallerList].getResourceId()) {
                                indexOfLargerList++;
                        }
                        if (smallerList[indexOfSmallerList].getResourceId().equals(largerList[indexOfLargerList].getResourceId())) {
                                similarityScore = similarityScore + 
                                        (smallerList[indexOfSmallerList].getRating() * largerList[indexOfLargerList].getRating());
                                indexOfSmallerList++;
                                indexOfLargerList++;
                                continue;
                        }                    
                }
                */
                return similarityScore;
        }        
        
        /**
        * Implements binary search on an ordered array of integers. As a second
        * argument, we have the value we want to look for. The third argument is the
        * initial point to start the binary search.
        * 
        * It returns the index in which the value was found. If the value wasn't
        * found there -1 is returned.
        * 
        */
        public static int binarySearch(IndexedRatedRes[] list, int value, int startPoint, int endPoint) {
                int result = -1;
                if (startPoint < endPoint) {
                        int middlePoint = (int) Math.ceil((endPoint + startPoint) / 2);
                        if (middlePoint == startPoint) {
                                if (list[startPoint].getResourceId() == value) {
                                        return startPoint;
                                }
                                return result;
                        }
                        if (list[middlePoint].getResourceId() == value) {
                                while (middlePoint > 0 
                                                && list[middlePoint - 1].getResourceId() == (list[middlePoint].getResourceId())) {
                                        middlePoint--;
                                }
                                return middlePoint;
                        } else if (list[middlePoint].getResourceId() > value) {
                                return binarySearch(list, value, startPoint, middlePoint);
                        } else {
                                return binarySearch(list, value, middlePoint, endPoint);
                        }
                }
                return result;
        }
        
        /*
        //A Multiple entries vector is a vector that contains more entries for the same rated item.
        //For example the vector [(1,1), (1,3), (1,1), (1,2)]. Means that the item with ID 1 was consumed 
        //in four different times and the user assigned four different ratings.
        public static Double calculateMultipleEntriesVectorSimilarityWithRatings(Object[] smallerList, Object[] largerList) {
                // Here we identify the smaller and the larger list.
                double dotProduct = 0.0;
                double norm = 0.0;
                double norm2Exp2smallerList = 0.0;
                double norm2Exp2LargerList = 0.0;
                int smallerListIndex = 0;
                int largerListIndex = 0;
                double sumOfRatingSmallerList = 0.0;
                double sumOfRatingLargerList = 0.0;
                
                while (smallerListIndex < smallerList.length 
                        && largerListIndex < largerList.length) { 
                    
                        IndexedRatedRes smallerListRR = (IndexedRatedRes)smallerList[smallerListIndex];
                        IndexedRatedRes largerListRR = (IndexedRatedRes)largerList[largerListIndex];

                        if (smallerListRR.equals(largerListRR)) {
                                if (sumOfRatingSmallerList == 0.0) sumOfRatingSmallerList 
                                        = smallerListRR.getRating();
                                if (sumOfRatingLargerList == 0.0) sumOfRatingLargerList 
                                        = largerListRR.getRating();
                            
                                while (smallerListIndex < smallerList.length - 1
                                        && smallerList[smallerListIndex].equals(
                                                smallerList[smallerListIndex + 1])) {
                                        sumOfRatingSmallerList = sumOfRatingSmallerList 
                                                + ((IndexedRatedRes)smallerList[smallerListIndex + 1]).getRating();
                                        smallerListIndex++;
                                }
                                while (largerListIndex < largerList.length - 1 
                                        && largerList[largerListIndex].equals(
                                                largerList[largerListIndex + 1])) {
                                        sumOfRatingLargerList = sumOfRatingLargerList 
                                                + ((IndexedRatedRes)largerList[largerListIndex + 1]).getRating();
                                        largerListIndex++;
                                }
                                
                                dotProduct = dotProduct + (sumOfRatingSmallerList * sumOfRatingLargerList);
                                norm2Exp2smallerList = norm2Exp2smallerList 
                                        + (sumOfRatingSmallerList * sumOfRatingSmallerList);
                                norm2Exp2LargerList = norm2Exp2LargerList
                                        + (sumOfRatingLargerList * sumOfRatingLargerList);
                                smallerListIndex++;
                                largerListIndex++;
                                sumOfRatingSmallerList = 0;
                                sumOfRatingLargerList = 0;
                        } else if ( smallerListRR.getResourceId() < largerListRR.getResourceId() ) {                 
                                if (sumOfRatingSmallerList == 0.0) sumOfRatingSmallerList 
                                        = smallerListRR.getRating();
                                if (sumOfRatingLargerList == 0.0) sumOfRatingLargerList 
                                        = largerListRR.getRating();
                                                        
                                while (smallerListIndex < smallerList.length - 1 
                                        && smallerList[smallerListIndex].equals(                                                
                                                smallerList[smallerListIndex + 1])) {                                                                                    
                                        sumOfRatingSmallerList = sumOfRatingSmallerList 
                                                + ((IndexedRatedRes)smallerList[smallerListIndex + 1]).getRating();
                                        smallerListIndex++;
                                }                                
                                norm2Exp2smallerList = norm2Exp2smallerList 
                                        + (sumOfRatingSmallerList * sumOfRatingSmallerList);                               
                                smallerListIndex++;
                                sumOfRatingSmallerList = 0;

                        } else if ( smallerListRR.getResourceId() > largerListRR.getResourceId() ) {                            
                                if (sumOfRatingSmallerList == 0.0) sumOfRatingSmallerList 
                                        = smallerListRR.getRating();
                                if (sumOfRatingLargerList == 0.0) sumOfRatingLargerList 
                                        = largerListRR.getRating();
                            
                                while (largerListIndex < largerList.length - 1
                                        && largerList[largerListIndex].equals(
                                                largerList[largerListIndex + 1])) {
                                        sumOfRatingLargerList = sumOfRatingLargerList 
                                                + ((IndexedRatedRes)largerList[largerListIndex + 1]).getRating();
                                        largerListIndex++;                       
                                }
                                norm2Exp2LargerList = norm2Exp2LargerList 
                                        + (sumOfRatingLargerList * sumOfRatingLargerList);                                                                                              
                                largerListIndex++;
                                sumOfRatingLargerList = 0;
                        }
                }
                //For the rest of elements remained without comparison.
                //It is either the first case or the second which made us escape from
                //the while loop above.
                while (smallerListIndex < smallerList.length ) {
                        IndexedRatedRes rr = (IndexedRatedRes) smallerList[smallerListIndex];
                        norm2Exp2smallerList = norm2Exp2smallerList 
                                + (rr.getRating() * rr.getRating());                                                            
                        smallerListIndex++;
                }
                while (largerListIndex < largerList.length ) {                    
                        IndexedRatedRes rr = (IndexedRatedRes) largerList[largerListIndex];
                        norm2Exp2LargerList = norm2Exp2LargerList 
                                + (rr.getRating() * rr.getRating());                                                             
                        largerListIndex++;
                }
                norm = Math.sqrt(norm2Exp2smallerList) *  Math.sqrt(norm2Exp2LargerList);
                //Since norm can be 0 because of the ratings:
                if (norm == 0.0) return 0.0;
                return (dotProduct / norm);
        }
        
        //A Multiple entries vector is a vector that contains more entries for the same rated item.
        //For example the vector [(1,1), (1,3), (1,1), (1,2)]. Means that the item with ID 1 was consumed 
        //in four different times and the user assigned four different ratings.
        public static Double calculateMultipleEntriesVectorSimilarityWithoutRatings(Object[] smallerList, Object[] largerList) {
                // Here we identify the smaller and the larger list.
                double dotProduct = 0.0;
                double norm = 0.0;
                double norm2Exp2smallerList = 0.0;
                double norm2Exp2LargerList = 0.0;
                int smallerListIndex = 0;
                int largerListIndex = 0;
                
                while (smallerListIndex < smallerList.length 
                        && largerListIndex < largerList.length) {
                        int occurrenceCounterSmallerList = 1;
                        int occurrenceCounterLargerList = 1;

                        if (smallerList[smallerListIndex].equals(largerList[largerListIndex])) {
                                while (smallerListIndex < smallerList.length - 1
                                        && smallerList[smallerListIndex].equals(
                                                smallerList[smallerListIndex + 1])) {
                                        occurrenceCounterSmallerList++;
                                        smallerListIndex++;
                                }
                                while (largerListIndex < largerList.length - 1 
                                        && largerList[largerListIndex].equals(
                                                largerList[largerListIndex + 1])) {
                                        occurrenceCounterLargerList++;
                                        largerListIndex++;
                                }
                                dotProduct = dotProduct + (occurrenceCounterSmallerList * occurrenceCounterLargerList);                                
                                norm2Exp2smallerList = norm2Exp2smallerList 
                                        + (occurrenceCounterSmallerList * occurrenceCounterSmallerList);
                                norm2Exp2LargerList = norm2Exp2LargerList
                                        + (occurrenceCounterLargerList * occurrenceCounterLargerList);
                                
                                smallerListIndex++;
                                largerListIndex++;
                        } else if ((Integer)smallerList[smallerListIndex] < (Integer)largerList[largerListIndex]) {
                                while (smallerListIndex < smallerList.length - 1 
                                        && smallerList[smallerListIndex].equals(
                                                smallerList[smallerListIndex + 1])) {
                                        occurrenceCounterSmallerList++;
                                        smallerListIndex++;
                                }
                                norm2Exp2smallerList = norm2Exp2smallerList 
                                    + (occurrenceCounterSmallerList * occurrenceCounterSmallerList);
                                smallerListIndex++;
                        } else if ((Integer)smallerList[smallerListIndex] > (Integer)largerList[largerListIndex]) {                                
                                while (largerListIndex < largerList.length - 1 
                                        && largerList[largerListIndex].equals(
                                                largerList[largerListIndex + 1])) {
                                        occurrenceCounterLargerList++;
                                        largerListIndex++;
                                }
                                norm2Exp2LargerList = norm2Exp2LargerList
                                        + (occurrenceCounterLargerList * occurrenceCounterLargerList);                                                                                            
                                largerListIndex++;
                        }

                }
                //For the rest of elements remained without comparison.
                while (smallerListIndex < smallerList.length ) {
                        norm2Exp2smallerList = norm2Exp2smallerList + 1;         
                        smallerListIndex++;
                }
                while (largerListIndex < largerList.length ) {                    
                        norm2Exp2LargerList = norm2Exp2LargerList + 1;
                        largerListIndex++;
                }
                norm = Math.sqrt(norm2Exp2smallerList) *  Math.sqrt(norm2Exp2LargerList);
                return (dotProduct / norm);
        }
        
        */
}
