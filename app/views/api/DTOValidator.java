package views.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

public abstract class DTOValidator {
	@JsonIgnore
	private List<ValidationError> validationErrors;

	public DTOValidator() {
		validationErrors = new ArrayList<ValidationError>();
	}

	public abstract void addValidation();

	// method called by Play!
	public List<ValidationError> validate() {
		addValidation();
		// check for empty list and return null
		return getValidationErrors().isEmpty() ? null : getValidationErrors();
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

	protected static ValidationError validateRequiredProperty(
			String propertyName, String property) {
		if (property == null || property.isEmpty()) {
			return new ValidationError(propertyName, propertyName
					+ " is required");
		} else {
			return null;
		}
	}

	protected static ValidationError validateIfNonNullThenNonEmptyProperty(
			String propertyName, String property) {

		if (property != null && property.isEmpty()) {
			return new ValidationError(propertyName, propertyName
					+ " cannot be empty");
		} else {
			return null;
		}
	}

	public static ValidationError validateEmailAddress(String propertyName,
			String emailAddress) {
		
		if (emailAddress == null || emailAddress.isEmpty()) {
			// field not present
			return null;
		}

		EmailValidator emailValidator = EmailValidator.getInstance();
		if (!emailValidator.isValid(emailAddress)) {
			return new ValidationError(propertyName, "Invalid " + propertyName);
		} else {
			// validation successful
			return null;
		}
	}

	protected static ValidationError validateRequiredEmailAddress(
			String propertyName, String emailAddress) {
		ValidationError validationError = validateRequiredProperty(
				propertyName, emailAddress);
		if (validationError != null) {
			return validationError;
		} else {
			return validationError = validateEmailAddress(propertyName,
					emailAddress);
		}
	}

	protected static ValidationError validateRequiredObjectId(
			String propertyName, String objectId) {
		ValidationError validationError = validateRequiredProperty(
				propertyName, objectId);
		if (validationError != null) {
			return validationError;
		} else {
			return validationError = validateObjectId(propertyName, objectId);
		}
	}

	protected static ValidationError validateObjectId(String propertyName,
			String objectId) {
		
		if (objectId == null) {
			// not a required property
			return null;
		}
		
		if (!ObjectId.isValid(objectId)) {
			return new ValidationError(propertyName, "Invalid " + propertyName);
		} else {
			// validation successful
			return null;
		}
	}

	protected static ValidationError validateBoolean(String propertyName,
			String value) {
		if (value == null || value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("false")) {
			return null;
		} else {
			return new ValidationError(propertyName, propertyName
					+ " should be either true or false");
		}
	}

	protected static ValidationError validateRequiredNonEmptyArray(
			String arrayName, List<String> scrobblerIds) {
		if (scrobblerIds == null || scrobblerIds.isEmpty()) {
			return new ValidationError(arrayName, arrayName + " is required");
		} else {
			for (String value : scrobblerIds) {
				if (!value.isEmpty()) {
					return null;
				}
			}
			// no value was non-empty
			return new ValidationError(arrayName, arrayName + " is required");
		}
	}

	protected static ValidationError validateRequiredNonEmptyObjectIdArray(
			String arrayName, String arrayItemName, List<String> scrobblerIds) {
		ValidationError result = validateRequiredNonEmptyArray(arrayName, scrobblerIds);
		if (result != null) {
			return result;
		}

		return validateObjectIdArray(arrayName, arrayItemName, scrobblerIds);
	}

	protected static ValidationError validateObjectIdArray(String arrayName,
			String arrayItemName, List<String> scrobblerIds) {
		if (scrobblerIds != null) {
			for (String id : scrobblerIds) {
				if (!ObjectId.isValid(id)) {
					return new ValidationError(arrayName, String.format(
							"Invalid %s: %s", arrayItemName, id));
				}
			}
		}
		return null;
	}

	protected static ValidationError validateUrl(String propertyName, String url) {
		if (url == null || url.isEmpty()) {
			// field not present
			return null;
		}
		
		UrlValidator urlValidator = UrlValidator.getInstance();
		if (!urlValidator.isValid(url)) {
			return new ValidationError(propertyName, "Invalid " + propertyName);
		} else {
			// validation successful
			return null;
		}
	}

	protected static ValidationError validateImageUrl(String propertyName,
			String imageUrl) {
		
		if (imageUrl == null || imageUrl.isEmpty()) {
			// field not present
			return null;
		}

		ValidationError validationError = validateUrl(propertyName, imageUrl);
		if (validationError != null) {
			return validationError;
		}

		// TODO: should we validate it's the URL of an accepted image format?
		return null;
	}

}
