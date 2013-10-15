package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

// there was a mysterious "radioStationDTO" property duplicating the output
@JsonIgnoreProperties({ "radioStationDTO" })
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

	@Override
	public String toString() {
		return "PostStationsResponse_V0_4 [radioStationDTO=" + radioStationDTO
				+ "]";
	}

}
