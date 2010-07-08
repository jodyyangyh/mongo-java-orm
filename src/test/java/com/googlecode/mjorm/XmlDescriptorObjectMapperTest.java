package com.googlecode.mjorm;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class XmlDescriptorObjectMapperTest {

	private XmlDescriptorObjectMapper mapper;

	@Before
	public void setUp()
		throws Exception {
		mapper = new XmlDescriptorObjectMapper();
	}

	@After
	public void tearDown()
		throws Exception {
		mapper = null;
	}

	private InputStream res(String path) {
		return getClass().getResourceAsStream(path);
	}

	private void addMapping(String path) 
		throws Exception {
		mapper.addXmlObjectDescriptor(res(path));
	}

	@Test
	public void testTranslateToAndFromDBObject()
		throws Exception {
		addMapping("/com/googlecode/mjorm/Address.mongo.xml");
		addMapping("/com/googlecode/mjorm/City.mongo.xml");
		addMapping("/com/googlecode/mjorm/Person.mongo.xml");
		addMapping("/com/googlecode/mjorm/State.mongo.xml");
		addMapping("/com/googlecode/mjorm/SuperDuperOverride.mongo.xml");

		City city = new City();
		city.setName("city name");
		city.setLat(new BigDecimal("123.456"));
		city.setLon(new BigDecimal("789.101"));

		Address address = new Address();
		address.setCity(city);
		address.setStreetName("street name");
		address.setStreetNumber(2435L);
		
		State state = new State();
		state.setName("state name");
		state.setCities(new HashSet<City>());
		state.getCities().add(city);

		DBObject cityDbObject = mapper.translateToDBObject(city, City.class);
		assertNotNull(cityDbObject);
		assertEquals(city.getName(), cityDbObject.get("name"));
		assertEquals(city.getLat(), cityDbObject.get("lat"));
		assertEquals(city.getLon(), cityDbObject.get("lon"));

		DBObject addressDbObject = mapper.translateToDBObject(address, Address.class);
		assertNotNull(addressDbObject);
		DBObject addressCityDbObject = (DBObject)addressDbObject.get("city");
		assertNotNull(addressCityDbObject);
		assertEquals(address.getStreetName(), addressDbObject.get("streetName"));
		assertEquals(address.getStreetNumber(), addressDbObject.get("streetNumber"));
		assertEquals(city.getName(), addressCityDbObject.get("name"));
		assertEquals(city.getLat(), addressCityDbObject.get("lat"));
		assertEquals(city.getLon(), addressCityDbObject.get("lon"));

		DBObject stateDbObject = mapper.translateToDBObject(state, State.class);
		assertNotNull(stateDbObject);
		BasicDBList stateCityDbList = (BasicDBList)stateDbObject.get("cities");
		assertNotNull(stateCityDbList);
		DBObject stateCityDbListObject = (DBObject)stateCityDbList.get(0);
		assertNotNull(stateCityDbList);
		assertEquals(state.getName(), stateDbObject.get("name"));
		assertEquals(city.getName(), stateCityDbListObject.get("name"));
		assertEquals(city.getLat(), stateCityDbListObject.get("lat"));
		assertEquals(city.getLon(), stateCityDbListObject.get("lon"));

		City transformedCity = mapper.translateFromDBObject(cityDbObject, City.class);
		assertNotNull(transformedCity);
		assertEquals(city, transformedCity);

		Address transformedAddress = mapper.translateFromDBObject(addressDbObject, Address.class);
		assertNotNull(transformedAddress);
		assertEquals(address, transformedAddress);

		State transformedState = mapper.translateFromDBObject(stateDbObject, State.class);
		assertNotNull(transformedState);
		assertEquals(state, transformedState);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testTranslateToAndFromDBObject_SuperDuper()
		throws Exception {
		addMapping("/com/googlecode/mjorm/Address.mongo.xml");
		addMapping("/com/googlecode/mjorm/Person.mongo.xml");
		addMapping("/com/googlecode/mjorm/SuperDuperOverride.mongo.xml");
		
		SuperDuper superDuper = new SuperDuper();
		superDuper.setPersonList(new ArrayList<Person>());
		superDuper.setPersonMap(new HashMap<String, Person>());
		superDuper.setPersonSet(new HashSet<Person>());
		superDuper.setPersonSortedSet(new TreeSet<Person>());
		superDuper.setStringMap(new HashMap<String, String>());

		DBObject superDuperDbObject = mapper.translateToDBObject(superDuper, SuperDuper.class);
		assertNotNull(superDuperDbObject);
		assertTrue(superDuperDbObject.get("personList") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("personMap") instanceof Map);
		assertTrue(superDuperDbObject.get("personSet") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("personSortedSet") instanceof BasicDBList);
		assertTrue(superDuperDbObject.get("stringMap") instanceof Map);
	}
		

}
