package views.api.stations;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("stationEntry")
public class StationSongListDTO_V0_4 extends DataTransferObject {
	
	private String stationId;
	
	// only for output
	private TrackDTO_V0_4 nowPlaying;
	
    // only for output
	private TrackDTO_V0_4 lookAhead;

	public StationSongListDTO_V0_4() {
		setValidator(this.new StationSongListDTOValidator());
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public TrackDTO_V0_4 getNowPlaying() {
		return nowPlaying;
	}

	public void setNowPlaying(TrackDTO_V0_4 nowPlaying) {
		this.nowPlaying = nowPlaying;
	}

	public TrackDTO_V0_4 getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(TrackDTO_V0_4 lookAhead) {
		this.lookAhead = lookAhead;
	}
	
	public class StationSongListDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateStationId());
		}
		
		private ValidationError validateStationId() {
			return validateRequiredObjectId("stationId", stationId);
		}
	}

}
