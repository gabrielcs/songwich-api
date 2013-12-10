package database.api;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import models.api.scrobbles.User;
import models.api.stations.RadioStation;
import models.api.subscriptions.Subscription;

import org.junit.Before;
import org.junit.Test;

import util.api.WithProductionDependencyInjection;

public class SubscriptionsDAOMongoTest extends
		WithProductionDependencyInjection {

	private User fatMike, jackJohnson, joeyCape, gabriel, daniel;
	private RadioStation fatMikeStation, joeyCapeStation, jackJohnsonStation;
	private Subscription gabrielFatMikeStationSubscription,
			gabrielJoeyCapeStationSubscription,
			gabrielJackJohnsonStationSubscription,
			danielJackJohnsonStationSubscription;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		initData();
	}

	private void initData() {
		// users who own stations
		fatMike = new User("fatmike@nofx.com", "Fat Mike");
		jackJohnson = new User("jack.johnson@artists.com", "Jack Johnson");
		joeyCape = new User("joey@cape.com", "Joey Cape");

		// stations
		fatMikeStation = new RadioStation("Fat Mike FM", fatMike);
		jackJohnsonStation = new RadioStation("Jack Johnson FM", jackJohnson);
		joeyCapeStation = new RadioStation("Joey Cape FM", joeyCape);
		getCascadeSaveRadioStationDAO().cascadeSave(fatMikeStation,
				getContext().getAppDeveloper().getEmailAddress());
		getCascadeSaveRadioStationDAO().cascadeSave(jackJohnsonStation,
				getContext().getAppDeveloper().getEmailAddress());
		getCascadeSaveRadioStationDAO().cascadeSave(joeyCapeStation,
				getContext().getAppDeveloper().getEmailAddress());

		// users who subscribe to stations
		gabriel = new User("gabriel@songwich.com", "Gabriel");
		daniel = new User("daniel@songwich.com", "Daniel");
		getUserDAO().save(gabriel,
				getContext().getAppDeveloper().getEmailAddress());
		getUserDAO().save(daniel,
				getContext().getAppDeveloper().getEmailAddress());

		// active subscriptions
		gabrielFatMikeStationSubscription = new Subscription(gabriel.getId(),
				fatMikeStation.getId());
		gabrielJackJohnsonStationSubscription = new Subscription(
				gabriel.getId(), jackJohnsonStation.getId());
		danielJackJohnsonStationSubscription = new Subscription(daniel.getId(),
				jackJohnsonStation.getId());
		getSubscriptionDAO().save(gabrielFatMikeStationSubscription,
				getContext().getAppDeveloper().getEmailAddress());
		getSubscriptionDAO().save(gabrielJackJohnsonStationSubscription,
				getContext().getAppDeveloper().getEmailAddress());
		getSubscriptionDAO().save(danielJackJohnsonStationSubscription,
				getContext().getAppDeveloper().getEmailAddress());

		// ended subscriptions
		gabrielJoeyCapeStationSubscription = new Subscription(gabriel.getId(),
				joeyCapeStation.getId());
		gabrielJoeyCapeStationSubscription.endSubscription();
		getSubscriptionDAO().save(gabrielJoeyCapeStationSubscription,
				getContext().getAppDeveloper().getEmailAddress());

	}

	@Test
	public void testCountEndSubscription() {
		assertEquals(3, getSubscriptionDAO().count());
		assertEquals(4, getSubscriptionDAO().count(false));
	}

	@Test
	public void testFindById() {
		Subscription databaseSubscription = (Subscription) getSubscriptionDAO()
				.findById(gabrielFatMikeStationSubscription.getId());
		assertEquals(databaseSubscription, gabrielFatMikeStationSubscription);
	}

	@Test
	public void testFindByUserId() {
		List<Subscription> gabrielSubscriptions = getSubscriptionDAO()
				.findByUserId(gabriel.getId());
		assertEquals(2, gabrielSubscriptions.size());
		assertTrue(gabrielSubscriptions.containsAll(Arrays.asList(
				gabrielFatMikeStationSubscription,
				gabrielJackJohnsonStationSubscription)));

		gabrielSubscriptions = getSubscriptionDAO().findByUserId(
				gabriel.getId(), false);
		assertEquals(3, gabrielSubscriptions.size());
		assertTrue(gabrielSubscriptions
				.contains(gabrielJoeyCapeStationSubscription));
	}

	@Test
	public void testFindByStationId() {
		List<Subscription> jackJohnsonStationSubscriptions = getSubscriptionDAO()
				.findByStationId(jackJohnsonStation.getId());
		assertEquals(2, jackJohnsonStationSubscriptions.size());
		assertTrue(jackJohnsonStationSubscriptions.containsAll(Arrays.asList(
				gabrielJackJohnsonStationSubscription,
				danielJackJohnsonStationSubscription)));

		List<Subscription> joeyCapeStationSubscriptions = getSubscriptionDAO()
				.findByStationId(joeyCapeStation.getId());
		assertEquals(0, joeyCapeStationSubscriptions.size());
		joeyCapeStationSubscriptions = getSubscriptionDAO().findByStationId(
				joeyCapeStation.getId(), false);
		assertEquals(1, joeyCapeStationSubscriptions.size());
		assertTrue(joeyCapeStationSubscriptions
				.contains(gabrielJoeyCapeStationSubscription));
	}
}
