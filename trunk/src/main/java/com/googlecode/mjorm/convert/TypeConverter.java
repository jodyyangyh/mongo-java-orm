package com.googlecode.mjorm.convert;

public interface TypeConverter<Target, Source> {

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
	 * @param context the {@link ConversionContext}
	 * @param hints hints
	 * @return the to object
	 * @throws ConversionException
	 */
	Target convert(Source source, ConversionContext context, TypeConversionHints hints)
		throws ConversionException;

}
