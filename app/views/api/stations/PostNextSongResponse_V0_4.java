package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//there was a mysterious "stationSongListDTO" property duplicating the output
@JsonIgnoreProperties({"stationSongListDTO"})
public class PostNextSongResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("station")
	private RadioStationOutputDTO_V0_4 radioStationUpdateDTO;

	public PostNextSongResponse_V0_4(APIStatus status, String message,
			RadioStationOutputDTO_V0_4 stationOutputDTO) {
		super(status, message);
		setStationSongList(stationOutputDTO);
	}

	public RadioStationOutputDTO_V0_4 getStationSongListDTO() {
		return radioStationUpdateDTO;
	}

	public void setStationSongList(RadioStationOutputDTO_V0_4 radioStationUpdateDTO) {
		this.radioStationUpdateDTO = radioStationUpdateDTO;
	}

}
