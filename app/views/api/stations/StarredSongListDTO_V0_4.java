package views.api.stations;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;

import views.api.DataTransferObject;

// let starredSongs be serialized even if it's empty
@JsonTypeName("starredSongs")
public class StarredSongListDTO_V0_4 extends DataTransferObject<Scrobble> {

	private String userId;

	// only for output
	private List<StationSongListEntryDTO_V0_4> starredSongs = new ArrayList<StationSongListEntryDTO_V0_4>();

	public StarredSongListDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		// nothing to validate
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<StationSongListEntryDTO_V0_4> getStarredSongs() {
		return starredSongs;
	}

	public void setStarredSongs(List<StationSongListEntryDTO_V0_4> starredSongs) {
		this.starredSongs = starredSongs;
	}

	public void add(StationSongListEntryDTO_V0_4 songDTO) {
		starredSongs.add(songDTO);
	}
}
