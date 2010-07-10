package com.googlecode.mjorm;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * a {@link TypeConverter} for primitives.
 */
public class PrimitiveTypeConverter
	implements TypeConverter {

	/**
	 * {@inheritDoc}
	 */
	public boolean canConvert(Class<?> clazz) {
		return clazz.isPrimitive()
			|| Boolean.class.isAssignableFrom(clazz)
			|| (Number.class.isAssignableFrom(clazz)
				&& !BigDecimal.class.isAssignableFrom(clazz)
				&& !BigInteger.class.isAssignableFrom(clazz))
			|| String.class.isAssignableFrom(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object convertFromMongo(Object object, Class<?> clazz) {
		if (Byte.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).byteValue();

		} else if (Short.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).shortValue();

		} else if (Integer.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).intValue();

		} else if (Long.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).longValue();

		} else if (Float.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).floatValue();

		} else if (Double.class.isAssignableFrom(clazz)) {
			return Number.class.cast(object).doubleValue();

		} else {
			return object;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object convertToMongo(Object object, Class<?> clazz) {
		return object;
	}

}
