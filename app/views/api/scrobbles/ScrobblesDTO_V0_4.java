package views.api.scrobbles;

import java.util.GregorianCalendar;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("scrobble")
public class ScrobblesDTO_V0_4 extends DataTransferObject {
	// used only for output
	private String scrobbleId;

	// used only for output
	private String userId;

	private String trackTitle;

	/*
	 * Single artist tracks can be scrobbled as in: "artistsNames": "Daft Punk"
	 * 
	 * Multiple artist tracks can be scrobbled slightly differently:
	 * "artistsNames": ["Daft Punk", "Pharrell Williams"]
	 */
	private List<String> artistsNames;

	private String albumTitle;

	private String chosenByUser;

	private String player;

	private String timestamp;

	public ScrobblesDTO_V0_4() {
		// sets default value for timestamp
		timestamp = Long.toString(System.currentTimeMillis());
		
		setValidator(this.new ScrobblesDTOValidator());
	}

	public String getScrobbleId() {
		return scrobbleId;
	}

	public void setScrobbleId(String scrobbleId) {
		this.scrobbleId = scrobbleId;
	}

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String track_title) {
		this.trackTitle = track_title;
	}

	public List<String> getArtistsNames() {
		return artistsNames;
	}

	public void setArtistsNames(List<String> artistsNames) {
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
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
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

	
	
	public class ScrobblesDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateTrackTitle(), validateArtistsNames(),
					validateTimestamp(), validateChosenByUser());
		}

		private ValidationError validateTrackTitle() {
			return validateRequiredProperty("trackTitle", trackTitle);
		}
		
		private ValidationError validateArtistsNames() {
			return validateRequiredNonEmptyArray("artistsNames", artistsNames);
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
		
		private ValidationError validateChosenByUser() {
			// choosenByUser is optional
			return validateBoolean("chosenByUser", chosenByUser);
		}
	}
}
