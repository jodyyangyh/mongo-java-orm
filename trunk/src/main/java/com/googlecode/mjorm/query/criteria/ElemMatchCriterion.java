package com.googlecode.mjorm.query.criteria;

import com.googlecode.mjorm.mql.MqlFunction;
import com.googlecode.mjorm.mql.MqlFunctionImpl;
import com.googlecode.mjorm.query.Query;
import com.mongodb.BasicDBObject;

public class ElemMatchCriterion
	implements Criterion {

	private Query queryCriterion;

	public ElemMatchCriterion(Query queryCriterion) {
		this.queryCriterion = queryCriterion;
	}

	public ElemMatchCriterion() {
		this.queryCriterion = new Query();
	}

	/**
	 * @return the queryCriterion
	 */
	public Query getQuery() {
		return queryCriterion;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toQueryObject() {
		return new BasicDBObject("$elemMatch", queryCriterion.toQueryObject());
	}

	public static MqlFunction createFunction(final String functionName) {
		return new MqlFunctionImpl() {
			protected void init() {
				setFunctionName(functionName);
				setAllowQuery(true);
			}
			@Override
			protected Criterion doCreate(Query query) {
				return new ElemMatchCriterion(query);
			}
		};
	}

}
