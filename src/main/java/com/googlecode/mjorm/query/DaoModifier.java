package com.googlecode.mjorm.query;

import com.googlecode.mjorm.MongoDao;
import com.googlecode.mjorm.query.modifiers.AbstractQueryModifiers;
import com.mongodb.DBEncoder;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class DaoModifier
	extends AbstractQueryModifiers<DaoModifier> {

	private MongoDao mongoDao;
	private DaoQuery query;

	/**
	 * Creates the {@link DaoModifier}.
	 * @param mongoDao the {@link MongoDao}
	 */
	public DaoModifier(DaoQuery query) {
		this.query 		= query;
		this.mongoDao	= query.getMongoDao();
		this.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DaoModifier self() {
		return this;
	}

	/**
	 * Removes the objects matched by this query.
	 * @return the {@link WriteResult}
	 */
	public WriteResult deleteObjects() {
		if (query.getCollection()==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.getCollection(query.getCollection())
			.remove(query.toQueryObject());
	}

	/**
	 * Performs a findAndDelete for the current query.
	 * @return the object found and modified
	 */
	public <T> T findAndDelete(Class<T> clazz) {
		if (query.getCollection()==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndDelete(
			query.getCollection(), query.toQueryObject(),
			query.getSortDBObject(), clazz);
	}

	/**
	 * Performs a findAndDelete for the current query.
	 * @return the object found and modified
	 * @param fields the fields to populate on the return object
	 */
	public <T> T findAndDelete(Class<T> clazz, String[] fields) {
		if (query.getCollection()==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndDelete(
			query.getCollection(), query.toQueryObject(),
			query.getSortDBObject(), clazz, fields);
	}

	/**
	 * Performs a findAndModify for the current query.
	 * @param returnNew whether or not to return the new or old object
	 * @param upsert create new if it doesn't exist
	 * @param clazz the type of object
	 * @return the object
	 */
	public <T> T findAndModify(boolean returnNew, boolean upsert, Class<T> clazz) {
		if (query.getCollection()==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndModify(
			query.getCollection(), query.toQueryObject(), query.getSortDBObject(),
			toModifierObject(), returnNew, upsert, clazz);
	}

	/**
	 * Performs a findAndModify for the current query.
	 * @param returnNew whether or not to return the new or old object
	 * @param upsert create new if it doesn't exist
	 * @param clazz the type of object
	 * @param fields the fields to populate on the return object
	 * @return the object
	 */
	public <T> T findAndModify(boolean returnNew, boolean upsert, Class<T> clazz, String[] fields) {
		if (query.getCollection()==null) {
			throw new IllegalStateException("collection must be specified");
		}
		return mongoDao.findAndModify(
			query.getCollection(), query.toQueryObject(), query.getSortDBObject(),
			toModifierObject(), returnNew, upsert, clazz, fields);
	}

	/**
	 * Performs an update with the current modifier object.
	 * @param upsert
	 * @param multi
	 * @param concern
	 * @param encoder
	 */
	public void update(boolean upsert, boolean multi, WriteConcern concern, DBEncoder encoder) {
		if (query.getCollection()==null) {
			throw new IllegalStateException("collection must be specified");
		}
		mongoDao.update(
			query.getCollection(), query.toQueryObject(),
			toModifierObject(), upsert, multi,
			concern, encoder);
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

}
