package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;

public interface MqlDocumentFunction {

	Query createForQuery(Query query);

	Query createForArguments(Object[] values);

	Query createForNoArguments();

	String getGroupName();
	
}
