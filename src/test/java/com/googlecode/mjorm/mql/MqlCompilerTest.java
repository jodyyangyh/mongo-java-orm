package com.googlecode.mjorm.mql;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		compiler.compile(rs("/com/googlecode/mjorm/mql/test.mql"));
	}

}
