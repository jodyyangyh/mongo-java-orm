package com.googlecode.mjorm.mql;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class MqlParserTest {


	public MqlParser createParser(String resource)
		throws IOException {
		return createParser(getClass().getResourceAsStream(resource));
	}


	public MqlParser createParser(InputStream ips)
		throws IOException {
		MqlLexer lexer 				= new MqlLexer(new ANTLRInputStream(ips));
		CommonTokenStream tokens 	= new CommonTokenStream(lexer);
		MqlParser parser 			= new MqlParser(tokens);
		return parser;
	}

	@Test
	public void test()
		throws Exception {
		MqlParser parser = createParser("/com/googlecode/mjorm/mql/test.mql");
		try {
			parser.start();
		} catch (RecognitionException e) {
			e.printStackTrace();
		}
	}

}
