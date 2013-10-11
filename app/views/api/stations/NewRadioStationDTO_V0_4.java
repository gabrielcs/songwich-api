package views.api.stations;

import java.util.List;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("station")
public class NewRadioStationDTO_V0_4 extends DataTransferObject<Scrobble> {

	// only for output
	private String stationId;

	private String stationName;

	private String groupName;

	private List<String> scrobblerIds;

	// only for output
	private StationSongListEntryDTO_V0_4 nowPlaying;

	// only for output
	private StationSongListEntryDTO_V0_4 lookAhead;

	private String imageUrl;

	public NewRadioStationDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		addValidation(validateStationName(), validateScrobblerIds(),
				validateGroupName(), validateImageUrl());
	}

	private ValidationError validateStationName() {
		return validateRequiredProperty("stationName", stationName);
	}

	private ValidationError validateScrobblerIds() {
		return validateRequiredNonEmptyObjectIdArray("scrobblerIds",
				"scrobblerId", scrobblerIds);
	}

	private ValidationError validateGroupName() {
		if (scrobblerIds.size() > 1) {
			return validateRequiredProperty("groupName", groupName);
		} else {
			return null;
		}
	}

	private ValidationError validateImageUrl() {
		return validateImageUrl("imageUrl", imageUrl);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public List<String> getScrobblerIds() {
		return scrobblerIds;
	}

	public void setScrobblerIds(List<String> scrobblerIds) {
		this.scrobblerIds = scrobblerIds;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Override
	public String toString() {
		return "NewRadioStationDTO_V0_4 [stationId=" + stationId
				+ ", stationName=" + stationName + ", groupName=" + groupName
				+ ", scrobblerIds=" + scrobblerIds + ", nowPlaying="
				+ nowPlaying + ", lookAhead=" + lookAhead + ", imageUrl="
				+ imageUrl + "]";
	}

}