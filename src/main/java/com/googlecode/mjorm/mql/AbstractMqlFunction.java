package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;

public class AbstractMqlFunction<T> {

	protected boolean allowQueryGroup	= false;
	protected boolean allowQuery		= false;
	protected int exactArgs 			= -1;
	protected int maxArgs 				= -1;
	protected int minArgs				= -1;
	protected Class<?> types[]			= new Class<?>[0];


	protected T doCreate(Object[] values) {
		throw new IllegalArgumentException(
			this.getClass().getName()+" doesn't implement doCreate(Object[])");
	}

	protected T doCreate() {
		throw new IllegalArgumentException(
			this.getClass().getName()+" doesn't implement doCreate()");
	}

	protected T doCreate(Query query) {
		throw new IllegalArgumentException(
			this.getClass().getName()+" doesn't implement doCreate(Query)");
	}

	protected T doCreate(QueryGroup queryGroup) {
		throw new IllegalArgumentException(
			this.getClass().getName()+" doesn't implement doCreate(QueryGroup)");
	}
	
	public T createForQuery(Query query) {
		if (!allowQuery) {
			throw new IllegalArgumentException(
				this.getClass().getName()+" doesn't take Query as an argument");
		}
		return doCreate(query);
	}

	public T createForQueryGroup(QueryGroup queryGroup) {
		if (!allowQueryGroup) {
			throw new IllegalArgumentException(
				this.getClass().getName()+" doesn't take QueryGroup as an argument");
		}
		return doCreate(queryGroup);
	}

	public T createForArguments(Object[] values) {
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

	public T createForNoArguments() {
		return doCreate();
	}

	/**
	 * @param allowQueryGroup the allowQueryGroup to set
	 */
	protected void setAllowQueryGroup(boolean allowQueryGroup) {
		this.allowQueryGroup = allowQueryGroup;
	}

	/**
	 * @param allowQuery the allowQuery to set
	 */
	protected void setAllowQuery(boolean allowQuery) {
		this.allowQuery = allowQuery;
	}

	/**
	 * @param exactArgs the exactArgs to set
	 */
	protected void setExactArgs(int exactArgs) {
		this.exactArgs = exactArgs;
	}

	/**
	 * @param maxArgs the maxArgs to set
	 */
	protected void setMaxArgs(int maxArgs) {
		this.maxArgs = maxArgs;
	}

	/**
	 * @param minArgs the minArgs to set
	 */
	protected void setMinArgs(int minArgs) {
		if (minArgs==0) {
			throw new IllegalArgumentException("minArgs must be > 0");
		}
		this.minArgs = minArgs;
	}

	/**
	 * @param types the types to set
	 */
	protected void setTypes(Class<?>... types) {
		this.types = types;
	}

	/**
	 * @return the allowQueryGroup
	 */
	protected boolean isAllowQueryGroup() {
		return allowQueryGroup;
	}

	/**
	 * @return the allowQuery
	 */
	protected boolean isAllowQuery() {
		return allowQuery;
	}

	/**
	 * @return the exactArgs
	 */
	protected int getExactArgs() {
		return exactArgs;
	}

	/**
	 * @return the maxArgs
	 */
	protected int getMaxArgs() {
		return maxArgs;
	}

	/**
	 * @return the minArgs
	 */
	protected int getMinArgs() {
		return minArgs;
	}

	/**
	 * @return the types
	 */
	protected Class<?>[] getTypes() {
		return types;
	}

	protected void assertArgumentTypes(Object[] arguments, Class<?> type) {
		for (int i=0; i<arguments.length; i++) {
			if (arguments[i]!=null && !type.isInstance(arguments[i])) {
				throw new IllegalArgumentException("Invalid type for argument "+i);
			}
		}
	}

	protected void assertArgumentTypes(Object[] arguments, Class<?>[] types) {
		if (arguments.length!=types.length) {
			throw new IllegalArgumentException("Argument length doesn't match type length");
		}
		for (int i=0; i<types.length; i++) {
			if (!types[i].isInstance(arguments[i])) {
				throw new IllegalArgumentException("Invalid type for argument "+i);
			}
		}
	}

	protected void assertArgumentLength(Object[] arguments, int length) {
		if (arguments.length!=length) {
			throw new IllegalArgumentException("Invalid argument length");
		}
	}

	protected void assertMinimumArgumentLength(Object[] arguments, int length) {
		if (arguments.length<length) {
			throw new IllegalArgumentException("Must have at least "+length+" arguments");
		}
	}

	protected void assertMaximumArgumentLength(Object[] arguments, int length) {
		if (arguments.length>length) {
			throw new IllegalArgumentException("Must have noe more than "+length+" arguments");
		}
	}

}