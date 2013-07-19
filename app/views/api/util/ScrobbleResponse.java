package views.api.util;

import views.api.ScrobbleDTO;

import com.fasterxml.jackson.databind.JsonNode;

public class ScrobbleResponse extends APIResponseV0_5 {

	private ScrobbleDTO scrobble;

	public ScrobbleResponse(Status status, String message, ScrobbleDTO scrobble) {
		super(status, message);
		this.scrobble = scrobble;
	}

	public JsonNode toJson() {
		return getObjectMapper().valueToTree(this);
	}

	/**
	 * @return the scrobble
	 */
	public ScrobbleDTO getScrobble() {
		return scrobble;
	}

	/**
	 * @param scrobble
	 *            the scrobble to set
	 */
	public void setScrobble(ScrobbleDTO scrobble) {
		this.scrobble = scrobble;
	}
}
