package util.api;

import models.api.scrobbles.App;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;

import database.api.CascadeSaveDAO;
import database.api.scrobbles.AppDAO;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.UserDAO;
import database.api.stations.RadioStationDAO;
import database.api.stations.StationHistoryDAO;
import database.api.subscriptions.SubscriptionDAO;

public class WithProductionDependencyInjection extends WithRequestContext {

	private Injector injector;
	private DAOProvider daoProvider;
	
	protected Injector getInjector() {
		return injector;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// for dependency injection
		injector = Guice.createInjector(
				new ProductionDependencyInjectionModule(),
				new MongoDependencyInjectionModule());
		
		DAOProvider.setInjector(injector);
		daoProvider = new DAOProvider();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	protected UserDAO<ObjectId> getUserDAO() {
		return daoProvider.getUserDAO();
	}
	
	protected AppDAO<ObjectId> getAppDAO() {
		return daoProvider.getAppDAO();
	}
	
	protected ScrobbleDAO<ObjectId> getScrobbleDAO() {
		return daoProvider.getScrobbleDAO();
	}

	protected RadioStationDAO<ObjectId> getRadioStationDAO() {
		return daoProvider.getRadioStationDAO();
	}

	protected StationHistoryDAO<ObjectId> getStationHistoryDAO() {
		return daoProvider.getStationHistoryDAO();
	}
	
	protected SubscriptionDAO<ObjectId> getSubscriptionDAO() {
		return daoProvider.getSubscriptionDAO();
	}
	
	protected CascadeSaveDAO<App, ObjectId> getCascadeSaveAppDAO() {
		return daoProvider.getCascadeSaveAppDAO();
	}

	protected CascadeSaveDAO<User, ObjectId> getCascadeSaveUserDAO() {
		return daoProvider.getCascadeSaveUserDAO();
	}

	protected CascadeSaveDAO<RadioStation, ObjectId> getCascadeSaveRadioStationDAO() {
		return daoProvider.getCascadeSaveRadioStationDAO();
	}
}
