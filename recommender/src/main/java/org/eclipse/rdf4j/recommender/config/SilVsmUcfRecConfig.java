/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.impl.IndexBasedDataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;

/**
 * This class represents the configuration of a collaborative recommender
 * based on the VSM implemented with scaled inverted lists. This requires a 
 * number of top ratings to be pre-processed in addition to the configuration
 * required by the standard inverted lists.
 */
public class SilVsmUcfRecConfig extends VsmCfRecConfig {
                
        /*-------------------------*
	 * Configuration variables *
	 *-------------------------*/
    
        private int numberOfTopRatings = 0; //DEFAULT
        
        /*--------------*
	 * Constructors *
	 *--------------*/
        
        public SilVsmUcfRecConfig (String configName) {
            super(configName);
        }
                
        public SilVsmUcfRecConfig (SilVsmUcfRecConfig config) {
                super(config);
                numberOfTopRatings = config.getNumberOfTopRatings();
        }
        
        /*---------*
	 * Methods *
	 *---------*/                
        
        /**
         * Gets the number of top ratings used for the preprocessing phase.
         * @return
         */
        public int getNumberOfTopRatings() {
                return numberOfTopRatings;
        }

        /**
         * Sets the number of top ratings used for the preprocessing phase.
         * @param numberOfTopRatings
         */
        public void setNumberOfTopRatings(int numberOfTopRatings) {
                this.numberOfTopRatings = numberOfTopRatings;
        }
        
        @Override
        public DataManager validateConfiguration() throws RecommenderException{
                super.validateConfiguration();
                
                if (getRecStorage() != RecStorage.SCALED_INVERTED_LISTS) {
                        throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S STORAGE");
                }
                
                if (hasToPreprocess() == false) 
                        throw new RecommenderException("THIS APPROACH REQUIRES PREPROCESSING");
                      
                if (numberOfTopRatings < 0)
                        throw new RecommenderException("CONFIGURATION NOT VALID. NUMBER OF TOP RATINGS CANNOT BE NEGATIVE");
                return new IndexBasedDataManager(this);
        }
}
