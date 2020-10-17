/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.util;

import com.google.common.collect.MinMaxPriorityQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;

/**
 * Test class for ListOperations.
 */
public class ListOperationsTest {
        //For double comparisons
        private static final double DELTA = 1e-15;
    
        /**
         * Test of ListOperations.computeNeighborhood(...), which computes the
         * neighborhood using inverted lists.
         */
        @Test
        public void testComputeNeighborhood() {
                int decimalPlaces = 4;
                //Definitions of all objects used:
                IndexedUserRating ur1 = null;
                IndexedUserRating ur2 = null;
                IndexedUserRating ur3 = null;
                InvertedList dur1 = null;
                InvertedList dur2 = null;
                InvertedList[] durArray = null;
                IndexedRatedRes neighbor1 = null;
                MinMaxPriorityQueue<IndexedRatedRes> actualNeighborhood = null;
                IndexedRatedRes neighbor2 = null;
                IndexedRatedRes  expNeighbor = null;
                Map<Integer, Double> usersL2Norms
                        = new HashMap<Integer, Double>();
                
                //The user for which we compute the neighborhood is the user 0.
                //His ratings are the following:
                int userId = 0;
                double[] twoUserRatedRes = new double[2];
                twoUserRatedRes[0] = 3.0; //rating of item 1
                twoUserRatedRes[1] = 2.0; //rating of item 2
                //We also need to store the l2 norms
                //For simplicity all the l2norms will be set to one
                usersL2Norms.put(0,1.0);
                usersL2Norms.put(1,1.0);
                usersL2Norms.put(2,1.0);
            
                /*Test 1*/
                ur1 = new IndexedUserRating(1, 4.0);
                ur2 = new IndexedUserRating(1, 5.0);
                
                dur1 = new InvertedList();
                dur1.insert(ur1);
                dur1.compactAndSortArray();
                
                dur2 = new InvertedList();
                dur2.insert(ur2);
                dur2.compactAndSortArray();
                        
                durArray = new InvertedList[2];
                durArray[0] = dur1;
                durArray[1] = dur2;
                
                neighbor1 = new IndexedRatedRes(1, 22.0); //((3.0 * 4.0) + (2.0 + 5.0))
                
                actualNeighborhood = ListOperations.computeNeighborhood(
                        userId, twoUserRatedRes, usersL2Norms,
                        durArray, 1, decimalPlaces);
                                
                //Best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor1.getResourceId(), expNeighbor.getResourceId());
                //Assert.assertEquals(new Double(neighbor1.getRating()), new Double(expNeighbor.getRating()));
                Assert.assertEquals(neighbor1.getRating(), expNeighbor.getRating(), DELTA);
                
                /*Test 2*/
                ur1 = new IndexedUserRating(1, 4.0);
                ur2 = new IndexedUserRating(2, 5.0);
                
                dur1 = new InvertedList();
                dur1.insert(ur1);
                dur1.compactAndSortArray();
                
                dur2 = new InvertedList();
                dur2.insert(ur2);
                dur2.compactAndSortArray();
                        
                durArray = new InvertedList[2];
                durArray[0] = dur1;
                durArray[1] = dur2;
                
                neighbor1 = new IndexedRatedRes(1, 12);  //(3.0 * 4.0)
                neighbor2 = new IndexedRatedRes(2, 10); //(2.0 * 5.0)
                
                actualNeighborhood = ListOperations.computeNeighborhood(
                        userId, twoUserRatedRes, usersL2Norms,
                        durArray, 2, decimalPlaces);
                
                //Best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor1.getResourceId(), expNeighbor.getResourceId());
                //Assert.assertEquals(new Double(expNeighbor.getRating()), new Double(expNeighbor.getRating()));
                Assert.assertTrue(Math.abs(neighbor1.getRating() - expNeighbor.getRating()) < 0.0001);
                
                //Second best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor2.getResourceId(), expNeighbor.getResourceId());
                //Assert.assertEquals(new Double(neighbor2.getRating()), new Double(expNeighbor.getRating()));
                Assert.assertTrue(Math.abs(neighbor2.getRating() - expNeighbor.getRating()) < 0.0001);
                
                /*Test 3*/               
                ur1 = new IndexedUserRating(1, 4.0);
                ur2 = new IndexedUserRating(2, 5.5);
                ur3 = new IndexedUserRating(2, 3.5);
                
                
                dur1 = new InvertedList();
                dur1.insert(ur1);
                dur1.compactAndSortArray();
                dur1.insert(ur2);
                dur1.compactAndSortArray();
                
                dur2 = new InvertedList();
                dur2.insert(ur3);
                dur2.compactAndSortArray();
                        
                durArray = new InvertedList[2];
                durArray[0] = dur1;
                durArray[1] = dur2;
                
                neighbor1 = new IndexedRatedRes(2, 23.5); // ( (3.0 * 5.5) + (2.0 * 3.5) ) 
                neighbor2 = new IndexedRatedRes(1, 12); //(3.0 * 4.0)
                
                actualNeighborhood = ListOperations.computeNeighborhood(
                        userId, twoUserRatedRes, usersL2Norms,
                        durArray, 2, decimalPlaces);
                
                //Best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor1.getResourceId(), expNeighbor.getResourceId());
                Assert.assertEquals(neighbor1.getRating(), expNeighbor.getRating(), DELTA);
                
                //Second best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor2.getResourceId(), expNeighbor.getResourceId());
                Assert.assertEquals(neighbor2.getRating(), expNeighbor.getRating(), DELTA);
        }
        
        /**
         * Short test of ListOperations.computeNeighborhood(...) which uses scaled
         * inverted lists
         */
        @Test
        public void testComputeSilNeighborhood() {
                int decimalPlaces = 4;
                //Definitions of all objects used:
                IndexedUserRating ur1 = null;
                IndexedUserRating ur2 = null;
                IndexedUserRating ur3 = null;
                InvertedList dur1 = null;
                InvertedList dur2 = null;
                InvertedList[] durArray = null;
                IndexedRatedRes neighbor1 = null;
                MinMaxPriorityQueue<IndexedRatedRes> actualNeighborhood = null;
                IndexedRatedRes neighbor2 = null;
                IndexedRatedRes  expNeighbor = null;
                Map<Integer, Double> usersL2Norms
                        = new HashMap<Integer, Double>();
                
                //The user for which we compute the neighborhood is the user 0.
                //His ratings are the following:
                int userId = 0;
                double[] twoUserRatedRes = new double[2];
                twoUserRatedRes[0] = 3.0; //rating of item 1
                twoUserRatedRes[1] = 2.0; //rating of item 2
                //We also need to store the l2 norms
                //For simplicity all the l2norms will be set to one
                usersL2Norms.put(0,1.0);
                usersL2Norms.put(1,1.0);
                usersL2Norms.put(2,1.0);
            
                /*Test 1*/
                ur1 = new IndexedUserRating(1, 12.0);
                ur2 = new IndexedUserRating(1, 10.0);
                
                dur1 = new InvertedList();
                dur1.insert(ur1);
                dur1.compactAndSortArray();
                
                dur2 = new InvertedList();
                dur2.insert(ur2);
                dur2.compactAndSortArray();
                        
                durArray = new InvertedList[2];
                durArray[0] = dur1;
                durArray[1] = dur2;
                
                neighbor1 = new IndexedRatedRes(1, 22.0); //((3.0 * 4.0) + (2.0 + 5.0))
                
                actualNeighborhood = ListOperations.computeSilNeighborhood(
                        userId, usersL2Norms,
                        durArray, 1, decimalPlaces);
                                
                //Best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor1.getResourceId(), expNeighbor.getResourceId());
                //Assert.assertEquals(new Double(neighbor1.getRating()), new Double(expNeighbor.getRating()));
                Assert.assertEquals(neighbor1.getRating(), expNeighbor.getRating(), DELTA);
                
                /*Test 2*/
                ur1 = new IndexedUserRating(1, 12.0);
                ur2 = new IndexedUserRating(2, 10.0);
                
                dur1 = new InvertedList();
                dur1.insert(ur1);
                dur1.compactAndSortArray();
                
                dur2 = new InvertedList();
                dur2.insert(ur2);
                dur2.compactAndSortArray();
                        
                durArray = new InvertedList[2];
                durArray[0] = dur1;
                durArray[1] = dur2;
                
                neighbor1 = new IndexedRatedRes(1, 12);  //(3.0 * 4.0)
                neighbor2 = new IndexedRatedRes(2, 10); //(2.0 * 5.0)
                
                actualNeighborhood = ListOperations.computeSilNeighborhood(
                        userId, usersL2Norms,
                        durArray, 2, decimalPlaces);
                
                //Best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor1.getResourceId(), expNeighbor.getResourceId());
                //Assert.assertEquals(new Double(expNeighbor.getRating()), new Double(expNeighbor.getRating()));
                Assert.assertEquals(neighbor1.getRating(), expNeighbor.getRating(), DELTA);
                
                //Second best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor2.getResourceId(), expNeighbor.getResourceId());
                //Assert.assertEquals(new Double(neighbor2.getRating()), new Double(expNeighbor.getRating()));
                Assert.assertEquals(neighbor2.getRating(), expNeighbor.getRating(), DELTA);
                
                /*Test 3*/               
                ur1 = new IndexedUserRating(1, 12.0);
                ur2 = new IndexedUserRating(2, 16.5);
                ur3 = new IndexedUserRating(2, 7.0);
                
                
                dur1 = new InvertedList();
                dur1.insert(ur1);
                dur1.compactAndSortArray();
                dur1.insert(ur2);
                dur1.compactAndSortArray();
                
                dur2 = new InvertedList();
                dur2.insert(ur3);
                dur2.compactAndSortArray();
                        
                durArray = new InvertedList[2];
                durArray[0] = dur1;
                durArray[1] = dur2;
                
                neighbor1 = new IndexedRatedRes(2, 23.5); // ( (2.0 * 5.5) + (2.0 * 3.5) ) 
                neighbor2 = new IndexedRatedRes(1, 12); //(3.0 * 4.0)
                
                actualNeighborhood = ListOperations.computeSilNeighborhood(
                        userId, usersL2Norms,
                        durArray, 2, decimalPlaces);
                
                //Best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor1.getResourceId(), expNeighbor.getResourceId());
                Assert.assertEquals(neighbor1.getRating(), expNeighbor.getRating(), DELTA);
                
                //Second best neighbor
                expNeighbor = actualNeighborhood.poll();
                Assert.assertEquals(neighbor2.getResourceId(), expNeighbor.getResourceId());
                Assert.assertEquals(neighbor2.getRating(), expNeighbor.getRating(), DELTA);
        }
        
        /**
         * 
         */
        @Test
        public void testDeterministicOrderOfNeighbors() {
                Comparator<IndexedRatedRes> ratedResRatingComparator = new IndexedRatedResRatingComparator();
                
                int neighborhoodSize = 3;
                
                //We create a min max queue to see how this works when
                //more neighbors with the same similarity score are pushed.
                MinMaxPriorityQueue<IndexedRatedRes> neighborhood1 = 
                        MinMaxPriorityQueue.orderedBy(ratedResRatingComparator)
                            .maximumSize(neighborhoodSize)
                            .create();
                
                IndexedRatedRes neighbor1 = new IndexedRatedRes(3, 0.5);
                IndexedRatedRes neighbor2 = new IndexedRatedRes(4, 0.5);
                IndexedRatedRes neighbor3 = new IndexedRatedRes(2, 0.5);
                IndexedRatedRes neighbor4 = new IndexedRatedRes(1, 1.0);
                
                neighborhood1.add(neighbor1);
                neighborhood1.add(neighbor2);
                neighborhood1.add(neighbor3);
                neighborhood1.add(neighbor4);
                
                //at the end of this process the neighbors should be 
                //neighbor4, neighbor3, neighbor1
                IndexedRatedRes actualFirstNeighbor = neighborhood1.poll();
                IndexedRatedRes actualSecondNeighbor = neighborhood1.poll();
                IndexedRatedRes actualThirdNeighbor = neighborhood1.poll();
                IndexedRatedRes actualFourthNeighbor = neighborhood1.poll();
                
                Assert.assertEquals(neighbor4, actualFirstNeighbor);
                Assert.assertEquals(neighbor2, actualSecondNeighbor);
                Assert.assertEquals(neighbor1, actualThirdNeighbor);
                Assert.assertNull(actualFourthNeighbor);
                
                neighborhood1.clear();
                
                //Some other order. The results should not change.
                neighborhood1.add(neighbor2);
                neighborhood1.add(neighbor1);
                neighborhood1.add(neighbor3);
                neighborhood1.add(neighbor4);
                
                actualFirstNeighbor = neighborhood1.poll();
                actualSecondNeighbor = neighborhood1.poll();
                actualThirdNeighbor = neighborhood1.poll();
                actualFourthNeighbor = neighborhood1.poll();
                
                Assert.assertEquals(neighbor4, actualFirstNeighbor);
                Assert.assertEquals(neighbor2, actualSecondNeighbor);
                Assert.assertEquals(neighbor1, actualThirdNeighbor);
                Assert.assertNull(actualFourthNeighbor);
                
                //Since this is fine I have to take a look what happens 
                //when you transform the queue to an array. Maybe the order
                //is messed up there.
                neighborhood1.clear();
                
                neighborhood1.add(neighbor2);
                neighborhood1.add(neighbor1);
                neighborhood1.add(neighbor3);
                neighborhood1.add(neighbor4);
                
                IndexedRatedRes[] neighborhoodArray = neighborhood1.toArray(new IndexedRatedRes[3]);
                Arrays.sort(neighborhoodArray, ratedResRatingComparator);
                
                Assert.assertEquals(neighbor4, neighborhoodArray[0]);
                Assert.assertEquals(neighbor2, neighborhoodArray[1]);
                Assert.assertEquals(neighbor1, neighborhoodArray[2]);
                //Assert.assertNull(neighborhoodArray[4]);
                
                //another ordering
                neighborhood1.clear();
                
                neighborhood1.add(neighbor1);
                neighborhood1.add(neighbor2);
                neighborhood1.add(neighbor3);
                neighborhood1.add(neighbor4);
                
                neighborhoodArray = neighborhood1.toArray(new IndexedRatedRes[3]);
                Arrays.sort(neighborhoodArray, ratedResRatingComparator);
                
                Assert.assertEquals(neighbor4, neighborhoodArray[0]);
                Assert.assertEquals(neighbor2, neighborhoodArray[1]);
                Assert.assertEquals(neighbor1, neighborhoodArray[2]);                
        }
}
