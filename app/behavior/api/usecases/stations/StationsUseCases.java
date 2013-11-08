package behavior.api.usecases.stations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.api.scrobbles.Song;
import models.api.scrobbles.User;
import models.api.stations.Group;
import models.api.stations.GroupMember;
import models.api.stations.RadioStation;
import models.api.stations.ScrobblerBridge;
import models.api.stations.StationHistoryEntry;
import models.api.stations.Track;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.UserDTO_V0_4;
import views.api.stations.RadioStationDTO_V0_4;
import views.api.stations.RadioStationUpdateDTO_V0_4;
import views.api.stations.StationSongListEntryDTO_V0_4;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import database.api.scrobbles.UserDAO;
import database.api.scrobbles.UserDAOMongo;
import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;

public class StationsUseCases extends UseCase {

	public StationsUseCases(RequestContext context) {
		super(context);
	}

	public List<RadioStationDTO_V0_4> getStations() {
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		// TODO: limit the number of results
		List<RadioStation> stations = radioStationDAO.find().asList();

		return createDTOForGetMultipleStations(stations);
	}

	public RadioStationDTO_V0_4 getStations(String stationId,
			StationStrategy stationStrategy) throws SongwichAPIException {

		RadioStation station = authorizeGetStations(stationId);

		// sets the station active if it can
		Float stationReadiness = null;
		if (!station.isActive()) {
			stationStrategy.setStation(station);
			if (stationStrategy.isStationReady()) {
				station.setActive(true);
				setNowPlaying(station, stationStrategy);
				setLookAhead(station, stationStrategy);
				// saves it
				saveStation(station);
			} else {
				stationReadiness = stationStrategy.getStationReadiness();
			}
		}
		return createStationDTO(station, stationReadiness);
	}

	private RadioStation authorizeGetStations(String stationId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(stationId)) {
			throw new SongwichAPIException("Invalid stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		RadioStation station = radioStationDAO
				.findById(new ObjectId(stationId));
		if (station == null) {
			throw new SongwichAPIException("Non-existent stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return station;
	}

	public void postStations(RadioStationDTO_V0_4 radioStationDTO,
			StationStrategy stationStrategy) throws SongwichAPIException {

		authenticatePostStations(radioStationDTO);

		// creates either a User RadioStation or a Group RadioStation
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		ScrobblerBridge scrobblerBridge;
		if (radioStationDTO.getGroupName() == null) {
			ObjectId userId = new ObjectId(radioStationDTO.getScrobblerIds()
					.get(0));
			// TODO: check if the user exists
			scrobblerBridge = new ScrobblerBridge(userDAO.findById(userId));
		} else {
			// validation guarantees there will be multiple scrobblerIds
			List<String> userIds = radioStationDTO.getScrobblerIds();
			Set<GroupMember> groupMembers = new HashSet<GroupMember>(
					userIds.size());
			User user;
			for (String userId : userIds) {
				// TODO: check if the users exist
				user = userDAO.findById(new ObjectId(userId));
				groupMembers.add(new GroupMember(user, System
						.currentTimeMillis()));
			}
			// add Group name to DTO
			scrobblerBridge = new ScrobblerBridge(new Group(
					radioStationDTO.getGroupName(), groupMembers));
		}
		String imageUrl = radioStationDTO.getImageUrl();
		RadioStation station = new RadioStation(
				radioStationDTO.getStationName(), scrobblerBridge, imageUrl);

		// checks if station can be activated and activates it
		stationStrategy.setStation(station);
		if (stationStrategy.isStationReady()) {
			activateStation(station, stationStrategy);
		}
		// save it
		saveStation(station);

		if (station.isActive()) {
			updateDTOForPostStations(station, null, radioStationDTO);
		} else {
			Float stationReadiness = stationStrategy.getStationReadiness();
			updateDTOForPostStations(station, stationReadiness, radioStationDTO);
		}
	}

	public void putStations(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {

		RadioStation station = authorizePutStations(stationId,
				radioStationUpdateDTO);

		// update imageUrl
		if (radioStationUpdateDTO.getImageUrl() != null) {
			station.setImageUrl(radioStationUpdateDTO.getImageUrl());
		}

		// update station name
		if (radioStationUpdateDTO.getStationName() != null) {
			station.setName(radioStationUpdateDTO.getStationName());
		}

		savePutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public void putStationsAddScrobblers(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO,
			StationStrategy stationStrategy) throws SongwichAPIException {

		RadioStation station = authorizePutStationsScrobblers(stationId,
				radioStationUpdateDTO);

		// add new scrobblers
		for (String scrobblerId : radioStationUpdateDTO.getScrobblerIds()) {
			User user = authorizePutStationsAddRemoveScrobbler(scrobblerId);
			// add new scrobbler
			if (!station.getScrobbler().getActiveScrobblersUserIds()
					.contains(new ObjectId(scrobblerId))) {

				station.getScrobbler().getGroup().addGroupMember(user);
			}
		}

		// checks if it can activate the station
		if (!station.isActive()) {
			stationStrategy.setStation(station);
			if (stationStrategy.isStationReady()) {
				station.setActive(true);
			}
		}

		savePutStationsScrobblers(station, radioStationUpdateDTO);
	}

	private User authorizePutStationsAddRemoveScrobbler(String scrobblerId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(scrobblerId)) {
			throw new SongwichAPIException("Invalid scrobblerId: "
					+ scrobblerId, APIStatus_V0_4.INVALID_PARAMETER);
		}

		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User user = userDAO.findById(new ObjectId(scrobblerId));
		if (user == null) {
			throw new SongwichAPIException("Non-existent scrobblerId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return user;
	}

	public void putStationsRemoveScrobblers(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO,
			StationStrategy stationStrategy) throws SongwichAPIException {

		RadioStation station = authorizePutStationsScrobblers(stationId,
				radioStationUpdateDTO);

		// remove scrobblers
		for (String scrobblerId : radioStationUpdateDTO.getScrobblerIds()) {
			User user = authorizePutStationsAddRemoveScrobbler(scrobblerId);
			// remove scrobbler
			if (station.getScrobbler().getActiveScrobblersUserIds()
					.contains(new ObjectId(scrobblerId))) {
				station.getScrobbler().getGroup().deactivateGroupMember(user);
			}
		}

		if (station.getScrobbler().getActiveScrobblersUserIds().size() < 1) {
			throw new SongwichAPIException(
					"Station has to have at least 1 active scrobbler",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// checks if it needs to deactivate station
		if (station.isActive()) {
			stationStrategy.setStation(station);
			if (!stationStrategy.isStationReady()) {
				station.setActive(false);
			}
		}

		savePutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public void postNextSong(RadioStationUpdateDTO_V0_4 radioStationUpdateDTO,
			StationStrategy stationStrategy) throws SongwichAPIException {

		RadioStation station = authorizePostNextSong(radioStationUpdateDTO);

		// run the algorithm to decide what the lookAhead Song will be
		stationStrategy.setStation(station);
		Song lookAheadSong = stationStrategy.getNextSong();

		// find out who the lookAhead scrobblers are if it's a group station
		List<User> lookAheadScrobblers = new ArrayList<User>();
		if (station.getScrobbler().isGroupStation()) {
			Set<ObjectId> lookAheadScrobblersIds = stationStrategy
					.getNextSongRecentScrobblers();
			UserDAO<ObjectId> userDao = new UserDAOMongo();
			lookAheadScrobblers = userDao
					.findUsersByIds(lookAheadScrobblersIds);
		}

		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();

		// turn the lookAhead Track into next and set the new lookAhead
		Track nowPlayingTrack = station.getLookAhead();
		StationHistoryEntry nowPlayingHistoryEntry = nowPlayingTrack
				.getStationHistoryEntry();
		nowPlayingHistoryEntry.setTimestamp(System.currentTimeMillis());
		stationHistoryDAO.save(nowPlayingHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		station.setNowPlaying(nowPlayingTrack);

		// create and save the StationHistoryEntry for the lookAhead Track (with
		// timestamp=null)
		StationHistoryEntry lookAheadHistoryEntry = new StationHistoryEntry(
				station.getId(), lookAheadSong, null);
		stationHistoryDAO.save(lookAheadHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		station.setLookAhead(new Track(lookAheadHistoryEntry,
				lookAheadScrobblers));

		// update radioStation
		saveStation(station);

		// update the DataTransferObject
		if (station.getScrobbler().isGroupStation()) {
			updateDTOForPostNextSong(radioStationUpdateDTO,
					nowPlayingHistoryEntry, station.getNowPlaying()
							.getSongScrobblers(), lookAheadHistoryEntry,
					lookAheadScrobblers);
		} else {
			updateDTOForPostNextSong(radioStationUpdateDTO,
					nowPlayingHistoryEntry, lookAheadHistoryEntry);
		}
	}

	private RadioStation authorizePostNextSong(
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {

		// fetch the RadioStation
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		RadioStation station = radioStationDAO.findById(new ObjectId(
				radioStationUpdateDTO.getStationId()));
		if (station == null) {
			throw new SongwichAPIException("Invalid stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (!station.isActive()) {
			throw new SongwichAPIException("This station is not active yet.",
					APIStatus_V0_4.BAD_REQUEST);
		}

		return station;
	}

	// it doesn't save it
	private void activateStation(RadioStation station,
			StationStrategy stationStrategy) throws SongwichAPIException {

		station.setActive(true);
		setNowPlaying(station, stationStrategy).getStationHistoryEntry();
		setLookAhead(station, stationStrategy).getStationHistoryEntry();
	}

	private Track setLookAhead(RadioStation station,
			StationStrategy stationStrategy) throws SongwichAPIException {

		Track track = saveHistoryEntryAndGetTrack(station, stationStrategy);
		station.setLookAhead(track);
		return track;
	}

	private Track setNowPlaying(RadioStation station,
			StationStrategy stationStrategy) throws SongwichAPIException {

		Track track = saveHistoryEntryAndGetTrack(station, stationStrategy);
		station.setNowPlaying(track);
		return track;
	}

	/**
	 * @param stationStrategyAlreadySet
	 *            it must have previously invoked setStation() or it will throw
	 *            an IllegalStateException
	 */
	private Track saveHistoryEntryAndGetTrack(RadioStation station,
			StationStrategy stationStrategyAlreadySet)
			throws SongwichAPIException {

		// no need to re-do stationStrategy.setStation(station)
		Song song = stationStrategyAlreadySet.getNextSong();
		StationHistoryEntry historyEntry = new StationHistoryEntry(
				station.getId(), song, System.currentTimeMillis());
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		stationHistoryDAO.save(historyEntry, getContext().getAppDeveloper()
				.getEmailAddress());

		Track track;
		if (station.getScrobbler().isGroupStation()) {
			Set<ObjectId> songScrobblersIds = stationStrategyAlreadySet
					.getNextSongRecentScrobblers();
			UserDAO<ObjectId> userDAO = new UserDAOMongo();
			List<User> songScrobblers = userDAO
					.findUsersByIds(songScrobblersIds);
			track = new Track(historyEntry, songScrobblers);
		} else {
			track = new Track(historyEntry);
		}

		return track;
	}

	private RadioStation authorizePutStations(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {

		if (!ObjectId.isValid(stationId)) {
			throw new SongwichAPIException("Invalid stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		RadioStation station = radioStationDAO
				.findById(new ObjectId(stationId));
		if (station == null) {
			throw new SongwichAPIException("Non-existent stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// check if the user is already one of the stations's scrobbler and will
		// continue to be
		authenticatePutStations(station, radioStationUpdateDTO);

		return station;
	}

	private RadioStation authorizePutStationsScrobblers(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {

		RadioStation station = authorizePutStations(stationId,
				radioStationUpdateDTO);

		if (radioStationUpdateDTO.getScrobblerIds() == null
				|| radioStationUpdateDTO.getScrobblerIds().isEmpty()) {

			throw new SongwichAPIException("Missing scrobblerId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (station.getScrobbler().isIndividualStation()) {
			throw new SongwichAPIException(
					"Not allowed to change scrobblers on a user station",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		return station;
	}

	private void authenticatePostStations(RadioStationDTO_V0_4 radioStationDTO)
			throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException("Missing X-Songwich.userAuthToken",
					APIStatus_V0_4.UNAUTHORIZED);
		}

		if (!radioStationDTO.getScrobblerIds().contains(
				getContext().getUser().getId().toString())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}
	}

	private void authenticatePutStations(RadioStation station,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {

		if (getContext().getUser() == null) {
			throw new SongwichAPIException("Missing X-Songwich.userAuthToken",
					APIStatus_V0_4.UNAUTHORIZED);
		}

		// check if the user is already one of the stations's scrobblers
		if (!station.getScrobbler().getActiveScrobblersUserIds()
				.contains(getContext().getUser().getId())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}
	}

	private void saveStation(RadioStation radioStation) {
		// save RadioStation
		RadioStationDAOMongo radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.cascadeSave(radioStation, getContext()
				.getAppDeveloper().getEmailAddress());
	}

	private void savePutStationsScrobblers(RadioStation station,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO) {

		// saves it
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.save(station, getContext().getAppDeveloper()
				.getEmailAddress());
		// updates the user output
		updateDTOForPutStations(radioStationUpdateDTO, station);
	}

	private static void updateDTOForPostStations(RadioStation station,
			Float stationReadiness, RadioStationDTO_V0_4 radioStationDTO) {

		radioStationDTO.setStationId(station.getId().toString());
		updateStationDTOWithReadinessOrSongs(station, stationReadiness,
				radioStationDTO);
	}

	private static RadioStationDTO_V0_4 createStationDTO(RadioStation station,
			Float stationReadiness) {

		RadioStationDTO_V0_4 stationDTO = createBasicDTOForStations(station);
		updateStationDTOWithReadinessOrSongs(station, stationReadiness,
				stationDTO);
		return stationDTO;
	}

	private static void updateStationDTOWithReadinessOrSongs(
			RadioStation station, Float stationReadiness,
			RadioStationDTO_V0_4 radioStationDTO) {

		if (station.isActive()) {
			updateDTOForGetActiveStation(station, radioStationDTO);
		} else {
			updateDTOForGetInactiveStation(station, stationReadiness,
					radioStationDTO);
		}
	}

	public static List<RadioStationDTO_V0_4> createDTOForGetMultipleStations(
			List<RadioStation> stations) {

		List<RadioStationDTO_V0_4> stationsDTO = new ArrayList<RadioStationDTO_V0_4>();
		RadioStationDTO_V0_4 stationDTO;
		for (RadioStation station : stations) {
			stationDTO = createBasicDTOForStations(station);
			stationDTO.setIsActive(station.isActive().toString());
			stationsDTO.add(stationDTO);
		}
		return stationsDTO;
	}

	// id, name, url, scrobblers (no songs, no activeness, no readiness)
	private static RadioStationDTO_V0_4 createBasicDTOForStations(
			RadioStation station) {
		RadioStationDTO_V0_4 stationDTO = new RadioStationDTO_V0_4();
		stationDTO.setStationId(station.getId().toString());
		stationDTO.setStationName(station.getName());
		stationDTO.setImageUrl(station.getImageUrl());
		if (station.getScrobbler().isGroupStation()) {
			stationDTO
					.setGroupName(station.getScrobbler().getGroup().getName());
		}

		List<String> scrobblerIds = new ArrayList<String>();
		for (ObjectId scrobblerId : station.getScrobbler()
				.getActiveScrobblersUserIds()) {
			scrobblerIds.add(scrobblerId.toString());
		}
		stationDTO.setScrobblerIds(scrobblerIds);

		return stationDTO;
	}

	private static void updateDTOForGetInactiveStation(RadioStation station,
			Float stationReadiness, RadioStationDTO_V0_4 stationDTO) {

		stationDTO.setStationReadiness(String.format("%.2f", stationReadiness));
	}

	private static void updateDTOForGetActiveStation(RadioStation station,
			RadioStationDTO_V0_4 stationDTO) {
		// nowPlaying
		StationSongListEntryDTO_V0_4 nowPlayingDTO = new StationSongListEntryDTO_V0_4();
		nowPlayingDTO.setArtistName(station.getNowPlaying()
				.getStationHistoryEntry().getSong().getArtistsNames()
				.toString());
		nowPlayingDTO.setTrackTitle(station.getNowPlaying()
				.getStationHistoryEntry().getSong().getSongTitle());
		nowPlayingDTO.setIdForFeedback(station.getNowPlaying()
				.getStationHistoryEntry().getId().toString());
		stationDTO.setNowPlaying(nowPlayingDTO);

		// lookAhead
		StationSongListEntryDTO_V0_4 lookAheadDTO = new StationSongListEntryDTO_V0_4();
		lookAheadDTO.setArtistName(station.getLookAhead()
				.getStationHistoryEntry().getSong().getArtistsNames()
				.toString());
		lookAheadDTO.setTrackTitle(station.getLookAhead()
				.getStationHistoryEntry().getSong().getSongTitle());
		lookAheadDTO.setIdForFeedback(station.getLookAhead()
				.getStationHistoryEntry().getId().toString());
		stationDTO.setLookAhead(lookAheadDTO);

		// recently scrobbled by
		if (station.getScrobbler().isGroupStation()) {
			nowPlayingDTO.setRecentScrobblers(createScrobblersDTO(station
					.getNowPlaying().getSongScrobblers()));

			lookAheadDTO.setRecentScrobblers(createScrobblersDTO(station
					.getLookAhead().getSongScrobblers()));
		}
	}

	private static void updateDTOForPostNextSong(
			RadioStationUpdateDTO_V0_4 radioStationDTO,
			StationHistoryEntry nowPlayingHistoryEntry,
			List<User> nowPlayingScrobblers,
			StationHistoryEntry lookAheadHistoryEntry,
			List<User> lookAheadScrobblers) {

		updateDTOForPostNextSong(radioStationDTO, nowPlayingHistoryEntry,
				lookAheadHistoryEntry);

		// updates the song scrobblers
		StationSongListEntryDTO_V0_4 nowPlayingSongListEntryDTO = radioStationDTO
				.getNowPlaying();
		nowPlayingSongListEntryDTO
				.setRecentScrobblers(createScrobblersDTO(nowPlayingScrobblers));
		StationSongListEntryDTO_V0_4 lookAheadSongListEntryDTO = radioStationDTO
				.getLookAhead();
		lookAheadSongListEntryDTO
				.setRecentScrobblers(createScrobblersDTO(lookAheadScrobblers));
	}

	private static List<UserDTO_V0_4> createScrobblersDTO(List<User> scrobblers) {
		List<UserDTO_V0_4> scrobblersDTO = new ArrayList<UserDTO_V0_4>();
		UserDTO_V0_4 userDTO;
		for (User scrobbler : scrobblers) {
			if (scrobbler.getName() != null && !scrobbler.getName().isEmpty()) {
				userDTO = new UserDTO_V0_4();
				userDTO.setUserId(scrobbler.getId().toString());
				userDTO.setName(scrobbler.getName());
				scrobblersDTO.add(userDTO);
			}
		}

		// don't show anything if the user(s) hasn't set up his name
		if (scrobblersDTO.isEmpty()) {
			return null;
		}

		return scrobblersDTO;
	}

	private static void updateDTOForPostNextSong(
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO,
			StationHistoryEntry nowPlayingHistoryEntry,
			StationHistoryEntry lookAheadHistoryEntry) {

		// sets StationSongListDTO's nowPlaying
		StationSongListEntryDTO_V0_4 nowPlayingSongListEntryDTO = new StationSongListEntryDTO_V0_4();
		nowPlayingSongListEntryDTO.setArtistName(nowPlayingHistoryEntry
				.getSong().getArtistsNames().toString());
		nowPlayingSongListEntryDTO.setTrackTitle(nowPlayingHistoryEntry
				.getSong().getSongTitle());
		nowPlayingSongListEntryDTO.setIdForFeedback(nowPlayingHistoryEntry
				.getId().toString());
		radioStationUpdateDTO.setNowPlaying(nowPlayingSongListEntryDTO);

		// sets StationSongListDTO's lookAhead
		StationSongListEntryDTO_V0_4 lookAheadSongListEntryDTO = new StationSongListEntryDTO_V0_4();
		lookAheadSongListEntryDTO.setArtistName(lookAheadHistoryEntry.getSong()
				.getArtistsNames().toString());
		lookAheadSongListEntryDTO.setTrackTitle(lookAheadHistoryEntry.getSong()
				.getSongTitle());
		lookAheadSongListEntryDTO.setIdForFeedback(lookAheadHistoryEntry
				.getId().toString());
		radioStationUpdateDTO.setLookAhead(lookAheadSongListEntryDTO);
	}

	private static RadioStationUpdateDTO_V0_4 updateDTOForPutStations(
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO,
			RadioStation station) {

		radioStationUpdateDTO.setStationId(station.getId().toString());
		radioStationUpdateDTO.setStationName(station.getName());
		radioStationUpdateDTO.setActive(station.isActive().toString());
		radioStationUpdateDTO.setImageUrl(station.getImageUrl());

		List<String> scrobblerIds = new ArrayList<String>();
		for (ObjectId scrobblerId : station.getScrobbler()
				.getActiveScrobblersUserIds()) {
			scrobblerIds.add(scrobblerId.toString());
		}
		radioStationUpdateDTO.setScrobblerIds(scrobblerIds);

		return radioStationUpdateDTO;
	}
}
