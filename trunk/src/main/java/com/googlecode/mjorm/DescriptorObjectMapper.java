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
	public <T> T translateFromDBObject(DBObject dbObject, Class<T> objectClass)
		throws Exception {
		if (!canConvert(objectClass)) {
			throw new IllegalArgumentException(
				"No ObjectDescriptor found for: "+objectClass.getName());
		}

		// get descriptor
		ObjectDescriptor objDesc = objectDescriptors.get(objectClass);

		// create the object type
		Object ret = objDesc.getObjectClass().newInstance();

		// loop through each property
		for (PropertyDescriptor propDesc : objDesc.getProperties()) {
			Object dbValue = dbObject.get(propDesc.getName());
			Object convertedValue = convertFromDBObject(
				dbValue,
				propDesc.getType(),
				propDesc.getParameterTypes());
			propDesc.set(ret, convertedValue);
		}

		// return the object
		return objectClass.cast(ret);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> DBObject translateToDBObject(T object, Class<T> objectClass)
		throws Exception {
		if (!canConvert(objectClass)) {
			throw new IllegalArgumentException(
				"No ObjectDescriptor found for: "+objectClass.getName());
		}

		// get descriptor
		ObjectDescriptor objDesc = objectDescriptors.get(objectClass);

		// create the object type
		BasicDBObjectBuilder ret = BasicDBObjectBuilder.start();

		// loop through each property
		for (PropertyDescriptor propDesc : objDesc.getProperties()) {
			Object propValue = propDesc.get(object);
			Object dbValue = convertToDBObject(
				propValue, propDesc.getType(), propDesc.getParameterTypes());
			ret.add(propDesc.getName(), dbValue);
		}

		// return it
		return ret.get();
	}

}
