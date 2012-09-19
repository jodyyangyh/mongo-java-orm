package com.googlecode.mjorm.convert;

public interface TypeConverter<T, S> {

	/**
	 * Returns the from type.
	 * @return the from type
	 */
	Class<?> getSourceClass();

	/**
	 * Returns the to type.
	 * @return the to type
	 */
	Class<?> getTargetClass();

	/**
	 * Indicates whether or not this converter can
	 * convert form {@link sourceClass} to {@link targetClass}.
	 * @param sourceClass the source
	 * @param targetClass the target
	 * @return true if it can convert {@link sourceClass} to {@link targetClass}.
	 */
	boolean canConvert(Class<?> sourceClass, Class<?> targetClass);

	/**
	 * Converts the {@code From} to a {@code To}.
	 * @param source the from object
	 * @param hints hints
	 * @return the to object
	 */
	T convert(S source, TypeConversionHints hints);

}
