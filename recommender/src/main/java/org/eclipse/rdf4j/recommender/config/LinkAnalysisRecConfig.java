/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.impl.GraphBasedDataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEdgeDistribution;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.parameter.RecPriorsDistribution;

/**
 * This class represents a configuration of a cross domain 
 * recommender based on link analysis techniques such as PageRank or 
 * k-Step-Markov-Centrality.
 */
public class LinkAnalysisRecConfig extends CrossDomainRecConfig {
                
        /*-------------------------*
	 * Configuration variables *
	 *-------------------------*/
        /**
         * The following settings are generic for all link analysis approaches.
         */ 
        private RecGraphOrientation graphOrientation = RecGraphOrientation.DIRECTED; //default
        private RecPriorsDistribution priorsDistribution = RecPriorsDistribution.UNIFORM; //default
        private RecEdgeDistribution edgesDistribution =  RecEdgeDistribution.UNIFORM; //default
        private int maxIterations = -1;
        
        /**
         * The following two settings are specific to 
         * CROSS_DOMAIN_PAGERANK_WITH_PRIORS
         */
        private double alpha = -1.0; //Default

        /**
         * The following two settings are specific to 
         * CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY
         */
        private int kMarkovSteps = -1;
        
        /**
         * The following are specific to
         * CROSS_DOMAIN_MACHINE_LEARNING
         */
        private String baseURI = null;
        private double cosineSimilarityThreshold = 0.5;
        private String featureFile = "feature.arff";
                
        /*--------------*
	 * Constructors *
	 *--------------*/
        
        public LinkAnalysisRecConfig (String configName) {
            super(configName);
        }
        
        public LinkAnalysisRecConfig (LinkAnalysisRecConfig config) {
                super(config);
        }
        
        /*---------*
	 * Methods *
	 *---------*/

        public RecGraphOrientation getGraphOrientation() {
                return graphOrientation;
        }

        public void setGraphOrientation(RecGraphOrientation graphOrientation) {
                this.graphOrientation = graphOrientation;
        }       
        
        public RecPriorsDistribution getPriorsDistribution() {
                return priorsDistribution;
        }

        public void setPriorsDistribution(RecPriorsDistribution priorsDistribution) {
                this.priorsDistribution = priorsDistribution;
        }

        public RecEdgeDistribution getEdgesDistribution() {
                return edgesDistribution;
        }

        public void setEdgesDistribution(RecEdgeDistribution edgesDistribution) {
                this.edgesDistribution = edgesDistribution;
        }
                       
        public int getMaxIterations() {
                return maxIterations;
        }

        public void setMaxIterations(int maxIterations) {
                this.maxIterations = maxIterations;
        }

        public double getAlpha() {
                return alpha;
        }

        public void setAlpha(double alpha) {
                this.alpha = alpha;
        }
        
        public int getkMarkovSteps() {
                return kMarkovSteps;
        }

        public void setkMarkovSteps(int kMarkovSteps) {
                this.kMarkovSteps = kMarkovSteps;
        }
        
        public String getBaseURI(){
    			return baseURI;
        }

        public void setBaseURI(String baseURI){
        		this.baseURI = baseURI;
        }
        
        public double getCosineSimilarityThreshold(){
        		return cosineSimilarityThreshold;
        }
        
        public void setCosineSimilarityThreshold(double threshold){
        		this.cosineSimilarityThreshold = threshold;
        }
        
        public String getFeatureFileName(){
        		return featureFile;
        }
        
        public void setFeatureFileName(String filename){
        		this.featureFile = filename;
        }
        
        @Override
        public DataManager validateConfiguration() throws RecommenderException{
                super.validateConfiguration();
                
                if (getRecParadigm() != RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY && 
                        getRecParadigm() != RecParadigm.CROSS_DOMAIN_REWORD && 
                        getRecParadigm() != RecParadigm.CROSS_DOMAIN_PAGERANK_WITH_PRIORS &&
                		getRecParadigm() != RecParadigm.CROSS_DOMAIN_MACHINE_LEARNING){
                                throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S APPROACH");
                }
                
                if (getRecStorage() != RecStorage.EXTERNAL_GRAPH) {
                        throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S STORAGE");
                }
                
                if (getRecParadigm() == RecParadigm.CROSS_DOMAIN_PAGERANK_WITH_PRIORS) {
                        if (getMaxIterations() < 1 || getAlpha() < 0.0 || getAlpha() > 1.0) {
                                throw new RecommenderException("PAGERANK WITH PRIORS REQUIRES A POSTIVE NUMBER OF"
                                        + "MAX ITERATIONS. ALSO ALPHA SHOULD BE IN THE RANGE [0,1]");
                        }
                }
                
                if (getRecParadigm() == RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY ) {
                        if (getkMarkovSteps() < 1 || getMaxIterations() < 1 || getMaxIterations() < getkMarkovSteps()) {
                                throw new RecommenderException("K STEP MARKOV CENTRALITY REQUIRES A POSTIVE "
                                        + "NUMBER OF STEPS AND MAX ITERATIONS. ALSO MAX ITERATIONS > NUMBER OF STEPS");
                        }
                }
                
                if (getRecParadigm() == RecParadigm.CROSS_DOMAIN_MACHINE_LEARNING) {
                		if (getBaseURI() == null){
                				throw new RecommenderException("MACHINE LEARNING BASED RECOMMENDER REQUIRES A BASE URI" 
                						+ " TO USE IN SPARQL QUERIES.");
                		}
                		
                		if (getCosineSimilarityThreshold() < 0.0 || getCosineSimilarityThreshold() > 1.0) {
                				throw new RecommenderException("MACHINE LEARNING BASED RECOMMENDER REQUIRES A COSINE" + 
                						" SIMILARITY THRESHOLD IN THE RANGE [0, 1]");
                		}
                }
                
                /* Removed because there are defaults */
                /*
                if (getPriorsDistribution() == null || getEdgesDistribution() == null ) {
                        throw new RecommenderException("PLEASE SET THE PRIORS AND EDGE DISTRIBUTIONS FOR "
                                + "THIS KIND OF APPROACHES");
                }
                */
                System.out.println("ZAID:: in LARecConfig: ");
                return new GraphBasedDataManager(this);
        }
        
}
