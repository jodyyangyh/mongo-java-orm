package com.googlecode.mjorm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.googlecode.mjorm.query.DaoQuery;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;

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
	public DaoQuery createQuery() {
		return new DaoQuery(this);
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
	public <T> T getPartialObject(String collection, String id, String name, Class<T> clazz) {
		return getPartialObject(collection, new BasicDBObject("_id", new ObjectId(id)), name, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPartialObject(String collection, DBObject query, String name, Class<T> clazz) {

		// query for the object
		DBObject dbObject = getCollection(collection).findOne(
			query, new BasicDBObject(name, 1));
		if (dbObject==null) {
			return null;
		}

		// now recurse down the object
		Object value = null;
		for (String part : name.split("\\.")) {
			if (!dbObject.containsField(part)) {
				return null;
			}
			value = dbObject.get(part);
			if (DBObject.class.isInstance(value)) {
				dbObject = DBObject.class.cast(value);
			} else {
				break;
			}
		}

		// now convert
		return !isPrimitive(clazz)
			? objectMapper.mapFromDBObject(dbObject, clazz)
			: (T)value;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void savePartialObject(
		String collection, String id, String name, T data, boolean upsert) {
		savePartialObject(collection, new BasicDBObject("_id", new ObjectId(id)), name, data, upsert);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void savePartialObject(
		String collection, DBObject query, String name, T data, boolean upsert) {

		// the value we're storing
		Object value = null;

		// if it's not null, determine the
		// type and store accordingly
		if (data!=null) {
			Class<?> clazz = data.getClass();
			value = !isPrimitive(clazz)
				? objectMapper.mapToDBObject(data)
				: data;
		}

		// save it
		getCollection(collection).update(
			query, new BasicDBObject("$set", new BasicDBObject(name, value)),
			upsert, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deletePartialObject(String collection, String id, String name) {
		deletePartialObject(collection, new BasicDBObject("_id", new ObjectId(id)), name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deletePartialObject(String collection, DBObject query, String name) {
		getCollection(collection).update(
			query, new BasicDBObject("$unset", new BasicDBObject(name, 1)),
			false, false);
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

		if (mapReduce.getOutputCollectionName()==null
			|| mapReduce.getOutputCollectionName().trim().length()==0) {
			throw new IllegalArgumentException("Invalid output collection name");
		}

		// create command
		MapReduceCommand cmd = new MapReduceCommand(
			getCollection(collection),
			mapReduce.getMapFunction(),
			mapReduce.getReduceFunction(),
			mapReduce.getOutputCollectionName(),
			mapReduce.getOutputType(),
			mapReduce.getQuery());
		
		if (mapReduce.getSort()!=null) {
			cmd.setSort(mapReduce.getSort());
		}
		if (mapReduce.getLimit()!=null) {
			cmd.setLimit(mapReduce.getLimit().intValue());
		}
		if (mapReduce.getFinalizeFunction()!=null) {
			cmd.setFinalize(mapReduce.getFinalizeFunction());
		}
		if (mapReduce.getScope()!=null) {
			cmd.setScope(mapReduce.getScope());
		}
		if (mapReduce.getVerbose()!=null) {
			cmd.setVerbose(mapReduce.getVerbose());
		}
		if (mapReduce.getOutputDBName()!=null) {
			cmd.setOutputDB(mapReduce.getOutputDBName());
		}

		// execute and return
		return new MapReduceResult(
			getCollection(collection).mapReduce(cmd));
	}

	/**
	 * Quick and easy check for primitives.
	 * @param clazz the class
	 * @return true if primitive
	 */
	private boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive()
			|| clazz.equals(Byte.class)
			|| clazz.equals(Short.class)
			|| clazz.equals(Integer.class)
			|| clazz.equals(Long.class)
			|| clazz.equals(Float.class)
			|| clazz.equals(Double.class)
			|| clazz.equals(Boolean.class)
			|| clazz.equals(Character.class)
			|| clazz.equals(String.class)
			|| clazz.equals(Byte.class);
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
