
package com.googlecode.mjorm.mql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;
import com.googlecode.mjorm.query.criteria.BetweenCriterion;
import com.googlecode.mjorm.query.criteria.Criterion;
import com.googlecode.mjorm.query.criteria.DocumentCriterion;
import com.googlecode.mjorm.query.criteria.ElemMatchCriterion;
import com.googlecode.mjorm.query.criteria.EqualsCriterion;
import com.googlecode.mjorm.query.criteria.ExistsCriterion;
import com.googlecode.mjorm.query.criteria.FieldCriterion;
import com.googlecode.mjorm.query.criteria.ModCriterion;
import com.googlecode.mjorm.query.criteria.NotCriterion;
import com.googlecode.mjorm.query.criteria.RegexCriterion;
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

	private Map<String, MqlFunction> fieldFunctions
		= new HashMap<String, MqlFunction>();

	private Map<String, MqlFunction> documentFunctions
		= new HashMap<String, MqlFunction>();

	private Map<String, Operator> comparisonOperators
		= new HashMap<String, SimpleCriterion.Operator>();

	public MqlCompiler() {
		registerDefaultFunctions();
		registerComparisonOperators();
	}

	public void registerFieldFunction(MqlFunction function) {
		fieldFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	public void registerDocumentFunction(MqlFunction function) {
		documentFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	private void registerComparisonOperators() {
		comparisonOperators.put(">", Operator.GT);
		comparisonOperators.put(">=", Operator.GTE);
		comparisonOperators.put("<", Operator.LT);
		comparisonOperators.put("<=", Operator.LTE);
		comparisonOperators.put("!=", Operator.NE);
		comparisonOperators.put("<>", Operator.NE);
	}

	private void registerDefaultFunctions() {
		
		// field functions
		registerFieldFunction(ExistsCriterion.createFunction("exists"));
		registerFieldFunction(ExistsCriterion.createNegatedFunction("not_exists"));
		registerFieldFunction(BetweenCriterion.createFunction("between"));
		registerFieldFunction(ElemMatchCriterion.createFunction("elemMatch"));
		registerFieldFunction(ModCriterion.createFunction("mod"));
		registerFieldFunction(SizeCriterion.createFunction("size"));
		registerFieldFunction(TypeCriterion.createFunction("type"));
		registerFieldFunction(SimpleCriterion.createForOperator("in", Operator.IN, 1, Integer.MAX_VALUE, -1));
		registerFieldFunction(SimpleCriterion.createForOperator("nin", Operator.NIN, 1, Integer.MAX_VALUE, -1));
		registerFieldFunction(SimpleCriterion.createForOperator("all", Operator.ALL, 1, Integer.MAX_VALUE, -1));

		// document functions
		registerDocumentFunction(QueryGroup.createMqlDocumentFunction("or", "$or", true, true));
		registerDocumentFunction(QueryGroup.createMqlDocumentFunction("nor", "$nor", true, true));
		registerDocumentFunction(QueryGroup.createMqlDocumentFunction("and", "$and", true, true));
		registerDocumentFunction(QueryGroup.createMqlDocumentFunction("predicate", "$where", 1));
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
		String fieldName = null;
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				String functionName = tree.getChild(0).getChild(0).getText().trim().toLowerCase();
				Criterion c = createCriterion(tree);
				if (!DocumentCriterion.class.isInstance(c)) {
					throw new IllegalArgumentException(
						"Document function '"+functionName+"' returned a Criterion other than a DocumentCriterion");
				}
				criterion = DocumentCriterion.class.cast(c);
				break;
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				fieldName = tree.getChild(0).getText().trim().toLowerCase();
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
				return readCriterionForFunctionCall(tree.getChild(0), documentFunctions);
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				return readCriterionForFunctionCall(tree.getChild(1), fieldFunctions);
				
			case MqlParser.COMPARE_CRITERION:
				String op = tree.getChild(1).getText();
				Object value = readVariableLiteral(tree.getChild(2));
				if (op.equals("=")) {
					return new EqualsCriterion(value);
				} else if (op.equals("=~")) {
					return new RegexCriterion(Pattern.class.cast(value));
				}
				return new SimpleCriterion(comparisonOperators.get(op), value);
				
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
	private Criterion readCriterionForFunctionCall(
		Tree tree, Map<String, MqlFunction> functionTable) {
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

		// criteria arguments
		} else if (tree.getChild(1).getType()==MqlParser.CRITERIA_GROUP_LIST) {
			QueryGroup queryGroup = new QueryGroup();
			readCriteriaGroupList(tree.getChild(1), queryGroup);
			ret = functionTable.get(functionName).createForQueryGroup(queryGroup);

		// variable list arguments
		} else if (tree.getChild(1).getType()==MqlParser.VARIABLE_LIST) {
			Object[] arguments = readVariableList(tree.getChild(1));
			ret = functionTable.get(functionName).createForArguments(arguments);
		}

		// return it
		return ret;
	}

	private QueryGroup readCriteriaGroupList(Tree tree, QueryGroup queryGroup) {
		assertTokenType(tree, MqlParser.CRITERIA_GROUP_LIST);
		if (queryGroup==null) {
			queryGroup = new QueryGroup();
		}
		for (int i=0; i<tree.getChildCount(); i++) {
			Tree groupTree = tree.getChild(i);
			Query query = new Query();
			readCriteria(groupTree.getChild(0), query);
			queryGroup.add(query);
		}
		return queryGroup;
	}

	/**
	 * Reads a variable literal.
	 * @param tree
	 * @return
	 */
	private Object readVariableLiteral(Tree tree) {
		String text = tree.getText();
		switch (tree.getType()){
			case MqlParser.REGEX:
				return Pattern.compile(text);
			case MqlParser.INTEGER:
				return new Integer(text);
			case MqlParser.DECIMAL:
				return new Double(text);
			case MqlParser.DOUBLE_QUOTED_STRING:
				return text;
			case MqlParser.SINGLE_QUOTED_STRING:
				return text;
			case MqlParser.TRUE:
				return Boolean.TRUE;
			case MqlParser.FALSE:
				return Boolean.FALSE;
			case MqlParser.ARRAY:
				Object[] vars = new Object[tree.getChild(0).getChildCount()];
				for (int i=0; i<vars.length; i++) {
					vars[i] = readVariableLiteral(tree.getChild(0).getChild(i));
				}
				return vars;
			default:
				throw new IllegalArgumentException(
					"Unknown variable literal type "+tree.getType()+" with value "+text);
		}
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
