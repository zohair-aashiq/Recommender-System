/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.spark.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.paradigm.collaborative.CfRecommender;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.index.spark.SparkStorage;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for CfSparkStorage.
 */
public class CfSparkStorageWithLikesTest {
    
    /**
     * Error tolerance.
     */
    private static final double DELTA = 1e-15;
    
    //TODO    
}
