package com.googlecode.mjorm.query;

/**
 * Represents a single criteria used in a {@link Query}.
 * 
 */
public interface Criterion {

	/**
	 * Returns the query representation of the {@code Criterion}.
	 * Most of the time this will be a {@link DBObject}, but
	 * it can be anything accepted by the MongoDB java driver.
	 * 
	 * @param visitor the {@link ValueVisitor}
	 * @return the {@link Object}
	 */
	Object toQueryObject();

}
