package com.googlecode.mjorm.query.criteria;

import java.util.regex.Pattern;

public class RegexCriterion
	extends EqualsCriterion {

	public RegexCriterion(Pattern pattern) {
		super(pattern);
	}

	public RegexCriterion(String regex, int flags) {
		this(Pattern.compile(regex, flags));
	}

	public RegexCriterion(String regex) {
		this(Pattern.compile(regex));
	}

}
