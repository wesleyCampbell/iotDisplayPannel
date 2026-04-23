package dataaccess.exception;

public class ObjectNotFoundException extends DataAccessException {
	public ObjectNotFoundException(String msg) {
		super(msg);
	}
	public ObjectNotFoundException(String msg, Throwable th) {
		super(msg, th);
	}
}	
