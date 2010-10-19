package com.googlecode.mjorm;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.jot.TranslationContext;
import com.googlecode.jot.TypeTranslator;
import com.googlecode.jot.translators.PrimitiveTypeTranslator;
import com.googlecode.mjorm.jot.ArrayTypeTranslator;
import com.googlecode.mjorm.jot.CollectionTypeTranslator;
import com.googlecode.mjorm.jot.MapTypeTranslator;
import com.mongodb.DBObject;

/**
 * Abstract {@link ObjectMapper} that handles most of the work of
 * object conversion for the subclass.
 */
public abstract class AbstractObjectMapper
	implements ObjectMapper {

	private TranslationContext translationCtx = new TranslationContext();
	private Set<TypeTranslator<?, ?>> typeTranslators
		= new HashSet<TypeTranslator<?, ?>>();

	/**
	 * Creates the mapper.
	 */
	public AbstractObjectMapper() {
		translationCtx.registerTranslator(new PrimitiveTypeTranslator());
		translationCtx.registerTranslator(new CollectionTypeTranslator());
		translationCtx.registerTranslator(new MapTypeTranslator());
		translationCtx.registerTranslator(new ArrayTypeTranslator());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T mapFromDBObject(DBObject dbObject, Class<T> objectClass) {
		return (T)translationCtx.translateToLocal(dbObject, objectClass);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> DBObject mapToDBObject(T object) {
		return translationCtx.translateFromLocal(object);
	}

	/**
	 * Registers the given {@link TypeTranslator}.
	 * @param typeTranslator the {@link TypeTranslator}
	 */
	public void registerConverter(TypeTranslator<?, ?> typeTranslator) {
		if (!typeTranslators.contains(typeTranslator)) {
			typeTranslators.add(typeTranslator);
			this.translationCtx.registerTranslator(typeTranslator);
		}
	}

	/**
	 * Unregisters the given {@link TypeTranslator}.
	 * @param typeTranslator the {@link TypeTranslator}
	 */
	public void unregisterConverter(TypeTranslator<?, ?> typeTranslator) {
		typeTranslators.remove(typeTranslator);
		this.translationCtx.unregisterTranslator(typeTranslator);
	}

	/**
	 * Unregisters all {@link TypeTranslator}s.
	 */
	public void unregisterAllConverters() {
		for (TypeTranslator<?, ?> c : typeTranslators) {
			this.translationCtx.unregisterTranslator(c);
		}
		typeTranslators.clear();
	}

}
