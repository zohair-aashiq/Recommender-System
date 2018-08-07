/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager.impl;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for GraphBasedDataManager with likes.
 */
public class GraphBasedDataManWithLikesTest {
        //TODO add test cases for single-domain.
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-5;
                                     
        /**
         * Tests if the method returns the right set of candidates when 
         * pre-processing is being done in the system and Reword is used as 
         * algorithm.
         */
        @Test
        public void testGetRecCandidatesUsingRwCd() throws RecommenderException {
                boolean preprocessing  = false;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess2(preprocessing);
                
                GraphBasedDataManager gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();
                
                Set<String> expectedCandidates = new HashSet<String>();
                expectedCandidates.add("http://example.org/graph#O");
                expectedCandidates.add("http://example.org/graph#P");
                expectedCandidates.add("http://example.org/graph#Q");
                expectedCandidates.add("http://example.org/graph#R");
                expectedCandidates.add("http://example.org/graph#S");
                expectedCandidates.add("http://example.org/graph#T");
                expectedCandidates.add("http://example.org/graph#U");
                
                Set<String> actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");
                
                Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
                for (String candidate: expectedCandidates) {
                         Assert.assertTrue(actualCandidates.contains(candidate));
                }
                
                preprocessing  = true;
                recRepository = 
                        TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess2(preprocessing);
                gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();
                
                expectedCandidates = new HashSet<String>();
                expectedCandidates.add("http://example.org/graph#O");
                expectedCandidates.add("http://example.org/graph#P");
                expectedCandidates.add("http://example.org/graph#S");
                expectedCandidates.add("http://example.org/graph#T");
                expectedCandidates.add("http://example.org/graph#U");
                
                actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");
                
                Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
                for (String candidate: expectedCandidates) {
                         Assert.assertTrue(actualCandidates.contains(candidate));
                }                              
        }
        
        /**
         * Tests if the method returns the right set of candidates when 
         * pre-processing is being done in the system and KSMC or PageRank with 
         * PRIORS is used as algorithm.
         */
        @Test
        public void testGetRecCandidatesUsingKsmcCd() throws RecommenderException {
                boolean preprocessing  = false;
                int numberOfSteps = 4;
                int maxNumIterations = 4;
                
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createCustomizedDatasetKsMcCdRecAndPreprocess2(numberOfSteps, maxNumIterations, preprocessing);
                
                GraphBasedDataManager gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();
                
                Set<String> expectedCandidates = new HashSet<String>();
                expectedCandidates.add("http://example.org/graph#O");
                expectedCandidates.add("http://example.org/graph#S");
                expectedCandidates.add("http://example.org/graph#T");
                
                Set<String> actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");
                
                Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
                for (String candidate: expectedCandidates) {
                         Assert.assertTrue(actualCandidates.contains(candidate));
                }
                
                preprocessing  = true;
                recRepository = 
                        TestRepositoryInstantiator.createCustomizedDatasetKsMcCdRecAndPreprocess2(numberOfSteps, maxNumIterations, preprocessing);
                gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();
                
                //Even with pre-processing, this shouldn't change.                
                actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");
                
                Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
                for (String candidate: expectedCandidates) {
                         Assert.assertTrue(actualCandidates.contains(candidate));
                }                              
        }
        
        
                                     
    /**
     * Tests if the method returns the right set of candidates when 
     * pre-processing is being done in the system and Reword is used as 
     * algorithm.
     */
    /*    
    @Test
    public void testGetRecCandidatesUsingRwSd() throws RecommenderException {
        boolean preprocessing  = false;
        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createUsersDatasetRwSdRecAndPreprocess2(preprocessing);

        GraphBasedDataManager gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();

        Set<String> expectedCandidates = new HashSet<String>();
        expectedCandidates.add("http://example.org/graph#O");
        expectedCandidates.add("http://example.org/graph#P");
        expectedCandidates.add("http://example.org/graph#Q");
        expectedCandidates.add("http://example.org/graph#R");
        expectedCandidates.add("http://example.org/graph#S");
        expectedCandidates.add("http://example.org/graph#T");
        expectedCandidates.add("http://example.org/graph#U");

        Set<String> actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");

        Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
        for (String candidate: expectedCandidates) {
                 Assert.assertTrue(actualCandidates.contains(candidate));
        }

        preprocessing  = true;
        recRepository = 
                TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess2(preprocessing);
        gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();

        expectedCandidates = new HashSet<String>();
        expectedCandidates.add("http://example.org/graph#O");
        expectedCandidates.add("http://example.org/graph#P");
        expectedCandidates.add("http://example.org/graph#S");
        expectedCandidates.add("http://example.org/graph#T");
        expectedCandidates.add("http://example.org/graph#U");

        actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");

        Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
        for (String candidate: expectedCandidates) {
                 Assert.assertTrue(actualCandidates.contains(candidate));
        }                              
    }
    */

    /**
     * Tests if the method returns the right set of candidates when 
     * pre-processing is being done in the system and KSMC or PageRank with 
     * PRIORS is used as algorithm.
     */
    /*    
    @Test
    public void testGetRecCandidatesUsingKsmcSd() throws RecommenderException {
        boolean preprocessing  = false;
        int numberOfSteps = 4;
        int maxNumIterations = 4;

        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createCustomizedDatasetKsMcSdRecAndPreprocess2(numberOfSteps, maxNumIterations, preprocessing);

        GraphBasedDataManager gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();

        Set<String> expectedCandidates = new HashSet<String>();
        expectedCandidates.add("http://example.org/graph#O");
        expectedCandidates.add("http://example.org/graph#S");
        expectedCandidates.add("http://example.org/graph#T");

        Set<String> actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");

        Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
        for (String candidate: expectedCandidates) {
                 Assert.assertTrue(actualCandidates.contains(candidate));
        }

        preprocessing  = true;
        recRepository = 
                TestRepositoryInstantiator.createCustomizedDatasetKsMcSdRecAndPreprocess2(numberOfSteps, maxNumIterations, preprocessing);
        gdm = (GraphBasedDataManager) recRepository.getRecommender().getDataManager();

        //Even with pre-processing, this shouldn't change.                
        actualCandidates = gdm.getRecCandidates("http://example.org/graph#User");

        Assert.assertEquals(expectedCandidates.size(), actualCandidates.size());
        for (String candidate: expectedCandidates) {
                 Assert.assertTrue(actualCandidates.contains(candidate));
        }                              
    }
    */
}