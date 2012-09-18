package com.googlecode.mjorm.jot;

import java.lang.reflect.Type;

import com.googlecode.jot.ReflectionUtil;
import com.googlecode.jot.TranslationContext;
import com.googlecode.jot.TranslationHints;
import com.googlecode.jot.TypeTranslator;

/**
 * {@link TypeTranslator} for enums.
 *
 */
public class EnumTypeTranslator
	implements TypeTranslator<String, Enum<?>> {

	/**
	 * {@inheritDoc}
	 */
	public String translateFromLocal(Enum<?> local, TranslationContext ctx, TranslationHints hints) {
		return local.name();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enum<?> translateToLocal(String object, TranslationContext ctx, Type desiredType, TranslationHints hints) {
		Class<?> clazz = ReflectionUtil.clazz(desiredType);
		return Enum.valueOf((Class<Enum>)clazz, String.class.cast(object));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsLocal(Type type) {
		Class<?> clazz = ReflectionUtil.clazz(type);
		return clazz!=null && Enum.class.isAssignableFrom(clazz);
	}

}
