package com.googlecode.mjorm;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Abstract class that uses {@link ObjectDescriptor}s and
 * {@link PropertyDescriptor}s to map objects to and from
 * mongo's {@link DBObject}s.
 */
public class DescriptorObjectMapper
	extends AbstractObjectMapper {

	private Map<Class<?>, ObjectDescriptor> objectDescriptors
		= new HashMap<Class<?>, ObjectDescriptor>();

	/**
	 * Registers a new {@link ObjectDescriptor}.
	 * @param descriptor the {@link ObjectDescriptor]
	 */
	protected void registerObjectDescriptor(ObjectDescriptor descriptor) {
		objectDescriptors.put(descriptor.getObjectClass(), descriptor);
	}

	/**
	 * Checks to see if the mapper can map the given class.
	 * @param objectClass the class
	 * @return true or false
	 */
	protected boolean canConvert(Class<?> objectClass) {
		return objectDescriptors.containsKey(objectClass);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T translateFromDBObject(DBObject dbObject, Class<T> objectClass) {
		if (!canConvert(objectClass)) {
			throw new IllegalArgumentException(
				"No ObjectDescriptor found for: "+objectClass.getName());
		}

		// bail on nulls
		if (dbObject==null) {
			return null;
		}

		// get descriptor
		ObjectDescriptor objDesc = objectDescriptors.get(objectClass);

		// create the object type
		Object ret;
		try {
			ret = objDesc.getObjectClass().newInstance();
		} catch (Exception e) {
			throw new MappingException(e);
		}

		// loop through each property
		for (PropertyDescriptor propDesc : objDesc.getProperties()) {

			// get value
			Object dbValue = propDesc.isIdentifier()
				? dbObject.get("_id") : dbObject.get(propDesc.getPropColumn());

			// skip null identifiers
			if (propDesc.isIdentifier() && dbValue==null) {
				continue;
			}

			// convert and add
			Object convertedValue;
			try {
				convertedValue = convertFromDBObject(
					dbValue,
					propDesc.getObjectClass(),
					propDesc.getGenericType(),
					propDesc.getParameterTypes());
				propDesc.set(ret, convertedValue);
			} catch (Exception e) {
				throw new MappingException(e);
			}
		}

		// return the object
		return objectClass.cast(ret);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> DBObject translateToDBObject(T object, Class<?> objectClass) {
		if (!canConvert(objectClass)) {
			throw new IllegalArgumentException(
				"No ObjectDescriptor found for: "+objectClass.getName());
		}

		// bail on nulls
		if (object==null) {
			return null;
		}

		// get descriptor
		ObjectDescriptor objDesc = objectDescriptors.get(objectClass);

		// create the object type
		BasicDBObjectBuilder ret = BasicDBObjectBuilder.start();

		// loop through each property
		for (PropertyDescriptor propDesc : objDesc.getProperties()) {

			// get value
			Object propValue;
			try {
				propValue = propDesc.get(object);
			} catch (Exception e) {
				throw new MappingException(e);
			}

			// skip null identifiers
			if (propDesc.isIdentifier() && propValue==null) {
				continue;
			}

			// convert and add
			Object dbValue;
			try {
				dbValue = convertToDBObject(
					propValue,
					propDesc.getObjectClass(),
					propDesc.getGenericType(),
					propDesc.getParameterTypes());
			} catch (Exception e) {
				throw new MappingException(e);
			}
			ret.add(propDesc.isIdentifier()
				? "_id" : propDesc.getPropColumn(), dbValue);
		}

		// return it
		return ret.get();
	}

}
