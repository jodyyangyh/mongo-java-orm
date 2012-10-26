package com.googlecode.mjorm.query.modifiers;

import java.util.Collection;

import com.googlecode.mjorm.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class PushAllModifier
	extends AbstractModifier {

	private Object[] values;

	public PushAllModifier(Object[] values) {
		this.values = new Object[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
	}

	public PushAllModifier(Collection<?> values) {
		this.values = values.toArray(new Object[0]);
	}

	@Override
	public DBObject toModifierObject(String propertyName, ObjectMapper mapper) {
		return new BasicDBObject("$pushAll",
			new BasicDBObject(propertyName, mapper.unmapValue(this.values)));
	}

}
