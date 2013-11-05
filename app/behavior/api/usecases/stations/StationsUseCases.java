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

import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.UserDTO_V0_4;
import views.api.stations.RadioStationDTO_V0_4;
import views.api.stations.RadioStationUpdateDTO_V0_4;
import views.api.stations.StationSongListEntryDTO_V0_4;
import behavior.api.algorithms.NaiveStationStrategy;
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

		return createDTOForGetStations(stations);
	}

	public RadioStationDTO_V0_4 getStations(String stationId)
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

		return createDTOForGetStations(station, true);
	}

	public void postStations(RadioStationDTO_V0_4 radioStationDTO)
			throws SongwichAPIException {

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
		// StationStrategy stationStrategyNowPlaying = new
		// PseudoDMCAStationStrategy(
		// radioStation);
		StationStrategy stationStrategy = new NaiveStationStrategy(station);
		if (stationStrategy.isStationReady()) {
			station.setActive(true);
			setNowPlaying(station);
			setLookAhead(station);
		}

		// save it
		saveStation(station);

		// update the DTO
		createDTOForPostStations(station, radioStationDTO);
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
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {
		RadioStation station = authorizePutStationsScrobblers(stationId,
				radioStationUpdateDTO);

		// add new scrobblers
		for (String scrobblerId : radioStationUpdateDTO.getScrobblerIds()) {
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

			// add new scrobbler
			if (!station.getScrobbler().getActiveScrobblersUserIds()
					.contains(new ObjectId(scrobblerId))) {

				station.getScrobbler().getGroup().addGroupMember(user);
			}
		}

		savePutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public void putStationsRemoveScrobblers(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {
		RadioStation station = authorizePutStationsScrobblers(stationId,
				radioStationUpdateDTO);

		// remove scrobblers
		for (String scrobblerId : radioStationUpdateDTO.getScrobblerIds()) {
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

		savePutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public void postNextSong(RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();

		// fetch the RadioStation
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		RadioStation radioStation = radioStationDAO.findById(new ObjectId(
				radioStationUpdateDTO.getStationId()));
		if (radioStation == null) {
			throw new SongwichAPIException("Invalid stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// run the algorithm to decide what the lookAhead Song will be
		// StationStrategy stationStrategy = new PseudoDMCAStationStrategy(
		// radioStation);
		StationStrategy stationStrategy = new NaiveStationStrategy(radioStation);
		Song lookAheadSong = stationStrategy.getNextSong();

		// find out who the lookAhead scrobblers are if it's a group station
		List<User> lookAheadScrobblers = new ArrayList<User>();
		if (radioStation.getScrobbler().isGroupStation()) {
			Set<ObjectId> lookAheadScrobblersIds = stationStrategy
					.getNextSongRecentScrobblers();
			MyLogger.debug("lookAheadScrobblersIds: " + lookAheadScrobblersIds);
			UserDAO<ObjectId> userDao = new UserDAOMongo();
			lookAheadScrobblers = userDao
					.findUsersByIds(lookAheadScrobblersIds);

			MyLogger.debug("lookAheadScrobblers: " + lookAheadScrobblers);
		}

		// turn the lookAhead Track into next and set the new lookAhead
		Track nowPlayingTrack = radioStation.getLookAhead();
		StationHistoryEntry nowPlayingHistoryEntry = nowPlayingTrack
				.getStationHistoryEntry();
		nowPlayingHistoryEntry.setTimestamp(System.currentTimeMillis());
		stationHistoryDAO.save(nowPlayingHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		radioStation.setNowPlaying(nowPlayingTrack);

		// create and save the StationHistoryEntry for the lookAhead Track (with
		// timestamp=null)
		StationHistoryEntry lookAheadHistoryEntry = new StationHistoryEntry(
				radioStation.getId(), lookAheadSong, null);
		stationHistoryDAO.save(lookAheadHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		radioStation.setLookAhead(new Track(lookAheadHistoryEntry,
				lookAheadScrobblers));

		// update radioStation
		radioStationDAO.save(radioStation, getContext().getAppDeveloper()
				.getEmailAddress());

		// update the DataTransferObject
		if (radioStation.getScrobbler().isGroupStation()) {
			updateDTOForPostNextSong(radioStationUpdateDTO,
					nowPlayingHistoryEntry, radioStation.getNowPlaying()
							.getSongScrobblers(), lookAheadHistoryEntry,
					lookAheadScrobblers);
		} else {
			updateDTOForPostNextSong(radioStationUpdateDTO,
					nowPlayingHistoryEntry, lookAheadHistoryEntry);
		}
	}

	public RadioStationDTO_V0_4 getStationReadiness(String stationId)
			throws SongwichAPIException {

		RadioStation station = authorizeGetStationReadiness(stationId);
		StationStrategy stationStrategy = new NaiveStationStrategy(station);
		// StationStrategy stationStrategy = new
		// PseudoDMCAStationStrategy(station);
		Float stationReadiness = stationStrategy.getStationReadiness();
		return createDTOForGetStationReadiness(station, stationReadiness);
	}

	public void putStationsActivate(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {
		// authorize request
		RadioStation station = authorizePutStationsActivate(stationId,
				radioStationUpdateDTO);

		// process request
		station.setActive(true);
		StationHistoryEntry nowPlayingHistoryEntry = setNowPlaying(station)
				.getStationHistoryEntry();
		StationHistoryEntry lookAheadHistoryEntry = setLookAhead(station)
				.getStationHistoryEntry();

		// save it
		saveStation(station);

		// update the DTO
		updateDTOForPutStationsActive(radioStationUpdateDTO, station,
				nowPlayingHistoryEntry, lookAheadHistoryEntry);
	}

	private Track setLookAhead(RadioStation station)
			throws SongwichAPIException {

		Track track = saveHistoryEntryAndGetTrack(station);
		station.setLookAhead(track);
		return track;
	}

	private Track setNowPlaying(RadioStation station)
			throws SongwichAPIException {

		Track track = saveHistoryEntryAndGetTrack(station);
		station.setNowPlaying(track);
		return track;
	}

	private Track saveHistoryEntryAndGetTrack(RadioStation station)
			throws SongwichAPIException {

		// StationStrategy stationStrategy = new
		// PseudoDMCAStationStrategy(station);
		StationStrategy stationStrategy = new NaiveStationStrategy(station);
		Song song = stationStrategy.getNextSong();
		StationHistoryEntry historyEntry = new StationHistoryEntry(
				station.getId(), song, System.currentTimeMillis());
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		stationHistoryDAO.save(historyEntry, getContext().getAppDeveloper()
				.getEmailAddress());

		Track track;
		if (station.getScrobbler().isGroupStation()) {
			Set<ObjectId> songScrobblersIds = stationStrategy
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

	private RadioStationDTO_V0_4 createDTOForGetStationReadiness(
			RadioStation station, Float stationReadiness) {
		RadioStationDTO_V0_4 radioStationDTO = new RadioStationDTO_V0_4();
		radioStationDTO.setStationId(station.getId().toString());
		radioStationDTO.setStationName(station.getName());
		radioStationDTO.setStationReadiness(String.format("%.2f",
				stationReadiness));
		return radioStationDTO;
	}

	private RadioStation authorizeGetStationReadiness(String stationId)
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

	private RadioStation authorizePutStationsActivate(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {

		RadioStation station = authorizePutStations(stationId,
				radioStationUpdateDTO);

		// StationStrategy stationStrategy = new
		// PseudoDMCAStationStrategy(station);
		StationStrategy stationStrategy = new NaiveStationStrategy(station);
		if (!stationStrategy.isStationReady()) {
			throw new SongwichAPIException(
					String.format(
							"The station cannot be activated yet because it is only %.0f%% ready",
							stationStrategy.getStationReadiness() * 100),
					APIStatus_V0_4.UNAUTHORIZED);
		}

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

	private void createDTOForPostStations(RadioStation station,
			RadioStationDTO_V0_4 radioStationDTO) {

		// update the DataTransferObject
		radioStationDTO.setStationId(station.getId().toString());

		// nowPlaying
		if (station.getNowPlaying() != null) {
			StationSongListEntryDTO_V0_4 nowPlayingDTO = new StationSongListEntryDTO_V0_4();
			nowPlayingDTO.setArtistName(station.getNowPlaying()
					.getStationHistoryEntry().getSong().getArtistsNames()
					.toString());
			nowPlayingDTO.setTrackTitle(station.getNowPlaying()
					.getStationHistoryEntry().getSong().getSongTitle());
			nowPlayingDTO.setIdForFeedback(station.getNowPlaying()
					.getStationHistoryEntry().getId().toString());
			radioStationDTO.setNowPlaying(nowPlayingDTO);
		}

		// lookAhead
		if (station.getLookAhead() != null) {
			StationSongListEntryDTO_V0_4 lookAheadDTO = new StationSongListEntryDTO_V0_4();
			lookAheadDTO.setArtistName(station.getLookAhead()
					.getStationHistoryEntry().getSong().getArtistsNames()
					.toString());
			lookAheadDTO.setTrackTitle(station.getLookAhead()
					.getStationHistoryEntry().getSong().getSongTitle());
			lookAheadDTO.setIdForFeedback(station.getLookAhead()
					.getStationHistoryEntry().getId().toString());
			radioStationDTO.setLookAhead(lookAheadDTO);
		}

		// recently scrobbled by
		if (station.getScrobbler().isGroupStation()) {
			StationSongListEntryDTO_V0_4 nowPlayingDTO = radioStationDTO
					.getNowPlaying();
			nowPlayingDTO.setRecentScrobblers(createScrobblersDTO(station
					.getNowPlaying().getSongScrobblers()));

			StationSongListEntryDTO_V0_4 lookAheadDTO = radioStationDTO
					.getLookAhead();
			lookAheadDTO.setRecentScrobblers(createScrobblersDTO(station
					.getLookAhead().getSongScrobblers()));
		}
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

	private void savePutStationsScrobblers(RadioStation station,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO) {
		// saves it
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.save(station, getContext().getAppDeveloper()
				.getEmailAddress());
		// updates the user output
		updateDTOForPutStations(radioStationUpdateDTO, station);
	}

	public static List<RadioStationDTO_V0_4> createDTOForGetStations(
			List<RadioStation> stations) {

		List<RadioStationDTO_V0_4> stationsDTO = new ArrayList<RadioStationDTO_V0_4>();
		RadioStationDTO_V0_4 stationDTO;
		for (RadioStation station : stations) {
			stationDTO = createDTOForGetStations(station, false);
			stationsDTO.add(stationDTO);
		}
		return stationsDTO;
	}

	private static RadioStationDTO_V0_4 createDTOForGetStations(
			RadioStation station, boolean showSongs) {
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

		if (showSongs) {
			StationSongListEntryDTO_V0_4 songListEntryDTO = new StationSongListEntryDTO_V0_4();
			songListEntryDTO.setTrackTitle(station.getNowPlaying()
					.getSongTitle());
			songListEntryDTO.setArtistName(station.getNowPlaying()
					.getArtistsNames().toString());
			songListEntryDTO.setIdForFeedback(station.getNowPlaying()
					.getStationHistoryEntry().getId().toString());
			songListEntryDTO.setRecentScrobblers(createScrobblersDTO(station
					.getNowPlaying().getSongScrobblers()));
			stationDTO.setNowPlaying(songListEntryDTO);

			songListEntryDTO = new StationSongListEntryDTO_V0_4();
			songListEntryDTO.setTrackTitle(station.getLookAhead()
					.getSongTitle());
			songListEntryDTO.setArtistName(station.getLookAhead()
					.getArtistsNames().toString());
			songListEntryDTO.setIdForFeedback(station.getLookAhead()
					.getStationHistoryEntry().getId().toString());
			songListEntryDTO.setRecentScrobblers(createScrobblersDTO(station
					.getLookAhead().getSongScrobblers()));
			stationDTO.setLookAhead(songListEntryDTO);
		}

		return stationDTO;
	}

	private void updateDTOForPostNextSong(
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

	private void updateDTOForPostNextSong(
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

	private RadioStationUpdateDTO_V0_4 updateDTOForPutStations(
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO,
			RadioStation station) {
		radioStationUpdateDTO.setStationId(station.getId().toString());
		radioStationUpdateDTO.setStationName(station.getName());
		radioStationUpdateDTO.setImageUrl(station.getImageUrl());

		List<String> scrobblerIds = new ArrayList<String>();
		for (ObjectId scrobblerId : station.getScrobbler()
				.getActiveScrobblersUserIds()) {
			scrobblerIds.add(scrobblerId.toString());
		}
		radioStationUpdateDTO.setScrobblerIds(scrobblerIds);

		return radioStationUpdateDTO;
	}

	private void updateDTOForPutStationsActive(
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO,
			RadioStation station, StationHistoryEntry nowPlayingHistoryEntry,
			StationHistoryEntry lookAheadHistoryEntry) {

		radioStationUpdateDTO.setStationId(station.getId().toString());
		radioStationUpdateDTO.setStationName(station.getName());
		radioStationUpdateDTO.setActive(station.isActive().toString());

		updateDTOForPostNextSong(radioStationUpdateDTO, nowPlayingHistoryEntry,
				lookAheadHistoryEntry);
	}

	/*
	 * public StationHistoryEntry getNowPlaying(StationSongListDTO_V0_4
	 * stationEntryDTO) { // TODO: REDESIGN StationHistoryDAO<ObjectId>
	 * stationHistoryDAO = new StationHistoryDAOMongo();
	 * List<StationHistoryEntry> stationHistoryList = stationHistoryDAO
	 * .findLastEntriesByStationId(stationEntryDTO.get, 1); // TODO: make sure
	 * it's not a brand new station return stationHistoryList.get(0); }
	 * 
	 * public void postSongFeedback(ObjectId stationHistoryEntryId, FeedbackType
	 * feedback) {
	 * 
	 * StationHistoryDAO<ObjectId> stationHistoryDAO = new
	 * StationHistoryDAOMongo(); StationHistoryEntry stationHistoryEntry =
	 * stationHistoryDAO .findById(stationHistoryEntryId); SongFeedback
	 * songFeedback = new SongFeedback(feedback, getContext()
	 * .getUser().getId()); stationHistoryEntry.addSongFeedback(songFeedback);
	 * 
	 * // execute the update stationHistoryDAO.save(stationHistoryEntry,
	 * getContext() .getAppDeveloper().getEmailAddress()); }
	 */
}
