package com.googlecode.mjorm.convert.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.googlecode.mjorm.ReflectionUtil;
import com.googlecode.mjorm.convert.ConversionContext;
import com.googlecode.mjorm.convert.ConversionException;
import com.googlecode.mjorm.convert.JavaType;
import com.googlecode.mjorm.convert.TypeConverter;
import com.mongodb.BasicDBList;

public class MongoToCollectionTypeConverter
	implements TypeConverter<BasicDBList, Collection<?>> {

	public static final String HINT_COMPARATOR_CLASS = "comparatorClass";

	public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
		return Collection.class.isAssignableFrom(targetClass)
			&& BasicDBList.class.equals(sourceClass);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<?> convert(
		BasicDBList source, JavaType targetType, ConversionContext context)
		throws ConversionException {

		// get parameter type
		JavaType parameterType = targetType.getJavaTypeParameter(0);

		// bail if we don't have a parameter type
		if (parameterType==null) {
			throw new ConversionException(
				"Unable to determine parameterType of "+targetType);
		}

		// get target class
		Class<?> targetClass = targetType.getTypeClass();

		// create collection
		Collection ret;
		if (!targetClass.isInterface()) {
			try {
				ret = Collection.class.cast(ReflectionUtil.instantiate(targetClass));
			} catch (Exception e) {
				throw new ConversionException("Couldn't instantiate "+targetClass.getName(), e);
			}
		} else if (SortedSet.class.isAssignableFrom(targetClass)) {
			ret = new TreeSet();
		} else if (Set.class.isAssignableFrom(targetClass)) {
			ret = new HashSet();
		} else if (List.class.isAssignableFrom(targetClass)) {
			ret = new ArrayList();
		} else {
			ret = new LinkedList();
		}

		// iterate and convert
		for (int i=0; i<source.size(); i++) {

			// get value
			Object value = source.get(i);

			// convert
			if (value!=null) {
				value = context.convert(value, parameterType);
			}

			// add
			ret.add(value);
		}

		// return it
		return ret;
	}

}
