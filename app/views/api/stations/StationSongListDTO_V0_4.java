package views.api.stations;

import models.api.scrobbles.Scrobble;
import play.data.validation.ValidationError;
import views.api.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(Include.NON_EMPTY)
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("stationEntry")
public class StationSongListDTO_V0_4 extends DataTransferObject<Scrobble> {
	
	private String stationId;
	
	// only for output
	private StationSongListEntryDTO_V0_4 nowPlaying;
	
    // only for output
	private StationSongListEntryDTO_V0_4 lookAhead;

	public StationSongListDTO_V0_4() {
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public StationSongListEntryDTO_V0_4 getNowPlaying() {
		return nowPlaying;
	}

	public void setNowPlaying(StationSongListEntryDTO_V0_4 nowPlaying) {
		this.nowPlaying = nowPlaying;
	}

	public StationSongListEntryDTO_V0_4 getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(StationSongListEntryDTO_V0_4 lookAhead) {
		this.lookAhead = lookAhead;
	}

	@Override
	public void addValidation() {
		addValidation(validateStationId());
	}
	
	private ValidationError validateStationId() {
		return validateRequiredObjectId("stationId", stationId);
	}

}
