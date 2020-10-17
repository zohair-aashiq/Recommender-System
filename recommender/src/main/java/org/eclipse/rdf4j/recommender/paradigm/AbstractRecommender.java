/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.paradigm;

import com.google.common.collect.MinMaxPriorityQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.impl.SparkDataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.util.RatedResourceRatingComparator;
import org.eclipse.rdf4j.repository.sail.SailRepository;

/**
 * This abstract class represents a generic recommender.
 * Default constructor and some methods are implemented here.
 */
public abstract class AbstractRecommender implements Recommender{
    
        /**
         * To compare rated resources only based on the "rating" 
         * without taking into account the rated item.
         */ 
        private final Comparator<RatedResource> comparatorOfRrBasedOnRating = new RatedResourceRatingComparator();
                
        /*-----------------*
	 * Data Structures *
	 *-----------------*/
    
        private final DataManager dataManager;
        private final RecConfig config; 

        /*-------------*
	 * Constructor *
	 *-------------*/
        
        public AbstractRecommender(SailRepository sailRep, DataManager dataManager)
                        throws RecommenderException {
				System.out.println("ZAID:: in AbstractRecommender");
                this.dataManager = dataManager;
                this.config = dataManager.getRecConfig();
                dataManager.init(sailRep);
        }
                
        /*---------*
	 * Methods *
	 *---------*/
        
        @Override
        public void preprocess() throws RecommenderException {
                dataManager.preprocess();
        }
        
        /*
        @Override
        public boolean isRelevant(String userURI, String itemURI) throws RecommenderException {
                double userAverage = getDataManager().getRatingAverageOfUser(userURI);
                double predictedRating = predictRating(userURI, itemURI);
                return predictedRating >= userAverage;
        }
        */

        @Override
        public RatedResource[] getTopRecommendations(String userURI, int size, boolean includeConsumedItems) 
                        throws RecommenderException {

                Set<String> consumedURIs = getDataManager().getConsumedResources(userURI);
                Set<String> candidatesURI  = getDataManager().getRecCandidates(userURI);  

                if (!includeConsumedItems) {
                        candidatesURI.removeAll(consumedURIs);
                }

                return getTopRecommendations(userURI, size, candidatesURI);
        }

        @Override
        public RatedResource[] getTopRecommendations(String userURI, int size, 
                        Set<String> candidatesURI) 
                        throws RecommenderException {
            
                RatedResource[] topK = null;                
                MinMaxPriorityQueue<RatedResource> topKQueue = null;
                
                if (candidatesURI == null) {
                        throw new RecommenderException("You candidate set is NULL");
                }
                if (candidatesURI.isEmpty()) return new RatedResource[size];
                
                RatedResource ratRes = null;
                                
                topKQueue = MinMaxPriorityQueue.orderedBy(comparatorOfRrBasedOnRating)
                        .maximumSize(size).create();

                for (String candidateURI: candidatesURI) {                                       
                        ratRes =  new RatedResource(candidateURI, predictRating(userURI, candidateURI));
                        topKQueue.add(ratRes);
                }                                
                topK = topKQueue.toArray(new RatedResource[size]);
                Arrays.sort(topK, comparatorOfRrBasedOnRating);
                              
                return topK;
        }
        
        @Override
        public void releaseResources() {
            dataManager.releaseResources();
        }
                        
        @Override
        public RecConfig getRecConfig(){
                return config;
        }
        
        @Override
        public DataManager getDataManager(){
                return dataManager;
        }
        
}
