package com.googlecode.mjorm.query;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ModCriteria
	extends AbstractCriteria<DBObject> {

	private Number left;
	private Number right;

	public ModCriteria(Number left, Number right) {
		this.left		= left;
		this.right		= right;
	}

	/**
	 * @return the left
	 */
	public Number getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public Number getRight() {
		return right;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DBObject toQueryObject() {
		BasicDBList numbers = new BasicDBList();
		numbers.add(left);
		numbers.add(right);
		return new BasicDBObject("$mod", numbers);
	}

}
