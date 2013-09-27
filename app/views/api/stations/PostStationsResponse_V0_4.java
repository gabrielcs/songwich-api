package views.api.stations;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class PostStationsResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("station")
	private RadioStationDTO_V0_4 radioStationDTO;

	public PostStationsResponse_V0_4(APIStatus status, String message,
			RadioStationDTO_V0_4 radioStationDTO) {
		super(status, message);
		setRadioStationDTO(radioStationDTO);
	}

	public RadioStationDTO_V0_4 getRadioStationDTO() {
		return radioStationDTO;
	}

	public void setRadioStationDTO(RadioStationDTO_V0_4 radioStationDTO) {
		this.radioStationDTO = radioStationDTO;
	}

}
