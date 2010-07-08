package com.googlecode.mjorm;

import com.mongodb.DBObject;

/**
 * The {@code ObjectMapper} is responsible for translating
 * objects to and from mongo's {@link DBObject}s.
 */
public interface ObjectMapper {

	/**
	 * Translates the given {@link DBObject} into
	 * a java object.
	 * @param <T> the type
	 * @param dbObject the {@link DBObject}
	 * @param objectClass the {@link Class} of the object to translate to
	 * @throws Exception on error
	 * @return the java object
	 */
	<T> T translateFromDBObject(DBObject dbObject, Class<T> objectClass)
		throws Exception;

	/**
	 * Translates the given java object into
	 * a {@link DBObject}.
	 * @param <T> the type
	 * @param object the java object
	 * @param objectClass the {@link Class} of the object to translate from
	 * @throws Exception on error
	 * @return the {@link DBObject}
	 */
	<T> DBObject translateToDBObject(T object, Class<T> objectClass)
		throws Exception;
}
