package views.api;

import java.util.List;

import models.api.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;


import play.data.validation.ValidationError;
import views.api.util.DataTransferObject;

// @JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("appDeveloper")
public class AppDevelopersDTO extends DataTransferObject<Scrobble> {
	
	private String devEmail;

	private String name;

	private String appName;

	public AppDevelopersDTO() {
	}

	@Override
	public List<ValidationError> validate() {
		addValidation(validateDevEmail(), validateName(), validateAppName());
		// check for empty list and return null
		return getValidationErrors().isEmpty() ? null : getValidationErrors();
	}

	private ValidationError validateDevEmail() {
		if (devEmail == null || devEmail.isEmpty()) {
			return new ValidationError("devEmail", "devEmail is required");
		}
		
		if (!DataTransferObject.validateEmailAddress(devEmail)) {
			return new ValidationError("devEmail", "Invalid devEmail");
		}
		
		// validation sucessfull
		return null;
	}
	
	private ValidationError validateName() {
		if (name == null || name.isEmpty()) {
			return new ValidationError("name", "name is required");
		}
		
		// validation sucessfull
		return null;
	}
	
	private ValidationError validateAppName() {
		if (appName == null || appName.isEmpty()) {
			return new ValidationError("appName", "appName is required");
		}
		
		// validation sucessfull
		return null;
	}

	/**
	 * @return the devEmail
	 */
	public String getDevEmail() {
		return devEmail;
	}

	/**
	 * @param devEmail
	 *            the devEmail to set
	 */
	public void setDevEmail(String devEmail) {
		this.devEmail = devEmail;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
