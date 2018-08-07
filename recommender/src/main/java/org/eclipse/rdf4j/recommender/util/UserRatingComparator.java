/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.util;

import java.util.Comparator;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;

/**
 * Comparator of IndexedUserRating that takes into account only the rating and not 
 the resource id. Note that this is necessary because neighborhoods consist
 of IndexedUserRating objects, where the rating is the similarity score.
 */
public class UserRatingComparator implements Comparator<IndexedUserRating> {       
        @Override
        public int compare(IndexedUserRating rr1, IndexedUserRating rr2) {
                if (rr1.getRating() < rr2.getRating()) {                        
                        return +1;
                }
                if (rr2.getRating() < rr1.getRating()) {
                        return -1;
                }
                return 0;
        }
}