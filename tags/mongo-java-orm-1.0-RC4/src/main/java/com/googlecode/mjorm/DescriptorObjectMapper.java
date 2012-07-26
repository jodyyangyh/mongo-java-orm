package com.googlecode.mjorm;

import com.googlecode.mjorm.jot.DescriptorTranslator;

/**
 * Abstract class that uses {@link ObjectDescriptor}s and
 * {@link PropertyDescriptor}s to map objects to and from
 * mongo's {@link DBObject}s.
 */
public class DescriptorObjectMapper
	extends AbstractObjectMapper {

	private ObjectDescriptorRegistry registry;

	public DescriptorObjectMapper() {
		registry = new ObjectDescriptorRegistry();
		super.registerConverter(new DescriptorTranslator(registry));
	}

	/**
	 * Registers a new {@link ObjectDescriptor}.
	 * @param descriptor the {@link ObjectDescriptor]
	 */
	protected void registerObjectDescriptor(ObjectDescriptor descriptor) {
		registry.registerDescriptor(descriptor);
	}

}
