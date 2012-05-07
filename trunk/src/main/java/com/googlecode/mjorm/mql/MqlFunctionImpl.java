package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.criteria.Criterion;

public class MqlFunctionImpl
	implements MqlFunction {

	private boolean allowQuery	= false;
	private int exactArgs		= -1;
	private int maxArgs			= -1;
	private int minArgs			= -1;
	private Class<?> types[]	= new Class<?>[0];

	public MqlFunctionImpl(boolean allowQuery, int exactArgs, Class<?>... types) {
		this.allowQuery	= allowQuery;
		this.exactArgs	= exactArgs;
		this.types 		= types;
	}

	public MqlFunctionImpl(int exactArgs, Class<?>... types) {
		this.allowQuery	= false;
		this.exactArgs	= exactArgs;
		this.types 		= types;
	}

	public MqlFunctionImpl(boolean allowQuery, int minArgs, int maxArgs, Class<?>... types) {
		this.allowQuery	= allowQuery;
		this.minArgs	= minArgs;
		this.maxArgs	= maxArgs;
		this.types 		= types;
	}

	public MqlFunctionImpl(int minArgs, int maxArgs, Class<?>... types) {
		this.allowQuery	= false;
		this.minArgs	= minArgs;
		this.maxArgs	= maxArgs;
		this.types 		= types;
	}

	public MqlFunctionImpl() {
		this(true, 0);
	}

	protected Criterion doCreate(Object[] values) {
		throw new IllegalArgumentException(
			"Function doesn't implement doCreate(Object[])");
	}

	protected Criterion doCreate() {
		throw new IllegalArgumentException(
			"Function doesn't implement doCreate()");
	}

	protected Criterion doCreate(Query query) {
		throw new IllegalArgumentException(
			"Function doesn't implement doCreate(Query)");
	}

	public Criterion createForQuery(Query query) {
		if (!allowQuery) {
			throw new IllegalArgumentException("Function doesn't take Query as an argument");
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

	public void assertArgumentTypes(Object[] arguments, Class<?> type) {
		for (int i=0; i<arguments.length; i++) {
			if (!type.isInstance(arguments[i])) {
				throw new IllegalArgumentException("Invalid type for argument "+i);
			}
		}
	}

	public void assertArgumentTypes(Object[] arguments, Class<?>[] types) {
		if (arguments.length!=types.length) {
			throw new IllegalArgumentException("Argument length doesn't match type length");
		}
		for (int i=0; i<types.length; i++) {
			if (!types[i].isInstance(arguments[i])) {
				throw new IllegalArgumentException("Invalid type for argument "+i);
			}
		}
	}

	public void assertArgumentLength(Object[] arguments, int length) {
		if (arguments.length!=length) {
			throw new IllegalArgumentException("Invalid argument length");
		}
	}

	public void assertMinimumArgumentLength(Object[] arguments, int length) {
		if (arguments.length<length) {
			throw new IllegalArgumentException("Must have at least "+length+" arguments");
		}
	}

	public void assertMaximumArgumentLength(Object[] arguments, int length) {
		if (arguments.length>length) {
			throw new IllegalArgumentException("Must have noe more than "+length+" arguments");
		}
	}
}
