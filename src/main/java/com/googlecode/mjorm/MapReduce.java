package com.googlecode.mjorm;

import com.mongodb.DBObject;

/**
 * For MapReducing with {@link MongoDao}.
 */
public class MapReduce {

	private String mapFunction;
	private String reduceFunction;
	private String finalizeFunction;
	private DBObject query;
	private DBObject sort;
	private Long limit;
	private String outputCollectionName;
	private Boolean keepTemp;
	private DBObject scope;
	private Boolean verbose;

	/**
	 * Creates the {@link MapReduce}.
	 * @param mapFunction the function
	 * @param reduceFunction the function
	 */
	public MapReduce(String mapFunction, String reduceFunction) {
		this.mapFunction 	= mapFunction;
		this.reduceFunction	= reduceFunction;
	}

	/**
	 * Creates the {@link MapReduce}.
	 * @param mapFunction the function
	 * @param reduceFunction the function
	 * @param finalizeFunction the function
	 */
	public MapReduce(
		String mapFunction, String reduceFunction, String finalizeFunction) {
		this.mapFunction 		= mapFunction;
		this.reduceFunction		= reduceFunction;
		this.finalizeFunction	= finalizeFunction;
	}


	/**
	 * @return the mapFunction
	 */
	public String getMapFunction() {
		return mapFunction;
	}

	/**
	 * @param mapFunction the mapFunction to set
	 */
	public void setMapFunction(String mapFunction) {
		this.mapFunction = mapFunction;
	}

	/**
	 * @return the reduceFunction
	 */
	public String getReduceFunction() {
		return reduceFunction;
	}

	/**
	 * @param reduceFunction the reduceFunction to set
	 */
	public void setReduceFunction(String reduceFunction) {
		this.reduceFunction = reduceFunction;
	}

	/**
	 * @return the finalizeFunction
	 */
	public String getFinalizeFunction() {
		return finalizeFunction;
	}

	/**
	 * @param finalizeFunction the finalizeFunction to set
	 */
	public void setFinalizeFunction(String finalizeFunction) {
		this.finalizeFunction = finalizeFunction;
	}

	/**
	 * @return the query
	 */
	public DBObject getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(DBObject query) {
		this.query = query;
	}

	/**
	 * @return the sort
	 */
	public DBObject getSort() {
		return sort;
	}

	/**
	 * @param sort the sort to set
	 */
	public void setSort(DBObject sort) {
		this.sort = sort;
	}

	/**
	 * @return the limit
	 */
	public Long getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return the outputCollectionName
	 */
	public String getOutputCollectionName() {
		return outputCollectionName;
	}

	/**
	 * @param outputCollectionName the outputCollectionName to set
	 */
	public void setOutputCollectionName(String outputCollectionName) {
		this.outputCollectionName = outputCollectionName;
	}

	/**
	 * @return the keepTemp
	 */
	public Boolean getKeepTemp() {
		return keepTemp;
	}

	/**
	 * @param keepTemp the keepTemp to set
	 */
	public void setKeepTemp(Boolean keepTemp) {
		this.keepTemp = keepTemp;
	}

	/**
	 * @return the scope
	 */
	public DBObject getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(DBObject scope) {
		this.scope = scope;
	}

	/**
	 * @return the verbose
	 */
	public Boolean getVerbose() {
		return verbose;
	}

	/**
	 * @param verbose the verbose to set
	 */
	public void setVerbose(Boolean verbose) {
		this.verbose = verbose;
	}

}
