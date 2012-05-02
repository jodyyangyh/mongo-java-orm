package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public abstract class AbstractCriterionBuilder<T extends AbstractCriterionBuilder<T>>
	extends AbstractCriterion {

	protected Stack<String> propertyStack = new Stack<String>();
	protected Map<List<String>, List<Criterion>> criteriaMap = new LinkedHashMap<List<String>, List<Criterion>>();

	protected abstract T self();

	/**
	 * Clears the query.
	 */
	public void clear() {
		criteriaMap.clear();
		propertyStack.clear();
	}

	/**
	 * Returns the {@link Criterion} specified for a
	 * given field.
	 * @param property the field
	 * @return the {@link Criterion}
	 */
	public Criterion[] getCriteria(String property) {
		List<String> key = propertyHierarchy(property);
		if (!criteriaMap.containsKey(key)) {
			return new Criterion[0];
		}
		return criteriaMap.get(key).toArray(new Criterion[0]);
	}

	/**
	 * Indicates whether or not there are {@link Criteria}
	 * specified for the given property.
	 * @param property the property
	 * @return true or false
	 */
	public Boolean hasCriteriaFor(String property) {
		return getCriteria(property).length > 0;
	}

	/**
	 * Pushed a property onto the property stack.
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T push(String property) {
		propertyStack.push(property);
		return self();
	}

	/**
	 * Pops a property off of the property stack.
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T pop() {
		propertyStack.pop();
		return self();
	}

	/**
	 * Adds a {@link Criterion} to the query.
	 * @param property the property name
	 * @param criterion the {@link Criterion}
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T add(String property, Criterion criterion) {
		List<String> key = propertyHierarchy(property);
		if (!criteriaMap.containsKey(key)) {
			criteriaMap.put(key, new ArrayList<Criterion>());
		}
		criteriaMap.get(key).add(criterion);
		return self();
	}

	/**
	 * Adds a query the {@link QueryGroup} for
	 * {@code $or}.
	 * @param query the {@link AbstractQueryCriterion} to add
	 * @return the {@link QueryGroup}
	 */
	public QueryGroup getGroup(String name) {
		List<String> key = propertyHierarchy(name);
		if (!criteriaMap.containsKey(key)) {
			criteriaMap.put(key, new ArrayList<Criterion>());
			criteriaMap.get(key).add(new QueryGroup());
		}
		return (QueryGroup)criteriaMap.get(key).get(0);
	}

	/**
	 * Adds a query the {@link QueryGroup} for
	 * {@code $or}.
	 * @param queryCriterion the {@link AbstractQueryCriterion} to add
	 * @return the {@link AbstractQueryCriterion} for chaining
	 */
	public T group(String name, Query queryCriterion) {
		getGroup(name).add(queryCriterion);
		return self();
	}

	/**
	 * Returns an array of the current property
	 * hierarchy plus the property given.
	 * @param property the property
	 * @return an array
	 */
	protected List<String> propertyHierarchy(String property) {
		List<String>  ret = new ArrayList<String>(propertyStack.size()+1);
		for (int i=0; i<propertyStack.size(); i++) {
			ret.add(propertyStack.get(i));
		}
		ret.add(property);
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DBObject toQueryObject() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		Stack<String> builderStack = new Stack<String>();
		for (Entry<List<String>, List<Criterion>> entry : criteriaMap.entrySet()) {
			String[] parts = entry.getKey().toArray(new String[0]);

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

			// make the Criteron create their query objects
			Object val = null;
			for (Criterion criterion : entry.getValue()) {

				// conver to query object
				Object curVal = criterion.toQueryObject();
				
				// can't merge
				if (val!=null
					&& (!DBObject.class.isInstance(val) || !DBObject.class.isInstance(curVal))) {
					throw new IllegalStateException(
						"A Criterion generated a value other than a "
						+"DBObject for a property that already has Criterion. "
						+"This usually means that two Criterion are being used "
						+"for a property that don't both create a DBObject and "
						+"and therefore can't be merged.");
					
				// first value
				} else if (val==null) {
					val = curVal;
				
				// merge them
				} else {
					DBObject.class.cast(val).putAll(
						DBObject.class.cast(curVal));
				}
			}

			// add the query
			builder.add(parts[parts.length-1], val);
		}
		return builder.get();
	}

}