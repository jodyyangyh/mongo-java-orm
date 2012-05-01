package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class SimpleCriteria<T>
	extends AbstractCriteria<DBObject> {

	public enum Operator {
		GT		("$gt"),
		GTE		("$gte"),
		LT		("$lt"),
		LTE		("$lte"),
		NE		("$ne"),
		IN		("$in"),
		NIN		("$nin"),
		ALL		("$all")
		;
		private String operator;
		Operator(String operator) {
			this.operator = operator;
		}
		public String getOperatorString() {
			return this.operator;
		}
	}

	private String operator;
	private T value;

	public SimpleCriteria(String operator, T value) {
		this.operator	= operator;
		this.value		= value;
	}

	public SimpleCriteria(Operator operator, T value) {
		this(operator.getOperatorString(), value);
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public DBObject toQueryObject() {
		return new BasicDBObject(operator, value);
	}

}
