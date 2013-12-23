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

import util.api.MyLogger;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.scrobbles.UserOutputDTO_V0_4;
import views.api.stations.RadioStationInputDTO_V0_4;
import views.api.stations.RadioStationOutputDTO_V0_4;
import views.api.stations.RadioStationUpdateInputDTO_V0_4;
import views.api.stations.TrackDTO_V0_4;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import behavior.api.usecases.scrobbles.UsersUseCases;
import behavior.api.usecases.subscriptions.SubscriptionsUseCases;

public class StationsUseCases extends UseCase {

	public StationsUseCases(RequestContext context) {
		super(context);
	}

	// TODO: limit the number of results
	public List<RadioStationOutputDTO_V0_4> getStations(
			boolean onlyActiveStations) {
		List<RadioStation> stations;

		if (onlyActiveStations) {
			stations = getRadioStationDAO().findActiveOnly();
		} else {
			stations = getRadioStationDAO().find().asList();
		}

		return createDTOForGetMultipleStations(stations);
	}

	public RadioStationOutputDTO_V0_4 getStations(String stationId,
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

		List<UserOutputDTO_V0_4> activeScrobblersDTO = null;
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

	public RadioStationOutputDTO_V0_4 postStations(
			RadioStationInputDTO_V0_4 radioStationInputDTO,
			StationStrategy stationStrategy, boolean subscribeScrobblers)
			throws SongwichAPIException {

		authenticatePostStations(radioStationInputDTO);

		// creates either a User RadioStation or a Group RadioStation
		ScrobblerBridge scrobblerBridge;
		if (radioStationInputDTO.getGroupName() == null) {
			ObjectId userId = new ObjectId(radioStationInputDTO
					.getScrobblerIds().iterator().next());
			// TODO: check if the user exists
			scrobblerBridge = new ScrobblerBridge(getUserDAO().findById(userId));
		} else {
			// validation guarantees there will be multiple scrobblerIds
			Collection<String> userIds = radioStationInputDTO.getScrobblerIds();
			Set<GroupMember> groupMembers = new HashSet<GroupMember>(
					userIds.size());
			User user;
			for (String userId : userIds) {
				// TODO: check if the users exist
				user = getUserDAO().findById(new ObjectId(userId));
				groupMembers.add(new GroupMember(user, System
						.currentTimeMillis()));
			}
			// add Group name
			scrobblerBridge = new ScrobblerBridge(new Group(
					radioStationInputDTO.getGroupName(), groupMembers));
		}

		RadioStation station = new RadioStation(
				radioStationInputDTO.getStationName(), scrobblerBridge,
				radioStationInputDTO.getImageUrl(),
				radioStationInputDTO.getDescription());

		// checks if station can be activated and activates it
		tryToActivateStation(station, stationStrategy);
		// checks if it's an individual station by a verified user and marks it
		// as verified
		if (station.getScrobbler().isIndividualStation()
				&& station.getScrobbler().getUser().isVerified()) {
			station.setVerified(true);
		}

		// save it
		saveStation(station);

		// check if we need to subscribe the scrobblers
		if (subscribeScrobblers) {
			subscribeScrobblers(station);
		}

		// output
		if (station.isActive()) {
			return createDTOForPostStations(station, null, radioStationInputDTO);
		} else {
			Float stationReadiness = stationStrategy.getStationReadiness();
			return createDTOForPostStations(station, stationReadiness,
					radioStationInputDTO);
		}
	}

	private void subscribeScrobblers(RadioStation station) {
		MyLogger.debug("inside subscribeScrobblers()");
		Set<ObjectId> scrobblerIds = station.getScrobbler()
				.getActiveScrobblersUserIds();
		SubscriptionsUseCases subscriptionUseCases = new SubscriptionsUseCases(
				getContext());
		subscriptionUseCases.postSubscriptionsForPostStations(scrobblerIds,
				station.getId());
	}

	public RadioStationOutputDTO_V0_4 putStations(String stationId,
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO)
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

		saveStation(station);

		return createDTOForPutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public RadioStationOutputDTO_V0_4 putStationsDeactivate(String stationId)
			throws SongwichAPIException {

		RadioStation station = authorizePutStations(stationId);

		// process request
		station.setDeactivated(true);
		getRadioStationDAO().save(station,
				getContext().getAppDeveloper().getEmailAddress());

		// update output
		RadioStationOutputDTO_V0_4 stationOutputDTO = new RadioStationOutputDTO_V0_4();
		stationOutputDTO.setStationId(stationId);

		return stationOutputDTO;
	}

	public RadioStationOutputDTO_V0_4 putStationsAddScrobblers(
			String stationId,
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO,
			StationStrategy stationStrategy, boolean subscribeScrobblers)
			throws SongwichAPIException {

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

		// save it
		saveStation(station);

		// check if we need to subscribe the scrobblers
		if (subscribeScrobblers) {
			subscribeScrobblers(station);
		}

		return createDTOForPutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public RadioStationOutputDTO_V0_4 putStationsRemoveScrobblers(
			String stationId,
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO,
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

		saveStation(station);

		return createDTOForPutStationsScrobblers(station, radioStationUpdateDTO);
	}

	public RadioStationOutputDTO_V0_4 putStationsMarkAsVerified(String stationId)
			throws SongwichAPIException {

		RadioStation station = authorizePutStations(stationId);

		// mark user as verified
		station.setVerified(true);
		getRadioStationDAO().save(station,
				getContext().getAppDeveloper().getEmailAddress());

		// output
		return createBasicDTOForStations(station);
	}

	public RadioStationOutputDTO_V0_4 postNextSong(
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO,
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
		// update its timestamp and save
		nowPlayingHistoryEntry.setTimestamp(System.currentTimeMillis());
		getStationHistoryDAO().save(nowPlayingHistoryEntry,
				getContext().getAppDeveloper().getEmailAddress());
		station.setNowPlaying(nowPlayingTrack);

		// create and save the StationHistoryEntry for the lookAhead Track
		// we cannot have a null timestamp or the StationStrategy might repeat
		// songs
		StationHistoryEntry lookAheadHistoryEntry = new StationHistoryEntry(
				station.getId(), lookAheadSong, System.currentTimeMillis());
		getStationHistoryDAO().save(lookAheadHistoryEntry,
				getContext().getAppDeveloper().getEmailAddress());
		station.setLookAhead(new Track(lookAheadHistoryEntry,
				lookAheadScrobblers));

		// update radioStation
		saveStation(station);

		// update the DataTransferObject
		if (station.getScrobbler().isGroupStation()) {
			return createDTOForPostNextSong(radioStationUpdateDTO,
					nowPlayingHistoryEntry, station.getNowPlaying()
							.getSongScrobblers(), lookAheadHistoryEntry,
					lookAheadScrobblers);
		} else {
			return createDTOForPostNextSong(radioStationUpdateDTO,
					nowPlayingHistoryEntry, lookAheadHistoryEntry);
		}
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

	private RadioStation authorizePostNextSong(
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO)
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
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO)
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

	private void authenticatePostStations(
			RadioStationInputDTO_V0_4 radioStationDTO)
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

	private RadioStationOutputDTO_V0_4 createDTOForPutStationsScrobblers(
			RadioStation station,
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO) {

		// updates the user output
		return createDTOForPutStations(radioStationUpdateDTO, station);
	}

	private static RadioStationOutputDTO_V0_4 createDTOForPostStations(
			RadioStation station, Float stationReadiness,
			RadioStationInputDTO_V0_4 inputDTO) {

		RadioStationOutputDTO_V0_4 outputDTO = new RadioStationOutputDTO_V0_4(
				inputDTO);
		outputDTO.setStationId(station.getId().toString());
		outputDTO.setActive(station.isActive().toString());
		outputDTO.setVerified(station.isVerified().toString());

		return updateStationDTOWithReadinessOrSongs(station, stationReadiness,
				outputDTO);
	}

	private static RadioStationOutputDTO_V0_4 createStationDTO(
			RadioStation station, Float stationReadiness,
			long numberSubscribers, List<UserOutputDTO_V0_4> activeScrobblersDTO) {

		RadioStationOutputDTO_V0_4 stationDTO = createBasicDTOForStations(station);
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

	private static RadioStationOutputDTO_V0_4 updateStationDTOWithReadinessOrSongs(
			RadioStation station, Float stationReadiness,
			RadioStationOutputDTO_V0_4 stationDTO) {

		if (station.isActive()) {
			return updateDTOForGetActiveStation(station, stationDTO);
		} else {
			return updateDTOForGetInactiveStation(station, stationReadiness,
					stationDTO);
		}
	}

	public static List<RadioStationOutputDTO_V0_4> createDTOForGetMultipleStations(
			Collection<RadioStation> scrobblerStations) {

		// defend from null parameters and never return a null list
		if (scrobblerStations == null) {
			return new ArrayList<RadioStationOutputDTO_V0_4>(0);
		}
		
		List<RadioStationOutputDTO_V0_4> stationsDTO = new ArrayList<RadioStationOutputDTO_V0_4>(
				scrobblerStations.size());
		RadioStationOutputDTO_V0_4 stationDTO;
		for (RadioStation station : scrobblerStations) {
			stationDTO = createBasicDTOForStations(station);
			stationDTO.setActive(station.isActive().toString());
			stationsDTO.add(stationDTO);
		}
		return stationsDTO;
	}

	// id, name, url, scrobblers, verified (no songs, no activeness, no
	// readiness)
	private static RadioStationOutputDTO_V0_4 createBasicDTOForStations(
			RadioStation station) {
		RadioStationOutputDTO_V0_4 stationDTO = new RadioStationOutputDTO_V0_4();
		stationDTO.setStationId(station.getId().toString());
		stationDTO.setActive(station.isActive().toString());
		stationDTO.setStationName(station.getName());
		stationDTO.setImageUrl(station.getImageUrl());
		stationDTO.setDescription(station.getDescription());
		stationDTO.setVerified(station.isVerified().toString());

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

	private static RadioStationOutputDTO_V0_4 updateDTOForGetInactiveStation(
			RadioStation station, Float stationReadiness,
			RadioStationOutputDTO_V0_4 stationDTO) {

		stationDTO.setStationReadiness(String.format("%.2f", stationReadiness));
		return stationDTO;
	}

	private static RadioStationOutputDTO_V0_4 updateDTOForGetActiveStation(
			RadioStation station,
			RadioStationOutputDTO_V0_4 radioStationOuputDTO) {
		// nowPlaying
		TrackDTO_V0_4 nowPlayingDTO = new TrackDTO_V0_4();
		nowPlayingDTO.setArtistsNames(station.getNowPlaying()
				.getStationHistoryEntry().getSong().getArtistsNames());
		nowPlayingDTO.setTrackTitle(station.getNowPlaying()
				.getStationHistoryEntry().getSong().getSongTitle());
		nowPlayingDTO.setIdForFeedback(station.getNowPlaying()
				.getStationHistoryEntry().getId().toString());
		radioStationOuputDTO.setNowPlaying(nowPlayingDTO);

		// lookAhead
		TrackDTO_V0_4 lookAheadDTO = new TrackDTO_V0_4();
		lookAheadDTO.setArtistsNames(station.getLookAhead()
				.getStationHistoryEntry().getSong().getArtistsNames());
		lookAheadDTO.setTrackTitle(station.getLookAhead()
				.getStationHistoryEntry().getSong().getSongTitle());
		lookAheadDTO.setIdForFeedback(station.getLookAhead()
				.getStationHistoryEntry().getId().toString());
		radioStationOuputDTO.setLookAhead(lookAheadDTO);

		// recently scrobbled by
		if (station.getScrobbler().isGroupStation()) {
			nowPlayingDTO.setRecentScrobblers(createScrobblersDTO(station
					.getNowPlaying().getSongScrobblers()));

			lookAheadDTO.setRecentScrobblers(createScrobblersDTO(station
					.getLookAhead().getSongScrobblers()));
		}

		return radioStationOuputDTO;
	}

	private static RadioStationOutputDTO_V0_4 createDTOForPostNextSong(
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO,
			StationHistoryEntry nowPlayingHistoryEntry,
			List<User> nowPlayingScrobblers,
			StationHistoryEntry lookAheadHistoryEntry,
			List<User> lookAheadScrobblers) {

		RadioStationOutputDTO_V0_4 radioStationOutputDTO = createDTOForPostNextSong(
				radioStationUpdateDTO, nowPlayingHistoryEntry,
				lookAheadHistoryEntry);

		// updates the song scrobblers
		TrackDTO_V0_4 nowPlayingSongListEntryDTO = radioStationOutputDTO
				.getNowPlaying();
		nowPlayingSongListEntryDTO
				.setRecentScrobblers(createScrobblersDTO(nowPlayingScrobblers));
		TrackDTO_V0_4 lookAheadSongListEntryDTO = radioStationOutputDTO
				.getLookAhead();
		lookAheadSongListEntryDTO
				.setRecentScrobblers(createScrobblersDTO(lookAheadScrobblers));

		return radioStationOutputDTO;
	}

	private static List<UserOutputDTO_V0_4> createScrobblersDTO(
			List<User> scrobblers) {
		List<UserOutputDTO_V0_4> scrobblersDTO = new ArrayList<UserOutputDTO_V0_4>();
		UserOutputDTO_V0_4 userDTO;
		for (User scrobbler : scrobblers) {
			if (scrobbler.getName() != null && !scrobbler.getName().isEmpty()) {
				userDTO = new UserOutputDTO_V0_4();
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

	private static RadioStationOutputDTO_V0_4 createDTOForPostNextSong(
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO,
			StationHistoryEntry nowPlayingHistoryEntry,
			StationHistoryEntry lookAheadHistoryEntry) {

		RadioStationOutputDTO_V0_4 radioStationOutputDTO = new RadioStationOutputDTO_V0_4(
				radioStationUpdateDTO);

		// sets StationSongListDTO's nowPlaying
		TrackDTO_V0_4 nowPlayingSongListEntryDTO = new TrackDTO_V0_4();
		nowPlayingSongListEntryDTO.setArtistsNames(nowPlayingHistoryEntry
				.getSong().getArtistsNames());
		nowPlayingSongListEntryDTO.setTrackTitle(nowPlayingHistoryEntry
				.getSong().getSongTitle());
		nowPlayingSongListEntryDTO.setIdForFeedback(nowPlayingHistoryEntry
				.getId().toString());
		radioStationOutputDTO.setNowPlaying(nowPlayingSongListEntryDTO);

		// sets StationSongListDTO's lookAhead
		TrackDTO_V0_4 lookAheadSongListEntryDTO = new TrackDTO_V0_4();
		lookAheadSongListEntryDTO.setArtistsNames(lookAheadHistoryEntry
				.getSong().getArtistsNames());
		lookAheadSongListEntryDTO.setTrackTitle(lookAheadHistoryEntry.getSong()
				.getSongTitle());
		lookAheadSongListEntryDTO.setIdForFeedback(lookAheadHistoryEntry
				.getId().toString());
		radioStationOutputDTO.setLookAhead(lookAheadSongListEntryDTO);

		return radioStationOutputDTO;
	}

	private static RadioStationOutputDTO_V0_4 createDTOForPutStations(
			RadioStationUpdateInputDTO_V0_4 radioStationUpdateDTO,
			RadioStation station) {

		RadioStationOutputDTO_V0_4 radioStationOutputDTO = new RadioStationOutputDTO_V0_4(
				radioStationUpdateDTO);
		radioStationOutputDTO.setActive(station.isActive().toString());

		List<String> scrobblerIds = new ArrayList<String>();
		for (ObjectId scrobblerId : station.getScrobbler()
				.getActiveScrobblersUserIds()) {
			scrobblerIds.add(scrobblerId.toString());
		}
		radioStationOutputDTO.setScrobblerIds(scrobblerIds);

		return radioStationOutputDTO;
	}

	public static RadioStationOutputDTO_V0_4 createDTOForSubscription(
			RadioStation station) {

		RadioStationOutputDTO_V0_4 stationDTO = new RadioStationOutputDTO_V0_4();
		stationDTO.setStationId(station.getId().toString());
		stationDTO.setStationName(station.getName());
		stationDTO.setImageUrl(station.getImageUrl());
		return stationDTO;
	}
}
