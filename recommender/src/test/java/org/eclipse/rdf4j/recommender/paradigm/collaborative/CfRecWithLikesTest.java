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
 * Test class for CfRecommender with likes.
 * Note that this recommender is of type single domain and therefore no test
 * cases for the cross-domain scenario are to be expected.
 */
public class CfRecWithLikesTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-4;
        
        /**
         * Tests CF for the likes case
         * @throws RecommenderException 
         */
        @Test
        public void testPredictLikesBasedOnCfSd() throws RecommenderException {
                int neighborhoodSize = 2;
                int numberOfTopRatings = 0;  
                //It doesn't matter whether it is the inverted list or the scaled ones
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createLikesBasedUbCfRecAndPreprocess( 
                                RecStorage.INVERTED_LISTS, neighborhoodSize);
                /*
                CfInvListBasedStorage storage = (CfInvListBasedStorage)
                        (recRepository.getRecommender().getDataManager()).getStorage();
                */
                
                //SIM(Alice, User1) = 0.816496
                //SIM(Alice, User2) = 0.0
                //SIM(Alice, User3) = 0.5
                //SIM(User1, User2) = 0.577350                        
                //SIM(User1, User3) = 0.408248                        
                //SIM(User2, User3) = 0.0
               
                //Test for Alice 
                //Alice's neighborhoods (2 neighbors):
                //U1->0.8165
                //U3->0.5
                Assert.assertEquals(0.816496, recRepository.predictRating("http://example.org/graph#Alice", 
                                "http://example.org/graph#Item1"), DELTA);
                Assert.assertEquals(0.65825, recRepository.predictRating("http://example.org/graph#Alice", 
                                "http://example.org/graph#Item2"), DELTA);
                Assert.assertEquals(0.816496, recRepository.predictRating("http://example.org/graph#Alice", 
                                "http://example.org/graph#Item3"), DELTA);
                Assert.assertEquals(0.5, recRepository.predictRating("http://example.org/graph#Alice", 
                                "http://example.org/graph#Item4"), DELTA);
                
                //Test for User 1
                //User 1 has consumed all items
                //U1's neighborhoods (2 neighbors):
                //Alice->0.8165
                //U2->0.5774
                Assert.assertEquals(0.816496, recRepository.predictRating("http://example.org/graph#User1", 
                                "http://example.org/graph#Item1"), DELTA);
                Assert.assertEquals(0.816496, recRepository.predictRating("http://example.org/graph#User1", 
                                "http://example.org/graph#Item2"), DELTA);
                Assert.assertEquals(0.577350, recRepository.predictRating("http://example.org/graph#User1", 
                                "http://example.org/graph#Item3"), DELTA);
                Assert.assertEquals(0.0, recRepository.predictRating("http://example.org/graph#User1", 
                                "http://example.org/graph#Item4"), DELTA);
                
                //Test for User 2
                //U2's neighborhoods (2 neighbors):
                //U1->0.8165
                //all other users have 0
                Assert.assertEquals(0.577350, recRepository.predictRating("http://example.org/graph#User2", 
                                "http://example.org/graph#Item1"), DELTA);
                Assert.assertEquals(0.577350, recRepository.predictRating("http://example.org/graph#User2", 
                                "http://example.org/graph#Item2"), DELTA);
                Assert.assertEquals(0.577350, recRepository.predictRating("http://example.org/graph#User2", 
                                "http://example.org/graph#Item3"), DELTA); 
                Assert.assertEquals(0.0, recRepository.predictRating("http://example.org/graph#User2", 
                                "http://example.org/graph#Item4"), DELTA); 
                
                //Test for User 3
                //U3's neighborhoods (2 neighbors):
                //Alice->0.5
                //U1->0.4082
                Assert.assertEquals(0.454124, recRepository.predictRating("http://example.org/graph#User3", 
                                "http://example.org/graph#Item1"), DELTA);
                Assert.assertEquals(0.454124, recRepository.predictRating("http://example.org/graph#User3", 
                                "http://example.org/graph#Item2"), DELTA);
                Assert.assertEquals(0.408248, recRepository.predictRating("http://example.org/graph#User3", 
                                "http://example.org/graph#Item3"), DELTA);
                Assert.assertEquals(0.0, recRepository.predictRating("http://example.org/graph#User3", 
                                "http://example.org/graph#Item4"), DELTA);
        } 
        
        //TODO as for the counterpart with Ratings we need to add here a test for the top K.
        
        @Test
        public void testTopKRecommendationsBasedOnCfSd() throws RecommenderException {
                int neighborhoodSize = 2;
                int numberOfTopRatings = 0;  
            
                SailRecommenderRepository recRepository 
                        = TestRepositoryInstantiator.createLikesBasedUbCfRecAndPreprocess( 
                                RecStorage.INVERTED_LISTS, neighborhoodSize);
                                
                //TOP K for Alice
                //We rank the objects according to the predictions (we assume 
                //these are correct.
                RatedResource[] expectedTopK = new RatedResource[4];
                expectedTopK[0] = new RatedResource("http://example.org/graph#Item1", 0.816496);
                expectedTopK[1] = new RatedResource("http://example.org/graph#Item3", 0.816496);
                expectedTopK[2] = new RatedResource("http://example.org/graph#Item2", 0.65825);
                expectedTopK[3] = new RatedResource("http://example.org/graph#Item4", 0.5);                               
                
                //First we test the method that returns a top-5 list of recommendations
                //In the data set there are only five items.
                RatedResource[] actualtopK 
                        = recRepository.getTopRecommendations("http://example.org/graph#Alice", 4, true);
                
                for (int i = 0; i < actualtopK.length; i++) {
                        Assert.assertEquals(expectedTopK[i].getResource(), actualtopK[i].getResource());
                        Assert.assertEquals(expectedTopK[i].getRating(), actualtopK[i].getRating(), DELTA);
                }
                Assert.assertEquals(expectedTopK.length, actualtopK.length);
                
                //TOP K for User 1
                expectedTopK = new RatedResource[4];
                expectedTopK[0] = new RatedResource("http://example.org/graph#Item1", 0.816496);
                expectedTopK[1] = new RatedResource("http://example.org/graph#Item2", 0.816496);
                expectedTopK[2] = new RatedResource("http://example.org/graph#Item3", 0.577350);
                expectedTopK[3] = null; //new RatedResource("http://example.org/graph#Item4", 0.0); //Item4 is not a candidate
                
                actualtopK 
                        = recRepository.getTopRecommendations("http://example.org/graph#User1", 4, true);
                
                for (int i = 0; i < 3; i++) {
                        Assert.assertEquals(expectedTopK[i].getResource(), actualtopK[i].getResource());
                        Assert.assertEquals(expectedTopK[i].getRating(), actualtopK[i].getRating(), DELTA);
                }
                Assert.assertNull(actualtopK[3]);                
                Assert.assertEquals(expectedTopK.length, actualtopK.length);
                
                //TOP K for User 2
                //TOP K for User 3
        }
        
        @Test
        public void testConsistencyOfResults() throws RecommenderException {
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createNonDeterministicDataset();
                
                RatedResource[] actualTopKUser1 
                        = recRepository.getTopRecommendations("http://example.org/movies#User1", 3, true);
                
                RatedResource[] actualTopKUser2 
                        = recRepository.getTopRecommendations("http://example.org/movies#User2", 3, true);
                
                RatedResource[] actualTopKUser3 
                        = recRepository.getTopRecommendations("http://example.org/movies#User3", 3, true);
                
                RatedResource[] actualTopKUser4
                        = recRepository.getTopRecommendations("http://example.org/movies#User4", 3, true);
                
                RatedResource[] actualTopKUser5
                        = recRepository.getTopRecommendations("http://example.org/movies#User5", 3, true);
                
                RatedResource[] actualTopKUser6
                        = recRepository.getTopRecommendations("http://example.org/movies#User6", 3, true);
                
                RatedResource[] actualTopKUser7
                        = recRepository.getTopRecommendations("http://example.org/movies#User7", 3, true);
                
                RatedResource[] actualTopKUser8
                        = recRepository.getTopRecommendations("http://example.org/movies#User8", 3, true);
                
                RatedResource[] actualTopKUser9
                        = recRepository.getTopRecommendations("http://example.org/movies#User9", 3, true);
                
                RatedResource[] expectedTopKUser1 = new RatedResource[3];
                expectedTopKUser1[0] = new RatedResource("http://example.org/movies#Item1", 0.7302967433402214);
                expectedTopKUser1[1] = new RatedResource("http://example.org/movies#Item10", 0.6976830062380396);
                expectedTopKUser1[2] = new RatedResource("http://example.org/movies#Item5", 0.692293105624233);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser1[i], actualTopKUser1[i]);
                }
                
                RatedResource[] expectedTopKUser2 = new RatedResource[3];
                expectedTopKUser2[0] = new RatedResource("http://example.org/movies#Item6",0.8017837257372731);
                expectedTopKUser2[1] = new RatedResource("http://example.org/movies#Item7",0.8017837257372731);
                expectedTopKUser2[2] = new RatedResource("http://example.org/movies#Item4",0.7750850959871928);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser2[i], actualTopKUser2[i]);
                }
                
                RatedResource[] expectedTopKUser3 = new RatedResource[3];
                expectedTopKUser3[0] = new RatedResource("http://example.org/movies#Item2",0.7866502377738662);
                expectedTopKUser3[1] = new RatedResource("http://example.org/movies#Item5",0.781605741786064);
                expectedTopKUser3[2] = new RatedResource("http://example.org/movies#Item9",0.781605741786064);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser3[i], actualTopKUser3[i]);
                }
                
                RatedResource[] expectedTopKUser4 = new RatedResource[3];
                expectedTopKUser4[0] = new RatedResource("http://example.org/movies#Item4",0.8638019127549122);
                expectedTopKUser4[1] = new RatedResource("http://example.org/movies#Item7",0.8017837257372731);
                expectedTopKUser4[2] = new RatedResource("http://example.org/movies#Item3",0.8012424097642126);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser4[i], actualTopKUser4[i]);
                }
                
                RatedResource[] expectedTopKUser5 = new RatedResource[3];
                expectedTopKUser5[0] = new RatedResource("http://example.org/movies#Item6",0.8280584215563864);
                expectedTopKUser5[1] = new RatedResource("http://example.org/movies#Item2",0.8237539681297918);
                expectedTopKUser5[2] = new RatedResource("http://example.org/movies#Item8",0.7926015598666017);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser5[i], actualTopKUser5[i]);
                }
                
                RatedResource[] expectedTopKUser6 = new RatedResource[3];
                expectedTopKUser6[0] = new RatedResource("http://example.org/movies#Item1",0.7509067465753405);
                expectedTopKUser6[1] = new RatedResource("http://example.org/movies#Item4",0.7466022931487459);
                expectedTopKUser6[2] = new RatedResource("http://example.org/movies#Item7",0.7466022931487459);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser6[i], actualTopKUser6[i]);
                }
                
                RatedResource[] expectedTopKUser7 = new RatedResource[3];
                expectedTopKUser7[0] = new RatedResource("http://example.org/movies#Item1",0.7032100735615172);
                expectedTopKUser7[1] = new RatedResource("http://example.org/movies#Item10",0.7032100735615172);
                expectedTopKUser7[2] = new RatedResource("http://example.org/movies#Item2",0.7032100735615172);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser7[i], actualTopKUser7[i]);
                }
                
                RatedResource[] expectedTopKUser8 = new RatedResource[3];
                expectedTopKUser8[0] = new RatedResource("http://example.org/movies#Item4",0.7466022931487459);
                expectedTopKUser8[1] = new RatedResource("http://example.org/movies#Item7",0.7466022931487459);
                expectedTopKUser8[2] = new RatedResource("http://example.org/movies#Item3",0.7259922899136269);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser8[i], actualTopKUser8[i]);
                }
                
                RatedResource[] expectedTopKUser9 = new RatedResource[3];
                expectedTopKUser9[0] = new RatedResource("http://example.org/movies#Item7",0.6912194095333876);
                expectedTopKUser9[1] = new RatedResource("http://example.org/movies#Item2",0.6681531047810609);
                expectedTopKUser9[2] = new RatedResource("http://example.org/movies#Item10",0.6665507396383809);
                
                for (int i = 0; i < actualTopKUser1.length; i++) {
                        Assert.assertEquals(expectedTopKUser8[i], actualTopKUser8[i]);
                }                
        }
}        