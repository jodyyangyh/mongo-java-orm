package com.googlecode.mjorm.annotations;

import com.googlecode.mjorm.DescriptorObjectMapper;

/**
 * Implementation of the {@ObjectMapper} that uses
 * annotations to define mappings.
 */
public class AnnotationsDescriptorObjectMapper
	extends DescriptorObjectMapper {

	private AnnotationsObjectDescriptorParser parser
		= new AnnotationsObjectDescriptorParser();

	/**
	 * Adds an annotated class to the {@link ObjectMapper}
	 * @param clazz the class to add
	 */
	public void addClass(Class<?> clazz) {
		registerObjectDescriptor(parser.parseClass(clazz));
	}

}
