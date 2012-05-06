package com.googlecode.mjorm.query.criteria;

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
		Object optimalValue = null;
		
		if (operator.equals(Operator.IN.getOperatorString())) {
			optimalValue = Criteria.optimalArrayValue(value, true);
			
		} else if (operator.equals(Operator.NIN.getOperatorString())) {
			optimalValue = Criteria.optimalArrayValue(value, true);
			
		} else if (operator.equals(Operator.ALL.getOperatorString())) {
			optimalValue = Criteria.optimalArrayValue(value, true);
			
		} else {
			optimalValue = value;
		}
		
		return new BasicDBObject(operator, optimalValue);
	}

}
