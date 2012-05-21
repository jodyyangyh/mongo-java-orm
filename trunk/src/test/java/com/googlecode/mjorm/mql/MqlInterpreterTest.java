package com.googlecode.mjorm.mql;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.Tree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.annotations.AnnotationsDescriptorObjectMapper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public class MqlInterpreterTest {

	private boolean canTest;
	private InterpreterImpl interpreter;
	private Mongo mongo;
	private ObjectMapper objectMapper;
	private String dbName;
	private DBCollection collection;

	@Before
	public void setUp()
		throws Exception {
		
		// connect to mongo
		try {
			mongo = new Mongo(new MongoURI("mongodb://localhost"));
			mongo.getDatabaseNames();
			canTest = true;
		} catch(Throwable t) {
			System.err.println("Unable to run MongoDB integration tests: "+t.getMessage());
			t.printStackTrace();
			canTest = false;
			return;
		}

		dbName = "mjorm_test_db";
		for (String c : mongo.getDB(dbName).getCollectionNames()) {
			mongo.getDB(dbName).getCollection(c).drop();
		}
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
		if (!canTest) { return; }
		interpreter = null;
		objectMapper = null;
		mongo.dropDatabase(dbName);
		mongo.close();
		mongo = null;
	}

	private void addPerson(
		String firstName, String lastName,
		String street, String city, String state, String zip, int num) {
		DBObject object = BasicDBObjectBuilder.start()
			.add("firstName", firstName)
			.add("lastName", lastName)
			.add("numbers", new Object[] {1,2,3})
			.add("num", num)
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
				"first"+i, "last"+i, "street"+i, "city"+i, "state"+i, "zip"+i, i);
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
	public void testSelect_withLimit()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// compile
		Tree tree = interpreter.compile(ips("from people select * limit 0, 2"));
	
		// interpret
		InterpreterResult res = interpreter.interpret(tree).get(0);

		// verify
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(2, people.size());
		assertEquals("first0", people.get(0).get("firstName"));
		assertEquals("first1", people.get(1).get("firstName"));

	}

	@Test
	public void testSelect_withFields()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// compile
		Tree tree = interpreter.compile(ips("from people select firstName"));
	
		// interpret
		InterpreterResult res = interpreter.interpret(tree).get(0);

		// verify
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(10, people.size());
		for (int i=0; i<people.size(); i++) {
			assertEquals(2, people.get(i).keySet().size());
			assertNotNull(people.get(i).get("_id"));
			assertNotNull(people.get(i).get("firstName"));
		}

	}

	@Test
	public void testUpdate()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// get value
		Tree tree = interpreter.compile(ips(
			"from people where firstName='first1' select *"));
		InterpreterResult res = interpreter.interpret(tree).get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		List<DBObject> people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("first1", people.get(0).get("firstName"));

		// update it
		tree = interpreter.compile(ips(
			"from people where firstName='first1' update "
			+"set firstName='new first name' rename lastName somethingElse"));
		res = interpreter.interpret(tree).get(0);

		// get value
		tree = interpreter.compile(ips(
			"from people where firstName='new first name' select *"));
		res = interpreter.interpret(tree).get(0);

		// assert
		assertNotNull(res);
		assertNotNull(res.getCursor());
		assertNull(res.getObject());
		people = readAll(res.getCursor());
		assertEquals(1, people.size());
		assertEquals("new first name", people.get(0).get("firstName"));
		assertEquals("last1", people.get(0).get("somethingElse"));
		
	}

	@Test
	public void testUpdateMultipleCommands()
		throws Exception {
		if (!canTest) { return; }

		// populate the db
		addPeople(10);

		// get value
		StringBuilder buff = new StringBuilder();
		for (int i=0; i<100; i++) {
			buff.append("from people where firstName='first1' find and modify add to set numbers "+i+" select *; ");
		}
		Tree tree = interpreter.compile(ips(buff.toString()));
		List<InterpreterResult> res = interpreter.interpret(tree);
		assertNotNull(res);
		assertEquals(100, res.size());
		List<Integer> expect = new ArrayList<Integer>();
		expect.add(1);
		expect.add(2);
		expect.add(3);
		for (int i=0; i<100; i++) {
			DBObject obj = res.get(i).getObject();
			if (i<1 || i>3) { // 1 through 3 were already in it
				expect.add(i);
			}
			assertNotNull(obj);
			assertNotNull(obj.get("numbers"));
			BasicDBList numbers = BasicDBList.class.cast(obj.get("numbers"));
			assertArrayEquals(expect.toArray(new Object[0]), numbers.toArray(new Object[0]));
		}
		
	}

}
