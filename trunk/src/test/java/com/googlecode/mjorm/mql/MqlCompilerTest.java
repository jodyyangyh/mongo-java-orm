package com.googlecode.mjorm.mql;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mjorm.query.DaoQuery;

public class MqlCompilerTest {

	private MqlCompiler compiler;

	@Before
	public void setUp() {
		compiler = new MqlCompiler();
	}

	@After
	public void tearDown() {
		compiler = null;
	}


	public InputStream rs(String resource)
		throws IOException {
		return getClass().getResourceAsStream(resource);
	}

	@Test
	public void test()
		throws Exception {

		// compile the test
		List<DaoQuery> queries = compiler.compile(rs("/com/googlecode/mjorm/mql/test.mql"));
		assertEquals(1, queries.size());
		System.out.println(queries.get(0).toQueryObject());
	}

}
