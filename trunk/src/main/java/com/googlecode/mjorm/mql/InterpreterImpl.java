
package com.googlecode.mjorm.mql;

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

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.query.DaoModifier;
import com.googlecode.mjorm.query.DaoQuery;
import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.AbstractQueryCriterion;
import com.googlecode.mjorm.query.criteria.Criterion;
import com.googlecode.mjorm.query.criteria.DocumentCriterion;
import com.googlecode.mjorm.query.criteria.EqualsCriterion;
import com.googlecode.mjorm.query.criteria.FieldCriterion;
import com.googlecode.mjorm.query.criteria.NotCriterion;
import com.googlecode.mjorm.query.criteria.RegexCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion.Operator;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class InterpreterImpl
	implements Interpreter {

	private static final Map<String, Object> NO_PARAMS = new HashMap<String, Object>();

	private static final CommonTreeAdaptor ADAPTER = new CommonTreeAdaptor() {
		public Object create(Token payload) {
			return new CommonTree(payload);
		}
	};

	private static final Map<String, Operator> comparisonOperators
		= new HashMap<String, SimpleCriterion.Operator>();

	static {
		comparisonOperators.put(">", Operator.GT);
		comparisonOperators.put(">=", Operator.GTE);
		comparisonOperators.put("<", Operator.LT);
		comparisonOperators.put("<=", Operator.LTE);
		comparisonOperators.put("!=", Operator.NE);
		comparisonOperators.put("<>", Operator.NE);
	}

	private DB db;
	private ObjectMapper objectMapper;
	private Map<String, MqlCriterionFunction> fieldFunctions;
	private Map<String, MqlCriterionFunction> documentFunctions;

	/**
	 * Creates it.
	 * @param db
	 * @param objectMapper
	 */
	public InterpreterImpl(DB db, ObjectMapper objectMapper) {
		this.db 				= db;
		this.objectMapper		= objectMapper;
		this.documentFunctions	= new HashMap<String, MqlCriterionFunction>();
		this.fieldFunctions		= new HashMap<String, MqlCriterionFunction>();
	}

	/**
	 * Registers a field function.
	 * @param function
	 */
	public void registerFieldFunction(MqlCriterionFunction function) {
		fieldFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	/**
	 * Registers a document function.
	 * @param function
	 */
	public void registerDocumentFunction(MqlCriterionFunction function) {
		documentFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	/**
	 * Compiles the given code return the AST.
	 * @param ips
	 * @return
	 * @throws IOException
	 * @throws RecognitionException
	 */
	public CommonTree compile(InputStream ips)
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
		return CommonTree.class.cast(ast.getTree());
	}

	/**
	 * {@inheritDoc}
	 */
	public List<InterpreterResult> interpret(Tree tree) {
		return interpret(tree, NO_PARAMS);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<InterpreterResult> interpret(
		Tree tree, Map<String, Object> parameters) {
		assertTokenType(tree, MqlParser.COMMANDS);
		List<InterpreterResult> ret = new ArrayList<InterpreterResult>();
		for (int i=0; i<tree.getChildCount(); i++) {
			ret.add(doInterpret(
				CommonTree.class.cast(tree.getChild(i)), parameters));
		}
		return ret;
	}


	/**
	 * Interprets a command tree.
	 * @param t
	 * @param parameters
	 * @return
	 */
	private InterpreterResult doInterpret(
		CommonTree tree, Map<String, Object> parameters) {
		assertTokenType(tree, MqlParser.COMMAND);

		// setup the query
		DaoQuery query = new DaoQuery();
		query.setDB(db);
		query.setObjectMapper(objectMapper);

		// set collection
		query.setCollection(child(tree, 0).getText());

		// read criteria
		CommonTree actionTree = null;
		if (child(tree, 1).getType()==MqlParser.CRITERIA) {
			readCriteria(child(tree, 1), query);
			actionTree = child(tree, 2);
		} else {
			actionTree = child(tree, 1);
		}

		// invoke the action
		assertTokenType(actionTree, MqlParser.ACTION);
		actionTree = child(actionTree, 0);
		switch (actionTree.getType()) {

			// select
			case MqlParser.SELECT_ACTION: {
				return executeSelectAction(actionTree, query);
			}

			// explain
			case MqlParser.EXPLAIN_ACTION: {
				return executeExplainAction(actionTree, query);
			}

			// delete
			case MqlParser.DELETE_ACTION: {
				return executeDeleteAction(actionTree, query);
			}

			// update
			case MqlParser.UPDATE_ACTION: {
				return executeUpdateAction(actionTree, query, false);
			}

			// upsert
			case MqlParser.UPSERT_ACTION: {
				return executeUpdateAction(actionTree, query, true);
			}

			// find and modify
			case MqlParser.FIND_AND_MODIFY: {
				return executeFamAction(actionTree, query);
			}

			// find and delete
			case MqlParser.FIND_AND_DELETE: {
				return executeFadAction(actionTree, query);
			}

			// zomg we're all gunna die
			default:
				throw new IllegalArgumentException(
					"Unknown action type");
		}
	}

	/**
	 * Executes a Find And Delete.
	 * @param tree
	 * @param query
	 * @return
	 */
	private InterpreterResult executeFadAction(CommonTree tree, DaoQuery query) {

		// get field list
		CommonTree fieldListTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.FIELD_LIST));
		DBObject fields = readFieldList(fieldListTree);

		// read sort
		CommonTree sortTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.SORT));
		if (sortTree!=null) {
			readSort(sortTree, query);
		}

		// execute it
		return new InterpreterResult(
			null, query.modify().findAndDelete(fields), null);
	}

	/**
	 * Executes a Find And Modify.
	 * @param tree
	 * @param query
	 * @return
	 */
	private InterpreterResult executeFamAction(CommonTree tree, DaoQuery query) {

		Tree upsert = tree.getFirstChildWithType(MqlParser.UPSERT);
		Tree returnTree = tree.getFirstChildWithType(MqlParser.RETURN);
		boolean returnNew = (returnTree!=null)
			? returnTree.getChild(0).getType()==MqlParser.NEW
			: true;

		// read updateOperations
		Tree updateTree = tree.getFirstChildWithType(MqlParser.UPDATE_OPERATIONS);
		readModifiers(CommonTree.class.cast(updateTree), query);

		// get field list
		CommonTree fieldListTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.FIELD_LIST));
		DBObject fields = readFieldList(fieldListTree);

		// read sort
		CommonTree sortTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.SORT));
		if (sortTree!=null) {
			readSort(sortTree, query);
		}

		// execute it
		return new InterpreterResult(
			null, query.modify().findAndModify(returnNew, upsert!=null, fields), null);
	}
	
	/**
	 * Executes an update action.
	 * @param action
	 * @param query
	 * @param upsert
	 * @return
	 */
	private InterpreterResult executeUpdateAction(
		CommonTree tree, DaoQuery query, boolean upsert) {

		// atomic? multi?
		Tree atomic = tree.getFirstChildWithType(MqlParser.ATOMIC);
		Tree multi = tree.getFirstChildWithType(MqlParser.MULTI);

		// read updateOperations
		Tree updateTree = tree.getFirstChildWithType(MqlParser.UPDATE_OPERATIONS);
		readModifiers(CommonTree.class.cast(updateTree), query);

		// execute it
		WriteResult res = query.modify()
			.setAtomic(atomic!=null)
			.update(upsert, multi!=null);

		// execute it
		return new InterpreterResult(null, null, res);
	}

	/**
	 * Executes a delete action.
	 * @param action
	 * @param query
	 * @return
	 */
	private InterpreterResult executeDeleteAction(CommonTree tree, DaoQuery query) {

		// read hint
		Tree atomic = tree.getFirstChildWithType(MqlParser.ATOMIC);

		// execute it
		WriteResult res = query
			.modify()
			.setAtomic(atomic!=null)
			.deleteObjects();

		// execute it
		return new InterpreterResult(null, null, res);
	}

	/**
	 * Executes an explain action.
	 * @param action
	 * @param query
	 * @return
	 */
	private InterpreterResult executeExplainAction(CommonTree tree, DaoQuery query) {

		// read hint
		CommonTree hintTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.HINT));
		if (hintTree!=null) {
			readHint(hintTree, query);
		}

		// execute it
		return new InterpreterResult(null, query.explain(), null);
	}

	/**
	 * Executes a select action.
	 * @param action
	 * @param query
	 * @return
	 */
	private InterpreterResult executeSelectAction(CommonTree tree, DaoQuery query) {

		// get field list
		CommonTree fieldListTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.FIELD_LIST));
		DBObject fields = readFieldList(fieldListTree);

		// read hint
		CommonTree hintTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.HINT));
		if (hintTree!=null) {
			readHint(hintTree, query);
		}

		// read sort
		CommonTree sortTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.SORT));
		if (sortTree!=null) {
			readSort(sortTree, query);
		}

		// read limit
		CommonTree limitTree = CommonTree.class.cast(
			tree.getFirstChildWithType(MqlParser.LIMIT));
		if (limitTree!=null) {
			readLimit(limitTree, query);
		}

		// execute it
		return (fields!=null)
			? new InterpreterResult(query.findObjects(fields), null, null)
			: new InterpreterResult(query.findObjects(), null, null);
	}

	/**
	 * Reads modifiers
	 * @param tree
	 * @param query
	 */
	private void readModifiers(CommonTree tree, DaoQuery query) {
		assertTokenType(tree, MqlParser.UPDATE_OPERATIONS);

		// get the modifer
		DaoModifier modifier = query.modify();

		// go through each operation
		for (int i=0; i<tree.getChildCount(); i++) {
			CommonTree modiferTree = child(tree, i);
			String field = null;
			Object value = null;

			// add the operation to the query
			switch(modiferTree.getType()) {
				case MqlParser.INC:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.inc(field, Number.class.cast(value));
					break;
				case MqlParser.SET:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.set(field, value);
					break;
				case MqlParser.UNSET:
					field = child(modiferTree, 0).getText();
					modifier.unset(field);
					break;
				case MqlParser.PUSH:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.push(field, value);
					break;
				case MqlParser.PUSH_ALL:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.pushAll(field, Object[].class.cast(value));
					break;
				case MqlParser.ADD_TO_SET:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.addToSet(field, value);
					break;
				case MqlParser.ADD_TO_SET_EACH:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.addToSetEach(field, Object[].class.cast(value));
					break;
				case MqlParser.POP:
					field = child(modiferTree, 0).getText();
					modifier.pop(field);
					break;
				case MqlParser.SHIFT:
					field = child(modiferTree, 0).getText();
					modifier.pop(field);
					break;
				case MqlParser.PULL:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.pull(field, value);
					break;
				case MqlParser.PULL_ALL:
					field = child(modiferTree, 0).getText();
					value = readVariableLiteral(child(modiferTree, 1));
					modifier.pullAll(field, Object[].class.cast(value));
					break;
				case MqlParser.RENAME:
					field = child(modiferTree, 0).getText();
					value = child(modiferTree, 1).getText();
					modifier.rename(field, String.class.cast(value));
					break;
				case MqlParser.BITWISE:
					Tree opTree = modiferTree.getChild(0);
					field = child(modiferTree, 1).getText();
					value = readVariableLiteral(child(modiferTree, 2));
					if (opTree.getType()==MqlParser.AND) {
						modifier.bitwiseAnd(field, Number.class.cast(value));
					} else {
						modifier.bitwiseOr(field, Number.class.cast(value));
					}
					break;
				default:
					throw new IllegalArgumentException(
						"Unknown modifier:" +modiferTree.toString());
			}
			
		}
		
	}

	
	/**
	 * Reads a sort.
	 * @param tree
	 * @param query
	 */
	private void readLimit(CommonTree tree, DaoQuery query) {
		assertTokenType(tree, MqlParser.LIMIT);

		int start 	= 0;
		int end 	= 0;

		if (tree.getChildCount()==1) {
			end = Integer.parseInt(tree.getChild(0).getText());
		} else {
			start = Integer.parseInt(tree.getChild(0).getText());
			end = Integer.parseInt(tree.getChild(1).getText());
		}

		query.setFirstDocument(start);
		query.setMaxDocuments(end);
	}

	/**
	 * Reads a sort.
	 * @param tree
	 * @param query
	 */
	private void readSort(CommonTree tree, DaoQuery query) {
		assertTokenType(tree, MqlParser.SORT);

		tree = CommonTree.class.cast(tree.getChild(0));
		for (int i=0; i<tree.getChildCount(); i++) {
			Tree sortField = tree.getChild(0);
			Tree direction = tree.getChild(1);
			int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
			query.addSort(sortField.getText(), dir);
		}
	}

	/**
	 * Reads a hint.
	 * @param tree
	 * @param query
	 */
	private void readHint(CommonTree tree, DaoQuery query) {
		assertTokenType(tree, MqlParser.HINT);

		// natural
		if (tree.getChild(0).getType()==MqlParser.NATURAL) {
			Tree direction = tree.getChild(1);
			int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
			query.setHint("$natural", dir);
			return;

		// string
		} else if (isString(tree.getChild(0))) {
			Tree direction = tree.getChild(1);
			int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
			query.setHint(tree.getChild(0).getText(), dir);
			return;

		// hint fields
		} else {
			DBObject hint = new BasicDBObject();
			for (int i=0; i<tree.getChildCount(); i++) {
				Tree hintField = tree.getChild(0);
				Tree direction = hintField.getChild(1);
				int dir = (direction==null || direction.getType()==MqlParser.ASC) ? 1 : -1;
				hint.put(hintField.getChild(0).getText(), dir);
			}
			query.setHint(hint);
		}
	}

	/**
	 * Reads a field list.
	 * @param fieldList
	 * @return
	 */
	private DBObject readFieldList(CommonTree fieldList) {
		if (fieldList==null) { return null; }
		assertTokenType(fieldList, MqlParser.FIELD_LIST);
		DBObject fields = new BasicDBObject();
		for (int i=0; i<fieldList.getChildCount(); i++) {
			if (fieldList.getChild(i).getType()==MqlParser.STAR) {
				return null;
			}
			fields.put(fieldList.getChild(i).getText(), 1);
		}
		return fields;
	}

	/**
	 * Reads criteria.
	 * @param tree
	 * @param query
	 */
	private void readCriteria(CommonTree tree, AbstractQueryCriterion<?> query) {
		assertTokenType(tree, MqlParser.CRITERIA);
		for (int i=0; i<tree.getChildCount(); i++) {
			readCriterion(child(tree, i), query);
		}
	}

	/**
	 * Creates a {@link Criterion} from the given tree and
	 * adds it to the given {@link DaoQuery}.
	 * @param tree
	 * @param query
	 */
	private void readCriterion(CommonTree tree, AbstractQueryCriterion<?> query) {
		DocumentCriterion criterion = null;
		String fieldName = null;
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				String functionName = child(tree, 0).getChild(0).getText().trim().toLowerCase();
				Criterion c = createCriterion(tree);
				if (!DocumentCriterion.class.isInstance(c)) {
					throw new IllegalArgumentException(
						"Document function '"+functionName+"' returned a Criterion other than a DocumentCriterion");
				}
				criterion = DocumentCriterion.class.cast(c);
				break;
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				fieldName = child(tree, 0).getText().trim();
				criterion = new FieldCriterion(fieldName, createCriterion(tree));
				break;
				
			case MqlParser.COMPARE_CRITERION:
				fieldName = child(tree, 0).getText().trim();
				criterion = new FieldCriterion(fieldName, createCriterion(tree));
				break;
				
			case MqlParser.NEGATED_CRITERION:
				fieldName = child(tree, 0).getChild(0).getText().trim();
				criterion = new NotCriterion(fieldName, createCriterion(child(tree, 0)));
				break;
				
			default:
				assertTokenType(tree);
		}
		query.add(criterion);
	}

	/**
	 * Creates a {@link Criterion} from the given tree.
	 * @param tree
	 * @return
	 */
	private Criterion createCriterion(CommonTree tree) {
		switch (tree.getType()) {
			case MqlParser.DOCUMENT_FUNCTION_CRITERION:
				return readCriterionForFunctionCall(child(tree, 0), documentFunctions);
				
			case MqlParser.FIELD_FUNCTION_CRITERION:
				return readCriterionForFunctionCall(child(tree, 1), fieldFunctions);
				
			case MqlParser.COMPARE_CRITERION:
				String op = child(tree, 1).getText();
				Object value = readVariableLiteral(child(tree, 2));
				if (op.equals("=")) {
					return new EqualsCriterion(value);
				} else if (op.equals("=~")) {
					return new RegexCriterion(Pattern.class.cast(value));
				}
				return new SimpleCriterion(comparisonOperators.get(op), value);
				
			case MqlParser.NEGATED_CRITERION:
				Criterion c = createCriterion(child(tree, 0));
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
		CommonTree tree, Map<String, MqlCriterionFunction> functionTable) {
		assertTokenType(tree, MqlParser.FUNCTION_CALL);

		// get the function name
		String functionName = child(tree, 0).getText().trim().toLowerCase();
		Criterion ret = null;

		// function not found
		if (!functionTable.containsKey(functionName)) {
			throw new IllegalArgumentException(
				"Unknown function: "+functionName);

		// no arguments
		} else if (tree.getChildCount()==1) {
			ret = functionTable.get(functionName).createForNoArguments();

		// criteria arguments
		} else if (child(tree, 1).getType()==MqlParser.CRITERIA) {
			Query query = new Query();
			readCriteria(child(tree, 1), query);
			ret = functionTable.get(functionName).createForQuery(query);

		// criteria arguments
		} else if (child(tree, 1).getType()==MqlParser.CRITERIA_GROUP_LIST) {
			QueryGroup queryGroup = new QueryGroup();
			readCriteriaGroupList(child(tree, 1), queryGroup);
			ret = functionTable.get(functionName).createForQueryGroup(queryGroup);

		// variable list arguments
		} else if (child(tree, 1).getType()==MqlParser.VARIABLE_LIST) {
			Object[] arguments = readVariableList(child(tree, 1));
			ret = functionTable.get(functionName).createForArguments(arguments);
		}

		// return it
		return ret;
	}

	private QueryGroup readCriteriaGroupList(CommonTree tree, QueryGroup queryGroup) {
		assertTokenType(tree, MqlParser.CRITERIA_GROUP_LIST);
		if (queryGroup==null) {
			queryGroup = new QueryGroup();
		}
		for (int i=0; i<tree.getChildCount(); i++) {
			CommonTree groupCommonTree = child(tree, i);
			Query query = new Query();
			readCriteria(child(groupCommonTree, 0), query);
			queryGroup.add(query);
		}
		return queryGroup;
	}

	/**
	 * Reads a variable literal.
	 * @param tree
	 * @return
	 */
	private Object readVariableLiteral(CommonTree tree) {
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
				Object[] vars = new Object[child(tree, 0).getChildCount()];
				for (int i=0; i<vars.length; i++) {
					vars[i] = readVariableLiteral(child(child(tree, 0), i));
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
	private Object[] readVariableList(CommonTree tree) {
		assertTokenType(tree, MqlParser.VARIABLE_LIST);
		Object[] ret = new Object[tree.getChildCount()];
		for (int i=0; i<ret.length; i++) {
			ret[i] = readVariableLiteral(child(tree, i));
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

	private CommonTree child(Tree tree, int idx) {
		return CommonTree.class.cast(tree.getChild(idx));
	}

	private boolean isString(Tree tree) {
		return (tree!=null && (
			tree.getType()==MqlParser.DOUBLE_QUOTED_STRING
			|| tree.getType()==MqlParser.SINGLE_QUOTED_STRING));
	}

}
