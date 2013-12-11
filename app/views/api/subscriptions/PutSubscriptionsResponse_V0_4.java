package views.api.subscriptions;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

//there was a mysterious "subscriptionsDTO" property duplicating the output
@JsonIgnoreProperties({"subscriptionsDTO"})
public class PutSubscriptionsResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("subscription")
	private SubscriptionDTO_V0_4 subscriptionDTO;

	public PutSubscriptionsResponse_V0_4(APIStatus status, String message,
			SubscriptionDTO_V0_4 subscriptionDTO) {
		super(status, message);
		setSubscriptionDTO(subscriptionDTO);
	}

	public SubscriptionDTO_V0_4 getSubscriptionsDTO() {
		return subscriptionDTO;
	}

	public void setSubscriptionDTO(SubscriptionDTO_V0_4 subscriptionDTO) {
		this.subscriptionDTO = subscriptionDTO;
	}
}
