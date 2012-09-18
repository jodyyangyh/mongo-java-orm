package com.googlecode.mjorm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a translation hint for use by the
 * {@link ObjectMapper} when translating the
 * property that this is annotated with.
 *
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslationHint {

	/**
	 * The hint's name.
	 * @return
	 */
	String name();

	/**
	 * The hints value.
	 * @return
	 */
	String stringValue();

}
