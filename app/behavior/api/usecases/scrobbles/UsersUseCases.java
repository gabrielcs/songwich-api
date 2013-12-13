package behavior.api.usecases.scrobbles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.api.scrobbles.AppUser;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;
import models.api.subscriptions.Subscription;

import org.apache.commons.validator.routines.EmailValidator;
import org.bson.types.ObjectId;

import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.DetailedUserDTO_V0_4;
import views.api.scrobbles.UserDTO_V0_4;
import views.api.scrobbles.UserUpdateDTO_V0_4;
import views.api.stations.RadioStationDTO_V0_4;
import views.api.stations.RadioStationUpdateDTO_V0_4;
import views.api.subscriptions.SubscriptionDTO_V0_4;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import behavior.api.usecases.stations.StationsUseCases;
import behavior.api.usecases.subscriptions.SubscriptionsUseCases;

public class UsersUseCases extends UseCase {

	public UsersUseCases(RequestContext context) {
		super(context);
	}

	public void postUsers(UserDTO_V0_4 userDTO) {
		User user = getUserDAO().findByEmailAddress(userDTO.getUserEmail());
		if (user != null) {

			// user was already in the database
			for (AppUser appUser : user.getAppUsers()) {
				if (appUser.getApp().equals(getContext().getApp())) {
					// AppUser was also already in the database
					updateDTO(user, appUser, userDTO);
					MyLogger.info(String
							.format("Tried to create user \"%s\" but it was already in database with id=%s",
									userDTO.getUserEmail(), userDTO.getUserId())
							+ String.format(". devAuthToken=%s", getContext()
									.getAppDeveloper().getDevAuthToken()
									.getToken()));
					return;
				} else {
					// registers a new AppUser for that User
					AppUser newAppUser = createAppUserAndSaveNewUser(user,
							userDTO.getUserEmail());
					updateDTO(user, newAppUser, userDTO);
				}
			}
		} else {
			user = new User(userDTO.getUserEmail(), userDTO.getName(),
					userDTO.getImageUrl(), userDTO.getShortBio());
			AppUser newAppUser = createAppUserAndSaveNewUser(user,
					userDTO.getUserEmail());
			updateDTO(user, newAppUser, userDTO);
		}
		MyLogger.debug(String.format(
				"Created user \"%s\" with id=%s and authToken=%s",
				userDTO.getUserEmail(), userDTO.getUserId(),
				userDTO.getUserAuthToken()));
	}

	public List<UserDTO_V0_4> getUsers() {
		// TODO: limit the number of results
		List<User> users = getUserDAO().find().asList();

		return createDTOForGetUsers(users);
	}

	public DetailedUserDTO_V0_4 getUsersById(String userId) throws SongwichAPIException {
		User user = authorizeForGetUsersById(userId);
		return getUsers(user);
	}

	public DetailedUserDTO_V0_4 getUsersByEmail(String userEmail)
			throws SongwichAPIException {

		User user = authorizeForGetUsersByEmail(userEmail);
		return getUsers(user);
	}

	public DetailedUserDTO_V0_4 getUsers(User user) throws SongwichAPIException {
		List<RadioStation> scrobblerStations = getRadioStationDAO()
				.findByScrobblerId(user.getId());

		List<Subscription> activeSubscriptions = getSubscriptionDAO()
				.findByUserId(user.getId());

		return createDetailedDTOForGetUsers(user, scrobblerStations,
				activeSubscriptions);
	}

	public void putUsers(String userId, UserUpdateDTO_V0_4 userUpdateDTO)
			throws SongwichAPIException {

		User user = authorizePutUsers(userId);

		// process the request
		if (userUpdateDTO.getName() != null
				&& !userUpdateDTO.getName().isEmpty()) {
			user.setName(userUpdateDTO.getName());
		}
		if (userUpdateDTO.getUserEmail() != null
				&& !userUpdateDTO.getUserEmail().isEmpty()) {
			user.setEmailAddress(userUpdateDTO.getUserEmail());
		}
		if (userUpdateDTO.getImageUrl() != null
				&& !userUpdateDTO.getImageUrl().isEmpty()) {
			user.setImageUrl(userUpdateDTO.getImageUrl());
		}
		if (userUpdateDTO.getShortBio() != null
				&& !userUpdateDTO.getShortBio().isEmpty()) {
			user.setShortBio(userUpdateDTO.getShortBio());
		}
		getUserDAO().save(user,
				getContext().getAppDeveloper().getEmailAddress());

		// update output
		updateDTOPutUsers(user, userUpdateDTO);
	}

	public UserUpdateDTO_V0_4 putUsersDeactivate(String userId,
			StationStrategy stationStrategy) throws SongwichAPIException {

		User user = authorizePutUsers(userId);

		// process request
		checkStationsForDeactivation(user.getId(), stationStrategy);

		// deactivate user
		user.setDeactivated(true);
		getUserDAO().save(user,
				getContext().getAppDeveloper().getEmailAddress());

		// update output
		UserUpdateDTO_V0_4 userUpdateDTO = new UserUpdateDTO_V0_4();
		userUpdateDTO.setUserId(userId);

		return userUpdateDTO;
	}

	private void checkStationsForDeactivation(ObjectId userId,
			StationStrategy stationStrategy) throws SongwichAPIException {

		StationsUseCases stationsUseCases = new StationsUseCases(getContext());
		List<RadioStation> stations = getRadioStationDAO().findByScrobblerId(
				userId);
		for (RadioStation station : stations) {
			if (station.getScrobbler().isIndividualStation()) {
				// individual station
				stationsUseCases.putStationsDeactivate(station.getId()
						.toString());
			} else {
				// group station
				if (station.getScrobbler().getActiveScrobblersUserIds().size() == 1) {
					// only active scrobbler (deactivate station)
					stationsUseCases.putStationsDeactivate(station.getId()
							.toString());
				} else {
					// there are other active scrobblers
					RadioStationUpdateDTO_V0_4 radioStationUpdateDTO = new RadioStationUpdateDTO_V0_4();
					radioStationUpdateDTO.setScrobblerIds(Arrays.asList(userId
							.toString()));
					stationsUseCases.putStationsRemoveScrobblers(station
							.getId().toString(), radioStationUpdateDTO,
							stationStrategy);
				}
			}
		}
	}

	private User authorizeForGetUsersById(String userId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		ObjectId userIdObject = new ObjectId(userId);
		User user = getUserDAO().findById(userIdObject);
		if (user == null) {
			throw new SongwichAPIException("Non-existent userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return user;
	}

	private User authorizeForGetUsersByEmail(String userEmail)
			throws SongwichAPIException {

		EmailValidator emailValidator = EmailValidator.getInstance();
		if (!emailValidator.isValid(userEmail)) {
			throw new SongwichAPIException("Invalid userEmail",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		User user = getUserDAO().findByEmailAddress(userEmail);
		if (user == null) {
			throw new SongwichAPIException("Non-existent userEmail",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return user;
	}

	private User authorizePutUsers(String userId) throws SongwichAPIException {

		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		User user = getUserDAO().findById(new ObjectId(userId));
		if (user == null) {
			throw new SongwichAPIException("Non-existent userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		authenticatePutUsers(user);
		return user;
	}

	private void authenticatePutUsers(User user) throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException("Missing X-Songwich.userAuthToken",
					APIStatus_V0_4.UNAUTHORIZED);
		}

		// check if the user is the one that's going to be updated
		if (!user.equals(getContext().getUser())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}
	}

	/*
	 * If the User is also a new one it will be saved to the database as well.
	 */
	private AppUser createAppUserAndSaveNewUser(User user, String userEmail) {
		AuthToken userAuthToken = AuthToken.createUserAuthToken();
		AppUser newAppUser = new AppUser(getContext().getApp(), userEmail,
				userAuthToken);
		user.addAppUser(newAppUser);

		// TODO: should we change this for "update" so that we never create 2
		// users for a same email address?
		getUserDAO().save(user,
				getContext().getAppDeveloper().getEmailAddress());

		return newAppUser;
	}

	private void updateDTO(User user, AppUser newAppUser, UserDTO_V0_4 userDTO) {
		userDTO.setUserAuthToken(newAppUser.getUserAuthToken().getToken());
		userDTO.setUserId(user.getId().toString());
	}

	private List<UserDTO_V0_4> createDTOForGetUsers(Collection<User> users) {
		List<UserDTO_V0_4> usersDTO = new ArrayList<UserDTO_V0_4>();
		for (User user : users) {
			usersDTO.add(createDTOForGetUsers(user, null, null));
		}
		return usersDTO;
	}
	
	private UserDTO_V0_4 createDTOForGetUsers(User user,
			Collection<RadioStation> scrobblerStations,
			Collection<Subscription> subscriptions) {
		
		UserDTO_V0_4 userDTO = new UserDTO_V0_4();
		userDTO.setName(user.getName());
		userDTO.setUserEmail(user.getEmailAddress());
		userDTO.setUserId(user.getId().toString());
		userDTO.setImageUrl(user.getImageUrl());
		userDTO.setShortBio(user.getShortBio());

		if (scrobblerStations != null && !scrobblerStations.isEmpty()) {
			List<RadioStationDTO_V0_4> scrobblerStationsDTO = StationsUseCases
					.createDTOForGetMultipleStations(scrobblerStations);
			userDTO.setScrobblerStations(scrobblerStationsDTO);
		}

		if (subscriptions != null && !subscriptions.isEmpty()) {
			Map<Subscription, RadioStation> subscriptionStationMap = new HashMap<Subscription, RadioStation>(
					subscriptions.size());
			RadioStation station;
			for (Subscription subscription : subscriptions) {
				station = getRadioStationDAO().findById(
						subscription.getStationId());
				subscriptionStationMap.put(subscription, station);
			}

			List<SubscriptionDTO_V0_4> subscriptionsDTO = SubscriptionsUseCases
					.createDTOForGetSubscriptions(subscriptionStationMap, false);

			userDTO.setActiveStationSubscriptions(subscriptionsDTO);
		}

		return userDTO;
	}

	private DetailedUserDTO_V0_4 createDetailedDTOForGetUsers(User user,
			Collection<RadioStation> scrobblerStations,
			Collection<Subscription> subscriptions) {
		
		DetailedUserDTO_V0_4 userDTO = new DetailedUserDTO_V0_4();
		userDTO.setName(user.getName());
		userDTO.setUserEmail(user.getEmailAddress());
		userDTO.setUserId(user.getId().toString());
		userDTO.setImageUrl(user.getImageUrl());
		userDTO.setShortBio(user.getShortBio());

		if (scrobblerStations != null && !scrobblerStations.isEmpty()) {
			List<RadioStationDTO_V0_4> scrobblerStationsDTO = StationsUseCases
					.createDTOForGetMultipleStations(scrobblerStations);
			userDTO.setScrobblerStations(scrobblerStationsDTO);
		}

		if (subscriptions != null && !subscriptions.isEmpty()) {
			Map<Subscription, RadioStation> subscriptionStationMap = new HashMap<Subscription, RadioStation>(
					subscriptions.size());
			RadioStation station;
			for (Subscription subscription : subscriptions) {
				station = getRadioStationDAO().findById(
						subscription.getStationId());
				subscriptionStationMap.put(subscription, station);
			}

			List<SubscriptionDTO_V0_4> subscriptionsDTO = SubscriptionsUseCases
					.createDTOForGetSubscriptions(subscriptionStationMap, false);

			userDTO.setActiveStationSubscriptions(subscriptionsDTO);
		}

		return userDTO;
	}

	private void updateDTOPutUsers(User user, UserUpdateDTO_V0_4 userUpdateDTO) {

		userUpdateDTO.setUserId(user.getId().toString());
		userUpdateDTO.setName(user.getName());
		userUpdateDTO.setUserEmail(user.getEmailAddress());
		userUpdateDTO.setImageUrl(user.getImageUrl());
		userUpdateDTO.setShortBio(user.getShortBio());
	}

	public List<UserDTO_V0_4> createUsersDTOForGetStations(
			Collection<User> users) {
		
		List<UserDTO_V0_4> usersDTO = new ArrayList<UserDTO_V0_4>(users.size());
		UserDTO_V0_4 userDTO;
		for (User user : users) {
			userDTO = new UserDTO_V0_4();
			userDTO.setUserId(user.getId().toString());
			userDTO.setName(user.getName());
			userDTO.setImageUrl(user.getImageUrl());
			usersDTO.add(userDTO);
		}
		return usersDTO;
	}
}
