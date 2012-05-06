package com.googlecode.mjorm.query.criteria;

import com.mongodb.BasicDBObject;

public class SizeCriterion
	extends AbstractCriterion {

	private Number size;

	public SizeCriterion(Number size) {
		this.size = size;
	}

	/**
	 * @return the size
	 */
	public Number getSize() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject() {
		return new BasicDBObject("$size", size);
	}

}
