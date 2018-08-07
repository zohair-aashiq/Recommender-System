/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.paradigm;

import java.util.Set;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;

/**
 * Generic recommender model.
 */
public interface Recommender {
        /**
         * Builds a static model for producing recommendations if the preprocessing
         * flag is set to true.
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException
         */
        public void preprocess() throws RecommenderException;
        
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
                throws RecommenderException;
        
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
                throws RecommenderException;
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
        public RatedResource[] getTopRecommendations(String userURI, 
                int size, boolean includeConsumedItems) 
                        throws RecommenderException;
        
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
                Set<String> candidatesURIs) throws RecommenderException;
        
        /**
         * Releases resources that were being used.
         */
        public void releaseResources();
        
        //FOR DEBUGGING PRUPOSES
        public DataManager getDataManager();
        
        public RecConfig getRecConfig();
}
