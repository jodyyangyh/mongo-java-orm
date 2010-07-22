package com.googlecode.mjorm;

import java.lang.reflect.Array;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Basic implementation of the {@link MongoDao} interface.
 */
public class MongoDaoImpl
	implements MongoDao {

	private DB db;
	private ObjectMapper objectMapper;

	/**
	 * Creates the {@link MongoDaoImpl}.
	 * @param db the {@link DB}
	 * @param objectMapper the {@link ObjectMapper}
	 */
	public MongoDaoImpl(DB db, ObjectMapper objectMapper) {
		this.db 			= db;
		this.objectMapper	= objectMapper;
	}

	/**
	 * Creates the {@link MongoDaoImpl}.
	 */
	public MongoDaoImpl() {
		this(null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public long countObjects(String collection, DBObject query) {
		return getCollection(collection).count(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T createObject(String collection, T object) {
		DBObject dbObject;
		try {
			dbObject = objectMapper.mapToDBObject(object);
			getCollection(collection).insert(dbObject);
			return (T)objectMapper.mapFromDBObject(dbObject, object.getClass());
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] createObjects(String collection, T[] objects) {
		DBObject[] dbObjects = new DBObject[objects.length];
		try {
			for (int i=0; i<objects.length; i++) {
				dbObjects[i] = objectMapper.mapToDBObject(objects[i]);
			}
			getCollection(collection).insert(dbObjects);
			T[] ret = (T[])Array.newInstance(objects[0].getClass(), objects.length);
			for (int i=0; i<objects.length; i++) {
				ret[i] = (T)objectMapper.mapFromDBObject(dbObjects[i], objects[i].getClass());
			}
		} catch (Exception e) {
			throw new MappingException(e);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObject(String collection, String id) {
		deleteObjects(collection, new BasicDBObject("_id", new ObjectId(id)));
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteObjects(String collection, DBObject query) {
		getCollection(collection).remove(query);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findByExample(String collection, T example, Class<T> clazz) {
		DBObject query;
		try {
			query = objectMapper.mapToDBObject(example);
		} catch (Exception e) {
			throw new MappingException(e);
		}
		return findObjects(collection, query, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T findObject(String collection, DBObject query, Class<T> clazz) {
		DBObject dbObject = getCollection(collection).findOne(query);
		try {
			return objectMapper.mapFromDBObject(dbObject, clazz);
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findObjects(
		String collection, DBObject query, Class<T> clazz) {
		DBCursor cursor = getCollection(collection).find(query);
		return new ObjectIterator<T>(cursor, objectMapper, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findObjects(
		String collection, DBObject query,
		int startIndex, int numObjects,
		Class<T> clazz) {
		DBCursor cursor = getCollection(collection).find(
			query, null, startIndex, numObjects);
		return new ObjectIterator<T>(cursor, objectMapper, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findObjects(
		String collection, DBObject query,
		int startIndex, int numObjects, int batchSize,
		Class<T> clazz) {
		DBCursor cursor = getCollection(collection).find(
			query, null, startIndex, numObjects, batchSize);
		return new ObjectIterator<T>(cursor, objectMapper, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public DBCollection getCollection(String name) {
		return db.getCollection(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public DB getDB() {
		return db;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T readObject(String collection, String id, Class<T> clazz) {
		DBObject dbObject = getCollection(collection).findOne(new BasicDBObject("_id", new ObjectId(id)));
		try {
			return objectMapper.mapFromDBObject(dbObject, clazz);
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateObject(String collection, String id, Object o) {
		DBObject dbObject;
		try {
			dbObject = objectMapper.mapToDBObject(o);
		} catch (Exception e) {
			throw new MappingException(e);
		}
		 getCollection(collection).update(new BasicDBObject("_id", new ObjectId(id)), dbObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandResult executeCommand(DBObject cmd) {
		CommandResult result = getDB().command(cmd);
		result.throwOnError();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandResult executeCommand(String cmd) {
		CommandResult result = getDB().command(cmd);
		result.throwOnError();
		return result;
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(DB db) {
		this.db = db;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
