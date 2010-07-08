package com.googlecode.mjorm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A descriptor of an object property.
 */
public class PropertyDescriptor {

	private String name;
	private Class<?> type;
	private Class<?>[] parameterTypes;
	private Method setter;
	private Method getter;

	/**
	 * Creates the {@link PropertyDescriptor}.
	 * @param name the property's name
	 * @param type the type
	 * @param parameterTypes the parameterTypes
	 * @param setter the getter method
	 * @param getter the setter method
	 */
	public PropertyDescriptor(
		String name, Class<?> type, Class<?>[] parameterTypes, Method setter, Method getter) {
		this.name 	= name;
		this.type 	= type;
		this.setter	= setter;
		this.getter	= getter;
		this.parameterTypes = Arrays.copyOf(parameterTypes, parameterTypes.length);
		if (setter==null || getter==null) {
			throw new IllegalArgumentException(
				name+"'s setter or getter is null");
		}
		if (setter.getParameterTypes().length!=1
			|| !type.isAssignableFrom(setter.getParameterTypes()[0])) {
			throw new IllegalArgumentException(
				name+"'s setter doesn't have the correct arguments");
		} else if (getter.getParameterTypes().length!=0
			|| getter.getReturnType()==null
			|| !type.isAssignableFrom(getter.getReturnType())) {
			throw new IllegalArgumentException(
				name+"'s getter doesn't have the correct return type and/or arguments");
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
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
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

	/**
	 * @return the parameterTypes
	 */
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

}
