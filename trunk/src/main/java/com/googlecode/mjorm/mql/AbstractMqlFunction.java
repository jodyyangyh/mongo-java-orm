package com.googlecode.mjorm.mql;

public class AbstractMqlFunction {

	protected boolean allowQuery	= false;
	protected int exactArgs 		= -1;
	protected int maxArgs 			= -1;
	protected int minArgs			= -1;
	protected Class<?> types[]		= new Class<?>[0];

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