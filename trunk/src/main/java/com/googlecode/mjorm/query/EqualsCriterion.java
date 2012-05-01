package com.googlecode.mjorm.query;

public class EqualsCriterion
	extends AbstractCriterion {

	private Object value;

	public EqualsCriterion(Object value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toQueryObject() {
		return value;
	}

}
