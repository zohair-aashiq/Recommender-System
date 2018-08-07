/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.paradigm.collaborative;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for CfRecommender with ratings.
 * Note that this recommender is of type single domain and therefore no test
 * cases for the cross-domain scenario are to be expected.
 */
public class CfRecWithRatingsTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-4;             
        
        /**
         * This method tests the method to predict the rating of an item for the
         * collaborative filtering approach.
         */
        @Test
        public void testPredictRatingBasedOnCfSd() throws RecommenderException {
                int neighborhoodSize = 2;
                int numberOfTopRatings = 0;  
                //It doesn't matter whether it is the inverted list or the scaled ones
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess( 
                                RecStorage.INVERTED_LISTS, neighborhoodSize, numberOfTopRatings);
                /*
                CfInvListBasedStorage storage = (CfInvListBasedStorage)
                        (recRepository.getRecommender().getDataManager()).getStorage();
                */
                
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
                
                /*
                //Predicted for Alice
                I1 -> Alice: 4.402041028867288
                I2 -> Alice: 2.896938456699069
                I3 -> Alice: 3.896938456699069
                I4 -> Alice: 3.9071436010355067
                I5 -> Alice: 4.896938456699068
                */
                
                double avgOfAlice = (5.0 + 3.0 + 4.0 + 4.0) / 4;
                double avgOfUser1 = (3.0 + 1.0 + 2.0 + 3.0 + 3.0) / 5;
                double avgOfUser2 = (4.0 + 3.0 + 4.0 + 3.0 + 5.0) / 5;
                double avgOfUser3 = (3.0 + 3.0 + 1.0 + 5.0 + 4.0) / 5;
                double avgOfUser4 = (1.0 + 5.0 + 5.0 + 2.0 + 1.0) / 5;
               
                //This is what I need to test
                //Alice's Neighborhod: User1 and User2
                //The prediction for user Alice of the movie Item5
                //4.896939336550798
                Double expectedPredictedRating = new Double(
                        avgOfAlice + 
                        ((0.8268 * (3.0 - avgOfUser1) ) + 
                        (0.8101 * (5.0 - avgOfUser2))) / (0.8268 + 0.8101)); 
                                
                Double actualPredictedRating 
                        = recRepository.predictRating("http://example.org/movies#Alice", 
                                "http://example.org/movies#Item5");
                
                Assert.assertEquals(expectedPredictedRating, actualPredictedRating, DELTA);                
                
                //We repeat the process for Alice and Item1
                expectedPredictedRating = new Double(
                        avgOfAlice + 
                        ((0.8268 * (3.0 - avgOfUser1) ) + 
                        (0.8101 * (4.0 - avgOfUser2))) / (0.8268 + 0.8101)); 
                
                actualPredictedRating 
                        = recRepository.predictRating("http://example.org/movies#Alice", 
                                "http://example.org/movies#Item1");
                
                Assert.assertEquals(expectedPredictedRating, actualPredictedRating, DELTA);
                
                //We repeat the process for Alice and Item2
                expectedPredictedRating = new Double(
                        avgOfAlice + 
                        ((0.8268 * (1.0 - avgOfUser1) ) + 
                        (0.8101 * (3.0 - avgOfUser2))) / (0.8268 + 0.8101)); 
                
                actualPredictedRating 
                        = recRepository.predictRating("http://example.org/movies#Alice", 
                                "http://example.org/movies#Item2");
                
                Assert.assertEquals(expectedPredictedRating, actualPredictedRating, DELTA);
                
                //We repeat the process for Alice and Item3
                expectedPredictedRating = new Double(
                        avgOfAlice + 
                        ((0.8268 * (2.0 - avgOfUser1) ) + 
                        (0.8101 * (4.0 - avgOfUser2))) / (0.8268 + 0.8101)); 
                
                actualPredictedRating 
                        = recRepository.predictRating("http://example.org/movies#Alice", 
                                "http://example.org/movies#Item3");
                
                Assert.assertEquals(expectedPredictedRating, actualPredictedRating, DELTA);
                
                //We repeat the process for Alice and Item4
                expectedPredictedRating = new Double(
                        avgOfAlice + 
                        ((0.8268 * (3.0 - avgOfUser1) ) + 
                        (0.8101 * (3.0 - avgOfUser2))) / (0.8268 + 0.8101)); 
                
                actualPredictedRating 
                        = recRepository.predictRating("http://example.org/movies#Alice", 
                                "http://example.org/movies#Item4");
                
                Assert.assertEquals(expectedPredictedRating, actualPredictedRating, DELTA);
                
                //Two more predictions from other users (just in case):
                //Prediction for User 2 and Item2
                expectedPredictedRating = new Double(
                        avgOfUser2 + 
                        ((0.9593 * (1.0 - avgOfUser1) ) + 
                        (0.8944 * (3.0 - avgOfUser3))) / (0.9593 + 0.8944));
                
                actualPredictedRating 
                        = recRepository.predictRating("http://example.org/movies#User2", 
                                "http://example.org/movies#Item2");
                
                Assert.assertEquals(expectedPredictedRating, actualPredictedRating, DELTA);
                
                //Prediction for User 4 and Item5
                //Interesting because Alice is in the neighborhood but she hasn't
                //rated Item5
                expectedPredictedRating = new Double(
                        avgOfUser4 + 
                        //((0.7895 * (? - avgOfAlice) ) + 
                        (0.7715 * (5.0 - avgOfUser2)) / (0.7715));
                
                actualPredictedRating 
                        = recRepository.predictRating("http://example.org/movies#User4", 
                                "http://example.org/movies#Item5");
                
                Assert.assertEquals(expectedPredictedRating, actualPredictedRating, DELTA);                             
        }                
        
        @Test
        public void testTopKRecommendationsBasedOnCfSd() throws RecommenderException {
                int neighborhoodSize = 2;
                int numberOfTopRatings = 0;  
            
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createBookUbCfRecAndPreprocess(
                                RecStorage.INVERTED_LISTS, neighborhoodSize, numberOfTopRatings);
                /*
                CfInvListBasedStorage storage = (CfInvListBasedStorage)
                        (recRepository.getRecommender().getDataManager()).getStorage();
                */
                
                /*
                //Predicted
                I1 -> Alice: 4.402041028867288
                I2 -> Alice: 2.896938456699069
                I3 -> Alice: 3.896938456699069
                I4 -> Alice: 3.9071436010355067
                I5 -> Alice: 4.896938456699068
                */
                
                //We rank the objects according to the predictions (we assume 
                //these are correct.
                RatedResource[] expectedTopK = new RatedResource[5];
                expectedTopK[0] = new RatedResource("http://example.org/movies#Item5", 4.896938456699068);
                expectedTopK[1] = new RatedResource("http://example.org/movies#Item1", 4.402041028867288);
                expectedTopK[2] = new RatedResource("http://example.org/movies#Item4", 3.9071436010355067);
                expectedTopK[3] = new RatedResource("http://example.org/movies#Item3", 3.896938456699069);
                expectedTopK[4] = new RatedResource("http://example.org/movies#Item2", 2.896938456699069);                               
                
                //First we test the method that returns a top-5 list of recommendations
                //In the data set there are only five items.
                RatedResource[] actualtopK 
                        = recRepository.getTopRecommendations("http://example.org/movies#Alice", 5, true);
                
                for (int i = 0; i < actualtopK.length; i++) {
                        Assert.assertEquals(expectedTopK[i].getResource(), actualtopK[i].getResource());
                        Assert.assertEquals(expectedTopK[i].getRating(), actualtopK[i].getRating(), DELTA);
                }
                Assert.assertEquals(expectedTopK.length, actualtopK.length);
                
                //Now we test what happens if we have more items in the top-k than those we can actually recommend.              
                expectedTopK = new RatedResource[7];
                expectedTopK[0] = new RatedResource("http://example.org/movies#Item5", 4.896938456699068);
                expectedTopK[1] = new RatedResource("http://example.org/movies#Item1", 4.402041028867288);
                expectedTopK[2] = new RatedResource("http://example.org/movies#Item4", 3.9071436010355067);
                expectedTopK[3] = new RatedResource("http://example.org/movies#Item3", 3.896938456699069);
                expectedTopK[4] = new RatedResource("http://example.org/movies#Item2", 2.896938456699069);  
                expectedTopK[5] = null;
                expectedTopK[6] = null;
                
                actualtopK 
                        = recRepository.getTopRecommendations("http://example.org/movies#Alice", 7, true);
                
                for (int i = 0; i < actualtopK.length; i++) {
                        if (actualtopK[i] != null) {
                                Assert.assertEquals(expectedTopK[i].getResource(), 
                                        actualtopK[i].getResource());
                                Assert.assertEquals(expectedTopK[i].getRating(), 
                                        actualtopK[i].getRating(), DELTA);                                                
                        } else {
                                Assert.assertNull(expectedTopK[i]);
                                Assert.assertNull(actualtopK[i]);
                        }                        
                }                
                Assert.assertEquals(expectedTopK.length, actualtopK.length);

                
                //Now we test what happens with a top-k where not all elements
                //fit in the list
                expectedTopK = new RatedResource[3];
                expectedTopK[0] = new RatedResource("http://example.org/movies#Item5", 4.896938456699068);
                expectedTopK[1] = new RatedResource("http://example.org/movies#Item1", 4.402041028867288);
                expectedTopK[2] = new RatedResource("http://example.org/movies#Item4", 3.9071436010355067);
                
                actualtopK 
                        = recRepository.getTopRecommendations("http://example.org/movies#Alice", 3, true);
                
                for (int i = 0; i < actualtopK.length; i++) {
                        Assert.assertEquals(expectedTopK[i].getResource(), actualtopK[i].getResource());
                        Assert.assertEquals(expectedTopK[i].getRating(), actualtopK[i].getRating(), DELTA);
                }
                Assert.assertEquals(expectedTopK.length, actualtopK.length);
                
                //Now we test the same method where the flag includedConsumedItems is set to false
                //In this case we only have one item we can recommend to alice: item5
                expectedTopK = new RatedResource[5];
                expectedTopK[0] = new RatedResource("http://example.org/movies#Item5", 4.896938456699068);
                expectedTopK[1] = null;
                expectedTopK[2] = null;
                expectedTopK[3] = null;
                expectedTopK[4] = null;
                
                actualtopK 
                        = recRepository.getTopRecommendations("http://example.org/movies#Alice", 5, false);
                
                for (int i = 0; i < actualtopK.length; i++) {
                        if (actualtopK[i] != null) {
                                Assert.assertEquals(expectedTopK[i].getResource(), 
                                        actualtopK[i].getResource());
                                Assert.assertEquals(expectedTopK[i].getRating(), 
                                        actualtopK[i].getRating(), DELTA);                                                
                        } else {
                                Assert.assertNull(expectedTopK[i]);
                                Assert.assertNull(actualtopK[i]);
                        }                        
                }
                Assert.assertEquals(expectedTopK.length, actualtopK.length);
        }
}        