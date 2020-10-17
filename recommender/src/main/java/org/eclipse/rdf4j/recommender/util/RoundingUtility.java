/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.util;

/**
 * Class created to provide rounding options for the data stored.
 */
//TODO fix erros
public class RoundingUtility {
        /**
         * Rounds a double by considering a number of decimal places.
         * @param value
         * @param places
         * @return 
         */
        //This method has been disabled because it is giving out of memory
        //errors.
        /*
        public static double round(double value, int places) {
                if (places < 0) throw new IllegalArgumentException();
                BigDecimal bd = new BigDecimal(value);
                bd = bd.setScale(places, RoundingMode.HALF_UP);
                return bd.doubleValue();
        }
        */
}
