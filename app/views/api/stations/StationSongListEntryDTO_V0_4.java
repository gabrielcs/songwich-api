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

	/*
	 * Multiple artist tracks will be separated by comma in a single string:
	 * "artistName": "Daft Punk, Pharrell Williams"
	 * 
	 * Only for output
	 */
	private String artistName;

	// only for output
	private String feedbackId;
	
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

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String track_title) {
		this.trackTitle = track_title;
	}
	
	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(String feedbackId) {
		this.feedbackId = feedbackId;
	}

}
