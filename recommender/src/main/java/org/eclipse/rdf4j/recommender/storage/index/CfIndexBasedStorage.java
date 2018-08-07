/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index;

import java.util.Map;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.storage.IndexBasedStorage;

/**
 * Interface of an index-based storage for a user collaborative filtering 
 * recommender model.
 */
public interface CfIndexBasedStorage extends IndexBasedStorage{
                        
        /**
         * Returns a sorted array that contains the neighborhood of a user or
         * item given as argument. A rated resource object represents a neighbor
         * and the similarity score.
         * @param index the index of a given user or item.
         * @return the neighborhood of the user or item given as argument.
         */
        public IndexedRatedRes[] getNeighborhood(int index);
        
        /**
         * It stores a neighborhood (a sorted array that stores a set of 
         * indexes together with their similarity scores) for a given user or
         * item.
         * @param index
         * @param neighborhood
         */
        public void storeNeighborhood(int index, IndexedRatedRes[] neighborhood);
        
        //FOR DEBUGGING
        public Map<Integer, IndexedRatedRes[]> getNeighborhoods();
}