package com.googlecode.mjorm.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.ObjectIterator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class DaoQuery
	extends AbstractQueryCriterion<DaoQuery> {
	
	private MongoDao mongoDao;
	private Map<String, Integer> sort;
	private Map<String, Object> specials;
	private Integer firstDocument;
	private Integer maxDocuments;
	private Integer batchSize;
	private String hint;
	private Boolean snapShot;
	private String comment;
	private String collection;
	private CursorVisitor cursorVisitor;

	/**
	 * Allows for the visiting of the {@link DBCursor}
	 * before it is returned as an {@link ObjectIterator}
	 * form the various query methods of this class.
	 */
	public static interface CursorVisitor {
		void visit(DBCursor cursor);
	}

	/**
	 * Creates the {@link MongoDao}.
	 * @param mongoDao the {@link MongoDao}
	 */
	public DaoQuery(MongoDao mongoDao) {
		this.mongoDao = mongoDao;
		this.clear();
	}

	/**
	 * Clears the query.
	 */
	public void clear() {
		super.clear();
		sort 			= new HashMap<String, Integer>();
		specials 		= new HashMap<String, Object>();
		firstDocument	= null;
		maxDocuments	= null;
		batchSize		= null;
		hint			= null;
		snapShot		= null;
		comment			= null;
		collection		= null;
		cursorVisitor	= null;
	}

	/**
	 * Executes the query and returns objects of the given type.
	 * @param clazz the type of objects to return
	 * @return the iterator.
	 */
	public <T> ObjectIterator<T> findObjects(Class<T> clazz) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		ObjectIterator<T> itr = mongoDao.findObjects(collection, toQueryObject(), clazz);
		setupCursor(itr.getDBCursor());
		return itr;
	}

	/**
	 * Executes the query and returns an object of the given type.
	 * @param clazz the type of object to return
	 * @return the object.
	 */
	public <T> T findObject(Class<T> clazz) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findObject(collection, toQueryObject(), clazz);
	}

	/**
	 * Executes the query and returns the number of objects
	 * that it would return.
	 * @return the count
	 */
	public long countObjects() {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.countObjects(collection, toQueryObject());
	}

	/**
	 * Removes the objects matched by this query.
	 * @return the {@link WriteResult}
	 */
	public WriteResult removeObjects() {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.getCollection(collection)
			.remove(toQueryObject());
	}

	/**
	 * Performs a findAndRemove for the current query.
	 * @return the object found and modified
	 */
	public <T> T findAndRemove(Class<T> clazz) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndRemove(collection, toQueryObject(), clazz);
	}

	/**
	 * Performs a findAndModify for the current query.
	 * @param update the update object
	 * @param returnNew whether or not to return the new or old object
	 * @param upsert create new if it doesn't exist
	 * @param clazz the type of object
	 * @return the object
	 */
	public <T> T findAndModify(
		DBObject update, boolean returnNew, boolean upsert, Class<T> clazz) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndModify(
			collection, toQueryObject(), getSortDBObject(), update, returnNew, upsert, clazz);
	}

	/**
	 * Performs a findAndModify for the current query.
	 * @param update the update object
	 * @param returnNew whether or not to return the new or old object
	 * @param clazz the type of object
	 * @return the object
	 */
	public <T> T findAndModify(
		DBObject update, boolean returnNew, Class<T> clazz) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndModify(
			collection, toQueryObject(), getSortDBObject(), update, returnNew, clazz);
	}

	/**
	 * Performs a findAndModify for the current query.
	 * @param update the update object
	 * @param clazz the type of object
	 * @return the object
	 */
	public <T> T findAndModify(DBObject update, Class<T> clazz) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndModify(
			collection, toQueryObject(), getSortDBObject(), update, clazz);
	}

	/**
	 * Returns distinct values for the given field.  This field
	 * passed must be the name of a field on a MongoDB document.
	 * @param field the field
	 * @return the distinct objects
	 */
	@SuppressWarnings("unchecked")
	public List<Object> distinct(String field) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao
			.getCollection(collection)
			.distinct(field, toQueryObject());
	}

	/**
	 * Returns distinct values for the given field.  This field
	 * passed must be the name of a field on a MongoDB document.
	 * @param field the field
	 * @param expected the expected type
	 * @return the distinct objects
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> distinct(String field, Class<T> expected) {
		if (collection==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao
			.getCollection(collection)
			.distinct(field, toQueryObject());
	}

	/**
	 * Sets up a {@link DBCursor} for this query.
	 * @param cursor the curor.
	 */
	private void setupCursor(DBCursor cursor) {
		if (firstDocument!=null) {
			cursor.skip(firstDocument);
		}
		if (maxDocuments!=null) {
			cursor.limit(maxDocuments);
		}
		if (batchSize!=null) {
			cursor.batchSize(batchSize);
		}
		if (hint!=null) {
			cursor.hint(hint);
		}
		if (snapShot!=null && snapShot==true) {
			cursor.snapshot();
		}
		if (comment!=null) {
			cursor.addSpecial("$comment", comment);
		}
		if (!specials.isEmpty()) {
			for (Entry<String, Object> special : specials.entrySet()) {
				cursor.addSpecial(special.getKey(), special.getValue());
			}
		}
		if (!sort.isEmpty()) {
			cursor.sort(getSortDBObject());
		}
		if (cursorVisitor!=null) {
			cursorVisitor.visit(cursor);
		}
	}

	/**
	 * Creats and returns the DBObject representing
	 * the sort for this query.
	 * @return the sort
	 */
	public DBObject getSortDBObject() {
		DBObject sortObj = new BasicDBObject();
		if (!sort.isEmpty()) {
			for (Entry<String, Integer> entry : sort.entrySet()) {
				sortObj.put(entry.getKey(), entry.getValue());
			}
		}
		return sortObj;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DaoQuery self() {
		return this;
	}

	/**
	 * Adds a sort.
	 * @param name the field
	 * @param direction the direction
	 * @return self
	 */
	public DaoQuery addSort(String name, Integer direction) {
		this.sort.put(name, direction);
		return self();
	}

	/**
	 * Adds a special.
	 * @param name the name
	 * @param special the special
	 * @return self
	 */
	public DaoQuery addSpecial(String name, Object special) {
		this.specials.put(name, special);
		return self();
	}

	/**
	 * @param sort the sort to set
	 */
	public DaoQuery setSort(Map<String, Integer> sort) {
		this.sort = sort;
		return self();
	}

	/**
	 * @param sort the sort to set
	 */
	@SuppressWarnings("unchecked")
	public DaoQuery setSort(DBObject sort) {
		this.sort.clear();
		this.sort.putAll(sort.toMap());
		return self();
	}

	/**
	 * @param specials the specials to set
	 */
	public void setSpecials(Map<String, Object> specials) {
		this.specials = specials;
	}

	/**
	 * @param firstDocument the firstDocument to set
	 */
	public DaoQuery setFirstDocument(Integer firstDocument) {
		this.firstDocument = firstDocument;
		return self();
	}

	/**
	 * @param maxDocuments the maxDocuments to set
	 */
	public DaoQuery setMaxDocuments(Integer maxDocuments) {
		this.maxDocuments = maxDocuments;
		return self();
	}

	/**
	 * @param batchSize the batchSize to set
	 */
	public DaoQuery setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return self();
	}

	/**
	 * @param hint the hint to set
	 */
	public DaoQuery setHint(String hint) {
		this.hint = hint;
		return self();
	}

	/**
	 * @param hint the hint to set
	 */
	public DaoQuery setHint(DBObject hint) {
		setHint(DBCollection.genIndexName(hint));
		return self();
	}

	/**
	 * @param snapShot the snapShot to set
	 */
	public DaoQuery setSnapShot(Boolean snapShot) {
		this.snapShot = snapShot;
		return self();
	}

	/**
	 * @param snapShot the snapShot to set
	 */
	public DaoQuery setSnapShot() {
		setSnapShot(true);
		return self();
	}

	/**
	 * @param comment the comment to set
	 */
	public DaoQuery setComment(String comment) {
		this.comment = comment;
		return self();
	}

	/**
	 * @param collection the collection to set
	 */
	public DaoQuery setCollection(String collection) {
		this.collection = collection;
		return self();
	}

	/**
	 * @param cursorVisitor the cursorVisitor to set
	 */
	public DaoQuery setCursorVisitor(CursorVisitor cursorVisitor) {
		this.cursorVisitor = cursorVisitor;
		return self();
	}

}
