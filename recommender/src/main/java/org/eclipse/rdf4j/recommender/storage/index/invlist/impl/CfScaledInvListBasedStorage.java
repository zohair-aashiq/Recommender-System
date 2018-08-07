/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist.impl;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.rdf4j.recommender.storage.index.CfIndexBasedStorage;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.storage.index.invlist.AbstractScaledInvListBasedStorage;

/**
 * The class is a concrete implementation of an index based storage based on
 * scaled inverted lists which supports the storage of neighborhoods for 
 * collaborative approaches.
 */
public class CfScaledInvListBasedStorage extends AbstractScaledInvListBasedStorage implements CfIndexBasedStorage{
                        
        /*-----------------*
	 * Data Structures *
	 *-----------------*/
    
        /**
         * Maps that stores neighborhoods. Key is a user or item ID.
         */ 
        private Map<Integer, IndexedRatedRes[]> neighborhoods 
                = new HashMap<Integer, IndexedRatedRes[]>(10000);
        
        /*---------*
	 * Methods *
	 *---------*/
        
        @Override
        public IndexedRatedRes[] getNeighborhood(int index) {
                return neighborhoods.get(index);
        }                
        
        @Override
        public void storeNeighborhood(int index, IndexedRatedRes[] neighborhood) {
                neighborhoods.put(index, neighborhood);
        } 
        
                
        @Override
        public void resetStorage() {
                super.resetStorage();
                neighborhoods = new HashMap<Integer, IndexedRatedRes[]>(10000);
        }
        
        //SOME GETTERS FOR TEST PURPOSES        
        @Override
        public Map<Integer, IndexedRatedRes[]> getNeighborhoods() {
                return neighborhoods;
        }
}
