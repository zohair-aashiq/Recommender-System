/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.paradigm.collaborative;

import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.paradigm.AbstractRecommender;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.repository.sail.SailRepository;

/**
 * This class represents a collaborative filtering recommender.
 */
public class CfRecommender extends AbstractRecommender{                
    
        /*-------------*
	 * Constructor *
	 *-------------*/
        
        public CfRecommender(SailRepository sailRep, DataManager dataManager)
                        throws RecommenderException {
            
                //Calling superclass' constructor.
                super(sailRep, dataManager);
        }
        
        @Override
        public double predictRating(String userURI, String itemURI) throws RecommenderException{
                Set<RatedResource> neighborhood = null;
                //Variables needed in the inner loop                                                                        
                Double nbSim = 0.0;
                double ratAvgOfNeighbor = 0.0; 
                double sumOfNbSim = 0.0;
                double sumWeightedVotes = 0.0;
                                
                if (getDataManager().hasPreprocessed()) {
                        if (getRecConfig().getRecParadigm() == RecParadigm.USER_COLLABORATIVE_FILTERING) {
                                //Case 1) with ratings
                                if (getRecConfig().getRecEntity(RecEntity.RATING) != null) {
                                        //The formula used here is shown in the slides of DAQL - SS15 
                                        double ratAvgOfActiveUser = getDataManager().getRatingAverageOfUser(userURI);                                                                                                                           

                                        neighborhood = getDataManager().getNeighbors(userURI);                          

                                        for (RatedResource neighbor: neighborhood) {
                                                ratAvgOfNeighbor = getDataManager().getRatingAverageOfUser(neighbor.getResource());
                                                nbSim = neighbor.getRating();
                                                Set<RatedResource> nbRatResources = getDataManager().getRatedResources(neighbor.getResource());

                                                for (RatedResource nbRr : nbRatResources) {
                                                        if (nbRr.getResource().equals(itemURI)) {
                                                                sumOfNbSim += Math.abs(neighbor.getRating());
                                                                sumWeightedVotes += nbSim * (nbRr.getRating() - ratAvgOfNeighbor);
                                                        }
                                                }                                        
                                        }
                                        if (sumWeightedVotes == 0.0 || sumOfNbSim == 0.0) return 0.0;

                                        return ratAvgOfActiveUser +
                                                (sumWeightedVotes / sumOfNbSim);
                                }
                                //Case 2) only positive feedback is provided.
                                else { 
                                        neighborhood = getDataManager().getNeighbors(userURI);  
                                        RatedResource recCandidate = new RatedResource(itemURI, 1.0);
                                        double neighborsCounter = 0.0;
                                        
                                        for (RatedResource neighbor: neighborhood) {
                                                nbSim = neighbor.getRating();
                                                Set<RatedResource> nbRatResources = getDataManager().getRatedResources(neighbor.getResource());     
                                                
                                                if (nbRatResources.contains(recCandidate)) {
                                                        neighborsCounter++;
                                                        sumOfNbSim += nbSim;
                                                }   
                                        }
                                        if (neighborsCounter == 0.0) return 0.0;
                                        return sumOfNbSim / neighborsCounter;
                                }
                        }
                }
                //TODO if system has not preprocessed, neighborhood should be computed
                //on-the-fly.
                return -1.0;
        }           
}
