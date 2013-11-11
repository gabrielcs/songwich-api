package views.api.scrobbles;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DataTransferObject;
import views.api.stations.RadioStationsListDTO_V0_4;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("user")
public class UserUpdateDTO_V0_4 extends DataTransferObject {
	
	private String userEmail;

	private String name;

	// not used for input, only for output
	private String userId;

	// not used for input, only for output
	private String userAuthToken;
	
	// not used for input, only for output
	private RadioStationsListDTO_V0_4 scrobblerStations;

	public RadioStationsListDTO_V0_4 getScrobblerStations() {
		return scrobblerStations;
	}

	public void setScrobblerStations(RadioStationsListDTO_V0_4 scrobblerStations) {
		this.scrobblerStations = scrobblerStations;
	}

	public UserUpdateDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		addValidation(validateUserEmail());
	}

	private ValidationError validateUserEmail() {
		return validateEmailAddress("userEmail", userEmail);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
