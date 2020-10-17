/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.repository;

import java.util.Set;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.paradigm.Recommender;
import org.eclipse.rdf4j.recommender.paradigm.collaborative.CfRecommender;
import org.eclipse.rdf4j.recommender.paradigm.crossdomain.CdRecommender;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;

/**
 * An implementation of the {@link Repository} interface that operates on a
 * (stack of) {@link Sail Sail} object(s). The behaviour of the repository is
 * determined by the Sail stack that it operates on; for example, the repository
 * will only support RDF Schema or OWL semantics if the Sail stack includes an
 * inferencer for this.
 * <p>
 * Creating a repository object of this type is very easy. For example, the
 * following code creates and initializes a main-memory store with RDF Schema
 * semantics:
 * 
 * <pre>
 * Repository repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
 * repository.initialize();
 * </pre>
 * 
 * Or, alternatively:
 * 
 * <pre>
 * Sail sailStack = new MemoryStore();
 * sailStack = new ForwardChainingRDFSInferencer(sailStack);
 * 
 * Repository repository = new SailRepository(sailStack);
 * repository.initialize();
 * </pre>
 * 
 */
public class SailRecommenderRepository extends SailRepository {
        
        /*--------*
	 * Fields *
	 *--------*/
    
         private RecConfig recConfig = null;
         private Recommender recommender= null;
         

	/*--------------*
	 * Constructors *
	 *--------------*/
         
	/**
	 * Creates a new repository object that operates on the supplied Sail.
	 * 
	 * @param sail
	 *        A Sail object.
	 */
	public SailRecommenderRepository(Sail sail) {
		super(sail);
	}

	/*---------*
	 * Methods *
	 *---------*/      
        /**
         * Loads a recommender configuration. The configuration is specific for
         * the kind of approach or paradigm selected. Inconsistencies in the
         * configuration should result in a RecommenderException.
         * 
         * @param recConfig
         * @throws RecommenderException 
         */
        public void loadRecConfiguration(RecConfig recConfig)
                        throws RecommenderException {
        		System.out.println("ZAID:: in SailRec, loadConfig");
                if (recConfig==null) 
                        throw new RecommenderException("CANNOT LOAD A NULL CONFIGURATION");
                
                this.recConfig = recConfig;

                DataManager dataManager = recConfig.validateConfiguration();
                
                if (recConfig.getRecParadigm() == RecParadigm.CONTENT_BASED) {
                        //TODO
                }

                if (recConfig.getRecParadigm() == RecParadigm.USER_COLLABORATIVE_FILTERING) {
                        recommender =  new CfRecommender(this, dataManager);
                }
                
                if (recConfig.getRecParadigm() == RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY || 
                        recConfig.getRecParadigm() == RecParadigm.CROSS_DOMAIN_PAGERANK_WITH_PRIORS ||
                        recConfig.getRecParadigm() == RecParadigm.CROSS_DOMAIN_REWORD ||
                        recConfig.getRecParadigm() == RecParadigm.CROSS_DOMAIN_MACHINE_LEARNING) {
                		System.out.println("ZAID:: in SailRec, creating recommender");
                        recommender =  new CdRecommender(this, dataManager);
                        System.out.println("ZAID:: in SailRec, created recommender: " + recommender);
                }
                                
                if (recConfig.hasToPreprocess())
                        recommender.preprocess();
        }

        /**
         * Returns the internal built recommender model.
         * @return 
         */
        public Recommender getRecommender() {
               return recommender;
        }
        
        /**
         * Gets the inner configuration.
         * @return 
         */
        public RecConfig getCurrentLoadedConfig(){
                return this.recConfig;
        }
        
        /**
         * Method for getting the predicted rating for a given user, item.
         * If the date doesn't contain ratings but just a like relationship
         * the returned predicted rating is 1 if the user likes 
         * itemURI according to the recommender, 0 otherwise.
         * If your data is of this kind, use preferably the method isRelevant().
         * @param userURI
         * @param itemURI
         * @return 
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException 
         */
        public double predictRating(String userURI, String itemURI) 
                throws RecommenderException {
						System.out.println("ZAID:: in SaiRecommender");
						System.out.println("ZAID:: recommender:" + recommender);
                        return recommender.predictRating(userURI, itemURI);
        }
        
        /**
         * The method returns TRUE if the itemURI is predicted to be relevant
         * for the user. It returns FALSE otherwise.
         * @param userURI
         * @param itemURI
         * @return 
         * @throws org.openrdf.recommender.exception.RecommenderException 
         */
        /*
        public boolean isRelevant(String userURI, String itemURI) 
                        throws RecommenderException {
                return recommender.isRelevant(userURI, itemURI);
        }
        */

        /**
         * Returns Top-k Recommendations for the user as an array of rated 
         * resources. If the flag includeConsumedItems is set to TRUE, then 
         * resources which the user has already consumed are considered to be
         * candidate for recommendations.
         * @param userURI
         * @param size the size of the list
         * @param includeConsumedItems
         * @return 
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException 
         */
        public RatedResource[] getTopRecommendations(String userURI, int size, 
                boolean includeConsumedItems) throws RecommenderException {
            
                return recommender.getTopRecommendations(userURI, size, includeConsumedItems);
        }
        
        /**
         * Returns Top-k Recommendations for the user using a given set of 
         * candidate resources. It returns an array of rated resources.
         * @param userURI
         * @param size
         * @param candidatesURIs
         * @return 
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException 
         */
        public RatedResource[] getTopRecommendations(String userURI, int size, 
                        Set<String> candidatesURIs) throws RecommenderException {
            
                return recommender.getTopRecommendations(userURI, size, candidatesURIs);
        }
        
        /**
         * Releases resources that are being used for recommender. 
         * 
         * This method should be called before destroying SailRecommenderRepository
         * object. 
         */
        public void releaseResources() {
            recommender.releaseResources();
        }
}
