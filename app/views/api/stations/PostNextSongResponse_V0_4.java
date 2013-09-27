package views.api.stations;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class PostNextSongResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("station")
	private StationSongListDTO_V0_4 stationSongListDTO;

	public PostNextSongResponse_V0_4(APIStatus status, String message,
			StationSongListDTO_V0_4 stationSongListDTO) {
		super(status, message);
		setStationSongList(stationSongListDTO);
	}

	public StationSongListDTO_V0_4 getStationSongListDTO() {
		return stationSongListDTO;
	}

	public void setStationSongList(StationSongListDTO_V0_4 stationSongListDTO) {
		this.stationSongListDTO = stationSongListDTO;
	}

}
