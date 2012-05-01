package com.googlecode.mjorm.query;

import java.util.Collection;
import java.util.regex.Pattern;

import com.googlecode.mjorm.query.SimpleCriteria.Operator;
import com.googlecode.mjorm.query.TypeCriteria.Type;

public class Criterion {
	
	public static <T> EqualsCriteria<T> eq(T value) {
		return new EqualsCriteria<T>(value);
	}

	public static <T> SimpleCriteria<T> gt(T value) {
		return new SimpleCriteria<T>(Operator.GT, value);
	}

	public static <T> SimpleCriteria<T> gte(T value) {
		return new SimpleCriteria<T>(Operator.GTE, value);
	}

	public static <T> SimpleCriteria<T> lt(T value) {
		return new SimpleCriteria<T>(Operator.LT, value);
	}

	public static <T> SimpleCriteria<T> lte(T value) {
		return new SimpleCriteria<T>(Operator.LTE, value);
	}

	public static <T> SimpleCriteria<T> ne(T value) {
		return new SimpleCriteria<T>(Operator.NE, value);
	}

	public static <T> SimpleCriteria<T> in(T values) {
		return new SimpleCriteria<T>(Operator.IN, values);
	}

	public static <T> SimpleCriteria<T[]> in(T... values) {
		return new SimpleCriteria<T[]>(Operator.IN, values);
	}

	public static <T> SimpleCriteria<Collection<T>> in(Collection<T> values) {
		return new SimpleCriteria<Collection<T>>(Operator.IN, values);
	}

	public static <T> SimpleCriteria<T> nin(T values) {
		return new SimpleCriteria<T>(Operator.NIN, values);
	}

	public static <T> SimpleCriteria<T[]> nin(T... values) {
		return new SimpleCriteria<T[]>(Operator.NIN, values);
	}

	public static <T> SimpleCriteria<Collection<T>> nin(Collection<T> values) {
		return new SimpleCriteria<Collection<T>>(Operator.NIN, values);
	}

	public static <T> SimpleCriteria<T> all(T values) {
		return new SimpleCriteria<T>(Operator.ALL, values);
	}

	public static <T> SimpleCriteria<T[]> all(T... values) {
		return new SimpleCriteria<T[]>(Operator.ALL, values);
	}

	public static <T> SimpleCriteria<Collection<T>> all(Collection<T> values) {
		return new SimpleCriteria<Collection<T>>(Operator.ALL, values);
	}

	public static ExistsCriteria exists(Boolean value) {
		return new ExistsCriteria(value);
	}

	public static ModCriteria mod(Number left, Number right) {
		return new ModCriteria(left, right);
	}

	public static RegexCriteria regex(Pattern pattern) {
		return new RegexCriteria(pattern);
	}

	public static RegexCriteria regex(String pattern) {
		return new RegexCriteria(pattern);
	}

	public static RegexCriteria regex(String pattern, int flags) {
		return new RegexCriteria(pattern, flags);
	}
	
	public static SizeCriteria size(Number size) {
		return new SizeCriteria(size);
	}
	
	public static TypeCriteria type(Number typeCode) {
		return new TypeCriteria(typeCode);
	}
	
	public static TypeCriteria type(Type type) {
		return new TypeCriteria(type);
	}

	public static ElemMatchCriteria elemMatch() {
		return new ElemMatchCriteria();
	}

	public static ElemMatchCriteria elemMatch(Query query) {
		return new ElemMatchCriteria(query);
	}

	public static QueryGroup group() {
		return new QueryGroup();
	}

	public static QueryGroup group(Query... queries) {
		QueryGroup ret = new QueryGroup();
		for (Query query : queries) {
			ret.add(query);
		}
		return ret;
	}

	public static NotCriteria not(Criteria<?> criteria) {
		return new NotCriteria(criteria);
	}
}
