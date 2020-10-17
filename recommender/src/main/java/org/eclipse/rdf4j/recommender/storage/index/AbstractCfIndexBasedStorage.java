/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index;

import org.eclipse.rdf4j.recommender.storage.index.AbstractIndexBasedStorage;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;

public abstract class AbstractCfIndexBasedStorage extends AbstractIndexBasedStorage  implements CfIndexBasedStorage {
                        
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
        
        //SOME GETTERS FOR TEST PURPOSES        
        @Override
        public Map<Integer, IndexedRatedRes[]> getNeighborhoods() {
                return neighborhoods;
        }
        
        @Override
        public void resetStorage() {
                super.resetStorage();
                neighborhoods = new HashMap<Integer, IndexedRatedRes[]>(10000);
        }
}
