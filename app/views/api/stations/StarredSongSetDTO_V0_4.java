package views.api.stations;

import java.util.LinkedHashSet;
import java.util.Set;

import views.api.DTOValidator;
import views.api.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonTypeName;

// let starredSongs be serialized even if it's empty
@JsonTypeName("starredSongs")
public class StarredSongSetDTO_V0_4 extends DataTransferObject {

	private String userId;

	// only for output
	// it has to maintain the insertion order and not allow duplicates
	private Set<TrackDTO_V0_4> starredSongs = new LinkedHashSet<TrackDTO_V0_4>();

	public StarredSongSetDTO_V0_4() {
		setValidator(this.new StarredSongSetDTOValidator());
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<TrackDTO_V0_4> getStarredSongs() {
		return starredSongs;
	}

	public void setStarredSongs(Set<TrackDTO_V0_4> starredSongs) {
		this.starredSongs = starredSongs;
	}

	public void add(TrackDTO_V0_4 songDTO) {
		starredSongs.add(songDTO);
	}
	
	public class StarredSongSetDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			// nothing to validate
		}
	}
}
