/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.paradigm.crossdomain;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for CdRecommender with likes.
 * Note that this recommender is of type cross domain and therefore no test
 * cases for the single-domain scenario are to be expected.
 */
public class CdRecWithLikesTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-3;                                
        
        /**
         * This method tests the method to predict the rating of an item for the
         * cross-domain approach based on K-Step_Markov Centrality.
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException
         */
        @Test
        public void testPredictRatingBasedOnKsmCd() throws RecommenderException {
                int numberOfSteps = 6;
                int maxIterations = 6;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createCustomizedDatasetKsMcCdRecAndPreprocess(numberOfSteps,maxIterations);
                
                //We have to test if the recommendation works according to
                //the computation of the K Step Markov Centrality.
                
                //We get the items that User1 has liked
                //We use nodes A as seed                
                
                //We can only ask for items marked as target items.                
                Assert.assertEquals(0.07936507936507936 / 0.07936507936507936, recRepository.predictRating(
                        "http://example.org/graph#User1", "http://example.org/graph#resD"), DELTA);
                Assert.assertEquals(0.06349206349206349 / 0.07936507936507936, recRepository.predictRating(
                        "http://example.org/graph#User1", "http://example.org/graph#resG"), DELTA);
                
                
                //This is wrong
                numberOfSteps = 6;
                maxIterations = 30;
                recRepository = 
                        TestRepositoryInstantiator.createCustomizedDatasetKsMcCdRecAndPreprocess(numberOfSteps,maxIterations);
                
                Assert.assertEquals(0.08345369342168607 / 0.08345369342168607, recRepository.predictRating(
                        "http://example.org/graph#User1", "http://example.org/graph#resD"), DELTA); 
                Assert.assertEquals(0.08337627587741897 / 0.08345369342168607, recRepository.predictRating(
                        "http://example.org/graph#User1", "http://example.org/graph#resG"), DELTA);                   
        }
        
        @Test
        public void testTopKRecommendationsBasedOnKsmCd() throws RecommenderException {
                int numberOfSteps = 6;
                int maxIterations = 30;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createCustomizedDatasetKsMcCdRecAndPreprocess(numberOfSteps,maxIterations);
                      
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
                RatedResource[] expectedTopK = new RatedResource[3];
                expectedTopK[0] = new RatedResource("http://example.org/graph#resD", 0.08337627587741897 / 0.08345369342168607);
                expectedTopK[1] = new RatedResource("http://example.org/graph#resG", 0.08337627587741897 / 0.08345369342168607);                            
                
                //First we test the method that returns a top-5 list of recommendations
                //In the data set there are only five items.
                RatedResource[] actualtopK 
                        = recRepository.getTopRecommendations("http://example.org/graph#User1", 3, true);
                
                    
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