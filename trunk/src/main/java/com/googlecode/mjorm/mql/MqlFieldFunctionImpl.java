package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.criteria.Criterion;

public class MqlFieldFunctionImpl
	extends AbstractMqlFunction
	implements MqlFieldFunction {

	protected Criterion doCreate(Object[] values) {
		throw new IllegalArgumentException(
			"MqlFieldFunction doesn't implement doCreate(Object[])");
	}

	protected Criterion doCreate() {
		throw new IllegalArgumentException(
			"MqlFieldFunction doesn't implement doCreate()");
	}

	protected Criterion doCreate(Query query) {
		throw new IllegalArgumentException(
			"MqlFieldFunction doesn't implement doCreate(Query)");
	}

	public Criterion createForQuery(Query query) {
		if (!allowQuery) {
			throw new IllegalArgumentException(
				"MqlFieldFunction doesn't take Query as an argument");
		}
		return doCreate(query);
	}

	public Criterion createForArguments(Object[] values) {
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

	public Criterion createForNoArguments() {
		return doCreate();
	}
}
