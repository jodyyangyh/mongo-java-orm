package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class SizeCriteria
	extends AbstractCriteria<DBObject> {

	private Number size;

	public SizeCriteria(Number size) {
		this.size		= size;
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
	public DBObject toQueryObject() {
		return new BasicDBObject("$size", size);
	}

}
