package behavior.api.usecases.subscriptions;

import static org.junit.Assert.*;

import java.util.List;

import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.junit.Before;
import org.junit.Test;

import util.api.SongwichAPIException;
import util.api.WithProductionDependencyInjection;
import views.api.subscriptions.SubscriptionDTO_V0_4;

public class SubscriptionsUseCasesTest extends
		WithProductionDependencyInjection {

	private User gabriel, daniel;
	private RadioStation gabrielStation, danielStation;

	private SubscriptionDTO_V0_4 gabrielSubscriptionGabrielStationDTO,
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
		gabrielSubscriptionGabrielStationDTO = new SubscriptionDTO_V0_4();
		gabrielSubscriptionGabrielStationDTO.setUserId(gabriel.getId()
				.toString());
		gabrielSubscriptionGabrielStationDTO.setStationId(gabrielStation
				.getId().toString());

		danielSubscriptionDanielStationDTO = new SubscriptionDTO_V0_4();
		danielSubscriptionDanielStationDTO.setUserId(daniel.getId().toString());
		danielSubscriptionDanielStationDTO.setStationId(danielStation.getId()
				.toString());

		danielSubscriptionGabrielStationDTO = new SubscriptionDTO_V0_4();
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
		subscriptionsUseCases
				.postSubscriptions(gabrielSubscriptionGabrielStationDTO);
		assertNotNull(gabrielSubscriptionGabrielStationDTO.getId());
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
				subscriptionsDTO.get(0).getStationId());
		assertEquals(gabriel.getId().toString(), subscriptionsDTO.get(0)
				.getUserId());

		setRequestContextUser(daniel);
		subscriptionsDTO = subscriptionsUseCases.getSubscriptions(daniel
				.getId().toString());
		assertEquals(2, subscriptionsDTO.size());
		for (SubscriptionDTO_V0_4 subscriptionDTO : subscriptionsDTO) {
			assertEquals(daniel.getId().toString(), subscriptionDTO.getUserId());
		}
	}

	@Test
	public void putStationsTest() throws SongwichAPIException {
		SubscriptionsUseCases subscriptionsUseCases = new SubscriptionsUseCases(
				getContext());

		setRequestContextUser(gabriel);
		subscriptionsUseCases
				.postSubscriptions(gabrielSubscriptionGabrielStationDTO);
		assertEquals(1, getSubscriptionDAO().findByUserId(gabriel.getId())
				.size());

		subscriptionsUseCases
				.putEndSubscription(gabrielSubscriptionGabrielStationDTO
						.getId());
		assertEquals(0, getSubscriptionDAO().findByUserId(gabriel.getId())
				.size());
	}
}
