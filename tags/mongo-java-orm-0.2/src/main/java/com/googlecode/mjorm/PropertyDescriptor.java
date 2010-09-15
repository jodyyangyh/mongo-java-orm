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
	private String propColumn;
	private Class<?> objectClass;
	private Type genericType;
	private Type[] parameterTypes;
	private Method setter;
	private Method getter;
	private boolean isIdentifier;

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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

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
	 * @return the genericType
	 */
	public Type getGenericType() {
		return genericType;
	}

	/**
	 * @param genericType the genericType to set
	 */
	public void setGenericType(Type genericType) {
		this.genericType = genericType;
	}

	/**
	 * @return the parameterTypes
	 */
	public Type[] getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * @param parameterTypes the parameterTypes to set
	 */
	public void setParameterTypes(Type[] parameterTypes) {
		this.parameterTypes = Arrays.copyOf(parameterTypes, parameterTypes.length);
	}

	/**
	 * @return the setter
	 */
	public Method getSetter() {
		return setter;
	}

	/**
	 * @param setter the setter to set
	 */
	public void setSetter(Method setter) {
		this.setter = setter;
	}

	/**
	 * @return the getter
	 */
	public Method getGetter() {
		return getter;
	}

	/**
	 * @param getter the getter to set
	 */
	public void setGetter(Method getter) {
		this.getter = getter;
	}

	/**
	 * @return the isIdentifier
	 */
	public boolean isIdentifier() {
		return isIdentifier;
	}

	/**
	 * @param isIdentifier the isIdentifier to set
	 */
	public void setIdentifier(boolean isIdentifier) {
		this.isIdentifier = isIdentifier;
	}

	/**
	 * @return the propColumn
	 */
	public String getPropColumn() {
		return propColumn;
	}

	/**
	 * @param propColumn the propColumn to set
	 */
	public void setPropColumn(String propColumn) {
		this.propColumn = propColumn;
	}

}
