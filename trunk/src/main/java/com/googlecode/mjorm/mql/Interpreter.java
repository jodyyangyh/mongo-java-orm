package com.googlecode.mjorm.mql;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

public interface Interpreter {

	/**
	 * Compiles the given code return the AST.
	 * @param ips
	 * @return
	 * @throws IOException
	 * @throws RecognitionException
	 */
	Tree compile(InputStream ips)
		throws IOException, RecognitionException;

	/**
	 * Interprets the given AST.
	 * @param tree
	 * @param parameters
	 * @return
	 */
	InterpreterResult interpret(Tree tree, Map<String, Object> parameters);

	/**
	 * Interprets the given AST.
	 * @param tree
	 * @return
	 */
	InterpreterResult interpret(Tree tree);

}
