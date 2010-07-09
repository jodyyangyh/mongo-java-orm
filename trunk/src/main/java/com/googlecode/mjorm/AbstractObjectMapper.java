package com.googlecode.mjorm;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
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

	private Map<Class<?>, TypeConverter> typeConverterCache
		= new HashMap<Class<?>, TypeConverter>();
	private List<TypeConverter> typeConverters
		= new ArrayList<TypeConverter>();

	/**
	 * Checks to see if the given {@link Class} can
	 * be converted with a registered {@link TypeConverter}.
	 * @param clazz the class
	 * @return true if it can
	 */
	private boolean canTypeConvert(Class<?> clazz) {
		if (!typeConverterCache.containsKey(clazz)) {
			for (TypeConverter tc : typeConverters) {
				if (tc.canConvert(clazz)) {
					typeConverterCache.put(clazz, tc);
					return true;
				}
			}
			typeConverterCache.put(clazz, null);
		}
		return typeConverterCache.get(clazz)!=null;
	}

	/**
	 * Registers a {@link TypeConverter}.
	 * @param typeConverter the {@link TypeConverter}
	 */
	public void registerTypeConverter(TypeConverter typeConverter) {
		typeConverters.add(typeConverter);
		typeConverterCache.clear();
	}

	/**
	 * Converts a java object to a DBObject.
	 * @param value the value to convert
	 * @param clazz the class
	 * @param genericType the generic type
	 * @param genericParamTypes the generic parameter types
	 * @return the converted object
	 * @throws Exception on error
	 */
	@SuppressWarnings("unchecked")
	protected Object convertToDBObject(
		Object value, Class<?> clazz, Type genericType, Type[] genericParamTypes)
		throws Exception {

		// Null
		if (value==null) {
			return null;

		// mapper can map it itself
		} else if (canConvert(clazz)) {
			return translateToDBObject(value, Class.class.cast(clazz));

		// check for a type converter
		} else if (canTypeConvert(clazz)) {
			return typeConverterCache.get(clazz).convertToMongo(value, clazz);

		// handle arrays
		} else if (clazz.isArray()) {
			Class<?> nonArrayClazz = clazz.getComponentType();
			Object[] values = (Object[])value;
			BasicDBList retValues = new BasicDBList();
			for (int i=0; i<values.length; i++) {
				retValues.add(convertToDBObject(
					values[i], nonArrayClazz, genericType, genericParamTypes));
			}
			return retValues;

		// primitives
		} else if (ReflectionUtil.isPrimitive(clazz)) {
			return value;

		// collections
		} else if (Collection.class.isInstance(value)
			&& Collection.class.isAssignableFrom(clazz)) {

			// cast to collection
			Collection<Object> collection = Collection.class.cast(value);
			Type paramType = null;

			// use configured types
			if (genericType!=null && genericParamTypes.length>0) {
				paramType = genericParamTypes[0];

			// try to reflect the types
			} else if (genericType!=null) {
				Type[] reflectedparamTypes = ReflectionUtil.getTypeParameters(genericType);
				if (reflectedparamTypes.length>0) {
					paramType = reflectedparamTypes[0];
				} else {
					throw new IllegalArgumentException(
						"Unable to determine param type for collection: "+clazz);
				}
			}

			// create the DBList and populate
			BasicDBList dbList = new BasicDBList();
			for (Object val : collection) {
				dbList.add(convertToDBObject(
					val, ReflectionUtil.clazz(paramType), paramType, new Type[0]));
			}
			return dbList;

		// maps
		} else if (Map.class.isInstance(value)
			&& Map.class.isAssignableFrom(clazz)) {

			// cast to map and get generic type
			Map<String, Object> map = Map.class.cast(value);
			Type keyParamType = null;
			Type valueParamType = null;

			// use configured types
			if (genericType!=null && genericParamTypes.length>1) {
				keyParamType = genericParamTypes[0];
				valueParamType = genericParamTypes[1];

			// try to reflect the types
			} else if (genericType!=null) {
				Type[] reflectedparamTypes = ReflectionUtil.getTypeParameters(genericType);
				if (reflectedparamTypes.length>1) {
					keyParamType = reflectedparamTypes[0];
					valueParamType = reflectedparamTypes[1];
				} else {
					throw new IllegalArgumentException(
						"Unable to determine param type for map: "+clazz);
				}
			}

			// ensure string param type
			if (!ReflectionUtil.clazz(keyParamType).equals(String.class)) {
				throw new IllegalArgumentException(
					"Maps may only have Strings for keys");
			}

			// populate dbObject
			DBObject dbObject = new BasicDBObject();
			for (String key : map.keySet()) {
				dbObject.put(key, convertToDBObject(
					map.get(key), ReflectionUtil.clazz(valueParamType), valueParamType, new Type[0]));
			}
			return dbObject;
		}

		// uh-oh
		throw new IllegalArgumentException(
			"Unable to translate type "+clazz.getName());
	}

	/**
	 * Converts the given object of the specified type to
	 * a DBObject.
	 * @param value the value
	 * @param clazz the type
	 * @param genericType the generic type if any
	 * @param genericParamTypes the generic parameter types
	 * @return the DBObject
	 * @throws Exception on error
	 */
	protected Object convertFromDBObject(
		Object value, Class<?> clazz, Type genericType, Type[] genericParamTypes)
		throws Exception {

		// Null
		if (value==null) {
			return null;

		// mapper can map it itself
		} else if (DBObject.class.isInstance(value)
			&& canConvert(clazz)) {
			return translateFromDBObject(DBObject.class.cast(value), clazz);

		// check for a type converter
		} else if (canTypeConvert(clazz)) {
			return typeConverterCache.get(clazz).convertFromMongo(value, clazz);

		// handle arrays
		} else if (clazz.isArray() && BasicDBList.class.isInstance(value)) {
			Class<?> nonArrayClazz = clazz.getComponentType();
			BasicDBList values = BasicDBList.class.cast(value);
			Object[] retValues = (Object[])Array.newInstance(nonArrayClazz, values.size());
			for (int i=0; i<retValues.length; i++) {
				retValues[i] = convertFromDBObject(
					values.get(i), nonArrayClazz, genericType, genericParamTypes);
			}
			return retValues;

		// primitives
		} else if (ReflectionUtil.isPrimitive(clazz)) {
			return value;

		// collections
		} else if (BasicDBList.class.isInstance(value)
			&& Collection.class.isAssignableFrom(clazz)) {

			// cast to list and get generic type
			BasicDBList dbList = BasicDBList.class.cast(value);
			Type paramType = null;

			// use configured types
			if (genericType!=null && genericParamTypes.length>0) {
				paramType = genericParamTypes[0];

			// try to reflect the types
			} else if (genericType!=null) {
				Type[] reflectedparamTypes = ReflectionUtil.getTypeParameters(genericType);
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
				collection.add(convertFromDBObject(
					dbList.get(i), ReflectionUtil.clazz(paramType), paramType, new Type[0]));
			}
			return collection;

		// maps
		} else if ((BasicDBObject.class.isInstance(value) || Map.class.isInstance(value))
			&& Map.class.isAssignableFrom(clazz)) {

			// cast to map and get generic type
			DBObject dbObject = BasicDBObject.class.isInstance(value)
				? DBObject.class.cast(value) : new BasicDBObject(Map.class.cast(value));
			Map<String, Object> map = new HashMap<String, Object>();
			Type keyParamType = null;
			Type valueParamType = null;

			// use configured types
			if (genericType!=null && genericParamTypes.length>1) {
				keyParamType = genericParamTypes[0];
				valueParamType = genericParamTypes[1];

			// try to reflect the types
			} else if (genericType!=null) {
				Type[] reflectedparamTypes = ReflectionUtil.getTypeParameters(genericType);
				if (reflectedparamTypes.length>1) {
					keyParamType = reflectedparamTypes[0];
					valueParamType = reflectedparamTypes[1];
				} else {
					throw new IllegalArgumentException(
						"Unable to determine param type for map: "+clazz);
				}
			}

			// ensure string param type
			if (!ReflectionUtil.clazz(keyParamType).equals(String.class)) {
				throw new IllegalArgumentException(
					"Maps may only have Strings for keys");
			}

			// populate map
			for (String key : dbObject.keySet()) {
				map.put(key, convertFromDBObject(
					dbObject.get(key), ReflectionUtil.clazz(valueParamType), valueParamType, new Type[0]));
			}
			return map;
		}

		// uh-oh
		throw new IllegalArgumentException(
			"Unable to translate type "+clazz);
	}
}
