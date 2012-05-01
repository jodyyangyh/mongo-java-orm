package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;

public class QueryGroup
	extends AbstractCriterion {

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

	/**
	 * Adds a {@link Query} to this group.
	 * @param query the {@link Query}
	 */
	public void add(Query query) {
		queries.add(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toQueryObject() {
		BasicDBList list = new BasicDBList();
		for (Query query : queries) {
			list.add(query.toQueryObject());
		}
		return list;
	}

}
