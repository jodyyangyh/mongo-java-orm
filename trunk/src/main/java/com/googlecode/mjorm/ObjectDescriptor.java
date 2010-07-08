package com.googlecode.mjorm;

import java.util.HashSet;
import java.util.Set;

/**
 * An object that describes how to translate
 * java objects to and from {@link DBObject}s.
 */
public class ObjectDescriptor {

	private Class<?> objectClass;
	private Set<PropertyDescriptor> properties
		= new HashSet<PropertyDescriptor>();

	/**
	 * @return the objectClass
	 */
	public Class<?> getObjectClass() {
		return objectClass;
	}

	/**
	 * @param objectClass the objectClass to set
	 */
	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}

	/**
	 * Returns the {@link PropertyDescriptor} for the given
	 * {@code propertyName}.
	 * @param propertyName the name
	 * @return the {@link PropertyDescriptor}
	 */
	public PropertyDescriptor getPropertyDescriptor(String propertyName) {
		for (PropertyDescriptor descriptor : properties) {
			if (descriptor.getName().equals(propertyName)) {
				return descriptor;
			}
		}
		return null;
	}

	/**
	 * @return the properties
	 */
	public Set<PropertyDescriptor> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Set<PropertyDescriptor> properties) {
		this.properties = properties;
	}
}
