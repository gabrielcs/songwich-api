package views.api.stations;

import java.util.LinkedHashSet;
import java.util.Set;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;

import views.api.DataTransferObject;

// let starredSongs be serialized even if it's empty
@JsonTypeName("starredSongs")
public class StarredSongSetDTO_V0_4 extends DataTransferObject<Scrobble> {

	private String userId;

	// only for output
	// it has to maintain the insertion order and not allow duplicates
	private Set<StationSongListEntryDTO_V0_4> starredSongs = new LinkedHashSet<StationSongListEntryDTO_V0_4>();

	public StarredSongSetDTO_V0_4() {
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

	public Set<StationSongListEntryDTO_V0_4> getStarredSongs() {
		return starredSongs;
	}

	public void setStarredSongs(Set<StationSongListEntryDTO_V0_4> starredSongs) {
		this.starredSongs = starredSongs;
	}

	public void add(StationSongListEntryDTO_V0_4 songDTO) {
		starredSongs.add(songDTO);
	}
}
