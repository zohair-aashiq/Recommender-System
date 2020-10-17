/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.recommender.config.CrossDomainRecConfig;
import org.eclipse.rdf4j.recommender.config.LinkAnalysisRecConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.AbstractIndexBasedDataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEdgeDistribution;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecPriorsDistribution;
import org.eclipse.rdf4j.recommender.storage.GraphBasedStorage;
import org.eclipse.rdf4j.recommender.storage.index.graph.impl.JungGraphIndexBasedStorage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;

import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public final class GraphBasedDataManager extends AbstractIndexBasedDataManager{
        /*--------*
	 * Fields *
	 *--------*/
        /**
         * The user for which recommendations where generated the last.
         */
        private String userOfLastRecommendation = "";


        /*-------------*
	 * Constructor *
	 *-------------*/  
        /**
         * The constructor has an important role. Depending on the configuration
         * it creates the appropriate storage.
         * @param config 
         */
        public GraphBasedDataManager(RecConfig config) {
                super(config);
                                
                switch(config.getRecParadigm()) {
                        case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY: 
                        case CROSS_DOMAIN_PAGERANK_WITH_PRIORS:
                                setStorage(new JungGraphIndexBasedStorage());
                        case CROSS_DOMAIN_REWORD:
                                setStorage(new JungGraphIndexBasedStorage());
                        case CROSS_DOMAIN_MACHINE_LEARNING:
                        		setStorage(new JungGraphIndexBasedStorage());
                        break;
                }
        }
        
        // ZAID::
        Set<Integer> sourceResources = new HashSet<Integer>();
		private SailRepository sailRep = null;
		private RepositoryConnection conn = null;
		Classifier classifier = null;

        @Override
        public void preprocessWithRatings() {
                //Do nothing for now
        }
        
        @Override
        public void preprocessWithoutRatings() {
	            
                switch(getRecConfig().getRecParadigm()) {
                        case CROSS_DOMAIN_REWORD:
                                //Here for each user I will store which items are reachable from
                                //items liked by the user in the past to the candidate items
                                //First we get all users:
                                
                        		Set<Integer> allUsersIndexes = getStorage().getAllUserIndexes();
                                int i = 0;
                                for (Integer indexOfUser: allUsersIndexes) {
                                        Set<IndexedRatedRes> irrSet = getStorage().getIndexedRatedResOfUser(indexOfUser);
                                        for (IndexedRatedRes irr: irrSet) {
                                                for (int targetIndex: ((GraphBasedStorage)getStorage()).getTargetNodes()) {
                                                        ((GraphBasedStorage)getStorage()).
                                                                storeReachability(irr.getResourceId(), targetIndex, 2);
                                                }
                                        }
                                }
                        break;
                        case CROSS_DOMAIN_MACHINE_LEARNING:
                        	try {
	                        		String filename = ((LinkAnalysisRecConfig)getRecConfig()).getFeatureFileName();
	                        		createArffFileForTraining(filename);
	                        		classifier = trainAndReturnClassifier();
                        	} catch (Exception ex) {
                        			throw new RecommenderException(ex);
                        	}

                }
        }
        
        @Override
        public void populateStorage() throws RecommenderException {
        		System.out.println("ZAID:: in populate storage of GBDM");
                super.populateStorage();

                try {	
                        String subjectRes = null;
                        String objectRes = null;
                        String predicateRes = null;
                        String targetRes = null;
                        String sourceRes = null;
                        int sourceResIndex = -1;

                        TupleQuery tupleQuery = null;
                        TupleQueryResult result = null;       
                        String preprocessingSPARQLQuery = null;
                        
                        Pattern pat = null;
                        Matcher mat = null;
                        String targetItemVar = "";
                        String sourceItemVar = "";

                        //I am querying the RDF model using the repository connection 
                        //to build the graph, processing RDF resources nodes and predicates
                        //and build the Jung graph.
                        /*
                        preprocessingSPARQLQuery =
                        "SELECT * "
                        + "WHERE {\n"
                        +       config.getRatGraphPattern()
                        + "}";  
                        */

                        //The complete RDF model.
                        preprocessingSPARQLQuery = 
                                "SELECT * "
                                + "WHERE {\n"
                                +       "?s ?p ?o"
                                + "}";

                        tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL,
                                preprocessingSPARQLQuery);

                        result = tupleQuery.evaluate();

                        if (result.hasNext() == false)
                            throw new RecommenderException("GRAPH PATTERN IS NOT MATCHING TO ANYTHING");

                        switch(getRecConfig().getRecParadigm()) {
                                case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY: case CROSS_DOMAIN_PAGERANK_WITH_PRIORS:
                                        while (result.hasNext()) {
                                                BindingSet bs = result.next();
                                                //We can access each of the variables configured:
                                                subjectRes = bs.getValue("s").stringValue();
                                                objectRes = bs.getValue("o").stringValue();

                                                //we store the resources found and build the new graph model
                                                ((GraphBasedStorage)getStorage()).addNode(subjectRes);
                                                ((GraphBasedStorage)getStorage()).addNode(objectRes);
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.DIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, null);                                                
                                                } else if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.UNDIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, null); 
                                                        ((GraphBasedStorage)getStorage()).addEdge(objectRes, subjectRes, null);
                                                }
                                        }
                                break;
                                case CROSS_DOMAIN_REWORD:
                                case CROSS_DOMAIN_MACHINE_LEARNING:
                                        while (result.hasNext()) {
                                                BindingSet bs = result.next();
                                                //We can access each of the variables configured:
                                                subjectRes = bs.getValue("s").stringValue();
                                                objectRes = bs.getValue("o").stringValue();
                                                predicateRes = bs.getValue("p").stringValue();

                                                //we store the resources found and build the new graph model
                                                ((GraphBasedStorage)getStorage()).addNode(subjectRes);
                                                ((GraphBasedStorage)getStorage()).addNode(objectRes);
                                                
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.DIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, predicateRes);                                                
                                                } else if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.UNDIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, predicateRes); 
                                                        ((GraphBasedStorage)getStorage()).addEdge(objectRes, subjectRes, predicateRes); 
                                                }
                                        }                                        
                                break;
                        }
                        
                        if (getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY ||
                                        getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_PAGERANK_WITH_PRIORS  || 
                                        getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_REWORD ||
                        				getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_MACHINE_LEARNING) {  
                            
                                //When dealing with cross-domain recommendations one has to
                                //store the resource from the graph are part of the target domain.
                                pat = Pattern.compile("\\?(\\w)+");
                                mat = pat.matcher(((CrossDomainRecConfig)getRecConfig()).getTargetDomain());

                                if (mat.find()) targetItemVar = mat.group();

                                preprocessingSPARQLQuery =
                                        "SELECT " + targetItemVar
                                        + " WHERE {\n"
                                        +   ((CrossDomainRecConfig)getRecConfig()).getTargetDomain()
                                        + "}";

                                tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL,
                                        preprocessingSPARQLQuery);

                                result = tupleQuery.evaluate();

                                if (result.hasNext() == false)
                                    throw new RecommenderException("NO TARGET NODES FOUND");

                                while (result.hasNext()) {
                                    BindingSet bs = result.next();
                                    targetRes = bs.getValue(targetItemVar.replace("?", "")).stringValue();
                                    //We set the found resource as target
                                    ((GraphBasedStorage)getStorage()).setTargetNode(targetRes);
                                }
                        }
                        
                        // for CROSS_DOMAIN_MACHINE_LEARNING, source items should also be stored in
                        // a set
                        if (getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_MACHINE_LEARNING) {
                        	
	                        	sailRep = super.sailRep;    // save to get connection in other methods
	                    
		                        pat = Pattern.compile("\\?(\\w)+");
		                        mat = pat.matcher(((CrossDomainRecConfig)getRecConfig()).getSourceDomain());
		
		                        if (mat.find()) sourceItemVar = mat.group();
		
		                        preprocessingSPARQLQuery =
		                                "SELECT " + sourceItemVar
		                                + " WHERE {\n"
		                                +   ((CrossDomainRecConfig)getRecConfig()).getSourceDomain()
		                                + "}";
		
		                        tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL,
		                                preprocessingSPARQLQuery);
		
		                        result = tupleQuery.evaluate();
		
		                        if (result.hasNext() == false)
		                            	throw new RecommenderException("NO SOURCE NODES FOUND");
		
		                        while (result.hasNext()) {
			                            BindingSet bs = result.next();
			                            sourceRes = bs.getValue(sourceItemVar.replace("?", "")).stringValue();
			
			                            sourceResIndex = getStorage().getIndexOf(sourceRes);
			                            sourceResources.add(sourceResIndex);
		                        }	                        
                }

                } catch (RepositoryException ex) {
                        throw new RecommenderException(ex);
                } catch (MalformedQueryException ex) {
                        throw new RecommenderException(ex);
                } catch (QueryEvaluationException ex) {
                        throw new RecommenderException(ex);
                }
        }
        
        @Override
        public Set<RatedResource> getNeighbors(String URI) throws RecommenderException {
    			throw new UnsupportedOperationException();
        }        
        
        
        @Override
        public Set<String> getRecCandidates(String userURI) throws RecommenderException {
                Set<String> recCandidates = new HashSet<String>();
                
                //Retrieve the index of user
                int indexOfUser = getStorage().getIndexOf(userURI);
                //If this cannot be retrieved then throw an exception
                if (indexOfUser == -1){
                        throw new RecommenderException("User resource was not found");
                }
                
                GraphBasedStorage graphStorage = (GraphBasedStorage) getStorage();
                Set<Integer> targetNodeIds = new HashSet<Integer>();
                targetNodeIds.addAll(graphStorage.getTargetNodes());
                                        
                switch(getRecConfig().getRecParadigm()) {
                                case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY: case CROSS_DOMAIN_PAGERANK_WITH_PRIORS:                                    
                                        Set<Integer> allUserNodes = graphStorage.getSubgraphVertices(indexOfUser);
                                        targetNodeIds.retainAll(allUserNodes);
                                        for (Integer nodeId: targetNodeIds) {
                                                recCandidates.add(getStorage().getURI(nodeId));
                                        }
                                break;
                                case CROSS_DOMAIN_REWORD:
                                	if (hasPreprocessed()) {
                                        //we collect all candidates reachable from the items liked / rated by the user.
                                        Set<IndexedRatedRes> irrSet = getStorage().getIndexedRatedResOfUser(indexOfUser);
                                        Set<Integer> allReachableCandidatesFromConsumedItem = new HashSet<Integer>();

                                        for (IndexedRatedRes irr: irrSet) {
                                                allReachableCandidatesFromConsumedItem.
                                                        addAll(graphStorage.getAllReachableNodes(irr.getResourceId(), 2));
                                        }
                                        allReachableCandidatesFromConsumedItem.retainAll(targetNodeIds);

                                        for (Integer nodeId: allReachableCandidatesFromConsumedItem) {
                                                recCandidates.add(getStorage().getURI(nodeId));
                                        }                                        
	                                } else {
	                                        //In case the pre-processing is not done we simply use the whole candidate set.
	                                        for (Integer nodeId: targetNodeIds) {
	                                                recCandidates.add(getStorage().getURI(nodeId));
	                                        }
	                                }
                                break;
                                case CROSS_DOMAIN_MACHINE_LEARNING:
                                		// Get all target nodes which are not liked by the user
                                        for (Integer nodeId: targetNodeIds) {
                                                recCandidates.add(getStorage().getURI(nodeId));
                                        }
                                        recCandidates.removeAll(getConsumedResources( userURI ));
                                        //TODO:: restrict the size here

                                break;
                        }                
                return recCandidates;
        }
        
        
        private Set<Integer> getRatedSourceItemsOfUser(Integer userIndex) {
        		Set<Integer> ratedSourceItems = new HashSet<Integer>();
        		
        		Set<IndexedRatedRes> irrSet = getStorage().getIndexedRatedResOfUser(userIndex);
    			for (IndexedRatedRes irr: irrSet) {
    					if(!((GraphBasedStorage)getStorage()).getTargetNodes().contains(irr.getResourceId())){
    							ratedSourceItems.add(irr.getResourceId());
    					}
    			}
        		return ratedSourceItems;
        }

        
        private double getSimilarUsersPreference(Integer userIndex, Integer targetIndex) {
        		String user = getStorage().getURI(userIndex);
        		String targetItem = getStorage().getURI(targetIndex);
        		
    			Set<Integer> ratedSourceItemsOfUser = getRatedSourceItemsOfUser(userIndex);
    			
    			conn = sailRep.getConnection();
    			
    			String sourceItemURI = null;
    			Integer sourceIndex = null;
	          	String query = null;
		        TupleQueryResult result = null;
		        TupleQuery tupleQuery;
		        
		        String userNode = getRecConfig().getRecEntity(RecEntity.USER);
		        String targetNode = getRecConfig().getRecEntity(RecEntity.POS_ITEM);
		        String posItemPattern = getRecConfig().getPosGraphPattern();
		        
		        // get the predicate which shows positive rating or likeness
		        Pattern pattern = Pattern.compile("\\" + userNode + " (.+?) \\" + targetNode);
		        Matcher matcher = pattern.matcher(posItemPattern);
		        matcher.find();
		        String ratingPredicate = matcher.group(1);
		        
    			
    			// for all the source items liked by the user,
    			// find other users who also liked this item
		        Set<String> similarUsersList = new HashSet<String>();
    			for (Integer sourceItem : ratedSourceItemsOfUser) {
	    				sourceItemURI = getStorage().getURI(sourceItem);
	    				
			          	query = "SELECT " + userNode + " \n"
			              		+ "WHERE { \n"
			              		+ userNode + " " + ratingPredicate + " <" + sourceItemURI + ">"
			              		+ "}\n";
			          		
			          	tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
			  			result = tupleQuery.evaluate();
			  			
			  			while (result.hasNext()){
			  				BindingSet bs = result.next();                                 
			  				similarUsersList.add(bs.getValue(userNode.replace("?", "")).stringValue());
			  			}
    			}
    			
    			//remove the original user from this list
    			similarUsersList.remove(user);
    			
    			String similarSource = new String();
    			
    			String sourceDomainPattern = getRecConfig().getRecEntity(RecEntity.SOURCE_DOMAIN);
		        pattern = Pattern.compile(".?\\w+");
		        matcher = pattern.matcher(sourceDomainPattern);
		        matcher.find();
		        String resNode = matcher.group(0);
    			
		        
		        // for all users found in previous loop, find all
		        // source items these users have liked and create a Map
		        HashMap<String, Set<Integer>> similarUsersRatedSourceItems= new HashMap<String, Set<Integer>>();
		        Set<Integer> ratedSourceItemsOfOtherUser = new HashSet<Integer>();
		        for (String similarUser : similarUsersList){
    					query = "SELECT " + resNode + " \n"
    							+ "WHERE { \n"
    							+ "<" + similarUser + "> " + ratingPredicate + " " + resNode + " ."
    							+ sourceDomainPattern
    							+ "}\n";
    					
    					tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
			  			result = tupleQuery.evaluate();
			  			
			  			ratedSourceItemsOfOtherUser = new HashSet<Integer>();
			  			while (result.hasNext()){
				  				BindingSet bs = result.next();
				  				similarSource = bs.getValue(resNode.replace("?", "")).stringValue();
				  				sourceIndex = getStorage().getIndexOf(similarSource);
				  				ratedSourceItemsOfOtherUser.add(sourceIndex);
			  			}
			  			similarUsersRatedSourceItems.put(similarUser, ratedSourceItemsOfOtherUser);
    			}
		        

		        //computer cosine similarity
		        Set<Integer> commonItems = null;
		        HashMap<String, Double> userSimilarities = new HashMap<String, Double>();
		        double cosineSimilarity = 0.0;
		        for (String otherUser : similarUsersRatedSourceItems.keySet()){
		        		commonItems = new HashSet<Integer>();
		        		ratedSourceItemsOfOtherUser = similarUsersRatedSourceItems.get(otherUser);
		        		
		        		commonItems.addAll(ratedSourceItemsOfOtherUser);
		        		commonItems.retainAll(ratedSourceItemsOfUser);
		        		
		        		cosineSimilarity = Double.valueOf(commonItems.size()) / 
		        							(Math.sqrt(ratedSourceItemsOfUser.size()) * 
		        							Math.sqrt(ratedSourceItemsOfOtherUser.size()));

		        		if (cosineSimilarity >= ((LinkAnalysisRecConfig)getRecConfig()).getCosineSimilarityThreshold()){
		        			userSimilarities.put(otherUser, cosineSimilarity);
		        		}
		        }
		        
		        ArrayList<Integer> ratingTargetItemBySimilarUsers = new ArrayList<Integer>();
		        for (String someUser : userSimilarities.keySet()){
			        	query = "SELECT * \n"
								+ "WHERE { \n"
								+ "<" + someUser + "> " + ratingPredicate + " <" + targetItem + "> ."
								+ "}\n";
						
						tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
			  			result = tupleQuery.evaluate();
			  			
			  			ratedSourceItemsOfOtherUser = new HashSet<Integer>();

			  			if (result.hasNext()){
			  					ratingTargetItemBySimilarUsers.add(1);
			  			} else {
			  					ratingTargetItemBySimilarUsers.add(0);
			  			}
		        }
    			
    			conn.close();
    			
    			Integer sum = 0;
				Double avg = 0.0;
    			if (ratingTargetItemBySimilarUsers.isEmpty()){
    					return avg;
    			}
    			else {
    					for (Integer i : ratingTargetItemBySimilarUsers){
    							sum += i;
    					}
    					avg = Double.valueOf(sum) / ratingTargetItemBySimilarUsers.size();
    					return avg;
    			}
        }
        
        
        private double getAvgPathLength(Integer userIndex, Integer targetIndex){
	        	try{
	        			String targetURI = getStorage().getURI(targetIndex);
	        			String sourceURI = null;

	        			String query = null;
				        TupleQueryResult result = null;
				        TupleQuery tupleQuery;
				        
				        String baseURI = ((LinkAnalysisRecConfig)getRecConfig()).getBaseURI();
				        Integer distance = null;
				        Integer totalNumberOfPaths = 0;
	        			
	        			conn = sailRep.getConnection();
	        			
	        			Set<Integer> ratedSourceItems = getRatedSourceItemsOfUser(userIndex);
	        			
	        			for (Integer sourceIndex: ratedSourceItems) {
	        					sourceURI = getStorage().getURI(sourceIndex);

					          	query = "SELECT (COUNT(?v) as ?count) \n"
					              		+ "WHERE { \n"
					              		+ "<" + sourceURI + "> (<" + baseURI + "/>|!<" + baseURI + "/>)* ?u. \n"
					              		+ " ?u ?p ?v. \n"
					              		+ " ?v (<" + baseURI + "/>|<" + baseURI + "/>)* <" + targetURI + ">. \n }";
					          		
					          	tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
					  			result = tupleQuery.evaluate(); 
					  			BindingSet bs = result.next();                                 
					  			String hops = bs.getValue("?count".replace("?","")).stringValue();
								distance = Integer.valueOf(hops);
								totalNumberOfPaths = totalNumberOfPaths + distance;
	        			}
						
						conn.close();
			  			
			  			return totalNumberOfPaths / ratedSourceItems.size();
        	
	        } catch (RepositoryException ex) {
		            throw new RecommenderException(ex);
		    } catch (MalformedQueryException ex) {
		            throw new RecommenderException(ex);
		    } catch (QueryEvaluationException ex) {
		            throw new RecommenderException(ex);
		    }
        }
        
        private int getTotalLikesOfTargetItem(Integer targetIndex) throws RecommenderException {
    		try{
    				String targetItem = getStorage().getURI(targetIndex);

		          	conn = sailRep.getConnection();
		
		          	String query = null;
			        TupleQueryResult result = null;
			        TupleQuery tupleQuery;
			         
			        String countStr = null;
			        Integer countInt = null;
			        
			        String userNode = getRecConfig().getRecEntity(RecEntity.USER);
			        String targetNode = getRecConfig().getRecEntity(RecEntity.POS_ITEM);
			        String posItemPattern = getRecConfig().getPosGraphPattern();
			        
			        // get the predicate which shows positive rating or likeness
			        Pattern pattern = Pattern.compile("\\" + userNode + " (.+?) \\" + targetNode);
			        Matcher matcher = pattern.matcher(posItemPattern);
			        matcher.find();

		          	query = "SELECT (COUNT(" + userNode + ") as ?count) \n"
		              		+ "WHERE { \n"
		              		+ userNode + " " + matcher.group(1) + " <" + targetItem + ">"
		              		+ "}\n"
		              		+"GROUP BY " + userNode + "\n";
		          	
		          	tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		  			result = tupleQuery.evaluate(); 
		  			BindingSet bs = result.next();                                 
		  			countStr = bs.getValue("?count".replace("?","")).stringValue();
		  			countInt = Integer.valueOf(countStr);
			          
			        conn.close();
			        
			        return countInt;
		        
	        } catch (RepositoryException ex) {
		            throw new RecommenderException(ex);
		    } catch (MalformedQueryException ex) {
		            throw new RecommenderException(ex);
		    } catch (QueryEvaluationException ex) {
		            throw new RecommenderException(ex);
		    }
        }
        
        //TODO: ZAID:: change this to total likes in target domain
        private int getTotalUserLikes(Integer userIndex) {
        		Set<IndexedRatedRes> irrSet = getStorage().getIndexedRatedResOfUser(userIndex);
        		return irrSet.size();
        }
        
        
        // check if a user has rated a certain item
        private String getUserRatingForItem(Integer userIndex, Integer itemIndex){
        		Set<IndexedRatedRes> irrSet = getStorage().getIndexedRatedResOfUser(userIndex);

        		for (IndexedRatedRes irr: irrSet){
        				if(irr.getResourceId() == itemIndex){
        						return "Like";
        				}
        		}
        		return "NotLike";
        }
        
        
        private String getFeatureValues(Integer userIndex, Integer itemIndex){
        		
        		Double similarUsersPreference = getSimilarUsersPreference(userIndex, itemIndex);
        		Double avgPathLength = getAvgPathLength(userIndex, itemIndex);
        		Integer totalUserLikes = getTotalUserLikes(userIndex);
        		Integer totalLikesOfTargetItem = getTotalLikesOfTargetItem(itemIndex);
        		String userRating = getUserRatingForItem(userIndex, itemIndex);

        		return similarUsersPreference + ", " + avgPathLength + ", " + 
        				totalUserLikes + ", " + totalLikesOfTargetItem + ", " + userRating;
        }
        
        
        private void createArffFileForTraining(String filename) {
        		try{
        			Set<Integer> allUsers = getStorage().getAllUserIndexes();
            		Set<Integer> allTargetItems = ((GraphBasedStorage)getStorage()).getTargetNodes();
            		
            		String Feature = null;

            		PrintWriter trainingfile = new PrintWriter(filename);
            		trainingfile.println("@RELATION User_and_Target_Items\n" +
            				"\n" +
            				"@ATTRIBUTE Preference_Of_Similar_Users REAL\n" + 
            				"@ATTRIBUTE Avg_Path_Length REAL\n" + 
            				"@ATTRIBUTE Total_Items_Liked_By_User INTEGER\n" + 
            				"@ATTRIBUTE Total_Likes_Of_Target_Item INTEGER\n" +
            				"@ATTRIBUTE Target_Item_Like {NotLike, Like}\n" +
            				"\n" +
            				"@DATA");
            		
            		String userURI = null;
            		Integer targetIndex = null;

            		Set<String> recCandidates = null;
            		Set<String> underSampledCandidates = new HashSet<String>();
            		
            		// Perform undersampling on unliked target items.
            		// for every target item liked by any user, get 
            		// three unliked target items. Then define features
            		// for the liked and undersampled unliked target items
            		for (Integer userIndex : allUsers) {
            			userURI = getStorage().getURI(userIndex);
            			Set<IndexedRatedRes> irrSet_i = getStorage().getIndexedRatedResOfUser(userIndex);
            			// get no. of target items liked by the user
            			int totalUserRatingsInTarget = 0;
            			for (IndexedRatedRes irr: irrSet_i){
            					if (allTargetItems.contains(irr.getResourceId())){
            							totalUserRatingsInTarget++;
            					}
            			}
            			int underSamplingSize = totalUserRatingsInTarget * 3;
            			
            			if (underSamplingSize < 1) {
            					underSamplingSize = 1;
            			}

            			recCandidates = getRecCandidates(userURI);

            			if (recCandidates.size() <= underSamplingSize) {
            					underSampledCandidates = recCandidates;
            			} else {
            					List recCandidatesList = new ArrayList(recCandidates);
            					for(int i = 0 ; i < underSamplingSize ; i++){
            							underSampledCandidates.add((String) recCandidatesList.get(i));
            					}
            			}
            			
            			for (String targetURI: underSampledCandidates){
            					targetIndex = getStorage().getIndexOf(targetURI);
            					Feature = getFeatureValues(userIndex, targetIndex);
            					trainingfile.println(Feature);
            				}
            		}
            		            		            		
            		trainingfile.close();
            		
        		} catch (FileNotFoundException ex) {
		            	throw new RecommenderException(ex);
        		}
        }
        
        
        public Classifier trainAndReturnClassifier() throws Exception{
        	try {
        		// loading dataset
        		String filename = ((LinkAnalysisRecConfig)getRecConfig()).getFeatureFileName();
	    		DataSource source = new DataSource(filename);
	    		
	    		Instances dataset = source.getDataSet();
	
	    		// set class index to the last attribute --- this is target class
	    		dataset.setClassIndex(dataset.numAttributes() - 1);
	    		
//	    		if (dataset.classAttribute().isNominal()) {
//	    		    dataset.stratify(numFolds);
//	    		  }
	    		
	    		
	    		AdaBoostM1 classifier = new AdaBoostM1();
	    		classifier.setClassifier(new J48());//needs one base-classifier
	    		classifier.setNumIterations(20);
	    		
	    		//Bagging with Random Tree
//	    		Bagging classifier = new Bagging();
//	    		classifier.setClassifier(new RandomTree());//needs one base-model
//	    		classifier.setNumIterations(25);
	    		
	    		//train the classifier
	    		classifier.buildClassifier(dataset);
	    		
	    		return classifier;
        	} catch (Exception ex) {
        		throw new RecommenderException(ex);
        	}
        }
        
        public double predictML(Integer indexOfNode1, Integer indexOfNode2) {
        		try {
        			String feature_values = getFeatureValues(indexOfNode1, indexOfNode2);
        			
        			PrintWriter  outfile = new PrintWriter("prediction_file.arff");
            		outfile.println("@RELATION User_and_Target_Items\n" +
            				"\n" +
            				"@ATTRIBUTE Preference_Of_Similar_Users REAL\n" + 
            				"@ATTRIBUTE Avg_Path_Length REAL\n" + 
            				"@ATTRIBUTE Total_Items_Liked_By_User INTEGER\n" + 
            				"@ATTRIBUTE Total_Likes_Of_Target_Item INTEGER\n" +
            				"@ATTRIBUTE Target_Item_Like {NotLike, Like}\n" +
            				"\n" +
            				"@DATA");

            		outfile.println(feature_values);
            		outfile.close();
            		
        			DataSource source1 = new DataSource("prediction_file.arff");

        			Instances testDataset = source1.getDataSet();

        			testDataset.setClassIndex(testDataset.numAttributes() - 1);

    				double actualValue = testDataset.firstInstance().classValue();
        				
    				Instance newInst = testDataset.firstInstance();

    				double predictedValue = classifier.classifyInstance(newInst);
    				String actual = testDataset.classAttribute().value((int) actualValue);

    				System.out.println("Prediction result: " + actualValue + ", " + predictedValue);
    				String predicted = testDataset.classAttribute().value((int) predictedValue);
    				System.out.println("Prediction result: " + actual + ", " + predicted);
    				
    				File outfile2 = new File("prediction_file.arff");
    				outfile2.delete();
    				
        			return predictedValue;
        		} catch (Exception ex) {
        			throw new RecommenderException(ex);
        		}
        }
        
        
        @Override
        public double getResRelativeImportance(String node1, String node2) 
                        throws RecommenderException {
        	try {
                //Retrieve the index of user
                int indexOfNode1 = getStorage().getIndexOf(node1);
                //If this cannot be retrieved then throw an exception
                if (indexOfNode1 == -1){
                        throw new RecommenderException(node1 + " was not found in the recommender model");
                }
                
                //Retrieve the index of user
                int indexOfNode2 = getStorage().getIndexOf(node2);
                //If this cannot be retrieved then throw an exception
                if (indexOfNode2 == -1){
                        throw new RecommenderException(node2 + " was not found in the recommender model");
                }
                if (!((GraphBasedStorage)getStorage()).getTargetNodes().contains(indexOfNode2)){
                        throw new RecommenderException(node2 + " is not a target resource");
                }
                
                
                //TODO
                switch(getRecConfig().getRecParadigm()) {
                        case CROSS_DOMAIN_PAGERANK_WITH_PRIORS:
                                //The first node is treated as the user
                                if (!userOfLastRecommendation.equals(node1)) {                                    
                                        if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.UNIFORM) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.UNIFORM) {
                                                        ((GraphBasedStorage)getStorage()).pageRankWithPriorsUniform(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }                                                
                                                else if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).pageRankWithPriorsUniformEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }                                                                                
                                        }                                    
                                        else if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.PRIORS_LIKED_ITEMS) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).pageRankWithPriorsLikesEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }
                                        }
                                        userOfLastRecommendation = node1;
                                }
                                return ((GraphBasedStorage)getStorage()).getPrpVertexScore(indexOfNode2); 
                            
                        case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY:
                                //The first node is treated as the user
                                if (!userOfLastRecommendation.equals(node1)) {
                                        if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.UNIFORM) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.UNIFORM) {
                                                        ((GraphBasedStorage)getStorage()).ksmcUniform(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }                                                
                                                else if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).ksmcUniformEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }
                                                
                                        }
                                        else if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.PRIORS_LIKED_ITEMS) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).ksmcLikesEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }
                                        }
                                        userOfLastRecommendation = node1;
                                }
                                return ((GraphBasedStorage)getStorage()).getKsmVertexScore(indexOfNode2);                                                                  
                        case CROSS_DOMAIN_REWORD:
                                double score = ((GraphBasedStorage)getStorage()).computeRewordRelatedness(indexOfNode1, indexOfNode2);
                                return score;
                        case CROSS_DOMAIN_MACHINE_LEARNING:
	                        	if (!getStorage().getAllUserIndexes().contains(indexOfNode1)){
	                                throw new RecommenderException(node1 + " is not a user.");
	                        	}
                        		//the first node is treated as a user
                        		Set<String> recCandidates = new HashSet<String>();    //ZAID::
                                Set<String> consumedItems = new HashSet<String>();    //ZAID::
                                
                                recCandidates = getRecCandidates(node1);
                        		consumedItems = getConsumedResources(node1);
                        		                        		
//                                System.out.println("ZAID: get similar user pref: " + getSimilarUsersPreference(0, 6));
//                                System.out.println("ZAID: avg path length:" + getAvgPathLength(0, 2));
//                                System.out.println("ZAID: total user likes: " + getTotalUserLikes(0));
//                                System.out.println("ZAID: total likes of target item: " + getTotalLikesOfTargetItem(6));
//                                System.out.println("ZAID: item 1 is " + getStorage().getURI(1));
//                                System.out.println("ZAID: user likes the item?: " + getUserRatingForItem(0, 2));
//                                System.out.println("ZAID: URI is " + getStorage().getURI(0));
//                                System.out.println("ZAID: index is " + getStorage().getIndexOf("http://example.org/graph#resB"));
//                        		getFeatureValues(0, 15);
                        		
//                        		String filename = ((LinkAnalysisRecConfig)getRecConfig()).getFeatureFileName();
//                        		createArffFileForTraining(filename);
//                    			Classifier classifier = trainAndReturnClassifier();
//                        		String feature = getFeatureValues(indexOfNode1, indexOfNode2);
                        		//TODO: check how to predict for 1 feature
//                        		System.out.println("ZAID feature is:: " + feature);
                        		
                        		double score_ml = predictML(indexOfNode1, indexOfNode2);
//                        		double score_ml = ((GraphBasedStorage)getStorage()).computeMachineLearningRelatedness(indexOfNode1, indexOfNode2);
                        		return score_ml;


                }
                return -1.0;
        	} catch (Exception ex) {
        		throw new RecommenderException(ex);
        	}
        }

        @Override
        public void releaseResources() {}
}
