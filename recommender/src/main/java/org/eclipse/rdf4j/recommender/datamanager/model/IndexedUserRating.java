/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommender.datamanager.model;

import java.io.Serializable;

/**
 * This class represents the orthogonal concept of a rated resource, i.e. a
 * pair consisting of user id (who provides a rating) and the rating.
 */
public class IndexedUserRating implements Comparable<IndexedUserRating>, Serializable{
        /*--------*
	 * Static *
	 *--------*/  
    
        private static final long serialVersionUID = 43L;

        /*--------*
	 * Fields *
	 *--------*/
        
        private int userId;
        private double rating;

        /*-------------*
	 * Constructor *
	 *-------------*/
        
        public IndexedUserRating(int userId, double rating) {
                this.userId = userId;
                this.rating = rating;
        }
        
        /*---------*
	 * Methods *
	 *---------*/   

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }
        
        //Since most of the times we will only have lists of user ratings that
        //have to be sorted by user id, we need to have a comparator that 
        //only takes the user id into account.
        @Override
        public int compareTo(IndexedUserRating ur) {
                int i = new Integer(userId).compareTo(ur.getUserId());
                if (i == 0) return new Double(rating).compareTo(ur.getRating());
                else return i;
        }

        @Override
        public String toString() {
                String urString = "";
                urString = "(" + userId + "," + rating + ")";
                return urString;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof IndexedRatedRes) {
                        IndexedUserRating ur = (IndexedUserRating)obj;
                        return (this.getUserId()== ur.getUserId() && 
                                this.getRating() ==  ur.getRating());
                }
                return false;
        }

        @Override
        public int hashCode() {
                final int prime = 31;
                double result = 1;
                result = prime * result + userId;
                result = prime * result + rating;
                return (int)result;
        }
}

//This is not necessary as for IndexedRatedRes
/*
class UserRatingComparator implements Comparator<UserRating> {
        @Override
        public int compare(IndexedUserRating ur1, IndexedUserRating ur2) {
                if (ur1.getRating() < ur2.getRating()) {                        
                        return +1;
                }
                if (ur2.getRating() < ur1.getRating()) {
                        return -1;
                }
                return 0;
        }        
}
*/