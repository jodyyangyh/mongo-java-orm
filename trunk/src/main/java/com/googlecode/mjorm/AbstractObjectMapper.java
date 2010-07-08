package com.googlecode.mjorm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Abstract {@link ObjectMapper} that handles most of the work of
 * object conversion for the subclass.
 */
public abstract class AbstractObjectMapper
	implements ObjectMapper {

	/**
	 * Must be implemented by the super class to indicate
	 * whether or not it can convert the given class.
	 * @param clazz the class
	 * @return true if it can convert, false otherwise
	 */
	protected abstract boolean canConvert(Class<?> clazz);

	/**
	 * Converts a java object to a DBObject.
	 * @param value the value to convert
	 * @param objectClass the type to convert it as
	 * @param paramTypes the potential generic parameter types
	 * @return the DBObject
	 * @throws Exception on error
	 */
	@SuppressWarnings("unchecked")
	protected Object convertToDBObject(Object value, Class<?> objectClass, Class<?>[] paramTypes)
		throws Exception {

		// Null
		if (value==null) {
			return null;

		// mapper can map it itself
		} else if (canConvert(objectClass)) {
			return translateToDBObject(value, Class.class.cast(objectClass));

		// primitives
		} else if (ReflectionUtil.isPrimitive(objectClass)) {
			return value;

		// collections
		} else if (Collection.class.isInstance(value)
			&& Collection.class.isAssignableFrom(objectClass)) {

			// cast to collection
			Collection<Object> collection = Collection.class.cast(value);
			Class<?> paramType = null;

			// use configured types
			if (paramTypes!=null && paramTypes.length>0) {
				paramType = paramTypes[0];

			// try to reflect the types
			} else {
				Class<?>[] reflectedparamTypes
					= ReflectionUtil.getParamsTypeForGenericClass(objectClass, Collection.class);
				if (reflectedparamTypes.length>0) {
					paramType = reflectedparamTypes[0];
				} else {
					throw new IllegalArgumentException(
						"Unable to determine param type for collection: "+objectClass);
				}
			}

			// create the DBList and populate
			BasicDBList dbList = new BasicDBList();
			for (Object val : collection) {
				dbList.add(convertToDBObject(val, paramType, null));
			}
			return dbList;

		// maps
		} else if (Map.class.isInstance(value)
			&& Map.class.isAssignableFrom(objectClass)) {

			// cast to map and get generic type
			Map<String, Object> map = Map.class.cast(value);
			Class<?> keyParamType = null;
			Class<?> valueParamType = null;

			// use configured types
			if (paramTypes!=null && paramTypes.length>1) {
				keyParamType = paramTypes[0];
				valueParamType = paramTypes[1];

			// try to reflect the types
			} else {
				Class<?>[] reflectedparamTypes
					= ReflectionUtil.getParamsTypeForGenericClass(objectClass, Collection.class);
				if (reflectedparamTypes.length>1) {
					keyParamType = reflectedparamTypes[0];
					valueParamType = reflectedparamTypes[1];
				} else {
					throw new IllegalArgumentException(
						"Unable to determine param type for map: "+objectClass);
				}
			}

			// ensure string param type
			if (!String.class.isAssignableFrom(keyParamType)) {
				throw new IllegalArgumentException(
					"Maps may only have Strings for keys");
			}

			// populate map
			DBObject dbObject = new BasicDBObject();
			for (String key : map.keySet()) {
				dbObject.put(key, convertToDBObject(map.get(key), valueParamType, null));
			}
			return map;
		}

		// uh-oh
		throw new IllegalArgumentException(
			"Unable to translate type "+objectClass.getName());
	}

	/**
	 * Converts the given object into the given type.
	 * @param value the value
	 * @param clazz the type to convert to
	 * @param paramTypes the parameter types
	 * @return the converted object
	 * @throws Exception on error
	 */
	protected Object convertFromDBObject(Object value, Class<?> clazz, Class<?>[] paramTypes)
		throws Exception {

		// get the value class
		Class<?> valueClass = (value!=null) ? value.getClass() : null;

		// Null
		if (value==null) {
			return null;

		// mapper can map it itself
		} else if (DBObject.class.isInstance(value)
			&& canConvert(valueClass)) {
			return translateFromDBObject(DBObject.class.cast(value), valueClass);

		// primitives
		} else if (ReflectionUtil.isPrimitive(clazz)) {
			return value;

		// collections
		} else if (BasicDBList.class.isInstance(value)
			&& Collection.class.isAssignableFrom(clazz)) {

			// cast to list and get generic type
			BasicDBList dbList = BasicDBList.class.cast(value);
			Class<?> paramType = null;

			// use configured types
			if (paramTypes!=null && paramTypes.length>0) {
				paramType = paramTypes[0];

			// try to reflect the types
			} else {
				Class<?>[] reflectedparamTypes
					= ReflectionUtil.getParamsTypeForGenericClass(clazz, Collection.class);
				if (reflectedparamTypes.length>0) {
					paramType = reflectedparamTypes[0];
				} else {
					throw new IllegalArgumentException(
						"Unable to determine param type for collection: "+clazz);
				}
			}

			// create the collection
			Collection<Object> collection = null;
			if (SortedSet.class.isAssignableFrom(clazz)) {
				collection = new TreeSet<Object>();
			} else if (Set.class.isAssignableFrom(clazz)) {
				collection = new HashSet<Object>();
			} else if (List.class.isAssignableFrom(clazz)) {
				collection = new ArrayList<Object>();
			} else if (Vector.class.isAssignableFrom(clazz)) {
				collection = new Vector<Object>();
			} else {
				throw new IllegalArgumentException(
					"Unknown collection type: "+clazz);
			}

			// populate collection
			for (int i=0; i<dbList.size(); i++) {
				collection.add(convertFromDBObject(dbList.get(i), paramType, null));
			}
			return collection;

		// maps
		} else if (BasicDBObject.class.isInstance(value)
			&& Map.class.isAssignableFrom(valueClass)) {

			// cast to map and get generic type
			DBObject dbObject = DBObject.class.cast(value);
			Map<String, Object> map = new HashMap<String, Object>();
			Class<?> keyParamType = null;
			Class<?> valueParamType = null;

			// use configured types
			if (paramTypes!=null && paramTypes.length>1) {
				keyParamType = paramTypes[0];
				valueParamType = paramTypes[1];

			// try to reflect the types
			} else {
				Class<?>[] reflectedparamTypes
					= ReflectionUtil.getParamsTypeForGenericClass(clazz, Collection.class);
				if (reflectedparamTypes.length>1) {
					keyParamType = reflectedparamTypes[0];
					valueParamType = reflectedparamTypes[1];
				} else {
					throw new IllegalArgumentException(
						"Unable to determine param type for map: "+clazz);
				}
			}

			// ensure string param type
			if (!String.class.isAssignableFrom(keyParamType)) {
				throw new IllegalArgumentException(
					"Maps may only have Strings for keys");
			}

			// populate map
			for (String key : dbObject.keySet()) {
				map.put(key, convertFromDBObject(dbObject.get(key), valueParamType, null));
			}
			return map;
		}

		// uh-oh
		throw new IllegalArgumentException(
			"Unable to translate type "+valueClass.getName());
	}
}
