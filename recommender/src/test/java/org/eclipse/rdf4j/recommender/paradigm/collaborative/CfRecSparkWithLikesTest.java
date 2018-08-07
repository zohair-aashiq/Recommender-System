/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.paradigm.collaborative;

import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for CfRecommender with likes using SparkDataManager.
 * Note that this recommender is of type single domain and therefore no test
 * cases for the cross-domain scenario are to be expected.
 */
public class CfRecSparkWithLikesTest {

    /**
     * Error tolerance.
     */
    private static final double DELTA = 1e-4;   

    /**
     * Tests CF for the likes case.
     * @throws RecommenderException 
     */
    @Test
    public void testPredictLikeBasedOnCfSd() throws RecommenderException {
        
        int neighborhoodSize = 2;
        int numberOfTopRatings = 0;  
        //It doesn't matter whether it is the inverted list or the scaled ones
        SailRecommenderRepository recRepository 
                = TestRepositoryInstantiator.createLikesBasedUbCfRecAndPreprocess(
                        RecStorage.SPARK, neighborhoodSize);

        //SIM(Alice, User1) = 0.816496
        //SIM(Alice, User2) = 0.0
        //SIM(Alice, User3) = 0.5
        //SIM(User1, User2) = 0.577350                        
        //SIM(User1, User3) = 0.408248                        
        //SIM(User2, User3) = 0.0               

        //Test for Alice                                                
        Assert.assertEquals(0.816496, recRepository.predictRating("http://example.org/graph#Alice", 
                        "http://example.org/graph#Item3"), DELTA);
        Assert.assertEquals(0.5, recRepository.predictRating("http://example.org/graph#Alice", 
                        "http://example.org/graph#Item4"), DELTA);
        //User 1 has consumed all items
        //Test for User 2
        Assert.assertEquals(0.577350, recRepository.predictRating("http://example.org/graph#User2", 
                        "http://example.org/graph#Item1"), DELTA);
        Assert.assertEquals(0.577350, recRepository.predictRating("http://example.org/graph#User2", 
                        "http://example.org/graph#Item2"), DELTA);                
        //Test for User 3
        Assert.assertEquals(0.454124, recRepository.predictRating("http://example.org/graph#User3", 
                        "http://example.org/graph#Item1"), DELTA);
        Assert.assertEquals(0.408248, recRepository.predictRating("http://example.org/graph#User3", 
                        "http://example.org/graph#Item3"), DELTA);
        
        recRepository.releaseResources();
    }   
}
