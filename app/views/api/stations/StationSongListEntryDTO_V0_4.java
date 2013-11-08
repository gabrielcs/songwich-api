package views.api.stations;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DataTransferObject;
import views.api.scrobbles.UserDTO_V0_4;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("stationEntry")
public class StationSongListEntryDTO_V0_4 extends DataTransferObject<Scrobble> {
	
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
	private List<UserDTO_V0_4> recentScrobblers = new ArrayList<UserDTO_V0_4>();

	public StationSongListEntryDTO_V0_4() {
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
				+ ", artistsNames=" + artistsNames + ", idForFeedback="
				+ idForFeedback + ", recentScrobblers=" + recentScrobblers
				+ "]";
	}

}
