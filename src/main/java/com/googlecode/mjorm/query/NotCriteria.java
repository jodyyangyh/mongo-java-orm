package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class NotCriteria
	extends AbstractCriteria<DBObject> {

	private Criteria<?> criteria;

	public NotCriteria(Criteria<?> criteria) {
		this.criteria	= criteria;
	}

	/**
	 * @return the criteria
	 */
	public Criteria<?> getCriteria() {
		return criteria;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DBObject toQueryObject() {
		return new BasicDBObject("$not", criteria.toQueryObject());
	}

}
