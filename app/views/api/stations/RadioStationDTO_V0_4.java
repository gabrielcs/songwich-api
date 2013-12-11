package views.api.stations;

import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("station")
public class RadioStationDTO_V0_4 extends DataTransferObject {

	// only for output
	private String stationId;

	private String stationName;

	private String groupName;

	private List<String> scrobblerIds;

	private Long numberSubscribers;

	// only for output
	private String isActive;

	// only for output
	private String stationReadiness;

	// only for output
	private TrackDTO_V0_4 nowPlaying;

	// only for output
	private TrackDTO_V0_4 lookAhead;

	private String imageUrl;

	public RadioStationDTO_V0_4() {
		setValidator(this.new RadioStationUpdateDTOValidator());
	}

	public Long getNumberSubscribers() {
		return numberSubscribers;
	}

	public void setNumberSubscribers(Long numberSubscribers) {
		this.numberSubscribers = numberSubscribers;
	}

	public String getActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getStationReadiness() {
		return stationReadiness;
	}

	public void setStationReadiness(String stationReadiness) {
		this.stationReadiness = stationReadiness;
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
		return "RadioStationDTO_V0_4 [stationId=" + stationId
				+ ", stationName=" + stationName + ", groupName=" + groupName
				+ ", scrobblerIds=" + scrobblerIds + ", numberSubscribers="
				+ numberSubscribers + ", isActive=" + isActive
				+ ", stationReadiness=" + stationReadiness + ", nowPlaying="
				+ nowPlaying + ", lookAhead=" + lookAhead + ", imageUrl="
				+ imageUrl + "]";
	}

	public class RadioStationUpdateDTOValidator extends DTOValidator {
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
	}

}