package com.googlecode.mjorm.convert;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeConversionHints {

	public static final TypeConversionHints NO_HINTS
		= new TypeConversionHints() {
		@Override
		public Type[] getTypeParameters() {
			return new Type[0];
		}
		@Override
		public void setTypeParameters(Type[] typeParameters) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void setOther(String key, Object value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Object getOther(String key) {
			return null;
		}
	};

	private Type[] typeParameters;
	private Map<String, Object> other = new HashMap<String, Object>();

	/**
	 * @return the typeParameters
	 */
	public Type[] getTypeParameters() {
		return typeParameters;
	}

	/**
	 * @param typeParameters the typeParameters to set
	 */
	public void setTypeParameters(Type[] typeParameters) {
		this.typeParameters = typeParameters;
	}

	/**
	 * Sets other hints.
	 * @param key hint key
	 * @param value hint value
	 */
	public void setOther(String key, Object value) {
		other.put(key, value);
	}

	/**
	 * Gets an other hint.
	 * @param key the key
	 * @return the hint value
	 */
	public Object getOther(String key) {
		return other.get(key);
	}

}
