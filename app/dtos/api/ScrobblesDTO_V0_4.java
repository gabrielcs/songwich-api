package dtos.api;

import java.util.GregorianCalendar;

import models.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.Constraints.Required;
import controllers.api.util.SongwichAPIException;
import dtos.api.util.APIStatus_V0_4;
import dtos.api.util.DataTransferObject;

// @JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("scrobble")
public class ScrobblesDTO_V0_4 extends DataTransferObject<Scrobble> {
	@Required
	private String trackTitle;

	@Required
	private String[] artistsNames;

	private String chosenByUser;

	private String player;

	private String timestamp;

	// not used for input, only for output
	private String userId;

	public ScrobblesDTO_V0_4() {
		// sets default value for timestamp
		timestamp = Long.toString(System.currentTimeMillis());
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		// no validation since it's only used for output
		this.userId = userId;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String track_title) {
		this.trackTitle = track_title;
	}

	public String[] getArtistsNames() {
		return artistsNames;
	}

	public void setArtistsNames(String[] artistsNames) {
		// separate by comma here?
		this.artistsNames = artistsNames;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
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
					APIStatus_V0_4.INVALID_PARAMETER);
		}
	}

	// check if it's not in the future or if it's not too old
	private void validateTimestamp(Long timestampNumber)
			throws SongwichAPIException {
		if (timestampNumber > System.currentTimeMillis()) {
			throw new SongwichAPIException("Timestamp cannot be in the future",
					APIStatus_V0_4.INVALID_PARAMETER);
		} else if (timestampNumber < new GregorianCalendar(2002, 1, 1)
				.getTimeInMillis()) {
			// it's older than scrobbling itself (2002)
			throw new SongwichAPIException("Timestamp is too old",
					APIStatus_V0_4.INVALID_PARAMETER);
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
	public void setChosenByUser(String chosenByUser) throws SongwichAPIException {
		validateChosenByUser(chosenByUser);
		this.chosenByUser = chosenByUser;
	}
	
	public void validateChosenByUser(String chosenByUser) throws SongwichAPIException {
		try {
			Boolean.parseBoolean(chosenByUser);
		} catch (NumberFormatException e) {
			throw new SongwichAPIException(
					"chosenByUser should be either 'true' or 'false'",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
	}
}
