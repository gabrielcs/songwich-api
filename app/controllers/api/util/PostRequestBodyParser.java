package controllers.api.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Http.MultipartFormData;



public class PostRequestBodyParser {

	//@BodyParser.Of(BodyParser.Json.class)
	public static Map<String, String> parse(Class<?> proxyClass,
			boolean ignoreUnexpectedData) throws SongwichAPIException {

		Map<String, String> data = new HashMap<String, String>();
		JsonNode jsonData;
		
		try {
			jsonData = Context.current().request().body().asJson();
		} catch(Throwable t) {
			Logger.debug("RuntimeException at body().asJson()");
			return data;  // returns an empty map
		}
		
		// check if there's data to extract
		if (jsonData == null) {
			Logger.debug("jsonData is null");
			return data; // returns an empty map
		}
		
		// discover what data needs to be extracted
		List<Field> fields = Arrays.asList(proxyClass.getDeclaredFields());
		List<String> proxyFieldNames = new ArrayList<String>(fields.size());
		for (Field field : fields) {
			proxyFieldNames.add(field.getName());
		}

		// check if there's unexpected data
		if (!ignoreUnexpectedData) {
			Iterator<String> dataFieldNames = jsonData.getFieldNames();
			String fieldName;
			while (dataFieldNames.hasNext()) {
				fieldName = dataFieldNames.next();
				if (!proxyFieldNames.contains(fieldName)) {
					throw new SongwichAPIException("Unexpected data: "
							+ fieldName,
							views.api.util.Status.BAD_REQUEST);
				}
			}
		}

		// extract the data
		for (String proxyFieldName : proxyFieldNames) {
			data.put(proxyFieldName, jsonData.findPath(proxyFieldName)
					.getTextValue());
		}
		return data;
	}

	// Read POST data as Multipart Form Data
	public static Map<String, String> readData(Class<?> proxyClass,
			boolean ignoreUnexpectedData) throws SongwichAPIException {

		Map<String, String> data = new HashMap<String, String>();

		// check if there's data to extract
		MultipartFormData multipartFormData = Context.current().request()
				.body().asMultipartFormData();

		if (multipartFormData == null) {
			return data; // returns an empty map
		}

		// discover what data has to be extracted
		List<Field> fields = Arrays.asList(proxyClass.getDeclaredFields());
		List<String> fieldNames = new ArrayList<String>(fields.size());
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}

		// reference the form url data to be extracted
		Map<String, String[]> formUrlData = multipartFormData
				.asFormUrlEncoded();

		// check if there's unexpected data
		if (!ignoreUnexpectedData) {
			Set<String> formUrlDataKeySet = formUrlData.keySet();
			for (String key : formUrlDataKeySet) {
				if (!fieldNames.contains(key)) {
					throw new SongwichAPIException("Unexpected data: " + key,
							views.api.util.Status.BAD_REQUEST);

				}
			}
		}

		// extract the data
		String[] dataArray;
		for (String fieldName : fieldNames) {
			dataArray = formUrlData.get(fieldName);
			if (dataArray == null) {
				data.put(fieldName, null);
			} else if (dataArray.length != 1) {
				throw new SongwichAPIException("Unexpected multiple data: "
						+ fieldName, views.api.util.Status.BAD_REQUEST);
			} else {
				data.put(fieldName, dataArray[0]);
			}
		}
		return data;
	}
}