/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.util;

import org.eclipse.rdf4j.recommender.util.VectorOperations;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;

/**
 * Test class for CosineVectorSimilarity.
 */
public class CosineVectorSimilarityTest {    
        /**
         * Test method for
         * {@link org.openrdf.recommender.util.CosineVectorSimilarity#computeSimilarityOfNormalizedOrderedVectors(
         * Object[] list1, Object[] list2)}
         * Verifies the correct computation of cosine similarity when the arrays contain objects of type
         * {@link org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes}.
         */
        @Test
        public void testCalculateCosineVectorSimilarity() {
                //Test 1
                IndexedRatedRes[] vector1 = new IndexedRatedRes[1];
                vector1[0] = new IndexedRatedRes(999999, 2);

                IndexedRatedRes[] vector2 = new IndexedRatedRes[1];
                vector2[0] = new IndexedRatedRes(150001, 1);

                Double actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2); 
                Double actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);      
                Assert.assertEquals(new Double(0.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);
                
                
                //Test 1.1
                vector1 = new IndexedRatedRes[1];
                vector1[0] = new IndexedRatedRes(999999, 2);

                vector2 = new IndexedRatedRes[1];
                vector2[0] = new IndexedRatedRes(999999, 1);
                
                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2); 
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);      
                Assert.assertEquals(new Double(2.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);
                
                //Test 1.2
                vector1 = new IndexedRatedRes[1];
                vector1[0] = new IndexedRatedRes(999999, 0);

                vector2 = new IndexedRatedRes[1];
                vector2[0] = new IndexedRatedRes(999999, 1);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2); 
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);      
                Assert.assertEquals(new Double(0.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);
                
                //Test 1.3
                vector1 = new IndexedRatedRes[1];
                vector1[0] = new IndexedRatedRes(999999, -5);

                vector2 = new IndexedRatedRes[1];
                vector2[0] = new IndexedRatedRes(999999, -2);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2); 
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);      
                Assert.assertEquals(new Double(10.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);
                
                //Test 1.4
                vector1 = new IndexedRatedRes[1];
                vector1[0] = new IndexedRatedRes(999999, -5);

                vector2 = new IndexedRatedRes[1];
                vector2[0] = new IndexedRatedRes(999999, 2);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2); 
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);      
                Assert.assertEquals(new Double(-10.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);            

                //Test 2
                vector1 = new IndexedRatedRes[2];
                vector1[0] = new IndexedRatedRes(999999, 1);
                vector1[1] = new IndexedRatedRes(999999, 1);

                vector2 = new IndexedRatedRes[1];
                vector2[0] = new IndexedRatedRes(999999, 1);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);      
                Assert.assertEquals(new Double(1.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);

                //Test 3
                vector2 = new IndexedRatedRes[2];
                vector2[0] = new IndexedRatedRes(999999, -1);
                vector2[1] = new IndexedRatedRes(1999999, 0);      

                vector1 = new IndexedRatedRes[1];
                vector1[0] = new IndexedRatedRes(999999, 10);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);   
                Assert.assertEquals(new Double(-10.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);

                
                //Test 4
                vector1 = new IndexedRatedRes[7];
                vector1[0] = new IndexedRatedRes(3,2);
                vector1[1] = new IndexedRatedRes(6,3);
                vector1[2] = new IndexedRatedRes(8,5);
                vector1[3] = new IndexedRatedRes(9,1);
                vector1[4] = new IndexedRatedRes(11,1);
                vector1[5] = new IndexedRatedRes(12,0);
                vector1[6] = new IndexedRatedRes(13,1);            

                vector2 = new IndexedRatedRes[5];
                vector2[0] = new IndexedRatedRes(3,3);
                vector2[1] = new IndexedRatedRes(6,2);
                vector2[2] = new IndexedRatedRes(8,1);
                vector2[3] = new IndexedRatedRes(9,4);
                vector2[4] = new IndexedRatedRes(11,2);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);            
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);
                Assert.assertEquals(new Double(23.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);


                //Test 5
                vector1 = new IndexedRatedRes[4];
                vector1[0] = new IndexedRatedRes(4,1);
                vector1[1] = new IndexedRatedRes(5,7);
                vector1[2] = new IndexedRatedRes(8,2);
                vector1[3] = new IndexedRatedRes(9,3);          

                vector2 = new IndexedRatedRes[5];
                vector2[0] = new IndexedRatedRes(6,3);
                vector2[1] = new IndexedRatedRes(7,0);
                vector2[2] = new IndexedRatedRes(8,2);
                vector2[3] = new IndexedRatedRes(10,2);
                vector2[4] = new IndexedRatedRes(11,1);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);
                Assert.assertEquals(new Double(4.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);

                //Test 6
                vector1 = new IndexedRatedRes[9];
                vector1[0] = new IndexedRatedRes(0,-2);
                vector1[1] = new IndexedRatedRes(1,3);
                vector1[2] = new IndexedRatedRes(20,4);
                vector1[3] = new IndexedRatedRes(200,5);
                vector1[4] = new IndexedRatedRes(250,0); 
                vector1[5] = new IndexedRatedRes(2000,-2); 
                vector1[6] = new IndexedRatedRes(2500,3); 
                vector1[7] = new IndexedRatedRes(2700,0); 
                vector1[8] = new IndexedRatedRes(4000,1); 

                vector2 = new IndexedRatedRes[8];
                vector2[0] = new IndexedRatedRes(10,-1);
                vector2[1] = new IndexedRatedRes(20,3);
                vector2[2] = new IndexedRatedRes(100,-3);
                vector2[3] = new IndexedRatedRes(200,2);
                vector2[4] = new IndexedRatedRes(250,-1);
                vector2[5] = new IndexedRatedRes(2000,-5);
                vector2[6] = new IndexedRatedRes(5000,3);
                vector2[7] = new IndexedRatedRes(6000,2);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);
                Assert.assertEquals(new Double(32.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);

                //Test 7
                vector1 = new IndexedRatedRes[5];
                vector1[0] = new IndexedRatedRes(200,3);
                vector1[1] = new IndexedRatedRes(2000,3);
                vector1[2] = new IndexedRatedRes(3000,1);
                vector1[3] = new IndexedRatedRes(4000,2);
                vector1[4] = new IndexedRatedRes(5000,3); 

                vector2 = new IndexedRatedRes[9];
                vector2[0] = new IndexedRatedRes(1,1);
                vector2[1] = new IndexedRatedRes(10,0);
                vector2[2] = new IndexedRatedRes(100,2);
                vector2[3] = new IndexedRatedRes(10000,0);
                vector2[4] = new IndexedRatedRes(20000,0);
                vector2[5] = new IndexedRatedRes(50000,1);
                vector2[6] = new IndexedRatedRes(500000,0);
                vector2[7] = new IndexedRatedRes(600000,1);
                vector2[8] = new IndexedRatedRes(700000,0);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);
                Assert.assertEquals(new Double(0.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);

                //Test 8
                vector1 = new IndexedRatedRes[2];
                vector1[0] = new IndexedRatedRes(1,-2);
                vector1[1] = new IndexedRatedRes(2,-1);

                vector2 = new IndexedRatedRes[2];
                vector2[0] = new IndexedRatedRes(1,1);
                vector2[1] = new IndexedRatedRes(2,-2);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);
                Assert.assertEquals(new Double(0.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);

                //Test 9
                vector1 = new IndexedRatedRes[5];
                vector1[0] = new IndexedRatedRes(0,2);
                vector1[1] = new IndexedRatedRes(1,-1);
                vector1[2] = new IndexedRatedRes(3,4);
                vector1[3] = new IndexedRatedRes(4,3);
                vector1[4] = new IndexedRatedRes(5,1);

                vector2 = new IndexedRatedRes[2];
                vector2[0] = new IndexedRatedRes(0,5);
                vector2[1] = new IndexedRatedRes(5,4);

                actualSimScore = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector1, vector2);
                actualSimScore2 = VectorOperations.computeSimilarityOfNormalizedSortedVectors(vector2, vector1);
                Assert.assertEquals(new Double(14.0), actualSimScore);
                Assert.assertEquals(actualSimScore, actualSimScore2);
        }
}
