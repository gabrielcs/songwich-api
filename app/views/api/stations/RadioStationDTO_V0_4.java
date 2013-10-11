package views.api.stations;

import java.util.List;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("station")
public class RadioStationDTO_V0_4 extends DataTransferObject<Scrobble> {

	private String stationId;

	private String stationName;

	private String imageUrl;

	private StationSongListEntryDTO_V0_4 nowPlaying;
	
	private List<String> scrobblerIds;

	public RadioStationDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		// nothing to validate so far
		addValidation();
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public StationSongListEntryDTO_V0_4 getNowPlaying() {
		return nowPlaying;
	}

	public void setNowPlaying(StationSongListEntryDTO_V0_4 nowPlaying) {
		this.nowPlaying = nowPlaying;
	}

	public List<String> getScrobblerIds() {
		return scrobblerIds;
	}

	public void setScrobblerIds(List<String> scrobblerIds) {
		this.scrobblerIds = scrobblerIds;
	}

}
