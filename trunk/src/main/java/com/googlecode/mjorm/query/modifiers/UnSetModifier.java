package com.googlecode.mjorm.query.modifiers;

public class UnSetModifier
	extends AbstractValueModifier<Object> {

	public UnSetModifier(Object value) {
		super(value, "$unset");
	}

}
