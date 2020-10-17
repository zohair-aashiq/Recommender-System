/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.parameter;

/**
 * Supported similarity metrics (compatible with the VSM).
 */
public enum RecEdgeDistribution {
        UNIFORM,
        OUTGOING_WEIGHTS_SUM_1,
        PREDICATE_INFORMATIVENESS
}
