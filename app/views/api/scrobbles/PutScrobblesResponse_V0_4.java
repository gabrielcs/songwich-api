package views.api.scrobbles;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//there was a mysterious "scrobbleUpdateDTO" property duplicating the output
@JsonIgnoreProperties({"scrobbleUpdateDTO"})
public class PutScrobblesResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("scrobble")
	private ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO;

	public PutScrobblesResponse_V0_4(APIStatus status, String message,
			ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO) {
		super(status, message);
		this.scrobblesUpdateDTO = scrobblesUpdateDTO;
	}

	/**
	 * @return the scrobbleDTO
	 */
	public ScrobblesUpdateDTO_V0_4 getScrobbleUpdateDTO() {
		return scrobblesUpdateDTO;
	}

	/**
	 * @param scrobblesUpdateDTO
	 *            the scrobbleDTO to set
	 */
	public void setScrobble(ScrobblesUpdateDTO_V0_4 scrobblesUpdateDTO) {
		this.scrobblesUpdateDTO = scrobblesUpdateDTO;
	}
}
