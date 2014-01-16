package behavior.api.usecases;

public class PagingNotAvailableException extends Exception {
	private static final long serialVersionUID = 4537311840403597808L;

	public PagingNotAvailableException() {
	}
	public PagingNotAvailableException(String message) {
		super(message);
	}
}
