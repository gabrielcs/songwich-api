package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// there was a mysterious "radioStationDTO" property duplicating the output
@JsonIgnoreProperties({"radioStationDTO"})
public class PostStationsResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("station")
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
