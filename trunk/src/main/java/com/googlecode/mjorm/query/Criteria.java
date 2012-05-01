package com.googlecode.mjorm.query;

import java.util.Collection;
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
	public static EqualsCriterion eq(Object value) {
		return new EqualsCriterion(value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion gt(Object value) {
		return new SimpleCriterion(Operator.GT, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion gte(Object value) {
		return new SimpleCriterion(Operator.GTE, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion lt(Object value) {
		return new SimpleCriterion(Operator.LT, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion lte(Object value) {
		return new SimpleCriterion(Operator.LTE, value);
	}
	
	/**
	 * {@see BetweenCriterion}
	 */
	public static BetweenCriterion between(Object left, Object right) {
		return new BetweenCriterion(left, right);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion ne(Object value) {
		return new SimpleCriterion(Operator.NE, value);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion in(Object values) {
		return new SimpleCriterion(Operator.IN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static  SimpleCriterion in(Object... values) {
		return new SimpleCriterion(Operator.IN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static  SimpleCriterion in(Collection<?> values) {
		return new SimpleCriterion(Operator.IN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion nin(Object values) {
		return new SimpleCriterion(Operator.NIN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static  SimpleCriterion nin(Object... values) {
		return new SimpleCriterion(Operator.NIN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static  SimpleCriterion nin(Collection<?> values) {
		return new SimpleCriterion(Operator.NIN, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static SimpleCriterion all(Object values) {
		return new SimpleCriterion(Operator.ALL, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static  SimpleCriterion all(Object... values) {
		return new SimpleCriterion(Operator.ALL, values);
	}
	
	/**
	 * {@see SimpleCriterion}
	 */
	public static  SimpleCriterion all(Collection<?> values) {
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
	public static ElemMatchCriterion elemMatch(Query query) {
		return new ElemMatchCriterion(query);
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
		for (Query query : queries) {
			ret.add(query);
		}
		return ret;
	}
	
	/**
	 * {@see NotCriterion}
	 */
	public static NotCriterion not(Criterion criteria) {
		return new NotCriterion(criteria);
	}
}
