/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.storage.index.spark.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.api.java.JavaPairRDD;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.storage.index.spark.SparkStorage;
import scala.Tuple2;


public class CfSparkStorage extends SparkStorage {
        
    /**
     * Maps that stores neighborhoods. Key is a user or item ID.
     */ 
    private JavaPairRDD<Integer, IndexedRatedRes[]> neighborhoods;
    
    public CfSparkStorage() {
        List<Tuple2<Integer, IndexedRatedRes[]>> emptyList1 = new ArrayList<>();
        neighborhoods = sc.parallelizePairs(emptyList1);
    }
    
    public IndexedRatedRes[] getNeighborhood(int index) {
        
        List<IndexedRatedRes[]> resultList = neighborhoods.lookup(index);         
        
        if( !resultList.isEmpty() ) {
            return resultList.get(0);
        }
        
        IndexedRatedRes[] emptyList = new IndexedRatedRes[0];
        return emptyList;
    }                

    public void storeNeighborhood(int index, IndexedRatedRes[] neighborhood) {
        
        List<Tuple2<Integer, IndexedRatedRes[]>> tempList = new ArrayList<>();
        Tuple2<Integer, IndexedRatedRes[]> tuple = new Tuple2(index,neighborhood); 
        tempList.add(tuple);
        
        JavaPairRDD<Integer, IndexedRatedRes[]> tempNeighborhoddRDD = sc.parallelizePairs(tempList);
        
        neighborhoods = neighborhoods.union(tempNeighborhoddRDD);
    }                       

    //SOME GETTERS FOR TEST PURPOSES     
    public Map<Integer, IndexedRatedRes[]> getNeighborhoods() {
        
        List<Tuple2<Integer,IndexedRatedRes[]>> collectedList = neighborhoods.collect();
        
        Map<Integer, IndexedRatedRes[]> resultMap = new HashMap<>();
        
        collectedList.stream().forEach((tuple) -> {
            resultMap.put(tuple._1(), tuple._2());
        });
        
        return resultMap;
    }

    @Override
    public void resetStorage() {
        super.resetStorage();
        resetNeighborhoods();
    }
    
    private void resetNeighborhoods() {
        List<Tuple2<Integer, IndexedRatedRes[]>> emptyList1 = new ArrayList<>();
        neighborhoods = sc.parallelizePairs(emptyList1);
    }
}
