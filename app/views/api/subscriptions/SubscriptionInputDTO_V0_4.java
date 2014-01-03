package views.api.subscriptions;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonInclude(Include.NON_EMPTY)
@JsonTypeName("subscription")
public class SubscriptionInputDTO_V0_4 extends DataTransferObject {

	private String stationId;

	private String userId;

	public SubscriptionInputDTO_V0_4() {
		setValidator(this.new SubscriptionDTOValidator());
	}
	
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public class SubscriptionDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateStationId(), validateUserId());
		}

		private ValidationError validateStationId() {
			return validateRequiredObjectId("stationId", stationId);
		}

		private ValidationError validateUserId() {
			return validateRequiredObjectId("userId", userId);
		}
	}
}