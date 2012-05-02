package com.googlecode.mjorm.query;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.googlecode.mjorm.query.SimpleCriterion.Operator;
import com.googlecode.mjorm.query.TypeCriterion.Type;

/**
 * Utility class for easily creating {@link Criterion}.
 */
public class Criteria {
	
	/**
	 * {@see EqualsCriterion}
	 */
	public static <V> EqualsCriterion eq(V value) {
		return new EqualsCriterion(value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion gt(V value) {
		return new SimpleCriterion(Operator.GT, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion gte(V value) {
		return new SimpleCriterion(Operator.GTE, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion lt(V value) {
		return new SimpleCriterion(Operator.LT, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion lte(V value) {
		return new SimpleCriterion(Operator.LTE, value);
	}
	
	/**
	 * {@see BetweenCriterion}
	 */
	public static <V> BetweenCriterion between(V left, V right) {
		return new BetweenCriterion(left, right);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion ne(V value) {
		return new SimpleCriterion(Operator.NE, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion in(V... values) {
		return new SimpleCriterion(Operator.IN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion in(Collection<V> values) {
		return new SimpleCriterion(Operator.IN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion nin(V... values) {
		return new SimpleCriterion(Operator.NIN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion nin(Collection<V> values) {
		return new SimpleCriterion(Operator.NIN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion all(V... values) {
		return new SimpleCriterion(Operator.ALL, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static <V> SimpleCriterion all(Collection<V> values) {
		return new SimpleCriterion(Operator.ALL, values);
	}
	
	/**
	 * {@see ExistsCriterion}
	 */
	public static ExistsCriterion exists(Boolean value) {
		return new ExistsCriterion(value);
	}
	
	/**
	 * {@see ModCriterion}
	 */
	public static ModCriterion mod(Number left, Number right) {
		return new ModCriterion(left, right);
	}
	
	/**
	 * {@see RegexCriterion}
	 */
	public static RegexCriterion regex(Pattern pattern) {
		return new RegexCriterion(pattern);
	}
	
	/**
	 * {@see RegexCriterion}
	 */
	public static RegexCriterion regex(String pattern) {
		return new RegexCriterion(pattern);
	}
	
	/**
	 * {@see RegexCriterion}
	 */
	public static RegexCriterion regex(String pattern, int flags) {
		return new RegexCriterion(pattern, flags);
	}
	
	/**
	 * {@see SizeCriterion}
	 */
	public static SizeCriterion size(Number size) {
		return new SizeCriterion(size);
	}
	
	/**
	 * {@see TypeCriterion}
	 */
	public static TypeCriterion type(Number typeCode) {
		return new TypeCriterion(typeCode);
	}
	
	/**
	 * {@see TypeCriterion}
	 */
	public static TypeCriterion type(Type type) {
		return new TypeCriterion(type);
	}
	
	/**
	 * {@see ElemMatchCriterion}
	 */
	public static ElemMatchCriterion elemMatch() {
		return new ElemMatchCriterion();
	}
	
	/**
	 * {@see ElemMatchCriterion}
	 */
	public static ElemMatchCriterion elemMatch(Query queryCriterion) {
		return new ElemMatchCriterion(queryCriterion);
	}
	
	/**
	 * {@see QueryGroup}
	 */
	public static QueryGroup group() {
		return new QueryGroup();
	}
	
	/**
	 * {@see QueryGroup}
	 */
	public static QueryGroup group(Query... queries) {
		QueryGroup ret = new QueryGroup();
		for (Query queryCriterion : queries) {
			ret.add(queryCriterion);
		}
		return ret;
	}
	
	/**
	 * {@see NotCriterion}
	 */
	public static NotCriterion not(Criterion criteria) {
		return new NotCriterion(criteria);
	}

	/**
	 * Returns an optimal array value for use in a MongoDB query.
	 * Collections or Arrays can be passed in and the optimal
	 * value is returned according to the following rules:
	 * <ul>
	 * 	<li>(null) if it's null, return null</li>
	 * 	<li>(Object) if it's not an array or collection, return the value</li>
	 * 	<li>(Object) if it only has 1 element, return the element</li>
	 * 	<li>(Object|Object[]) if enforceUnique is true remove dupes. return unique values.
	 * 		if there is only one value after removing dupes, return only that value.</li>
	 * 	<li>(Object[]) return all of the values if enforceUnique is false
	 * </ul>
	 * @param obj the value to inspect
	 * @param enforceUnique whether or not to enforce unique values in the return
	 * @return the optimal array value
	 */
    public static Object optimalArrayValue(Object obj, boolean enforceUnique) {

        // bail on null
        if (obj == null) {
        	return null;
        }

        // no array, no collection
        Class<?> clazz = obj.getClass();
        if (!clazz.isArray() && !Collection.class.isAssignableFrom(clazz)) {
        	return obj;
        }

        // get array
        Object[] objs = null;
        if (clazz.isArray()) {
        	objs = (Object[])obj;
        } else {
        	objs = Collection.class.cast(obj).toArray();
        }

        // if we have none, bail
        if (objs.length==0) {
        	return null;

        // if we have one, return it
        } else if (objs.length==1) {
        	return objs[0];

        // if we're enforcing unique values,
        // remove duplicates
        } else if (enforceUnique) {
            Set<Object> valueObjs = new LinkedHashSet<Object>();
            for (Object o : objs) {
                    valueObjs.add(o);
            }
            objs = valueObjs.toArray();
            return (objs.length==1) ? objs[0] : objs;
        }

        // return the array as is
        return objs;
    }

}
