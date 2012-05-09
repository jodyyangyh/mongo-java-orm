package com.googlecode.mjorm.mql;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class InterpreterResult {

	private DBCursor cursor;

	private DBObject object;

	public InterpreterResult(DBCursor cursor, DBObject object) {
		this.cursor		= cursor;
		this.object		= object;
	}

	/**
	 * @return the cursor
	 */
	protected DBCursor getCursor() {
		return cursor;
	}

	/**
	 * @return the object
	 */
	protected DBObject getObject() {
		return object;
	}

}
