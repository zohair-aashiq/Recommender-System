/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;

/**
 * This abstract class represents a configuration of a collaborative 
 * recommender. This can be used for both user-based and item-based
 * collaborative filtering.
 */
public abstract class CfRecConfig extends GenericRecConfig {
	       
        /*-------------------------*
	 * Configuration variables *
	 *-------------------------*/
        
        private int neighborhoodSize = 20;//DEFAULT       
        
        /*--------------*
	 * Constructors *
	 *--------------*/
        
        public CfRecConfig (String configName) {
            super(configName);
        }
        
        public CfRecConfig (CfRecConfig config) {
                super(config);
                neighborhoodSize = config.getNeighborhoodSize();
        }
        
        /*---------*
	 * Methods *
	 *---------*/                
        
        /**
         * Sets the size of the neighborhood (collaborative methods).
         * @param size 
         */
        public void setNeighborhoodSize(int size) {
                this.neighborhoodSize = size;
        }
        
        /**
         * Gets the size of the neighborhood.
         * @return 
         */
        public int getNeighborhoodSize() {
                return this.neighborhoodSize;
        }
        
        @Override
        public DataManager validateConfiguration() throws RecommenderException{
                super.validateConfiguration();
                        
                if (neighborhoodSize < 1)
                        throw new RecommenderException("CONFIGURATION NOT VALID. NEIGHBORHOOD CANNOT BE NEGATIVE");
                if (neighborhoodSize > 100)
                        throw new RecommenderException("CONFIGURATION NOT VALID. TOO MANY NEIGHBORHOODS");
                return null;
        }
}
