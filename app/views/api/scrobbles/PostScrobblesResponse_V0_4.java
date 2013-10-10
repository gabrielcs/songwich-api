package views.api.scrobbles;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostScrobblesResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("scrobble")
	private ScrobblesDTO_V0_4 scrobbleDTO;

	public PostScrobblesResponse_V0_4(APIStatus status, String message,
			ScrobblesDTO_V0_4 scrobbleDTO) {
		super(status, message);
		this.scrobbleDTO = scrobbleDTO;
	}

	/**
	 * @return the scrobbleDTO
	 */
	public ScrobblesDTO_V0_4 getScrobbleDTO() {
		return scrobbleDTO;
	}

	/**
	 * @param scrobbleDTO
	 *            the scrobbleDTO to set
	 */
	public void setScrobble(ScrobblesDTO_V0_4 scrobbleDTO) {
		this.scrobbleDTO = scrobbleDTO;
	}
}
