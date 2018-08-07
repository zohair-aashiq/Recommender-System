/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.storage.index.spark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.InvertedList;
import org.eclipse.rdf4j.recommender.storage.Storage;
import scala.Tuple2;

public abstract class SparkStorage implements Storage {

    //Counts the number of resources.
    private int resourceCounter = 0;
    private double minRating    = -1.0;
    private double maxRating    = 1.0;
            
    // TODO discuss: why <String, Integer> instead of <Integer,String>
    // KEY is an URI (resource) and value is the index of the array in which the resource is stored.
    private JavaPairRDD<Integer, String> resourceIdMap;   
    
    //KEY is the ID of a user. Value is a list of rated resources.
    private JavaPairRDD<Integer, Set<IndexedRatedRes>> userRatedItemsMap;
    //Each user has a set of rated resources. In order to speed up the computation 
    //of similarities, each rating is divided by the l-norm. This Maps stores them
    //in order to be able to return non normalized ratings.
    private JavaPairRDD<Integer, Double> usersL2Norms;
    
    private JavaPairRDD<Integer,InvertedList> resInvertedLists; 
        
    protected JavaSparkContext sc;
    
    
    public SparkStorage() {
        
        SparkConf conf = new SparkConf().setAppName("RDF4J: Recommender System").setMaster("local");
        sc = new JavaSparkContext(conf);
        
        initializeRDDs();
    }
    
    @Override
    public void resetStorage() {
        
        resourceCounter = 0;
        minRating       = -1.0;
        maxRating       = 1.0;    
        
        initializeRDDs();
    }

    private void initializeRDDs() {        
        List<Tuple2<Integer, String>> emptyList1 = new ArrayList<>();
        List<Tuple2<Integer, Set<IndexedRatedRes>>> emptyList2 = new ArrayList<>();
        List<Tuple2<Integer, Double>> emptyList3 = new ArrayList<>();
        List<Tuple2<Integer, InvertedList>> emptyList4 = new ArrayList<>();
        resourceIdMap       = sc.parallelizePairs(emptyList1);
        userRatedItemsMap   = sc.parallelizePairs(emptyList2);
        usersL2Norms        = sc.parallelizePairs(emptyList3);
        resInvertedLists    = sc.parallelizePairs(emptyList4);
    }
    
    public int getIndexOf(String URI) {
        
        JavaPairRDD<String, Integer> tempMap = resourceIdMap.mapToPair(
            t1 -> new Tuple2<String, Integer>(t1._2(), t1._1()));   
        
        
        List<Integer> index = tempMap.lookup(URI);
        
        if( !index.isEmpty() )
            return index.get(0);
        else return -1;
    }
    
    public String getURI(int key) {
        
        List<String> uri = resourceIdMap.lookup(key);
        
        // TODO ask
        if( uri.isEmpty() ){
            return null;
        }
        else{
            return uri.get(0);
        }
    }
    
    public Set<IndexedRatedRes> getIndexedRatedResOfUser(int indexOfUser) {     
        
        List<Set<IndexedRatedRes>> resList = userRatedItemsMap.lookup(indexOfUser);
        
        if( !resList.isEmpty() ) {
            return resList.get(0);
        }
        return null;
    }
        
    public Set<Integer> getAllUserIndexes() {
        
        JavaRDD keys = userRatedItemsMap.keys();
        
        List<Integer> keysList = keys.collect();
        
        return new HashSet<>(keysList);
    }
    
    public Map<Integer, Double> getUsersL2NormsMap() {
        List<Tuple2<Integer, Double>> resultList = usersL2Norms.collect();
        
        HashMap<Integer,Double> resultMap = new HashMap<>();
        
        resultList.stream().forEach((tuple) -> {
            resultMap.put(tuple._1(), tuple._2());
        }); 
        
        return resultMap;
    }        
     
    public Map<Integer, String> getResourceIdMap() {
    
        List<Tuple2<Integer, String>> resultList = resourceIdMap.collect();
        
        HashMap<Integer,String> resultMap = new HashMap<>();
        
        resultList.stream().forEach((tuple) -> {
            resultMap.put(tuple._1(), tuple._2());
        }); 
        
        return resultMap;
    }
    
    /**
     * Adds rated resources to userRatedItemsMap using given tupleList.
     * 
     * @param tupleList 
     */
    public void addRatedResources(List<Tuple2<Integer,Set<IndexedRatedRes>>> tupleList) {        
        JavaPairRDD<Integer,Set<IndexedRatedRes>> tempUserRatedItemsMap = sc.parallelizePairs(tupleList);
        userRatedItemsMap = userRatedItemsMap.union(tempUserRatedItemsMap);
    }
    
    /**
     * Sets rated resources to userRatedItemsMap using given tupleList, instead of adding.
     * 
     * @param tupleList 
     */
    public void setRatedResources(List<Tuple2<Integer,Set<IndexedRatedRes>>> tupleList) {        
        userRatedItemsMap = sc.parallelizePairs(tupleList);
    }
     
    /**
     * Adds resources to resourceIdMap using given resourceToAddList.
     * 
     * @param resourceToAddList 
     */  
    public void addResources(List<Tuple2<Integer,String>> resourceToAddList) {
        JavaPairRDD<Integer,String> resourcesToAdd = sc.parallelizePairs(resourceToAddList);
        resourceIdMap = resourceIdMap.union(resourcesToAdd);
    }
    
    /**
     * Sets resourceIdMap using given resourceToAddList, instead of adding.
     * 
     * @param resourceToAddList 
     */
    public void setResources(List<Tuple2<Integer,String>> resourceToAddList) {
        resourceIdMap = sc.parallelizePairs(resourceToAddList);
    }
    
    /**
     * Adds usersL2Norms using given l2NormsOfUsersList.
     * 
     * @param l2NormsOfUsersList 
     */
    public void addL2NormsOfUsers(List<Tuple2<Integer,Double>> l2NormsOfUsersList) {
        JavaPairRDD<Integer,Double> l2NormsToAdd = sc.parallelizePairs(l2NormsOfUsersList);
        usersL2Norms = usersL2Norms.union(l2NormsToAdd);
    }
    
    /**
     * Sets usersL2Norms using given l2NormsOfUsersList, instead of adding.
     * 
     * @param l2NormsOfUsersList 
     */
    public void setL2NormsOfUsers(List<Tuple2<Integer,Double>> l2NormsOfUsersList) {
        usersL2Norms = sc.parallelizePairs(l2NormsOfUsersList);
    }
    
    /**
     * Gets userRatedItemsMap as a list.
     * 
     * @return 
     */
    public List<Tuple2<Integer, Set<IndexedRatedRes>>> getUserRatedItemsList() {        
        return userRatedItemsMap.collect();
    }
    
    public List<Tuple2<Integer,String>> getResourceList() {
        return resourceIdMap.collect();
    }
    
    public List<String> getResourceStrings() {
        return resourceIdMap.values().collect();
    }
    
    public List<Integer> getResourceIds() {
        return resourceIdMap.keys().collect();
    }
    
    public JavaPairRDD<Integer,String> getResources() {
        return resourceIdMap;
    }
        
    public int getResourceCounter() {
        return resourceCounter;
    }
        
    public void setResourceCounter(int count) {
        this.resourceCounter = count;
    }
        
    public Double getDatasetMinRating() {
        return minRating;
    }

    public Double getDatasetMaxRating() {
        return maxRating;
    }
    
    public Map<Integer, Set<IndexedRatedRes>> getUserRatedItemsMap() {
        
        List<Tuple2<Integer, Set<IndexedRatedRes>>> userRatedItemsList = userRatedItemsMap.collect();
        
        Map<Integer, Set<IndexedRatedRes>> resultMap = new HashMap<>();
        
        userRatedItemsList.stream().forEach((tuple) -> {
            resultMap.put(tuple._1(), tuple._2());
        });
        
        return resultMap;
    }
    
    public void addInvLists(List<Tuple2<Integer, InvertedList>> invListsToAdd) {
        JavaPairRDD<Integer,InvertedList> invListsRDD = sc.parallelizePairs(invListsToAdd);
        resInvertedLists = resInvertedLists.union(invListsRDD);
    }
    
    public Map<Integer, InvertedList> getResInvertedLists() {
        
        List<Tuple2<Integer, InvertedList>> userRatedItemsList = resInvertedLists.collect();
        
        Map<Integer, InvertedList> resultMap = new HashMap<>();
        
        userRatedItemsList.stream().forEach((tuple) -> {
            resultMap.put(tuple._1(), tuple._2());
        });
        
        return resultMap;
    }
    
    
    public InvertedList getInvertedListOfItem(int indexOfItem) {
        return getResInvertedLists().get(indexOfItem);
    }
    
    public void stopSpark() {
        sc.stop();
    }
    
    /**
     * For testing.
     * 
     * @param indexOfUser
     * @return 
     */
    public double getL2NormOfUser(int indexOfUser) {
        List<Double> resultList = usersL2Norms.lookup(indexOfUser);
        
        // TODO ask empty
        if( resultList.isEmpty() ) {
            return 0.0;
        }
        
        return resultList.get(0);
    }   
}
