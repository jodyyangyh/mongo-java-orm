package com.googlecode.mjorm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A descriptor of an object property.
 */
public class PropertyDescriptor {

	private String name;
	private Class<?> objectClass;
	private Type genericType;
	private Type[] parameterTypes;
	private Method setter;
	private Method getter;

	/**
	 * Creates the {@link PropertyDescriptor}.
	 * @param name the property's name
	 * @param genericType the genericType
	 * @param parameterTypes the parameterTypes
	 * @param objectClass the objectClass
	 * @param setter the getter method
	 * @param getter the setter method
	 */
	public PropertyDescriptor(
		String name, Class<?> objectClass,
		Type genericType, Type[] parameterTypes,
		Method setter, Method getter) {
		this.name 			= name;
		this.genericType 	= genericType;
		this.objectClass 	= objectClass;
		this.setter			= setter;
		this.getter			= getter;
		this.parameterTypes = Arrays.copyOf(parameterTypes, parameterTypes.length);
		if (setter==null || getter==null) {
			throw new IllegalArgumentException(
				name+"'s setter or getter is null");
		}
	}

	/**
	 * Sets the value on the given target.
	 * @param target the target
	 * @param value the value
	 * @throws IllegalAccessException on error
	 * @throws InvocationTargetException on error
	 */
	public void set(Object target, Object value)
		throws IllegalAccessException,
		InvocationTargetException {
		setter.setAccessible(true);
		setter.invoke(target, value);
	}

	/**
	 * Gets the value on the given target.
	 * @param target the target
	 * @return the value
	 * @throws IllegalAccessException on error
	 * @throws InvocationTargetException on error
	 */
	public Object get(Object target)
		throws IllegalAccessException,
		InvocationTargetException {
		getter.setAccessible(true);
		return getter.invoke(target);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the objectClass
	 */
	public Class<?> getObjectClass() {
		return objectClass;
	}

	/**
	 * @return the genericType
	 */
	public Type getGenericType() {
		return genericType;
	}

	/**
	 * @return the parameterTypes
	 */
	public Type[] getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * @return the setter
	 */
	public Method getSetter() {
		return setter;
	}

	/**
	 * @return the getter
	 */
	public Method getGetter() {
		return getter;
	}

}
