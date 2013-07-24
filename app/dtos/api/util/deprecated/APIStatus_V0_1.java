package dtos.api.util.deprecated;

import dtos.api.util.APIStatus;

@Deprecated
public enum APIStatus_V0_1 implements APIStatus {

	UNKNOWN_ERROR("-1", "Unknown error"), 
	SUCCESS("0", "Success"), 
	BAD_REQUEST("1", "Bad request"), 
	METHOD_NOT_FOUND("2", "Method not found"), 
	INVALID_TIMESTAMP("3", "Invalid timestamp"), 
	INVALID_USER_ID("4", "Invalid user_id");

	private String code;
	private String string;

	private APIStatus_V0_1(String code, String string) {
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
