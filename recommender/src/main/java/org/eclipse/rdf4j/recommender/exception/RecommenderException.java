/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.exception;

import org.eclipse.rdf4j.RDF4JException;

/**
 * An exception thrown by classes from the Recommender Repository API to 
 * indicate an error.
 */
public class RecommenderException extends RDF4JException {

	public RecommenderException() {
		super();
	}

	public RecommenderException(String msg) {
		super(msg);
	}

	public RecommenderException(Throwable t) {
		super(t);
	}

	public RecommenderException(String msg, Throwable t) {
		super(msg, t);
	}
}
