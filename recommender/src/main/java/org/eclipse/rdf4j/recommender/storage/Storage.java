/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage;

/**
 * Interface of an storage for a recommender.
 */
public interface Storage {                
               
        /**
         * Initialize all data structures used to store information
         * for recommendations. It clears data structures which contain
         * information
         */
        public void resetStorage();
}
