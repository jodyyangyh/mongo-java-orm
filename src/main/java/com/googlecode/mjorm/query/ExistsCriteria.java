package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ExistsCriteria
	extends AbstractCriteria<DBObject> {

	private Boolean value;

	public ExistsCriteria(Boolean value) {
		this.value		= value;
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
	public DBObject toQueryObject() {
		return new BasicDBObject("$exists", value);
	}

}
