package views.api.scrobbles;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("appDeveloper")
public class AppDevelopersDTO_V0_4 extends DataTransferObject {
	
	private String devEmail;

	private String devName;

	private String appName;

	public AppDevelopersDTO_V0_4() {
		setValidator(this.new AppDevelopersDTOValidator());
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

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}
	
	public class AppDevelopersDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateDevEmail(), validateDevName(), validateAppName());
		}

		private ValidationError validateDevEmail() {
			return validateRequiredEmailAddress("devEmail", devEmail);
		}
		
		private ValidationError validateDevName() {
			return validateRequiredProperty("devName", devName);
		}
		
		private ValidationError validateAppName() {
			return validateRequiredProperty("appName", appName);
		}
	}
}
