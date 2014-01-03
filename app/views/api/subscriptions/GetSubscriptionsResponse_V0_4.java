package views.api.subscriptions;

import java.util.List;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetSubscriptionsResponse_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("subscriptions")
	private List<SubscriptionDTO_V0_4> subscriptionDTO;

	public GetSubscriptionsResponse_V0_4(APIStatus status, String message,
			List<SubscriptionDTO_V0_4> subscriptionDTO) {
		super(status, message);
		setSubscriptionDTO(subscriptionDTO);
	}

	public List<SubscriptionDTO_V0_4> getSubscriptionDTO() {
		return subscriptionDTO;
	}

	public void setSubscriptionDTO(List<SubscriptionDTO_V0_4> subscriptionDTO) {
		this.subscriptionDTO = subscriptionDTO;
	}

}
