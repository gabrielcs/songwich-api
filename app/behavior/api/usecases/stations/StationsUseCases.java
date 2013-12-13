package behavior.api.usecases.stations;

import java.util.ArrayList;
import java.util.Collection;
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
import views.api.stations.TrackDTO_V0_4;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import behavior.api.usecases.scrobbles.UsersUseCases;

public class StationsUseCases extends UseCase {

	public StationsUseCases(RequestContext context) {
		super(context);
	}

	// TODO: limit the number of results
	public List<RadioStationDTO_V0_4> getStations(boolean onlyActiveStations) {
		List<RadioStation> stations;

		if (onlyActiveStations) {
			stations = getRadioStationDAO().findActiveOnly();
		} else {
			stations = getRadioStationDAO().find().asList();
		}

		return createDTOForGetMultipleStations(stations);
	}

	public RadioStationDTO_V0_4 getStations(String stationId,
			StationStrategy stationStrategy, boolean includeScrobblersData)
			throws SongwichAPIException {

		RadioStation station = authorizeGetStations(stationId);

		// sets the station active if it can
		Float stationReadiness = null;
		if (!station.isActive()) {
			stationStrategy.setStation(station);
			if (stationStrategy.isStationReady()) {
				activateStation(station, stationStrategy);
				// saves it
				saveStation(station);
			} else {
				stationReadiness = stationStrategy.getStationReadiness();
			}
		}

		long numberSubscribers = getSubscriptionDAO().countByStationId(
				new ObjectId(stationId));

		List<UserDTO_V0_4> activeScrobblersDTO = null;
		if (includeScrobblersData) {
			Set<ObjectId> activeScrobblersObjectId = station.getScrobbler()
					.getActiveScrobblersUserIds();
			List<User> activeScrobblers = new ArrayList<User>(
					activeScrobblersObjectId.size());
			for (ObjectId scrobblerId : activeScrobblersObjectId) {
				User scrobbler = getUserDAO().findById(scrobblerId);
				activeScrobblers.add(scrobbler);
			}

			UsersUseCases usersUseCases = new UsersUseCases(getContext());
			activeScrobblersDTO = usersUseCases
					.createUsersDTOForGetStations(activeScrobblers);
		}

		return createStationDTO(station, stationReadiness, numberSubscribers,
				activeScrobblersDTO);
	}

	private RadioStation authorizeGetStations(String stationId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(stationId)) {
			throw new SongwichAPIException("Invalid stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		RadioStation station = getRadioStationDAO().findById(
				new ObjectId(stationId));
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
		ScrobblerBridge scrobblerBridge;
		if (radioStationDTO.getGroupName() == null) {
			ObjectId userId = new ObjectId(radioStationDTO.getScrobblerIds()
					.iterator().next());
			// TODO: check if the user exists
			scrobblerBridge = new ScrobblerBridge(getUserDAO().findById(userId));
		} else {
			// validation guarantees there will be multiple scrobblerIds
			Collection<String> userIds = radioStationDTO.getScrobblerIds();
			Set<GroupMember> groupMembers = new HashSet<GroupMember>(
					userIds.size());
			User user;
			for (String userId : userIds) {
				// TODO: check if the users exist
				user = getUserDAO().findById(new ObjectId(userId));
				groupMembers.add(new GroupMember(user, System
						.currentTimeMillis()));
			}
			// add Group name to DTO
			scrobblerBridge = new ScrobblerBridge(new Group(
					radioStationDTO.getGroupName(), groupMembers));
		}

		RadioStation station = new RadioStation(
				radioStationDTO.getStationName(), scrobblerBridge,
				radioStationDTO.getImageUrl(), radioStationDTO.getDescription());

		// checks if station can be activated and activates it
		tryToActivateStation(station, stationStrategy);
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

		RadioStation station = authorizePutStations(stationId);

		// update imageUrl
		if (radioStationUpdateDTO.getImageUrl() != null) {
			station.setImageUrl(radioStationUpdateDTO.getImageUrl());
		}

		// update description
		if (radioStationUpdateDTO.getDescription() != null) {
			station.setDescription(radioStationUpdateDTO.getDescription());
		}

		// update station name
		if (radioStationUpdateDTO.getStationName() != null) {
			station.setName(radioStationUpdateDTO.getStationName());
		}

		savePutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public RadioStationUpdateDTO_V0_4 putStationsDeactivate(String stationId)
			throws SongwichAPIException {

		RadioStation station = authorizePutStations(stationId);

		// process request
		station.setDeactivated(true);
		getRadioStationDAO().save(station,
				getContext().getAppDeveloper().getEmailAddress());

		// update output
		RadioStationUpdateDTO_V0_4 stationUpdateDTO = new RadioStationUpdateDTO_V0_4();
		stationUpdateDTO.setStationId(stationId);

		return stationUpdateDTO;
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
			} else {
				throw new SongwichAPIException(
						"Cannot add a user that's already an active scrobbler of the station: "
								+ scrobblerId, APIStatus_V0_4.BAD_REQUEST);
			}
		}

		tryToActivateStation(station, stationStrategy);

		savePutStationsScrobblers(station, radioStationUpdateDTO);
	}

	private User authorizePutStationsAddRemoveScrobbler(String scrobblerId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(scrobblerId)) {
			throw new SongwichAPIException("Invalid scrobblerId: "
					+ scrobblerId, APIStatus_V0_4.INVALID_PARAMETER);
		}

		User user = getUserDAO().findById(new ObjectId(scrobblerId));
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
			lookAheadScrobblers = getUserDAO().findUsersByIds(
					lookAheadScrobblersIds);
		}

		// turn the lookAhead Track into next and set the new lookAhead
		Track nowPlayingTrack = station.getLookAhead();
		StationHistoryEntry nowPlayingHistoryEntry = nowPlayingTrack
				.getStationHistoryEntry();
		nowPlayingHistoryEntry.setTimestamp(System.currentTimeMillis());
		getStationHistoryDAO().save(nowPlayingHistoryEntry,
				getContext().getAppDeveloper().getEmailAddress());
		station.setNowPlaying(nowPlayingTrack);

		// create and save the StationHistoryEntry for the lookAhead Track (with
		// timestamp=null)
		StationHistoryEntry lookAheadHistoryEntry = new StationHistoryEntry(
				station.getId(), lookAheadSong, null);
		getStationHistoryDAO().save(lookAheadHistoryEntry,
				getContext().getAppDeveloper().getEmailAddress());
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
		RadioStation station = getRadioStationDAO().findById(
				new ObjectId(radioStationUpdateDTO.getStationId()));
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
	public boolean tryToActivateStation(RadioStation station,
			StationStrategy stationStrategy) throws SongwichAPIException {
		
		if (!station.isActive()) {
			stationStrategy.setStation(station);
			if (stationStrategy.isStationReady()) {
				activateStation(station, stationStrategy);
				return true;
			}
		}
		return false;
	}

	// it doesn't save it
	private void activateStation(RadioStation station,
			StationStrategy stationStrategy) throws SongwichAPIException {

		station.setActive(true);
		setNowPlaying(station, stationStrategy);
		// resets the station strategy so it doesn't return the cached results
		stationStrategy.reset().setStation(station);
		setLookAhead(station, stationStrategy);
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
		getStationHistoryDAO().save(historyEntry,
				getContext().getAppDeveloper().getEmailAddress());

		Track track;
		if (station.getScrobbler().isGroupStation()) {
			Set<ObjectId> songScrobblersIds = stationStrategyAlreadySet
					.getNextSongRecentScrobblers();
			List<User> songScrobblers = getUserDAO().findUsersByIds(
					songScrobblersIds);
			track = new Track(historyEntry, songScrobblers);
		} else {
			track = new Track(historyEntry);
		}

		return track;
	}

	private RadioStation authorizePutStations(String stationId)
			throws SongwichAPIException {

		if (!ObjectId.isValid(stationId)) {
			throw new SongwichAPIException("Invalid stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		RadioStation station = getRadioStationDAO().findById(
				new ObjectId(stationId));
		if (station == null) {
			throw new SongwichAPIException("Non-existent stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// check if the user is already one of the stations's scrobbler and will
		// continue to be
		authenticatePutStations(station);

		return station;
	}

	private RadioStation authorizePutStationsScrobblers(String stationId,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO)
			throws SongwichAPIException {

		RadioStation station = authorizePutStations(stationId);

		if (radioStationUpdateDTO.getScrobblerIds() == null
				|| radioStationUpdateDTO.getScrobblerIds().isEmpty()) {

			throw new SongwichAPIException("Missing scrobblerId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		if (station.getScrobbler().isIndividualStation()) {
			throw new SongwichAPIException(
					"Not allowed to change scrobblers on an individual station",
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

	private void authenticatePutStations(RadioStation station)
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
		getCascadeSaveRadioStationDAO().cascadeSave(radioStation,
				getContext().getAppDeveloper().getEmailAddress());
	}

	private void savePutStationsScrobblers(RadioStation station,
			RadioStationUpdateDTO_V0_4 radioStationUpdateDTO) {

		// saves it
		getRadioStationDAO().save(station,
				getContext().getAppDeveloper().getEmailAddress());
		// updates the user output
		updateDTOForPutStations(radioStationUpdateDTO, station);
	}

	private static void updateDTOForPostStations(RadioStation station,
			Float stationReadiness, RadioStationDTO_V0_4 radioStationDTO) {

		radioStationDTO.setStationId(station.getId().toString());
		radioStationDTO.setIsActive(station.isActive().toString());
		updateStationDTOWithReadinessOrSongs(station, stationReadiness,
				radioStationDTO);
	}

	private static RadioStationDTO_V0_4 createStationDTO(RadioStation station,
			Float stationReadiness, long numberSubscribers,
			List<UserDTO_V0_4> activeScrobblersDTO) {

		RadioStationDTO_V0_4 stationDTO = createBasicDTOForStations(station);
		updateStationDTOWithReadinessOrSongs(station, stationReadiness,
				stationDTO);

		if (numberSubscribers > 0) {
			stationDTO.setNumberSubscribers(numberSubscribers);
		}

		if (activeScrobblersDTO != null) {
			stationDTO.setActiveScrobblers(activeScrobblersDTO);
			// so we don't have duplicate data
			stationDTO.setScrobblerIds(null);
		}

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
			Collection<RadioStation> scrobblerStations) {

		List<RadioStationDTO_V0_4> stationsDTO = new ArrayList<RadioStationDTO_V0_4>();
		RadioStationDTO_V0_4 stationDTO;
		for (RadioStation station : scrobblerStations) {
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
		stationDTO.setIsActive(station.isActive().toString());
		stationDTO.setStationName(station.getName());
		stationDTO.setImageUrl(station.getImageUrl());
		stationDTO.setDescription(station.getDescription());
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
		TrackDTO_V0_4 nowPlayingDTO = new TrackDTO_V0_4();
		nowPlayingDTO.setArtistsNames(station.getNowPlaying()
				.getStationHistoryEntry().getSong().getArtistsNames());
		nowPlayingDTO.setTrackTitle(station.getNowPlaying()
				.getStationHistoryEntry().getSong().getSongTitle());
		nowPlayingDTO.setIdForFeedback(station.getNowPlaying()
				.getStationHistoryEntry().getId().toString());
		stationDTO.setNowPlaying(nowPlayingDTO);

		// lookAhead
		TrackDTO_V0_4 lookAheadDTO = new TrackDTO_V0_4();
		lookAheadDTO.setArtistsNames(station.getLookAhead()
				.getStationHistoryEntry().getSong().getArtistsNames());
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
		TrackDTO_V0_4 nowPlayingSongListEntryDTO = radioStationDTO
				.getNowPlaying();
		nowPlayingSongListEntryDTO
				.setRecentScrobblers(createScrobblersDTO(nowPlayingScrobblers));
		TrackDTO_V0_4 lookAheadSongListEntryDTO = radioStationDTO
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
		TrackDTO_V0_4 nowPlayingSongListEntryDTO = new TrackDTO_V0_4();
		nowPlayingSongListEntryDTO.setArtistsNames(nowPlayingHistoryEntry
				.getSong().getArtistsNames());
		nowPlayingSongListEntryDTO.setTrackTitle(nowPlayingHistoryEntry
				.getSong().getSongTitle());
		nowPlayingSongListEntryDTO.setIdForFeedback(nowPlayingHistoryEntry
				.getId().toString());
		radioStationUpdateDTO.setNowPlaying(nowPlayingSongListEntryDTO);

		// sets StationSongListDTO's lookAhead
		TrackDTO_V0_4 lookAheadSongListEntryDTO = new TrackDTO_V0_4();
		lookAheadSongListEntryDTO.setArtistsNames(lookAheadHistoryEntry
				.getSong().getArtistsNames());
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
		radioStationUpdateDTO.setDescription(station.getDescription());

		List<String> scrobblerIds = new ArrayList<String>();
		for (ObjectId scrobblerId : station.getScrobbler()
				.getActiveScrobblersUserIds()) {
			scrobblerIds.add(scrobblerId.toString());
		}
		radioStationUpdateDTO.setScrobblerIds(scrobblerIds);

		return radioStationUpdateDTO;
	}

	public static RadioStationDTO_V0_4 createDTOForSubscription(
			RadioStation station) {

		RadioStationDTO_V0_4 stationDTO = new RadioStationDTO_V0_4();
		stationDTO.setStationId(station.getId().toString());
		stationDTO.setStationName(station.getName());
		stationDTO.setImageUrl(station.getImageUrl());
		return stationDTO;
	}
}
