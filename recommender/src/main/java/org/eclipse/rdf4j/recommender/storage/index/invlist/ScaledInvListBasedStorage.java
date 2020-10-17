/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist;

import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;

/**
 * Interface of a scaled inverted lists-based storage for a recommender.
 */
public interface ScaledInvListBasedStorage extends InvListBasedStorage{
    
        /**
         * Stores a top rated resource. First added resources are more important.
         * @param ratedRes 
         */
        public void addTopRatedRes(IndexedRatedRes ratedRes);
                        
        /**
         * For a given user it retrieves the inverted lists and stores the in a 
         * separate data structure.
         */
        public void precomputeInvListsPartialDotProducts();    
                 
}
