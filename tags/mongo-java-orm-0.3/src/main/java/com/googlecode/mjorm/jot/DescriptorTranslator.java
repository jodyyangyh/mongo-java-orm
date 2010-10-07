package com.googlecode.mjorm.jot;

import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.googlecode.mjorm.MappingException;
import com.googlecode.mjorm.ObjectDescriptor;
import com.googlecode.mjorm.PropertyDescriptor;
import com.googlecode.jot.AbstractTypeTranslator;
import com.googlecode.jot.TranslationContext;
import com.googlecode.jot.ReflectionUtil;
import com.googlecode.jot.TranslationHints;
import com.mongodb.BasicDBObject;

/***
 * Converts {@link BasicDBObject} to pojos.
 *
 */
public class DescriptorTranslator
	extends AbstractTypeTranslator<BasicDBObject, Object> {

	private ObjectDescriptor descriptor;

	/**
	 * Creates the {@code PojoToDbObjectConverter}.
	 * @param descriptor the {@link ObjectDescriptor}
	 */
	public DescriptorTranslator(ObjectDescriptor descriptor) {
		this.descriptor = descriptor;
		super.addSupportedLocalClass(descriptor.getObjectClass());
	}

	/**
	 * {@inheritDoc}
	 */
	public BasicDBObject translateFromLocal(Object pojo, TranslationContext converter, TranslationHints hints) {

		// create the return object
		BasicDBObject ret = new BasicDBObject();

		// loop through each property
		for (PropertyDescriptor prop : descriptor.getProperties()) {

			try {
				// get it
				Object value = prop.get(pojo);

				// handle ids
				if (prop.isIdentifier()) {
					if (value==null) {
						continue;
					}
					ret.put("_id", new ObjectId(String.class.cast(value)));

				} else {
					Object convertedValue = converter.translateFromLocal(value);
					ret.put(prop.getPropColumn(), convertedValue);
				}
			} catch (Exception e) {
				throw new MappingException(
					"Error mapping property "+prop.getName()
					+" of class "+descriptor.getObjectClass(), e);
			}

		}

		// return it
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object translateToLocal(
		BasicDBObject object, TranslationContext converter, Type desiredType, TranslationHints hints) {

		// create the return object
		Object ret;
		try {
			ret = ReflectionUtil.instantiate(descriptor.getObjectClass());
		} catch (Exception e) {
			throw new MappingException(
				"Error creating class: "+desiredType, e);
		}

		// loop through each property
		for (PropertyDescriptor prop : descriptor.getProperties()) {

			try {
				if (prop.isIdentifier()) {
					Object value = object.get("_id");
					if (value==null) {
						continue;
					}
					prop.set(ret, ObjectId.class.cast(value).toStringMongod());

				} else {
					Object value = object.get(prop.getPropColumn());
					TranslationHints translationHints = new TranslationHints();
					Type[] paramTypes = prop.getParameterTypes().length>0
						? prop.getParameterTypes()
						: ReflectionUtil.getTypeParameters(prop.getGenericType());
					translationHints.setTypeParameters(paramTypes);
					Object convertedValue = converter.translateToLocal(
						value, prop.getObjectClass(), translationHints);

					prop.set(ret, convertedValue);
				}

			} catch (Exception e) {
				throw new MappingException(
					"Error mapping property "+prop.getName()
					+" of class "+descriptor.getObjectClass(), e);
			}

		}

		// return it
		return ret;
	}

}
