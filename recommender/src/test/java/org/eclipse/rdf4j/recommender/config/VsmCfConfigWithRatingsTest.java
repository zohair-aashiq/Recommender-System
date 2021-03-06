/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universiaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.config;

import org.junit.Test;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;

/**
 * Test class for VsmCfRecConfig with ratings.
 */
public class VsmCfConfigWithRatingsTest {

        /**
         * Test if the RecommenderException is thrown.
         * Here the user has not been set.
         * @throws RecommenderException
         */
        @Test(expected=RecommenderException.class)
        public void testRecommenderExceptionOnValidationSd1() throws RecommenderException {
                int numberOfDecimalPlaces = 3;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createEmptyTestRepository();
                
                //RECOMMENDATION                        
                //One needs first to create a configuration for the recommender.
                VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

                //NEW thing: 
                //Set a recommendation configuration. Right now only one configuration is
                //possible. In the future more configurations should be possible.
                configuration.setRatGraphPattern(
                            "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                            + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                            + "?intermNode <http://example.org/movies#hasRating> ?rating"
                );
                                                
                //configuration.setRecEntity(RecEntity.USER, "?user");
                configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                configuration.setRecEntity(RecEntity.RATING, "?rating");
                
                configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                configuration.preprocessBeforeRecommending(true);
                //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
                configuration.setSimMetric(RecSimMetric.COSINE);
                configuration.setRecStorage(RecStorage.INVERTED_LISTS);
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
                        TestRepositoryInstantiator.createEmptyTestRepository();
                
                //RECOMMENDATION                        
                //One needs first to create a configuration for the recommender.
                VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

                //NEW thing: 
                //Set a recommendation configuration. Right now only one configuration is
                //possible. In the future more configurations should be possible.
                configuration.setRatGraphPattern(
                            "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                            + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                            + "?intermNode <http://example.org/movies#hasRating> ?rating"
                );
                                                
                configuration.setRecEntity(RecEntity.USER, "?user");
                //configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                configuration.setRecEntity(RecEntity.RATING, "?rating");
                
                configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                configuration.preprocessBeforeRecommending(true);
                //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
                configuration.setSimMetric(RecSimMetric.COSINE);
                configuration.setRecStorage(RecStorage.INVERTED_LISTS);
                configuration.setDecimalPlaces(numberOfDecimalPlaces);
                configuration.setNeighborhoodSize(2);

                recRepository.loadRecConfiguration(configuration);                
        }
        
        /**
         * Test if the RecommenderException is thrown.
         * Here the rating has not been set.
         * @throws RecommenderException 
         */
        //This test was disabled. Ratings are not mandatory anymore.
        /*
        @Test(expected=RecommenderException.class)
        public void testRecommenderExceptionOnValidation3() throws RecommenderException {
                int numberOfDecimalPlaces = 3;
                TestFixedSailRecommenderRepository recRepository = createEmptyTestRepository();
                
                //RECOMMENDATION                        
                //One needs first to create a configuration for the recommender.
                VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

                //NEW thing: 
                //Set a recommendation configuration. Right now only one configuration is
                //possible. In the future more configurations should be possible.
                configuration.setRatGraphPattern(
                            "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                            + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                            + "?intermNode <http://example.org/movies#hasRating> ?rating"
                );
                                                
                configuration.setRecEntity(RecEntity.USER, "?user");
                configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                //configuration.setRecEntity(RecEntity.RATING, "?rating");
                
                configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                configuration.preprocessBeforeRecommending(true);
                //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
                configuration.setSimMetric(RecSimMetric.COSINE);
                configuration.setRecStorage(RecStorage.INVERTED_LISTS);
                configuration.setDecimalPlaces(numberOfDecimalPlaces);
                configuration.setNeighborhoodSize(2);

                recRepository.loadRecConfiguration(configuration);                
        }
        */
        
        /**
         * Test if the RecommenderException is thrown.
         * Here the recommender graph pattern has not been set.
         * @throws RecommenderException 
         */
        @Test(expected=RecommenderException.class)
        public void testRecommenderExceptionOnValidationSd4() throws RecommenderException {
                int numberOfDecimalPlaces = 3;
                SailRecommenderRepository recRepository = 
                        TestRepositoryInstantiator.createEmptyTestRepository();
                
                //RECOMMENDATION                        
                //One needs first to create a configuration for the recommender.
                VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

                //NEW thing: 
                //Set a recommendation configuration. Right now only one configuration is
                //possible. In the future more configurations should be possible.
                /*
                configuration.setRatGraphPattern(
                            "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                            + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                            + "?intermNode <http://example.org/movies#hasRating> ?rating"
                );
                */
                                                
                configuration.setRecEntity(RecEntity.USER, "?user");
                configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                configuration.setRecEntity(RecEntity.RATING, "?rating");
                
                configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                configuration.preprocessBeforeRecommending(true);
                //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
                configuration.setSimMetric(RecSimMetric.COSINE);
                configuration.setRecStorage(RecStorage.INVERTED_LISTS);
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
                        TestRepositoryInstantiator.createEmptyTestRepository();
                
                //RECOMMENDATION                        
                //One needs first to create a configuration for the recommender.
                VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

                //NEW thing: 
                //Set a recommendation configuration. Right now only one configuration is
                //possible. In the future more configurations should be possible.
                configuration.setRatGraphPattern(
                            "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                            + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                            + "?intermNode <http://example.org/movies#hasRating> ?rating"
                );
                                                
                configuration.setRecEntity(RecEntity.USER, "?user");
                configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                configuration.setRecEntity(RecEntity.RATING, "?rating");
                
                //configuration.setRecParadigm(RecParadigm.SIM_USER_COLLABORATIVE_FILTERING);
                configuration.preprocessBeforeRecommending(true);
                //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
                configuration.setSimMetric(RecSimMetric.COSINE);
                configuration.setRecStorage(RecStorage.INVERTED_LISTS);
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
                        TestRepositoryInstantiator.createEmptyTestRepository();
                
                //RECOMMENDATION                        
                //One needs first to create a configuration for the recommender.
                VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

                //NEW thing: 
                //Set a recommendation configuration. Right now only one configuration is
                //possible. In the future more configurations should be possible.
                configuration.setRatGraphPattern(
                            "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                            + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                            + "?intermNode <http://example.org/movies#hasRating> ?rating"
                );
                                                
                configuration.setRecEntity(RecEntity.USER, "?user");
                configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                configuration.setRecEntity(RecEntity.RATING, "?rating");
                
                configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                configuration.preprocessBeforeRecommending(true);
                //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
                //configuration.setSimilarityFunction(RecSimMetric.COSINE);
                configuration.setRecStorage(RecStorage.INVERTED_LISTS);
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
                        TestRepositoryInstantiator.createEmptyTestRepository();
                
                //RECOMMENDATION                        
                //One needs first to create a configuration for the recommender.
                VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

                //NEW thing: 
                //Set a recommendation configuration. Right now only one configuration is
                //possible. In the future more configurations should be possible.
                configuration.setRatGraphPattern(
                            "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                            + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                            + "?intermNode <http://example.org/movies#hasRating> ?rating"
                );
                                                
                configuration.setRecEntity(RecEntity.USER, "?user");
                configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
                configuration.setRecEntity(RecEntity.RATING, "?rating");
                
                configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
                configuration.preprocessBeforeRecommending(true);
                //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
                configuration.setSimMetric(RecSimMetric.COSINE);
                //configuration.setRecStorageModel(RecStorage.JAVA_COLLECTIONS);
                configuration.setDecimalPlaces(numberOfDecimalPlaces);
                configuration.setNeighborhoodSize(2);

                recRepository.loadRecConfiguration(configuration);                
        }
}
