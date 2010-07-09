package com.googlecode.mjorm;

/**
 * Converts types to and from MongoDB.
 */
public interface TypeConverter {

	/**
	 * Converts the given {@link Object} to an
	 * object usable by MongoDB.
	 * @param value the {@link Object} to convert
	 * @param clazz the expected input class
	 * @return the MongoDB object
	 */
	Object convertToMongo(Object value, Class<?> clazz);

	/**
	 * Converts the given {@link Object} from a
	 * MongoDB type.
	 * @param value the MongoDB object
	 * @param clazz the expected output class
	 * @return the {@link Object}
	 */
	Object convertFromMongo(Object value, Class<?> clazz);

	/**
	 * Indicates whether or not the {@link TypeConverter}
	 * can convert the given {@link Class}.
	 * @param clazz the class
	 * @return true if it can, false otherwise
	 */
	boolean canConvert(Class<?> clazz);

}
