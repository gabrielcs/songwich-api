package views.api.stations;

import java.util.List;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetStationsResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("stations")
	private List<RadioStationDTO_V0_4> radioStationsDTO;

	public GetStationsResponse_V0_4(APIStatus status, String message,
			List<RadioStationDTO_V0_4> radioStationsDTO) {
		super(status, message);
		setRadioStationsDTO(radioStationsDTO);
	}

	public List<RadioStationDTO_V0_4> getRadioStationsDTO() {
		return radioStationsDTO;
	}

	public void setRadioStationsDTO(List<RadioStationDTO_V0_4> radioStationsDTO) {
		this.radioStationsDTO = radioStationsDTO;
	}

}
