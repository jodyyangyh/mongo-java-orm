package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ElemMatchCriteria
	implements Criteria<DBObject> {

	private Query query;

	public ElemMatchCriteria(Query query) {
		this.query = query;
	}

	public ElemMatchCriteria() {
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
	public DBObject toQueryObject() {
		return new BasicDBObject("$elemMatch", query.toQueryObject());
	}

}
