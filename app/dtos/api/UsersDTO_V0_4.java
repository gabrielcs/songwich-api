package dtos.api;

import java.util.List;

import models.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import dtos.api.util.DataTransferObject;

// @JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("user")
public class UsersDTO_V0_4 extends DataTransferObject<Scrobble> {
	
	private String userEmail;

	// not used for input, only for output
	private String userId;

	// not used for input, only for output
	private String userAuthToken;

	public UsersDTO_V0_4() {
	}

	@Override
	public List<ValidationError> validate() {
		addValidation(validateUserEmail());
		// check for empty list and return null
		return getValidationErrors().isEmpty() ? null : getValidationErrors();
	}

	private ValidationError validateUserEmail() {
		if (userEmail == null || userEmail.isEmpty()) {
			return new ValidationError("userEmail", "userEmail is required");
		}
		
		if (!DataTransferObject.validateEmailAddress(userEmail)) {
			return new ValidationError("userEmail", "Invalid userEmail");
		}
		
		// validation sucessfull
		return null;
	}

	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail
	 *            the userEmail to set
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
	 * @param userAuthToken
	 *            the userAuthToken to set
	 */
	public void setUserAuthToken(String userAuthToken) {
		this.userAuthToken = userAuthToken;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
