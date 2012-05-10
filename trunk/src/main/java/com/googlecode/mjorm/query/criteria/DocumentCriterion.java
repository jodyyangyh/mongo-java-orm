package com.googlecode.mjorm.query.criteria;

import com.mongodb.DBObject;

public interface DocumentCriterion
	extends Criterion {

	DBObject toQueryObject();
}
