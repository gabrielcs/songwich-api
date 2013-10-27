package views.api.stations;

import java.util.HashSet;
import java.util.Set;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;

import views.api.DataTransferObject;

// let starredSongs be serialized even if it's empty
@JsonTypeName("starredSongs")
public class StarredSongSetDTO_V0_4 extends DataTransferObject<Scrobble> {
	
	private String userId;
	
	// only for output
	private Set<SongDTO_V0_4> starredSongs = new HashSet<SongDTO_V0_4>();

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

	public Set<SongDTO_V0_4> getStarredSongs() {
		return starredSongs;
	}

	public void setStarredSongs(Set<SongDTO_V0_4> starredSongs) {
		this.starredSongs = starredSongs;
	}

	public void add(SongDTO_V0_4 songDTO) {
		starredSongs.add(songDTO);
	}
}
