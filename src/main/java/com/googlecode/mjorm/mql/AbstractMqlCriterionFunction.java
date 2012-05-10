package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.Criterion;

public abstract class AbstractMqlCriterionFunction
	implements MqlCriterionFunction {

	private String functionName				= "unknown";
	private boolean allowQueryGroup		= false;
	private boolean allowQuery				= false;
	private int exactArgs					= -1;
	private int maxArgs					= -1;
	private int minArgs					= -1;
	private Class<?> types[]				= new Class<?>[0];
	private boolean initialized			= false;
	private boolean strictInitialization	= true;

	public AbstractMqlCriterionFunction() {
		initialized = false;
		init();
		if (functionName==null || functionName.trim().length()==0) {
			throw new IllegalArgumentException("Invalid function name");
		}
		initialized = true;
	}

	protected abstract void init();

	protected Criterion doCreate(Object[] values) {
		throw new IllegalArgumentException(
			functionName+" doesn't implement doCreate(Object[])");
	}

	protected Criterion doCreate() {
		throw new IllegalArgumentException(
			functionName+" doesn't implement doCreate()");
	}

	protected Criterion doCreate(Query query) {
		throw new IllegalArgumentException(
			functionName+" doesn't implement doCreate(Query)");
	}

	protected Criterion doCreate(QueryGroup queryGroup) {
		throw new IllegalArgumentException(
			functionName+" doesn't implement doCreate(QueryGroup)");
	}
	
	public Criterion createForQuery(Query query) {
		if (!allowQuery) {
			throw new IllegalArgumentException(
				functionName+" doesn't take Query as an argument");
		}
		return doCreate(query);
	}

	public Criterion createForQueryGroup(QueryGroup queryGroup) {
		if (!allowQueryGroup) {
			throw new IllegalArgumentException(
				functionName+" doesn't take QueryGroup as an argument");
		}
		return doCreate(queryGroup);
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

	public String getName() {
		return functionName;
	}

	protected void assertNotInitialized() {
		if (strictInitialization && initialized) {
			throw new IllegalStateException("Function "+functionName+" already initialized");
		}
	}
	
	protected void assertArgumentTypes(Object[] arguments, Class<?> type) {
		for (int i=0; i<arguments.length; i++) {
			if (arguments[i]!=null && !type.isInstance(arguments[i])) {
				throw new IllegalArgumentException("Invalid type for argument "+i+" in function "+functionName);
			}
		}
	}

	protected void assertArgumentTypes(Object[] arguments, Class<?>[] types) {
		if (arguments.length!=types.length) {
			throw new IllegalArgumentException(
				"Argument length doesn't match type length in function "+functionName);
		}
		for (int i=0; i<types.length; i++) {
			if (!types[i].isInstance(arguments[i])) {
				throw new IllegalArgumentException(
					"Invalid type for argument "+i+" in function "+functionName);
			}
		}
	}

	protected void assertArgumentLength(Object[] arguments, int length) {
		if (arguments.length!=length) {
			throw new IllegalArgumentException(
				"Invalid argument length in function "+functionName);
		}
	}

	protected void assertMinimumArgumentLength(Object[] arguments, int length) {
		if (arguments.length<length) {
			throw new IllegalArgumentException(
				"Must have at least "+length+" arguments in function "+functionName);
		}
	}

	protected void assertMaximumArgumentLength(Object[] arguments, int length) {
		if (arguments.length>length) {
			throw new IllegalArgumentException(
				"Must have no more than "+length+" arguments in function "+functionName);
		}
	}

	/**
	 * @return the initialized
	 */
	protected boolean isInitialized() {
		return initialized;
	}

	/**
	 * @return the strictInitialization
	 */
	protected boolean isStrictInitialization() {
		return strictInitialization;
	}

	/**
	 * @param strictInitialization the strictInitialization to set
	 */
	protected void setStrictInitialization(boolean strictInitialization) {
		this.strictInitialization = strictInitialization;
	}

	/**
	 * @return the functionName
	 */
	protected String getFunctionName() {
		return functionName;
	}

	/**
	 * @param functionName the functionName to set
	 */
	protected void setFunctionName(String functionName) {
		assertNotInitialized();
		this.functionName = functionName;
	}

	/**
	 * @return the allowQueryGroup
	 */
	protected boolean isAllowQueryGroup() {
		return allowQueryGroup;
	}

	/**
	 * @param allowQueryGroup the allowQueryGroup to set
	 */
	protected void setAllowQueryGroup(boolean allowQueryGroup) {
		assertNotInitialized();
		this.allowQueryGroup = allowQueryGroup;
	}

	/**
	 * @return the allowQuery
	 */
	protected boolean isAllowQuery() {
		return allowQuery;
	}

	/**
	 * @param allowQuery the allowQuery to set
	 */
	protected void setAllowQuery(boolean allowQuery) {
		assertNotInitialized();
		this.allowQuery = allowQuery;
	}

	/**
	 * @return the exactArgs
	 */
	protected int getExactArgs() {
		return exactArgs;
	}

	/**
	 * @param exactArgs the exactArgs to set
	 */
	protected void setExactArgs(int exactArgs) {
		assertNotInitialized();
		this.exactArgs = exactArgs;
	}

	/**
	 * @return the maxArgs
	 */
	protected int getMaxArgs() {
		return maxArgs;
	}

	/**
	 * @param maxArgs the maxArgs to set
	 */
	protected void setMaxArgs(int maxArgs) {
		assertNotInitialized();
		this.maxArgs = maxArgs;
	}

	/**
	 * @return the minArgs
	 */
	protected int getMinArgs() {
		return minArgs;
	}

	/**
	 * @param minArgs the minArgs to set
	 */
	protected void setMinArgs(int minArgs) {
		assertNotInitialized();
		this.minArgs = minArgs;
	}

	/**
	 * @return the types
	 */
	protected Class<?>[] getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	protected void setTypes(Class<?>... types) {
		assertNotInitialized();
		this.types = types;
	}

}