package views.api.scrobbles;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;
import views.api.stations.RadioStationDTO_V0_4;
import views.api.subscriptions.SubscriptionDTO_V0_4;

// we want to always have scrobblerStations and activeStationSubscriptions
//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName("user")
public class DetailedUserDTO_V0_4 extends DataTransferObject {

	private String userEmail;

	private String name;

	private String imageUrl;

	private String shortBio;

	// not used for input, only for output
	private String userId;

	// not used for input, only for output
	private String userAuthToken;

	// not used for input, only for output
	// creates an empty list so the parameter is always present
	private List<RadioStationDTO_V0_4> scrobblerStations = new ArrayList<RadioStationDTO_V0_4>();

	// not used for input, only for output
	// creates an empty list so the parameter is always present
	private List<SubscriptionDTO_V0_4> activeStationSubscriptions = new ArrayList<SubscriptionDTO_V0_4>();

	public DetailedUserDTO_V0_4() {
		setValidator(this.new UserDTOValidator());
	}

	public List<RadioStationDTO_V0_4> getScrobblerStations() {
		return scrobblerStations;
	}

	public void setScrobblerStations(
			List<RadioStationDTO_V0_4> scrobblerStations) {

		this.scrobblerStations = scrobblerStations;
	}

	public void setActiveStationSubscriptions(
			List<SubscriptionDTO_V0_4> activeStationSubscriptions) {
		this.activeStationSubscriptions = activeStationSubscriptions;
	}

	public List<SubscriptionDTO_V0_4> getActiveStationSubscriptions() {
		return activeStationSubscriptions;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getShortBio() {
		return shortBio;
	}

	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
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

	@Override
	public String toString() {
		return "UserDTO_V0_4 [userEmail=" + userEmail + ", name=" + name
				+ ", imageUrl=" + imageUrl + ", shortBio=" + shortBio
				+ ", userId=" + userId + ", userAuthToken=" + userAuthToken
				+ ", scrobblerStations=" + scrobblerStations
				+ ", activeStationSubscriptions=" + activeStationSubscriptions
				+ "]";
	}

	public class UserDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateUserEmail(), validateImageUrl());
		}

		private ValidationError validateUserEmail() {
			return validateRequiredEmailAddress("userEmail", userEmail);
		}

		private ValidationError validateImageUrl() {
			return validateImageUrl("imageUrl", imageUrl);
		}
	}
}
