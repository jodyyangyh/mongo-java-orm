package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;

public class ExistsCriterion
	extends AbstractCriterion {

	private Boolean value;

	public ExistsCriterion(Boolean value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject() {
		return new BasicDBObject("$exists", value);
	}

}
