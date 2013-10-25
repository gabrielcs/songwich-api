package views.api.stations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

//there was a mysterious "radioStationsDTO" property duplicating the output
@JsonIgnoreProperties({"radioStationsDTO"})
public class GetStarredSongsResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("starredSongs")
	private StarredSongSetDTO_V0_4 starredSongListDTO;

	public GetStarredSongsResponse_V0_4(APIStatus status, String message,
			StarredSongSetDTO_V0_4 starredSongSetDTO) {
		super(status, message);
		setRadioStationsDTO(starredSongSetDTO);
	}

	public StarredSongSetDTO_V0_4 getRadioStationsDTO() {
		return starredSongListDTO;
	}

	public void setRadioStationsDTO(StarredSongSetDTO_V0_4 starredSongSetDTO) {
		this.starredSongListDTO = starredSongSetDTO;
	}
}
