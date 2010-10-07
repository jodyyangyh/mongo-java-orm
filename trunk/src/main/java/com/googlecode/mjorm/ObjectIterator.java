package com.googlecode.mjorm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * An ObjectIterator provides mapping features for
 * {@link DBCursors}s.  Basically it returns java
 * objects rather than {@link DBObject}s.
 * @param <E>
 */
public class ObjectIterator<E>
	implements Iterator<E> {

	private DBCursor cursor;
	private final ObjectMapper objectMapper;
	private final Class<E> clazz;
	private DBObject lastSort;
	private String lastHint;

	/**
	 * Creates the {@link ObjectIterator}.
	 * @param cursor the cursor to wrap
	 * @param objectMapper the {@link ObjectMapper} to use
	 * @param clazz the class we're returning
	 */
	public ObjectIterator(
		DBCursor cursor, ObjectMapper objectMapper, Class<E> clazz) {
		this.cursor 		= cursor;
		this.objectMapper	= objectMapper;
		this.clazz			= clazz;
	}

	/**
	 * Reads all of the objects behind this cursor
	 * and returns them in a {@link List}.
	 * @return the {@link List} of objects.
	 */
	public List<E> readAll() {
		List<E> ret = new ArrayList<E>();
		while (hasNext()) {
			ret.add(next());
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return cursor.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public E next() {
		try {
			return objectMapper.mapFromDBObject(cursor.next(), clazz);
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public E curr() {
		try {
			return objectMapper.mapFromDBObject(cursor.curr(), clazz);
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}

	/**
	 * {@see DBCursor#explain()}.
	 * @return DBObject
	 */
	public DBObject explain() {
		return cursor.explain();
	}

	/**
	 * {@see DBCursor#getKeysWanted()}.
	 * @return DBObject
	 */
	public DBObject getKeysWanted() {
		return cursor.getKeysWanted();
	}

	/**
	 * {@see DBCursor#getQuery()}.
	 * @return DBObject
	 */
	public DBObject getQuery() {
		return cursor.getQuery();
	}

	/**
	 * {@see DBCursor#getSizes()}.
	 * @return List<Integer>
	 */
	public List<Integer> getSizes() {
		return cursor.getSizes();
	}

	/**
	 * {@see DBCursor#remove()}.
	 */
	public void remove() {
		cursor.remove();
	}

	/**
	 * {@see DBCursor#addOption(int)}.
	 * @param option option
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> addOption(int option) {
		cursor = cursor.addOption(option);
		return this;
	}

	/**
	 * {@see DBCursor#batchSize(int)}.
	 * @param n n
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> batchSize(int n) {
		cursor = cursor.batchSize(n);
		return this;
	}

	/**
	 * {@see DBCursor#copy()}.
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> copy() {
		cursor = cursor.copy();
		return this;
	}

	/**
	 * {@see DBCursor#hint(DBObject)}.
	 * @param indexKeys indexKeys
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> hint(DBObject indexKeys) {
		lastHint = (indexKeys != null) ? DBCollection.genIndexName(indexKeys) : null;
		cursor = cursor.hint(indexKeys);
		return this;
	}

	/**
	 * {@see DBCursor#hint(String)}.
	 * @param indexName indexName
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> hint(String indexName) {
		lastHint = indexName;
		cursor = cursor.hint(indexName);
		return this;
	}

	/**
	 * {@see DBCursor#limit(int)}.
	 * @param m m
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> limit(int m) {
		cursor = cursor.limit(m);
		return this;
	}

	/**
	 * {@see DBCursor#skip(int)}.
	 * @param n n
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> skip(int n) {
		cursor = cursor.skip(n);
		return this;
	}

	/**
	 * {@see DBCursor#snapshot()}.
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> snapshot() {
		cursor = cursor.snapshot();
		return this;
	}

	/**
	 * {@see DBCursor#sort(DBObject)}.
	 * @param orderBy orderBy
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> sort(DBObject orderBy) {
		lastSort = new BasicDBObject(orderBy.toMap());
		cursor = cursor.sort(orderBy);
		return this;
	}

	/**
	 * {@see DBCursor#count()}.
	 * @return int
	 */
	public int count() {
		return cursor.count();
	}

	/**
	 * {@see DBCursor#length()}.
	 * @return int
	 */
	public int length() {
		return cursor.length();
	}

	/**
	 * {@see DBCursor#numGetMores()}.
	 * @return int
	 */
	public int numGetMores() {
		return cursor.numGetMores();
	}

	/**
	 * {@see DBCursor#numSeen()}.
	 * @return int
	 */
	public int numSeen() {
		return cursor.numSeen();
	}

	/**
	 * @return the last sort parameters applied to the cursor
	 */
	public DBObject getLastSort() {
		return lastSort;
	}

	/**
	 * @return the last hint index name applied to the cursor
	 */
	public String getLastHint() {
		return lastHint;
	}

}
