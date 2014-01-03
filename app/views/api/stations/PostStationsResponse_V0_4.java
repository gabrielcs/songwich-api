package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// there was a mysterious "radioStationDTO" property duplicating the output
@JsonIgnoreProperties({ "radioStationDTO" })
public class PostStationsResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("station")
	private RadioStationOutputDTO_V0_4 radioStationDTO;

	public PostStationsResponse_V0_4(APIStatus status, String message,
			RadioStationOutputDTO_V0_4 radioStationOutputDTO) {
		super(status, message);
		setRadioStationDTO(radioStationOutputDTO);
	}

	public RadioStationOutputDTO_V0_4 getRadioStationDTO() {
		return radioStationDTO;
	}

	public void setRadioStationDTO(RadioStationOutputDTO_V0_4 radioStationDTO) {
		this.radioStationDTO = radioStationDTO;
	}

	@Override
	public String toString() {
		return "PostStationsResponse_V0_4 [radioStationDTO=" + radioStationDTO
				+ "]";
	}

}
