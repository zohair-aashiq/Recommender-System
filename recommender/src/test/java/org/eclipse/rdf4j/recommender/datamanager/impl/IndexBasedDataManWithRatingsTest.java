/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager.impl;

import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.index.invlist.impl.CfInvListBasedStorage;
import org.eclipse.rdf4j.recommender.storage.index.invlist.impl.CfScaledInvListBasedStorage;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for IndexBasedDataManager with ratings.
 */
public class IndexBasedDataManWithRatingsTest {

        private static final double DELTA = 1e-4;                                   
        
        /**
         * Test if the neighborhoods are correctly computed using inverted lists
         * (dataset from the book).
         */
        @Test
        public void testNBInInvListsStorageSd() {
                int neighborhoodSize = 4;
                int numberOfTopRatings = 0;  
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess( 
                                RecStorage.INVERTED_LISTS, neighborhoodSize, numberOfTopRatings);
                
                CfInvListBasedStorage storage = (CfInvListBasedStorage)
                        (recRepository.getRecommender()).getDataManager().getStorage();
                
                //Expected similarity scores.
                //SIM(Alice, User1) = 0.82686886579
                //SIM(Alice, User2) = 0.810162722151
                //SIM(Alice, User3) = 0.762770071396
                //SIM(Alice, User4) = 0.789542033952
                //SIM(User1, User2) = 0.95938348259                        
                //SIM(User1, User3) = 0.935692702405                        
                //SIM(User1, User4) = 0.637815048203
                //SIM(User2, User3) = 0.894427191
                //SIM(User2, User4) = 0.77151674981
                //SIM(User3, User4) = 0.638310642392  
                                                              
                //Test neighborhood
                //COLLABORATIVE FILTERING
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
                int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
                int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
                int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
                int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");
                       
                //Testing the neighborhood of Alice
                //PriorityQueue<RatedResource> actualPqBob = ((CfInvListRecommender)recRepository.getRecModel()).getNeighborhood(indexOfBob);                        
                IndexedRatedRes[] actualPqAlice = 
                        storage.getNeighborhood(indexOfAlice);
                
                IndexedRatedRes expectedAliceNeighbor1 = new IndexedRatedRes(indexOfUser1, 0.8268688657895645);
                //RatedResource expectedAliceNeighbor1 = new IndexedRatedRes(indexOfUser2, 57);
                IndexedRatedRes actualAliceNeighbor1 = actualPqAlice[0];
                IndexedRatedRes expectedAliceNeighbor2 = new IndexedRatedRes(indexOfUser2, 0.8101627221513195);
                //RatedResource expectedAliceNeighbor2 = new IndexedRatedRes(indexOfUser3, 48);
                IndexedRatedRes actualAliceNeighbor2 = actualPqAlice[1];
                IndexedRatedRes expectedAliceNeighbor3 = new IndexedRatedRes(indexOfUser4, 0.7895420339517227);
                //RatedResource expectedAliceNeighbor3 = new IndexedRatedRes(indexOfUser4, 48);
                IndexedRatedRes actualAliceNeighbor3 = actualPqAlice[2];
                IndexedRatedRes expectedAliceNeighbor4 = new IndexedRatedRes(indexOfUser3, 0.7627700713964738);
                //RatedResource expectedAliceNeighbor4 = new IndexedRatedRes(indexOfUser1, 38);
                IndexedRatedRes actualAliceNeighbor4 = actualPqAlice[3];
                
                Assert.assertEquals(expectedAliceNeighbor1.getResourceId(), actualAliceNeighbor1.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor1.getRating(), actualAliceNeighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedAliceNeighbor2.getResourceId(), actualAliceNeighbor2.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor2.getRating(), actualAliceNeighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedAliceNeighbor3.getResourceId(), actualAliceNeighbor3.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor3.getRating(), actualAliceNeighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedAliceNeighbor4.getResourceId(), actualAliceNeighbor4.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor4.getRating(), actualAliceNeighbor4.getRating(), DELTA);
                
                
                //Testing the neighborhood of User1    
                IndexedRatedRes[] actualPqUser1 = 
                        storage.getNeighborhood(indexOfUser1);               
                IndexedRatedRes expectedUser1Neighbor1 = new IndexedRatedRes(indexOfUser2, 0.9593834825900779);
                //RatedResource expectedUser1Neighbor1 = new IndexedRatedRes(indexOfUser2, 47);
                IndexedRatedRes actualUser1Neighbor1 = actualPqUser1[0];
                IndexedRatedRes expectedUser1Neighbor2 = new IndexedRatedRes(indexOfUser3, 0.9356927024046586);
                //RatedResource expectedUser1Neighbor2 = new IndexedRatedRes(indexOfUser3, 41);
                IndexedRatedRes actualUser1Neighbor2 = actualPqUser1[1];
                IndexedRatedRes expectedUser1Neighbor3 = new IndexedRatedRes(indexOfAlice, 0.8268688657895645);
                //RatedResource expectedUser1Neighbor3 = new IndexedRatedRes(indexOfAlice, 38);
                IndexedRatedRes actualUser1Neighbor3 = actualPqUser1[2];
                IndexedRatedRes expectedUser1Neighbor4 = new IndexedRatedRes(indexOfUser4, 0.6378150482030709);
                //RatedResource expectedUser1Neighbor4 = new IndexedRatedRes(indexOfUser4, 27);
                IndexedRatedRes actualUser1Neighbor4 = actualPqUser1[3];
                
                Assert.assertEquals(expectedUser1Neighbor1.getResourceId(), actualUser1Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor1.getRating(), actualUser1Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser1Neighbor2.getResourceId(), actualUser1Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor2.getRating(), actualUser1Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser1Neighbor3.getResourceId(), actualUser1Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor3.getRating(), actualUser1Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser1Neighbor4.getResourceId(), actualUser1Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor4.getRating(), actualUser1Neighbor4.getRating(), DELTA);
                
                
                //Testing the neighborhood of User2
                IndexedRatedRes[] actualPqUser2 = 
                        storage.getNeighborhood(indexOfUser2);
                IndexedRatedRes expectedUser2Neighbor1 = new IndexedRatedRes(indexOfUser1, 0.9593834825900779);
                IndexedRatedRes actualUser2Neighbor1 = actualPqUser2[0];
                IndexedRatedRes expectedUser2Neighbor2 = new IndexedRatedRes(indexOfUser3, 0.8944271909999157);
                IndexedRatedRes actualUser2Neighbor2 = actualPqUser2[1];
                IndexedRatedRes expectedUser2Neighbor3 = new IndexedRatedRes(indexOfAlice, 0.8101627221513195);
                IndexedRatedRes actualUser2Neighbor3 = actualPqUser2[2];
                IndexedRatedRes expectedUser2Neighbor4 = new IndexedRatedRes(indexOfUser4, 0.7715167498104594);
                IndexedRatedRes actualUser2Neighbor4 = actualPqUser2[3];

                Assert.assertEquals(expectedUser2Neighbor1.getResourceId(), actualUser2Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor1.getRating(), actualUser2Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser2Neighbor2.getResourceId(), actualUser2Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor2.getRating(), actualUser2Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser2Neighbor3.getResourceId(), actualUser2Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor3.getRating(), actualUser2Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser2Neighbor4.getResourceId(), actualUser2Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor4.getRating(), actualUser2Neighbor4.getRating(), DELTA);
                
                IndexedRatedRes[] actualPqUser3 = 
                        storage.getNeighborhood(indexOfUser3);
                
                //Testing the neighborhood of User3
                IndexedRatedRes expectedUser3Neighbor1 = new IndexedRatedRes(indexOfUser1, 0.9356927024046586); 
                IndexedRatedRes actualUser3Neighbor1 = actualPqUser3[0];
                IndexedRatedRes expectedUser3Neighbor2 = new IndexedRatedRes(indexOfUser2, 0.8944271909999157);
                IndexedRatedRes actualUser3Neighbor2 = actualPqUser3[1];
                IndexedRatedRes expectedUser3Neighbor3 = new IndexedRatedRes(indexOfAlice, 0.7627700713964738);
                IndexedRatedRes actualUser3Neighbor3 = actualPqUser3[2];
                IndexedRatedRes expectedUser3Neighbor4 = new IndexedRatedRes(indexOfUser4, 0.6383106423916777);
                IndexedRatedRes actualUser3Neighbor4 = actualPqUser3[3];

                Assert.assertEquals(expectedUser3Neighbor1.getResourceId(), actualUser3Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor1.getRating(), actualUser3Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser3Neighbor2.getResourceId(), actualUser3Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor2.getRating(), actualUser3Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser3Neighbor3.getResourceId(), actualUser3Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor3.getRating(), actualUser3Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser3Neighbor4.getResourceId(), actualUser3Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor4.getRating(), actualUser3Neighbor4.getRating(), DELTA);
                
                IndexedRatedRes[] actualPqUser4 = 
                        storage.getNeighborhood(indexOfUser4);
                
                //Testing the neighborhood of User4
                IndexedRatedRes expectedUser4Neighbor1 = new IndexedRatedRes(indexOfAlice, 0.7895420339517227); 
                IndexedRatedRes actualUser4Neighbor1 = actualPqUser4[0];
                IndexedRatedRes expectedUser4Neighbor2 = new IndexedRatedRes(indexOfUser2, 0.7715167498104594);
                IndexedRatedRes actualUser4Neighbor2 = actualPqUser4[1];
                IndexedRatedRes expectedUser4Neighbor3 = new IndexedRatedRes(indexOfUser3, 0.6383106423916777);
                IndexedRatedRes actualUser4Neighbor3 = actualPqUser4[2];
                IndexedRatedRes expectedUser4Neighbor4 = new IndexedRatedRes(indexOfUser1, 0.6378150482030709);
                IndexedRatedRes actualUser4Neighbor4 = actualPqUser4[3];

                Assert.assertEquals(expectedUser4Neighbor1.getResourceId(), actualUser4Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor1.getRating(), actualUser4Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser4Neighbor2.getResourceId(), actualUser4Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor2.getRating(), actualUser4Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser4Neighbor3.getResourceId(), actualUser4Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor3.getRating(), actualUser4Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser4Neighbor4.getResourceId(), actualUser4Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor4.getRating(), actualUser4Neighbor4.getRating(), DELTA);
        }
        
        /**
         * Test if the neighborhoods are correctly computed using inverted lists
         * (dataset from RecSPARQL).
         */
        @Test
        public void testNBInInvListsStorageSd2() {
                int neighborhoodSize = 1;
                                
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createRecSparqlMoviesUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, neighborhoodSize);
                CfInvListBasedStorage storage = (CfInvListBasedStorage)
                        (recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //Test neighborhood
                //COLLABORATIVE FILTERING
                int indexOfBob = storage.getIndexOf("http://example.org/movies#Bob");
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");

                Map<Integer, IndexedRatedRes[]> actualUserNeighborhood 
                        = storage.getNeighborhoods();
                
                Assert.assertTrue(actualUserNeighborhood.size()== storage.getUserRatedItemsMap().size());

                IndexedRatedRes[] actualPqBob = storage.getNeighborhood(indexOfBob);                        
                IndexedRatedRes[] actualPqAlice = storage.getNeighborhood(indexOfAlice);

                Assert.assertTrue(actualPqBob.length == 1);
                Assert.assertTrue(actualPqAlice.length == 1);

                IndexedRatedRes actualBobNeighbor = actualPqBob[0];
                IndexedRatedRes expectedBobNeighbor = new IndexedRatedRes(indexOfAlice, 0.7807268223307298);

                IndexedRatedRes actualAliceNeighbor = actualPqAlice[0];
                IndexedRatedRes expectedAliceNeighbor = new IndexedRatedRes(indexOfBob, 0.7807268223307298);

                Assert.assertEquals(expectedBobNeighbor.getResourceId(), actualBobNeighbor.getResourceId());
                Assert.assertEquals(expectedBobNeighbor.getRating(), actualBobNeighbor.getRating(), DELTA);
                Assert.assertEquals(expectedAliceNeighbor.getResourceId(), actualAliceNeighbor.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor.getRating(), actualAliceNeighbor.getRating(), DELTA);
        }
        
        /**
         * Test if the neighborhoods are correctly stored.
         * Dataset: Book
         */
        @Test
        public void testNBInScaledInvListsStorageSd() {
                int neighborhoodSize = 4;
                int numberOfTopRatings = 0;                
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess( 
                                RecStorage.SCALED_INVERTED_LISTS, neighborhoodSize, numberOfTopRatings);
                CfScaledInvListBasedStorage storage = (CfScaledInvListBasedStorage)
                        (recRepository.getRecommender().getDataManager()).getStorage();
                
                //SIM(Alice, User1) = 0.82686886579
                //SIM(Alice, User2) = 0.810162722151
                //SIM(Alice, User3) = 0.762770071396
                //SIM(Alice, User4) = 0.789542033952
                //SIM(User1, User2) = 0.95938348259                        
                //SIM(User1, User3) = 0.935692702405                        
                //SIM(User1, User4) = 0.637815048203
                //SIM(User2, User3) = 0.894427191
                //SIM(User2, User4) = 0.77151674981
                //SIM(User3, User4) = 0.638310642392
                                                              
                //Test neighborhood
                //COLLABORATIVE FILTERING
                int indexOfAlice = storage.getIndexOf("http://example.org/movies#Alice");
                int indexOfUser1 = storage.getIndexOf("http://example.org/movies#User1");
                int indexOfUser2 = storage.getIndexOf("http://example.org/movies#User2");
                int indexOfUser3 = storage.getIndexOf("http://example.org/movies#User3");
                int indexOfUser4 = storage.getIndexOf("http://example.org/movies#User4");
                                      
                IndexedRatedRes[] actualPqAlice = 
                        storage.getNeighborhood(indexOfAlice);
                
                IndexedRatedRes expectedAliceNeighbor1 = new IndexedRatedRes(indexOfUser1, 0.8268688657895645);
                //RatedResource expectedAliceNeighbor1 = new IndexedRatedRes(indexOfUser2, 57);
                IndexedRatedRes actualAliceNeighbor1 = actualPqAlice[0];
                IndexedRatedRes expectedAliceNeighbor2 = new IndexedRatedRes(indexOfUser2, 0.8101627221513195);
                //RatedResource expectedAliceNeighbor2 = new IndexedRatedRes(indexOfUser3, 48);
                IndexedRatedRes actualAliceNeighbor2 = actualPqAlice[1];
                IndexedRatedRes expectedAliceNeighbor3 = new IndexedRatedRes(indexOfUser4, 0.7895420339517227);
                //RatedResource expectedAliceNeighbor3 = new IndexedRatedRes(indexOfUser4, 48);
                IndexedRatedRes actualAliceNeighbor3 = actualPqAlice[2];
                IndexedRatedRes expectedAliceNeighbor4 = new IndexedRatedRes(indexOfUser3, 0.7627700713964738);
                //RatedResource expectedAliceNeighbor4 = new IndexedRatedRes(indexOfUser1, 38);
                IndexedRatedRes actualAliceNeighbor4 = actualPqAlice[3];
                
                Assert.assertEquals(expectedAliceNeighbor1.getResourceId(), actualAliceNeighbor1.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor1.getRating(), actualAliceNeighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedAliceNeighbor2.getResourceId(), actualAliceNeighbor2.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor2.getRating(), actualAliceNeighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedAliceNeighbor3.getResourceId(), actualAliceNeighbor3.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor3.getRating(), actualAliceNeighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedAliceNeighbor4.getResourceId(), actualAliceNeighbor4.getResourceId());
                Assert.assertEquals(expectedAliceNeighbor4.getRating(), actualAliceNeighbor4.getRating(), DELTA);
                                
                //Testing the neighborhood of User1    
                IndexedRatedRes[] actualPqUser1 = 
                        storage.getNeighborhood(indexOfUser1);               
                IndexedRatedRes expectedUser1Neighbor1 = new IndexedRatedRes(indexOfUser2, 0.9593834825900779);
                //RatedResource expectedUser1Neighbor1 = new IndexedRatedRes(indexOfUser2, 47);
                IndexedRatedRes actualUser1Neighbor1 = actualPqUser1[0];
                IndexedRatedRes expectedUser1Neighbor2 = new IndexedRatedRes(indexOfUser3, 0.9356927024046586);
                //RatedResource expectedUser1Neighbor2 = new IndexedRatedRes(indexOfUser3, 41);
                IndexedRatedRes actualUser1Neighbor2 = actualPqUser1[1];
                IndexedRatedRes expectedUser1Neighbor3 = new IndexedRatedRes(indexOfAlice, 0.8268688657895645);
                //RatedResource expectedUser1Neighbor3 = new IndexedRatedRes(indexOfAlice, 38);
                IndexedRatedRes actualUser1Neighbor3 = actualPqUser1[2];
                IndexedRatedRes expectedUser1Neighbor4 = new IndexedRatedRes(indexOfUser4, 0.6378150482030709);
                //RatedResource expectedUser1Neighbor4 = new IndexedRatedRes(indexOfUser4, 27);
                IndexedRatedRes actualUser1Neighbor4 = actualPqUser1[3];
                
                Assert.assertEquals(expectedUser1Neighbor1.getResourceId(), actualUser1Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor1.getRating(), actualUser1Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser1Neighbor2.getResourceId(), actualUser1Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor2.getRating(), actualUser1Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser1Neighbor3.getResourceId(), actualUser1Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor3.getRating(), actualUser1Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser1Neighbor4.getResourceId(), actualUser1Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser1Neighbor4.getRating(), actualUser1Neighbor4.getRating(), DELTA);
                                
                //Testing the neighborhood of User2
                IndexedRatedRes[] actualPqUser2 = 
                        storage.getNeighborhood(indexOfUser2);
                IndexedRatedRes expectedUser2Neighbor1 = new IndexedRatedRes(indexOfUser1, 0.9593834825900779);
                IndexedRatedRes actualUser2Neighbor1 = actualPqUser2[0];
                IndexedRatedRes expectedUser2Neighbor2 = new IndexedRatedRes(indexOfUser3, 0.8944271909999157);
                IndexedRatedRes actualUser2Neighbor2 = actualPqUser2[1];
                IndexedRatedRes expectedUser2Neighbor3 = new IndexedRatedRes(indexOfAlice, 0.8101627221513195);
                IndexedRatedRes actualUser2Neighbor3 = actualPqUser2[2];
                IndexedRatedRes expectedUser2Neighbor4 = new IndexedRatedRes(indexOfUser4, 0.7715167498104594);
                IndexedRatedRes actualUser2Neighbor4 = actualPqUser2[3];

                Assert.assertEquals(expectedUser2Neighbor1.getResourceId(), actualUser2Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor1.getRating(), actualUser2Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser2Neighbor2.getResourceId(), actualUser2Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor2.getRating(), actualUser2Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser2Neighbor3.getResourceId(), actualUser2Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor3.getRating(), actualUser2Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser2Neighbor4.getResourceId(), actualUser2Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser2Neighbor4.getRating(), actualUser2Neighbor4.getRating(), DELTA);
                
                IndexedRatedRes[] actualPqUser3 = 
                        storage.getNeighborhood(indexOfUser3);
                
                //Testing the neighborhood of User3
                IndexedRatedRes expectedUser3Neighbor1 = new IndexedRatedRes(indexOfUser1, 0.9356927024046586); 
                IndexedRatedRes actualUser3Neighbor1 = actualPqUser3[0];
                IndexedRatedRes expectedUser3Neighbor2 = new IndexedRatedRes(indexOfUser2, 0.8944271909999157);
                IndexedRatedRes actualUser3Neighbor2 = actualPqUser3[1];
                IndexedRatedRes expectedUser3Neighbor3 = new IndexedRatedRes(indexOfAlice, 0.7627700713964738);
                IndexedRatedRes actualUser3Neighbor3 = actualPqUser3[2];
                IndexedRatedRes expectedUser3Neighbor4 = new IndexedRatedRes(indexOfUser4, 0.6383106423916777);
                IndexedRatedRes actualUser3Neighbor4 = actualPqUser3[3];

                Assert.assertEquals(expectedUser3Neighbor1.getResourceId(), actualUser3Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor1.getRating(), actualUser3Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser3Neighbor2.getResourceId(), actualUser3Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor2.getRating(), actualUser3Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser3Neighbor3.getResourceId(), actualUser3Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor3.getRating(), actualUser3Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser3Neighbor4.getResourceId(), actualUser3Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser3Neighbor4.getRating(), actualUser3Neighbor4.getRating(), DELTA);
                
                IndexedRatedRes[] actualPqUser4 = 
                        storage.getNeighborhood(indexOfUser4);
                
                //Testing the neighborhood of User4
                IndexedRatedRes expectedUser4Neighbor1 = new IndexedRatedRes(indexOfAlice, 0.7895420339517227); 
                IndexedRatedRes actualUser4Neighbor1 = actualPqUser4[0];
                IndexedRatedRes expectedUser4Neighbor2 = new IndexedRatedRes(indexOfUser2, 0.7715167498104594);
                IndexedRatedRes actualUser4Neighbor2 = actualPqUser4[1];
                IndexedRatedRes expectedUser4Neighbor3 = new IndexedRatedRes(indexOfUser3, 0.6383106423916777);
                IndexedRatedRes actualUser4Neighbor3 = actualPqUser4[2];
                IndexedRatedRes expectedUser4Neighbor4 = new IndexedRatedRes(indexOfUser1, 0.6378150482030709);
                IndexedRatedRes actualUser4Neighbor4 = actualPqUser4[3];

                Assert.assertEquals(expectedUser4Neighbor1.getResourceId(), actualUser4Neighbor1.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor1.getRating(), actualUser4Neighbor1.getRating(), DELTA);
                Assert.assertEquals(expectedUser4Neighbor2.getResourceId(), actualUser4Neighbor2.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor2.getRating(), actualUser4Neighbor2.getRating(), DELTA);
                Assert.assertEquals(expectedUser4Neighbor3.getResourceId(), actualUser4Neighbor3.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor3.getRating(), actualUser4Neighbor3.getRating(), DELTA);
                Assert.assertEquals(expectedUser4Neighbor4.getResourceId(), actualUser4Neighbor4.getResourceId());
                Assert.assertEquals(expectedUser4Neighbor4.getRating(), actualUser4Neighbor4.getRating(), DELTA);
        }
        
        /**
         * Tests the method getRatingAverageOfUser.
         */
        @Test
        public void testGetRatingAverageOfUserSd() throws RecommenderException {
                int neighborhoodSize = 4;
                int numberOfTopRatings = 0;
                //It doesn't matter whether it is the inverted list or the scaled ones
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, neighborhoodSize, numberOfTopRatings);
                DataManager dm = recRepository.getRecommender().getDataManager();
                
                double expAvgOfAlice = (5.0 + 3.0 + 4.0 + 4.0) / 4;
                double expAvgOfUser1 = (3.0 + 1.0 + 2.0 + 3.0 + 3.0) / 5;
                double expAvgOfUser2 = (4.0 + 3.0 + 4.0 + 3.0 + 5.0) / 5;
                //TODO complete for other users.
                
                double actualAvgOfAlice = dm.getRatingAverageOfUser("http://example.org/movies#Alice");
                double actualAvgOfUser1 = dm.getRatingAverageOfUser("http://example.org/movies#User1");
                double actualAvgOfUser2 = dm.getRatingAverageOfUser("http://example.org/movies#User2");
                
                Assert.assertEquals(expAvgOfAlice, actualAvgOfAlice, DELTA);
                Assert.assertEquals(expAvgOfUser1, actualAvgOfUser1, DELTA);
                Assert.assertEquals(expAvgOfUser2, actualAvgOfUser2, DELTA);
        }                                
}        