package com.googlecode.mjorm.mql;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.antlr.runtime.tree.Tree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public class MqlInterpreterTest {

	public static final Boolean DROP_DB_AFTER_TEST = true;

	private InterpreterImpl interpreter;
	private Mongo mongo;
	private ObjectMapper objectMapper;
	private String dbName;
	private DBCollection collection;

	@Before
	public void setUp()
		throws Exception {
		
		// connect to mongo
		mongo = new Mongo(new MongoURI("mongodb://localhost"));
		dbName = "mjormTestDb"+((int)(new Random(System.currentTimeMillis()).nextFloat()*1000));
		mongo.getDB(dbName).createCollection("people", new BasicDBObject());
		collection = mongo.getDB(dbName).getCollection("people");
		
		// create objectMapper
		AnnotationsDescriptorObjectMapper mapper = new AnnotationsDescriptorObjectMapper();
		mapper.addClass(Person.class);
		mapper.addClass(Address.class);
		objectMapper = mapper;

		// create interpreter
		interpreter = (InterpreterImpl)InterpreterFactory
			.getDefaultInstance().create(mongo.getDB(dbName), objectMapper);
	}

	@After
	public void tearDown() {
		interpreter = null;
		objectMapper = null;
		if (DROP_DB_AFTER_TEST) {
			mongo.dropDatabase(dbName);
		}
		mongo.close();
		mongo = null;
	}

	private void addPerson(
		String firstName, String lastName,
		String street, String city, String state, String zip) {
		DBObject object = BasicDBObjectBuilder.start()
			.add("firstName", firstName)
			.add("lastName", lastName)
			.push("address")
				.add("street", street)
				.add("city", city)
				.add("state", state)
				.add("zipCode", zip)
				.pop()
			.get();
		collection.insert(object);
	}

	private void addPeople(int num) {
		for (int i=0; i<num; i++) {
			addPerson(
				"first"+i, "last"+1, "street"+1, "city"+1, "state"+1, "zip"+1);
		}
	}

	private InputStream rs(String resource)
		throws IOException {
		return getClass().getResourceAsStream(resource);
	}

	private InputStream ips(String command) {
		return new ByteArrayInputStream(command.getBytes());
	}

	private List<DBObject> readAll(DBCursor cursor) {
		List<DBObject> ret = new ArrayList<DBObject>();
		while (cursor.hasNext()) {
			ret.add(cursor.next());
		}
		return ret;
	}

	@Test
	public void testSelect()
		throws Exception {

		// populate the db
		addPeople(10);

		// compile
		Tree tree = interpreter.compile(ips("from people select * limit 0, 2"));
	
		// interpret
		InterpreterResult res = interpreter.interpret(tree);
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(2, people.size());
		assertEquals("first0", people.get(0).get("firstName"));
		assertEquals("first1", people.get(1).get("firstName"));

	}

}
