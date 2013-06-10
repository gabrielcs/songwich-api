package controllers.api.util;

public enum Status {
	UNKNOWN_ERROR(-1),
	SUCCESS(0), 
	BAD_REQUEST(1), 
	METHOD_NOT_FOUND(2),
	INVALID_TIMESTAMP(3);

	private int code;

	private Status(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
