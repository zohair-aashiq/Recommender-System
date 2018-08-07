/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;

/**
 * Test class for SilVsmCfRecConfig with likes.
 * 
 * Test cases are copied from SilVsmCfConfigWithRatingsTest and modified.
 */
public class SilVsmCfConfigWithLikesTest {
    
    /**
     * Test if the RecommenderException is thrown.
     * Here the user has not been set.
     * @throws RecommenderException
     */
    @Test(expected=RecommenderException.class)
    public void testRecommenderExceptionOnValidationSd1() throws RecommenderException {
        
        int numberOfDecimalPlaces = 3;
        SailRecommenderRepository recRepository 
            = TestRepositoryInstantiator.createEmptyLikesTestRepository();

        //RECOMMENDATION                        
        //One needs first to create a configuration for the recommender.
        SilVsmUcfRecConfig configuration = new SilVsmUcfRecConfig("config1");

        //NEW thing: 
        //Set a recommendation configuration. Right now only one configuration is
        //possible. In the future more configurations should be possible.
        configuration.setRatGraphPattern(
            "?user <http://example.org/graph#likes> ?item"
        );

        //configuration.setRecEntity(RecEntity.USER, "?user");
        configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");

        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.SCALED_INVERTED_LISTS);
        configuration.setDecimalPlaces(numberOfDecimalPlaces);
        configuration.setNeighborhoodSize(2);

        recRepository.loadRecConfiguration(configuration);              
    }

    /**
     * Test if the RecommenderException is thrown.
     * Here the item has not been set.
     * @throws RecommenderException 
     */
    @Test(expected=RecommenderException.class)
    public void testRecommenderExceptionOnValidationSd2() throws RecommenderException {
        int numberOfDecimalPlaces = 3;
        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createEmptyLikesTestRepository();

        //RECOMMENDATION                        
        //One needs first to create a configuration for the recommender.
        SilVsmUcfRecConfig configuration = new SilVsmUcfRecConfig("config1");

        //NEW thing: 
        //Set a recommendation configuration. Right now only one configuration is
        //possible. In the future more configurations should be possible.
        configuration.setRatGraphPattern(
            "?user <http://example.org/graph#likes> ?item"
        );

        configuration.setRecEntity(RecEntity.USER, "?user");
        //configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");

        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.SCALED_INVERTED_LISTS);
        configuration.setDecimalPlaces(numberOfDecimalPlaces);
        configuration.setNeighborhoodSize(2);

        recRepository.loadRecConfiguration(configuration);                
    }

    /**
     * Test if the RecommenderException is not thrown.
     * Here the rating has not been set.
     * @throws RecommenderException 
     */
    @Test(expected=RecommenderException.class)
    public void testRecommenderExceptionOnValidationSd3() throws RecommenderException {
        int numberOfDecimalPlaces = 3;
        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createEmptyLikesTestRepository();
        printAllTriplesOfRepository(recRepository);
        //RECOMMENDATION                        
        //One needs first to create a configuration for the recommender.
        SilVsmUcfRecConfig configuration = new SilVsmUcfRecConfig("config1");

        //NEW thing: 
        //Set a recommendation configuration. Right now only one configuration is
        //possible. In the future more configurations should be possible.
        configuration.setRatGraphPattern(
            "?user <http://example.org/graph#likes> ?item"
        );

        configuration.setRecEntity(RecEntity.USER, "?user");
        configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");

        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.SCALED_INVERTED_LISTS);
        configuration.setDecimalPlaces(numberOfDecimalPlaces);
        configuration.setNeighborhoodSize(2);

        recRepository.loadRecConfiguration(configuration);                
    }

    /**
     * Test if the RecommenderException is thrown.
     * Here the recommender graph pattern has not been set.
     * @throws RecommenderException 
     */
    @Test(expected=RecommenderException.class)
    public void testRecommenderExceptionOnValidationSd4() throws RecommenderException {
        int numberOfDecimalPlaces = 3;
        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createEmptyLikesTestRepository();

        //RECOMMENDATION                        
        //One needs first to create a configuration for the recommender.
        SilVsmUcfRecConfig configuration = new SilVsmUcfRecConfig("config1");

        //NEW thing: 
        //Set a recommendation configuration. Right now only one configuration is
        //possible. In the future more configurations should be possible.
        /*
        configuration.setRatGraphPattern(
            "?user <http://example.org/graph#likes> ?item"
        );
        */

        configuration.setRecEntity(RecEntity.USER, "?user");
        configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");

        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.SCALED_INVERTED_LISTS);
        configuration.setDecimalPlaces(numberOfDecimalPlaces);
        configuration.setNeighborhoodSize(2);

        recRepository.loadRecConfiguration(configuration);                
    }

    /**
     * Test if the RecommenderException is thrown.
     * Here the recommender paradigm has not been set.
     * @throws RecommenderException 
     */
    @Test(expected=RecommenderException.class)
    public void testRecommenderExceptionOnValidationSd5() throws RecommenderException {
        int numberOfDecimalPlaces = 3;
        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createEmptyLikesTestRepository();

        //RECOMMENDATION                        
        //One needs first to create a configuration for the recommender.
        SilVsmUcfRecConfig configuration = new SilVsmUcfRecConfig("config1");

        //NEW thing: 
        //Set a recommendation configuration. Right now only one configuration is
        //possible. In the future more configurations should be possible.
        configuration.setRatGraphPattern(
            "?user <http://example.org/graph#likes> ?item"
        );

        configuration.setRecEntity(RecEntity.USER, "?user");
        configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");

        //configuration.setRecParadigm(RecParadigm.INVERTED_INDEX_CB);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.SCALED_INVERTED_LISTS);
        configuration.setDecimalPlaces(numberOfDecimalPlaces);
        configuration.setNeighborhoodSize(2);

        recRepository.loadRecConfiguration(configuration);                
    }

    /**
     * Test if the RecommenderException is thrown.
     * Here the recommender similarity function has not been set.
     * @throws RecommenderException 
     */
    @Test(expected=RecommenderException.class)
    public void testRecommenderExceptionOnValidationSd6() throws RecommenderException {
        int numberOfDecimalPlaces = 3;
        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createEmptyLikesTestRepository();

        //RECOMMENDATION                        
        //One needs first to create a configuration for the recommender.
        SilVsmUcfRecConfig configuration = new SilVsmUcfRecConfig("config1");

        //NEW thing: 
        //Set a recommendation configuration. Right now only one configuration is
        //possible. In the future more configurations should be possible.
        configuration.setRatGraphPattern(
            "?user <http://example.org/graph#likes> ?item"
        );

        configuration.setRecEntity(RecEntity.USER, "?user");
        configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");

        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        //configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.SCALED_INVERTED_LISTS);
        configuration.setDecimalPlaces(numberOfDecimalPlaces);
        configuration.setNeighborhoodSize(2);

        recRepository.loadRecConfiguration(configuration);                
    }

    /**
     * Test if the RecommenderException is thrown.
     * Here the recommender storage model has not been set.
     * @throws RecommenderException 
     */
    @Test(expected=RecommenderException.class)
    public void testRecommenderExceptionOnValidationSd7() throws RecommenderException {
        int numberOfDecimalPlaces = 3;
        SailRecommenderRepository recRepository = 
                TestRepositoryInstantiator.createEmptyLikesTestRepository();

        //RECOMMENDATION                        
        //One needs first to create a configuration for the recommender.
        SilVsmUcfRecConfig configuration = new SilVsmUcfRecConfig("config1");

        //NEW thing: 
        //Set a recommendation configuration. Right now only one configuration is
        //possible. In the future more configurations should be possible.
        configuration.setRatGraphPattern(
            "?user <http://example.org/graph#likes> ?item"
        );

        configuration.setRecEntity(RecEntity.USER, "?user");
        configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");

        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        configuration.setSimMetric(RecSimMetric.COSINE);
        //configuration.setRecStorage(RecStorage.SCALED_INVERTED_LISTS);
        configuration.setDecimalPlaces(numberOfDecimalPlaces);
        configuration.setNeighborhoodSize(2);

        recRepository.loadRecConfiguration(configuration);              
    }     
    
    /**
     * Prints all triples of given Repository. 
     * 
     * Can be used for development & testing.
     * 
     * @param rep
     * 
     * @throws org.eclipse.rdf4j.repository.RepositoryException
     * @throws org.eclipse.rdf4j.query.MalformedQueryException
     * @throws org.eclipse.rdf4j.query.QueryEvaluationException
     */
    public void printAllTriplesOfRepository(SailRepository rep) 
            throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        System.out.println("org.eclipse.rdf4j.recommender.config.SilVsmCfConfigWithLikesTest.printAllTriplesOfRepository()");
        RepositoryConnection conn = rep.getConnection();
        
        String query = "SELECT $s $p $o WHERE { $s $p $o }";        
        
        TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
        TupleQueryResult result = tupleQuery.evaluate();

            while (result.hasNext()) {
                
                BindingSet bs = result.next();
                
                Value sVal   = bs.getValue("s");
                Value pVal   = bs.getValue("p");
                Value oVal   = bs.getValue("o");

                System.out.println(sVal.stringValue() + " " +
                        pVal.stringValue() + " " + oVal.stringValue() );                
            }      
        System.out.println("org.eclipse.rdf4j.recommender.config.SilVsmCfConfigWithLikesTest.printAllTriplesOfRepository()");             
    } 
}
