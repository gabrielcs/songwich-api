package views.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

/*
 * http://martinfowler.com/eaaCatalog/dataTransferObject.html
 */
public abstract class DataTransferObject<T> {

	private final static Pattern EMAIL_REGEX = Pattern
			.compile("\\b[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\b");
	
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

	public static String errorsAsString(
			Map<String, List<ValidationError>> errorsMap) {
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
	
	/*
	 * Returns true if the email address is valid or false otherwise
	 * 
	 * Source: https://github.com/playframework/playframework/blob/master/framework/src/play-java/src/main/java/play/data/validation/Constraints.java
	 */
	public static boolean validateEmailAddress(String emailAddress) {
		return EMAIL_REGEX.matcher(emailAddress).matches();
	}
}
