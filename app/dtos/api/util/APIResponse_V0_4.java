package dtos.api.util;

public class APIResponse_V0_4 {

	private String status;
	private String message;

	public APIResponse_V0_4(APIStatus status, String message) {
		setStatus(status);
		setMessage(message);
	}

	public void setStatus(APIStatus status) {
		this.status = status.getCode();
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
};
