package com.googlecode.mjorm.convert.converters;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.logging.Logger;

import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.TypeConversionHints;
import com.googlecode.mjorm.convert.TypeConverter;

public class NumberTypeConverter<Target extends Number>
	implements TypeConverter<Target, Number> {

	private static final Logger LOGGER = Logger.getLogger(NumberTypeConverter.class.getName());

	private Method valueOfMethod;
	private Class<Target> targetClass;

	public NumberTypeConverter(Class<Target> targetClass) {
		this.targetClass = targetClass;
	}

	public Class<?> getSourceClass() {
		return Number.class;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return Number.class.isAssignableFrom(sourceClass)
			&& targetClass.isAssignableFrom(this.targetClass);
	}

	public Target convert(Number source, TypeConversionHints hints)
		throws ConversionException {

		if (targetClass.equals(Byte.class)) {
			return targetClass.cast(Byte.valueOf(source.byteValue()));
			
		} else if (targetClass.equals(Short.class)) {
			return targetClass.cast(Short.valueOf(source.shortValue()));
			
		} else if (targetClass.equals(Integer.class)) {
			return targetClass.cast(Integer.valueOf(source.intValue()));
			
		} else if (targetClass.equals(Long.class)) {
			return targetClass.cast(Long.valueOf(source.longValue()));
			
		} else if (targetClass.equals(Float.class)) {
			return targetClass.cast(Float.valueOf(source.floatValue()));
			
		} else if (targetClass.equals(Double.class)) {
			return targetClass.cast(Double.valueOf(source.doubleValue()));
			
		} else if (targetClass.equals(BigDecimal.class)) {
			return targetClass.cast(BigDecimal.valueOf(source.floatValue()));

		// last ditch effort using reflection
		} else {
			try {
				LOGGER.warning("Using reflected valueOf method for "+targetClass.getName()+", consider creating a TypeConverter for this class");
				if (valueOfMethod==null) {
					valueOfMethod = targetClass.getMethod("valueOf", String.class);
				}
				return targetClass.cast(valueOfMethod.invoke(null, source.toString()));
			} catch(Exception e) {
				LOGGER.severe("Reflecting valueOf method of Number class "+targetClass.getName()+"failed");
				throw new ConversionException(e);
			}
		}
	}

}
