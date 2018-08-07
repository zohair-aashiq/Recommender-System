/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist.impl;

import org.eclipse.rdf4j.recommender.storage.index.invlist.impl.CfInvListBasedStorage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.paradigm.collaborative.CfRecommender;
import org.eclipse.rdf4j.recommender.storage.IndexBasedStorage;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for CfInvListBasedStorage.
 */
public class CfInvListBasedStorageWithRatingsTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-15;                        
        
        /**
         * Test if all resources are indexed correctly.
         * @throws RecommenderException 
         */
        @Test
        public void testIndexingSd() throws RecommenderException {
                int sizeOfNeighborhood = 1;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createRecSparqlMoviesUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, sizeOfNeighborhood);
                IndexBasedStorage storage = (IndexBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //We can start with the resource counter.
                //Stored are only users and items. For the test file this makes an overall of 6 resources.
                int expectedIndexOfResources = 6;                        
                Assert.assertEquals(expectedIndexOfResources, storage.getResourceCounter());

                //The list that contains the resources should instead have 10000 entries
                //but only the first six should be different than null.
                int  i = 0;
                for (String resource: storage.getResourceList()) {
                        if (i < 6) {
                                Assert.assertTrue(resource != null);
                        } else {
                                Assert.assertTrue(resource == null);
                        }
                        i++;
                }
                
                //The same applies for the following map
                Assert.assertEquals(expectedIndexOfResources, storage.getResourceIdMap().size());

                //We verify that all resources are indexed
                Assert.assertTrue(storage.getResourceList().contains("http://example.org/movies#Bob"));
                Assert.assertTrue(storage.getResourceList().contains("http://example.org/movies#Gravity"));
                Assert.assertTrue(storage.getResourceList().contains("http://example.org/movies#Django_Unchained"));
                Assert.assertTrue(storage.getResourceList().contains("http://example.org/movies#Alice"));
                Assert.assertTrue(storage.getResourceList().contains("http://example.org/movies#Elysium"));
                Assert.assertTrue(storage.getResourceList().contains("http://example.org/movies#Man_of_steel"));

                int index = 0;
                //we assert that for each entry in this list, an entry in the map                        
                for (String resourceName: storage.getResourceList()) {
                        if (resourceName != null) {
                                Assert.assertTrue(storage.getResourceIdMap().containsKey(resourceName));
                                Assert.assertEquals(new Integer(index), 
                                        storage.getResourceIdMap().get(resourceName));
                                Assert.assertEquals(new Integer(index), 
                                        new Integer(storage.getIndexOf(resourceName)));
                                index++;
                        }
                }
        }
        
        /**
         * Test if the min and max values stored in the system are correct.
         * @throws RecommenderException 
         *//*
        @Test
        public void testMinAndMax() throws RecommenderException {
                TestFixedSailRecommenderRepository recRepository = createTestRepositoryAndPreprocess2();
                IndexBasedStorage storage = ((CfRecommender)recRepository.getRecommender()).getStorageModel();
                                                              
                //We assert whether MIN and MAX ratings are correct
                Double expectedMinRating = 3.0;
                Assert.assertEquals(expectedMinRating, storage.getDatasetMinRating());

                Double expectedMaxRating = 9.5;
                Assert.assertEquals(expectedMaxRating, storage.getDatasetMaxRating());
        }*/
        
        /**
         * Test if for each user the set of rated resources is stored correctly.
         * @throws RecommenderException 
         */
        @Test
        public void testResourcesMapSd() throws RecommenderException {
                int sizeOfNeighborhood = 1;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createRecSparqlMoviesUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, sizeOfNeighborhood);
                IndexBasedStorage storage = (IndexBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                Map<Integer, Set<IndexedRatedRes>> expectedUserRatedItemsMap = new HashMap<Integer, Set<IndexedRatedRes>>();
                //Get the indeces of users
                int indexOfBob = storage.getIndexOf("http://example.org/movies#Bob");
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");

                //Get the indeces of movies
                int indexOfElysium = storage.getIndexOf("http://example.org/movies#Elysium");
                int indexOfGravity = storage.getIndexOf("http://example.org/movies#Gravity");
                int indexOfDjango = storage.getIndexOf("http://example.org/movies#Django_Unchained");
                int indexOfManOfSteel = storage.getIndexOf("http://example.org/movies#Man_of_steel");                                        

                double normalizationForBob = Math.sqrt(Math.pow(4.0, 2) + Math.pow(9.0, 2));
                double normalizationForAlice = Math.sqrt(Math.pow(3.0, 2) + Math.pow(5.0, 2) + Math.pow(9.5, 2) + Math.pow(8.0, 2));

                IndexedRatedRes res1 = new IndexedRatedRes(indexOfElysium, 3.0 );
                IndexedRatedRes res2 = new IndexedRatedRes(indexOfGravity, 4.0 );
                IndexedRatedRes res3 = new IndexedRatedRes(indexOfGravity, 5.0 );
                IndexedRatedRes res4 = new IndexedRatedRes(indexOfDjango, 9.0 );
                IndexedRatedRes res5 = new IndexedRatedRes(indexOfDjango, 9.5 );
                IndexedRatedRes res6 = new IndexedRatedRes(indexOfManOfSteel, 8.0 );
                
                Set<IndexedRatedRes> bobResources = new HashSet();
                bobResources.add(res2);
                bobResources.add(res4);
                
                Set<IndexedRatedRes> aliceResources = new HashSet();
                aliceResources.add(res1);
                aliceResources.add(res3);
                aliceResources.add(res5);
                aliceResources.add(res6);

                //This is only testing if the resources are actually there but not the ratings.
                //This happens because of the equals method.
                Assert.assertEquals(storage.getUserRatedItemsMap().get(indexOfBob), bobResources);
                Assert.assertEquals(storage.getUserRatedItemsMap().get(indexOfAlice), aliceResources);
                
                //Now we verify that the ratings are the same.
                Set<IndexedRatedRes> bobRes = storage.getUserRatedItemsMap().get(indexOfBob);
                for (IndexedRatedRes ratRes: bobRes) {
                        if (ratRes.equals(res2)) {
                                Assert.assertTrue(ratRes.getRating()==res2.getRating());
                        }else if (ratRes.equals(res4)) {
                                Assert.assertTrue(ratRes.getRating()==res4.getRating());
                        }
                }
                
                Set<IndexedRatedRes> aliceRes = storage.getUserRatedItemsMap().get(indexOfAlice);
                for (IndexedRatedRes ratRes: aliceRes) {
                        if (ratRes.equals(res1)) {
                                Assert.assertTrue(ratRes.getRating()==res1.getRating());
                        } else if (ratRes.equals(res3)) {
                                Assert.assertTrue(ratRes.getRating()==res3.getRating());
                        } else if (ratRes.equals(res5)) {
                                Assert.assertTrue(ratRes.getRating()==res5.getRating());
                        } else if (ratRes.equals(res6)) {
                                Assert.assertTrue(ratRes.getRating()==res6.getRating());
                        }
                } 
        }
        
        /**
         * Test if inverted lists are built correctly.
         * @throws RecommenderException 
         */
        @Test
        public void testInvertedListsSd() throws RecommenderException {
                int sizeOfNeighborhood = 1;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createRecSparqlMoviesUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, sizeOfNeighborhood);
                CfInvListBasedStorage storage = (CfInvListBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //Get the indeces of users
                int indexOfBob = storage.getIndexOf("http://example.org/movies#Bob");
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");

                //Get the indeces of movies
                int indexOfElysium = storage.getIndexOf("http://example.org/movies#Elysium");
                int indexOfGravity = storage.getIndexOf("http://example.org/movies#Gravity");
                int indexOfDjango = storage.getIndexOf("http://example.org/movies#Django_Unchained");
                int indexOfManOfSteel = storage.getIndexOf("http://example.org/movies#Man_of_steel");
                
                double normalizationForBob = Math.sqrt(Math.pow(4.0, 2) + Math.pow(9.0, 2));
                double normalizationForAlice = Math.sqrt(Math.pow(3.0, 2) + Math.pow(5.0, 2) + Math.pow(9.5, 2) + Math.pow(8.0, 2));

                /*
                IndexedRatedRes res1 = new IndexedRatedRes(indexOfElysium, 3.0);
                IndexedRatedRes res2 = new IndexedRatedRes(indexOfGravity, 4.0);
                IndexedRatedRes res3 = new IndexedRatedRes(indexOfGravity, 5.0);
                IndexedRatedRes res4 = new IndexedRatedRes(indexOfDjango, 9.0);
                IndexedRatedRes res5 = new IndexedRatedRes(indexOfDjango, 9.5);
                IndexedRatedRes res6 = new IndexedRatedRes(indexOfManOfSteel, 8.0);
                */
                
                Map<Integer, InvertedList> actualInvertedLists 
                        = storage.getResInvertedLists();
                
                //The map should have one entry, one for each movie.
                //Actually the map is initialized with 10000 empty keys. But
                //there are 4 keys different than null.                
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfElysium));
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfGravity));
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfManOfSteel));
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfDjango));
                
                //Now I have to test the content of each of the lists.
                InvertedList elysiumInvList = actualInvertedLists.get(indexOfElysium);
                InvertedList gravityInvList = actualInvertedLists.get(indexOfGravity);
                InvertedList mosInvList = actualInvertedLists.get(indexOfManOfSteel);
                InvertedList djangoInvList = actualInvertedLists.get(indexOfDjango);                
                
                Integer numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: elysiumInvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfAlice) {
                                        Assert.assertEquals(new Double(3.0), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }
                        }
                }
                                
                Assert.assertEquals(new Integer(1), new Integer(elysiumInvList.getInnerArray().length));
                Assert.assertEquals(new Integer(1), numberOfCorrectRatings);
                
                numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: gravityInvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfBob) {
                                        Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfAlice) {
                                        Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }
                        }
                }
                Assert.assertEquals(new Integer(2), new Integer(gravityInvList.getInnerArray().length));
                Assert.assertEquals(new Integer(2), numberOfCorrectRatings);  
                
                numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: djangoInvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfBob && ur.getRating() == (9.0 )) {
                                        Assert.assertEquals(new Double(9.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfAlice && ur.getRating() == (9.5 )) {
                                        Assert.assertEquals(new Double(9.5 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }
                        }
                }
                Assert.assertEquals(new Integer(2), new Integer(djangoInvList.getInnerArray().length));
                Assert.assertEquals(new Integer(2), numberOfCorrectRatings);
                
                numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: mosInvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfAlice && ur.getRating() == (8.0 )) {
                                        Assert.assertEquals(new Double(8.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }                                 
                        }
                }
                Assert.assertEquals(new Integer(1), new Integer(mosInvList.getInnerArray().length));
                Assert.assertEquals(new Integer(1), numberOfCorrectRatings);
        }
        
        /**
         * Test inverted lists on the on the book dataset.
         * @throws RecommenderException 
         */
        @Test
        public void testInvertedListsSd2() throws RecommenderException {
                int sizeOfNeighborhood = 4;
                int numberOfTopRatings = 0; 
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, sizeOfNeighborhood, numberOfTopRatings);
                CfInvListBasedStorage storage = 
                        (CfInvListBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //Get the indeces of users
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
                int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
                int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
                int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
                int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");

                //Get the indeces of movies
                int indexOfItem1 = storage.getIndexOf("http://example.org/movies#Item1");
                int indexOfItem2 = storage.getIndexOf("http://example.org/movies#Item2");
                int indexOfItem3 = storage.getIndexOf("http://example.org/movies#Item3");
                int indexOfItem4 = storage.getIndexOf("http://example.org/movies#Item4");
                int indexOfItem5 = storage.getIndexOf("http://example.org/movies#Item5");

                /*    
                IndexedRatedRes resAlice1 = new IndexedRatedRes(indexOfItem1, 5.0);
                IndexedRatedRes resAlice2 = new IndexedRatedRes(indexOfItem2, 3.0);
                IndexedRatedRes resAlice3 = new IndexedRatedRes(indexOfItem3, 4.0);
                IndexedRatedRes resAlice4 = new IndexedRatedRes(indexOfItem4, 4.0);
                
                IndexedRatedRes resUser11 = new IndexedRatedRes(indexOfItem1, 3.0);
                IndexedRatedRes resUser12 = new IndexedRatedRes(indexOfItem2, 1.0);
                IndexedRatedRes resUser13 = new IndexedRatedRes(indexOfItem3, 2.0);
                IndexedRatedRes resUser14 = new IndexedRatedRes(indexOfItem4, 3.0);
                IndexedRatedRes resUser15 = new IndexedRatedRes(indexOfItem5, 3.0);
                
                IndexedRatedRes resUser21 = new IndexedRatedRes(indexOfItem1, 4.0);
                IndexedRatedRes resUser22 = new IndexedRatedRes(indexOfItem2, 3.0);
                IndexedRatedRes resUser23 = new IndexedRatedRes(indexOfItem3, 4.0);
                IndexedRatedRes resUser24 = new IndexedRatedRes(indexOfItem4, 3.0);
                IndexedRatedRes resUser25 = new IndexedRatedRes(indexOfItem5, 5.0);
                
                IndexedRatedRes resUser31 = new IndexedRatedRes(indexOfItem1, 3.0);
                IndexedRatedRes resUser32 = new IndexedRatedRes(indexOfItem2, 3.0);
                IndexedRatedRes resUser33 = new IndexedRatedRes(indexOfItem3, 1.0);
                IndexedRatedRes resUser34 = new IndexedRatedRes(indexOfItem4, 5.0);
                IndexedRatedRes resUser35 = new IndexedRatedRes(indexOfItem5, 4.0);
                
                IndexedRatedRes resUser41 = new IndexedRatedRes(indexOfItem1, 1.0);
                IndexedRatedRes resUser42 = new IndexedRatedRes(indexOfItem2, 5.0);
                IndexedRatedRes resUser43 = new IndexedRatedRes(indexOfItem3, 5.0);
                IndexedRatedRes resUser44 = new IndexedRatedRes(indexOfItem4, 2.0);
                IndexedRatedRes resUser45 = new IndexedRatedRes(indexOfItem5, 1.0);
                */
                
                double normalizationForAlice = Math.sqrt(Math.pow(5.0, 2) + Math.pow(3.0, 2) + Math.pow(4, 2) + Math.pow(4, 2));
                double normalizationForUser1 = Math.sqrt(Math.pow(3.0, 2) + Math.pow(1.0, 2) + Math.pow(2, 2) + Math.pow(3, 2) + Math.pow(3, 2));
                double normalizationForUser2 = Math.sqrt(Math.pow(4.0, 2) + Math.pow(3.0, 2) + Math.pow(4, 2) + Math.pow(3, 2) + Math.pow(5, 2));
                double normalizationForUser3 = Math.sqrt(Math.pow(3.0, 2) + Math.pow(3.0, 2) + Math.pow(1, 2) + Math.pow(5, 2) + Math.pow(4, 2));
                double normalizationForUser4 = Math.sqrt(Math.pow(1.0, 2) + Math.pow(5.0, 2) + Math.pow(5, 2) + Math.pow(2, 2) + Math.pow(1, 2));                              
                             
                
                Map<Integer, InvertedList> actualInvertedLists 
                        = storage.getResInvertedLists();
                
                //The map should have one entry, one for each movie.
                //Actually the map is initialized with 10000 empty keys. But
                //there are 5 keys different than null.                
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem1));
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem2));
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem3));
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem4));
                Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem5));
                
                //Now I have to test the content of each of the lists.
                InvertedList item1InvList = actualInvertedLists.get(indexOfItem1);
                InvertedList item2InvList = actualInvertedLists.get(indexOfItem2);
                InvertedList item3InvList = actualInvertedLists.get(indexOfItem3);
                InvertedList item4InvList = actualInvertedLists.get(indexOfItem4);
                InvertedList item5InvList = actualInvertedLists.get(indexOfItem5);
                                                                
                Integer numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: item1InvList.getInnerArray()) {                              
                        if (ur != null) {
                                if (ur.getUserId() == indexOfAlice) {
                                        Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser1) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser2) {
                                        Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser3 && ur.getRating() == 3.0 ) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser4 && ur.getRating() == 1.0 ) {
                                        Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }
                        }
                }
                //Tests the number of ratings given to movie Item1
                Assert.assertEquals(new Integer(5), new Integer(item1InvList.getInnerArray().length));
                Assert.assertEquals(new Integer(5), numberOfCorrectRatings);
                
                numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: item2InvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfAlice) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser1) {
                                        Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser2) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser3) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser4) {
                                        Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }
                        }
                }
                //Tests the number of ratings given to movie Item2
                Assert.assertEquals(new Integer(5), new Integer(item2InvList.getInnerArray().length));
                Assert.assertEquals(new Integer(5), numberOfCorrectRatings);
                
                numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: item3InvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfAlice) {
                                        Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser1) {
                                        Assert.assertEquals(new Double(2.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser2) {
                                        Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser3) {
                                     Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser4) {
                                        Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }
                        }
                }
                //Tests the number of ratings given to movie Item3
                Assert.assertEquals(new Integer(5), new Integer(item3InvList.getInnerArray().length));
                Assert.assertEquals(new Integer(5), numberOfCorrectRatings);
                
                numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: item4InvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfAlice) {
                                        Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser1) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser2) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser3) {
                                        Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser4) {
                                        Assert.assertEquals(new Double(2.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } 
                        }
                }
                //Tests the number of ratings given to movie Item1
                Assert.assertEquals(new Integer(5), new Integer(item4InvList.getInnerArray().length));
                Assert.assertEquals(new Integer(5), numberOfCorrectRatings);
                
                numberOfCorrectRatings = 0;
                for (IndexedUserRating ur: item5InvList.getInnerArray()) {
                        if (ur != null) {
                                if (ur.getUserId() == indexOfUser1) {
                                        Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser2) {
                                        Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser3) {
                                        Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                } else if (ur.getUserId() == indexOfUser4) {
                                        Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                                        numberOfCorrectRatings++;
                                }
                        }
                }
                //Tests the number of ratings given to movie Item1
                Assert.assertEquals(new Integer(4), new Integer(item5InvList.getInnerArray().length));
                Assert.assertEquals(new Integer(4), numberOfCorrectRatings);                                
        }
        
        /**
         * Test l2norms.
         * @throws RecommenderException 
         */
        @Test
        public void testL2NormsSd
        () throws RecommenderException {
                int sizeOfNeighborhood = 1;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createRecSparqlMoviesUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, sizeOfNeighborhood);
                IndexBasedStorage storage = (IndexBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //Get the indeces of users
                int indexOfBob = storage.getIndexOf("http://example.org/movies#Bob");
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");

                //Get the indeces of movies
                int indexOfElysium = storage.getIndexOf("http://example.org/movies#Elysium");
                int indexOfGravity = storage.getIndexOf("http://example.org/movies#Gravity");
                int indexOfDjango = storage.getIndexOf("http://example.org/movies#Django_Unchained");
                int indexOfManOfSteel = storage.getIndexOf("http://example.org/movies#Man_of_steel");                                    

                double normalizationForBob = Math.sqrt(Math.pow(4.0, 2) + Math.pow(9.0, 2));
                double normalizationForAlice = Math.sqrt(Math.pow(3.0, 2) + Math.pow(5.0, 2) + Math.pow(9.5, 2) + Math.pow(8.0, 2));

                IndexedRatedRes res1 = new IndexedRatedRes(indexOfElysium, 3.0);
                IndexedRatedRes res2 = new IndexedRatedRes(indexOfGravity, 4.0);
                IndexedRatedRes res3 = new IndexedRatedRes(indexOfGravity, 5.0);
                IndexedRatedRes res4 = new IndexedRatedRes(indexOfDjango, 9.0);
                IndexedRatedRes res5 = new IndexedRatedRes(indexOfDjango, 9.5);
                IndexedRatedRes res6 = new IndexedRatedRes(indexOfManOfSteel, 8.0);
                
                //Test l2norms
                Assert.assertEquals(normalizationForBob, storage.getL2NormOfUser(indexOfBob), DELTA);
                Assert.assertEquals(normalizationForAlice, storage.getL2NormOfUser(indexOfAlice), DELTA);
        }
        
        /**
         * Test l2norms on the RecSPARQL dataset.
         * @throws RecommenderException 
         */
        @Test
        public void testL2NormsSd2() throws RecommenderException {
                int numberOfTopRatings = 0; 
                int sizeOfNeighborhood = 4;
                SailRecommenderRepository recRepository =
                        TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, sizeOfNeighborhood, numberOfTopRatings);            
                IndexBasedStorage storage = (IndexBasedStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //Get the indeces of users
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
                int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
                int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
                int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
                int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");

                //Get the indeces of movies
                int indexOfItem1 = storage.getIndexOf("http://example.org/movies#Item1");
                int indexOfItem2 = storage.getIndexOf("http://example.org/movies#Item2");
                int indexOfItem3 = storage.getIndexOf("http://example.org/movies#Item3");
                int indexOfItem4 = storage.getIndexOf("http://example.org/movies#Item4");
                int indexOfItem5 = storage.getIndexOf("http://example.org/movies#Item5");               
                
                double normalizationForAlice = Math.sqrt(Math.pow(5.0, 2) + Math.pow(3.0, 2) + Math.pow(4, 2) + Math.pow(4, 2));
                double normalizationForUser1 = Math.sqrt(Math.pow(3.0, 2) + Math.pow(1.0, 2) + Math.pow(2, 2) + Math.pow(3, 2) + Math.pow(3, 2));
                double normalizationForUser2 = Math.sqrt(Math.pow(4.0, 2) + Math.pow(3.0, 2) + Math.pow(4, 2) + Math.pow(3, 2) + Math.pow(5, 2));
                double normalizationForUser3 = Math.sqrt(Math.pow(3.0, 2) + Math.pow(3.0, 2) + Math.pow(1, 2) + Math.pow(5, 2) + Math.pow(4, 2));
                double normalizationForUser4 = Math.sqrt(Math.pow(1.0, 2) + Math.pow(5.0, 2) + Math.pow(5, 2) + Math.pow(2, 2) + Math.pow(1, 2)); 
                
                //Test l2norms               
                Assert.assertEquals(normalizationForAlice, storage.getL2NormOfUser(indexOfAlice), DELTA);
                Assert.assertEquals(normalizationForUser1, storage.getL2NormOfUser(indexOfUser1), DELTA);
                Assert.assertEquals(normalizationForUser2, storage.getL2NormOfUser(indexOfUser2), DELTA);
                Assert.assertEquals(normalizationForUser3, storage.getL2NormOfUser(indexOfUser3), DELTA);
                Assert.assertEquals(normalizationForUser4, storage.getL2NormOfUser(indexOfUser4), DELTA);
        }
}
