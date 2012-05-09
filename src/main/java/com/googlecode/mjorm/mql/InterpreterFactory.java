package com.googlecode.mjorm.mql;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.mjorm.ObjectMapper;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.BetweenCriterion;
import com.googlecode.mjorm.query.criteria.ElemMatchCriterion;
import com.googlecode.mjorm.query.criteria.ExistsCriterion;
import com.googlecode.mjorm.query.criteria.ModCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion;
import com.googlecode.mjorm.query.criteria.SizeCriterion;
import com.googlecode.mjorm.query.criteria.TypeCriterion;
import com.googlecode.mjorm.query.criteria.SimpleCriterion.Operator;
import com.mongodb.DB;

public class InterpreterFactory {

	private static InterpreterFactory DEFAULT_INSTANCE = null;

	private Map<String, MqlFunction> documentFunctions 	= new HashMap<String, MqlFunction>();
	private Map<String, MqlFunction> fieldFunctions = new HashMap<String, MqlFunction>();

	/**
	 * Returns the default {@link InterpreterFactory} instance.
	 * @return
	 */
	public static InterpreterFactory getDefaultInstance() {
		if (DEFAULT_INSTANCE==null) {
			DEFAULT_INSTANCE = newInstance();
			DEFAULT_INSTANCE.registerDefaultFunctions();
		}
		return DEFAULT_INSTANCE;
	}

	/**
	 * Creates a new {@link InterpreterFactory}.
	 * @return
	 */
	public static InterpreterFactory newInstance() {
		return new InterpreterFactory();
	}

	/**
	 * Creates an {@link Interpreter}.
	 * @param mongo
	 * @param objectMapper
	 * @return
	 */
	public Interpreter create(DB db, ObjectMapper objectMapper) {
		InterpreterImpl ret = new InterpreterImpl(db, objectMapper);
		for (Entry<String, MqlFunction> entry : documentFunctions.entrySet()) {
			ret.registerDocumentFunction(entry.getValue());
		}
		for (Entry<String, MqlFunction> entry : fieldFunctions.entrySet()) {
			ret.registerFieldFunction(entry.getValue());
		}
		return ret;
	}

	/**
	 * Clears field functions.
	 */
	public void clearFieldFunctions() {
		fieldFunctions.clear();
	}

	/**
	 * Clears document functions.
	 */
	public void clearDocumentFunctions() {
		documentFunctions.clear();
	}

	/**
	 * Clears all functions.
	 */
	public void clearFunctions() {
		clearFieldFunctions();
		clearDocumentFunctions();
	}

	/**
	 * Removes a specific document function.
	 * @param name
	 */
	public void removeDocumentFunction(String name) {
		documentFunctions.remove(name.trim().toLowerCase());
	}

	/**
	 * Removes a specific field function.
	 * @param name
	 */
	public void removeFieldFunction(String name) {
		fieldFunctions.remove(name.trim().toLowerCase());
	}

	/**
	 * Registers a field function.
	 * @param function
	 */
	public void registerFieldFunction(MqlFunction function) {
		fieldFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	/**
	 * Registers a document function.
	 * @param function
	 */
	public void registerDocumentFunction(MqlFunction function) {
		documentFunctions.put(function.getName().trim().toLowerCase(), function);
	}

	/**
	 * Registers a default function set.
	 */
	public void registerDefaultFunctions() {
		
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
}
