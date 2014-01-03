package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// there was a mysterious "radioStationDTO" property duplicating the output
@JsonIgnoreProperties({"radioStationUpdateDTO"})
public class PutStationsResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("station")
	private RadioStationOutputDTO_V0_4 radioStationOutputDTO;

	public PutStationsResponse_V0_4(APIStatus status, String message,
			RadioStationOutputDTO_V0_4 radioStationUpdateDTO) {
		super(status, message);
		setRadioStationDTO(radioStationUpdateDTO);
	}

	public RadioStationOutputDTO_V0_4 getRadioStationUpdateDTO() {
		return radioStationOutputDTO;
	}

	public void setRadioStationDTO(RadioStationOutputDTO_V0_4 radioStationUpdateDTO) {
		this.radioStationOutputDTO = radioStationUpdateDTO;
	}

	@Override
	public String toString() {
		return "PostStationsResponse_V0_4 [radioStationUpdateDTO="
				+ radioStationOutputDTO + "]";
	}

}
