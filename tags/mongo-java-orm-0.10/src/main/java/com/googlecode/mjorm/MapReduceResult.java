package com.googlecode.mjorm;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * The result of a map reduce operation.
 */
public class MapReduceResult {

	private DBCollection resultCollection;
	private Long numObjectsScanned;
	private Long numEmits;
	private Long numObjectsOutput;
	private Long timeMillis;

	/**
	 * Creates the {@link MapReduceResult}.
	 * @param db the database that it was run on
	 * @param result the {@link CommandResult}
	 */
	public MapReduceResult(DB db, CommandResult result) {
		this.resultCollection = db.getCollection(result.getString("result"));
		if (result.containsField("counts")) {
			DBObject counts = (DBObject)result.get("counts");
			this.numObjectsScanned	= new Long(counts.get("input").toString());
			this.numEmits 			= new Long(counts.get("emit").toString());
			this.numObjectsOutput 	= new Long(counts.get("output").toString());
		}
		if (result.containsField("timeMillis")) {
			this.timeMillis = new Long(result.get("timeMillis").toString());
		}
	}

	/**
	 * @return the resultCollection
	 */
	public DBCollection getResultCollection() {
		return resultCollection;
	}

	/**
	 * @return the numObjectsScanned
	 */
	public Long getNumObjectsScanned() {
		return numObjectsScanned;
	}

	/**
	 * @return the numEmits
	 */
	public Long getNumEmits() {
		return numEmits;
	}

	/**
	 * @return the numObjectsOutput
	 */
	public Long getNumObjectsOutput() {
		return numObjectsOutput;
	}

	/**
	 * @return the timeMillis
	 */
	public Long getTimeMillis() {
		return timeMillis;
	}

}
