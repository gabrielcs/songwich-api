package views.api.deprecated;

import java.util.GregorianCalendar;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import views.api.util.deprecated.APIStatus_V0_1;
import controllers.api.util.SongwichAPIException;

public class ScrobbleProxy_V0_3 {
	
	private String user_id;
	private String track_title;
	private String artist_name;
	private String service;
	private String timestamp;

	public ScrobbleProxy_V0_3(String user_id, String track_title,
			String artist_name, String service, String timestamp)
			throws SongwichAPIException {
		
		setUser_id(user_id);
		setTrack_title(track_title);
		setArtist_name(artist_name);
		setService(service);
		setTimestamp(timestamp);
	}

	public JsonNode toJson() {
		JsonNode json =  Json.toJson(this);
		removeNullOptionalParameters((ObjectNode) json);
		return json;
	}

	private void removeNullOptionalParameters(ObjectNode json) {
		if (service == null) {
			json.remove("service");
		} else if (service.isEmpty()) {
			json.remove("service");
		}
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) throws SongwichAPIException {
		if (user_id == null || user_id.isEmpty()) {
			throw new SongwichAPIException("Missing parameter: user_id",
					APIStatus_V0_1.BAD_REQUEST);
		}

		validateUserId(user_id);
		this.user_id = user_id;
	}

	// check if it's a string and delegates further validation
	private void validateUserId(String user_id) throws SongwichAPIException {
		try {
			Long userIdLong = Long.parseLong(user_id);
			validateUserId(userIdLong);
		} catch (NumberFormatException e) {
			throw new SongwichAPIException("Invalid user_id",
					APIStatus_V0_1.INVALID_USER_ID);
		}
	}

	private void validateUserId(Long user_id) throws SongwichAPIException {
		// TODO: authenticate user
	}

	public String getTrack_title() {
		return track_title;
	}

	public void setTrack_title(String track_title) throws SongwichAPIException {
		if (track_title == null || track_title.isEmpty()) {
			throw new SongwichAPIException("Missing parameter: track_title",
					APIStatus_V0_1.BAD_REQUEST);
		}

		this.track_title = track_title;
	}

	public String getArtist_name() {
		return artist_name;
	}

	public void setArtist_name(String artist_name) throws SongwichAPIException {
		if (artist_name == null || artist_name.isEmpty()) {
			throw new SongwichAPIException("Missing parameter: artist_name",
					APIStatus_V0_1.BAD_REQUEST);
		}

		this.artist_name = artist_name;
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
		if (timestamp == null) {
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
					APIStatus_V0_1.INVALID_TIMESTAMP);
		}
	}

	// check if it's not in the future or if it's not too old
	private void validateTimestamp(Long timestampNumber)
			throws SongwichAPIException {
		if (timestampNumber > System.currentTimeMillis()) {
			throw new SongwichAPIException("Timestamp cannot be in the future",
					APIStatus_V0_1.INVALID_TIMESTAMP);
		} else if (timestampNumber < new GregorianCalendar(2002, 1, 1)
				.getTimeInMillis()) {
			// it's older than scrobbling itself (2002)
			throw new SongwichAPIException("Timestamp is too old",
					APIStatus_V0_1.INVALID_TIMESTAMP);
		}
	}
}
