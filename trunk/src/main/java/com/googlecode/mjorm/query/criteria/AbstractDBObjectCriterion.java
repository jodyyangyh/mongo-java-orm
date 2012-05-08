package com.googlecode.mjorm.query.criteria;

import com.mongodb.DBObject;

public abstract class AbstractDBObjectCriterion
	extends AbstractCriterion {

	@Override
	public abstract DBObject toQueryObject();

}
