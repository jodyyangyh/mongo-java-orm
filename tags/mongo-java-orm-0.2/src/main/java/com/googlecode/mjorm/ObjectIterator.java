package com.googlecode.mjorm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private ObjectMapper objectMapper;
	private Class<E> clazz;

	/**
	 * Creates the {@link ObjectIterator}.
	 * @param cursor the curser to wrap
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
		return new ObjectIterator<E>(
			cursor.addOption(option), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#batchSize(int)}.
	 * @param n n
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> batchSize(int n) {
		return new ObjectIterator<E>(
			cursor.batchSize(n), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#copy()}.
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> copy() {
		return new ObjectIterator<E>(
			cursor.copy(), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#hint(DBObject)}.
	 * @param indexKeys indexKeys
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> hint(DBObject indexKeys) {
		return new ObjectIterator<E>(
			cursor.hint(indexKeys), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#hint(String)}.
	 * @param indexName indexName
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> hint(String indexName) {
		return new ObjectIterator<E>(
			cursor.hint(indexName), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#limit(int)}.
	 * @param m m
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> limit(int m) {
		return new ObjectIterator<E>(
			cursor.limit(m), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#skip(int)}.
	 * @param n n
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> skip(int n) {
		return new ObjectIterator<E>(
			cursor.skip(n), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#snapshot()}.
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> snapshot() {
		return new ObjectIterator<E>(
			cursor.snapshot(), objectMapper, clazz);
	}

	/**
	 * {@see DBCursor#sort(DBObject)}.
	 * @param orderBy orderBy
	 * @return ObjectIterator<E>
	 */
	public ObjectIterator<E> sort(DBObject orderBy) {
		return new ObjectIterator<E>(
			cursor.sort(orderBy), objectMapper, clazz);
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

}
