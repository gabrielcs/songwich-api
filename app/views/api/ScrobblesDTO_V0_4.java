package views.api;

import java.util.GregorianCalendar;
import java.util.List;

import models.api.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;


import play.data.validation.ValidationError;
import views.api.util.DataTransferObject;

// @JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("scrobble")
public class ScrobblesDTO_V0_4 extends DataTransferObject<Scrobble> {
	private String trackTitle;

	/*
	 * Single artist tracks can be scrobbled as in: "artistsNames": "Daft Punk"
	 * 
	 * Multiple artist tracks can be scrobbled slightly differently:
	 * "artistsNames": ["Daft Punk", "Pharrell Williams"]
	 */
	private List<String> artistsNames;

	private String chosenByUser;

	private String player;

	private String timestamp;

	public ScrobblesDTO_V0_4() {
		// sets default value for timestamp
		timestamp = Long.toString(System.currentTimeMillis());
	}

	@Override
	public List<ValidationError> validate() {
		addValidation(validateTrackTitle(), validateArtistsNames(),
				validateTimestamp(), validateChosenByUser());
		// check for empty list and return null
		return getValidationErrors().isEmpty() ? null : getValidationErrors();
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String track_title) {
		this.trackTitle = track_title;
	}

	private ValidationError validateTrackTitle() {
		if (trackTitle == null || trackTitle.isEmpty()) {
			return new ValidationError("trackTitle", "trackTitle is required");
		}

		return null;
	}

	public List<String> getArtistsNames() {
		return artistsNames;
	}

	public void setArtistsNames(List<String> artistsNames) {
		// separate by comma here?
		this.artistsNames = artistsNames;
	}

	private ValidationError validateArtistsNames() {
		if (artistsNames == null || artistsNames.isEmpty()) {
			return new ValidationError("artistsNames",
					"artistsNames is required");
		} else {
			for (String artistName : artistsNames) {
				if (!artistName.isEmpty()) {
					return null;
				}
			}
			// no artistName was non-empty
			return new ValidationError("artistsNames",
					"artistsNames is required");
		}
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
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	// convert to Long and delegate further validation
	private ValidationError validateTimestamp() {
		Long timestampNumber;
		try {
			timestampNumber = Long.parseLong(timestamp);
		} catch (NumberFormatException e) {
			return new ValidationError("timestamp",
					"timestamp is not an integer number");
		}

		// check if the number is within the range
		if (timestampNumber > System.currentTimeMillis()) {
			return new ValidationError("timestamp",
					"timestamp cannot be in the future");
		} else if (timestampNumber < new GregorianCalendar(2002, 1, 1)
				.getTimeInMillis()) {
			// it's older than scrobbling itself (2002)
			return new ValidationError("timestamp", "timestamp is too old");
		}

		return null;
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

	private ValidationError validateChosenByUser() {
		// choosenByUser is optional
		if (chosenByUser == null || chosenByUser.equalsIgnoreCase("true")
				|| chosenByUser.equalsIgnoreCase("false")) {
			return null;
		} else {
			return new ValidationError("chosenByUser",
					"chosenByUser should be either true or false");
		}
	}
}
