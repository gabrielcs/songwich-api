package controllers.api.proxies;

import java.util.GregorianCalendar;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Json;
import controllers.api.util.SongwichAPIException;
import controllers.api.util.Status;

public class ScrobbleProxyV0_1 {
	
	private Long user_id;
	private String track_title;
	private String artist_name;
	private String service;
	private Long timestamp;

	public ScrobbleProxyV0_1(Long user_id, String track_title, String artist_name,
			String service, Long timestamp) throws SongwichAPIException {

		setUser_id(user_id);
		setTrack_title(track_title);
		setArtist_name(artist_name);
		setService(service);
		setTimestamp(timestamp);
	}

	public JsonNode toJson() {
		return Json.toJson(this);
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getTrack_title() {
		return track_title;
	}

	public void setTrack_title(String track_title) {
		this.track_title = track_title;
	}

	public String getArtist_name() {
		return artist_name;
	}

	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		if (service != null) {
			Logger.debug("service is not null");
			this.service = service;
		} else {
			Logger.debug("service is null");
			this.service = "";
		}
	}

	/**
	 * Timestamp is the difference, measured in milliseconds, between the
	 * current time and midnight, January 1, 1970 UTC.
	 * 
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Timestamp is the difference, measured in milliseconds, between the
	 * current time and midnight, January 1, 1970 UTC.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Long timestamp) throws SongwichAPIException {
		if (timestamp > System.currentTimeMillis()) {
			throw new SongwichAPIException("Timestamp cannot be in the future",
					Status.INVALID_TIMESTAMP);
		} else if (timestamp < new GregorianCalendar(2002,1,1).getTimeInMillis()) {
			// it's older than scrobbling itself (2002)
			throw new SongwichAPIException("Timestamp too old",
					Status.INVALID_TIMESTAMP);
		}
		
		this.timestamp = timestamp;
	}
}
