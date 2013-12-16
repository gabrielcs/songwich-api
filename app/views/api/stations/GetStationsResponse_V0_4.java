package views.api.stations;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class GetStationsResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("stations")
	private List<RadioStationOutputDTO_V0_4> radioStationsDTO;

	public GetStationsResponse_V0_4(APIStatus status, String message,
			List<RadioStationOutputDTO_V0_4> radioStationsDTO2) {
		super(status, message);
		setRadioStationsDTO(radioStationsDTO2);
	}

	public List<RadioStationOutputDTO_V0_4> getRadioStationsDTO() {
		return radioStationsDTO;
	}

	public void setRadioStationsDTO(List<RadioStationOutputDTO_V0_4> radioStationsDTO) {
		this.radioStationsDTO = radioStationsDTO;
	}

}
