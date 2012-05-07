package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.MqlFunction;
import com.googlecode.mjorm.mql.MqlFunctionImpl;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class ModCriterion
	extends AbstractCriterion {

	private Number left;
	private Number right;

	public ModCriterion(Number left, Number right) {
		this.left		= left;
		this.right		= right;
	}

	/**
	 * @return the left
	 */
	public Number getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public Number getRight() {
		return right;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject() {
		BasicDBList numbers = new BasicDBList();
		numbers.add(left);
		numbers.add(right);
		return new BasicDBObject("$mod", numbers);
	}

	public static final MqlFunction MQL_FUNCTION = new MqlFunctionImpl(2, Number.class) {
		@Override
		protected Criterion doCreate(Object[] values) {
			return new ModCriterion(
				Number.class.cast(values[0]),
				Number.class.cast(values[1]));
		}
	};

}
