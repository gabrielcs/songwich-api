package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

// there was a mysterious "radioStationDTO" property duplicating the output
@JsonIgnoreProperties({"radioStationDTO"})
public class PutStationsResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("station")
	private RadioStationUpdateDTO_V0_4 radioStationUpdateDTO;

	public PutStationsResponse_V0_4(APIStatus status, String message,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO) {
		super(status, message);
		setRadioStationUpdateDTO(radioStationUpdateDTO);
	}

	public RadioStationUpdateDTO_V0_4 getRadioStationUpdateDTO() {
		return radioStationUpdateDTO;
	}

	public void setRadioStationUpdateDTO(RadioStationUpdateDTO_V0_4 radioStationUpdateDTO) {
		this.radioStationUpdateDTO = radioStationUpdateDTO;
	}

	@Override
	public String toString() {
		return "PostStationsResponse_V0_4 [radioStationUpdateDTO="
				+ radioStationUpdateDTO + "]";
	}

}
