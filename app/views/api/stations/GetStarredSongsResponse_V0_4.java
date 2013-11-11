package views.api.stations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

//there was a mysterious "radioStationsDTO" property duplicating the output
@JsonIgnoreProperties({"radioStationsDTO"})
public class GetStarredSongsResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("starredSongsResult")
	private StarredSongListDTO_V0_4 starredSongListDTO;

	public GetStarredSongsResponse_V0_4(APIStatus status, String message,
			StarredSongListDTO_V0_4 starredSongSetDTO) {
		super(status, message);
		setRadioStationsDTO(starredSongSetDTO);
	}

	public StarredSongListDTO_V0_4 getRadioStationsDTO() {
		return starredSongListDTO;
	}

	public void setRadioStationsDTO(StarredSongListDTO_V0_4 starredSongSetDTO) {
		this.starredSongListDTO = starredSongSetDTO;
	}
}
