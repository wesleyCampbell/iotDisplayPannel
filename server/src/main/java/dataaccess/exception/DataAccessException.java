package dataaccess.exception;

public class DataAccessException extends Exception {
	public DataAccessException(String msg) {
		super(msg);
	}
	public DataAccessException(String msg, Throwable th) {
		super(msg, th);
	}
}
