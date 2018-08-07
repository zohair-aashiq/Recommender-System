/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.util;

import com.google.common.collect.MinMaxPriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;

/**
 * Class responsible for performing list-based operations, like union of intersection
 * of lists.
 */
public class ListOperations {
    
        /*---------*
	 * Static  *
	 *---------*/
    
        private static final Comparator<IndexedRatedRes> ratedResRatingComparator = new IndexedRatedResRatingComparator();
        //private static final Comparator<UserRating> urComparator = new UserRatingComparator();
        
        /*---------*
	 * Methods *
	 *---------*/
        
        /**
         * A computation of the neighborhood using inverted lists.
         * The method has to scan all the elements of the lists and requires
         * the ratings of the users.
         * @param mainUserId
         * @param userRatings
         * @param usersL2Norms
         * @param invList
         * @param neighborhoodSize
         * @param decimalPlaces
         * @return 
         */
        public static MinMaxPriorityQueue<IndexedRatedRes> computeNeighborhood(
                        int mainUserId, double[] userRatings,
                        Map<Integer, Double> usersL2Norms,
                        InvertedList[] invList,
                        int neighborhoodSize, int decimalPlaces) {
            
                MinMaxPriorityQueue<IndexedRatedRes> neighborhood = 
                        MinMaxPriorityQueue.orderedBy(ratedResRatingComparator)
                            .maximumSize(neighborhoodSize)
                            .create();
                               
                //Wen need ot keep an array of positions.
                int[] arrayOfPos = new int[invList.length]; //Automatically filled with 0s.
                int currentUser = Integer.MAX_VALUE;
                int nextValue = Integer.MAX_VALUE;
                double dotProduct = 0;
                int numberOfCompletedLists = 0;
                int indexOfCurrentList = 0;
                double mainUserL2Norm = usersL2Norms.get(mainUserId);
                double currentUserL2Norm;
                double denominator;
                
                //Initialization
                //Get the user with min ID by searching the first cell of each array.
                for (InvertedList dur: invList) {
                        if (dur.get(0).getUserId() < currentUser) {
                                currentUser = dur.get(0).getUserId();
                        }
                }                                                
                while (numberOfCompletedLists < invList.length) {
                        numberOfCompletedLists = 0;
                        indexOfCurrentList = 0;
                        dotProduct = 0;
                        IndexedRatedRes neighbor = null;
                        
                        //for each list
                        for (InvertedList dur: invList) {
                                if (arrayOfPos[indexOfCurrentList] < dur.size()) {
                                        if (dur.get(arrayOfPos[indexOfCurrentList]).getUserId() == currentUser) {
                                                dotProduct = dotProduct + 
                                                        (dur.get(arrayOfPos[indexOfCurrentList]).getRating() * userRatings[indexOfCurrentList]);
                                                arrayOfPos[indexOfCurrentList] = arrayOfPos[indexOfCurrentList] + 1;
                                        }
                                        if (arrayOfPos[indexOfCurrentList] < dur.size() 
                                                        && dur.get(arrayOfPos[indexOfCurrentList]).getUserId() < nextValue) {
                                                nextValue = dur.get(arrayOfPos[indexOfCurrentList]).getUserId();
                                        }                                                                                
                                } else numberOfCompletedLists++;
                                
                                indexOfCurrentList++;                                
                        }
                        //we need to make sure that the user itslef is not added to the neighborhood.
                        if (numberOfCompletedLists < invList.length && mainUserId != currentUser ) {
                                //dotProduct = RoundingUtility.round( dotProduct, decimalPlaces ); 
                                currentUserL2Norm = usersL2Norms.get(currentUser);
                                denominator =  mainUserL2Norm * currentUserL2Norm;
                                neighbor = new IndexedRatedRes(currentUser, dotProduct / denominator);     
                                neighborhood.add(neighbor);
                        }
                        currentUser = nextValue;
                        nextValue = Integer.MAX_VALUE;
                }                                                                                
                return neighborhood;
        }
        
        /**
         * A computation of the neighborhood using scaled inverted lists.
         * This method doesn't require the ratings of the user because the lists
         * are already scaled by the rating.
         * @param mainUserId
         * @param usersL2Norms
         * @param invList
         * @param neighborhoodSize
         * @param decimalPlaces
         * @return 
         */
        public static MinMaxPriorityQueue<IndexedRatedRes> computeSilNeighborhood(
                        //int mainUserId, double[] userRatings,
                        //int mainUserId, double[] userRatings,
                        int mainUserId,
                        Map<Integer, Double> usersL2Norms,
                        InvertedList[] scaledInvList,
                        int neighborhoodSize, int decimalPlaces) {
            
                MinMaxPriorityQueue<IndexedRatedRes> neighborhood = 
                        MinMaxPriorityQueue.orderedBy(ratedResRatingComparator)
                            .maximumSize(neighborhoodSize)
                            .create();
                               
                //Wen need ot keep an array of positions.
                int[] arrayOfPos = new int[scaledInvList.length]; //Automatically filled with 0s.
                int currentUser = Integer.MAX_VALUE;
                int nextValue = Integer.MAX_VALUE;
                double dotProduct = 0;
                int numberOfCompletedLists = 0;
                int indexOfCurrentList = 0;
                double mainUserL2Norm = usersL2Norms.get(mainUserId);
                double currentUserL2Norm;
                double denominator;
                
                //Initialization
                //Get the user with min ID by searching the first cell of each array.
                for (InvertedList dur: scaledInvList) {
                        if (dur.get(0).getUserId() == 0) {
                                //This is already the minimum Id
                                currentUser = 0;
                                break;
                        }
                        if (dur.get(0).getUserId() < currentUser) {
                                currentUser = dur.get(0).getUserId();
                        }
                }                                                
                while (numberOfCompletedLists < scaledInvList.length) {
                                        
                        numberOfCompletedLists = 0;
                        indexOfCurrentList = 0;
                        dotProduct = 0;
                        IndexedRatedRes neighbor = null;
                        
                        //for each list
                        for (InvertedList dur: scaledInvList) {
                                if (arrayOfPos[indexOfCurrentList] < dur.size()) {
                                        if (dur.get(arrayOfPos[indexOfCurrentList]).getUserId() == currentUser) {
                                                dotProduct = dotProduct + 
                                                        (dur.get(arrayOfPos[indexOfCurrentList]).getRating());
                                                arrayOfPos[indexOfCurrentList] = arrayOfPos[indexOfCurrentList] + 1;
                                        }
                                        if (arrayOfPos[indexOfCurrentList] < dur.size() 
                                                        && dur.get(arrayOfPos[indexOfCurrentList]).getUserId() < nextValue) {
                                                nextValue = dur.get(arrayOfPos[indexOfCurrentList]).getUserId();
                                        }                                                                                
                                } else numberOfCompletedLists++;
                                
                                indexOfCurrentList++;                                
                        }
                        //we need to make sure that the user itslef is not added to the neighborhood.
                        if (numberOfCompletedLists < scaledInvList.length && mainUserId != currentUser ) {
                                currentUserL2Norm = usersL2Norms.get(currentUser);
                                denominator =  mainUserL2Norm * currentUserL2Norm;
                                //dotProduct = RoundingUtility.round( dotProduct, decimalPlaces );                            
                                neighbor = new IndexedRatedRes(currentUser, dotProduct / denominator);
                                neighborhood.add(neighbor);
                        }
                        currentUser = nextValue;
                        nextValue = Integer.MAX_VALUE;
                }                                                                                
                return neighborhood;
        }
        
        /**
         * The method merges a set of lists and returns a single list.
         * @param invList
         * @return 
         */
        public static InvertedList mergeLists(
                        InvertedList[] invList) {
            
                /*        
                MinMaxPriorityQueue<UserRating> dotProducts = 
                        MinMaxPriorityQueue.orderedBy(urComparator)
                            //.maximumSize(neighborhoodSize)
                            .create();
                */                               
                List<IndexedUserRating> dotProducts = new ArrayList<IndexedUserRating>();
                
            
                //Wen need ot keep an array of positions.
                int[] arrayOfPos = new int[invList.length]; //Automatically filled with 0s.
                int currentUser = Integer.MAX_VALUE;
                int nextValue = Integer.MAX_VALUE;
                double dotProduct = 0;
                int numberOfCompletedLists = 0;
                int indexOfCurrentList = 0;
                
                //Initialization
                //Get the user with min ID by searching the first cell of each array.
                for (InvertedList dur: invList) {
                        if (dur.get(0).getUserId() < currentUser) {
                                currentUser = dur.get(0).getUserId();
                        }
                }                                                
                while (numberOfCompletedLists < invList.length) {
                        numberOfCompletedLists = 0;
                        indexOfCurrentList = 0;
                        dotProduct = 0;
                        IndexedUserRating neighbor = null;
                        
                        //for each list
                        for (InvertedList dur: invList) {
                                if (arrayOfPos[indexOfCurrentList] < dur.size()) {
                                        if (dur.get(arrayOfPos[indexOfCurrentList]).getUserId() == currentUser) {
                                                dotProduct = dotProduct + 
                                                        (dur.get(arrayOfPos[indexOfCurrentList]).getRating());
                                                arrayOfPos[indexOfCurrentList] = arrayOfPos[indexOfCurrentList] + 1;
                                        }
                                        if (arrayOfPos[indexOfCurrentList] < dur.size() 
                                                        && dur.get(arrayOfPos[indexOfCurrentList]).getUserId() < nextValue) {
                                                nextValue = dur.get(arrayOfPos[indexOfCurrentList]).getUserId();
                                        }                                                                                
                                } else numberOfCompletedLists++;
                                
                                indexOfCurrentList++;                                
                        }
                        //we need to make sure that the user itslef is not added to the neighborhood.
                        //if (numberOfCompletedLists < invList.length && mainUserId != currentUser ) {
                        if (numberOfCompletedLists < invList.length ) {
                                //dotProduct = RoundingUtility.round( dotProduct, decimalPlaces );                            
                                neighbor = new IndexedUserRating(currentUser, dotProduct); 
                                dotProducts.add(neighbor);
                        }
                        currentUser = nextValue;
                        nextValue = Integer.MAX_VALUE;
                }
                return new InvertedList(
                                (IndexedUserRating[]) dotProducts.toArray(new IndexedUserRating[dotProducts.size()]));
        }
       
        
        /**
         * Implements binary search on an ordered array of integers. As a second
         * argument, we have the value we want to look for. The third argument is the
         * initial point to start the binary search.
         * 
         * It returns the index in which the value was found. If the value wasn't
         * found there -1 is returned.
         * 
         * @param list
         * @param value
         * @param startPoint
         * @param endPoint
         * @return 
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
}
