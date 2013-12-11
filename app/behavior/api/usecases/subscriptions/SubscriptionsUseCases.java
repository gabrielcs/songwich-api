package behavior.api.usecases.subscriptions;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.User;
import models.api.stations.RadioStation;
import models.api.subscriptions.Subscription;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.subscriptions.SubscriptionDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;

public class SubscriptionsUseCases extends UseCase {

	public SubscriptionsUseCases(RequestContext context) {
		super(context);
	}

	public void postSubscriptions(SubscriptionDTO_V0_4 subscriptionDTO)
			throws SongwichAPIException {

		authorizePostSubscriptions(subscriptionDTO);

		// process request
		Subscription subscription = new Subscription(new ObjectId(
				subscriptionDTO.getUserId()), new ObjectId(
				subscriptionDTO.getStationId()));

		saveSubscription(subscription);
		updateDTOForPostSubscriptions(subscriptionDTO, subscription);
	}

	public List<SubscriptionDTO_V0_4> getSubscriptions(String userId)
			throws SongwichAPIException {

		User user = authenticateGetSubscriptions(userId);

		// process request
		List<Subscription> subscriptions = getSubscriptionDAO().findByUserId(
				user.getId());

		return createDTOForGetSubscriptions(subscriptions);
	}

	public SubscriptionDTO_V0_4 putEndSubscription(String subscriptionId)
			throws SongwichAPIException {

		Subscription subscription = authenticatePostSubscriptions(subscriptionId);

		// process request
		subscription.endSubscription();
		saveSubscription(subscription);
		
		return createDTOForPutSubscriptions(subscription);
	}

	private Subscription authenticatePostSubscriptions(String subscriptionId)
			throws SongwichAPIException {
		
		Subscription subscription = getSubscriptionDAO().findById(new ObjectId(subscriptionId));
		if (subscription == null) {
			throw new SongwichAPIException("Non-existent subscriptionId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
		return subscription;
	}

	private User authenticateGetSubscriptions(String userId)
			throws SongwichAPIException {
		
		User user = getUserDAO().findById(new ObjectId(userId));
		if (user == null) {
			throw new SongwichAPIException("Non-existent userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
		return user;
	}

	private void authorizePostSubscriptions(SubscriptionDTO_V0_4 subscriptionDTO)
			throws SongwichAPIException {

		User user = getUserDAO().findById(
				new ObjectId(subscriptionDTO.getUserId()));
		if (user == null) {
			throw new SongwichAPIException("Non-existent userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		RadioStation station = getRadioStationDAO().findById(
				new ObjectId(subscriptionDTO.getStationId()));
		if (station == null) {
			throw new SongwichAPIException("Non-existent stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
	}

	private void saveSubscription(Subscription subscription) {
		getSubscriptionDAO().save(subscription,
				getContext().getAppDeveloper().getEmailAddress());
	}

	private void updateDTOForPostSubscriptions(
			SubscriptionDTO_V0_4 subscriptionDTO, Subscription subscription) {

		subscriptionDTO.setId(subscription.getId().toString());
	}

	private List<SubscriptionDTO_V0_4> createDTOForGetSubscriptions(
			List<Subscription> subscriptions) {

		List<SubscriptionDTO_V0_4> subscriptionsDTO = new ArrayList<SubscriptionDTO_V0_4>(
				subscriptions.size());
		SubscriptionDTO_V0_4 subscriptionDTO;
		for (Subscription subscription : subscriptions) {
			subscriptionDTO = new SubscriptionDTO_V0_4();
			subscriptionDTO.setId(subscription.getId().toString());
			subscriptionDTO.setUserId(subscription.getUserId().toString());
			subscriptionDTO
					.setStationId(subscription.getStationId().toString());
			subscriptionsDTO.add(subscriptionDTO);
		}
		return subscriptionsDTO;
	}
	
	private SubscriptionDTO_V0_4 createDTOForPutSubscriptions(
			Subscription subscription) {
		
		SubscriptionDTO_V0_4 subscriptionDTO = new SubscriptionDTO_V0_4();
		subscriptionDTO.setId(subscription.getId().toString());
		return subscriptionDTO;
	}
}
