package com.googlecode.mjorm.query.modifiers;

import com.googlecode.mjorm.ObjectMapper;
import com.mongodb.DBObject;

public abstract class AbstractModifier
	implements Modifier {

	/**
	 * {@inheritDoc}
	 */
	public abstract DBObject toModifierObject(String propertyName, ObjectMapper mapper);

}
