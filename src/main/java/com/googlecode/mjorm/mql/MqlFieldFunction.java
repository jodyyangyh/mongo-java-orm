package com.googlecode.mjorm.mql;

import com.googlecode.mjorm.query.criteria.Criterion;

public interface MqlFieldFunction {

	Criterion createCriterion(Object[] values);

}
