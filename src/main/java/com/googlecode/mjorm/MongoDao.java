package com.googlecode.mjorm;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * An interface for working with mapped objects in mongo.
 */
public interface MongoDao {

	/**
	 * Creates the object in the given collection.
	 * @param <T> the type
	 * @param collection the collection
	 * @param object the object to create
	 * @return the created object
	 */
	<T> T createObject(String collection, T object);

	/**
	 * Creates the given objects in the given collection.
	 * @param <T> the type
	 * @param collection the collection
	 * @param objects the objects to create
	 * @return the created objects
	 */
	<T> T[] createObjects(String collection, T[] objects);

	/**
	 * Maps and returns an object from the given collection.
	 * @param <T> the type
	 * @param collection the collection
	 * @param id the object's id
	 * @param clazz the object's class
	 * @return the object read
	 */
	<T> T readObject(String collection, String id, Class<T> clazz);

	/**
	 * Updates the object with the given id in the given
	 * collection.
	 * @param collection the collection
	 * @param id the id
	 * @param o the object
	 */
	void updateObject(String collection, String id, Object o);

	/**
	 * Deletes the object with the given id from the
	 * given collection.
	 * @param collection the collection
	 * @param id the id
	 */
	void deleteObject(String collection, String id);

	/**
	 * Deletes the objects matching the given query from
	 * the given collection.
	 * @param collection the collection
	 * @param query the query
	 */
	void deleteObjects(String collection, DBObject query);

	/**
	 * Finds objects by the given example in the given
	 * collection and returns an {@link ObjectIterator}
	 * for them.
	 * @param <T> the object type
	 * @param collection the collection
	 * @param example the example
	 * @param clazz the class
	 * @return the {@link ObjectIterator}
	 */
	<T> ObjectIterator<T> findByExample(String collection, T example, Class<T> clazz);

	/**
	 * Maps and returns an {@link ObjectIterator} for objects
	 * matching the given query in the given collection.
	 * @param <T> the type
	 * @param collection the collection
	 * @param query the query
	 * @param clazz the java type to map the objects to
	 * @return the {@link ObjectIterator}
	 */
	<T> ObjectIterator<T> findObjects(String collection, DBObject query, Class<T> clazz);

	/**
	 * Maps and returns an {@link ObjectIterator} for objects
	 * matching the given query in the given collection.
	 * @param <T> the type
	 * @param collection the collection
	 * @param query the query
	 * @param startIndex the first object to return
	 * @param numObjects the number of objects to return
	 * @param clazz the java type to map the objects to
	 * @return the {@link ObjectIterator}
	 */
	<T> ObjectIterator<T> findObjects(String collection, DBObject query,
		int startIndex, int numObjects, Class<T> clazz);

	/**
	 * Maps and returns an {@link ObjectIterator} for objects
	 * matching the given query in the given collection.
	 * @param <T> the type
	 * @param collection the collection
	 * @param query the query
	 * @param startIndex the first object to return
	 * @param numObjects the number of objects to return
	 * @param batchSize the batchSize
	 * @param clazz the java type to map the objects to
	 * @return the {@link ObjectIterator}
	 */
	<T> ObjectIterator<T> findObjects(String collection, DBObject query,
		int startIndex, int numObjects, int batchSize, Class<T> clazz);

	/**
	 * Returns the count of objects matching the given query
	 * in the given collection.
	 * @param collection the collection
	 * @param query the query
	 * @return the count
	 */
	long countObjects(String collection, DBObject query);

	/**
	 * Maps and returns a single object matching the given query
	 * from the given collection.
	 * @param <T> the type
	 * @param collection the collection
	 * @param query the query
	 * @param clazz the java type to map the objects to
	 * @return the object, or null if not found
	 */
	<T> T findObject(String collection, DBObject query, Class<T> clazz);

	/**
	 * Returns the underlying {@link DB}.
	 * @return the {@link DB}
	 */
	DB getDB();

	/**
	 * Returns a {@link DBCollection} by it's name.
	 * @param name the name
	 * @return the {@link DBCollection}
	 */
	DBCollection getCollection(String name);
}
