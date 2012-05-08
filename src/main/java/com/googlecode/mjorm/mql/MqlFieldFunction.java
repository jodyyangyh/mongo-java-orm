package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.QueryGroup;
import com.googlecode.mjorm.query.criteria.Criterion;

public interface MqlFieldFunction {

	Criterion createForQuery(Query query);

	Criterion createForQueryGroup(QueryGroup queryGroup);

	Criterion createForArguments(Object[] values);

	Criterion createForNoArguments();

}
