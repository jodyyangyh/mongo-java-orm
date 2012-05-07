package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.criteria.Criterion;

public abstract class AbstractMqlFieldFunction
	extends AbstractMqlFunction
	implements MqlFieldFunction {

	private int exactArgs		= -1;
	private int maxArgs			= -1;
	private int minArgs			= -1;
	private Class<?> types[]	= new Class<?>[0];

	public AbstractMqlFieldFunction(int exactArgs, Class<?>... types) {
		this.exactArgs	= exactArgs;
		this.types 		= types;
	}

	public AbstractMqlFieldFunction(int minArgs, int maxArgs, Class<?>... types) {
		this.minArgs	= minArgs;
		this.maxArgs	= maxArgs;
		this.types 		= types;
	}

	public Criterion createCriterion(Object[] values) {
		if (maxArgs!=-1) {
			assertMaximumArgumentLength(values, maxArgs);
		}
		if (minArgs!=-1) {
			assertMinimumArgumentLength(values, minArgs);
		}
		if (exactArgs!=-1) {
			assertArgumentLength(values, exactArgs);
		}
		if (types.length==1) {
			assertArgumentTypes(values, types[0]);
		}
		if (types.length>1) {
			assertArgumentTypes(values, types);
		}
		return doCreate(values);
	}

	protected abstract Criterion doCreate(Object[] values);
}
