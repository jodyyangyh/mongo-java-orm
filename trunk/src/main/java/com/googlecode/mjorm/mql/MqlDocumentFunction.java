package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;

public interface MqlDocumentFunction {

	Query createForQuery(Query query);

	Query createForQueryGroup(QueryGroup queryGroup);

	Query createForArguments(Object[] values);

	Query createForNoArguments();
	
}
