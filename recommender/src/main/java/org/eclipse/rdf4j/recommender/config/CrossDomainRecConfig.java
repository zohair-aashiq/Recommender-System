/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import java.util.HashMap;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;

/**
 * This abstract class represents a configuration of a cross domain 
 * recommender, i.e. a recommender where the items to recommend belong to another
 * domain. For instance, recommends music for users who consume books.
 */
public abstract class CrossDomainRecConfig extends GenericRecConfig {        
        
        /*--------------*
	 * Constructors *
	 *--------------*/
        
        public CrossDomainRecConfig (String configName) {
            super(configName);
        }
        
        public CrossDomainRecConfig (CrossDomainRecConfig config) {
                super(config);
        }
        
        /*---------*
	 * Methods *
	 *---------*/
        
        /**
         * Gets the source domain specified in the configuration.
         * @return 
         */
        public String getSourceDomain() {
                return ((HashMap<RecEntity, String>)getEntitiesMap())
                        .get(RecEntity.SOURCE_DOMAIN);
        }
        
        /**
         * Gets the target domain specified in the configuration.
         * @return 
         */
        public String getTargetDomain() {
                return ((HashMap<RecEntity, String>)getEntitiesMap())
                        .get(RecEntity.TARGET_DOMAIN);
        }

        @Override
        public DataManager validateConfiguration() throws RecommenderException{
                super.validateConfiguration();

                //Two more entities are required. The source domain and the target
                //domain.
                String sourceDomName = ((HashMap<RecEntity, String>)getEntitiesMap())
                        .get(RecEntity.SOURCE_DOMAIN);
                String targetDomName = ((HashMap<RecEntity, String>)getEntitiesMap())
                        .get(RecEntity.TARGET_DOMAIN);
                
                if (sourceDomName ==  null || targetDomName == null)
                        throw new RecommenderException("CONFIGURATION NOT VALID. THIS APPROACH REQUIRES SOURCE AND TARGET DOMAIN");
                
                return null;
        }
        
}
