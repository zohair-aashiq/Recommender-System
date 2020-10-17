/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.graph.impl;

import org.eclipse.rdf4j.recommender.storage.index.graph.impl.JungGraphIndexBasedStorage;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.paradigm.crossdomain.CdRecommender;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.IndexBasedStorage;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;
import org.eclipse.rdf4j.recommender.util.VectorOperations;

/**
 * Test class for JungGraphIndexBasedStorage.
 */
public class JungGraphIndexBasedStorageWithRatingsTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-3;                        
        
        //TODO            
}
