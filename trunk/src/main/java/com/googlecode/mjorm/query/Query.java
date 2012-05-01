package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Pattern;

import com.googlecode.mjorm.query.TypeCriteria.Type;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class Query
	extends AbstractCriteria<DBObject> {

	private Stack<String> propertyStack = new Stack<String>();
	private Map<String[], Criteria<?>> criterias = new LinkedHashMap<String[], Criteria<?>>();

	public Query start() {
		return new Query();
	}

	public Query push(String property) {
		propertyStack.push(property);
		return this;
	}

	public Query pop() {
		propertyStack.pop();
		return this;
	}

	private String[] propertyHierarchy(String property) {
		List<String> ret = new ArrayList<String>();
		for (String element : propertyStack) {
			ret.add(element);
		}
		ret.add(property);
		return ret.toArray(new String[0]);
	}

	public Query add(String property, Criteria<?> criteria) {
		criterias.put(propertyHierarchy(property), criteria);
		return this;
	}

	public Query or(Query query) {
		String[] hierarchy = propertyHierarchy("$or");
		if (!criterias.containsKey(hierarchy)) {
			criterias.put(hierarchy, new QueryGroup());
		}
		QueryGroup group = (QueryGroup)criterias.get(hierarchy);
		group.add(query);
		return this;
	}

	public Query nor(Query query) {
		String[] hierarchy = propertyHierarchy("$nor");
		if (!criterias.containsKey(hierarchy)) {
			criterias.put(hierarchy, new QueryGroup());
		}
		QueryGroup group = (QueryGroup)criterias.get(hierarchy);
		group.add(query);
		return this;
	}

	public Query and(Query query) {
		String[] hierarchy = propertyHierarchy("$and");
		if (!criterias.containsKey(hierarchy)) {
			criterias.put(hierarchy, new QueryGroup());
		}
		QueryGroup group = (QueryGroup)criterias.get(hierarchy);
		group.add(query);
		return this;
	}

	public void clear() {
		criterias.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DBObject toQueryObject() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		Stack<String> builderStack = new Stack<String>();
		for (Entry<String[], Criteria<?>> entry : criterias.entrySet()) {
			String[] parts = entry.getKey();
			// come out
			while (builderStack.size()>parts.length-1) {
				builderStack.pop();
				builder.pop();
			}
			// come out until matched
			if (builderStack.size()>0) {
				for (int i=parts.length-2;
					i>0 && !builderStack.empty() && !builderStack.peek().equals(parts[i]);
					i--) {
					builderStack.pop();
					builder.pop();
				}
			}
			// go deeper
			for (int i=builderStack.size(); i<parts.length-1; i++) {
				builderStack.push(parts[i]);
				builder.push(parts[i]);
			}
			builder.add(parts[parts.length-1], entry.getValue().toQueryObject());
		}
		return builder.get();
	}

	public <T> Query eq(String property, T value) {
		return add(property, new EqualsCriteria<T>(value));
	}

	public <T> Query gt(String property, T value) {
		return add(property, Criterion.gt(value));
	}

	public <T> Query gte(String property, T value) {
		return add(property, Criterion.gte(value));
	}

	public <T> Query lt(String property, T value) {
		return add(property, Criterion.lt(value));
	}

	public <T> Query lte(String property, T value) {
		return add(property, Criterion.lte(value));
	}

	public <T> Query ne(String property, T value) {
		return add(property, Criterion.ne(value));
	}

	public <T> Query in(String property, T values) {
		return add(property, Criterion.in(values));
	}

	public <T> Query in(String property, T... values) {
		return add(property, Criterion.in(values));
	}

	public <T> Query in(String property, Collection<T> values) {
		return add(property, Criterion.in(values));
	}

	public <T> Query nin(String property, T values) {
		return add(property, Criterion.nin(values));
	}

	public <T> Query nin(String property, T... values) {
		return add(property, Criterion.nin(values));
	}

	public <T> Query nin(String property, Collection<T> values) {
		return add(property, Criterion.nin(values));
	}

	public <T> Query all(String property, T values) {
		return add(property, Criterion.all(values));
	}

	public <T> Query all(String property, T... values) {
		return add(property, Criterion.all(values));
	}

	public <T> Query all(String property, Collection<T> values) {
		return add(property, Criterion.all(values));
	}

	public Query exists(String property, Boolean value) {
		return add(property, Criterion.exists(value));
	}

	public Query mod(String property, Number left, Number right) {
		return add(property, Criterion.mod(left, right));
	}

	public Query regex(String property, Pattern pattern) {
		return add(property, Criterion.regex(pattern));
	}

	public Query regex(String property, String pattern) {
		return add(property, Criterion.regex(pattern));
	}

	public Query regex(String property, String pattern, int flags) {
		return add(property, Criterion.regex(pattern, flags));
	}
	
	public Query size(String property, Number size) {
		return add(property, Criterion.size(size));
	}
	
	public Query type(String property, Number typeCode) {
		return add(property, Criterion.type(typeCode));
	}
	
	public Query type(String property, Type type) {
		return add(property, Criterion.type(type));
	}

	public Query elemMatch(String property, Query query) {
		return add(property, Criterion.elemMatch(query));
	}

	public Query not(String property, Criteria<?> criteria) {
		return add(property, Criterion.not(criteria));
	}

}
