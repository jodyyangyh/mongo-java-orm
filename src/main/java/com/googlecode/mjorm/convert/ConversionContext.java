package com.googlecode.mjorm.convert;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ConversionContext {

	private Collection<TypeConverter<?, ?>> typeConverters
		= new LinkedList<TypeConverter<?, ?>>();
	private Map<String, TypeConverter<?, ?>> typeConverterCache
		= new HashMap<String, TypeConverter<?,?>>();

	/**
	 * Converts the source object from the sourceClass to the targetClass.
	 * @param source the source
	 * @param sourceClass the source class
	 * @param targetClass the target class
	 * @return the converted object
	 * @throws ConversionException
	 */
	public <Target, Source> Target convert(Source source, Class<Source> sourceClass, Class<Target> targetClass)
		throws ConversionException {
		return convert(source, sourceClass, targetClass, new TypeConversionHints());
	}

	/**
	 * Converts the source object from the sourceClass to the targetClass.
	 * @param source the source
	 * @param sourceClass the source class
	 * @param targetClass the target class
	 * @param hints hints
	 * @return the converted object
	 * @throws ConversionException
	 */
	public <Target, Source> Target convert(
		Source source, Class<Source> sourceClass, Class<Target> targetClass, TypeConversionHints hints)
		throws ConversionException {

		// pass nulls through
		if (source==null) {
			return null;
		}

		// find a converter
		TypeConverter<Target, Source> conv = getConverter(sourceClass, targetClass);
		if (conv==null) {
			throw new ConversionException(
				"Unable to convert "+sourceClass.getName()+" to "+targetClass.getName());
		}

		// do the conversion
		return conv.convert(source, hints);
	}

	/**
	 * Returns a TypeConverter capable of translating source to target.
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <Target, Source> TypeConverter<Target, Source> getConverter(Class<Source> sourceClass, Class<Target> targetClass) {
		String cacheKey = sourceClass.getName()+"_"+targetClass.getName();
		if (typeConverterCache.containsKey(cacheKey)) {
			return (TypeConverter<Target, Source>)typeConverterCache.get(cacheKey);
		}
		for (TypeConverter<?, ?> conv : typeConverters) {
			if (conv.canConvert(sourceClass, targetClass)) {
				typeConverterCache.put(cacheKey, conv);
				return (TypeConverter<Target, Source>) conv;
			}
		}
		return null;
	}


	/**
	 * Registers a new {@link TypeConverter}.
	 * @param typeConverter the converter
	 */
	public void registerTypeConverter(TypeConverter<?, ?> typeConverter) {
		typeConverterCache.clear();
		typeConverters.add(typeConverter);
	}

}
