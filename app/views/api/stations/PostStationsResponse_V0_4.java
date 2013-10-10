package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostStationsResponse_V0_4 extends APIResponse_V0_4 {
	// TODO: find out why this is duplicating the output
	@JsonProperty("station2")
	private NewRadioStationDTO_V0_4 newRadioStationDTO;

	public PostStationsResponse_V0_4(APIStatus status, String message,
			NewRadioStationDTO_V0_4 newRadioStationDTO) {
		super(status, message);
		setRadioStationDTO(newRadioStationDTO);
	}

	public NewRadioStationDTO_V0_4 getRadioStationDTO() {
		return newRadioStationDTO;
	}

	public void setRadioStationDTO(NewRadioStationDTO_V0_4 newRadioStationDTO) {
		this.newRadioStationDTO = newRadioStationDTO;
	}

	@Override
	public String toString() {
		return "PostStationsResponse_V0_4 [newRadioStationDTO="
				+ newRadioStationDTO + "]";
	}

}
