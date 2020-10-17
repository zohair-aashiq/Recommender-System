/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.parameter;

/**
 * Supported recommendation paradigms.
 */
public enum RecParadigm {
        CONTENT_BASED,
        USER_COLLABORATIVE_FILTERING,
        CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY,
        CROSS_DOMAIN_PAGERANK_WITH_PRIORS,
        CROSS_DOMAIN_REWORD,
        CROSS_DOMAIN_MACHINE_LEARNING
}
