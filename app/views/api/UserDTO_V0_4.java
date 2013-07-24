package views.api;

import models.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import views.api.util.DataTransferObject;

// @JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("user")
public class UserDTO_V0_4 extends DataTransferObject<Scrobble> {
	@Email
	@Required
	private String userEmail;

	private String userAuthToken;
	
	public UserDTO_V0_4() {
	}
	
	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @return the userAuthToken
	 */
	public String getUserAuthToken() {
		return userAuthToken;
	}

	/**
	 * @param userAuthToken the userAuthToken to set
	 */
	public void setUserAuthToken(String userAuthToken) {
		this.userAuthToken = userAuthToken;
	}
	
}
