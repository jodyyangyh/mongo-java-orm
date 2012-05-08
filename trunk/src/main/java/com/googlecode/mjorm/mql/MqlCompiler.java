
package com.googlecode.mjorm.mql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

import com.googlecode.mjorm.query.DaoQuery;
import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;
import com.googlecode.mjorm.query.criteria.BetweenCriterion;
import com.googlecode.mjorm.query.criteria.Criterion;
import com.googlecode.mjorm.query.criteria.DocumentCriterion;
import com.googlecode.mjorm.query.criteria.ElemMatchCriterion;
import com.googlecode.mjorm.query.criteria.ExistsCriterion;
import com.googlecode.mjorm.query.criteria.FieldCriterion;
import com.googlecode.mjorm.query.criteria.ModCriterion;
import com.googlecode.mjorm.query.criteria.NotCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion;
import com.googlecode.mjorm.query.criteria.SizeCriterion;
import com.googlecode.mjorm.query.criteria.TypeCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion.Operator;

public class MqlCompiler {

	private static final TreeAdaptor ADAPTER = new CommonTreeAdaptor() {
		public Object create(Token payload) {
			return new CommonTree(payload);
		}
	};

	private Map<String, MqlFieldFunction> fieldFunctions
		= new HashMap<String, MqlFieldFunction>();

	private Map<String, MqlDocumentFunction> documentFunctions
		= new HashMap<String, MqlDocumentFunction>();

	public MqlCompiler() {
		registerDefaultFunctions();
	}

	public void registerFieldFunction(String name, MqlFieldFunction function) {
		fieldFunctions.put(name.trim().toLowerCase(), function);
	}

	public void registerDocumentFunction(String name, MqlDocumentFunction function) {
		documentFunctions.put(name.trim().toLowerCase(), function);
	}

	public void registerDefaultFunctions() {
		registerFieldFunction("exists", ExistsCriterion.MQL_EXISTS_FUNCTION);
		registerFieldFunction("not_exists", ExistsCriterion.MQL_DOESNT_EXIST_FUNCTION);
		registerFieldFunction("between", BetweenCriterion.MQL_FUNCTION);
		registerFieldFunction("elemMatch", ElemMatchCriterion.MQL_FUNCTION);
		registerFieldFunction("mod", ModCriterion.MQL_FUNCTION);
		registerFieldFunction("size", SizeCriterion.MQL_FUNCTION);
		registerFieldFunction("type", TypeCriterion.MQL_FUNCTION);
		registerFieldFunction("in", SimpleCriterion.createForOperator(Operator.IN, 1, Integer.MAX_VALUE, -1));
		registerFieldFunction("nin", SimpleCriterion.createForOperator(Operator.NIN, 1, Integer.MAX_VALUE, -1));
		registerFieldFunction("all", SimpleCriterion.createForOperator(Operator.ALL, 1, Integer.MAX_VALUE, -1));

		//registerDocumentFunction("or", MqlDocumentFunctionImpl.createDocumentQueryFunction("$or"));
		//registerDocumentFunction("nor", MqlDocumentFunctionImpl.createDocumentQueryFunction("$nor"));
		//registerDocumentFunction("and", MqlDocumentFunctionImpl.createDocumentQueryFunction("$and"));
	}

	public List<DaoQuery> compile(String code)
		throws IOException,
		RecognitionException {
		return compile(new ByteArrayInputStream(code.getBytes()));
	}

	public List<DaoQuery> compile(InputStream ips)
		throws IOException,
		RecognitionException {

		// create the lexer and parser
		MqlLexer lexer 				= new MqlLexer(new ANTLRInputStream(ips));
		CommonTokenStream tokens 	= new CommonTokenStream(lexer);
		MqlParser parser 			= new MqlParser(tokens);

		// set adapter
		parser.setTreeAdaptor(ADAPTER);

		// parse
		MqlParser.start_return ast = parser.start();
		CommonTree tree = CommonTree.class.cast(ast.getTree());

		// walk the AST
		List<DaoQuery> ret = new ArrayList<DaoQuery>(tree.getChildCount());
		for (int i=0; i<tree.getChildCount(); i++) {
			ret.add(readCommand(tree.getChild(i)));
		}

		// return the stuff
		return ret;
	}

	/**
	 * Reads a a command from the tree.
	 * @param tree
	 * @return
	 */
	private DaoQuery readCommand(Tree tree) {

		// create the query
		DaoQuery ret = new DaoQuery();

		// set collection
		ret.setCollection(tree.getChild(0).getText());

		// read criteria
		if (tree.getChild(1).getType()==MqlParser.CRITERIA) {
			readCriteria(tree.getChild(1), ret);
		}

		// read action
		readAction(tree.getChild(tree.getChildCount()-1), ret);

		// return it
		return ret;
	}

	/**
	 * Reads criteria.
	 * @param tree
	 * @param query
	 */
	private void readCriteria(Tree tree, AbstractQueryCriterion<?> query) {
		assertTokenType(tree, MqlParser.CRITERIA);
		for (int i=0; i<tree.getChildCount(); i++) {
			readCriterion(tree.getChild(i), query);
		}
	}

	/**
	 * Creates a {@link Criterion} from the given tree and
	 * adds it to the given {@link DaoQuery}.
	 * @param tree
	 * @param query
	 */
	private void readCriterion(Tree tree, AbstractQueryCriterion<?> query) {
		DocumentCriterion criterion = null;
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				String functionName = tree.getChild(0).getChild(0).getText().trim().toLowerCase();
				//groupQuery = Query.class.cast(createCriterion(tree));
				//groupName = documentFunctions.get(functionName).getGroupName();
				break;
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				String fieldName = tree.getChild(0).getText().trim().toLowerCase();
				criterion = new FieldCriterion(fieldName, createCriterion(tree));
				break;
				
			case MqlParser.COMPARE_CRITERION:
				fieldName = tree.getChild(0).getText().trim().toLowerCase();
				criterion = new FieldCriterion(fieldName, createCriterion(tree));
				break;
				
			case MqlParser.NEGATED_CRITERION:
				fieldName = tree.getChild(0).getChild(0).getText().trim().toLowerCase();
				criterion = new NotCriterion(fieldName, createCriterion(tree.getChild(0)));
				break;
				
			default:
				assertTokenType(tree);
		}
		query.add(criterion);
	}

	/**
	 * Reads a query action.
	 * @param tree
	 * @param query
	 */
	private void readAction(Tree tree, DaoQuery query) {
		
	}

	/**
	 * Creates a {@link Criterion} from the given tree.
	 * @param tree
	 * @return
	 */
	private Criterion createCriterion(Tree tree) {
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				return readQueryForDocumentFunction(tree.getChild(0), documentFunctions);
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				return readCriterionForFieldFunction(tree.getChild(0), fieldFunctions);
				
			case MqlParser.COMPARE_CRITERION:
				return new SimpleCriterion(
					tree.getChild(1).getText(),
					readVariableLiteral(tree.getChild(2)));
				
			case MqlParser.NEGATED_CRITERION:
				Criterion c = createCriterion(tree.getChild(0));
				if (!FieldCriterion.class.isInstance(c)) {
					throw new IllegalArgumentException(
						"NOT requires FieldCriteiron");
				}
				return new NotCriterion(FieldCriterion.class.cast(c));
				
			default:
				assertTokenType(tree);
				return null;
		}
	}

	/**
	 * Creates a {@link Criterion} for the given function call.
	 * @param tree
	 * @param functionTable
	 * @return
	 */
	private Query readQueryForDocumentFunction(
		Tree tree, Map<String, MqlDocumentFunction> functionTable) {
		assertTokenType(tree, MqlParser.FUNCTION_CALL);

		// get the function name
		String functionName = tree.getChild(0).getText().trim().toLowerCase();
		Query ret = null;

		// function not found
		if (!functionTable.containsKey(functionName)) {
			throw new IllegalArgumentException(
				"Unknown function: "+functionName);

		// no arguments
		} else if (tree.getChildCount()==1) {
			ret = functionTable.get(functionName).createForNoArguments();

		// criteria arguments
		} else if (tree.getChild(1).getType()==MqlParser.CRITERIA) {
			Query query = new Query();
			readCriteria(tree.getChild(1), query);
			ret = functionTable.get(functionName).createForQuery(query);

		// variable list arguments
		} else if (tree.getChild(1).getType()==MqlParser.VARIABLE_LIST) {
			Object[] arguments = readVariableList(tree.getChild(1));
			ret = functionTable.get(functionName).createForArguments(arguments);
		}

		// return it
		return ret;
	}

	/**
	 * Creates a {@link Criterion} for the given function call.
	 * @param tree
	 * @param functionTable
	 * @return
	 */
	private Criterion readCriterionForFieldFunction(
		Tree tree, Map<String, MqlFieldFunction> functionTable) {
		assertTokenType(tree, MqlParser.FUNCTION_CALL);

		// get the function name
		String functionName = tree.getChild(0).getText().trim().toLowerCase();
		Criterion ret = null;

		// function not found
		if (!functionTable.containsKey(functionName)) {
			throw new IllegalArgumentException(
				"Unknown function: "+functionName);

		// no arguments
		} else if (tree.getChildCount()==1) {
			ret = functionTable.get(functionName).createForNoArguments();

		// criteria arguments
		} else if (tree.getChild(1).getType()==MqlParser.CRITERIA) {
			Query query = new Query();
			readCriteria(tree.getChild(1), query);
			ret = functionTable.get(functionName).createForQuery(query);

		// variable list arguments
		} else if (tree.getChild(1).getType()==MqlParser.VARIABLE_LIST) {
			Object[] arguments = readVariableList(tree.getChild(1));
			ret = functionTable.get(functionName).createForArguments(arguments);
		}

		// return it
		return ret;
	}

	/**
	 * Reads a variable literal.
	 * @param tree
	 * @return
	 */
	private Object readVariableLiteral(Tree tree) {
		return null;
	}

	/**
	 * Reads variable literals from a variable list.
	 * @param tree
	 * @return
	 */
	private Object[] readVariableList(Tree tree) {
		assertTokenType(tree, MqlParser.VARIABLE_LIST);
		Object[] ret = new Object[tree.getChildCount()];
		for (int i=0; i<ret.length; i++) {
			ret[i] = readVariableLiteral(tree.getChild(i));
		}
		return ret;
	}

	/**
	 * Asserts a token is of an expected type.
	 * @param tree
	 * @param types
	 */
	private void assertTokenType(Tree tree, int... types) {
		if (tree==null) {
			throw new IllegalArgumentException(
				"Got a null token when expecting a specific type");
		}
		int treeType = tree.getType();
		for (int type : types) {
			if (type==treeType) {
				return;
			}
		}
		throw new IllegalArgumentException(
			"Unknown token: "+tree.toString());
	}

}
