/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager;

import java.util.Set;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.storage.Storage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;

/**
 * Interface of a data manager, whose goal is to encapsulate the different kinds
 * of storage supported by the system. A recommender has its data manager, which
 * is created and returned when the configuration is validated.
 */
public interface DataManager {        
        /**
         * Sets the storage that the data manager manages.
         * @param storage 
         */
        public void setStorage(Storage storage);
        
        /**
         * Gets the inner storage which is created and initialized when the data
         * manager is created.
         * @return 
         */
        public Storage getStorage();
        
        /**
         * The data manager is initialized and the managed storage populated.
         * The instance of the repository 
         * @param repository 
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException
         */
        public void init(SailRepository repository)
                throws RecommenderException;        
        
        /**
         * Initializes the storage. This is fed with the data coming from the
         * repository. 
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException
         */
        public void initStorage()
                throws RecommenderException;
    
        /**
         * Builds the recommender model within the storage. 
         * The recommendation graph must have been set (Recommendation entities) 
         * In the same way USER and ITEM must have been set. Otherwise, the
         * preprocessing has to be aborted.
         * @throws RecommenderException 
         */
        public void preprocess() throws RecommenderException;
        
        /**
         * Returns true if the data manager has already pre-process the data.
         * @return 
         */
        public boolean hasPreprocessed();        
        
        /**
         * Preprocesses in case a rating variable has been specified in the
         * graph pattern.
         */
        public void preprocessWithRatings();
        
        /**
         * Preprocesses in case a rating variable has not been specified in the
         * graph pattern.
         */
        public void preprocessWithoutRatings();
        
        /**
         * Populates the data structure with users, items and ratings
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException
         */
        public void populateStorage()
                throws RecommenderException;
        
        /**
         * It normalizes the ratings based on a given strategy.
         */
        public void normalizeRatings();
        
        /**
         * Gets the set of rated resources the user has consumed and rated in 
         * the past.
         * @param userURI
         * @return
         * @throws RecommenderException 
         */
        public Set<RatedResource> getRatedResources(String userURI) throws RecommenderException; 
                
       /**
         * Computes the average of rating of a user represented by a uri.
         * @param userURI
         * @return 
         */
        public double getRatingAverageOfUser(String userURI) throws RecommenderException;                
        
        /**
         * Gets a set of neighbors. Neighbors are represented themselves as 
         * rated resources where the rating is the similarity score between the 
         * active user (URI) and the neighbor. Note that this method could be 
         * also used to return neighbors of items.
         * @param URI
         * @return
         * @throws RecommenderException 
         */
        public Set<RatedResource> getNeighbors(String URI) throws RecommenderException;         
        
        /**
         * Gets a set of rated resources objects that the user rated in the past.
         * @param userURI
         * @return
         * @throws RecommenderException 
         */
        public Set<String> getConsumedResources(String userURI) throws RecommenderException;
        
        /**
         * Gets a set of candidates of recommendations. This method should be 
         * implemented in specific classes. For instance if the approach is 
         * user-based collaborative filtering the candidates are those resources
         * consumed by the neighbors.
         * @param userURI
         * @return
         * @throws RecommenderException 
         */
        public Set<String> getRecCandidates(String userURI) throws RecommenderException;
        
        /**
         * The method returns a score that reflects the degree of importance of
         * one node base on other.
         * More implementation-specific details should be provided.
         * @param node1
         * @param node2
         * @return
         * @throws RecommenderException 
         */
        public double getResRelativeImportance(String node1, String node2) 
                        throws RecommenderException;
                
        /**
         * Releases resources that were being used.
         */
        public void releaseResources();
        
        /**
         * Returns the configuration of the recommender.
         * @return 
         */
        public RecConfig getRecConfig();
        
        /**
         * To get the connection used to process the recommender model.
         * @return 
         */
        public RepositoryConnection getConnection();
}
