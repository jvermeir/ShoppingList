package shopv1;

public class OperationNotSupportedException extends RuntimeException {

	public OperationNotSupportedException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;	
}
