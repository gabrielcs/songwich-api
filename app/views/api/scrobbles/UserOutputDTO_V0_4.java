package views.api.scrobbles;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DataTransferObject;
import views.api.stations.RadioStationOutputDTO_V0_4;
import views.api.subscriptions.SubscriptionDTO_V0_4;

//@JsonInclude(Include.NON_NULL)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeName("user")
public class UserOutputDTO_V0_4 extends DataTransferObject {

	private String userEmail;

	private String name;

	private String imageUrl;

	private String shortBio;

	private String userId;

	private String verified;

	private String userAuthToken;

	private List<RadioStationOutputDTO_V0_4> scrobblerStations;

	private List<SubscriptionDTO_V0_4> activeStationSubscriptions;

	public UserOutputDTO_V0_4() {
		// make sure they're not null
		setScrobblerStations(new ArrayList<RadioStationOutputDTO_V0_4>(1));
		setActiveStationSubscriptions(new ArrayList<SubscriptionDTO_V0_4>(1));
	}

	public UserOutputDTO_V0_4(UserInputDTO_V0_4 userInputDTO) {
		setUserEmail(userInputDTO.getUserEmail());
		setName(userInputDTO.getName());
		setImageUrl(userInputDTO.getImageUrl());
		setShortBio(userInputDTO.getShortBio());

		// make sure they're not null
		setScrobblerStations(new ArrayList<RadioStationOutputDTO_V0_4>(1));
		setActiveStationSubscriptions(new ArrayList<SubscriptionDTO_V0_4>(1));
	}

	public UserOutputDTO_V0_4(UserUpdateInputDTO_V0_4 userUpdateInputDTO) {
		setUserEmail(userUpdateInputDTO.getUserEmail());
		setName(userUpdateInputDTO.getName());
		setImageUrl(userUpdateInputDTO.getImageUrl());
		setShortBio(userUpdateInputDTO.getShortBio());

		// make sure they're not null
		setScrobblerStations(new ArrayList<RadioStationOutputDTO_V0_4>(1));
		setActiveStationSubscriptions(new ArrayList<SubscriptionDTO_V0_4>(1));
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

	public List<RadioStationOutputDTO_V0_4> getScrobblerStations() {
		return scrobblerStations;
	}

	/** Empty lists will be included in the output JSON */
	public void setScrobblerStations(
			List<RadioStationOutputDTO_V0_4> scrobblerStationsDTO) {

		if (scrobblerStationsDTO != null) {
			this.scrobblerStations = scrobblerStationsDTO;
		}
	}

	/** Empty lists will be included in the output JSON */
	public void setActiveStationSubscriptions(
			List<SubscriptionDTO_V0_4> activeStationSubscriptions) {

		if (activeStationSubscriptions != null) {
			this.activeStationSubscriptions = activeStationSubscriptions;
		}
	}

	public List<SubscriptionDTO_V0_4> getActiveStationSubscriptions() {
		return activeStationSubscriptions;
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

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	@Override
	public String toString() {
		return "UserOutputDTO_V0_4 [userEmail=" + userEmail + ", name=" + name
				+ ", imageUrl=" + imageUrl + ", shortBio=" + shortBio
				+ ", userId=" + userId + ", verified=" + verified
				+ ", userAuthToken=" + userAuthToken + ", scrobblerStations="
				+ scrobblerStations + ", activeStationSubscriptions="
				+ activeStationSubscriptions + "]";
	}
}
