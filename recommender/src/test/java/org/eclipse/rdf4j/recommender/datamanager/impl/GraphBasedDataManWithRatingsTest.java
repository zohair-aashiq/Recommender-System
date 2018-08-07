/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager.impl;

import org.eclipse.rdf4j.recommender.datamanager.impl.GraphBasedDataManager;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for GraphBasedDataManager with ratings.
 */
public class GraphBasedDataManWithRatingsTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-5; 
        
        //TODO so far I used a graph model to deal exclusively with the likes case.            
}