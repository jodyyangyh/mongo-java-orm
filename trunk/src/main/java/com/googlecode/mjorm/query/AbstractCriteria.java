package com.googlecode.mjorm.query;

public abstract class AbstractCriteria<T>
	implements Criteria<T> {

	/**
	 * {@inheritDoc}
	 */
	public abstract T toQueryObject();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toQueryObject().toString();
	}

}
