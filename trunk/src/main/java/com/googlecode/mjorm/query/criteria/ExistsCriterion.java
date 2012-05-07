package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.AbstractMqlFieldFunction;
import com.googlecode.mjorm.mql.MqlFieldFunction;
import com.mongodb.BasicDBObject;

public class ExistsCriterion
	extends AbstractCriterion {

	private Boolean value;

	public ExistsCriterion(Boolean value) {
		this.value = value;
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
	public Object toQueryObject() {
		return new BasicDBObject("$exists", value);
	}

	public static final MqlFieldFunction
		MQL_EXISTS_FUNCTION = new AbstractMqlFieldFunction(0, 1, Boolean.class) {
		@Override
		protected Criterion doCreate(Object[] values) {
			Boolean arg = (values.length>0) ? Boolean.class.cast(values[0]) : true;
			return new ExistsCriterion(arg);
		}
	};

	public static final MqlFieldFunction
		MQL_DOESNT_EXIST_FUNCTION = new AbstractMqlFieldFunction(0, Boolean.class) {
		@Override
		protected Criterion doCreate(Object[] values) {
			return new ExistsCriterion(false);
		}
	};

}
