package views.api.stations;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DataTransferObject;
import views.api.scrobbles.UserDTO_V0_4;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("track")
public class TrackDTO_V0_4 extends DataTransferObject {
	
	// only for output
	private String songTitle;
	
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
	private List<UserDTO_V0_4> recentScrobblers = new ArrayList<UserDTO_V0_4>();

	public TrackDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		// nothing to validate
	}
	
	public List<UserDTO_V0_4> getRecentScrobblers() {
		return recentScrobblers;
	}

	public void setRecentScrobblers(List<UserDTO_V0_4> recentScrobblers) {
		this.recentScrobblers = recentScrobblers;
	}
	
	public boolean addRecentScrobbler(UserDTO_V0_4 recentScrobbler) {
		return recentScrobblers.add(recentScrobbler);
	}
	
	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public void setSongTitle(String track_title) {
		this.songTitle = track_title;
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
		return "StationSongListEntryDTO_V0_4 [songTitle=" + songTitle
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
				+ ((songTitle == null) ? 0 : songTitle.hashCode());
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
		if (songTitle == null) {
			if (other.songTitle != null)
				return false;
		} else if (!songTitle.equals(other.songTitle))
			return false;
		return true;
	}

}
