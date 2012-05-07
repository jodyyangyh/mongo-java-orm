package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.MqlFunction;
import com.googlecode.mjorm.mql.MqlFunctionImpl;
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

	public static final MqlFunction MQL_FUNCTION = new MqlFunctionImpl(2) {
		@Override
		protected Criterion doCreate(Object[] values) {
			return new BetweenCriterion(values[0], values[1]);
		}
	};

}
