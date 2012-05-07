package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;

public class MqlDocumentFunctionImpl
	extends AbstractMqlFunction
	implements MqlDocumentFunction {

	private String groupName;

	/**
	 * Method for creating a document function that only
	 * accepts a query and returns it as the criterion.
	 * @param groupName the group name
	 * @return the function
	 */
	public static MqlDocumentFunction createDocumentQueryFunction(final String groupName) {
		return new MqlDocumentFunctionImpl() {
			{
				setAllowQuery(true);
				setGroupName(groupName);
			}
			@Override
			public Query doCreate(Query query) {
				return query;
			}
		};
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	protected void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	protected Query doCreate(Object[] values) {
		throw new IllegalArgumentException(
			"MqlDocumentFunction doesn't implement doCreate(Object[])");
	}

	protected Query doCreate() {
		throw new IllegalArgumentException(
			"MqlDocumentFunction doesn't implement doCreate()");
	}

	protected Query doCreate(Query query) {
		throw new IllegalArgumentException(
			"MqlDocumentFunction doesn't implement doCreate(Query)");
	}

	public Query createForQuery(Query query) {
		if (groupName==null) {
			throw new IllegalArgumentException("groupName must be specified");
		}
		if (!allowQuery) {
			throw new IllegalArgumentException(
				"MqlDocumentFunction doesn't take Query as an argument");
		}
		return doCreate(query);
	}

	public Query createForArguments(Object[] values) {
		if (groupName==null) {
			throw new IllegalArgumentException("groupName must be specified");
		}
		if (maxArgs!=-1) {
			assertMaximumArgumentLength(values, maxArgs);
		}
		if (minArgs!=-1) {
			assertMinimumArgumentLength(values, minArgs);
		}
		if (exactArgs!=-1) {
			assertArgumentLength(values, exactArgs);
		}
		if (types.length==1) {
			assertArgumentTypes(values, types[0]);
		}
		if (types.length>1) {
			assertArgumentTypes(values, types);
		}
		return doCreate(values);
	}

	public Query createForNoArguments() {
		if (groupName==null) {
			throw new IllegalArgumentException("groupName must be specified");
		}
		return doCreate();
	}
}
