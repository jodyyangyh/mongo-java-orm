package com.googlecode.mjorm.convert;

import java.util.Collection;
import java.util.LinkedList;

public class ConverterContext {

	private boolean fastNullConversions = true;
	private Collection<TypeConverter<?, ?>> typeConverters
		= new LinkedList<TypeConverter<?, ?>>();

	public <T> convert(Object from, Class<T> toClass) {

		// do nulls fast
		if (from==null && fastNullConversions) {
			return null;
		}

		// get a type converter
		TypeConverter<T, ?> converter = getTypeConverter(toClass, from.getClass());
	}

	@SuppressWarnings("unchecked")
	public <To, From> TypeConverter<To, From> getTypeConverter(Class<To> toClass, Class<From> fromClass) {

		// find exact match
		for (TypeConverter<?, ?> conv : typeConverters) {
			if (conv.getFromType().equals(fromClass)
				&& conv.getToType().equals(toClass)) {
				return (TypeConverter<To, From>) conv;
				// TODO: potential optimization, cache in
				// a map or something similar
			}
		}

		// find subclass
		for (TypeConverter<?, ?> conv : typeConverters) {
			if (conv.getFromType().equals(fromClass)
				&& conv.getToType().equals(toClass)) {
				return (TypeConverter<To, From>) conv;
				// TODO: potential optimization, cache in
				// a map or something similar
			}
		}
		return null;
	}

	public void registerTypeConverter(TypeConverter<?, ?> typeConverter) {
		typeConverters.add(typeConverter);
	}

}
