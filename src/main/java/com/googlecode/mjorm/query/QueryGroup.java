package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.googlecode.mjorm.query.criteria.AbstractCriterion;
import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;
import com.mongodb.BasicDBList;

public class QueryGroup
	extends AbstractCriterion {

	private List<Query> queryCriterions = new ArrayList<Query>();

	public QueryGroup() {
		
	}

	public QueryGroup(Collection<Query> queries) {
		addAll(queries);
	}

	public QueryGroup(Query... queries) {
		addAll(queries);
	}

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
	public QueryGroup add(Query queryCriterion) {
		queryCriterions.add(queryCriterion);
		return this;
	}

	/**
	 * Adds a {@link AbstractQueryCriterion} to this group.
	 * @param queryCriterion the {@link AbstractQueryCriterion}
	 */
	public QueryGroup addAll(Collection<Query> queries) {
		queryCriterions.addAll(queries);
		return this;
	}

	/**
	 * Adds a {@link AbstractQueryCriterion} to this group.
	 * @param queryCriterion the {@link AbstractQueryCriterion}
	 */
	public QueryGroup addAll(Query... queries) {
		queryCriterions.addAll(Arrays.asList(queries));
		return this;
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
