package com.googlecode.mjorm.jot;

import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.googlecode.jot.ReflectionUtil;
import com.googlecode.jot.TranslationContext;
import com.googlecode.jot.TranslationHints;
import com.googlecode.jot.TypeTranslator;

/**
 * {@link TypeTranslator} for enums.
 *
 */
public class ObjectIdTypeTranslator
	implements TypeTranslator<ObjectId, String> {

	/**
	 * {@inheritDoc}
	 */
	public ObjectId translateFromLocal(String local, TranslationContext ctx, TranslationHints hints) {
		return new ObjectId(local);
	}

	/**
	 * {@inheritDoc}
	 */
	public String translateToLocal(ObjectId object, TranslationContext ctx, Type desiredType, TranslationHints hints) {
		return object.toStringMongod();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsLocal(Type type) {
		Class<?> clazz = ReflectionUtil.clazz(type);
		return clazz!=null && ObjectId.class.isAssignableFrom(clazz);
	}

}
