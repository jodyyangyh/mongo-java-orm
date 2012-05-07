package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.MqlFieldFunction;
import com.googlecode.mjorm.mql.MqlFieldFunctionImpl;
import com.mongodb.BasicDBObject;

public class SizeCriterion
	extends AbstractCriterion {

	private Number size;

	public SizeCriterion(Number size) {
		this.size = size;
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
	public Object toQueryObject() {
		return new BasicDBObject("$size", size);
	}

	public static final MqlFieldFunction MQL_FUNCTION = new MqlFieldFunctionImpl() {
		{
			setExactArgs(1);
			setTypes(Number.class);
		}
		@Override
		protected Criterion doCreate(Object[] values) {
			return new SizeCriterion(Number.class.cast(values[0]));
		}
	};
}
