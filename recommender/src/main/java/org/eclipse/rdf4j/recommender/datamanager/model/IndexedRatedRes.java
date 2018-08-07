/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.datamanager.model;

import java.io.Serializable;

/**
 * This class represents a pair resource-rating. A resource could be any node
 * from an RDF-graph.
 */
public class IndexedRatedRes implements Comparable<IndexedRatedRes>, Serializable{
        /*--------*
	 * Static *
	 *--------*/    
    
        private static final long serialVersionUID = 42L;
        
        /*--------*
	 * Fields *
	 *--------*/
        
        private int resourceId;
        private double rating;

        /*-------------*
	 * Constructor *
	 *-------------*/
        
        public IndexedRatedRes(int resourceId, double rating) {
                this.resourceId = resourceId;
                this.rating = rating;
        }

        /*---------*
	 * Methods *
	 *---------*/   
        
        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        @Override
        public int compareTo(IndexedRatedRes rr) {
                int i = new Integer(resourceId).compareTo(rr.getResourceId());
                if (i == 0) return new Double(rating).compareTo(rr.getRating());
                else return i;
        }

        @Override
        public String toString() {
                String rrString = "";
                rrString = "(" + resourceId + "," + rating + ")";
                return rrString;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof IndexedRatedRes) {
                        IndexedRatedRes cv = (IndexedRatedRes)obj;
                        return (this.getResourceId() == cv.getResourceId() && 
                                this.getRating() ==  cv.getRating());
                }
                return false;
        }

        @Override
        public int hashCode() {                
                final int prime = 31;
                double result = 1;
                result = prime * result + resourceId;
                result = prime * result + rating;
                return (int)result;
        }
}