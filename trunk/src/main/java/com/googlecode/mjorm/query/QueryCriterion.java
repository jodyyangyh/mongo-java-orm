package com.googlecode.mjorm.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Pattern;

import com.googlecode.mjorm.query.TypeCriterion.Type;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * An object for building MongoDB queries using
 * {@link Criterion}.
 * 
 * @see Criterion
 * @see Criteria
 */
public abstract class QueryCriterion<T extends QueryCriterion<T>>
	extends AbstractCriterion {

	private Stack<String> propertyStack = new Stack<String>();
	private Map<List<String>, List<Criterion>> criteriaMap = new LinkedHashMap<List<String>, List<Criterion>>();

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
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T push(String property) {
		propertyStack.push(property);
		return self();
	}

	/**
	 * Pops a property off of the property stack.
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T pop() {
		propertyStack.pop();
		return self();
	}

	/**
	 * Adds a {@link Criterion} to the query.
	 * @param property the property name
	 * @param criterion the {@link Criterion}
	 * @return the {@link QueryCriterion} for chaining
	 */
	@SuppressWarnings("unchecked")
	public T add(String property, Criterion criterion) {
		List<String> key = propertyHierarchy(property);
		if (!criteriaMap.containsKey(key)) {
			criteriaMap.put(key, new ArrayList<Criterion>());
		}
		criteriaMap.get(key).add(criterion);
		return (T)this;
	}

	/**
	 * Adds a query the {@link QueryGroup} for
	 * {@code $or}.
	 * @param query the {@link QueryCriterion} to add
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
	 * @param queryCriterion the {@link QueryCriterion} to add
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T group(String name, Query queryCriterion) {
		getGroup(name).add(queryCriterion);
		return self();
	}

	/**
	 * Adds a query the {@link QueryGroup} for
	 * {@code $or}.
	 * @param queryCriterion the {@link QueryCriterion} to add
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T or(Query queryCriterion) {
		group("$or", queryCriterion);
		return self();
	}

	/**
	 * Adds a query the {@link QueryGroup} for
	 * {@code $nor}.
	 * @param queryCriterion the {@link QueryCriterion} to add
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T nor(Query queryCriterion) {
		group("$nor", queryCriterion);
		return self();
	}

	/**
	 * Adds a query the {@link QueryGroup} for
	 * {@code $and}.
	 * @param queryCriterion the {@link QueryCriterion} to add
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T and(Query queryCriterion) {
		group("$and", queryCriterion);
		return self();
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

	/**
	 * Returns an array of the current property
	 * hierarchy plus the property given.
	 * @param property the property
	 * @return an array
	 */
	private List<String> propertyHierarchy(String property) {
		List<String>  ret = new ArrayList<String>(propertyStack.size()+1);
		for (int i=0; i<propertyStack.size(); i++) {
			ret.add(propertyStack.get(i));
		}
		ret.add(property);
		return ret;
	}

	/**
	 * {@see Criteria#eq(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T eq(String property, Object value) {
		return add(property, Criteria.eq(value));
	}

	/**
	 * {@see Criteria#gt(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T gt(String property, Object value) {
		return add(property, Criteria.gt(value));
	}

	/**
	 * {@see Criteria#gte(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T gte(String property, Object value) {
		return add(property, Criteria.gte(value));
	}

	/**
	 * {@see Criteria#lt(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T lt(String property, Object value) {
		return add(property, Criteria.lt(value));
	}

	/**
	 * {@see Criteria#lte(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T lte(String property, Object value) {
		return add(property, Criteria.lte(value));
	}

	/**
	 * {@see Criteria#between(Object, Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T between(String property, Object left, Object right) {
		return add(property, Criteria.between(left, right));
	}

	/**
	 * {@see Criteria#ne(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T ne(String property, Object value) {
		return add(property, Criteria.ne(value));
	}

	/**
	 * {@see Criteria#in(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T in(String property, Object values) {
		return add(property, Criteria.in(values));
	}

	/**
	 * {@see Criteria#in(T[])}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public <V> T in(String property, V... values) {
		return add(property, Criteria.in(values));
	}

	/**
	 * {@see Criteria#in(Collection)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T in(String property, Collection<?> values) {
		return add(property, Criteria.in(values));
	}

	/**
	 * {@see Criteria#nin(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T nin(String property, Object values) {
		return add(property, Criteria.nin(values));
	}

	/**
	 * {@see Criteria#nin(T[])}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T nin(String property, Object... values) {
		return add(property, Criteria.nin(values));
	}

	/**
	 * {@see Criteria#nin(Collection)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T nin(String property, Collection<?> values) {
		return add(property, Criteria.nin(values));
	}

	/**
	 * {@see Criteria#all(Object)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T all(String property, Object values) {
		return add(property, Criteria.all(values));
	}

	/**
	 * {@see Criteria#all(T[])}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T all(String property, Object... values) {
		return add(property, Criteria.all(values));
	}

	/**
	 * {@see Criteria#all(Collection)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T all(String property, Collection<?> values) {
		return add(property, Criteria.all(values));
	}

	/**
	 * {@see Criteria#exists(Boolean)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T exists(String property, Boolean value) {
		return add(property, Criteria.exists(value));
	}

	/**
	 * {@see Criteria#mod(Number, Number)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T mod(String property, Number left, Number right) {
		return add(property, Criteria.mod(left, right));
	}

	/**
	 * {@see Criteria#regex(Pattern)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T regex(String property, Pattern pattern) {
		return add(property, Criteria.regex(pattern));
	}

	/**
	 * {@see Criteria#regex(String)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T regex(String property, String pattern) {
		return add(property, Criteria.regex(pattern));
	}

	/**
	 * {@see Criteria#regex(String, int)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T regex(String property, String pattern, int flags) {
		return add(property, Criteria.regex(pattern, flags));
	}

	/**
	 * {@see Criteria#size(Number)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T size(String property, Number size) {
		return add(property, Criteria.size(size));
	}

	/**
	 * {@see Criteria#type(Number)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T type(String property, Number typeCode) {
		return add(property, Criteria.type(typeCode));
	}

	/**
	 * {@see Criteria#type(Type)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T type(String property, Type type) {
		return add(property, Criteria.type(type));
	}

	/**
	 * {@see Criteria#elemMatch(QueryCriterion)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T elemMatch(String property, Query queryCriterion) {
		return add(property, Criteria.elemMatch(queryCriterion));
	}

	/**
	 * {@see Criteria#not(Criterion)}
	 * @return the {@link QueryCriterion} for chaining
	 */
	public T not(String property, Criterion criteria) {
		return add(property, Criteria.not(criteria));
	}
}
