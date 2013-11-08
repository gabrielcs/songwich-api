package util.api;

import models.api.scrobbles.App;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import database.api.CascadeSaveDAO;
import database.api.scrobbles.AppDAO;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.UserDAO;
import database.api.stations.RadioStationDAO;
import database.api.stations.StationHistoryDAO;

public class DAOProvider {

	private static Injector INJECTOR;

	private AppDAO<ObjectId> appDAO;
	private UserDAO<ObjectId> userDAO;
	private ScrobbleDAO<ObjectId> scrobbleDAO;
	private RadioStationDAO<ObjectId> radioStationDAO;
	private StationHistoryDAO<ObjectId> stationHistoryDAO;

	private CascadeSaveDAO<App, ObjectId> cascadeSaveAppDAO;
	private CascadeSaveDAO<User, ObjectId> cascadeSaveUserDAO;
	private CascadeSaveDAO<RadioStation, ObjectId> cascadeSaveRadioStationDAO;

	public DAOProvider() {
	}

	public static void setInjector(final Injector GLOBAL_INJECTOR) {
		INJECTOR = GLOBAL_INJECTOR;
	}

	public AppDAO<ObjectId> getAppDAO() {
		if (appDAO == null) {
			appDAO = INJECTOR.getInstance(Key
					.get(new TypeLiteral<AppDAO<ObjectId>>() {
					}));
		}
		return appDAO;
	}

	public UserDAO<ObjectId> getUserDAO() {
		if (userDAO == null) {
			userDAO = INJECTOR.getInstance(Key
					.get(new TypeLiteral<UserDAO<ObjectId>>() {
					}));
		}
		return userDAO;
	}

	public ScrobbleDAO<ObjectId> getScrobbleDAO() {
		if (scrobbleDAO == null) {
			scrobbleDAO = INJECTOR.getInstance(Key
					.get(new TypeLiteral<ScrobbleDAO<ObjectId>>() {
					}));
		}
		return scrobbleDAO;
	}

	public RadioStationDAO<ObjectId> getRadioStationDAO() {
		if (radioStationDAO == null) {
			radioStationDAO = INJECTOR.getInstance(Key
					.get(new TypeLiteral<RadioStationDAO<ObjectId>>() {
					}));
		}
		return radioStationDAO;
	}

	public StationHistoryDAO<ObjectId> getStationHistoryDAO() {
		if (stationHistoryDAO == null) {
			stationHistoryDAO = INJECTOR.getInstance(Key
					.get(new TypeLiteral<StationHistoryDAO<ObjectId>>() {
					}));
		}
		return stationHistoryDAO;
	}

	public CascadeSaveDAO<App, ObjectId> getCascadeSaveAppDAO() {
		if (cascadeSaveAppDAO == null) {
			cascadeSaveAppDAO = INJECTOR.getInstance(Key
					.get(new TypeLiteral<CascadeSaveDAO<App, ObjectId>>() {
					}));
		}
		return cascadeSaveAppDAO;
	}

	public CascadeSaveDAO<User, ObjectId> getCascadeSaveUserDAO() {
		if (cascadeSaveUserDAO == null) {
			cascadeSaveUserDAO = INJECTOR.getInstance(Key
					.get(new TypeLiteral<CascadeSaveDAO<User, ObjectId>>() {
					}));
		}
		return cascadeSaveUserDAO;
	}

	public CascadeSaveDAO<RadioStation, ObjectId> getCascadeSaveRadioStationDAO() {
		if (cascadeSaveRadioStationDAO == null) {
			cascadeSaveRadioStationDAO = INJECTOR
					.getInstance(Key
							.get(new TypeLiteral<CascadeSaveDAO<RadioStation, ObjectId>>() {
							}));
		}
		return cascadeSaveRadioStationDAO;
	}

}
