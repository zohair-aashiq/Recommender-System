/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.paradigm.crossdomain;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for CdRecommender with ratings.
 * Note that this recommender is of type cross domain and therefore no test
 * cases for the single-domain scenario are to be expected.
 */
public class CdRecWithRatingsTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-3;
        
        //TODO
}        