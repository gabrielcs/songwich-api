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
import views.api.scrobbles.UserInputDTO_V0_4;
import views.api.scrobbles.UserOutputDTO_V0_4;
import views.api.scrobbles.UserUpdateInputDTO_V0_4;
import views.api.stations.RadioStationOutputDTO_V0_4;
import views.api.stations.RadioStationUpdateInputDTO_V0_4;
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

	public UserOutputDTO_V0_4 postUsers(UserInputDTO_V0_4 userInputDTO) {
		UserOutputDTO_V0_4 userOutputDTO = null;
		User user = getUserDAO()
				.findByEmailAddress(userInputDTO.getUserEmail());
		if (user != null) {

			// user was already in the database
			for (AppUser appUser : user.getAppUsers()) {
				if (appUser.getApp().equals(getContext().getApp())) {
					// AppUser was also already in the database
					userOutputDTO = createOutputUserDTO(user, appUser,
							userInputDTO);
					MyLogger.info(String
							.format("Tried to create user \"%s\" but it was already in database with id=%s",
									userInputDTO.getUserEmail(),
									userOutputDTO.getUserId())
							+ String.format(". devAuthToken=%s", getContext()
									.getAppDeveloper().getDevAuthToken()
									.getToken()));
					return userOutputDTO;
				} else {
					// registers a new AppUser for that User
					AppUser newAppUser = createAppUserAndSaveNewUser(user,
							userInputDTO.getUserEmail());
					userOutputDTO = createOutputUserDTO(user, newAppUser,
							userInputDTO);
					// TODO: test with 1 User and multiple AppUser's
				}
			}
		} else {
			user = new User(userInputDTO.getUserEmail(),
					userInputDTO.getName(), userInputDTO.getImageUrl(),
					userInputDTO.getShortBio());
			AppUser newAppUser = createAppUserAndSaveNewUser(user,
					userInputDTO.getUserEmail());
			userOutputDTO = createOutputUserDTO(user, newAppUser, userInputDTO);
		}
		MyLogger.debug(String.format(
				"Created user \"%s\" with id=%s and authToken=%s",
				userOutputDTO.getUserEmail(), userOutputDTO.getUserId(),
				userOutputDTO.getUserAuthToken()));
		return userOutputDTO;
	}

	public List<UserOutputDTO_V0_4> getUsers() {
		// TODO: limit the number of results
		List<User> users = getUserDAO().find().asList();

		return createDTOForGetUsers(users);
	}

	public UserOutputDTO_V0_4 getUsersById(String userId)
			throws SongwichAPIException {
		User user = authorizeForGetUsersById(userId);
		return getUsers(user);
	}

	public UserOutputDTO_V0_4 getUsersByEmail(String userEmail)
			throws SongwichAPIException {

		User user = authorizeForGetUsersByEmail(userEmail);
		return getUsers(user);
	}

	public UserOutputDTO_V0_4 getUsers(User user) throws SongwichAPIException {
		List<RadioStation> scrobblerStations = getRadioStationDAO()
				.findByScrobblerId(user.getId());

		List<Subscription> activeSubscriptions = getSubscriptionDAO()
				.findByUserId(user.getId());

		return createDetailedDTOForGetUsers(user, scrobblerStations,
				activeSubscriptions);
	}

	public UserOutputDTO_V0_4 putUsers(String userId,
			UserUpdateInputDTO_V0_4 userUpdateDTO) throws SongwichAPIException {

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
		return createDTOPutUsers(user, userUpdateDTO);
	}

	public UserOutputDTO_V0_4 putUsersDeactivate(String userId,
			StationStrategy stationStrategy) throws SongwichAPIException {

		User user = authorizePutUsers(userId);

		// process request
		checkStationsForDeactivation(user.getId(), stationStrategy);

		// deactivate user
		user.setDeactivated(true);
		getUserDAO().save(user,
				getContext().getAppDeveloper().getEmailAddress());

		// output
		UserOutputDTO_V0_4 userOutputDTO = new UserOutputDTO_V0_4();
		userOutputDTO.setUserId(userId);

		return userOutputDTO;
	}

	public UserOutputDTO_V0_4 putUsersMarkAsVerified(String userId)
			throws SongwichAPIException {

		User user = authorizePutUsers(userId);

		// mark user as verified
		user.setVerified(true);
		getUserDAO().save(user,
				getContext().getAppDeveloper().getEmailAddress());

		// mark the user's individual station(s) as verified
		List<RadioStation> scrobblerStations = getRadioStationDAO()
				.findByScrobblerId(user.getId());
		for (RadioStation station : scrobblerStations) {
			if (station.getScrobbler().isIndividualStation()
					&& station.isVerified() == false) {
				station.setVerified(true);
				getRadioStationDAO().save(station,
						getContext().getAppDeveloper().getEmailAddress());
			}
		}

		// output
		return createDTOForGetUsers(user, null, null);
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
					RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO = new RadioStationUpdateInputDTO_V0_4();
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

	private UserOutputDTO_V0_4 createOutputUserDTO(User user,
			AppUser newAppUser, UserInputDTO_V0_4 userInputDTO) {
		UserOutputDTO_V0_4 userOutputDTO = new UserOutputDTO_V0_4(userInputDTO);
		userOutputDTO
				.setUserAuthToken(newAppUser.getUserAuthToken().getToken());
		userOutputDTO.setUserId(user.getId().toString());
		userOutputDTO.setVerified(user.isVerified().toString());
		return userOutputDTO;
	}

	private List<UserOutputDTO_V0_4> createDTOForGetUsers(Collection<User> users) {
		List<UserOutputDTO_V0_4> usersDTO = new ArrayList<UserOutputDTO_V0_4>();
		for (User user : users) {
			usersDTO.add(createDTOForGetUsers(user, null, null));
		}
		return usersDTO;
	}

	private UserOutputDTO_V0_4 createDTOForGetUsers(User user,
			Collection<RadioStation> scrobblerStations,
			Collection<Subscription> subscriptions) {

		UserOutputDTO_V0_4 userDTO = new UserOutputDTO_V0_4();
		userDTO.setName(user.getName());
		userDTO.setUserEmail(user.getEmailAddress());
		userDTO.setUserId(user.getId().toString());
		userDTO.setImageUrl(user.getImageUrl());
		userDTO.setShortBio(user.getShortBio());
		userDTO.setVerified(user.isVerified().toString());

		if (scrobblerStations != null && !scrobblerStations.isEmpty()) {
			List<RadioStationOutputDTO_V0_4> scrobblerStationsDTO = StationsUseCases
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

	private UserOutputDTO_V0_4 createDetailedDTOForGetUsers(User user,
			Collection<RadioStation> scrobblerStations,
			Collection<Subscription> subscriptions) {

		UserOutputDTO_V0_4 userDTO = new UserOutputDTO_V0_4();
		userDTO.setName(user.getName());
		userDTO.setUserEmail(user.getEmailAddress());
		userDTO.setUserId(user.getId().toString());
		userDTO.setImageUrl(user.getImageUrl());
		userDTO.setShortBio(user.getShortBio());
		userDTO.setVerified(user.isVerified().toString());

		List<RadioStationOutputDTO_V0_4> scrobblerStationsDTO = StationsUseCases
				.createDTOForGetMultipleStations(scrobblerStations);
		// make sure activeStationSubscriptions is never null or there might be
		// a bug on the front-end
		userDTO.setScrobblerStations(scrobblerStationsDTO);

		Map<Subscription, RadioStation> subscriptionStationMap = new HashMap<Subscription, RadioStation>(
				subscriptions.size());
		if (subscriptions != null) {
			RadioStation station;
			for (Subscription subscription : subscriptions) {
				station = getRadioStationDAO().findById(
						subscription.getStationId());
				subscriptionStationMap.put(subscription, station);
			}
		}
		List<SubscriptionDTO_V0_4> subscriptionsDTO = SubscriptionsUseCases
				.createDTOForGetSubscriptions(subscriptionStationMap, false);
		// make sure activeStationSubscriptions is never null or there might be
		// a bug on the front-end
		userDTO.setActiveStationSubscriptions(subscriptionsDTO);

		return userDTO;
	}

	private UserOutputDTO_V0_4 createDTOPutUsers(User user,
			UserUpdateInputDTO_V0_4 userUpdateInputDTO) {
		UserOutputDTO_V0_4 userOutputDTO = new UserOutputDTO_V0_4(
				userUpdateInputDTO);
		userOutputDTO.setUserId(user.getId().toString());
		userOutputDTO.setName(user.getName());
		userOutputDTO.setUserEmail(user.getEmailAddress());
		userOutputDTO.setImageUrl(user.getImageUrl());
		userOutputDTO.setShortBio(user.getShortBio());
		userOutputDTO.setVerified(user.isVerified().toString());
		return userOutputDTO;
	}

	public List<UserOutputDTO_V0_4> createUsersDTOForGetStations(
			Collection<User> users) {

		List<UserOutputDTO_V0_4> usersDTO = new ArrayList<UserOutputDTO_V0_4>(
				users.size());
		UserOutputDTO_V0_4 userDTO;
		for (User user : users) {
			userDTO = new UserOutputDTO_V0_4();
			userDTO.setUserId(user.getId().toString());
			userDTO.setName(user.getName());
			userDTO.setImageUrl(user.getImageUrl());
			userDTO.setVerified(user.isVerified().toString());
			usersDTO.add(userDTO);
		}
		return usersDTO;
	}
}
