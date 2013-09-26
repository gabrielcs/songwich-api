package views.api.scrobbles;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DataTransferObject;

// @JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("appDeveloper")
public class AppDevelopersDTO_V0_4 extends DataTransferObject<Scrobble> {
	
	private String devEmail;

	private String name;

	private String appName;

	public AppDevelopersDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		addValidation(validateDevEmail(), validateName(), validateAppName());
	}

	private ValidationError validateDevEmail() {
		return validateRequiredEmailAddress("devEmail", devEmail);
	}
	
	private ValidationError validateName() {
		return validateRequiredProperty("name", name);
	}
	
	private ValidationError validateAppName() {
		return validateRequiredProperty("appName", appName);
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
