package com.googlecode.mjorm.query;

public class EqualsCriteria<T>
	extends AbstractCriteria<T> {

	private T value;

	public EqualsCriteria(T value) {
		this.value		= value;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public T toQueryObject() {
		return value;
	}

}
