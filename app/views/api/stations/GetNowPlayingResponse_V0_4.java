package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class GetNowPlayingResponse_V0_4 extends APIResponse_V0_4 {
	
	private StationSongListDTO_V0_4 stationSongListDTO;

	public GetNowPlayingResponse_V0_4(APIStatus status, String message,
			StationSongListDTO_V0_4 stationSongListDTO) {
		super(status, message);
		setStationSongListDTO(stationSongListDTO);
	}

	public StationSongListDTO_V0_4 getStationSongListDTO() {
		return stationSongListDTO;
	}

	public void setStationSongListDTO(StationSongListDTO_V0_4 stationSongListDTO) {
		this.stationSongListDTO = stationSongListDTO;
	}

}
