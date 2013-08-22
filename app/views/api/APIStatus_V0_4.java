package views.api;

public enum APIStatus_V0_4 implements APIStatus {

	UNKNOWN_ERROR("-1", "Unknown error"),
	SUCCESS("0", "Success"),
	BAD_REQUEST("1", "Bad request"),
	INVALID_DEV_AUTH_TOKEN("2", "Invalid X-Songwich.devAuthToken"),
	INVALID_USER_AUTH_TOKEN("3", "Invalid X-Songwich.userAuthToken"),
	UNAUTHORIZED("4", "Unauthorized"), 
	INVALID_PARAMETER("5", "Invalid parameter"); 

	private String code;
	private String string;

	private APIStatus_V0_4(String code, String string) {
		this.code = code;
		this.string = string;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return string;
	}
}
