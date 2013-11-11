package views.api.stations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

//there was a mysterious "stationSongListDTO" property duplicating the output
@JsonIgnoreProperties({"stationSongListDTO"})
public class PostNextSongResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("station")
	private RadioStationUpdateDTO_V0_4 radioStationUpdateDTO;

	public PostNextSongResponse_V0_4(APIStatus status, String message,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO) {
		super(status, message);
		setStationSongList(radioStationUpdateDTO);
	}

	public RadioStationUpdateDTO_V0_4 getStationSongListDTO() {
		return radioStationUpdateDTO;
	}

	public void setStationSongList(RadioStationUpdateDTO_V0_4 radioStationUpdateDTO) {
		this.radioStationUpdateDTO = radioStationUpdateDTO;
	}

}
