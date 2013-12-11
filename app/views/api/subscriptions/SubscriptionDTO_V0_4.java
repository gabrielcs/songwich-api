package views.api.subscriptions;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DTOValidator;
import views.api.DataTransferObject;
import views.api.stations.RadioStationDTO_V0_4;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("subscription")
public class SubscriptionDTO_V0_4 extends DataTransferObject {

	// only for output
	private String id;

	// only for output
	private String userId;

	// only for output
	private RadioStationDTO_V0_4 station;

	public SubscriptionDTO_V0_4() {
		setValidator(this.new SubscriptionDTOValidator());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RadioStationDTO_V0_4 getStation() {
		return station;
	}

	public void setStation(RadioStationDTO_V0_4 station) {
		this.station = station;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "SubscriptionDTO_V0_4 [id=" + id + ", userId=" + userId
				+ ", station=" + station + "]";
	}



	public class SubscriptionDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			// nothing to validate
			addValidation();
		}
	}
}