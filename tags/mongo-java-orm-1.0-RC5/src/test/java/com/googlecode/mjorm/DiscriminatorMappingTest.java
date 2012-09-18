package com.googlecode.mjorm;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class DiscriminatorMappingTest {

	private AnnotationsDescriptorObjectMapper annotationsMapper;
	private XmlDescriptorObjectMapper xmlMapper;

	@Before
	public void setUp()
		throws Exception {
		annotationsMapper = new AnnotationsDescriptorObjectMapper();
		annotationsMapper.addClass(DiscriminatorTestObject.class);
		xmlMapper = new XmlDescriptorObjectMapper();
		xmlMapper.addXmlObjectDescriptor(res("/com/googlecode/mjorm/DiscriminatorTestObject.mongo.xml"));
	}

	@After
	public void tearDown()
		throws Exception {
	}

	private InputStream res(String path) {
		return getClass().getResourceAsStream(path);
	}

	@Test
	public void testAnnotations() {

		// create test objects
		DBObject one = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "1")
			.add("disc", "subClassOne")
			.add("one", "it is one")
			.get();

		DBObject two = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "2")
			.add("disc", "subClassTwo")
			.add("two", "it is two")
			.get();

		DBObject three = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "3")
			.get();

		DiscriminatorTestObject obj = annotationsMapper.mapFromDBObject(one, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassOne.class, obj.getClass());
		assertEquals("1", obj.getName());
		assertEquals("it is one", TestObjectSubClassOne.class.cast(obj).getOne());

		obj = annotationsMapper.mapFromDBObject(two, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassTwo.class, obj.getClass());
		assertEquals("2", obj.getName());
		assertEquals("it is two", TestObjectSubClassTwo.class.cast(obj).getTwo());

		obj = annotationsMapper.mapFromDBObject(three, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(DiscriminatorTestObject.class, obj.getClass());
		assertEquals("3", obj.getName());
	}

	@Test
	public void testXML() {

		// create test objects
		DBObject one = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "1")
			.add("disc", "subClassOne")
			.add("one", "it is one")
			.get();

		DBObject two = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "2")
			.add("disc", "subClassTwo")
			.add("two", "it is two")
			.get();

		DBObject three = BasicDBObjectBuilder.start()
			.add("_id", new ObjectId())
			.add("name", "3")
			.get();

		DiscriminatorTestObject obj = xmlMapper.mapFromDBObject(one, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassOne.class, obj.getClass());
		assertEquals("1", obj.getName());
		assertEquals("it is one", TestObjectSubClassOne.class.cast(obj).getOne());

		obj = xmlMapper.mapFromDBObject(two, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(TestObjectSubClassTwo.class, obj.getClass());
		assertEquals("2", obj.getName());
		assertEquals("it is two", TestObjectSubClassTwo.class.cast(obj).getTwo());

		obj = xmlMapper.mapFromDBObject(three, DiscriminatorTestObject.class);
		assertNotNull(obj);
		assertEquals(DiscriminatorTestObject.class, obj.getClass());
		assertEquals("3", obj.getName());
	}

}
