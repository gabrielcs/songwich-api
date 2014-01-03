package views.api.scrobbles;

import java.util.List;

import views.api.DTOValidator;
import views.api.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("scrobble")
public class ScrobblesUpdateDTO_V0_4 extends DataTransferObject {
	// used only for output
	private String scrobbleId;

	private String userId;

	private String trackTitle;

	private List<String> artistsNames;

	private String albumTitle;

	private String chosenByUser;

	private String player;

	private String timestamp;

	public ScrobblesUpdateDTO_V0_4() {
		setValidator(this.new ScrobblesUpdateDTOValidator());
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
		if (albumTitle != null && !albumTitle.isEmpty()) {
			this.albumTitle = albumTitle;
		}
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

	public class ScrobblesUpdateDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			// nothing to validate
		}
	}
}
