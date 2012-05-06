package com.googlecode.mjorm.query.criteria;

import com.mongodb.BasicDBObject;

public class NotCriterion
	extends AbstractCriterion {

	private Criterion criterion;

	public NotCriterion(Criterion criterion) {
		this.criterion	= criterion;
	}

	/**
	 * @return the criterion
	 */
	public Criterion getCriterion() {
		return criterion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject() {
		return new BasicDBObject("$not", criterion.toQueryObject());
	}

}
