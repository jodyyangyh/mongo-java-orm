package com.googlecode.mjorm.convert.converters;

import org.bson.types.ObjectId;

import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConverter;

public class StringToObjectIdTypeConverter
	implements TypeConverter<String, ObjectId> {

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return ObjectId.class.equals(targetClass)
			&& String.class.equals(sourceClass);
	}

	public ObjectId convert(
		String source, JavaType targetType, ConversionContext context)
		throws ConversionException {
		return new ObjectId(source);
	}

}
