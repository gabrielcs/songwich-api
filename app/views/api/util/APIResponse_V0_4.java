package views.api.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIResponse_V0_4 {
	@JsonIgnore
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private String status;
	private String message;
	
	public APIResponse_V0_4(Status status, String message) {
		setStatus(status);
		setMessage(message);
	}
	
	public JsonNode toJson() {
		return objectMapper.valueToTree(this);
	}
	
	public void setStatus(Status status) {
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
	
	/**
	 * @return the objectMapper
	 */
	protected static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	protected static void setObjectMapper(ObjectMapper objectMapper) {
		APIResponse_V0_4.objectMapper = objectMapper;
	}
}
;