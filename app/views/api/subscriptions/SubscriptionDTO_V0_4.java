package views.api.subscriptions;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DTOValidator;
import views.api.DataTransferObject;
import views.api.stations.RadioStationOutputDTO_V0_4;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("subscription")
public class SubscriptionDTO_V0_4 extends DataTransferObject {

	// only for output
	private String id;

	// only for output
	private String userId;

	// only for output
	private RadioStationOutputDTO_V0_4 station;

	public SubscriptionDTO_V0_4() {
		setValidator(this.new SubscriptionDTOValidator());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RadioStationOutputDTO_V0_4 getStation() {
		return station;
	}

	public void setStation(RadioStationOutputDTO_V0_4 stationDTO) {
		this.station = stationDTO;
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