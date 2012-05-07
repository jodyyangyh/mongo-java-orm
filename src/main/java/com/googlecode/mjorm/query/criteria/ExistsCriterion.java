package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.MqlFieldFunction;
import com.googlecode.mjorm.mql.MqlFieldFunctionImpl;
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
		MQL_EXISTS_FUNCTION = new MqlFieldFunctionImpl() {
		{
			setMinArgs(1);
			setMaxArgs(1);
			setTypes(Boolean.class);
		}
		@Override
		protected Criterion doCreate(Object[] values) {
			Boolean arg = (values.length>0) ? Boolean.class.cast(values[0]) : true;
			return new ExistsCriterion(arg);
		}
		@Override
		protected Criterion doCreate() {
			return new ExistsCriterion(true);
		}
	};

	public static final MqlFieldFunction
		MQL_DOESNT_EXIST_FUNCTION = new MqlFieldFunctionImpl() {
		{
			setMinArgs(1);
			setMaxArgs(1);
			setTypes(Boolean.class);
		}
		@Override
		protected Criterion doCreate(Object[] values) {
			Boolean arg = (values.length>0) ? Boolean.class.cast(values[0]) : false;
			return new ExistsCriterion(arg);
		}
		@Override
		protected Criterion doCreate() {
			return new ExistsCriterion(false);
		}
	};

}
