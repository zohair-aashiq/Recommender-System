/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage;

import edu.uci.ics.jung.graph.Graph;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface of an graph-based storage for a recommender.
 */
public interface GraphBasedStorage extends Storage {
        /**
         * Adds a node to the inner graph model.
         * @param URI 
         */
        public void addNode(String URI);
        
        /**
         * Sets one of the nodes as target of the recommendation.
         * @param URI 
         */
        public void setTargetNode(String URI);
        
        /**
         * Get a set of all nodes marked as target.
         * @return 
         */
        public Set<Integer> getTargetNodes();        
        
        /**
         * Adds an edge to the inner graph model. If the predicate is null then
         * a predicate is created on-the-fly by making a string out of the
         * source and target as follows: "source -> target"
         * 
         * @param sourceURI
         * @param targetURI
         * @param predicateURI 
         */
        public void addEdge(String sourceURI, String targetURI, String predicateURI);
        
        /**
         * If two nodes are reachable in a graph one can store this information.
         * If the graph is directed it is treated as undirected.
         * This method should help to speed up some of the methods. 
         * @param node1
         * @param node2 
         * @param numberOfHops 
         */
        public void storeReachability(int node1, int node2, int numberOfHops);
        
        /**
         * Allows to get all nodes reachable from a starting point "sourceId"
         * at distance at most numberOfHops.
         * @param sourceId
         * @param numberOfHops
         * @return 
         */
        public Set<Integer> getAllReachableNodes(int sourceId, int numberOfHops);
        
        /**
         * Returns the overall number of triples in the system.
         * @return 
         */
        public int numberOfTriples();
        
        /**
         * Get a normalized predicate frequency for a given resource and an
         * incoming predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getNormalizedInPredFreq(int nodeId, String predicateURI);
        
        /**
         * Get a normalized predicate frequency for a given resource and an
         * outgoing predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getNormalizedOutPredFreq(int nodeId, String predicateURI);
        
        /**
         * Returns the inverted triple frequency.
         * @param predicateURI
         * @return 
         */
        public double getInvertedTripleFrequency(String predicateURI);
        
        /**
         * Returns the incoming PFITF of a predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getInPFITF(int nodeId, String predicateURI);
        
        /**
         * Returns the outgoing PFITF of a predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getOutPFITF(int nodeId, String predicateURI);
        
        /**
         * To get the predicate informativeness.
         * @param statement
         * @return 
         */
        public double getPredicateInformativeness(String statement);
        
        /**
         * Path informativeness (builds upon predicate informativeness).
         * @param statementsPath
         * @return 
         */
        public double getPathInformativeness(List<String> statementsPath);
        
        /**
         * Finds all path from sourceId to targetId within a given reachability.
         * @param sourceId
         * @param targetId
         * @param reachability
         * @return
         */
        public Set<List<String>> findAllPaths(int sourceId, int targetId, int reachability);
        
        /**
         * Computes PageRank With Priors.
         * Vertex priors: uniform distribution over all vertices.
         * Uniform distribution over all outgoing edges.          
         * @param userId
         * @param numMaxIterations 
         * @param alpha 
         */
        public void pageRankWithPriorsUniform(int userId, int numMaxIterations,
                double alpha);
        
        /**
         * Computes K Step Markov Centrality.
         * Vertex priors: uniform distribution over all vertices.
         * Uniform distribution over all outgoing edges.
         * @param userId
         * @param numberOfSteps
         * @param numMaxIterations
         */
        public void ksmcUniform(int userId, int numberOfSteps, 
                int numMaxIterations);
        
        /**
         * Computes PageRank With Priors.
         * Vertex priors: uniform distribution over all vertices.
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numMaxIterations 
         * @param alpha 
         */
        public void pageRankWithPriorsUniformEdgesSumOne(int userId, int numMaxIterations,
                double alpha);
        
        /**
         * Computes K Step Markov Centrality.
         * Vertex priors: uniform distribution over all vertices.
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numberOfSteps
         * @param numMaxIterations
         */
        public void ksmcUniformEdgesSumOne(int userId, int numberOfSteps, 
                int numMaxIterations);
        
        /**
         * Computes PageRank With Priors.
         * Vertex priors: the items the user liked. Each of these items gets the weight (1 / #items the user liked).
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numMaxIterations 
         * @param alpha 
         */
        public void pageRankWithPriorsLikesEdgesSumOne(int userId, int numMaxIterations,
                double alpha);
        
        /**
         * Computes K Step Markov Centrality.
         * Vertex priors: the items the user liked. Each of these items gets the weight (1 / #items the user liked).
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numberOfSteps
         * @param numMaxIterations
         */
        public void ksmcLikesEdgesSumOne(int userId, int numberOfSteps, 
                int numMaxIterations);
        
        /**
         * Computes Reword.
         * @param nodeId1
         * @param nodeId2
         * @return 
         */
        public double computeRewordRelatedness(int nodeId1, int nodeId2);
        
        /**
         * Compute the relatedness of the two nodes provided with the Machine Learning Algorithm.
         * @param indexOfNode1
         * @param indexOfNode2
         * @return
         */
		public double computeMachineLearningRelatedness(int indexOfNode1, int indexOfNode2);
        
        /**
         * It returns the score of a node computed based on K Step Markov 
         * Centrality algorithm. 
         * The value returned should be a value between 0 and 1, i.e. the value
         * should be normalized according to the maximum value found among the
         * nodes in the target set.
         * @param resId
         * @return 
         */
        public double getKsmVertexScore(int resId);
        
        /**
         * It returns the score of a node computed based on PageRanking with
         * Priors algorithm.
         * The value returned should be a value between 0 and 1, i.e. the value
         * should be normalized according to the maximum value found among the
         * nodes in the target set.
         * @param resId
         * @return 
         */
        public double getPrpVertexScore(int resId);
        
        /**
         * Gets all vertices of a subgraph built from the complete graph from 
         * all nodes connected to the nodeId.
         * @param nodeId
         * @return 
         */
        public Set<Integer> getSubgraphVertices(int nodeId);
        
        
        //FOR DEBUGGING PURPOSES       
        public Map<String, Integer> getPredicateOccurrenciesMap();

}
