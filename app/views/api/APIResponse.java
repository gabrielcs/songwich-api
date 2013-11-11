package views.api;

import org.codehaus.jackson.map.annotate.JsonSerialize;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class APIResponse {

	private String status;
	private String message;
	private DataTransferObject result;

	public APIResponse(APIStatus status, String message) {
		setStatus(status);
		setMessage(message);
	}

	public APIResponse(APIStatus status, String message,
			DataTransferObject result) {

		setStatus(status);
		setMessage(message);
		setResult(result);
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

	public DataTransferObject getResult() {
		return result;
	}

	public void setResult(DataTransferObject result) {
		this.result = result;
	}
};
