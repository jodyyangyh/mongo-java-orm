package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;

public class QueryGroup
	extends AbstractCriteria<BasicDBList> {

	private List<Query> queries = new ArrayList<Query>();

	/**
	 * Adds a {@link Query} to the list of conditions.
	 * @return the {@link Query}
	 */
	public Query add() {
		Query ret = new Query();
		add(ret);
		return ret;
	}

	public void add(Query criteria) {
		queries.add(criteria);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicDBList toQueryObject() {
		BasicDBList list = new BasicDBList();
		for (Query query : queries) {
			list.add(query.toQueryObject());
		}
		return list;
	}

}
