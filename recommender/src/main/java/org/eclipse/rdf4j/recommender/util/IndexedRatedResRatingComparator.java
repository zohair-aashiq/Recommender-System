/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.util;

import java.util.Comparator;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;

/**
 * Comparator of IndexedRatedRes that takes into account only the rating and not 
 the resource id.
 */
public class IndexedRatedResRatingComparator implements Comparator<IndexedRatedRes> {       
        @Override
        public int compare(IndexedRatedRes rr1, IndexedRatedRes rr2) {
                if (rr1 == null && rr2 != null) return +1;
                if (rr2 == null && rr1 != null) return -1;
                if (rr1 == null && rr2 == null) return +1;
                
                if (rr1.getRating() < rr2.getRating()) {                        
                        return +1;
                }
                if (rr2.getRating() < rr1.getRating()) {
                        return -1;
                }
                //If both have the same score
                //we need a deterministic ordering
                if (rr1.getRating() == rr2.getRating()) {
                        if (rr1.getResourceId() <  rr2.getResourceId()) {                        
                                return +1;
                        }
                        if (rr2.getResourceId() <  rr1.getResourceId()) {                        
                                return -1;
                        }
                }       
                return 0;
        }
}