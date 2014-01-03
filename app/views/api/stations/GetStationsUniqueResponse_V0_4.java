package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetStationsUniqueResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("station")
	private RadioStationOutputDTO_V0_4 radioStationDTO;

	public GetStationsUniqueResponse_V0_4(APIStatus status, String message,
			RadioStationOutputDTO_V0_4 radioStationDTO2) {
		super(status, message);
		setRadioStationDTO(radioStationDTO2);
	}

	public RadioStationOutputDTO_V0_4 getRadioStationDTO() {
		return radioStationDTO;
	}

	public void setRadioStationDTO(RadioStationOutputDTO_V0_4 radioStationDTO) {
		this.radioStationDTO = radioStationDTO;
	}

}
