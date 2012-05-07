package com.googlecode.mjorm.mql;

public class AbstractMqlFunction {

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
