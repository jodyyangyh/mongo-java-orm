package com.googlecode.mjorm;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for reflection related things.
 */
public final class ReflectionUtil {

	/**
	 * Does nothing.
	 */
	private ReflectionUtil() { }
	static { new ReflectionUtil(); }

	/**
	 * Casts a {@link Type} to a {@link Class}.
	 * @param t the type
	 * @return the {@link Class}
	 */
	public static Class<?> clazz(Type t) {
		return (Class.class.isInstance(t))
			? Class.class.cast(t) : null;
	}

	/**
	 * Casts a {@link Class} to a {@link ParameterizedType}.
	 * @param clazz the {@link Class}
	 * @return the {@link ParameterizedType}
	 */
	public static ParameterizedType parameterizedType(Class<?> clazz) {
		return (ParameterizedType.class.isInstance(clazz))
			? ParameterizedType.class.cast(clazz) : null;
	}

	/**
	 * Finds the specified getter method on the specified class.
	 * @param clazz the class
	 * @param name the getter name
	 * @param type the type
	 * @return the method
	 */
	public static Method findGetter(Class<?> clazz, String name, Class<?> type) {
		String lCaseName = name.toLowerCase();
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName().toLowerCase();
			Class<?> returnType = method.getReturnType();
			if ((methodName.equals("get"+lCaseName) || methodName.startsWith("is"+lCaseName))
				&& (type==null || (type.isAssignableFrom(returnType)))) {
				return method;
			}
		}
		return null;
	}

	/**
	 * Finds the specified setter method on the specified class.
	 * @param clazz the class
	 * @param name the setter name
	 * @param type the type
	 * @return the method
	 */
	public static Method findSetter(Class<?> clazz, String name, Class<?> type) {
		String lCaseName = name.toLowerCase();
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName().toLowerCase();
			Class<?>[] paramTypes = method.getParameterTypes();
			if (methodName.equals("set"+lCaseName)
				&& (type==null
					|| (paramTypes.length==1 && type.isAssignableFrom(paramTypes[0])))) {
				return method;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link Collection} param type or null
	 * if it's not a {@link Collection} or doesn't have
	 * a param type.
	 * @param clazz the {@link Class}
	 * @param genericClazz the generic {@link Class}
	 * @return the type
	 */
	public static Class<?>[] getParamsTypeForGenericClass(Class<?> clazz, Class<?> genericClazz) {
		for (ParameterizedType pt : getParameterizedTypes(clazz)) {
			Class<?> c = clazz(pt);
			if (c!=null
				&& genericClazz.isAssignableFrom(c)
				&& pt.getActualTypeArguments().length>0) {
				Type[] params = pt.getActualTypeArguments();
				if (params!=null && params.length>0) {
					Class<?>[] ret = new Class<?>[params.length];
					for (int i=0; i<params.length; i++) {
						ret[i] = clazz(params[i]);
					}
					return ret;
				}
			}
		}
		return new Class<?>[0];
	}

	/**
	 * Returns the {@link ParameterizedType}s that the
	 * property type implements and extends.
	 * @param clazz the {@link Class}
	 * @return the {@link ParameterizedType}s
	 */
	public static ParameterizedType[] getParameterizedTypes(Class<?> clazz) {
		List<ParameterizedType> ret = new ArrayList<ParameterizedType>();
		for (Type t : clazz.getGenericInterfaces()) {
			if (t instanceof ParameterizedType) {
				ret.add((ParameterizedType)t);
			}
		}
		if (clazz.getSuperclass()!=null
			&& ParameterizedType.class.isAssignableFrom(clazz.getSuperclass())) {
			Type t = clazz.getSuperclass();
			ret.add((ParameterizedType)t);
		}
		return ret.toArray(new ParameterizedType[0]);
	}

	/**
	 * Returns whether or not the type is a primivite type.
	 * @param clazz the {@link Class}
	 * @return true or false
	 */
	public static boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive()
			|| Number.class.isAssignableFrom(clazz)
			|| String.class.isAssignableFrom(clazz)
			|| Byte.class.isAssignableFrom(clazz)
			|| Boolean.class.isAssignableFrom(clazz)
			|| Character.class.isAssignableFrom(clazz);
	}

	/**
	 * Indicates whether or not the type is generic.
	 * @param clazz the {@link Class}
	 * @return true if the type has generics
	 */
	public static boolean isGeneric(Class<?> clazz) {
		return ReflectionUtil.getParameterizedTypes(clazz).length>0;
	}
}
