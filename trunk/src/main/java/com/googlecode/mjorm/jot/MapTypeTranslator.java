package com.googlecode.mjorm.jot;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.jot.ReflectionUtil;
import com.googlecode.jot.TranslationContext;
import com.googlecode.jot.TranslationException;
import com.googlecode.jot.TranslationHints;
import com.googlecode.jot.TypeTranslator;
import com.mongodb.BasicDBObject;

/**
 * A {@link TypeTranslator} for {@link Collection}s.
 *
 */
@SuppressWarnings("rawtypes")
public class MapTypeTranslator
	implements TypeTranslator<BasicDBObject, Map> {

	/**
	 * {@inheritDoc}
	 */
	public BasicDBObject translateFromLocal(
		Map local, TranslationContext converter, TranslationHints hints) {
		BasicDBObject ret = new BasicDBObject();
		for (Object key : local.keySet()) {
			if (!String.class.isInstance(key)) {
				throw new TranslationException(
					"Unable to translate maps without String keys");
			}
			ret.put(String.class.cast(key), converter.translateFromLocal(local.get(key)));
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Map translateToLocal(
		BasicDBObject object, TranslationContext converter, Type desiredType, TranslationHints hints) {

		// get parameter types
		Type[] parameterTypes = (hints.getTypeParameters()!=null && hints.getTypeParameters().length>0)
			? hints.getTypeParameters()
			: ReflectionUtil.getTypeParameters(desiredType);
		if (parameterTypes==null || parameterTypes.length<2) {
			throw new TranslationException(
				"Unable to determine type parameters for map: "+desiredType);
		} else if (!String.class.isAssignableFrom(ReflectionUtil.clazz(parameterTypes[0]))) {
			throw new TranslationException(
				"Unable to translate maps without String keys");
		}

		// create and convert
		Map ret = new HashMap();
		for (Entry<String, Object> entry : object.entrySet()) {

			// check for wildcard types
			Type type = parameterTypes[1];
			if (WildcardType.class.isInstance(type) && type!=null) {
				type = entry.getValue().getClass();
			}

			ret.put(entry.getKey(), converter.translateToLocal(entry.getValue(), type));
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsLocal(Type type) {
		return Map.class.isAssignableFrom(ReflectionUtil.clazz(type));
	}

}
