package views.api.util;

import org.codehaus.jackson.JsonNode;

import views.api.ScrobbleDTO_V0_4;

public class ScrobbleResponse_V0_4 extends APIResponse_V0_4 {

	private ScrobbleDTO_V0_4 scrobble;

	public ScrobbleResponse_V0_4(Status status, String message,
			ScrobbleDTO_V0_4 scrobble) {
		super(status, message);
		this.scrobble = scrobble;
	}

	public JsonNode toJson() {
		return getObjectMapper().valueToTree(this);
	}

	/**
	 * @return the scrobble
	 */
	public ScrobbleDTO_V0_4 getScrobble() {
		return scrobble;
	}

	/**
	 * @param scrobble
	 *            the scrobble to set
	 */
	public void setScrobble(ScrobbleDTO_V0_4 scrobble) {
		this.scrobble = scrobble;
	}
}
