package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.criteria.Criterion;

public interface MqlFunction {

	Criterion createForQuery(Query query);

	Criterion createForArguments(Object[] values);

	Criterion createForNoArguments();

}
