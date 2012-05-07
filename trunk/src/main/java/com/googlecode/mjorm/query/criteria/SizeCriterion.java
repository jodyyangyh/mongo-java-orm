package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.MqlFunction;
import com.googlecode.mjorm.mql.MqlFunctionImpl;
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

	public static final MqlFunction MQL_FUNCTION = new MqlFunctionImpl(1, Number.class) {
		@Override
		protected Criterion doCreate(Object[] values) {
			return new SizeCriterion(Number.class.cast(values[0]));
		}
	};
}
