/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist.impl;

import org.eclipse.rdf4j.recommender.storage.index.invlist.impl.CfScaledInvListBasedStorage;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.paradigm.collaborative.CfRecommender;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for JavaArraysStorageModel.
 */
public class CfScaledInvListBasedStorageWithRatingsTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-15;                        
        
        /**
         * Tests the indexes for retrieving the inverted lists.
         * Dataset: Book
         * @throws RecommenderException 
         */
        @Test
        public void testIndexingWithoutOptimizationSd() throws RecommenderException {
                int numberOfNeighbors = 4;
                int numberOfTopRatings = 0;                
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.SCALED_INVERTED_LISTS, numberOfNeighbors, numberOfTopRatings);
                CfScaledInvListBasedStorage storage 
                        = (CfScaledInvListBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                //Get the indexes of movies
                int indexOfItem1 = storage.getIndexOf("http://example.org/movies#Item1");
                int indexOfItem2 = storage.getIndexOf("http://example.org/movies#Item2");
                int indexOfItem3 = storage.getIndexOf("http://example.org/movies#Item3");
                int indexOfItem4 = storage.getIndexOf("http://example.org/movies#Item4");
                int indexOfItem5 = storage.getIndexOf("http://example.org/movies#Item5");                                              
                
                Map<IndexedRatedRes, InvertedList> actualInvertedLists 
                        = storage.getResInvertedLists();
                
                //Keys of indexes
                IndexedRatedRes item1_5 = new IndexedRatedRes(indexOfItem1, 5.0);
                IndexedRatedRes item1_3 = new IndexedRatedRes(indexOfItem1, 3.0);
                IndexedRatedRes item1_4 = new IndexedRatedRes(indexOfItem1, 4.0);
                IndexedRatedRes item1_1 = new IndexedRatedRes(indexOfItem1, 1.0);
                                                              
                //-----------ITEM 1-------------             
                Assert.assertTrue(actualInvertedLists.containsKey(item1_5));
                Assert.assertTrue(actualInvertedLists.containsKey(item1_3));
                Assert.assertTrue(actualInvertedLists.containsKey(item1_4));
                Assert.assertTrue(actualInvertedLists.containsKey(item1_1));                  
                //------------------------------
                
                //Keys of indexes
                IndexedRatedRes item2_3 = new IndexedRatedRes(indexOfItem2, 3.0);
                IndexedRatedRes item2_1 = new IndexedRatedRes(indexOfItem2, 1.0);
                IndexedRatedRes item2_5 = new IndexedRatedRes(indexOfItem2, 5.0);
                //------------------------------
                
                //-----------ITEM 2------------- 
                Assert.assertTrue(actualInvertedLists.containsKey(item2_3));
                Assert.assertTrue(actualInvertedLists.containsKey(item2_1));
                Assert.assertTrue(actualInvertedLists.containsKey(item2_5));
                //------------------------------
                
                //Keys of indexes
                IndexedRatedRes item3_4 = new IndexedRatedRes(indexOfItem3, 4.0);
                IndexedRatedRes item3_2 = new IndexedRatedRes(indexOfItem3, 2.0);
                IndexedRatedRes item3_1 = new IndexedRatedRes(indexOfItem3, 1.0);
                IndexedRatedRes item3_5 = new IndexedRatedRes(indexOfItem3, 5.0);
                
                //-----------ITEM 3-------------             
                Assert.assertTrue(actualInvertedLists.containsKey(item3_4));
                Assert.assertTrue(actualInvertedLists.containsKey(item3_2));
                Assert.assertTrue(actualInvertedLists.containsKey(item3_1));
                Assert.assertTrue(actualInvertedLists.containsKey(item3_5));                  
                //------------------------------
                
                //Keys of indexes
                IndexedRatedRes item4_4 = new IndexedRatedRes(indexOfItem4, 4.0);
                IndexedRatedRes item4_3 = new IndexedRatedRes(indexOfItem4, 3.0);
                IndexedRatedRes item4_5 = new IndexedRatedRes(indexOfItem4, 5.0);
                IndexedRatedRes item4_2 = new IndexedRatedRes(indexOfItem4, 2.0);
                
                //-----------ITEM 4-------------             
                Assert.assertTrue(actualInvertedLists.containsKey(item4_4));
                Assert.assertTrue(actualInvertedLists.containsKey(item4_3));
                Assert.assertTrue(actualInvertedLists.containsKey(item4_5));
                Assert.assertTrue(actualInvertedLists.containsKey(item4_2));                 
                //------------------------------
                                
                //Keys of indexes
                IndexedRatedRes item5_3 = new IndexedRatedRes(indexOfItem5, 3.0);
                IndexedRatedRes item5_5 = new IndexedRatedRes(indexOfItem5, 5.0);
                IndexedRatedRes item5_4 = new IndexedRatedRes(indexOfItem5, 4.0);
                IndexedRatedRes item5_1 = new IndexedRatedRes(indexOfItem5, 1.0);
                
                //-----------ITEM 4-------------             
                Assert.assertTrue(actualInvertedLists.containsKey(item5_3));
                Assert.assertTrue(actualInvertedLists.containsKey(item5_5));
                Assert.assertTrue(actualInvertedLists.containsKey(item5_4));
                Assert.assertTrue(actualInvertedLists.containsKey(item5_1));                 
                //------------------------------                                
        }
        
        /**
         * Tests the content of the scaled inverted lists.
         * Dataset: Book
         * @throws RecommenderException 
         */
        @Test
        public void testSilWithoutOptimizationSd() throws RecommenderException {
                int numberOfNeighbors = 4;
                int numberOfTopRatings = 0;                
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.SCALED_INVERTED_LISTS, numberOfNeighbors, numberOfTopRatings);
                
                CfScaledInvListBasedStorage storage 
                        = (CfScaledInvListBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();                                                              
                //Get the indeces of users
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
                int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
                int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
                int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
                int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");                                      
                
                Map<Integer, InvertedList> actualInvertedLists 
                        = storage.getResInvertedLists();                   
                
                //I will test only four users: ALICE, USER1, USER3, USER4
                //-----------ALICE-------------             
                InvertedList[] durAlice = storage.getInvertedListsOfUser(indexOfAlice);
                Assert.assertEquals(new Integer(4), new Integer(durAlice.length));
                
                //This could be given in any order.
                InvertedList actualAliceItem1_5 = durAlice[3];
                InvertedList actualAliceItem2_3 = durAlice[1];
                InvertedList actualAliceItem3_4 = durAlice[2];
                InvertedList actualAliceItem4_4 = durAlice[0];                
                                
                InvertedList expectedAliceItem1_5 = new InvertedList();
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfAlice, 25));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser1, 15));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser2, 20));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser3, 15));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser4, 5));
                expectedAliceItem1_5.compactAndSortArray();
                Assert.assertEquals(expectedAliceItem1_5, actualAliceItem1_5);
                
                
                InvertedList expectedAliceItem2_3 = new InvertedList();
                expectedAliceItem2_3.insert(new IndexedUserRating(indexOfAlice, 9));
                expectedAliceItem2_3.insert(new IndexedUserRating(indexOfUser1, 3));
                expectedAliceItem2_3.insert(new IndexedUserRating(indexOfUser2, 9));
                expectedAliceItem2_3.insert(new IndexedUserRating(indexOfUser3, 9));
                expectedAliceItem2_3.insert(new IndexedUserRating(indexOfUser4, 15));                                      
                Assert.assertEquals(expectedAliceItem2_3, actualAliceItem2_3);
                
                InvertedList expectedAliceItem3_4 = new InvertedList();
                expectedAliceItem3_4.insert(new IndexedUserRating(indexOfAlice, 16));
                expectedAliceItem3_4.insert(new IndexedUserRating(indexOfUser1, 8));
                expectedAliceItem3_4.insert(new IndexedUserRating(indexOfUser2, 16));
                expectedAliceItem3_4.insert(new IndexedUserRating(indexOfUser3, 4));
                expectedAliceItem3_4.insert(new IndexedUserRating(indexOfUser4, 20));                                      
                Assert.assertEquals(expectedAliceItem3_4, actualAliceItem3_4);
                
                InvertedList expectedAliceItem4_4 = new InvertedList();
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfAlice, 16));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser1, 12));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser2, 12));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser3, 20));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser4, 8));                                      
                Assert.assertEquals(expectedAliceItem4_4, actualAliceItem4_4);
                
                //-----------USER 1------------- 
                InvertedList[] durUser1 = storage.getInvertedListsOfUser(indexOfUser1);
                Assert.assertEquals(new Integer(5), new Integer(durUser1.length));
                
                //(Item2,3),(Item1,3) will be merged.
                //This could be given in any order.
                InvertedList actualUser1Item1_3 = durUser1[3];
                InvertedList actualUser1Item2_1 = durUser1[0];
                InvertedList actualUser1Item3_2 = durUser1[1];
                InvertedList actualUser1Item4_3 = durUser1[2];
                InvertedList actualUser1Item5_3 = durUser1[4];
                                
                InvertedList expectedUser1Item1_3 = new InvertedList();
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfAlice, 15));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser1, 9));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser2, 12));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser3, 9));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser4, 3));
                expectedUser1Item1_3.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item1_3, actualUser1Item1_3);
                
                InvertedList expectedUser1Item2_1 = new InvertedList();
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfAlice, 3));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser1, 1));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser2, 3));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser3, 3));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser4, 5));
                expectedUser1Item2_1.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item2_1, actualUser1Item2_1);
                
                InvertedList expectedUser1Item3_2 = new InvertedList();
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfAlice, 8));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser1, 4));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser2, 8));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser3, 2));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser4, 10));
                expectedUser1Item3_2.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item3_2, actualUser1Item3_2);
                
                InvertedList expectedUser1Item4_3 = new InvertedList();
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfAlice, 12));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser1, 9));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser2, 9));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser3, 15));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser4, 6));
                expectedUser1Item4_3.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item4_3, actualUser1Item4_3);
                
                InvertedList expectedUser1Item5_3 = new InvertedList();
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser1, 9));
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser2, 15));
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser3, 12));
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser4, 3));
                expectedUser1Item5_3.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item5_3, actualUser1Item5_3);                
                
                //-----------USER 3-------------                 
                InvertedList[] durUser3 = storage.getInvertedListsOfUser(indexOfUser3);
                Assert.assertEquals(new Integer(5), new Integer(durUser3.length));
                
                //This could be given in any order.
                InvertedList actualUser3Item1_3 = durUser3[2];
                InvertedList actualUser3Item2_3 = durUser3[0];
                InvertedList actualUser3Item3_1 = durUser3[3];
                InvertedList actualUser3Item4_5 = durUser3[1];
                InvertedList actualUser3Item5_4 = durUser3[4];
                
                InvertedList expectedUser3Item1_3 = new InvertedList();
                expectedUser3Item1_3.insert(new IndexedUserRating(indexOfAlice, 15));
                expectedUser3Item1_3.insert(new IndexedUserRating(indexOfUser1, 9));
                expectedUser3Item1_3.insert(new IndexedUserRating(indexOfUser2, 12));
                expectedUser3Item1_3.insert(new IndexedUserRating(indexOfUser3, 9));
                expectedUser3Item1_3.insert(new IndexedUserRating(indexOfUser4, 3));
                expectedUser3Item1_3.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item1_3, actualUser3Item1_3);
                
                InvertedList expectedUser3Item2_3 = new InvertedList();
                expectedUser3Item2_3.insert(new IndexedUserRating(indexOfAlice, 9));
                expectedUser3Item2_3.insert(new IndexedUserRating(indexOfUser1, 3));
                expectedUser3Item2_3.insert(new IndexedUserRating(indexOfUser2, 9));
                expectedUser3Item2_3.insert(new IndexedUserRating(indexOfUser3, 9));
                expectedUser3Item2_3.insert(new IndexedUserRating(indexOfUser4, 15));
                expectedUser3Item2_3.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item2_3, actualUser3Item2_3);
                
                InvertedList expectedUser3Item3_1 = new InvertedList();
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfAlice, 4));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser1, 2));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser2, 4));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser3, 1));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser4, 5));
                expectedUser3Item3_1.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item3_1, actualUser3Item3_1);
                
                InvertedList expectedUser3Item4_5 = new InvertedList();
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfAlice, 20));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser1, 15));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser2, 15));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser3, 25));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser4, 10));
                expectedUser3Item4_5.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item4_5, actualUser3Item4_5);
                
                InvertedList expectedUser3Item5_4 = new InvertedList();
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser1, 12));
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser2, 20));
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser3, 16));
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser4, 4));
                expectedUser3Item5_4.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item5_4, actualUser3Item5_4);                                
                
                //-----------USER 4------------- 
                InvertedList[] durUser4 = storage.getInvertedListsOfUser(indexOfUser4);
                Assert.assertEquals(new Integer(5), new Integer(durUser4.length));
                
                //(Item2,3),(Item1,3) will be merged.
                //This could be given in any order.
                InvertedList actualUser4Item1_1 = durUser4[0];
                InvertedList actualUser4Item2_5 = durUser4[2];
                InvertedList actualUser4Item3_5 = durUser4[1];
                InvertedList actualUser4Item4_2 = durUser4[4];
                InvertedList actualUser4Item5_1 = durUser4[3];
                                
                InvertedList expectedUser4Item1_1 = new InvertedList();
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfAlice, 5));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser1, 3));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser2, 4));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser3, 3));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser4, 1));
                expectedUser4Item1_1.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item1_1, actualUser4Item1_1);
                
                InvertedList expectedUser4Item2_5 = new InvertedList();
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfAlice, 15));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser1, 5));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser2, 15));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser3, 15));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser4, 25));
                expectedUser4Item2_5.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item2_5, actualUser4Item2_5);
                
                InvertedList expectedUser4Item3_5 = new InvertedList();
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfAlice, 20));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser1, 10));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser2, 20));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser3, 5));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser4, 25));
                expectedUser4Item3_5.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item3_5, actualUser4Item3_5);
                
                InvertedList expectedUser4Item4_2 = new InvertedList();
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfAlice, 8));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser1, 6));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser2, 6));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser3, 10));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser4, 4));
                expectedUser4Item4_2.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item4_2, actualUser4Item4_2);
                
                InvertedList expectedUser4Item5_1 = new InvertedList();
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser1, 3));
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser2, 5));
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser3, 4));
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser4, 1));
                expectedUser4Item5_1.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item5_1, actualUser4Item5_1);
        }
        
        /**
         * Tests the content of the scaled inverted lists when the number of top
         * ratings is set to 3.
         * Dataset: Book
         * @throws RecommenderException 
         */
        @Test
        public void testSilWithOptimizationSd() throws RecommenderException {
                int numberOfNeighbors = 4;
                int numberOfTopRatings = 3;                
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.SCALED_INVERTED_LISTS, numberOfNeighbors, numberOfTopRatings);

                CfScaledInvListBasedStorage storage =
                        (CfScaledInvListBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                //Top 3 lists:
                //(Item2,3)
                //(Item3,4)
                //(Item1,3)
                                                              
                //Get the indeces of users
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
                int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
                int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
                int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
                int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");                                      
                
                Map<Integer, InvertedList> actualInvertedLists 
                        = storage.getResInvertedLists();                   
                
                //I will test only four users: ALICE, USER1, USER3, USER4
                //-----------ALICE-------------             
                InvertedList[] durAlice = storage.getInvertedListsOfUser(indexOfAlice);
                Assert.assertEquals(new Integer(3), new Integer(durAlice.length));
                
                //(Item2,3),(Item3,4) will be merged.
                //This could be given in any order.
                InvertedList actualAliceItem1_5 = durAlice[1];
                InvertedList actualAliceItem2_3_Item3_4 = durAlice[2];
                InvertedList actualAliceItem4_4 = durAlice[0];                
                                
                InvertedList expectedAliceItem1_5 = new InvertedList();
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfAlice, 25));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser1, 15));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser2, 20));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser3, 15));
                expectedAliceItem1_5.insert(new IndexedUserRating(indexOfUser4, 5));
                expectedAliceItem1_5.compactAndSortArray();
                Assert.assertEquals(expectedAliceItem1_5, actualAliceItem1_5);                
                
                InvertedList expectedAliceItem2_3_Item3_4 = new InvertedList();
                expectedAliceItem2_3_Item3_4.insert(new IndexedUserRating(indexOfAlice, 9 + 16));
                expectedAliceItem2_3_Item3_4.insert(new IndexedUserRating(indexOfUser1, 3 + 8));
                expectedAliceItem2_3_Item3_4.insert(new IndexedUserRating(indexOfUser2, 9 + 16));
                expectedAliceItem2_3_Item3_4.insert(new IndexedUserRating(indexOfUser3, 9 + 4));
                expectedAliceItem2_3_Item3_4.insert(new IndexedUserRating(indexOfUser4, 15 + 20));                                      
                Assert.assertEquals(expectedAliceItem2_3_Item3_4, actualAliceItem2_3_Item3_4);
                
                InvertedList expectedAliceItem4_4 = new InvertedList();
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfAlice, 16));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser1, 12));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser2, 12));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser3, 20));
                expectedAliceItem4_4.insert(new IndexedUserRating(indexOfUser4, 8));                                      
                Assert.assertEquals(expectedAliceItem4_4, actualAliceItem4_4);                                                                                          
                
                //-----------USER 3-------------                 
                InvertedList[] durUser3 = storage.getInvertedListsOfUser(indexOfUser3);
                Assert.assertEquals(new Integer(4), new Integer(durUser3.length));
                
                //(Item2,3),(Item1,3) will be merged.
                //This could be given in any order.
                InvertedList actualUser3Item3_1 = durUser3[2];
                InvertedList actualUser3Item4_5 = durUser3[0];
                InvertedList actualUser3Item5_4 = durUser3[3];
                InvertedList actualUser3Item1_3_Item2_3 = durUser3[1];
                
                
                InvertedList expectedUser3Item3_1 = new InvertedList();
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfAlice, 4));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser1, 2));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser2, 4));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser3, 1));
                expectedUser3Item3_1.insert(new IndexedUserRating(indexOfUser4, 5));
                expectedUser3Item3_1.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item3_1, actualUser3Item3_1);
                
                InvertedList expectedUser3Item4_5 = new InvertedList();
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfAlice, 20));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser1, 15));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser2, 15));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser3, 25));
                expectedUser3Item4_5.insert(new IndexedUserRating(indexOfUser4, 10));
                expectedUser3Item4_5.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item4_5, actualUser3Item4_5);  
                
                InvertedList expectedUser3Item5_4 = new InvertedList();
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser1, 12));
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser2, 20));
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser3, 16));
                expectedUser3Item5_4.insert(new IndexedUserRating(indexOfUser4, 4));
                expectedUser3Item5_4.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item5_4, actualUser3Item5_4);
                
                InvertedList expectedUser3Item1_3_Item2_3 = new InvertedList();
                expectedUser3Item1_3_Item2_3.insert(new IndexedUserRating(indexOfAlice, 15 + 9));
                expectedUser3Item1_3_Item2_3.insert(new IndexedUserRating(indexOfUser1, 9 + 3));
                expectedUser3Item1_3_Item2_3.insert(new IndexedUserRating(indexOfUser2, 12 + 9));
                expectedUser3Item1_3_Item2_3.insert(new IndexedUserRating(indexOfUser3, 9 + 9));
                expectedUser3Item1_3_Item2_3.insert(new IndexedUserRating(indexOfUser4, 3 + 15));
                expectedUser3Item1_3_Item2_3.compactAndSortArray();
                Assert.assertEquals(expectedUser3Item1_3_Item2_3, actualUser3Item1_3_Item2_3);
                
                //Two User for which there are no changes:
                //-----------USER 1------------- 
                InvertedList[] durUser1 = storage.getInvertedListsOfUser(indexOfUser1);
                Assert.assertEquals(new Integer(5), new Integer(durUser1.length));
                
                //(Item2,3),(Item1,3) will be merged.
                //This could be given in any order.
                InvertedList actualUser1Item1_3 = durUser1[3];
                InvertedList actualUser1Item2_1 = durUser1[0];
                InvertedList actualUser1Item3_2 = durUser1[1];
                InvertedList actualUser1Item4_3 = durUser1[2];
                InvertedList actualUser1Item5_3 = durUser1[4];
                                
                InvertedList expectedUser1Item1_3 = new InvertedList();
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfAlice, 15));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser1, 9));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser2, 12));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser3, 9));
                expectedUser1Item1_3.insert(new IndexedUserRating(indexOfUser4, 3));
                expectedUser1Item1_3.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item1_3, actualUser1Item1_3);
                
                InvertedList expectedUser1Item2_1 = new InvertedList();
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfAlice, 3));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser1, 1));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser2, 3));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser3, 3));
                expectedUser1Item2_1.insert(new IndexedUserRating(indexOfUser4, 5));
                expectedUser1Item2_1.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item2_1, actualUser1Item2_1);
                
                InvertedList expectedUser1Item3_2 = new InvertedList();
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfAlice, 8));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser1, 4));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser2, 8));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser3, 2));
                expectedUser1Item3_2.insert(new IndexedUserRating(indexOfUser4, 10));
                expectedUser1Item3_2.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item3_2, actualUser1Item3_2);
                
                InvertedList expectedUser1Item4_3 = new InvertedList();
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfAlice, 12));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser1, 9));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser2, 9));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser3, 15));
                expectedUser1Item4_3.insert(new IndexedUserRating(indexOfUser4, 6));
                expectedUser1Item4_3.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item4_3, actualUser1Item4_3);
                
                InvertedList expectedUser1Item5_3 = new InvertedList();
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser1, 9));
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser2, 15));
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser3, 12));
                expectedUser1Item5_3.insert(new IndexedUserRating(indexOfUser4, 3));
                expectedUser1Item5_3.compactAndSortArray();
                Assert.assertEquals(expectedUser1Item5_3, actualUser1Item5_3);
                
                //-----------USER 4------------- 
                InvertedList[] durUser4 = storage.getInvertedListsOfUser(indexOfUser4);
                Assert.assertEquals(new Integer(5), new Integer(durUser4.length));
                
                //(Item2,3),(Item1,3) will be merged.
                //This could be given in any order.
                InvertedList actualUser4Item1_1 = durUser4[0];
                InvertedList actualUser4Item2_5 = durUser4[2];
                InvertedList actualUser4Item3_5 = durUser4[1];
                InvertedList actualUser4Item4_2 = durUser4[4];
                InvertedList actualUser4Item5_1 = durUser4[3];
                                
                InvertedList expectedUser4Item1_1 = new InvertedList();
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfAlice, 5));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser1, 3));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser2, 4));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser3, 3));
                expectedUser4Item1_1.insert(new IndexedUserRating(indexOfUser4, 1));
                expectedUser4Item1_1.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item1_1, actualUser4Item1_1);
                
                InvertedList expectedUser4Item2_5 = new InvertedList();
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfAlice, 15));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser1, 5));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser2, 15));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser3, 15));
                expectedUser4Item2_5.insert(new IndexedUserRating(indexOfUser4, 25));
                expectedUser4Item2_5.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item2_5, actualUser4Item2_5);
                
                InvertedList expectedUser4Item3_5 = new InvertedList();
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfAlice, 20));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser1, 10));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser2, 20));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser3, 5));
                expectedUser4Item3_5.insert(new IndexedUserRating(indexOfUser4, 25));
                expectedUser4Item3_5.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item3_5, actualUser4Item3_5);
                
                InvertedList expectedUser4Item4_2 = new InvertedList();
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfAlice, 8));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser1, 6));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser2, 6));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser3, 10));
                expectedUser4Item4_2.insert(new IndexedUserRating(indexOfUser4, 4));
                expectedUser4Item4_2.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item4_2, actualUser4Item4_2);
                
                InvertedList expectedUser4Item5_1 = new InvertedList();
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser1, 3));
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser2, 5));
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser3, 4));
                expectedUser4Item5_1.insert(new IndexedUserRating(indexOfUser4, 1));
                expectedUser4Item5_1.compactAndSortArray();
                Assert.assertEquals(expectedUser4Item5_1, actualUser4Item5_1);
        }

        
        /**
         * Test l2norms.
         * Dataset: Book
         * @throws RecommenderException 
         */
        @Test
        public void testL2NormsSd() throws RecommenderException {
                int numberOfNeighbors = 4;
                int numberOfTopRatings = 0;                
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.SCALED_INVERTED_LISTS, numberOfNeighbors, numberOfTopRatings);

                CfScaledInvListBasedStorage storage 
                        = (CfScaledInvListBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //Get the indeces of users
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
                int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
                int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
                int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
                int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");            
                
                double normalizationForAlice = Math.sqrt(Math.pow(5, 2) + Math.pow(3, 2) + Math.pow(4, 2) + Math.pow(4, 2));
                double normalizationForUser1 = Math.sqrt(Math.pow(3, 2) + Math.pow(1, 2) + Math.pow(2, 2) + Math.pow(3, 2) + Math.pow(3, 2));
                double normalizationForUser2 = Math.sqrt(Math.pow(4, 2) + Math.pow(3, 2) + Math.pow(4, 2) + Math.pow(3, 2) + Math.pow(5, 2));
                double normalizationForUser3 = Math.sqrt(Math.pow(3, 2) + Math.pow(3, 2) + Math.pow(1, 2) + Math.pow(5, 2) + Math.pow(4, 2));
                double normalizationForUser4 = Math.sqrt(Math.pow(1, 2) + Math.pow(5, 2) + Math.pow(5, 2) + Math.pow(2, 2) + Math.pow(1, 2)); 
                
                //Test l2norms
                Assert.assertEquals(normalizationForAlice, storage.getL2NormOfUser(indexOfAlice), DELTA);
                Assert.assertEquals(normalizationForUser1, storage.getL2NormOfUser(indexOfUser1), DELTA);
                Assert.assertEquals(normalizationForUser2, storage.getL2NormOfUser(indexOfUser2), DELTA);
                Assert.assertEquals(normalizationForUser3, storage.getL2NormOfUser(indexOfUser3), DELTA);
                Assert.assertEquals(normalizationForUser4, storage.getL2NormOfUser(indexOfUser4), DELTA);
        }
}
