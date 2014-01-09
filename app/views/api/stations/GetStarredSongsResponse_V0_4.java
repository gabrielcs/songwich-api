package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

//there was a mysterious "radioStationsDTO" property duplicating the output
@JsonIgnoreProperties({ "radioStationsDTO" })
//paging might be null
@JsonInclude(Include.NON_NULL)
public class GetStarredSongsResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("starredSongsResult")
	private StarredSongSetDTO_V0_4 starredSongListDTO;
	@JsonProperty("paging")
	private StarredSongsPagingDTO_V0_4 pagingDTO;

	public GetStarredSongsResponse_V0_4(APIStatus status, String message,
			StarredSongSetDTO_V0_4 starredSongSetDTO,
			StarredSongsPagingDTO_V0_4 starredSongsPaging) {
		super(status, message);
		setRadioStationsDTO(starredSongSetDTO);
		setPagingDTO(starredSongsPaging);
	}

	public StarredSongSetDTO_V0_4 getRadioStationsDTO() {
		return starredSongListDTO;
	}

	public void setRadioStationsDTO(StarredSongSetDTO_V0_4 starredSongSetDTO) {
		this.starredSongListDTO = starredSongSetDTO;
	}
	
	public StarredSongsPagingDTO_V0_4 getPagingDTO() {
		return pagingDTO;
	}

	public void setPagingDTO(StarredSongsPagingDTO_V0_4 pagingDTO) {
		this.pagingDTO = pagingDTO;
	}
}
