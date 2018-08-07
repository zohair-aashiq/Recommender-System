/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.invlist.impl;

import org.eclipse.rdf4j.recommender.storage.index.invlist.impl.CfScaledInvListBasedStorage;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedUserRating;
import org.eclipse.rdf4j.recommender.paradigm.collaborative.CfRecommender;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for JavaArraysStorageModel.
 */
public class CfScaledInvListBasedStorageWithLikesTest {
        /**
         * Error tolerance.
         */
        private static final double DELTA = 1e-15;                                
        //TODO
}
