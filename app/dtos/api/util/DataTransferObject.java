package dtos.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

/*
 * http://martinfowler.com/eaaCatalog/dataTransferObject.html
 */
public abstract class DataTransferObject<T> {

	// method called by Play!
	public abstract List<ValidationError> validate();

	@JsonIgnore
	private List<ValidationError> validationErrors;

	public DataTransferObject() {
		validationErrors = new ArrayList<ValidationError>();
	}

	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	public void setValidationErrors(List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}

	protected void addValidation(ValidationError... validationErrors) {
		for (ValidationError validationError : validationErrors) {
			if (validationError != null) {
				this.validationErrors.add(validationError);
			}
		}		
	}
	
	public static String errorsAsString(Map<String, List<ValidationError>> errorsMap) {
		Set<String> keySet = errorsMap.keySet();
		List<ValidationError> errors;
		StringBuilder errorsString = new StringBuilder();
		for (String key : keySet) {
			errors = errorsMap.get(key);
			for (ValidationError error : errors) {
				if (errorsString.length() != 0) {
					errorsString.append(" ");
				}
				errorsString.append(error.message()).append(".");
			}
		}
		return errorsString.toString();
	}
}
