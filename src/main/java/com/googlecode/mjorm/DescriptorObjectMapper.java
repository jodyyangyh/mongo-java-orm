package com.googlecode.mjorm;

import com.googlecode.mjorm.ocf4j.DescriptorTranslator;

/**
 * Abstract class that uses {@link ObjectDescriptor}s and
 * {@link PropertyDescriptor}s to map objects to and from
 * mongo's {@link DBObject}s.
 */
public class DescriptorObjectMapper
	extends AbstractObjectMapper {

	/**
	 * Registers a new {@link ObjectDescriptor}.
	 * @param descriptor the {@link ObjectDescriptor]
	 */
	protected void registerObjectDescriptor(ObjectDescriptor descriptor) {
		super.registerConverter(new DescriptorTranslator(descriptor));
	}

}
