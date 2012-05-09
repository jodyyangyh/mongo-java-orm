package com.googlecode.mjorm.query;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.query.modifiers.AbstractQueryModifiers;
import com.mongodb.DBEncoder;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class DaoModifier
	extends AbstractQueryModifiers<DaoModifier> {

	private DaoQuery query;
	private boolean atomic = false;

	/**
	 * Creates the {@link DaoModifier}.
	 * @param mongoDao the {@link MongoDao}
	 */
	public DaoModifier() {
		this.clear();
	}

	/**
	 * Creates the {@link DaoModifier}.
	 * @param mongoDao the {@link MongoDao}
	 */
	public DaoModifier(DaoQuery query) {
		this.query = query;
		this.clear();
	}

	/**
	 * Asserts that the {@link DaoModifier} is valid.
	 * Throws an exception if not.
	 */
	public void assertValid() {
		if (query==null) {
			throw new IllegalStateException("query must be specified");
		}
		query.assertValid();
	}

	/**
	 * Clears this modifier query.
	 */
	public void clear() {
		super.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DaoModifier self() {
		return this;
	}

	/**
	 * Returns the {@link DaoQuery} tha this modifier will use.
	 * @return
	 */
	public DaoQuery getQuery() {
		return query;
	}

	/**
	 * Sets the {@link DaoQuery} that this modifier will use.
	 * @param query
	 * @return
	 */
	public DaoModifier setQuery(DaoQuery query) {
		this.query = query;
		return self();
	}

	/**
	 * @param atomic the atomic to set
	 */
	public DaoModifier setAtomic(boolean atomic) {
		this.atomic = atomic;
		return self();
	}

	/**
	 * Removes the objects matched by this query.
	 * @return the {@link WriteResult}
	 */
	public WriteResult deleteObjects() {
		assertValid();
		return query.getDB().getCollection(query.getCollection())
			.remove(query.toQueryObject());
	}

	/**
	 * Performs a findAndDelete for the current query.
	 * @return the object found and modified
	 */
	public <T> T findAndDelete(Class<T> clazz) {
		assertValid();
		DBObject object = query.getDB().getCollection(query.getCollection())
			.findAndModify(query.toQueryObject(), null, query.getSortDBObject(),
				true, null, false, false);
		return query.getObjectMapper().mapFromDBObject(object, clazz);
	}

	/**
	 * Performs a findAndDelete for the current query.
	 * @return the object found and modified
	 * @param fields the fields to populate on the return object
	 */
	public DBObject findAndDelete(DBObject fields) {
		assertValid();
		return query.getDB().getCollection(query.getCollection())
			.findAndModify(query.toQueryObject(), fields, query.getSortDBObject(),
				true, null, false, false);
	}

	/**
	 * Performs a findAndDelete for the current query.
	 * @return the object found and modified
	 * @param fields the fields to populate on the return object
	 */
	public DBObject findAndDelete() {
		assertValid();
		return findAndDelete((DBObject)null);
	}

	/**
	 * Performs a findAndModify for the current query.
	 * @param returnNew whether or not to return the new or old object
	 * @param upsert create new if it doesn't exist
	 * @param clazz the type of object
	 * @return the object
	 */
	public <T> T findAndModify(boolean returnNew, boolean upsert, Class<T> clazz) {
		assertValid();
		DBObject object = query.getDB().getCollection(query.getCollection())
			.findAndModify(query.toQueryObject(), null, query.getSortDBObject(),
				false, toModifierObject(), returnNew, upsert);
		return query.getObjectMapper().mapFromDBObject(object, clazz);
	}

	/**
	 * Performs an update with the current modifier object.
	 * @param upsert
	 * @param multi
	 * @param concern
	 * @param encoder
	 */
	public void update(boolean upsert, boolean multi, WriteConcern concern, DBEncoder encoder) {
		assertValid();
		query.getDB().getCollection(query.getCollection()).update(
			query.toQueryObject(), toModifierObject(), upsert, multi, concern, encoder);
	}

	/**
	 * Performs an update with the current modifier object.
	 * @param upsert
	 * @param multi
	 * @param concern
	 */
	public void update(boolean upsert, boolean multi, WriteConcern concern) {
		update(upsert, multi, concern, null);
	}

	/**
	 * Performs an update with the current modifier object.
	 * @param upsert
	 * @param multi
	 */
	public void update(boolean upsert, boolean multi) {
		update(upsert, multi, null, null);
	}

	/**
	 * Performs a single update.
	 */
	public void update() {
		update(false, false);
	}

	/**
	 * Performs a multi update.
	 */
	public void updateMulti() {
		update(false, true);
	}

	/**
	 * Performs a single upsert.
	 */
	public void upsert() {
		update(true, false);
	}

	/**
	 * Performs a multi upsert.
	 */
	public void upsertMulti() {
		update(true, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DBObject toModifierObject() {
		DBObject ret = super.toModifierObject();
		if (this.atomic) {
			ret.put("$atomic", true);
		}
		return ret;
	}

	

}
