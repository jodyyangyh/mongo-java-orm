package com.googlecode.mjorm.query.criteria;

import com.mongodb.BasicDBObject;

public class BetweenCriterion
	extends AbstractCriterion {

	private Object left;
	private Object right;

	public BetweenCriterion(Object left, Object right) {
		this.left	= left;
		this.right	= right;
	}

	@Override
	public Object toQueryObject() {
		BasicDBObject ret = new BasicDBObject();
		ret.put("$gte", left);
		ret.put("$lte", right);
		return ret;
	}

}
