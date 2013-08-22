package controllers.api.scrobbles;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import controllers.api.APIResponse_V0_4;
import controllers.api.APIStatus;

import views.api.scrobbles.ScrobblesDTO_V0_4;


public class GetScrobblesResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("scrobbles")
	private List<ScrobblesDTO_V0_4> scrobblesDTO;

	/*
	public GetScrobblesResponse_V0_4(APIStatus status, String message) {
		super(status, message);
		scrobblesDTO = new ArrayList<ScrobblesDTO_V0_4>();
	}
	*/
	
	public GetScrobblesResponse_V0_4(APIStatus status, String message,
			List<ScrobblesDTO_V0_4> scrobblesDTO) {
		super(status, message);
		setScrobblesDTO(scrobblesDTO);
	}

	/**
	 * @return the scrobbleDTO
	 */
	public List<ScrobblesDTO_V0_4> getScrobblesDTO() {
		return scrobblesDTO;
	}

	/**
	 * @param scrobbleDTO
	 *            the scrobbleDTO to set
	 */
	public void setScrobblesDTO(List<ScrobblesDTO_V0_4> scrobblesDTO) {
		this.scrobblesDTO = scrobblesDTO;
	}
	
	public void addScrobbleDTO(ScrobblesDTO_V0_4 scrobbleDTO) {
		scrobblesDTO.add(scrobbleDTO);
	}
}
