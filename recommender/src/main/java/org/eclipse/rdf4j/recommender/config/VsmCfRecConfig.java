/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.impl.IndexBasedDataManager;
import org.eclipse.rdf4j.recommender.datamanager.impl.SparkDataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;

/**
 * This class represents a configuration of a collaborative recommender
 * based on the VSM. 
 */
public class VsmCfRecConfig extends CfRecConfig {
                
        /*-------------------------*
	 * Configuration variables *
	 *-------------------------*/   
    
        private RecSimMetric simMetric = null;
        
        /*--------------*
	 * Constructors *
	 *--------------*/
        
        public VsmCfRecConfig (String configName) {
            super(configName);
        }
        
        public VsmCfRecConfig (VsmCfRecConfig config) {
                super(config);
                simMetric = config.getSimMetric();
        }
        
        /*---------*
	 * Methods *
	 *---------*/                
        
        /**
         * Sets a similarity metric to be used by the model.
         * @param simMetric
         */
        public void setSimMetric(RecSimMetric simMetric) {
                this.simMetric = simMetric;
        }
        
        /**
         * Gets the selected similarity function to build the model.
         * @return 
         */
        public RecSimMetric getSimMetric() {
                return this.simMetric;
        }              

        
        @Override
        public DataManager validateConfiguration() throws RecommenderException{
                super.validateConfiguration();     
                
                //Supported paradigms
                if (getRecParadigm() != RecParadigm.USER_COLLABORATIVE_FILTERING) {
                        throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S APPROACH");
                }
                
                if (getRecStorage() != RecStorage.INVERTED_LISTS 
                        && getRecStorage() != RecStorage.SCALED_INVERTED_LISTS
                        && getRecStorage() != RecStorage.SPARK) {
                        throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S STORAGE");
                }
                
                if (hasToPreprocess() == false) 
                        throw new RecommenderException("THIS APPROACH REQUIRES PREPROCESSING");
                        
                if (simMetric == null)
                        throw new RecommenderException("CONFIGURATION NOT VALID. SIM FUNCTION IS NOT VALID");
                
                if( getRecStorage() == RecStorage.SPARK ) {
                    return new SparkDataManager(this);
                }
                return new IndexBasedDataManager(this);
        }
}
