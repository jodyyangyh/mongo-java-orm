package com.googlecode.mjorm.query.criteria;

import com.mongodb.DBObject;

public abstract class DocumentCriterion
	extends AbstractDBObjectCriterion {

	public abstract DBObject toQueryObject();

}
