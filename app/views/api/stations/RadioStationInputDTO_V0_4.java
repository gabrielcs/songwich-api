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
public class RadioStationInputDTO_V0_4 extends DataTransferObject {

	private String stationName;

	private String groupName;

	private List<String> scrobblerIds;

	private String imageUrl;

	private String description;

	public RadioStationInputDTO_V0_4() {
		setValidator(this.new RadioStationUpdateDTOValidator());
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
		return "RadioStationInputDTO_V0_4 [stationName=" + stationName
				+ ", groupName=" + groupName + ", scrobblerIds=" + scrobblerIds
				+ ", imageUrl=" + imageUrl + ", description=" + description
				+ "]";
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
			if (scrobblerIds != null && scrobblerIds.size() > 1) {
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