package com.googlecode.mjorm.query.criteria;

import java.util.regex.Pattern;

public class RegexCriterion
	extends AbstractCriterion {

	private Pattern pattern;

	public RegexCriterion(Pattern pattern) {
		this.pattern = pattern;
	}

	public RegexCriterion(String regex, int flags) {
		this(Pattern.compile(regex, flags));
	}

	public RegexCriterion(String regex) {
		this(Pattern.compile(regex));
	}

	/**
	 * @return the pattern
	 */
	public Object getPattern() {
		return pattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object toQueryObject() {
		return pattern;
	}

}
