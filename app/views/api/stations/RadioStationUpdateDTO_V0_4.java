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
public class RadioStationUpdateDTO_V0_4 extends DataTransferObject<Scrobble> {

	// only for output
	private String stationId;

	private String stationName;

	private String groupName;

	private List<String> scrobblerIds;

	private String imageUrl;

	public RadioStationUpdateDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		addValidation(validateScrobblerIds(), validateImageUrl());
	}

	private ValidationError validateScrobblerIds() {
		return validateObjectIdArray("scrobblerIds", "scrobblerId",
				scrobblerIds);
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
				+ ", imageUrl=" + imageUrl + "]";
	}

}