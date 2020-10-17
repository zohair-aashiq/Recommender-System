/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.parameter;

/**
 * Supported techniques for normalizing the ratings.
 */
public enum RecRatingsNormalization {
        NONE,
        MEAN_CENTERING,
        Z_SCORE
}
