package views.api.stations;

import models.api.scrobbles.Scrobble;
import views.api.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(Include.NON_EMPTY)
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("stationEntry")
public class StationSongListEntryDTO_V0_4 extends DataTransferObject<Scrobble> {
	
	// only for output
	private String trackTitle;

	/*
	 * Multiple artist tracks will be separed by comma in a single string:
	 * "artistName": "Daft Punk, Pharrell Williams"
	 * 
	 * Only for output
	 */
	private String artistName;

	// only for output
	private String feedbackId;

	public StationSongListEntryDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		// nothing to validate
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
