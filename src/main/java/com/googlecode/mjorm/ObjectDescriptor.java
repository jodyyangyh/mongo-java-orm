package com.googlecode.mjorm;

import java.util.HashMap;
import java.util.Map;

/**
 * An object that describes how to translate
 * java objects to and from {@link DBObject}s.
 */
public class ObjectDescriptor {

	private Class<?> type;
	private Map<String, PropertyDescriptor> properties
		= new HashMap<String, PropertyDescriptor>();

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> objectClass) {
		this.type = objectClass;
	}

	/**
	 * Returns the {@link PropertyDescriptor} for the given
	 * {@code propertyName}.
	 * @param propertyName the name
	 * @return the {@link PropertyDescriptor}
	 */
	public PropertyDescriptor getPropertyDescriptor(String propertyName) {
		return properties.get(propertyName.toLowerCase());
	}

	/**
	 * @return the properties
	 */
	public PropertyDescriptor[] getProperties() {
		return properties.values().toArray(new PropertyDescriptor[0]);
	}

	/**
	 * Adds a {@link PropertyDescriptor}.
	 * @param desc the {@link PropertyDescriptor}
	 */
	public void addPropertyDescriptor(PropertyDescriptor desc) {
		if (properties.containsKey(desc.getName().toLowerCase())) {
			throw new IllegalArgumentException(
				"PropertyDescriptor for "+desc.getName()+" exists");
		}
		properties.put(desc.getName().toLowerCase(), desc);
	}

}
