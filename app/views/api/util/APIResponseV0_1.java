package views.api.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class APIResponseV0_1 {
	
	private Status status;
	private String message;
	private Map<String,JsonNode> content;
	
	public APIResponseV0_1(Status status, String message) {
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
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	
}