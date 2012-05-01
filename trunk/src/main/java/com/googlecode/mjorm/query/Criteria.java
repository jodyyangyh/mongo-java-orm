package com.googlecode.mjorm.query;

public interface Criteria<T> {

	/**
	 * Returns the query representation of the {@code Criteria}.
	 * Most of the time this will be a {@link DBObject}, but
	 * it can be anything accepted by the MongoDB java driver.
	 * @return the {@link Object}
	 */
	T toQueryObject();
}
