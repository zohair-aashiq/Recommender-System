/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.graph.impl;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.paradigm.crossdomain.CdRecommender;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.IndexBasedStorage;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;
import org.eclipse.rdf4j.recommender.util.VectorOperations;

/**
 * Test class for JungGraphIndexBasedStorage with likes.
 */
public class JungGraphIndexBasedStorageWithLikesTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-3;
        
        //TODO implement Sd test cases.
        
        /**
         * Test if all resources are indexed correctly.
         */
        @Test
        public void testIndexingCd() {
                int numberOfSteps = 6;
                int maxIterations = 6;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createPaperDatasetKsMcCdRecAndPreprocess(numberOfSteps, maxIterations);
                IndexBasedStorage storage = (IndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                                                              
                //We can start with the resource counter.
                //Stored are only users and items. For the test file this makes an overall of 6 resources.
                int expectedIndexOfResources = 13;                        
                Assert.assertEquals(expectedIndexOfResources, storage.getResourceCounter());
                
                Map<String, Integer> indexMap = storage.getResourceIdMap();
                Assert.assertEquals(expectedIndexOfResources, indexMap.size());
                
                Set<String> expectedKeySet = new HashSet<String>();
                expectedKeySet.add("http://example.org/graph#User1");
                expectedKeySet.add("http://example.org/graph#Source");
                expectedKeySet.add("http://example.org/graph#Target");
                expectedKeySet.add("http://example.org/graph#resA");
                expectedKeySet.add("http://example.org/graph#resB");
                expectedKeySet.add("http://example.org/graph#resC");
                expectedKeySet.add("http://example.org/graph#resD");
                expectedKeySet.add("http://example.org/graph#resE");
                expectedKeySet.add("http://example.org/graph#resF");
                expectedKeySet.add("http://example.org/graph#resG");
                expectedKeySet.add("http://example.org/graph#resH");
                expectedKeySet.add("http://example.org/graph#resI");
                expectedKeySet.add("http://example.org/graph#resJ");
                
                
                Assert.assertEquals(expectedKeySet, indexMap.keySet());
                 
                for (String key: expectedKeySet) {
                        Integer mapValue = indexMap.get(key);
                        //Verify that this value is indexed correctly.
                        Assert.assertTrue(key.equals(storage.getResourceList().get(mapValue)));
                }
                
                //Here we test if the items consumed by the user are stored correctly
                Set<IndexedRatedRes> expectedRatRes = new HashSet<IndexedRatedRes>();
                expectedRatRes.add(new IndexedRatedRes(indexMap.get("http://example.org/graph#resA"), 1));
                expectedRatRes.add(new IndexedRatedRes(indexMap.get("http://example.org/graph#resF"), 1));
                        
                
                Set<IndexedRatedRes> actualRatRes =
                        storage.getIndexedRatedResOfUser(0);
                
                Assert.assertEquals(expectedRatRes, actualRatRes);
                
                
                
        }               
        
        /**
         * Test if all the graph contains all the nodes and edges required 
         */
        @Test
        public void testCompleteGraphCd() {
                int numberOfSteps = 6;
                int maxIterations = 6;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createPaperDatasetKsMcCdRecAndPreprocess(numberOfSteps, maxIterations);
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                IndexBasedStorage indexStorage = (IndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                Graph<Integer, String> expectedGraph = new DirectedSparseGraph<Integer, String>();
                Graph<Integer, String> actualGraph = graphStorage.getCompleteGraph();
                
                //We assume that all resources are indexed correctly because
                //this was tested previously
                Map<String, Integer> indexMap = indexStorage.getResourceIdMap();
                
                Integer user1 = indexMap.get("http://example.org/graph#User1");
                Integer target = indexMap.get("http://example.org/graph#Target");
                Integer source = indexMap.get("http://example.org/graph#Source");
                Integer valueA = indexMap.get("http://example.org/graph#resA");
                Integer valueB = indexMap.get("http://example.org/graph#resB");
                Integer valueC = indexMap.get("http://example.org/graph#resC");
                Integer valueD = indexMap.get("http://example.org/graph#resD");
                Integer valueE = indexMap.get("http://example.org/graph#resE");
                Integer valueF = indexMap.get("http://example.org/graph#resF");
                Integer valueG = indexMap.get("http://example.org/graph#resG");
                Integer valueH = indexMap.get("http://example.org/graph#resH");
                Integer valueI = indexMap.get("http://example.org/graph#resI");
                Integer valueJ = indexMap.get("http://example.org/graph#resJ");
                
                //we build the graph manually
                for (String key: indexMap.keySet()) {
                        Integer vertexValue = indexMap.get(key);
                        Assert.assertTrue(actualGraph.containsVertex(vertexValue));
                }
                
                //This part was done manually by taking a look to the edges
                //we build the edges
                //Asserts
                //There are 30 edges
                Assert.assertTrue(actualGraph.containsEdge(valueA + "->" + valueB));                 
                Assert.assertTrue(actualGraph.containsEdge(valueB + "->" + valueA));                 
                Assert.assertTrue(actualGraph.containsEdge(valueA + "->" + valueC));                
                Assert.assertTrue(actualGraph.containsEdge(valueC + "->" + valueA));                
                Assert.assertTrue(actualGraph.containsEdge(valueA + "->" + valueD));                
                Assert.assertTrue(actualGraph.containsEdge(valueD + "->" + valueA));                
                Assert.assertTrue(actualGraph.containsEdge(valueD + "->" + valueE));                
                Assert.assertTrue(actualGraph.containsEdge(valueE + "->" + valueD));                
                Assert.assertTrue(actualGraph.containsEdge(valueD + "->" + valueF));                
                Assert.assertTrue(actualGraph.containsEdge(valueF + "->" + valueD));                
                Assert.assertTrue(actualGraph.containsEdge(valueC + "->" + valueJ));                
                Assert.assertTrue(actualGraph.containsEdge(valueJ + "->" + valueC));                
                Assert.assertTrue(actualGraph.containsEdge(valueC + "->" + valueB));                
                Assert.assertTrue(actualGraph.containsEdge(valueB + "->" + valueC));
                Assert.assertTrue(actualGraph.containsEdge(valueJ + "->" + valueH));
                Assert.assertTrue(actualGraph.containsEdge(valueH + "->" + valueJ));
                Assert.assertTrue(actualGraph.containsEdge(valueB + "->" + valueI));
                Assert.assertTrue(actualGraph.containsEdge(valueI + "->" + valueB));                
                Assert.assertTrue(actualGraph.containsEdge(valueE + "->" + valueF));                
                Assert.assertTrue(actualGraph.containsEdge(valueF + "->" + valueE));                
                Assert.assertTrue(actualGraph.containsEdge(valueE + "->" + valueJ));                
                Assert.assertTrue(actualGraph.containsEdge(valueJ + "->" + valueE));                
                Assert.assertTrue(actualGraph.containsEdge(valueH + "->" + valueG));                
                Assert.assertTrue(actualGraph.containsEdge(valueG + "->" + valueH));                
                Assert.assertTrue(actualGraph.containsEdge(valueG + "->" + valueF));                
                Assert.assertTrue(actualGraph.containsEdge(valueF + "->" + valueG));                
                Assert.assertTrue(actualGraph.containsEdge(valueG + "->" + valueI));
                Assert.assertTrue(actualGraph.containsEdge(valueI + "->" + valueG));                
                Assert.assertTrue(actualGraph.containsEdge(valueI + "->" + valueH));                
                Assert.assertTrue(actualGraph.containsEdge(valueH + "->" + valueI));  
                
                Assert.assertTrue(actualGraph.containsEdge(user1 + "->" + valueA)); 
                Assert.assertTrue(actualGraph.containsEdge(user1 + "->" + valueF)); 
                
                Assert.assertTrue(actualGraph.containsEdge(valueA + "->" + source)); 
                Assert.assertTrue(actualGraph.containsEdge(valueB + "->" + target)); 
                Assert.assertTrue(actualGraph.containsEdge(valueC + "->" + target)); 
                Assert.assertTrue(actualGraph.containsEdge(valueD + "->" + target)); 
                Assert.assertTrue(actualGraph.containsEdge(valueE + "->" + target)); 
                Assert.assertTrue(actualGraph.containsEdge(valueF + "->" + source)); 
                Assert.assertTrue(actualGraph.containsEdge(valueG + "->" + target)); 
                Assert.assertTrue(actualGraph.containsEdge(valueH + "->" + target)); 
                Assert.assertTrue(actualGraph.containsEdge(valueI + "->" + target)); 
                Assert.assertTrue(actualGraph.containsEdge(valueJ + "->" + target));
                
                Assert.assertEquals(new Integer(42), new Integer(actualGraph.getEdgeCount()));  
        }
        
        /**
         * Test the number of triples.
         */
        @Test
        public void testNumberOfTriplesCd() {     
                int numberOfSteps = 6;
                int maxIterations = 6;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createPaperDatasetKsMcCdRecAndPreprocess(numberOfSteps, maxIterations);
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                Integer expectedNumberOfTriples = 42;
                
                Assert.assertEquals(expectedNumberOfTriples, new Integer(graphStorage.numberOfTriples()));                      
        }
        
        /**
         * Test if the occurrences of predicates is correct.
         */
        @Test
        public void testPredicateOccurrenciesCd() {
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess();
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                Integer expectedNumberOfTriples = 11;
                Assert.assertEquals(expectedNumberOfTriples, new Integer(graphStorage.numberOfTriples()));
                
                Integer expectedNumberOfFoundPredicates = 8;
                Assert.assertEquals(expectedNumberOfFoundPredicates, 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().size()));
                
                //Now we check this values for each predicate:
                Assert.assertEquals(new Integer(2), 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().get("http://example.org/graph#livesIn")));
                Assert.assertEquals(new Integer(2), 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().get("http://example.org/graph#occupation")));
                Assert.assertEquals(new Integer(1), 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().get("http://example.org/graph#isBossOf")));
                Assert.assertEquals(new Integer(1), 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().get("http://example.org/graph#knows")));
                Assert.assertEquals(new Integer(1), 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().get("http://example.org/graph#loves")));
                Assert.assertEquals(new Integer(1), 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().get("http://example.org/graph#livesWith"))); 
                Assert.assertEquals(new Integer(2), 
                        new Integer(graphStorage.getPredicateOccurrenciesMap().get("http://example.org/graph#worksWith")));
        }
        
        /**
         * Tests PF as described in Reword.
         */
        @Test
        public void testInAndOutPredicateFrequencyCd() {
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess();
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                //List of predicates: livesIn occupation isBossOf knows loves livesWith
                
                Integer numOfTriplesOfJohn = 4;
                Integer numOfTriplesOfParis = 2;
                Integer numOfTriplesOfStudent = 1;
                Integer numOfTriplesOfMaria = 4;
                Integer numOfTriplesOfNurse = 1;
                Integer numOfTriplesOfWilliam = 1;                
                Integer numOfTriplesODiego = 1;                
                Integer numOfTriplesOfFlorian = 1;
                Integer numOfTriplesOfLaura = 1;
                
                Integer johnLivesInIncoming = 0;
                Integer johnLivesInOutgoing = 1;
                Integer parisLivesInIncoming = 2;
                Integer parisLivesInOutgoing = 0;
                Integer studentLivesInIncoming = 0;
                Integer studentLivesInOutgoing = 0;
                Integer mariaLivesInIncoming = 0;
                Integer mariaLivesInOutgoing = 1;
                Integer nurseLivesInIncoming = 0;
                Integer nurseLivesInOutgoing = 0;
                Integer williamLivesInIncoming = 0;
                Integer williamLivesInOutgoing = 0;
                Integer diegoLivesInIncoming = 0;
                Integer diegoLivesInOutgoing = 0;  
                Integer florianLivesInIncoming = 0;
                Integer florianLivesInOutgoing = 0; 
                Integer lauraLivesInIncoming = 0;
                Integer lauraLivesInOutgoing = 0;
                
                Integer johnOccupationIncoming = 0;
                Integer johnOccupationOutgoing = 1;
                Integer parisOccupationIncoming = 0;
                Integer parisOccupationOutgoing = 0;
                Integer studentOccupationIncoming = 1;
                Integer studentOccupationOutgoing = 0;
                Integer mariaOccupationIncoming = 0;
                Integer mariaOccupationOutgoing = 1;
                Integer nurseOccupationIncoming = 1;
                Integer nurseOccupationOutgoing = 0;
                Integer williamOccupationIncoming = 0;
                Integer williamOccupationOutgoing = 0;
                Integer diegoOccupationIncoming = 0;
                Integer diegoOccupationOutgoing = 0;  
                Integer florianOccupationIncoming = 0;
                Integer florianOccupationOutgoing = 0; 
                Integer lauraOccupationIncoming = 0;
                Integer lauraOccupationOutgoing = 0;  
                
                Integer johnIsBossOfIncoming = 1;
                Integer johnIsBossOfOutgoing = 0;
                Integer parisIsBossOfIncoming = 0;
                Integer parisIsBossOfOutgoing = 0;
                Integer studentIsBossOfIncoming = 0;
                Integer studentIsBossOfOutgoing = 0;
                Integer mariaIsBossOfIncoming = 0;
                Integer mariaIsBossOfOutgoing = 0;
                Integer nurseIsBossOfIncoming = 0;
                Integer nurseIsBossOfOutgoing = 0;
                Integer williamIsBossOfIncoming = 0;
                Integer williamIsBossOfOutgoing = 1;
                Integer diegoIsBossOfIncoming = 0;
                Integer diegoIsBossOfOutgoing = 0;  
                Integer florianIsBossOfIncoming = 0;
                Integer florianIsBossOfOutgoing = 0; 
                Integer lauraIsBossOfIncoming = 0;
                Integer lauraIsBossOfOutgoing = 0;  
                
                Integer johnKnowsIncoming = 1;
                Integer johnKnowsOutgoing = 0;
                Integer parisKnowsIncoming = 0;
                Integer parisKnowsOutgoing = 0;
                Integer studentKnowsIncoming = 0;
                Integer studentKnowsOutgoing = 0;
                Integer mariaKnowsIncoming = 0;
                Integer mariaKnowsOutgoing = 0;
                Integer nurseKnowsIncoming = 0;
                Integer nurseKnowsOutgoing = 0;
                Integer williamKnowsIncoming = 0;
                Integer williamKnowsOutgoing = 0;
                Integer diegoKnowsIncoming = 0;
                Integer diegoKnowsOutgoing = 1;  
                Integer florianKnowsIncoming = 0;
                Integer florianKnowsOutgoing = 0; 
                Integer lauraKnowsIncoming = 0;
                Integer lauraKnowsOutgoing = 0;    
                
                Integer johnLovesIncoming = 0;
                Integer johnLovesOutgoing = 0;
                Integer parisLovesIncoming = 0;
                Integer parisLovesOutgoing = 0;
                Integer studentLovesIncoming = 0;
                Integer studentLovesOutgoing = 0;
                Integer mariaLovesIncoming = 1;
                Integer mariaLovesOutgoing = 0;
                Integer nurseLovesIncoming = 0;
                Integer nurseLovesOutgoing = 0;
                Integer williamLovesIncoming = 0;
                Integer williamLovesOutgoing = 0;
                Integer diegoLovesIncoming = 0;
                Integer diegoLovesOutgoing = 0;  
                Integer florianLovesIncoming = 0;
                Integer florianLovesOutgoing = 1; 
                Integer lauraLovesIncoming = 0;
                Integer lauraLovesOutgoing = 0;  
                
                Integer johnLivesWithIncoming = 0;
                Integer johnLivesWithOutgoing = 0;
                Integer parisLivesWithIncoming = 0;
                Integer parisLivesWithOutgoing = 0;
                Integer studentLivesWithIncoming = 0;
                Integer studentLivesWithOutgoing = 0;
                Integer mariaLivesWithIncoming = 1;
                Integer mariaLivesWithOutgoing = 0;
                Integer nurseLivesWithIncoming = 0;
                Integer nurseLivesWithOutgoing = 0;
                Integer williamLivesWithIncoming = 0;
                Integer williamLivesWithOutgoing = 0;
                Integer diegoLivesWithIncoming = 0;
                Integer diegoLivesWithOutgoing = 0;  
                Integer florianLivesWithIncoming = 0;
                Integer florianLivesWithOutgoing = 0; 
                Integer lauraLivesWithIncoming = 0;
                Integer lauraLivesWithOutgoing = 1; 
                
                //Testing some of the values which results in something larger 
                //than zero.
                Assert.assertEquals(johnLivesInOutgoing / (double)numOfTriplesOfJohn, 
                        graphStorage.getNormalizedOutPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#John"), 
                                        "http://example.org/graph#livesIn"), DELTA);
                                        
                Assert.assertEquals(parisLivesInIncoming / (double)numOfTriplesOfParis, 
                        graphStorage.getNormalizedInPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#Paris"), 
                                        "http://example.org/graph#livesIn"), DELTA);                                
                        
                Assert.assertEquals(mariaLivesInOutgoing / (double)numOfTriplesOfMaria, 
                        graphStorage.getNormalizedOutPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#Maria"), 
                                        "http://example.org/graph#livesIn"), DELTA);
                
                Assert.assertEquals(johnOccupationOutgoing / (double)numOfTriplesOfJohn, 
                        graphStorage.getNormalizedOutPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#John"), 
                                        "http://example.org/graph#occupation"), DELTA);  
                                
                Assert.assertEquals(studentOccupationIncoming / (double)numOfTriplesOfStudent, 
                        graphStorage.getNormalizedInPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#Student"), 
                                        "http://example.org/graph#occupation"), DELTA);  
                                                        
                Assert.assertEquals(mariaOccupationOutgoing / (double)numOfTriplesOfMaria, 
                        graphStorage.getNormalizedOutPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#Maria"), 
                                        "http://example.org/graph#occupation"), DELTA);    
                
                
                Assert.assertEquals(nurseOccupationIncoming / (double)numOfTriplesOfNurse, 
                        graphStorage.getNormalizedInPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#Nurse"), 
                                        "http://example.org/graph#occupation"), DELTA);
                                
                Assert.assertEquals(johnIsBossOfIncoming / (double)numOfTriplesOfJohn, 
                        graphStorage.getNormalizedInPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#John"), 
                                        "http://example.org/graph#isBossOf"), DELTA);
                
                Assert.assertEquals(williamIsBossOfOutgoing / (double)numOfTriplesOfWilliam,
                        graphStorage.getNormalizedOutPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#William"), 
                                        "http://example.org/graph#isBossOf"), DELTA);  
                
                //Testing some of the values for which predicate frequency is zero.
                Assert.assertEquals(0.0,
                        graphStorage.getNormalizedOutPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#Student"), 
                                        "http://example.org/graph#isBossOf"), DELTA);
                
                Assert.assertEquals(0.0,
                        graphStorage.getNormalizedInPredFreq(
                                graphStorage.getIndexOf("http://example.org/graph#John"), 
                                        "http://example.org/graph#loves"), DELTA);                                
        }
        
        /**
         * Tests predicate and path informativeness as described in Reword.
         */
        @Test
        public void testPredicateAndPathInformativenessCd() {
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess();
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                //We assume in this test that predicate frequency input and output has been tested
                //and seems to be correct.                
                double mariaLivesInOPF = graphStorage.getNormalizedOutPredFreq(
                        graphStorage.getIndexOf("http://example.org/graph#Maria"), 
                        "http://example.org/graph#livesIn");
                
                double parisLivesInIPF = graphStorage.getNormalizedInPredFreq(
                        graphStorage.getIndexOf("http://example.org/graph#Paris"), 
                        "http://example.org/graph#livesIn");                
                
                double livesInITF = graphStorage.
                        getInvertedTripleFrequency("http://example.org/graph#livesIn");
                                                
                double expectedPredicateInformativeness = 
                        ((mariaLivesInOPF * livesInITF) +
                        (parisLivesInIPF * livesInITF)) / 2;  
                
                Assert.assertEquals(expectedPredicateInformativeness,
                        graphStorage.getPredicateInformativeness(
                                graphStorage.getIndexOf("http://example.org/graph#Maria") +
                                "->http://example.org/graph#livesIn->" + 
                                graphStorage.getIndexOf("http://example.org/graph#Paris")), DELTA);
                
                double johnLivesInOPF = graphStorage.getNormalizedOutPredFreq(
                        graphStorage.getIndexOf("http://example.org/graph#John"), 
                        "http://example.org/graph#livesIn");
                
                expectedPredicateInformativeness = 
                        ((johnLivesInOPF * livesInITF) +
                        (parisLivesInIPF * livesInITF)) / 2;
                
                Assert.assertEquals(expectedPredicateInformativeness,
                        graphStorage.getPredicateInformativeness(
                                graphStorage.getIndexOf("http://example.org/graph#John") +
                                "->http://example.org/graph#livesIn->" + 
                                graphStorage.getIndexOf("http://example.org/graph#Paris")), DELTA);
                
                double mariaOccupationOPF = graphStorage.getNormalizedOutPredFreq(
                        graphStorage.getIndexOf("http://example.org/graph#Maria"), 
                        "http://example.org/graph#occupation");
                
                double nurseOccupationInIPF = graphStorage.getNormalizedInPredFreq(
                        graphStorage.getIndexOf("http://example.org/graph#Nurse"), 
                        "http://example.org/graph#occupation");                
                
                double occupationITF = graphStorage.
                        getInvertedTripleFrequency("http://example.org/graph#occupation");
                
                expectedPredicateInformativeness = 
                        ((mariaOccupationOPF * occupationITF) +
                        (nurseOccupationInIPF * occupationITF)) / 2; 
                
                Assert.assertEquals(expectedPredicateInformativeness,
                        graphStorage.getPredicateInformativeness(
                                graphStorage.getIndexOf("http://example.org/graph#Maria") +
                                "->http://example.org/graph#occupation->" + 
                                graphStorage.getIndexOf("http://example.org/graph#Nurse")), DELTA);
                
                double johnOccupationOPF = graphStorage.getNormalizedOutPredFreq(
                        graphStorage.getIndexOf("http://example.org/graph#John"), 
                        "http://example.org/graph#occupation");
                 
                double studentOccupationInIPF = graphStorage.getNormalizedInPredFreq(
                        graphStorage.getIndexOf("http://example.org/graph#Student"), 
                        "http://example.org/graph#occupation");
                 
                expectedPredicateInformativeness = 
                        ((johnOccupationOPF * occupationITF) +
                        (studentOccupationInIPF * occupationITF)) / 2;
                  
                Assert.assertEquals(expectedPredicateInformativeness,
                        graphStorage.getPredicateInformativeness(
                                graphStorage.getIndexOf("http://example.org/graph#John") +
                                "->http://example.org/graph#occupation->" + 
                                graphStorage.getIndexOf("http://example.org/graph#Student")), DELTA);
                  
                //Now we can test the path informativeness
                //Path between John and Maria: John -> Paris <- Maria
                //We assume the the single predicate informativeness is correct.
                  
                String johnLivesInParisSt = 
                        graphStorage.getIndexOf("http://example.org/graph#John") +
                                "->http://example.org/graph#livesIn->" + 
                                graphStorage.getIndexOf("http://example.org/graph#Paris");
                String mariaLivesInParisSt =
                        graphStorage.getIndexOf("http://example.org/graph#Maria") +
                                "->http://example.org/graph#livesIn->" + 
                                graphStorage.getIndexOf("http://example.org/graph#Paris");
                
                List<String> statements = new ArrayList<String>();
                statements.add(johnLivesInParisSt);
                statements.add(mariaLivesInParisSt);
                  
                double expectedPathInf = (
                        graphStorage.getPredicateInformativeness(johnLivesInParisSt) +
                        graphStorage.getPredicateInformativeness(mariaLivesInParisSt)
                        ) / 2;
                
                Assert.assertEquals(expectedPathInf,
                        graphStorage.getPathInformativeness(statements), DELTA);
                 
                 
                String williamBossOfJohnSt = 
                        graphStorage.getIndexOf("http://example.org/graph#William") +
                                "->http://example.org/graph#isBossOf->" + 
                                graphStorage.getIndexOf("http://example.org/graph#John");
                
                statements = new ArrayList<String>();
                statements.add(williamBossOfJohnSt);
                statements.add(johnLivesInParisSt);
                
                expectedPathInf = (
                        graphStorage.getPredicateInformativeness(williamBossOfJohnSt) +
                        graphStorage.getPredicateInformativeness(johnLivesInParisSt)
                        ) / 2;
                
                Assert.assertEquals(expectedPathInf,
                        graphStorage.getPathInformativeness(statements), DELTA);
                
        }
        
        
        
        @Test
        public void testFindAllPathsCd() {
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess();
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                Set<List<String>> allActualPaths = new HashSet();
                
                //First we test that no paths are found when they don't exist
                int florianNodeId = graphStorage.getIndexOf("http://example.org/graph#Florian");
                int robinNodeId = graphStorage.getIndexOf("http://example.org/graph#Robin");
                allActualPaths = graphStorage.findAllPaths(florianNodeId, robinNodeId, 1);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(robinNodeId, florianNodeId, 1);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(florianNodeId, robinNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(robinNodeId, florianNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(florianNodeId, robinNodeId, 3);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(robinNodeId, florianNodeId, 3);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(florianNodeId, robinNodeId, 4);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(robinNodeId, florianNodeId, 4);
                Assert.assertEquals(0, allActualPaths.size());
                
                //We check now the case for which there is only one path
                int mariaNodeId = graphStorage.getIndexOf("http://example.org/graph#Maria");
                int parisNodeId = graphStorage.getIndexOf("http://example.org/graph#Paris");
                List<String> path = new ArrayList<String>();
                String predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                path.add(predicate);
                allActualPaths = graphStorage.findAllPaths(mariaNodeId, parisNodeId, 1);
                Assert.assertEquals(1, allActualPaths.size());
                Assert.assertTrue(allActualPaths.contains(path));
                allActualPaths = graphStorage.findAllPaths(parisNodeId, mariaNodeId, 1);
                Assert.assertEquals(1, allActualPaths.size());
                Assert.assertTrue(allActualPaths.contains(path));
                //Changing K should not have any effect on the result
                allActualPaths = graphStorage.findAllPaths(mariaNodeId, parisNodeId, 2);
                Assert.assertEquals(1, allActualPaths.size());
                Assert.assertTrue(allActualPaths.contains(path));
                allActualPaths = graphStorage.findAllPaths(parisNodeId, mariaNodeId, 2);
                Assert.assertEquals(1, allActualPaths.size());
                Assert.assertTrue(allActualPaths.contains(path));
                
                //We check now the case for which there are multiple paths of
                //different lengths
                int williamNodeId = graphStorage.getIndexOf("http://example.org/graph#William");
                
                allActualPaths = graphStorage.findAllPaths(florianNodeId, williamNodeId, 1);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(williamNodeId, florianNodeId, 1);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(florianNodeId, williamNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(williamNodeId, florianNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(florianNodeId, williamNodeId, 3);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(williamNodeId, florianNodeId, 3);
                Assert.assertEquals(0, allActualPaths.size());
                //From this point on, we have 
                allActualPaths = graphStorage.findAllPaths(florianNodeId, williamNodeId, 4);
                Assert.assertEquals(2, allActualPaths.size());
                //First path
                path = new ArrayList<String>();
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Sarah") + "->" +
                        "http://example.org/graph#worksWith" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Florian");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Diego") + "->" +
                        "http://example.org/graph#worksWith" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Sarah");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Diego") + "->" +
                        "http://example.org/graph#knows" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#John");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#William") + "->" +
                        "http://example.org/graph#isBossOf" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#John");
                path.add(predicate);
                Assert.assertTrue(allActualPaths.contains(path));
                //Second path
                path = new ArrayList<String>();
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Florian") + "->" +
                        "http://example.org/graph#loves" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Maria");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#John") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#William") + "->" +
                        "http://example.org/graph#isBossOf" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#John");
                path.add(predicate);
                Assert.assertTrue(allActualPaths.contains(path));
                //Same test in reverse
                allActualPaths = graphStorage.findAllPaths(williamNodeId, florianNodeId, 4);
                Assert.assertEquals(2, allActualPaths.size());                
                //First path
                path = new ArrayList<String>();
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#William") + "->" +
                        "http://example.org/graph#isBossOf" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#John");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Diego") + "->" +
                        "http://example.org/graph#knows" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#John");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Diego") + "->" +
                        "http://example.org/graph#worksWith" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Sarah");
                path.add(predicate);                                
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Sarah") + "->" +
                        "http://example.org/graph#worksWith" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Florian");
                path.add(predicate);
                Assert.assertTrue(allActualPaths.contains(path));                
                //Second path
                path = new ArrayList<String>();
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#William") + "->" +
                        "http://example.org/graph#isBossOf" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#John");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#John") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                path.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Florian") + "->" +
                        "http://example.org/graph#loves" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Maria");
                path.add(predicate);
                Assert.assertTrue(allActualPaths.contains(path));
                //We also check that nothing changes if we increase K
                Assert.assertEquals(allActualPaths, graphStorage.findAllPaths(williamNodeId, florianNodeId, 5));
                Assert.assertEquals(allActualPaths, graphStorage.findAllPaths(williamNodeId, florianNodeId, 6));
                
                //Another interesting test case. Path between Student and Nurse
                int studentNodeId = graphStorage.getIndexOf("http://example.org/graph#Student");
                int nurseNodeId = graphStorage.getIndexOf("http://example.org/graph#Nurse");
                allActualPaths = graphStorage.findAllPaths(studentNodeId, nurseNodeId, 1);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(nurseNodeId, studentNodeId, 1);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(studentNodeId, nurseNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(nurseNodeId, studentNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(studentNodeId, nurseNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(nurseNodeId, studentNodeId, 2);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(studentNodeId, nurseNodeId, 3);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(nurseNodeId, studentNodeId, 3);
                Assert.assertEquals(0, allActualPaths.size());
                allActualPaths = graphStorage.findAllPaths(studentNodeId, nurseNodeId, 4);
                List<String> pathFromStudentToNurseViaParis = new ArrayList<String>();
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#John") + "->" +
                        "http://example.org/graph#occupation" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Student");
                pathFromStudentToNurseViaParis.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#John") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                pathFromStudentToNurseViaParis.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                pathFromStudentToNurseViaParis.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#occupation" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Nurse");
                pathFromStudentToNurseViaParis.add(predicate);
                Assert.assertEquals(1, allActualPaths.size());
                Assert.assertTrue(allActualPaths.contains(pathFromStudentToNurseViaParis));
                
                allActualPaths = graphStorage.findAllPaths(nurseNodeId, studentNodeId, 4);
                Assert.assertEquals(1, allActualPaths.size());
                List<String> pathFromNurseToStudentViaParis = new ArrayList<String>();
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#occupation" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Nurse");
                pathFromNurseToStudentViaParis.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                pathFromNurseToStudentViaParis.add(predicate);    
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#John") + "->" +
                        "http://example.org/graph#livesIn" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Paris");
                pathFromNurseToStudentViaParis.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#John") + "->" +
                        "http://example.org/graph#occupation" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Student");
                pathFromNurseToStudentViaParis.add(predicate);
                Assert.assertEquals(1, allActualPaths.size());
                Assert.assertTrue(allActualPaths.contains(pathFromNurseToStudentViaParis));
                Assert.assertEquals(allActualPaths, graphStorage.findAllPaths(nurseNodeId, studentNodeId, 5));
                Assert.assertNotEquals(allActualPaths, graphStorage.findAllPaths(nurseNodeId, studentNodeId, 6));
                allActualPaths = graphStorage.findAllPaths(nurseNodeId, studentNodeId, 6);
                List<String> pathFromNurseToStudentViaSarah = new ArrayList<String>();
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Maria") + "->" +
                        "http://example.org/graph#occupation" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Nurse");
                pathFromNurseToStudentViaSarah.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Florian") + "->" +
                        "http://example.org/graph#loves" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Maria");
                pathFromNurseToStudentViaSarah.add(predicate);    
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Sarah") + "->" +
                        "http://example.org/graph#worksWith" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Florian");
                pathFromNurseToStudentViaSarah.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Diego") + "->" +
                        "http://example.org/graph#worksWith" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Sarah");
                pathFromNurseToStudentViaSarah.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#Diego") + "->" +
                        "http://example.org/graph#knows" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#John");
                pathFromNurseToStudentViaSarah.add(predicate);
                predicate = 
                        graphStorage.getIndexOf("http://example.org/graph#John") + "->" +
                        "http://example.org/graph#occupation" + "->" +
                        graphStorage.getIndexOf("http://example.org/graph#Student");
                pathFromNurseToStudentViaSarah.add(predicate);    
                Assert.assertTrue(allActualPaths.contains(pathFromNurseToStudentViaParis));
                Assert.assertTrue(allActualPaths.contains(pathFromNurseToStudentViaSarah));
                
        }
        
        @Test
        public void testComputeRewordRelatednessCd() {
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createUsersDatasetRwCdRecAndPreprocess();
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                int indexOfJohn = graphStorage.getIndexOf("http://example.org/graph#John");
                int indexOfParis = graphStorage.getIndexOf("http://example.org/graph#Paris");
                int indexOfMaria = graphStorage.getIndexOf("http://example.org/graph#Maria");
                
                //We compute the relatedness between John and Maria
                //We assume that values like PFITF are correct because this have
                //individually tested
                
                //First the longest path. There is only one with length <= 3
                double livesInJPInf = graphStorage.getPredicateInformativeness(
                        indexOfJohn + "->http://example.org/graph#livesIn->" + indexOfParis);
                
                double livesInMPInf = graphStorage.getPredicateInformativeness(
                        indexOfMaria + "->http://example.org/graph#livesIn->" + indexOfParis);
                
                //We build the input/output vectors for each resource.                
                IndexedRatedRes[] johnInputVector = new IndexedRatedRes[3];
                IndexedRatedRes[] johnOutputVector = new IndexedRatedRes[2];
                IndexedRatedRes[] mariaInputVector = new IndexedRatedRes[3];
                IndexedRatedRes[] mariaOutputVector = new IndexedRatedRes[2];
                
                int indexOfKnows = 0;
                int indexOfIsBossOf = 1;
                int indexOfLivesIn = 2;
                int indexOfOccupation = 3;
                int indexOfLoves = 4;
                int indexOfLivesWith = 5;
                
                johnInputVector[0] = new IndexedRatedRes(indexOfKnows, 
                        graphStorage.getInPFITF(indexOfJohn, "http://example.org/graph#knows"));
                johnInputVector[1] = new IndexedRatedRes(indexOfIsBossOf, 
                        graphStorage.getOutPFITF(indexOfJohn, "http://example.org/graph#isBossOf"));                
                johnOutputVector[0] = new IndexedRatedRes(indexOfLivesIn, 
                        graphStorage.getInPFITF(indexOfJohn, "http://example.org/graph#livesIn"));
                johnOutputVector[1] = new IndexedRatedRes(indexOfOccupation, 
                        graphStorage.getOutPFITF(indexOfJohn, "http://example.org/graph#occupation"));
                
                mariaInputVector[0] = new IndexedRatedRes(indexOfLoves, 
                        graphStorage.getInPFITF(indexOfMaria, "http://example.org/graph#loves"));
                mariaInputVector[1] = new IndexedRatedRes(indexOfLivesWith, 
                        graphStorage.getOutPFITF(indexOfMaria, "http://example.org/graph#livesWith"));                
                mariaOutputVector[0] = new IndexedRatedRes(indexOfLivesIn, 
                        graphStorage.getInPFITF(indexOfMaria, "http://example.org/graph#livesIn"));
                mariaOutputVector[1] = new IndexedRatedRes(indexOfOccupation, 
                        graphStorage.getOutPFITF(indexOfMaria, "http://example.org/graph#occupation"));
                
                //We add to that the predicate informativeness of the most informative path
                johnInputVector[2] = new IndexedRatedRes(indexOfLivesIn, livesInJPInf + livesInMPInf);
                mariaInputVector[2] = new IndexedRatedRes(indexOfLivesIn, livesInJPInf + livesInMPInf);

                johnOutputVector[0] = new IndexedRatedRes(johnOutputVector[0].getResourceId(), 
                        johnOutputVector[0].getRating() + livesInJPInf + livesInMPInf);
                mariaOutputVector[0] = new IndexedRatedRes(mariaOutputVector[0].getResourceId(), 
                        mariaOutputVector[0].getRating() + livesInJPInf + livesInMPInf);
                
                //Now we have to normalize the errors:
                double normJohnInput = 0.0;
                double normMariaInput = 0.0;
                double normJohnOutput = 0.0;
                double normMariaOutput = 0.0;
                
                for (IndexedRatedRes irr: johnInputVector) {
                        normJohnInput = normJohnInput + (irr.getRating() * irr.getRating());
                }
                for (IndexedRatedRes irr: mariaInputVector) {
                        normMariaInput = normMariaInput + (irr.getRating() * irr.getRating());
                }
                for (IndexedRatedRes irr: johnOutputVector) {
                        normJohnOutput = normJohnOutput + (irr.getRating() * irr.getRating());
                }
                for (IndexedRatedRes irr: mariaOutputVector) {
                        normMariaOutput = normMariaOutput + (irr.getRating() * irr.getRating());
                }
                
                normJohnInput = Math.sqrt(normJohnInput);
                normMariaInput = Math.sqrt(normMariaInput);
                normJohnOutput = Math.sqrt(normJohnOutput);
                normMariaOutput = Math.sqrt(normMariaOutput);
                
                for (int i = 0; i < johnInputVector.length; i++) {
                        IndexedRatedRes irr = johnInputVector[i];
                        johnInputVector[i] = new IndexedRatedRes(irr.getResourceId(), irr.getRating() / normJohnInput);
                }
                for (int i = 0; i < mariaInputVector.length; i++) {
                        IndexedRatedRes irr = mariaInputVector[i];
                        mariaInputVector[i] = new IndexedRatedRes(irr.getResourceId(), irr.getRating() / normMariaInput);
                }
                for (int i = 0; i < johnOutputVector.length; i++) {
                        IndexedRatedRes irr = johnOutputVector[i];
                        johnOutputVector[i] = new IndexedRatedRes(irr.getResourceId(), irr.getRating() / normJohnOutput);
                }
                for (int i = 0; i < mariaOutputVector.length; i++) {
                        IndexedRatedRes irr = mariaOutputVector[i];
                        mariaOutputVector[i] = new IndexedRatedRes(irr.getResourceId(), irr.getRating() / normMariaOutput);
                }
                                                
                //We sort the vectors
                Arrays.sort(johnInputVector);
                Arrays.sort(johnOutputVector);
                Arrays.sort(mariaInputVector);
                Arrays.sort(mariaOutputVector);
                
                double expectedCosSim = 
                        (VectorOperations.computeSimilarityOfNormalizedSortedVectors(johnInputVector, mariaInputVector) + 
                        VectorOperations.computeSimilarityOfNormalizedSortedVectors(johnOutputVector, mariaOutputVector))
                        / 2;
                
                /*
                System.out.println("***************************************************************");
                System.out.println("John input vector");
                for (IndexedRatedRes irr: johnInputVector) {
                        System.out.println(irr);
                }
                System.out.println("Maria input vector");
                for (IndexedRatedRes irr: mariaInputVector) {
                        System.out.println(irr);
                }
                System.out.println("John output vector");
                for (IndexedRatedRes irr: johnOutputVector) {
                        System.out.println(irr);
                }
                System.out.println("Maria input vector");
                for (IndexedRatedRes irr: mariaOutputVector) {
                        System.out.println(irr);
                }
                System.out.println("Reword: ");
                System.out.println(expectedCosSim);
                System.out.println("***************************************************************");
                */
                
                //TODO complete this test
                //Assert.assertEquals(expectedCosSim, graphStorage.computeRewordRelatedness(indexOfJohn, indexOfMaria), DELTA);
        }
        
        /**
         * Test the graph that is built to recommend a set of products to a 
         * single user. 
         */
        @Test
        public void testComputeUserSubgraphCd() {
                int numberOfSteps = 6;
                int maxIterations = 6;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createCustomizedDatasetKsMcCdRecAndPreprocess(numberOfSteps, maxIterations);

                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                IndexBasedStorage indexStorage = (IndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                Map<String, Integer> indexMap = indexStorage.getResourceIdMap();
                
                Integer user1 = indexMap.get("http://example.org/graph#User1");
                Integer target = indexMap.get("http://example.org/graph#Target");
                Integer source = indexMap.get("http://example.org/graph#Source");
                Integer valueA = indexMap.get("http://example.org/graph#resA");
                Integer valueB = indexMap.get("http://example.org/graph#resB");
                //Integer valueC = indexMap.get("http://example.org/graph#resC");
                Integer valueD = indexMap.get("http://example.org/graph#resD");
                //Integer valueE = indexMap.get("http://example.org/graph#resE");
                //Integer valueF = indexMap.get("http://example.org/graph#resF");
                Integer valueG = indexMap.get("http://example.org/graph#resG");
                
                //We invoke the method to build the graph of User1.
                graphStorage.computeUserSubgraph(
                        indexMap.get("http://example.org/graph#User1"), 
                        indexStorage.getIndexedRatedResOfUser(
                                        indexMap.get("http://example.org/graph#User1")));
                //We store that graph.
                Graph<Integer, String> actualUserGraph = graphStorage.getUserGraph();
                
                Assert.assertTrue(actualUserGraph.containsVertex(user1));
                Assert.assertTrue(actualUserGraph.containsVertex(source));
                Assert.assertTrue(actualUserGraph.containsVertex(target));
                Assert.assertTrue(actualUserGraph.containsVertex(valueA));
                Assert.assertTrue(actualUserGraph.containsVertex(valueB));
                Assert.assertTrue(actualUserGraph.containsVertex(valueD));
                Assert.assertTrue(actualUserGraph.containsVertex(valueG));                
                
                Assert.assertEquals(7, actualUserGraph.getVertices().size());
                
                Assert.assertTrue(actualUserGraph.containsEdge(source + "->" + user1));
                Assert.assertTrue(actualUserGraph.containsEdge(target + "->" + user1));
                Assert.assertTrue(actualUserGraph.containsEdge(user1 + "->" + valueA));                
                Assert.assertTrue(actualUserGraph.containsEdge(valueA + "->" + source));
                Assert.assertTrue(actualUserGraph.containsEdge(valueA + "->" + valueB)); 
                Assert.assertTrue(actualUserGraph.containsEdge(valueA + "->" + valueG)); 
                Assert.assertTrue(actualUserGraph.containsEdge(valueB + "->" + valueD)); 
                Assert.assertTrue(actualUserGraph.containsEdge(valueD + "->" + target)); 
                Assert.assertTrue(actualUserGraph.containsEdge(valueG + "->" + target)); 
                
                Assert.assertEquals(9, actualUserGraph.getEdges().size());                                                             
        }                
        
        /**
         * Test if all the graph contains all the nodes and edges required
         */
        /*
        @Test
        public void testKStepMarkovCentrality() {
                int numberOfSteps = 6;
                int maxIterations = 6;
                TestFixedSailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createCustomizedDatasetKsMcCdRecAndPreprocess(numberOfSteps, maxIterations);
            
                IndexBasedStorage storage = (IndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                        ((CdRecommender)recRepository.getRecommender()).getDataManager().getStorage();
                
                Map<String, Integer> indexMap = storage.getResourceIdMap();
                
                Integer user1 = indexMap.get("http://example.org/graph#User1");
                Integer target = indexMap.get("http://example.org/graph#Target");
                Integer source = indexMap.get("http://example.org/graph#Source");
                Integer valueA = indexMap.get("http://example.org/graph#resA");
                Integer valueB = indexMap.get("http://example.org/graph#resB");
                //Integer valueC = indexMap.get("http://example.org/graph#resC");
                Integer valueD = indexMap.get("http://example.org/graph#resD");
                //Integer valueE = indexMap.get("http://example.org/graph#resE");
                //Integer valueF = indexMap.get("http://example.org/graph#resF");
                Integer valueG = indexMap.get("http://example.org/graph#resG");
                
                //We get the items that User1 has liked
                //We use nodes A as seed
                graphStorage.
                        ksmcUniform(
                                indexMap.get("http://example.org/graph#User1"), 6, 6);
                
                              
                KStepMarkov<Integer, String> ranker = graphStorage.getKsmVertexScorer();
                Assert.assertEquals(0.25396825396825395, ranker.getVertexScore(valueA), DELTA);
                Assert.assertEquals(0.06349206349206349, ranker.getVertexScore(source), DELTA);
                Assert.assertEquals(0.06349206349206349, ranker.getVertexScore(valueB), DELTA);
                Assert.assertEquals(0.07936507936507936, ranker.getVertexScore(valueD), DELTA);
                Assert.assertEquals(0.19047619047619047, ranker.getVertexScore(target), DELTA);
                Assert.assertEquals(0.06349206349206349, ranker.getVertexScore(valueG), DELTA);
                Assert.assertEquals(0.2857142857142857, ranker.getVertexScore(user1), DELTA);
                
                //The maximum of the target resources, i.e. the maximum between D and G
                double maximum = 0.07936507936507936;
                
                //The method getVertexScore() from the storage returns the same value normalized
                Assert.assertEquals(0.25396825396825395 / maximum, graphStorage.getKsmVertexScore(valueA), DELTA);                
                Assert.assertEquals(0.06349206349206349 / maximum, graphStorage.getKsmVertexScore(source), DELTA);
                Assert.assertEquals(0.06349206349206349 / maximum, graphStorage.getKsmVertexScore(valueB), DELTA);
                Assert.assertEquals(0.07936507936507936 / maximum, graphStorage.getKsmVertexScore(valueD), DELTA);
                Assert.assertEquals(0.19047619047619047 / maximum, graphStorage.getKsmVertexScore(target), DELTA);
                Assert.assertEquals(0.06349206349206349 / maximum, graphStorage.getKsmVertexScore(valueG), DELTA);
                Assert.assertEquals(0.2857142857142857 / maximum, graphStorage.getKsmVertexScore(user1), DELTA);
                
                //Same example from before with           
                graphStorage.
                        ksmcUniform(
                                indexMap.get("http://example.org/graph#User1"), 6, 30);
                
                ranker = graphStorage.getKsmVertexScorer();
                
                Assert.assertEquals(0.24978286798131324, ranker.getVertexScore(valueA), DELTA);                
                Assert.assertEquals(0.08337627587741897, ranker.getVertexScore(source), DELTA);
                Assert.assertEquals(0.08337627587741897, ranker.getVertexScore(valueB), DELTA);
                Assert.assertEquals(0.08345369342168607, ranker.getVertexScore(valueD), DELTA);
                Assert.assertEquals(0.16674771315832124, ranker.getVertexScore(target), DELTA);
                Assert.assertEquals(0.08337627587741897, ranker.getVertexScore(valueG), DELTA);
                Assert.assertEquals(0.24988689780642218, ranker.getVertexScore(user1), DELTA);
                
                
                
                //The method getVertexScore() from the storage returns the same value normalized
                Assert.assertEquals(0.24978286798131324 / 0.08345369342168607, graphStorage.getKsmVertexScore(valueA), DELTA);                
                Assert.assertEquals(0.08337627587741897 / 0.08345369342168607, graphStorage.getKsmVertexScore(source), DELTA);
                Assert.assertEquals(0.08337627587741897 / 0.08345369342168607, graphStorage.getKsmVertexScore(valueB), DELTA);
                Assert.assertEquals(0.08337627587741897 / 0.08345369342168607, graphStorage.getKsmVertexScore(valueD), DELTA);
                Assert.assertEquals(0.16674771315832124 / 0.08345369342168607, graphStorage.getKsmVertexScore(target), DELTA);
                Assert.assertEquals(0.08337627587741897 / 0.08345369342168607, graphStorage.getKsmVertexScore(valueG), DELTA);
                Assert.assertEquals(0.24988689780642218 / 0.08345369342168607, graphStorage.getKsmVertexScore(user1), DELTA);                                
        }
        */       
}
