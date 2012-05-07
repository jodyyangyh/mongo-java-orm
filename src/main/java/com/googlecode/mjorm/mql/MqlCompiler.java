
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
import com.googlecode.mjorm.query.criteria.ElemMatchCriterion;
import com.googlecode.mjorm.query.criteria.ExistsCriterion;
import com.googlecode.mjorm.query.criteria.ModCriterion;
import com.googlecode.mjorm.query.criteria.NotCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion;
import com.googlecode.mjorm.query.criteria.SizeCriterion;
import com.googlecode.mjorm.query.criteria.TypeCriterion;

public class MqlCompiler {

	private static final TreeAdaptor ADAPTER = new CommonTreeAdaptor() {
		public Object create(Token payload) {
			return new CommonTree(payload);
		}
	};

	private Map<String, MqlFunction> fieldFunctions
		= new HashMap<String, MqlFunction>();

	private Map<String, MqlFunction> groupFunctions
		= new HashMap<String, MqlFunction>();

	public MqlCompiler() {
		registerDefaultFunctions();
	}

	public void registerFieldFunction(String name, MqlFunction function) {
		fieldFunctions.put(name.toLowerCase(), function);
	}

	public void registerGroupFunction(String name, MqlFunction function) {
		groupFunctions.put(name.toLowerCase(), function);
	}

	public void registerDefaultFunctions() {
		registerFieldFunction("exists", ExistsCriterion.MQL_EXISTS_FUNCTION);
		registerFieldFunction("not_exists", ExistsCriterion.MQL_DOESNT_EXIST_FUNCTION);
		registerFieldFunction("between", BetweenCriterion.MQL_FUNCTION);
		registerFieldFunction("elemMatch", ElemMatchCriterion.MQL_FUNCTION);
		registerFieldFunction("mod", ModCriterion.MQL_FUNCTION);
		registerFieldFunction("size", SizeCriterion.MQL_FUNCTION);
		registerFieldFunction("type", TypeCriterion.MQL_FUNCTION);
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
		String fieldName = null;
		String groupName = null;
		Query groupQuery = null;
		Criterion criterion = null;
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				groupName = getFunctionNameFromFunctionCall(tree.getChild(0));
				criterion = createCriterion(tree);
				break;
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				fieldName = getFieldNameFromFieldCriterion(tree);
				criterion = createCriterion(tree);
				break;
				
			case MqlParser.COMPARE_CRITERION:
				fieldName = getFieldNameFromFieldCriterion(tree);
				criterion = createCriterion(tree);
				break;
				
			case MqlParser.NEGATED_CRITERION:
				fieldName = getFieldNameFromFieldCriterion(tree.getChild(0));
				criterion = createCriterion(tree.getChild(0));
				break;
				
			default:
				assertTokenType(tree);
		}
		if (groupName!=null) {
			// TODO: work out document function criterion
			//query.doc(groupName, criterion);
		} else {
			query.add(fieldName, criterion);
		}
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
				String groupFunctionName = tree.getChild(0).getText();
				if (!groupFunctions.containsKey(groupFunctionName)) {
					throw new IllegalArgumentException(
						"Unknown doc function: "+groupFunctionName);
					
				} else if (tree.getChildCount()==1) {
					return groupFunctions.get(groupFunctionName).createForNoArguments();
					
				} else if (tree.getChild(1).getType()==MqlParser.CRITERIA) {
					Query query = new Query();
					readCriteria(tree.getChild(1), query);
					return groupFunctions.get(groupFunctionName).createForQuery(query);
					
				} else if (tree.getChild(1).getType()==MqlParser.VARIABLE_LIST) {
					Object[] arguments = readVariableListFromFunctionCall(tree.getChild(1));
					return groupFunctions.get(groupFunctionName).createForArguments(arguments);
				}
				throw new IllegalArgumentException(
					"Unknown document function argument type");
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				String functionName = getFunctionNameFromFunctionCall(tree.getChild(1));
				if (!fieldFunctions.containsKey(functionName)) {
					throw new IllegalArgumentException(
						"Unknown field function: "+functionName);
				}
				Object[] arguments = readVariableListFromFunctionCall(tree.getChild(1));
				return fieldFunctions.get(functionName).createForArguments(arguments); 
				
			case MqlParser.COMPARE_CRITERION:
				return new SimpleCriterion(
					tree.getChild(1).getText(),
					readVariableLiteral(tree.getChild(2)));
				
			case MqlParser.NEGATED_CRITERION:
				return new NotCriterion(createCriterion(tree.getChild(0)));
				
			default:
				assertTokenType(tree);
				return null;
		}
	}

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
	 * Returns a function name from a function call.
	 * @param tree
	 * @return
	 */
	private Object[] readVariableListFromFunctionCall(Tree tree) {
		assertTokenType(tree, MqlParser.FUNCTION_CALL);
		return tree.getChildCount()>1
			? readVariableList(tree.getChild(1))
			: new Object[0];
	}

	/**
	 * Returns a function name from a function call.
	 * @param tree
	 * @return
	 */
	private String getFunctionNameFromFunctionCall(Tree tree) {
		assertTokenType(tree, MqlParser.FUNCTION_CALL);
		return tree.getChild(0).getText().toLowerCase();
	}

	/**
	 * Gets the field name from a field criterion.
	 * @param tree
	 * @return
	 */
	private String getFieldNameFromFieldCriterion(Tree tree) {
		assertTokenType(tree,
			MqlParser.FIELD_FUNCTION_CRITERION,
			MqlParser.COMPARE_CRITERION);
		return tree.getChild(0).getText();
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
