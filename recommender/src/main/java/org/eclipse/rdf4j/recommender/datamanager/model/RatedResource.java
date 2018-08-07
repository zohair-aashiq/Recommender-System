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
public class RatedResource implements Comparable<RatedResource>, Serializable{
        /*--------*
	 * Static *
	 *--------*/    
    
        private static final long serialVersionUID = 71L;
        
        /*--------*
	 * Fields *
	 *--------*/
        
        private String resource;
        private double rating;

        /*-------------*
	 * Constructor *
	 *-------------*/
        
        public RatedResource(String resource, double rating) {
                this.resource = resource;
                this.rating = rating;
        }

        /*---------*
	 * Methods *
	 *---------*/   
        
        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        @Override
        public int compareTo(RatedResource rr) {
                int i = resource.compareTo(rr.getResource());
                if (i == 0) return new Double(rating).compareTo(rr.getRating());
                else return i;
        }

        @Override
        public String toString() {
                String rrString = "";
                rrString = "(" + resource + "," + rating + ")";
                return rrString;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof RatedResource) {
                        RatedResource cv = (RatedResource)obj;
                        return (this.getResource().equals(cv.getResource()) && 
                                this.getRating() ==  cv.getRating());
                }
                return false;
        }

        @Override
        public int hashCode() {                
                final int prime = 31;
                double result = 1;
                result = prime * result + resource.hashCode();
                result = prime * result + rating;
                return (int)result;
        }
}