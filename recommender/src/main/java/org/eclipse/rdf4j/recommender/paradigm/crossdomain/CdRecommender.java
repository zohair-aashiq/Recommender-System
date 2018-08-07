/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.paradigm.crossdomain;

import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.paradigm.AbstractRecommender;
import org.eclipse.rdf4j.repository.sail.SailRepository;

/**
 * This class represents a cross domain filtering recommender.
 */
public final class CdRecommender extends AbstractRecommender{

        /*-------------*
	 * Constructor *
	 *-------------*/
        
        public CdRecommender(SailRepository sailRep, DataManager dataManager)
                        throws RecommenderException {
                //Calling superclass' constructor.
                super(sailRep, dataManager);
        }
        
        @Override
       public double predictRating(String userURI, String itemURI)  
                        throws RecommenderException {
				System.out.println("ZAID:: in CdRecommender 1");
                            
                switch(getRecConfig().getRecParadigm()) {
                        case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY:
                        case CROSS_DOMAIN_PAGERANK_WITH_PRIORS: 
                                //This executes the K Step Markov Centrality activating the
                                //resources the user likes.
                                return getDataManager().getResRelativeImportance(userURI, itemURI);
                        case CROSS_DOMAIN_REWORD:
                                //This executes the approach base on Reword.
                                return getDataManager().getResRelativeImportance(userURI, itemURI);
                        case CROSS_DOMAIN_MACHINE_LEARNING:
                				System.out.println("ZAID:: in CdRecommender 2");
                        		//This executes the machine learning based approach.
                        		return getDataManager().getResRelativeImportance(userURI, itemURI);
                        //break;
                }
                return 0.0;
        }
        
       /*
        @Override
        public boolean isRelevant(String userURI, String itemURI) throws RecommenderException {
                //Since the scores are normalized, we can use a value between 
                //0 and 1 as threshold to decide whether a resource is relevant.
                return predictRating(userURI, itemURI) > 0.5;
        }
       */
        
        @Override
        public RatedResource[] getTopRecommendations(String userURI, int size, 
                        Set<String> candidatesURI) 
                        throws RecommenderException {
                RatedResource[] topK = super.getTopRecommendations(userURI, size, candidatesURI);                 
                RatedResource[] normalizedTopK = new RatedResource[topK.length];
                double maxScore = topK[0].getRating(); //Highest score
                
                //We normalize based on this max value                
                for (int i = 0; i < topK.length; i++) {
                        if(topK[i] == null) {
                                normalizedTopK[i] = null;
                        } else {
                                normalizedTopK[i] = 
                                        new RatedResource(topK[i].getResource(), 
                                                topK[i].getRating() / maxScore);                                                     
                        }
                }                                              
                return normalizedTopK;
        }
}
