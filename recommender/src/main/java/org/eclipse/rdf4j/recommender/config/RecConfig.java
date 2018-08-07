/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import java.io.PrintWriter;
import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecRatingsNormalization;

/**
 * Interface of a generic configuration of a recommender. Most of the methods
 * are focused on selecting the graph patterns used to extract the 
 * recommendation entities or to select the storage method.
 */
public interface RecConfig {        
        /**
         * Gets the name of the configuration.
         * @return 
         */
        public String getConfigurationName();
    
        /**
         * Sets the subgraph that contains all recommendation entities needed to
         * build the model, when the feedback of the user is expressed in the 
         * for of ratings. It is expected that this graph pattern contains the
         * interlinking between users, items and the ratings.
         * @param ratGraphPattern a string which represents the graph pattern 
         * containing the recommendation entities.
         */
        public void setRatGraphPattern(String ratGraphPattern);
        
        /**
         * @return Returns the selected graph pattern.
         */
        public String getRatGraphPattern();
        
        
        /**
         * Sets the subgraph that contains all recommendation entities needed to
         * build the model, when the feedback of the user is expressed as a 
         * positive path between the user and an item.
         * The graph pattern is expected to contain nothing else than this link.
         * @param posGraphPattern a string which represents the graph pattern 
         * containing the recommendation entities.
         */
        public void setPosGraphPattern(String posGraphPattern);
        
        /**
         * @return Returns the selected graph pattern.
         */
        public String getPosGraphPattern();
        
        /**
         * Sets the subgraph that contains all recommendation entities needed to
         * build the model, when the feedback of the user is expressed as a 
         * negative path between the user and an item.
         * The graph pattern is expected to contain nothing else than this link.
         * @param negGraphPattern a string which represents the graph pattern 
         * containing the recommendation entities.
         */
        public void setNegGraphPattern(String negGraphPattern);
        
        /**
         * @return Returns the selected graph pattern.
         */
        public String getNegGraphPattern();        
        
        /**
         * Given the input graph this method specifies the parts of it which
         * corresponds to the recommendation entities.
         * @param entity a predefined entity
         * @param varName the variable name of the recGraphPattern which selects
         * the information corresponding to the entity.
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException
         */
        public void setRecEntity(RecEntity entity, String varName) throws RecommenderException;
        
        /**
         * Returns the variable that represents a recommendation entity
         * within the input graph.
         * @param entity
         * @return 
         */
        public String getRecEntity(RecEntity entity);
        
        /**
         * Sets the paradigm used to compute the recommendations.
         * @param paradigm
         */
        public void setRecParadigm(RecParadigm paradigm);
        
        /**
         * Gets the paradigm used to compute the recommendations.
         * @return 
         */
        public RecParadigm getRecParadigm();       
        
        /**
         * Sets a storage model for the recommendations.
         * @param storage 
         */
        public void setRecStorage(RecStorage storage);
        
        /**
         * Gets the selected storage model.
         * @return 
         */
        public RecStorage getRecStorage();
                       
        
        /**
         * Sets a method for normalizing the ratings of the data.
         * @param norm
         */
        public void setRatingsNormStrategy(RecRatingsNormalization norm);
        
        
        /**
         * Gets the strategy for normalizing the ratings.
         * @return 
         */
        public RecRatingsNormalization getRecRatingNormStrategy();
        
        /**
         * Sets the number of decimal places to be used when computing scores in
         * the system.
         * @param decimalPlaces 
         */
        public void setDecimalPlaces(int decimalPlaces);
        
        /**
         * Gets the number of decimal places specified in the configuration.
         * @return 
         */
        public int getDecimalPlaces();
        
        /**
         * If this parameter is set to true, the data is preprocessed before
         * recommendations are computed.
         * @param preprocess
         */
        public void preprocessBeforeRecommending(boolean preprocess);
        
        /**
         * This returns true if the user has set the flag preprocessing to true.
         * @return 
         */
        public  boolean hasToPreprocess();
        
        /**
         * Set the file where information of the different recommendation stages
         * is stored.
         * @param logTimeWriter 
         */
        public void setLogTimeWriter(PrintWriter logTimeWriter);
        
        /**
         * Returns the file where information of the different recommendation 
         * stages is stored.
         * @return 
         */
        public PrintWriter getLogTimeWriter();
        
        /**
         * Checks whether all parameters have been configured correctly for the
         * given configuration.
         * @return A DataManager instance
         * @throws RecommenderException 
         */
        public DataManager validateConfiguration() throws RecommenderException;        
}
