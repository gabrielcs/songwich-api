package dtos.api.util;

import org.codehaus.jackson.annotate.JsonProperty;

import dtos.api.ScrobbleDTO_V0_4;


public class ScrobblesResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("scrobble")
	private ScrobbleDTO_V0_4 scrobbleDTO;

	public ScrobblesResponse_V0_4(APIStatus status, String message,
			ScrobbleDTO_V0_4 scrobbleDTO) {
		super(status, message);
		this.scrobbleDTO = scrobbleDTO;
	}

	/**
	 * @return the scrobbleDTO
	 */
	public ScrobbleDTO_V0_4 getScrobbleDTO() {
		return scrobbleDTO;
	}

	/**
	 * @param scrobbleDTO
	 *            the scrobbleDTO to set
	 */
	public void setScrobble(ScrobbleDTO_V0_4 scrobbleDTO) {
		this.scrobbleDTO = scrobbleDTO;
	}
}
