package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.MqlFunction;
import com.googlecode.mjorm.mql.MqlFunctionImpl;
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

	public static final MqlFunction
		MQL_EXISTS_FUNCTION = new MqlFunctionImpl(0, 1, Boolean.class) {
		@Override
		protected Criterion doCreate(Object[] values) {
			Boolean arg = (values.length>0) ? Boolean.class.cast(values[0]) : true;
			return new ExistsCriterion(arg);
		}
	};

	public static final MqlFunction
		MQL_DOESNT_EXIST_FUNCTION = new MqlFunctionImpl(0, Boolean.class) {
		@Override
		protected Criterion doCreate(Object[] values) {
			return new ExistsCriterion(false);
		}
	};

}
