package com.googlecode.mjorm.query;

import java.util.regex.Pattern;

public class RegexCriteria
	extends AbstractCriteria<Pattern> {

	private Pattern pattern;

	public RegexCriteria(Pattern pattern) {
		this.pattern = pattern;
	}

	public RegexCriteria(String regex, int flags) {
		this(Pattern.compile(regex, flags));
	}

	public RegexCriteria(String regex) {
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
	public Pattern toQueryObject() {
		return pattern;
	}

}
