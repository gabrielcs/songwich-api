package util.api;

import models.api.scrobbles.App;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import database.api.CascadeSaveDAO;
import database.api.scrobbles.AppDAO;
import database.api.scrobbles.AppDAOMongo;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;
import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;
import database.api.subscriptions.SubscriptionDAO;
import database.api.subscriptions.SubscriptionDAOMongo;

public class MongoDependencyInjectionModule extends AbstractModule {
	@Override
	protected void configure() {
		// AppDAO
		bind(new TypeLiteral<AppDAO<ObjectId>>() {}).to(AppDAOMongo.class);
		bind(new TypeLiteral<CascadeSaveDAO<App, ObjectId>>() {}).to(AppDAOMongo.class);
		bind(AppDAO.class).to(AppDAOMongo.class);
		
		// ScrobbleDAO
		bind(new TypeLiteral<ScrobbleDAO<ObjectId>>() {}).to(ScrobbleDAOMongo.class);
		bind(ScrobbleDAO.class).to(ScrobbleDAOMongo.class);
		
		// UserDAO
		bind(new TypeLiteral<UserDAO<ObjectId>>() {}).to(UserDAOMongo.class);
		bind(new TypeLiteral<CascadeSaveDAO<User, ObjectId>>() {}).to(UserDAOMongo.class);
		bind(UserDAO.class).to(UserDAOMongo.class);
		
		// RadioStationDAO
		bind(new TypeLiteral<RadioStationDAO<ObjectId>>() {}).to(RadioStationDAOMongo.class);
		bind(new TypeLiteral<CascadeSaveDAO<RadioStation, ObjectId>>() {}).to(RadioStationDAOMongo.class);
		bind(RadioStationDAO.class).to(RadioStationDAOMongo.class);
		
		// StationHistoryDAO
		bind(new TypeLiteral<StationHistoryDAO<ObjectId>>() {}).to(StationHistoryDAOMongo.class);
		bind(StationHistoryDAO.class).to(StationHistoryDAOMongo.class);
		
		// SubscriptionDAO
		bind(new TypeLiteral<SubscriptionDAO<ObjectId>>() {}).to(SubscriptionDAOMongo.class);
		bind(SubscriptionDAO.class).to(SubscriptionDAOMongo.class);
	}
}
