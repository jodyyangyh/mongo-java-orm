package com.googlecode.mjorm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
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

	private static final int DEFAULT_BATCH_SIZE = 10;

	private DB db;
	private ObjectMapper objectMapper;
	private Integer batchSize = DEFAULT_BATCH_SIZE;

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
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
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
	public void deleteObjects(String collection, String[] ids) {
		ObjectId[] objIds = new ObjectId[ids.length];
		for (int i=0; i<objIds.length; i++) {
			objIds[i] = new ObjectId(ids[i]);
		}
		getCollection(collection).remove(
			new BasicDBObject("_id", new BasicDBObject("$in", objIds)));
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
		DBCursor cursor = getCollection(collection).find(query, null, 0, batchSize);
		return new ObjectIterator<T>(cursor, objectMapper, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findObjects(
		String collection, DBObject query,
		int startIndex, int numObjects,
		Class<T> clazz) {
		DBCursor cursor = getCollection(collection)
			.find(query, null, startIndex, batchSize)
			.limit(numObjects);
		return new ObjectIterator<T>(cursor, objectMapper, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> ObjectIterator<T> findObjects(
		String collection, DBObject query,
		int startIndex, int numObjects, int batchSize,
		Class<T> clazz) {
		DBCursor cursor = getCollection(collection)
			.find(query, null, startIndex, batchSize)
			.limit(numObjects);
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
		DBObject dbObject = getCollection(collection)
			.findOne(new BasicDBObject("_id", new ObjectId(id)));
		try {
			return objectMapper.mapFromDBObject(dbObject, clazz);
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] readObjects(String collection, String[] ids, Class<T> clazz) {
		ObjectId[] objIds = new ObjectId[ids.length];
		for (int i=0; i<objIds.length; i++) {
			objIds[i] = new ObjectId(ids[i]);
		}
		DBCursor cursor = getCollection(collection).find(
			new BasicDBObject("_id", new BasicDBObject("$in", objIds)));
		try {
			List<T> ret = new ArrayList<T>();
			while (cursor.hasNext()) {
				ret.add((T)objectMapper.mapFromDBObject(cursor.next(), clazz));
			}
			return ret.toArray((T[])Array.newInstance(clazz, 0));
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
	 * {@inheritDoc}
	 */
	public void ensureIndex(
		String collection, String key,
		boolean background, boolean unique, boolean dropDupes) {
		ensureIndex(collection, new BasicDBObject(key, 1), background, unique, dropDupes);
	}

	/**
	 * {@inheritDoc}
	 */
	public void ensureIndex(
		String collection, DBObject keys,
		boolean background, boolean unique, boolean dropDupes) {
		getCollection(collection).ensureIndex(keys,
			BasicDBObjectBuilder.start()
				.add("unique", unique)
				.add("dropDups", dropDupes)
				.add("background", background)
				.get());
	}

	/**
	 * {@inheritDoc}
	 */
	public MapReduceResult mapReduce(String collection, MapReduce mapReduce) {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
			.add("mapreduce", collection)
			.add("map", mapReduce.getMapFunction())
			.add("reduce", mapReduce.getReduceFunction());
		if (mapReduce.getQuery()!=null) {
			builder.add("query", mapReduce.getQuery());
		}
		if (mapReduce.getSort()!=null) {
			builder.add("sort", mapReduce.getSort());
		}
		if (mapReduce.getLimit()!=null) {
			builder.add("limit", mapReduce.getLimit());
		}
		if (mapReduce.getOutputCollectionName()!=null) {
			builder.add("out", mapReduce.getOutputCollectionName());
		}
		if (mapReduce.getKeepTemp()!=null) {
			builder.add("keeptemp", mapReduce.getKeepTemp());
		}
		if (mapReduce.getFinalizeFunction()!=null) {
			builder.add("finalize", mapReduce.getFinalizeFunction());
		}
		if (mapReduce.getScope()!=null) {
			builder.add("scope", mapReduce.getScope());
		}
		if (mapReduce.getVerbose()!=null) {
			builder.add("verbose", mapReduce.getVerbose());
		}

		// execute the command
		CommandResult result = getDB().command(builder.get());
		result.throwOnError();

		// return output
		return new MapReduceResult(getDB(), result);
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
