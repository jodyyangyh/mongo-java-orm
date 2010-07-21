package com.googlecode.mjorm.jot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;

import com.googlecode.jot.ReflectionUtil;
import com.googlecode.jot.TranslationContext;
import com.googlecode.jot.TranslationException;
import com.googlecode.jot.TranslationHints;
import com.googlecode.jot.TypeTranslator;
import com.mongodb.BasicDBList;

/**
 * A {@link TypeTranslator} for {@link Collection}s.
 *
 */
@SuppressWarnings("unchecked")
public class CollectionTypeTranslator
	implements TypeTranslator<BasicDBList, Collection> {

	/**
	 * {@inheritDoc}
	 */
	public BasicDBList translateFromLocal(
		Collection local, TranslationContext converter, TranslationHints hints) {
		BasicDBList ret = new BasicDBList();
		for (Object obj : local) {
			ret.add(converter.translateFromLocal(obj));
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection translateToLocal(
		BasicDBList object, TranslationContext converter, Type desiredType, TranslationHints hints) {

		// get class
		Class<?> desiredClass = ReflectionUtil.clazz(desiredType);

		// get parameter types
		Type[] parameterTypes = (hints.getTypeParameters()!=null && hints.getTypeParameters().length>0)
			? hints.getTypeParameters()
			: ReflectionUtil.getTypeParameters(desiredType);
		if (parameterTypes==null || parameterTypes.length<1) {
			throw new TranslationException(
				"Unable to determine type parameters for collection: "+desiredType);
		}

		// create collection
		Collection ret;
		if (SortedSet.class.isAssignableFrom(desiredClass)) {
			ret = new TreeSet();
		} else if (Set.class.isAssignableFrom(desiredClass)) {
			ret = new HashSet();
		} else if (List.class.isAssignableFrom(desiredClass)) {
			ret = new ArrayList();
		} else {
			ret = new LinkedList();
		}

		// iterate and convert
		for (int i=0; i<object.size(); i++) {
			ret.add(converter.translateToLocal(object.get(i), parameterTypes[0]));
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsLocal(Type type) {
		return Collection.class.isAssignableFrom(ReflectionUtil.clazz(type));
	}

}
