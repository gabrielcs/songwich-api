package views.api.subscriptions;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class PostSubscriptionsResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("subscription")
	private SubscriptionDTO_V0_4 subscriptionDTO;

	public PostSubscriptionsResponse_V0_4(APIStatus status, String message,
			SubscriptionDTO_V0_4 subscriptionDTO) {
		super(status, message);
		setSubscriptionDTO(subscriptionDTO);
	}

	public SubscriptionDTO_V0_4 getSubscriptionDTO() {
		return subscriptionDTO;
	}

	public void setSubscriptionDTO(SubscriptionDTO_V0_4 subscriptionDTO) {
		this.subscriptionDTO = subscriptionDTO;
	}

	@Override
	public String toString() {
		return "PostSubscriptionsResponse_V0_4 [subscriptionDTO=" + subscriptionDTO
				+ "]";
	}

}
