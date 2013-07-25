package dtos.api.util.deprecated;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import dtos.api.util.APIStatus;

import play.libs.Json;

@Deprecated
public class APIResponse_V0_1 {
	
	private APIStatus status;
	private String message;
	private Map<String,JsonNode> content;
	
	public APIResponse_V0_1(APIStatus status, String message) {
		content = new LinkedHashMap<String,JsonNode>();
		setStatus(status);
		setMessage(message);
	}
	
	public JsonNode toJson() {
		ObjectNode result = Json.newObject();
		result.put("status", status.getCode());
		result.put("message", message);
		result.putAll(content);
		return Json.toJson(result);
	}
	
	public void put(String fieldName, JsonNode value) {
		content.put(fieldName, value);
	}
	
	public void setStatus(APIStatus status) {
		this.status = status;
	}
	
	public APIStatus getStatus() {
		return status;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	
}