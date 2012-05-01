package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;

public class SimpleCriterion
	extends AbstractCriterion {

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
	private Object value;

	public SimpleCriterion(String operator, Object value) {
		this.operator	= operator;
		this.value		= value;
	}

	public SimpleCriterion(Operator operator, Object value) {
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
	public Object getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toQueryObject() {
		return new BasicDBObject(operator, value);
	}

}
