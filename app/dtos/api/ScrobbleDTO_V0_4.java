package dtos.api;

import java.util.GregorianCalendar;

import models.Scrobble;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import controllers.api.util.SongwichAPIException;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.DataTransferObject;

// @JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("scrobble")
public class ScrobbleDTO_V0_4 extends DataTransferObject<Scrobble> {
	@JsonProperty("user")
	@Email
	private String userEmail;

	@Required
	private String trackTitle;

	@Required
	private String artistName;

	private String chosenByUser;

	private String service;

	// 01-Jan-2002
	// @Min(1012528800000L)
	private String timestamp;

	public ScrobbleDTO_V0_4() {
		// sets default value for timestamp
		timestamp = Long.toString(System.currentTimeMillis());
	}

	/*
	 * public ScrobbleDTO_V0_4(String userEmail, String trackTitle, String
	 * artistName, String chosenByUser, String service, String timestamp) throws
	 * SongwichAPIException {
	 * 
	 * setUserEmail(userEmail); setTrackTitle(trackTitle);
	 * setArtistName(artistName); setChosenByUser(chosenByUser);
	 * setService(service); setTimestamp(timestamp); }
	 */

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String user_id) throws SongwichAPIException {
		if (user_id == null || user_id.isEmpty()) {
			throw new SongwichAPIException(
					"Missing parameter: user_auth_token",
					APIStatus_V0_4.BAD_REQUEST);
		}

		// validateUserId(user_id);
		this.userEmail = user_id;
	}

	// check if it's a string and delegates further validation
	private void validateUserId(String user_id) throws SongwichAPIException {
		try {
			Long userIdLong = Long.parseLong(user_id);
			validateUserId(userIdLong);
		} catch (NumberFormatException e) {
			throw new SongwichAPIException("Invalid user_auth_token",
					APIStatus_V0_4.INVALID_USER_AUTH_TOKEN);
		}
	}

	private void validateUserId(Long user_id) throws SongwichAPIException {
		// TODO: authenticate userAuthToken
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String track_title) throws SongwichAPIException {
		if (track_title == null || track_title.isEmpty()) {
			throw new SongwichAPIException("Missing parameter: trackTitle",
					APIStatus_V0_4.BAD_REQUEST);
		}

		this.trackTitle = track_title;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artist_name) throws SongwichAPIException {
		if (artist_name == null || artist_name.isEmpty()) {
			throw new SongwichAPIException("Missing parameter: artistName",
					APIStatus_V0_4.BAD_REQUEST);
		}

		this.artistName = artist_name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	/**
	 * Timestamp is the difference, measured in milliseconds, between the
	 * current time and midnight, January 1, 1970 UTC.
	 * 
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Timestamp is the difference, measured in milliseconds, between the
	 * current time and midnight, January 1, 1970 UTC.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(String timestamp) throws SongwichAPIException {
		if (timestamp == null || timestamp.isEmpty()) {
			this.timestamp = Long.toString(System.currentTimeMillis());
		} else {
			validateTimestamp(timestamp);
			this.timestamp = timestamp;
		}
	}

	// convert to Long and delegate further validation
	private void validateTimestamp(String timestampString)
			throws SongwichAPIException {
		try {
			Long timestampNumber = Long.parseLong(timestampString);
			validateTimestamp(timestampNumber);
		} catch (NumberFormatException e) {
			throw new SongwichAPIException(
					"Timestamp is not an integer number",
					APIStatus_V0_4.INVALID_TIMESTAMP);
		}
	}

	// check if it's not in the future or if it's not too old
	private void validateTimestamp(Long timestampNumber)
			throws SongwichAPIException {
		if (timestampNumber > System.currentTimeMillis()) {
			throw new SongwichAPIException("Timestamp cannot be in the future",
					APIStatus_V0_4.INVALID_TIMESTAMP);
		} else if (timestampNumber < new GregorianCalendar(2002, 1, 1)
				.getTimeInMillis()) {
			// it's older than scrobbling itself (2002)
			throw new SongwichAPIException("Timestamp is too old",
					APIStatus_V0_4.INVALID_TIMESTAMP);
		}
	}

	/**
	 * @return the chosenByUser
	 */
	public String getChosenByUser() {
		return chosenByUser;
	}

	/**
	 * @param chosenByUser
	 *            the chosenByUser to set
	 */
	public void setChosenByUser(String chosenByUser) {
		this.chosenByUser = chosenByUser;
	}
}
