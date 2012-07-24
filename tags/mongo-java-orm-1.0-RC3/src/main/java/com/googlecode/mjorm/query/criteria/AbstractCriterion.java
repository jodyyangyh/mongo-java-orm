package com.googlecode.mjorm.query.criteria;

public abstract class AbstractCriterion
	implements Criterion {

	/**
	 * {@inheritDoc}
	 */
	public abstract Object toQueryObject();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		Object queryObj = toQueryObject();
		return (queryObj!=null) ? queryObj.toString() : "null";
	}

}
