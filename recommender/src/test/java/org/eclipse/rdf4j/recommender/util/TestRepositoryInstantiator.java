/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rdf4j.recommender.config.LinkAnalysisRecConfig;
import org.eclipse.rdf4j.recommender.config.SilVsmCfConfigWithRatingsTest;
import org.eclipse.rdf4j.recommender.config.SilVsmUcfRecConfig;
import org.eclipse.rdf4j.recommender.config.VsmCfRecConfig;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.paradigm.collaborative.CfRecWithRatingsTest;
import org.eclipse.rdf4j.recommender.parameter.RecEdgeDistribution;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecPriorsDistribution;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Static class that helps in creating repositories with different test datasets.
 * In this way all JUnit tests have a centralized access to these repositories.
 */
public final class TestRepositoryInstantiator {
        /**
         * Private constructor to have a static class behavior.
         */
        private TestRepositoryInstantiator() {};
        
        private static final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();                
        
        /**
         * Creates a repository without a configuration.
         * @return 
         */
        public static SailRecommenderRepository createEmptyTestRepository() {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/moviesFromBook.ttl";
                        String baseURI = "http://example.org/movies#";                        
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                } catch (Exception ex) {
                        Logger.getLogger(SilVsmCfConfigWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return recRepository;
        }   
        
        /**
         * Creates a repository without a configuration. (Likes case)
         * @return 
         */
        public static SailRecommenderRepository createEmptyLikesTestRepository() {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/likes.ttl";
                        String baseURI = "http://example.org/graph#";                      
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                } catch (Exception ex) {
                        Logger.getLogger(SilVsmCfConfigWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return recRepository;
        }
        
        /**
         * Creates a repository with a collaborative approach.
         * Dataset: Book
         * @param sizeOfNeighborhood
         * @param stor
         * @param topRatingsNumber
         * @return 
         */        
        public static SailRecommenderRepository createBookUbCfRecAndPreprocess(
                        RecStorage stor, int sizeOfNeighborhood, int topRatingsNumber){
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/moviesFromBook.ttl";
                        String baseURI = "http://example.org/movies#";                 
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        
                        VsmCfRecConfig configuration = null;
                        if (stor == RecStorage.INVERTED_LISTS) {
                                configuration = new VsmCfRecConfig("config1");          
                        } else if (stor == RecStorage.SCALED_INVERTED_LISTS) {
                                configuration = new SilVsmUcfRecConfig("config1");
                        } else if (stor == RecStorage.SPARK) {
                                configuration = new VsmCfRecConfig("config2");
                        }
                        
                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setRatGraphPattern(
                                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?user");
                        configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                        configuration.setRecEntity(RecEntity.RATING, "?rating");

                        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);                        
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setSimMetric(RecSimMetric.COSINE);
                        configuration.setRecStorage(stor);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);
                        configuration.setNeighborhoodSize(sizeOfNeighborhood);
                        
                        //This configuration has the default number of top ratings
                        //which is 0.
                        if (stor == RecStorage.SCALED_INVERTED_LISTS && topRatingsNumber > 0) {
                                ((SilVsmUcfRecConfig) configuration).setNumberOfTopRatings(topRatingsNumber);
                        }

                        recRepository.loadRecConfiguration(configuration);
                } catch (IOException ex) { 
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RDFParseException ex) {
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RepositoryException ex) {
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RecommenderException ex) { 
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } 
                return recRepository;
        }
        
        /**
         * Creates a repository with a collaborative approach.
         * Dataset: RecSPARQL
         * @param sizeOfNeighborhood
         * @param stor
         * @return 
         */        
        public static SailRecommenderRepository createRecSparqlMoviesUbCfRecAndPreprocess(RecStorage stor, int sizeOfNeighborhood){
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/movieRecsparql.ttl";
                        String baseURI = "http://example.org/movies#";                      
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        VsmCfRecConfig configuration = null;
                        if (stor == RecStorage.INVERTED_LISTS) {
                                configuration = new VsmCfRecConfig("config2");                        
                        } else if (stor == RecStorage.SCALED_INVERTED_LISTS) {
                                configuration = new SilVsmUcfRecConfig("config2");
                        } else if (stor == RecStorage.SPARK) {
                                configuration = new VsmCfRecConfig("config2");
                        }
                                                
                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setRatGraphPattern(
                                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
                        );
                        configuration.setRecEntity(RecEntity.USER, "?user");
                        configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                        configuration.setRecEntity(RecEntity.RATING, "?rating");
                        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setSimMetric(RecSimMetric.COSINE);
                        configuration.setRecStorage(stor);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);
                        configuration.setNeighborhoodSize(sizeOfNeighborhood);
                        
                        if (stor == RecStorage.SCALED_INVERTED_LISTS) {
                                //((SilVsmUcfRecConfig) configuration).getNumberOfTopRatings();
                        }
                        
                        recRepository.loadRecConfiguration(configuration); 
                } catch (IOException ex) { 
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RDFParseException ex) {
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RepositoryException ex) {
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RecommenderException ex) { 
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return recRepository;
        }    
        
        /**
         * Creates a repository with a collaborative approach.
         * Dataset: RecSPARQL
         * @param sizeOfNeighborhood
         * @param stor
         * @return 
         */        
        public static SailRecommenderRepository createLikesBasedUbCfRecAndPreprocess(RecStorage stor, int sizeOfNeighborhood){
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/likes.ttl";
                        String baseURI = "http://example.org/graph#";                 
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        VsmCfRecConfig configuration = null;
                        if (stor == RecStorage.INVERTED_LISTS) {
                                configuration = new VsmCfRecConfig("config2");                        
                        } else if (stor == RecStorage.SCALED_INVERTED_LISTS) {
                                configuration = new SilVsmUcfRecConfig("config2");
                        } else if (stor == RecStorage.SPARK) {
                                configuration = new VsmCfRecConfig("config2");
                        }
                                                
                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                "?user <http://example.org/graph#likes> ?item"
                        );
                        configuration.setRecEntity(RecEntity.USER, "?user");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?item");
                        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setSimMetric(RecSimMetric.COSINE);
                        configuration.setRecStorage(stor);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);
                        configuration.setNeighborhoodSize(sizeOfNeighborhood);
                        
                        if (stor == RecStorage.SCALED_INVERTED_LISTS) {
                                //((SilVsmUcfRecConfig) configuration).getNumberOfTopRatings();
                        }
                        
                        recRepository.loadRecConfiguration(configuration); 
                } catch (IOException ex) { 
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RDFParseException ex) {
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RepositoryException ex) {
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RecommenderException ex) { 
                        Logger.getLogger(CfRecWithRatingsTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return recRepository;
        }
        
        /**
         * Creates a repository with a  K Step Markov Centrality cross-domain 
         * approach.
         * Dataset: Paper Algorithms for Estimating Relative Importance In
         * Networks, White et al.
         * @param numOfStep
         * @param maxIterations
         * @return 
         */      
        public static SailRecommenderRepository createPaperDatasetKsMcCdRecAndPreprocess(int numOfStep, int maxIterations){
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/kstepmarkov.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                "?userNode <http://example.org/graph#likes> ?resNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?resNode");
                        configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                                "?res <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/graph#Source>");
                        configuration.setRecEntity(RecEntity.TARGET_DOMAIN, 
                                "?res <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/graph#Target>");

                        configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY);
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setkMarkovSteps(numOfStep);
                        configuration.setMaxIterations(maxIterations);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                } catch (RepositoryException ex) {
                } catch (IOException ex) {
                } catch (RDFParseException ex) {
                } 
                return recRepository;
        }       
        
        /**
         * Creates a repository with a  K Step Markov Centrality cross-domain 
         * approach.
         * Dataset: Hand created one. It seems to verify the process of building
         * a subgraph for each user.
         * @param numOfStep
         * @param maxIterations
         * @return 
         */      
        public static SailRecommenderRepository createCustomizedDatasetKsMcCdRecAndPreprocess(int numOfStep, int maxIterations) {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/kstepmarkov_2.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                "?userNode <http://example.org/graph#likes> ?resNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?resNode");
                        configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                                "?resNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/graph#Source>");
                        configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                                "?resNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/graph#Target>");

                        configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY);
                        configuration.setPriorsDistribution(RecPriorsDistribution.UNIFORM);
                        configuration.setEdgesDistribution(RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1);
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setkMarkovSteps(numOfStep);
                        configuration.setMaxIterations(maxIterations);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                } catch (IOException ex) { 
                } catch (RDFParseException ex) {
                } catch (RepositoryException ex) {
                } 
                return recRepository;
        }
        
        /**
         * Creates a repository with a  K Step Markov Centrality cross-domain 
         * approach.
         * Dataset: Hand created one. It seems to verify the process of building
         * a subgraph for each user.
         * @param numOfStep
         * @param maxIterations
         * @return 
         */      
        public static SailRecommenderRepository createCustomizedDatasetKsMcCdRecAndPreprocess2(int numOfStep, int maxIterations, boolean preprocess) {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/rewordTest3.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                "?userNode <http://example.org/graph#likes> ?resNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?resNode");
                        configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                                "?resNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/graph#Source>");
                        configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                                "?resNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/graph#Target>");

                        configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY);
                        configuration.setPriorsDistribution(RecPriorsDistribution.UNIFORM);
                        configuration.setEdgesDistribution(RecEdgeDistribution.UNIFORM);
                        configuration.preprocessBeforeRecommending(preprocess);
                        configuration.setkMarkovSteps(numOfStep);
                        configuration.setMaxIterations(maxIterations);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                } catch (IOException ex) { 
                } catch (RDFParseException ex) {
                } catch (RepositoryException ex) {
                } 
                return recRepository;
        }
        
        /**
         * Creates a repository with a  K Step Markov Centrality cross-domain 
         * approach.
         * Dataset: Hand created one. It seems to verify the process of building
         * a subgraph for each user.
         * @param numOfStep
         * @param maxIterations
         * @return 
         */      
        public static SailRecommenderRepository createCustomizedDatasetKsMcSdRecAndPreprocess2(int numOfStep, int maxIterations, boolean preprocess) {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/rewordTest3sd.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                "?userNode <http://example.org/graph#likes> ?resNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?resNode");
                        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                        configuration.setPriorsDistribution(RecPriorsDistribution.UNIFORM);
                        configuration.setEdgesDistribution(RecEdgeDistribution.UNIFORM);
                        configuration.preprocessBeforeRecommending(preprocess);
                        configuration.setkMarkovSteps(numOfStep);
                        configuration.setMaxIterations(maxIterations);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                } catch (IOException ex) { 
                } catch (RDFParseException ex) {
                } catch (RepositoryException ex) {
                } 
                return recRepository;
        }
        
        /**
         * Creates a repository with a Reword cross-domain based approach.
         * Dataset: Hand created one. Based on users John and Maria.
         * @return 
         */      
        public static SailRecommenderRepository createUsersDatasetRwCdRecAndPreprocess() {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/rewordTest.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                    "?userNode <http://example.org/graph#livesIn> ?placeNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?placeNode");
                        //TODO
                        //Modify this later
                        configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                                " ");                        
                        configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                                "?resNode <http://example.org/graph#livesIn> <http://example.org/graph#Paris>");

                        configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_REWORD);
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                } catch (IOException ex) { 
                } catch (RDFParseException ex) {
                } catch (RepositoryException ex) {
                } 
                return recRepository;
        }
        
        /**
         * Creates a repository with a Reword cross-domain based approach.
         * Dataset: Hand created one to test if candidate rec items are 
         * optimized for each user.
         * @return 
         */      
        public static SailRecommenderRepository createUsersDatasetRwCdRecAndPreprocess2(boolean preprocess) {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/rewordTest3.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                "?userNode <http://example.org/graph#likes> ?itemNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?itemNode");
                        //TODO
                        //Modify this later
                        configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                                " ");                        
                        configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                                "?resNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/graph#Target>");

                        configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_REWORD);
                        configuration.preprocessBeforeRecommending(preprocess);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                } catch (IOException ex) { 
                } catch (RDFParseException ex) {
                } catch (RepositoryException ex) {
                } 
                return recRepository;
        }
        
        /**
         * Creates a repository with a Reword single-domain based approach.
         * Dataset: Hand created one to test if candidate rec items are 
         * optimized for each user.
         * @return 
         */      
        public static SailRecommenderRepository createUsersDatasetRwSdRecAndPreprocess2(boolean preprocess) {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/rewordTest3sd.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                "?userNode <http://example.org/graph#likes> ?itemNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?itemNode");
                        // TODO is below true? - kemal
                        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                        configuration.preprocessBeforeRecommending(preprocess);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                    System.out.println(ex.getMessage());
                } catch (IOException ex) { 
                    System.out.println(ex.getMessage());
                } catch (RDFParseException ex) {
                    System.out.println(ex.getMessage());
                } catch (RepositoryException ex) {
                    System.out.println(ex.getMessage());
                } 
                return recRepository;
        }
        
        /**
         * Creates a repository with a Reword cross-domain based approach.
         * Dataset: Hand created one. Based on users John and Maria.
         * @return 
         */      
        public static SailRecommenderRepository createRoyDatasetRw() {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/rewordTestRoy.ttl";
                        String baseURI = "http://example.org/graph#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        //RECOMMENDATION                        
                        //One needs first to create a configuration for the recommender.
                        LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

                        //NEW thing: 
                        //Set a recommendation configuration. Right now only one configuration is
                        //possible. In the future more configurations should be possible.
                        configuration.setPosGraphPattern(
                                    "?userNode <http://example.org/graph#livesIn> ?placeNode"
                        );

                        configuration.setRecEntity(RecEntity.USER, "?userNode");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?placeNode");
                        //TODO
                        //Modify this later
                        configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                                " ");                        
                        configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                                "?resNode <http://example.org/graph#livesIn> <http://example.org/graph#Paris>");

                        configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_REWORD);
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                        configuration.setDecimalPlaces(numberOfDecimalPlaces);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                    System.out.println(ex.getMessage());
                } catch (IOException ex) { 
                    System.out.println(ex.getMessage());
                } catch (RDFParseException ex) {
                    System.out.println(ex.getMessage());
                } catch (RepositoryException ex) {
                    System.out.println(ex.getMessage());
                } 
                return recRepository;
        }
        
        /**
         * This dataset was making some trouble in the test suite of the evaluation
         * module, in the sense that results of the recommender changed every 
         * time the test case was executed
         */      
        public static SailRecommenderRepository createNonDeterministicDataset() {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                int numberOfDecimalPlaces = 3;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/moviesLikes.ttl";
                        String baseURI = "http://example.org/movies#";
                        
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        VsmCfRecConfig configuration = new VsmCfRecConfig("config1");
                        configuration.setPosGraphPattern(
                                "?user <http://example.org/movies#hasLiked> ?movie "
                        );
                        configuration.setRecEntity(RecEntity.USER, "?user");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?movie");

                        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                        configuration.preprocessBeforeRecommending(true);
                        configuration.setSimMetric(RecSimMetric.COSINE);
                        configuration.setRecStorage(RecStorage.INVERTED_LISTS);
                        configuration.setDecimalPlaces(3);
                        configuration.setNeighborhoodSize(4);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                    System.out.println(ex.getMessage());
                } catch (IOException ex) { 
                    System.out.println(ex.getMessage());
                } catch (RDFParseException ex) {
                    System.out.println(ex.getMessage());
                } catch (RepositoryException ex) {
                    System.out.println(ex.getMessage());
                } 
                return recRepository;
        }
}
