package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.Query;
import com.googlecode.mjorm.query.criteria.Criterion;

public interface MqlGroupFunction {

	Criterion createCriterionForQuery(Query query);

	Criterion createCriterionForVariables(Object[] values);

	Criterion createCriterionForNoArguments();

}
