package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;

public class ElemMatchCriterion
	implements Criterion {

	private Query query;

	public ElemMatchCriterion(Query query) {
		this.query = query;
	}

	public ElemMatchCriterion() {
		this.query = new Query();
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toQueryObject() {
		return new BasicDBObject("$elemMatch", query.toQueryObject());
	}

}
