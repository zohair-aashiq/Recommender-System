/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.parameter.RecRatingsNormalization;

/**
 * This abstract class represents a configuration of a generic recommender not
 * related to any specific recommender technique.
 */
public abstract class GenericRecConfig implements RecConfig {
                
        /*-------------------------*
	 * Configuration variables *
	 *-------------------------*/

        /**
         * The hash map stores for each recommendation entity (KEY) the string
         * of the variable name used in the graph pattern which represent that
         * entity.
         */
        private HashMap<RecEntity, String> ENTITY_CONFIG_HASH_TABLE = 
                new HashMap<RecEntity, String>();
        
        /**
         * This graph pattern contains all the entities needed for computing
         * the recommendations.
         * In the graph pattern prefixes are not allowed, the graph should have
         * the complete URIs / IRIs.
         */
        //These are mandatory in te configuration
        private final String configName;
        private String ratGraphPattern = null;
        private String posGraphPattern = null;
        private String negGraphPattern = null;
        private RecParadigm recParadigm =  null;
        private RecStorage recStorage = null;
        private RecRatingsNormalization normStrategy = RecRatingsNormalization.NONE; 
        //This have a default. No need for checking them in the configuration
        private boolean preprocess = false; //DEFAULT
        private int decimalPlaces = 2; //DEFAULT
        private PrintWriter logTimeWriter = null; //DEFAULT: no file
        
        /*--------------*
	 * Constructors *
	 *--------------*/
        
        public GenericRecConfig (String configName) {
                if (configName == null) {
                        this.configName = UUID.randomUUID().toString();
                } else {
                        this.configName = configName;          
                }
        }        
        
        public GenericRecConfig (GenericRecConfig config) {
                ENTITY_CONFIG_HASH_TABLE = new HashMap<RecEntity, String>(config.getEntitiesMap()); 
                configName = config.getConfigurationName();
                preprocess = config.hasToPreprocess();
                ratGraphPattern = config.getRatGraphPattern();
                posGraphPattern = config.getPosGraphPattern();
                negGraphPattern = config.getNegGraphPattern();
                recParadigm = config.getRecParadigm();
                recStorage = config.getRecStorage();
                normStrategy = config.getRecRatingNormStrategy();
                decimalPlaces = config.getDecimalPlaces();
                logTimeWriter = config.getLogTimeWriter();
        }
        
        
        /*---------*
	 * Methods *
	 *---------*/        
        
        @Override
        public String getConfigurationName(){
                return configName;
        }        
        
        @Override
        public void setRatGraphPattern(String ratGraphPattern) {
                if (ratGraphPattern != null) {
                        this.ratGraphPattern = ratGraphPattern;
                }
        }
        
        @Override
        public String getRatGraphPattern() {
                return ratGraphPattern;
        }
        
        @Override
        public void setPosGraphPattern(String posGraphPattern) {
                if (posGraphPattern != null) {
                        this.posGraphPattern = posGraphPattern;
                }
        }
        
        @Override
        public String getPosGraphPattern() {
                return posGraphPattern;
        }
        
        @Override
        public void setNegGraphPattern(String negGraphPattern) {
                if (negGraphPattern != null) {
                        this.negGraphPattern = negGraphPattern;
                }
        }
        
        @Override
        public String getNegGraphPattern() {
                return negGraphPattern;
        }
        
        @Override
        public void setRecEntity(RecEntity entity, String varName) throws RecommenderException {
                if (this.ratGraphPattern == null && this.posGraphPattern == null  && this.negGraphPattern == null) 
                        throw new RecommenderException("CONFIG: YOU HAVE TO FIRST SET THE INPUT GRAPH");
                if (varName!=null)
                        ENTITY_CONFIG_HASH_TABLE.put(entity, varName);
        }
        
        @Override
        public String getRecEntity(RecEntity entity) {
                if (ENTITY_CONFIG_HASH_TABLE.containsKey(entity)) 
                        return ENTITY_CONFIG_HASH_TABLE.get(entity);
                return null;
        }
        
        @Override
        public void setRecParadigm(RecParadigm recParadigm) {
                this.recParadigm = recParadigm;
        }
        
        @Override
        public RecParadigm getRecParadigm() {
                return recParadigm;
        }

        @Override
        public void setRecStorage(RecStorage recStorage) {
            this.recStorage = recStorage;
        }    
        
        @Override
        public RecStorage getRecStorage() {
            return recStorage;
        }            
        
        @Override
        public void setRatingsNormStrategy(RecRatingsNormalization norm) {
                this.normStrategy = norm;
        }
        
        @Override
        public RecRatingsNormalization getRecRatingNormStrategy() {
                return this.normStrategy;
        }        
        
        @Override
        public void setDecimalPlaces(int decimalPlaces) {
                this.decimalPlaces = decimalPlaces;
        }        

        @Override
        public int getDecimalPlaces(){
                return this.decimalPlaces;
        }
        
                
        @Override
        public void preprocessBeforeRecommending(boolean preprocess) {
                this.preprocess = preprocess;
        }
        
        @Override
        public boolean hasToPreprocess() {
                return this.preprocess;
        }
        
        @Override
        public PrintWriter getLogTimeWriter() {
                return logTimeWriter;
        }

        @Override
        public void setLogTimeWriter(PrintWriter logTimeWriter) {
                this.logTimeWriter = logTimeWriter;
        }  
        
        //FOR DEBUGGING pruposes
        protected HashMap getEntitiesMap() {
                return ENTITY_CONFIG_HASH_TABLE;
        }
        
        @Override
        public DataManager validateConfiguration() throws RecommenderException{
                if (configName == null) 
                        throw new RecommenderException("CONFIGURATION NOT VALID. NAME OF CONFIGURATION IS MISSING.");
                if (ratGraphPattern == null && this.posGraphPattern == null  && this.negGraphPattern == null ) 
                        throw new RecommenderException("CONFIGURATION NOT VALID. GRAPH PATTERN HAS NOT BEEN SPECIFIED.");
                if (!ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.USER)) 
                        throw new RecommenderException("CONFIGURATION NOT VALID. NO USERS HAVE BEEN DEFINED WITHIN THE GRAPH PATTERN");
                
                String userVarName = ENTITY_CONFIG_HASH_TABLE.get(RecEntity.USER);                
                String ratedItemVarName = ENTITY_CONFIG_HASH_TABLE.get(RecEntity.RAT_ITEM);
                String posItemVarName = ENTITY_CONFIG_HASH_TABLE.get(RecEntity.POS_ITEM);
                String negItemVarName = ENTITY_CONFIG_HASH_TABLE.get(RecEntity.NEG_ITEM);
                String ratingVarName = ENTITY_CONFIG_HASH_TABLE.get(RecEntity.RATING);
                
                if (!ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.USER)) 
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "NO USERS HAVE BEEN DEFINED WITHIN THE GRAPH PATTERN");
                if (userVarName ==  null)
                        throw new RecommenderException("CONFIGURATION NOT VALID. "
                                + "USER VARIABLE CANNOT BE NULL");
                
                //Here there are some validation which depends on the kind of
                //feedback provided:
                //CASE 1: WE HAVE A GRAPH PATTERN WHICH CONTAINS RATINGS
                if (ratGraphPattern != null) {
                        // If ratings are given, then other kinds of graph patterns
                        // should not be allowed.
                        if (posGraphPattern != null || negGraphPattern != null) 
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "YOU HAVE ALREADY SET A GRAPH PATTERN WHICH CONTAINS RATINGS. "
                                        + "YOU SHOULD NOT SET OTHERS WHICH CONTAIN POSITIVE OR NEGATIVE FEEDBACK.");
                        //In case of ratings you should have that:
                        if (!ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.RAT_ITEM)) 
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "NO ITEMS HAVE BEEN DEFINED WITHIN THE GRAPH PATTERN.");
                        if (ratedItemVarName ==  null)
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "ITEM VARIABLE CANNOT BE NULL.");
                        if (!ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.RATING)) 
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "NO RATINGS HAVE BEEN DEFINED WITHIN THE RATING GRAPH PATTERN.");
                        if (ratingVarName ==  null)
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "RATING VARIABLE CANNOT BE NULL.");
                        //Recommendation entities which should not be allowed:
                        if (ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.POS_ITEM))
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "WITH A RATING GRAPH PATTERN YOU ARE NOT ALLOWED TO SET A POSITIVE ITEM. "
                                        + "THIS INFORMATION IS ENCODED IN THE RATING ITSELF.");
                        if (ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.NEG_ITEM))
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "WITH A RATING GRAPH PATTERN YOU ARE NOT ALLOWED TO SET A NEGATIVE ITEM. "
                                        + "THIS INFORMATION IS ENCODED IN THE RATING ITSELF.");
                }
                //CASE 4: both, positive and negative feedback are provided.
                if (posGraphPattern != null || negGraphPattern != null) {
                        //CASE 2: the likes case (only positive feedback is known to the system.
                        if (posGraphPattern != null) {
                                // If only likes are given, then the rating graph pattern should be disabled
                                if (ratGraphPattern != null) 
                                        throw new RecommenderException("CONFIGURATION NOT VALID. "
                                                + "YOU HAVE ALREADY SET A GRAPH PATTERN WHICH CONTAINS ONLY THE POSITIVE FEEDBACK. "
                                                + "YOU SHOULD NOT SET OTHERS WHICH CONTAIN RATINGS.");
                                //In case of ratings you should have at least one, positive items or negative items:
                                if (!ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.POS_ITEM)) 
                                        throw new RecommenderException("CONFIGURATION NOT VALID. "
                                                + "NO POSITIVE ITEMS HAVE BEEN DEFINED WITHIN THE GRAPH PATTERN.");
                                if (posItemVarName ==  null)
                                        throw new RecommenderException("CONFIGURATION NOT VALID. "
                                                + "POSTIVE ITEM VARIABLE CANNOT BE NULL.");
                        }
                        //CASE 3: the negative feedback case (only negative feedback is known to the system.
                        if (negGraphPattern != null) {
                                // If only likes are given, then the rating graph pattern should be disabled
                                if (ratGraphPattern != null) 
                                        throw new RecommenderException("CONFIGURATION NOT VALID. "
                                                + "YOU HAVE ALREADY SET A GRAPH PATTERN WHICH CONTAINS ONLY THE NEGATIVE FEEDBACK. "
                                                + "YOU SHOULD NOT SET OTHERS WHICH CONTAIN RATINGS.");
                                //In case of ratings you should have at least one, positive items or negative items:
                                if (!ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.NEG_ITEM)) 
                                        throw new RecommenderException("CONFIGURATION NOT VALID. "
                                                + "NO POSITIVE ITEMS HAVE BEEN DEFINED WITHIN THE GRAPH PATTERN.");
                                if (negItemVarName ==  null)
                                        throw new RecommenderException("CONFIGURATION NOT VALID. "
                                                + "NEGATIVE ITEM VARIABLE CANNOT BE NULL.");                                
                        }
                        
                        if (posGraphPattern == null && negGraphPattern != null)
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                                + "ONLY PROVIDING NEGATIVE FEEDBACK IS NOT CURRENTLY SUPPORTED."); 
                        
                        //Recommendation entities which should not be allowed:
                        if (ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.RAT_ITEM))
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "WITH A GRAPH PATTERN WITH ONLY POSTIVE OR NEGATIVE "
                                        + "FEEDBACK YOU ARE NOT ALLOWED TO SET A RATED ITEM.");
                        if (ENTITY_CONFIG_HASH_TABLE.containsKey(RecEntity.RATING))
                                throw new RecommenderException("CONFIGURATION NOT VALID. "
                                        + "WITH A GRAPH PATTERN WITH ONLY POSTIVE OR NEGATIVE "
                                        + "FEEDBACK YOU ARE NOT ALLOWED TO SET A RATING.");
                }
                                                
                if (recParadigm == null)
                    throw new RecommenderException("CONFIGURATION NOT VALID. REC PARADIGM NONE IS NOT VALID");                                
                if (recStorage == null)
                        throw new RecommenderException("CONFIGURATION NOT VALID. STORAGE IS NOT VALID");
                if (normStrategy == null)
                        throw new RecommenderException("CONFIGURATION NOT VALID. NORMALIZAITON STRATEGY CANNOT BE SET TO NULL");                
                if (decimalPlaces < 0)
                        throw new RecommenderException("CONFIGURATION NOT VALID. DECIMAL PLACES CANNOT BE NEGATIVE");
                if (decimalPlaces > 6)
                        throw new RecommenderException("CONFIGURATION NOT VALID. DECIMAL PLACES TOO LARGE");    
                
                return null;
        }
}
