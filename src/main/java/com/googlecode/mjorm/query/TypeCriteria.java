package com.googlecode.mjorm.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TypeCriteria
	extends AbstractCriteria<DBObject> {

	public enum Type {
		Double(1),
		String(2),
		Object(3),
		Array(4),
		Binary(5),
		ObjectId(7),
		Boolean(8),
		Date(9),
		Null(10),
		RegEx(11),
		JavaScript(13),
		Symbol(14),
		JavaScriptWithScope(15),
		Int32(16),
		Timestamp(17),
		Int64(18),
		MinKey(255),
		MaxKey(127)
		;
		private Number code;
		Type(Number code) {
			this.code = code;
		}
		public Number getCode() {
			return code;
		}
	}
	
	private Number typeCode;

	public TypeCriteria(Number typeCode) {
		this.typeCode	= typeCode;
	}

	public TypeCriteria(Type type) {
		this(type.getCode());
	}

	/**
	 * @return the typeCode
	 */
	public Number getTypeCode() {
		return typeCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DBObject toQueryObject() {
		return new BasicDBObject("$type", typeCode);
	}

}
