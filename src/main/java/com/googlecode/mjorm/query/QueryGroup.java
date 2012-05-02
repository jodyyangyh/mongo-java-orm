package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;

public class QueryGroup
	extends AbstractCriterion {

	private List<Query> queryCriterions = new ArrayList<Query>();

	/**
	 * Adds a {@link AbstractQueryCriterion} to the list of conditions.
	 * @return the {@link AbstractQueryCriterion}
	 */
	public Query add() {
		Query ret = new Query();
		add(ret);
		return ret;
	}

	/**
	 * Adds a {@link AbstractQueryCriterion} to this group.
	 * @param queryCriterion the {@link AbstractQueryCriterion}
	 */
	public void add(Query queryCriterion) {
		queryCriterions.add(queryCriterion);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject() {
		BasicDBList list = new BasicDBList();
		for (Query queryCriterion : queryCriterions) {
			list.add(queryCriterion.toQueryObject());
		}
		return list;
	}

}
