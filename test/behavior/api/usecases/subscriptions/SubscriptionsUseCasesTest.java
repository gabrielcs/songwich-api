package behavior.api.usecases.subscriptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.junit.Before;
import org.junit.Test;

import util.api.SongwichAPIException;
import util.api.WithProductionDependencyInjection;
import views.api.subscriptions.SubscriptionDTO_V0_4;
import views.api.subscriptions.SubscriptionInputDTO_V0_4;

public class SubscriptionsUseCasesTest extends
		WithProductionDependencyInjection {

	private User gabriel, daniel;
	private RadioStation gabrielStation, danielStation;

	private SubscriptionInputDTO_V0_4 gabrielSubscriptionGabrielStationDTO,
			danielSubscriptionDanielStationDTO,
			danielSubscriptionGabrielStationDTO;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		initData();
	}

	private void initData() {
		// users
		gabriel = new User("gabriel@example.com", "Gabriel");
		daniel = new User("daniel@example.com", "Daniel");
		getUserDAO().save(gabriel,
				getContext().getAppDeveloper().getEmailAddress());
		getUserDAO().save(daniel,
				getContext().getAppDeveloper().getEmailAddress());

		// stations
		gabrielStation = new RadioStation("Gabriel FM", gabriel);
		danielStation = new RadioStation("Daniel FM", daniel);
		getCascadeSaveRadioStationDAO().cascadeSave(gabrielStation,
				getContext().getAppDeveloper().getEmailAddress());
		getCascadeSaveRadioStationDAO().cascadeSave(danielStation,
				getContext().getAppDeveloper().getEmailAddress());

		// subscription dto's
		gabrielSubscriptionGabrielStationDTO = new SubscriptionInputDTO_V0_4();
		gabrielSubscriptionGabrielStationDTO.setUserId(gabriel.getId()
				.toString());
		gabrielSubscriptionGabrielStationDTO.setStationId(gabrielStation
				.getId().toString());

		danielSubscriptionDanielStationDTO = new SubscriptionInputDTO_V0_4();
		danielSubscriptionDanielStationDTO.setUserId(daniel.getId().toString());
		danielSubscriptionDanielStationDTO.setStationId(danielStation.getId()
				.toString());

		danielSubscriptionGabrielStationDTO = new SubscriptionInputDTO_V0_4();
		danielSubscriptionGabrielStationDTO
				.setUserId(daniel.getId().toString());
		danielSubscriptionGabrielStationDTO.setStationId(gabrielStation.getId()
				.toString());

	}

	@Test
	public void postSubscriptionsTest() throws SongwichAPIException {
		SubscriptionsUseCases subscriptionsUseCases = new SubscriptionsUseCases(
				getContext());
		setRequestContextUser(gabriel);
		SubscriptionDTO_V0_4 subscriptionDTO = subscriptionsUseCases
				.postSubscriptions(gabrielSubscriptionGabrielStationDTO);
		assertNotNull(subscriptionDTO.getId());
	}

	@Test
	public void getStationsTest() throws SongwichAPIException {
		SubscriptionsUseCases subscriptionsUseCases = new SubscriptionsUseCases(
				getContext());

		setRequestContextUser(gabriel);
		subscriptionsUseCases
				.postSubscriptions(gabrielSubscriptionGabrielStationDTO);
		setRequestContextUser(daniel);
		subscriptionsUseCases
				.postSubscriptions(danielSubscriptionDanielStationDTO);
		subscriptionsUseCases
				.postSubscriptions(danielSubscriptionGabrielStationDTO);

		setRequestContextUser(gabriel);
		List<SubscriptionDTO_V0_4> subscriptionsDTO = subscriptionsUseCases
				.getSubscriptions(gabriel.getId().toString());
		assertEquals(1, subscriptionsDTO.size());
		assertEquals(gabrielSubscriptionGabrielStationDTO.getStationId(),
				subscriptionsDTO.get(0).getStation().getStationId());

		setRequestContextUser(daniel);
		subscriptionsDTO = subscriptionsUseCases.getSubscriptions(daniel
				.getId().toString());
		assertEquals(2, subscriptionsDTO.size());
	}

	@Test
	public void putStationsTest() throws SongwichAPIException {
		SubscriptionsUseCases subscriptionsUseCases = new SubscriptionsUseCases(
				getContext());

		setRequestContextUser(gabriel);
		SubscriptionDTO_V0_4 subscriptionDTO = subscriptionsUseCases
				.postSubscriptions(gabrielSubscriptionGabrielStationDTO);
		assertEquals(1, getSubscriptionDAO().findByUserId(gabriel.getId())
				.size());

		subscriptionsUseCases.putEndSubscription(subscriptionDTO.getId());
		assertEquals(0, getSubscriptionDAO().findByUserId(gabriel.getId())
				.size());
	}
}
