package behavior.api.usecases;

import models.api.scrobbles.App;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import util.api.DAOProvider;
import database.api.CascadeSaveDAO;
import database.api.scrobbles.AppDAO;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.UserDAO;
import database.api.stations.RadioStationDAO;
import database.api.stations.StationHistoryDAO;
import database.api.subscriptions.SubscriptionDAO;


public abstract class UseCase {

	private RequestContext context;
	private final DAOProvider daoProvider;

	public UseCase(RequestContext context) {
		setContext(context);
		daoProvider = new DAOProvider();
	}

	public RequestContext getContext() {
		return context;
	}

	public void setContext(RequestContext context) {
		this.context = context;
	}
	
	protected AppDAO<ObjectId> getAppDAO() {
		return daoProvider.getAppDAO();
	}
	
	protected UserDAO<ObjectId> getUserDAO() {
		return daoProvider.getUserDAO();
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
