package com.googlecode.mjorm.jot;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import com.googlecode.jot.ReflectionUtil;
import com.googlecode.jot.TranslationContext;
import com.googlecode.jot.TranslationHints;
import com.googlecode.jot.TypeTranslator;
import com.mongodb.BasicDBList;

/**
 * Translates array types.
 */
public class ArrayTypeTranslator
	implements TypeTranslator<BasicDBList, Object[]> {

		/**
		 * {@inheritDoc}
		 */
	public BasicDBList translateFromLocal(
		Object[] local, TranslationContext converter, TranslationHints hints) {
		BasicDBList ret = new BasicDBList();
		for (Object obj : local) {
			ret.add(converter.translateFromLocal(obj));
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] translateToLocal(
		BasicDBList object, TranslationContext converter, Type desiredType, TranslationHints hints) {

		// get class
		Class<?> desiredClass = ReflectionUtil.clazz(desiredType);
		Class<?> componentType = desiredClass.getComponentType();

		// create array
		Object ret = Array.newInstance(componentType, object.size());

		// iterate and convert
		for (int i=0; i<object.size(); i++) {
			Array.set(ret, i, converter.translateToLocal(object.get(i), componentType));
		}
		return Object[].class.cast(ret);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsLocal(Type type) {
		return ReflectionUtil.clazz(type).isArray();
	}

}
