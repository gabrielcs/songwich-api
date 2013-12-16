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
public class RadioStationUpdateInputDTO_V0_4 extends DataTransferObject {

	private String stationId;

	private String stationName;

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	private String groupName;

	private List<String> scrobblerIds;

	private String imageUrl;

	private String description;

	public RadioStationUpdateInputDTO_V0_4() {
		this.new RadioStationUpdateDTOValidator();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
		return "RadioStationUpdateInputDTO_V0_4 [stationId=" + stationId
				+ ", stationName=" + stationName + ", groupName=" + groupName
				+ ", scrobblerIds=" + scrobblerIds + ", imageUrl=" + imageUrl
				+ ", description=" + description + "]";
	}

	public class RadioStationUpdateDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateStationId(), validateStationName(),
					validateScrobblerIds(), validateImageUrl());
		}

		private ValidationError validateStationName() {
			return validateIfNonNullThenNonEmptyProperty("stationName",
					stationName);
		}

		private ValidationError validateScrobblerIds() {
			return validateObjectIdArray("scrobblerIds", "scrobblerId",
					scrobblerIds);
		}

		private ValidationError validateStationId() {
			return validateObjectId("stationId", stationId);
		}

		private ValidationError validateImageUrl() {
			return validateImageUrl("imageUrl", imageUrl);
		}
	}
}