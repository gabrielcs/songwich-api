package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

// there was a mysterious "radioStationDTO" property duplicating the output
@JsonIgnoreProperties({"radioStationDTO"})
public class PostStationsResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("station")
	private RadioStationDTO_V0_4 newRadioStationDTO;

	public PostStationsResponse_V0_4(APIStatus status, String message,
			RadioStationDTO_V0_4 newRadioStationDTO) {
		super(status, message);
		setRadioStationDTO(newRadioStationDTO);
	}

	public RadioStationDTO_V0_4 getRadioStationDTO() {
		return newRadioStationDTO;
	}

	public void setRadioStationDTO(RadioStationDTO_V0_4 newRadioStationDTO) {
		this.newRadioStationDTO = newRadioStationDTO;
	}

	@Override
	public String toString() {
		return "PostStationsResponse_V0_4 [newRadioStationDTO="
				+ newRadioStationDTO + "]";
	}

}
