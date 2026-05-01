package dataaccess.exception;

public class ForeignConstraintException extends DataAccessException {
	public ForeignConstraintException(String msg) {
		super(msg);
	}
	public ForeignConstraintException(String msg, Throwable th) {
		super(msg, th);
	}
}
