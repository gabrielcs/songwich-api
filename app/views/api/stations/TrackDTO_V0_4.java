package views.api.stations;

import java.util.ArrayList;
import java.util.List;

import views.api.DTOValidator;
import views.api.DataTransferObject;
import views.api.scrobbles.UserOutputDTO_V0_4;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("stationEntry")
public class TrackDTO_V0_4 extends DataTransferObject {
	// only for output
	private String trackTitle;
	
	private String albumTitle;

	/*
	 * Multiple artist tracks will be separated by comma in a single string:
	 * "artistName": "Daft Punk, Pharrell Williams"
	 * 
	 * Only for output
	 */
	private List<String> artistsNames;

	// only for output
	private String idForFeedback;
	
	// only for output
	private List<UserOutputDTO_V0_4> recentScrobblers = new ArrayList<UserOutputDTO_V0_4>();

	public TrackDTO_V0_4() {
		setValidator(this.new TrackDTOValidator());
	}
	
	public List<UserOutputDTO_V0_4> getRecentScrobblers() {
		return recentScrobblers;
	}

	public void setRecentScrobblers(List<UserOutputDTO_V0_4> list) {
		this.recentScrobblers = list;
	}
	
	public boolean addRecentScrobbler(UserOutputDTO_V0_4 recentScrobbler) {
		return recentScrobblers.add(recentScrobbler);
	}
	
	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String track_title) {
		this.trackTitle = track_title;
	}
	
	public List<String> getArtistsNames() {
		return artistsNames;
	}

	public void setArtistsNames(List<String> artistsNames) {
		this.artistsNames = artistsNames;
	}

	public String getIdForFeedback() {
		return idForFeedback;
	}

	public void setIdForFeedback(String idForFeedback) {
		this.idForFeedback = idForFeedback;
	}

	@Override
	public String toString() {
		return "StationSongListEntryDTO_V0_4 [trackTitle=" + trackTitle
				+ ", albumTitle=" + albumTitle + ", artistsNames="
				+ artistsNames + ", idForFeedback=" + idForFeedback
				+ ", recentScrobblers=" + recentScrobblers + "]";
	}

	// doesn't take into account idForFeedback and recentScrobblers
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((albumTitle == null) ? 0 : albumTitle.hashCode());
		result = prime * result
				+ ((artistsNames == null) ? 0 : artistsNames.hashCode());
		result = prime * result
				+ ((trackTitle == null) ? 0 : trackTitle.hashCode());
		return result;
	}

	// doesn't take into account idForFeedback and recentScrobblers
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrackDTO_V0_4 other = (TrackDTO_V0_4) obj;
		if (albumTitle == null) {
			if (other.albumTitle != null)
				return false;
		} else if (!albumTitle.equals(other.albumTitle))
			return false;
		if (artistsNames == null) {
			if (other.artistsNames != null)
				return false;
		} else if (!artistsNames.equals(other.artistsNames))
			return false;
		if (trackTitle == null) {
			if (other.trackTitle != null)
				return false;
		} else if (!trackTitle.equals(other.trackTitle))
			return false;
		return true;
	}
	
	public class TrackDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			// nothing to validate
		}
	}

}
