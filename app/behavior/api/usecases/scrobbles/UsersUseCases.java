package behavior.api.usecases.scrobbles;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.AppUser;
import models.api.scrobbles.AuthToken;
import models.api.scrobbles.User;
import models.api.stations.RadioStation;

import org.bson.types.ObjectId;

import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.UserDTO_V0_4;
import views.api.stations.RadioStationDTO_V0_4;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import behavior.api.usecases.stations.StationsUseCases;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;
import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;

public class UsersUseCases extends UseCase {

	public UsersUseCases(RequestContext context) {
		super(context);
	}

	public void postUsers(UserDTO_V0_4 userDTO) {
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User user = userDAO.findByEmailAddress(userDTO.getUserEmail());
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
					AppUser newAppUser = saveNewAppUser(user,
							userDTO.getUserEmail());
					updateDTO(user, newAppUser, userDTO);
				}
			}
		} else {
			// TODO: add name to UserDTO
			user = new User(userDTO.getUserEmail(), userDTO.getName());
			AppUser newAppUser = saveNewAppUser(user, userDTO.getUserEmail());
			updateDTO(user, newAppUser, userDTO);
		}
		MyLogger.info(String.format(
				"Created user \"%s\" with id=%s and authToken=%s",
				userDTO.getUserEmail(), userDTO.getUserId(),
				userDTO.getUserAuthToken()));
	}

	/*
	 * If the User is also a new one it will be saved to the database as well.
	 */
	private AppUser saveNewAppUser(User user, String userEmail) {
		AuthToken userAuthToken = AuthToken.createUserAuthToken();
		AppUser newAppUser = new AppUser(getContext().getApp(), userEmail,
				userAuthToken);
		user.addAppUser(newAppUser);

		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		userDAO.save(user, getContext().getAppDeveloper().getEmailAddress());

		return newAppUser;
	}

	private void updateDTO(User user, AppUser newAppUser, UserDTO_V0_4 userDTO) {
		userDTO.setUserAuthToken(newAppUser.getUserAuthToken().getToken());
		userDTO.setUserId(user.getId().toString());
	}

	public List<UserDTO_V0_4> getUsers() {
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		// TODO: limit the number of results
		List<User> users = userDAO.find().asList();

		return createDTOForGetUsers(users);
	}

	public UserDTO_V0_4 getUsers(String userId) throws SongwichAPIException {
		if (!ObjectId.isValid(userId)) {
			throw new SongwichAPIException("Invalid userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		} else if (getContext().getUser() == null) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		ObjectId userIdObject = new ObjectId(userId);
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User user = userDAO.findById(userIdObject);
		if (user == null) {
			throw new SongwichAPIException("Non-existent userId",
					APIStatus_V0_4.INVALID_PARAMETER);
		} else if (!user.equals(getContext().getUser())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}

		RadioStationDAO<ObjectId> stationDAO = new RadioStationDAOMongo();
		List<RadioStation> scrobblerStations = stationDAO
				.findByScrobblerId(userIdObject);

		return createDTOForGetUsers(user, scrobblerStations);
	}

	private List<UserDTO_V0_4> createDTOForGetUsers(List<User> users) {
		List<UserDTO_V0_4> usersDTO = new ArrayList<UserDTO_V0_4>();
		for (User user : users) {
			usersDTO.add(createDTOForGetUsers(user, null));
		}
		return usersDTO;
	}

	private UserDTO_V0_4 createDTOForGetUsers(User user,
			List<RadioStation> scrobblerStations) {
		UserDTO_V0_4 userDTO = new UserDTO_V0_4();
		userDTO.setName(user.getName());
		userDTO.setUserEmail(user.getEmailAddress());
		userDTO.setUserId(user.getId().toString());

		if (scrobblerStations != null && !scrobblerStations.isEmpty()) {
			List<RadioStationDTO_V0_4> scrobblerStationsDTO = StationsUseCases
					.createDTOForGetStations(scrobblerStations);
			userDTO.setScrobblerStations(scrobblerStationsDTO);
		}

		return userDTO;
	}
}
