package com.googlecode.mjorm.convert.converters;

import java.math.BigDecimal;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConverter;

public class NumberTypeConverter
	implements TypeConverter<Number, Number> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return Number.class.isAssignableFrom(sourceClass)
			&& Number.class.isAssignableFrom(targetClass);
	}

	public Number convert(Number source, JavaType targetType, ConversionContext context)
		throws ConversionException {

		if (targetType.is(Byte.class)) {
			return Byte.valueOf(source.byteValue());
			
		} else if (targetType.is(Short.class)) {
			return Short.valueOf(source.shortValue());
			
		} else if (targetType.is(Integer.class)) {
			return Integer.valueOf(source.intValue());
			
		} else if (targetType.is(Long.class)) {
			return Long.valueOf(source.longValue());
			
		} else if (targetType.is(Float.class)) {
			return Float.valueOf(source.floatValue());
			
		} else if (targetType.is(Double.class)) {
			return Double.valueOf(source.doubleValue());
			
		} else if (targetType.is(BigDecimal.class)) {
			return BigDecimal.valueOf(source.floatValue());
			
		}

		throw new ConversionException(
			"Unable to convert source Number to "+targetType);
	}

}
