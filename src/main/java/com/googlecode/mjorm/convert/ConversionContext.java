package com.googlecode.mjorm.convert;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class ConversionContext {

	private Collection<TypeConverter<?, ?>> typeConverters
		= new LinkedList<TypeConverter<?, ?>>();
	private Map<String, TypeConverter<?, ?>> typeConverterCache
		= new HashMap<String, TypeConverter<?, ?>>();
	private Map<Class<?>, Class<?>> defaultStorageClasses
		= new HashMap<Class<?>, Class<?>>();

	/**
	 * Returns the default storage JavaType for the
	 * given class.
	 * @param clazz
	 * @return
	 * @throws ConversionException
	 */
	public JavaType getStorageType(Class<?> clazz)
		throws ConversionException {
		return JavaType.fromType(getStorageClass(clazz));
	}

	/**
	 * Returns the default storage class for the
	 * given class.
	 * @param clazz the class
	 * @return the default storage class
	 * @throws ConversionException on error
	 */
	public Class<?> getStorageClass(Class<?> clazz)
		throws ConversionException {
		Class<?> ret = defaultStorageClasses.get(clazz);
		if (ret!=null) {
			return ret;

		} else if (clazz.isPrimitive()) {
			return clazz;

		} else if (clazz.isArray()) {
			return BasicDBList.class;

		} else if (String.class.isAssignableFrom(clazz)) {
			return clazz;
			
		} else if (Number.class.isAssignableFrom(clazz)) {
			return clazz;

		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return clazz;

		} else if (Character.class.isAssignableFrom(clazz)) {
			return clazz;

		} else if (Collection.class.isAssignableFrom(clazz)) {
			return BasicDBList.class;

		} else if (Map.class.isAssignableFrom(clazz)) {
			return BasicDBObject.class;

		} else {
			return BasicDBObject.class;
		}
	}

	@SuppressWarnings("unchecked")
	public <S, T> T convert(S source, JavaType targetType)
		throws ConversionException {

		// pass nulls through
		if (source==null) { return null; }

		// bail on null target
		if (targetType==null) {
			throw new IllegalArgumentException(
				"Must have a targetType and it must be instantiable");
		}

		// get source class
		Class<?> sourceClass = source.getClass();

		// no conversion needed
		if (sourceClass.equals(targetType.getTypeClass())) {
			return (T)source;
		}

		// find a converter
		TypeConverter<S, T> conv = getConverter(sourceClass, targetType.getTypeClass());
		if (conv==null) {
			throw new ConversionException(
				"Unable to map "+sourceClass+" to "+targetType);
		}

		// do the conversion
		return (T)conv.convert(source, targetType, this);
	}

	/**
	 * Returns a TypeConverter capable of translating source to target.
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <S, T> TypeConverter<S, T> getConverter(Class<?> sourceClass, Class<?> targetClass) {
		String cacheKey = sourceClass.getName()+"_"+targetClass.getName();
		if (typeConverterCache.containsKey(cacheKey)) {
			return (TypeConverter<S, T>)typeConverterCache.get(cacheKey);
		}
		for (TypeConverter<?, ?> conv : typeConverters) {
			if (conv.canConvert(sourceClass, targetClass)) {
				typeConverterCache.put(cacheKey, conv);
				return (TypeConverter<S, T>) conv;
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

	/**
	 * Registers a default storage class.
	 * @param source the soruce
	 * @param target the target
	 */
	public void registerDefaultStorageClass(Class<?> source, Class<?> target) {
		defaultStorageClasses.put(source, target);
	}

}
