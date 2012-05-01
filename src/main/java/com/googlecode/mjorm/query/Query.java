package com.googlecode.mjorm.query;

public class Query
	extends QueryCriterion<Query> {

	/**
	 * Method to make chaining look cleaner.
	 * @return a new {@link Query}
	 */
	public static Query start() {
		return new Query();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Query self() {
		return this;
	}

}
