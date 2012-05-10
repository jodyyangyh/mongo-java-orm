package com.googlecode.mjorm.mql;

@SuppressWarnings("serial")
public class MqlException
	extends RuntimeException {

	public MqlException(Exception exception) {
		super(exception);
	}

	public MqlException(String message) {
		super(message);
	}

	public MqlException(String message, Exception exception) {
		super(message, exception);
	}

	public MqlException(int line, int col, String near) {
		super("Error on line: "+line+" column: "+col+" near: "+near);
	}

	public MqlException(int line, int col, String near, Exception exception) {
		super("Error on line: "+line+" column: "+col+" near: "+near, exception);
	}
}
