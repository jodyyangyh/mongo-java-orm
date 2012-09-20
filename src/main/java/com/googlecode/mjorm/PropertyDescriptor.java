package com.googlecode.mjorm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.mjorm.convert.JavaType;


/**
 * A descriptor of an object property.
 */
public class PropertyDescriptor {

	private String name;
	private Map<String, Object> translationHints = new HashMap<String, Object>();
	private String propColumn;
	private JavaType type;
	private JavaType storageType;
	private Method setter;
	private Method getter;
	private boolean isIdentifier;
	private boolean isAutoGenerated;

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
	 * @return the translationHints
	 */
	public Map<String, Object> getTranslationHints() {
		return translationHints;
	}

	/**
	 * @param translationHints the translationHints to set
	 */
	public void setTranslationHints(Map<String, Object> translationHints) {
		this.translationHints = translationHints;
	}

	/**
	 * @return the type
	 */
	public JavaType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(JavaType objectClass) {
		this.type = objectClass;
	}

	/**
	 * @return the storageType
	 */
	public JavaType getStorageType() {
		return storageType;
	}

	/**
	 * @param storageType the storageType to set
	 */
	public void setStorageType(JavaType storageType) {
		this.storageType = storageType;
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
	 * @return the isAutoGenerated
	 */
	public boolean isAutoGenerated() {
		return isAutoGenerated;
	}

	/**
	 * @param isAutoGenerated the isAutoGenerated to set
	 */
	public void setAutoGenerated(boolean isAutoGenerated) {
		this.isAutoGenerated = isAutoGenerated;
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
