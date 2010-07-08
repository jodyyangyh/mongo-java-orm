package com.googlecode.mjorm;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
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
	 * {@inheritDoc}
	 */
	public long countObjects(String collection, DBObject query) {
		return getCollection(collection).count(query);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T createObject(String collection, T object) {
		DBObject dbObject;
		try {
			dbObject = objectMapper.translateToDBObject(object, object.getClass());
		} catch (Exception e) {
			throw new MappingException(e);
		}
		getCollection(collection).insert(dbObject);
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] createObjects(String collection, T[] objects) {
		DBObject[] dbObjects = new DBObject[objects.length];
		try {
			for (int i=0; i<objects.length; i++) {
				dbObjects[i] = objectMapper.translateToDBObject(
					objects[i], objects[i].getClass());
			}
		} catch (Exception e) {
			throw new MappingException(e);
		}
		getCollection(collection).insert(dbObjects);
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
	public <T> T findObject(String collection, DBObject query, Class<T> clazz) {
		DBObject dbObject = getCollection(collection).findOne(query);
		try {
			return objectMapper.translateFromDBObject(dbObject, clazz);
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
			return objectMapper.translateFromDBObject(dbObject, clazz);
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
			dbObject = objectMapper.translateToDBObject(o, o.getClass());
		} catch (Exception e) {
			throw new MappingException(e);
		}
		 getCollection(collection).update(new BasicDBObject("_id", new ObjectId(id)), dbObject);
	}


}
