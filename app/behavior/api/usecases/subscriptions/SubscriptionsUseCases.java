package behavior.api.usecases.subscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.api.scrobbles.User;
import models.api.stations.RadioStation;
import models.api.subscriptions.Subscription;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.stations.RadioStationOutputDTO_V0_4;
import views.api.subscriptions.SubscriptionDTO_V0_4;
import views.api.subscriptions.SubscriptionInputDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import behavior.api.usecases.stations.StationsUseCases;

public class SubscriptionsUseCases extends UseCase {

	public SubscriptionsUseCases(RequestContext context) {
		super(context);
	}

	public SubscriptionDTO_V0_4 postSubscriptions(
			SubscriptionInputDTO_V0_4 subscriptionInputDTO)
			throws SongwichAPIException {

		authorizePostSubscriptions(subscriptionInputDTO);

		// process request
		Subscription subscription = new Subscription(new ObjectId(
				subscriptionInputDTO.getUserId()), new ObjectId(
				subscriptionInputDTO.getStationId()));
		saveSubscription(subscription);

		// output
		RadioStation station = getRadioStationDAO().findById(
				subscription.getStationId());
		return createDTOForSubscription(subscription, station, true);
	}

	public List<SubscriptionDTO_V0_4> getSubscriptions(String userId)
			throws SongwichAPIException {

		User user = authenticateGetSubscriptions(userId);

		// process request
		List<Subscription> subscriptions = getSubscriptionDAO().findByUserId(
				user.getId());
		Map<Subscription, RadioStation> subscriptionStationMap = new HashMap<Subscription, RadioStation>(
				subscriptions.size());
		RadioStation station;
		for (Subscription subscription : subscriptions) {
			station = getRadioStationDAO()
					.findById(subscription.getStationId());
			subscriptionStationMap.put(subscription, station);
		}

		// output
		return createDTOForGetSubscriptions(subscriptionStationMap, false);
	}

	public SubscriptionDTO_V0_4 putEndSubscription(String subscriptionId)
			throws SongwichAPIException {

		Subscription subscription = authorizePutSubscriptions(subscriptionId);

		// process request
		subscription.endSubscription();
		saveSubscription(subscription);

		return createDTOForPutSubscriptions(subscription);
	}

	private void authorizePostSubscriptions(
			SubscriptionInputDTO_V0_4 subscriptionInputDTO)
			throws SongwichAPIException {

		User user = getUserDAO().findById(
				new ObjectId(subscriptionInputDTO.getUserId()));
		if (user == null) {
			throw new SongwichAPIException("Non-existent userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		RadioStation station = getRadioStationDAO().findById(
				new ObjectId(subscriptionInputDTO.getStationId()));
		if (station == null) {
			throw new SongwichAPIException("Non-existent stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		authenticateForSubscriptions(user.getId());
	}

	private Subscription authorizePutSubscriptions(String subscriptionId)
			throws SongwichAPIException {

		Subscription subscription = getSubscriptionDAO().findById(
				new ObjectId(subscriptionId));
		if (subscription == null) {
			throw new SongwichAPIException("Non-existent subscriptionId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		authenticateForSubscriptions(subscription.getUserId());

		return subscription;
	}

	private void authenticateForSubscriptions(ObjectId subscriptionUserId)
			throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException("Missing X-Songwich.userAuthToken",
					APIStatus_V0_4.UNAUTHORIZED);
		}

		if (!subscriptionUserId.equals(getContext().getUser().getId())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}
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

	private void saveSubscription(Subscription subscription) {
		getSubscriptionDAO().save(subscription,
				getContext().getAppDeveloper().getEmailAddress());
	}

	private static SubscriptionDTO_V0_4 createDTOForSubscription(
			Subscription subscription, RadioStation station,
			boolean includeUserData) {

		SubscriptionDTO_V0_4 subscriptionDTO = new SubscriptionDTO_V0_4();
		subscriptionDTO.setId(subscription.getId().toString());

		RadioStationOutputDTO_V0_4 stationDTO = StationsUseCases
				.createDTOForSubscription(station);
		subscriptionDTO.setStation(stationDTO);

		if (includeUserData) {
			subscriptionDTO.setUserId(subscription.getUserId().toString());
		}

		return subscriptionDTO;
	}

	public static List<SubscriptionDTO_V0_4> createDTOForGetSubscriptions(
			Map<Subscription, RadioStation> subscriptionStationMap,
			boolean includeUserId) {

		List<SubscriptionDTO_V0_4> subscriptionsDTO = new ArrayList<SubscriptionDTO_V0_4>(
				subscriptionStationMap.size());
		SubscriptionDTO_V0_4 subscriptionDTO;
		for (Subscription subscription : subscriptionStationMap.keySet()) {
			subscriptionDTO = createDTOForSubscription(subscription,
					subscriptionStationMap.get(subscription), includeUserId);
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
