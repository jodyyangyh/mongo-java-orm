package com.googlecode.mjorm.convert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JavaType {

	private Type type;
	private ParameterizedType parameterizedType;
	private Class<?> clazz;
	private Type[] typeParameters;
	private boolean instantiable;
	private boolean genericInfo;

	public static JavaType fromType(Type type) {
		if (type==null) {
			return null;
		}
		return new JavaType(type);
	}

	private JavaType(Type type) {

		// the type
		this.type = type;

		// parameterized?
		parameterizedType = ParameterizedType.class.isInstance(type)
			? ParameterizedType.class.cast(type) : null;

		// generic info
		genericInfo = parameterizedType!=null;

		// get class
		if (Class.class.isInstance(type)) {
			clazz = Class.class.cast(type);
		} else if (parameterizedType!=null) {
			clazz = Class.class.cast(parameterizedType.getRawType());
		} else {
			clazz = null;
		}

		// instantiable
		instantiable = clazz != null;

		// typeParameters
		typeParameters = parameterizedType!=null
			? parameterizedType.getActualTypeArguments()
			: null;
	
		// genericInfo
		genericInfo = typeParameters != null;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the parameterizedType
	 */
	public ParameterizedType getParameterizedType() {
		return parameterizedType;
	}

	/**
	 * @return the clazz
	 */
	public Class<?> getTypeClass() {
		return clazz;
	}

	/**
	 * @return the typeParameters
	 */
	public Type[] getTypeParameters() {
		return typeParameters;
	}

	/**
	 * @return the typeParameters
	 */
	public Type getTypeParameter(int index) {
		return typeParameters!=null && typeParameters.length>index
			? typeParameters[index] : null;
	}

	/**
	 * @return the typeParameters
	 */
	public JavaType getJavaTypeParameter(int index) {
		Type type = getTypeParameter(index);
		return (type==null) ? null : JavaType.fromType(type);
	}

	/**
	 * @return the instantiable
	 */
	public boolean isInstantiable() {
		return instantiable;
	}

	/**
	 * @return the genericInfo
	 */
	public boolean isGenericInfo() {
		return genericInfo;
	}

	@Override
	public String toString() {
		return type.toString();
	}

}
