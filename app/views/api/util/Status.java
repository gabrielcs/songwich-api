package views.api.util;

public enum Status {

	UNKNOWN_ERROR("-1", "Unknown error"), 
	SUCCESS("0", "Success"), 
	BAD_REQUEST("1", "Bad request"), 
	METHOD_NOT_FOUND("2", "Method not found"), 
	INVALID_TIMESTAMP("3", "Invalid timestamp"), 
	INVALID_USER_ID("4", "Invalid user_id");

	private String code;
	private String string;

	private Status(String code, String string) {
		this.code = code;
		this.string = string;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return string;
	}
}
