/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.spark.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.paradigm.collaborative.CfRecommender;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.index.spark.SparkStorage;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for CfSparkStorage.
 */
public class CfSparkStorageWithRatingsTest {
    
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
                RecStorage.SPARK, sizeOfNeighborhood);
        SparkStorage storage = (SparkStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();

        // Stored resources are only users and items. For the test file this makes an overall of 6 resources.
        int expectedIndexOfResources = 6;                        
        Assert.assertEquals(expectedIndexOfResources, storage.getResourceCounter());

        // Verify that all resources are indexed        
        Assert.assertTrue(storage.getResourceStrings().contains("http://example.org/movies#Bob"));
        Assert.assertTrue(storage.getResourceStrings().contains("http://example.org/movies#Gravity"));
        Assert.assertTrue(storage.getResourceStrings().contains("http://example.org/movies#Django_Unchained"));
        Assert.assertTrue(storage.getResourceStrings().contains("http://example.org/movies#Alice"));
        Assert.assertTrue(storage.getResourceStrings().contains("http://example.org/movies#Elysium"));
        Assert.assertTrue(storage.getResourceStrings().contains("http://example.org/movies#Man_of_steel"));

        Assert.assertEquals(expectedIndexOfResources, storage.getResourceIdMap().size());
        
        recRepository.releaseResources();
    }
    
    
    /**
     * Test if for each user the set of rated resources is stored correctly.
     * @throws RecommenderException 
     */
    @Test
    public void testResourcesMapSd() throws RecommenderException {
        
        int sizeOfNeighborhood = 1;
        
        SailRecommenderRepository recRepository = 
            TestRepositoryInstantiator.createRecSparqlMoviesUbCfRecAndPreprocess(
                RecStorage.SPARK, sizeOfNeighborhood);
        SparkStorage storage = (SparkStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();
        
        Map<Integer, Set<IndexedRatedRes>> expectedUserRatedItemsMap = new HashMap<>();
        // Get the indeces of users
        int indexOfBob = storage.getIndexOf("http://example.org/movies#Bob");
        int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");

        // Get the indeces of movies
        int indexOfElysium = storage.getIndexOf("http://example.org/movies#Elysium");
        int indexOfGravity = storage.getIndexOf("http://example.org/movies#Gravity");
        int indexOfDjango = storage.getIndexOf("http://example.org/movies#Django_Unchained");
        int indexOfManOfSteel = storage.getIndexOf("http://example.org/movies#Man_of_steel");

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

        // This is only testing if the resources are actually there but not the ratings.
        // This happens because of the equals method.
        Assert.assertEquals(storage.getUserRatedItemsMap().get(indexOfBob), bobResources);
        Assert.assertEquals(storage.getUserRatedItemsMap().get(indexOfAlice), aliceResources);

        // Verify that the ratings are the same.
        Set<IndexedRatedRes> bobRes = storage.getUserRatedItemsMap().get(indexOfBob);
        bobRes.stream().forEach((ratRes) -> {
            if (ratRes.equals(res2)) {
                Assert.assertTrue(ratRes.getRating()==res2.getRating());
            }
            else if (ratRes.equals(res4)) {
                Assert.assertTrue(ratRes.getRating()==res4.getRating());
            }
        });

        Set<IndexedRatedRes> aliceRes = storage.getUserRatedItemsMap().get(indexOfAlice);
        aliceRes.stream().forEach((ratRes) -> {
            if (ratRes.equals(res1)) {
                Assert.assertTrue(ratRes.getRating()==res1.getRating());
            } 
            else if (ratRes.equals(res3)) {
                Assert.assertTrue(ratRes.getRating()==res3.getRating());
            } 
            else if (ratRes.equals(res5)) {
                Assert.assertTrue(ratRes.getRating()==res5.getRating());
            } 
            else if (ratRes.equals(res6)) {
                Assert.assertTrue(ratRes.getRating()==res6.getRating());
            }
        }); 
        
        recRepository.releaseResources();
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
                    RecStorage.SPARK, sizeOfNeighborhood);
        CfSparkStorage storage = (CfSparkStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();

        // Get the indices of users
        int indexOfBob      = storage.getIndexOf("http://example.org/movies#Bob");
        int indexOfAlice    = storage.getIndexOf("http://example.org/movies#Alice");

        // Get the indices of movies
        int indexOfElysium      = storage.getIndexOf("http://example.org/movies#Elysium");
        int indexOfGravity      = storage.getIndexOf("http://example.org/movies#Gravity");
        int indexOfDjango       = storage.getIndexOf("http://example.org/movies#Django_Unchained");
        int indexOfManOfSteel   = storage.getIndexOf("http://example.org/movies#Man_of_steel");

        Map<Integer, InvertedList> actualInvertedLists 
                = storage.getResInvertedLists();

        System.out.println("SIZE OF INV LISTS " + actualInvertedLists.size());
        
        
        // Map should have one entry, one for each movie.             
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfElysium));
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfGravity));
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfManOfSteel));
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfDjango));

        // Test the content of each of the lists.
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
                } 
                else if (ur.getUserId() == indexOfAlice) {
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
                } 
                else if (ur.getUserId() == indexOfAlice && ur.getRating() == (9.5 )) {
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
        
        recRepository.releaseResources();
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
                    RecStorage.SPARK, sizeOfNeighborhood, numberOfTopRatings);
        CfSparkStorage storage = 
            (CfSparkStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();

        // Get the indeces of users
        int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
        int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
        int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
        int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
        int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");

        // Get the indeces of movies
        int indexOfItem1 = storage.getIndexOf("http://example.org/movies#Item1");
        int indexOfItem2 = storage.getIndexOf("http://example.org/movies#Item2");
        int indexOfItem3 = storage.getIndexOf("http://example.org/movies#Item3");
        int indexOfItem4 = storage.getIndexOf("http://example.org/movies#Item4");
        int indexOfItem5 = storage.getIndexOf("http://example.org/movies#Item5");                            

        Map<Integer, InvertedList> actualInvertedLists = storage.getResInvertedLists();

        // Map should have one entry, one for each movie.            
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem1));
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem2));
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem3));
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem4));
        Assert.assertTrue(actualInvertedLists.containsKey(indexOfItem5));

        // Test the content of each of the lists.
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
                } 
                else if (ur.getUserId() == indexOfUser1) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser2) {
                    Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser3 && ur.getRating() == 3.0 ) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser4 && ur.getRating() == 1.0 ) {
                    Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                }
            }
        }
        
        // Tests the number of ratings given to movie Item1
        Assert.assertEquals(new Integer(5), new Integer(item1InvList.getInnerArray().length));
        Assert.assertEquals(new Integer(5), numberOfCorrectRatings);

        numberOfCorrectRatings = 0;
        for (IndexedUserRating ur: item2InvList.getInnerArray()) {
            if (ur != null) {
                if (ur.getUserId() == indexOfAlice) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser1) {
                    Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser2) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser3) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser4) {
                    Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                }
            }
        }
        
        // Tests the number of ratings given to movie Item2
        Assert.assertEquals(new Integer(5), new Integer(item2InvList.getInnerArray().length));
        Assert.assertEquals(new Integer(5), numberOfCorrectRatings);

        numberOfCorrectRatings = 0;
        for (IndexedUserRating ur: item3InvList.getInnerArray()) {
            if (ur != null) {
                if (ur.getUserId() == indexOfAlice) {
                    Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser1) {
                    Assert.assertEquals(new Double(2.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser2) {
                    Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser3) {
                    Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser4) {
                    Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                }
            }
        }
        
        // Tests the number of ratings given to movie Item3
        Assert.assertEquals(new Integer(5), new Integer(item3InvList.getInnerArray().length));
        Assert.assertEquals(new Integer(5), numberOfCorrectRatings);

        numberOfCorrectRatings = 0;
        for (IndexedUserRating ur: item4InvList.getInnerArray()) {
            if (ur != null) {
                if (ur.getUserId() == indexOfAlice) {
                    Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser1) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser2) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser3) {
                    Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser4) {
                    Assert.assertEquals(new Double(2.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
            }
        }
        
        // Tests the number of ratings given to movie Item1
        Assert.assertEquals(new Integer(5), new Integer(item4InvList.getInnerArray().length));
        Assert.assertEquals(new Integer(5), numberOfCorrectRatings);

        numberOfCorrectRatings = 0;
        for (IndexedUserRating ur: item5InvList.getInnerArray()) {
            if (ur != null) {
                if (ur.getUserId() == indexOfUser1) {
                    Assert.assertEquals(new Double(3.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser2) {
                    Assert.assertEquals(new Double(5.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser3) {
                    Assert.assertEquals(new Double(4.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                } 
                else if (ur.getUserId() == indexOfUser4) {
                    Assert.assertEquals(new Double(1.0 ), new Double(ur.getRating()));
                    numberOfCorrectRatings++;
                }
            }
        }
        
        // Tests the number of ratings given to movie Item1
        Assert.assertEquals(new Integer(4), new Integer(item5InvList.getInnerArray().length));
        Assert.assertEquals(new Integer(4), numberOfCorrectRatings);         
        
        recRepository.releaseResources();
    }
        
    /**
     * Test l2norms.
     * @throws RecommenderException 
     */
    @Test
    public void testL2NormsSd() throws RecommenderException {
        
        int sizeOfNeighborhood = 1;
        SailRecommenderRepository recRepository = 
            TestRepositoryInstantiator.createRecSparqlMoviesUbCfRecAndPreprocess(
                RecStorage.SPARK, sizeOfNeighborhood);
        CfSparkStorage storage = (CfSparkStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();

        // Get the indeces of users
        int indexOfBob = storage.getIndexOf("http://example.org/movies#Bob");
        int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");                             

        double normalizationForBob = Math.sqrt(Math.pow(4.0, 2) + Math.pow(9.0, 2));
        double normalizationForAlice = Math.sqrt(Math.pow(3.0, 2) + Math.pow(5.0, 2) + Math.pow(9.5, 2) + Math.pow(8.0, 2));

        // Test l2norms
        Assert.assertEquals(normalizationForBob  , storage.getL2NormOfUser(indexOfBob)  , DELTA);
        Assert.assertEquals(normalizationForAlice, storage.getL2NormOfUser(indexOfAlice), DELTA);
        
        recRepository.releaseResources();
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
                    RecStorage.SPARK, sizeOfNeighborhood, numberOfTopRatings);            
        CfSparkStorage storage = (CfSparkStorage)((CfRecommender)recRepository.getRecommender()).getDataManager().getStorage();

        // Get the indeces of users
        int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
        int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
        int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
        int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
        int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");        

        double normalizationForAlice = Math.sqrt(Math.pow(5.0, 2) + Math.pow(3.0, 2) + Math.pow(4, 2) + Math.pow(4, 2));
        double normalizationForUser1 = Math.sqrt(Math.pow(3.0, 2) + Math.pow(1.0, 2) + Math.pow(2, 2) + Math.pow(3, 2) + Math.pow(3, 2));
        double normalizationForUser2 = Math.sqrt(Math.pow(4.0, 2) + Math.pow(3.0, 2) + Math.pow(4, 2) + Math.pow(3, 2) + Math.pow(5, 2));
        double normalizationForUser3 = Math.sqrt(Math.pow(3.0, 2) + Math.pow(3.0, 2) + Math.pow(1, 2) + Math.pow(5, 2) + Math.pow(4, 2));
        double normalizationForUser4 = Math.sqrt(Math.pow(1.0, 2) + Math.pow(5.0, 2) + Math.pow(5, 2) + Math.pow(2, 2) + Math.pow(1, 2)); 

        // Test l2norms               
        Assert.assertEquals(normalizationForAlice, storage.getL2NormOfUser(indexOfAlice), DELTA);
        Assert.assertEquals(normalizationForUser1, storage.getL2NormOfUser(indexOfUser1), DELTA);
        Assert.assertEquals(normalizationForUser2, storage.getL2NormOfUser(indexOfUser2), DELTA);
        Assert.assertEquals(normalizationForUser3, storage.getL2NormOfUser(indexOfUser3), DELTA);
        Assert.assertEquals(normalizationForUser4, storage.getL2NormOfUser(indexOfUser4), DELTA);
        
        recRepository.releaseResources();
    }
}
