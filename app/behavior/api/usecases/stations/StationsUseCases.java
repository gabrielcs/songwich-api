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
import views.api.stations.StationSongListDTO_V0_4;
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
		RadioStation radioStation = new RadioStation(
				radioStationDTO.getStationName(), scrobblerBridge, imageUrl);

		// set nowPlaying
		StationStrategy stationStrategyNowPlaying = new NaiveStationStrategy(
				radioStation);
		Song nowPlayingSong = stationStrategyNowPlaying.getNextSong();
		Set<ObjectId> nowPlayingSongScrobblersIds = stationStrategyNowPlaying
				.getRecentScrobblers();
		List<User> nowPlayingSongScrobblers = new ArrayList<User>();
		if (radioStation.getScrobbler().isGroupScrobbler()) {
			nowPlayingSongScrobblers = userDAO
					.findUsersByIds(nowPlayingSongScrobblersIds);
		}

		StationHistoryEntry nowPlayingHistoryEntry = new StationHistoryEntry(
				radioStation.getId(), nowPlayingSong, null);
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		stationHistoryDAO.save(nowPlayingHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		radioStation.setNowPlaying(new Track(nowPlayingHistoryEntry,
				nowPlayingSong, nowPlayingSongScrobblers));

		// set lookAhead
		StationStrategy stationStrategyLookAhead = new NaiveStationStrategy(
				radioStation);
		Song lookAheadSong = stationStrategyLookAhead.getNextSong();
		Set<ObjectId> lookAheadSongScrobblersIds = stationStrategyLookAhead
				.getRecentScrobblers();
		List<User> lookAheadSongScrobblers = userDAO
				.findUsersByIds(lookAheadSongScrobblersIds);

		StationHistoryEntry lookAheadHistoryEntry = new StationHistoryEntry(
				radioStation.getId(), lookAheadSong, null);
		stationHistoryDAO.save(lookAheadHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		radioStation.setLookAhead(new Track(lookAheadHistoryEntry,
				lookAheadSong, lookAheadSongScrobblers));

		// save it
		savePostStations(radioStation);

		// update the DTO
		if (radioStation.getScrobbler().isGroupScrobbler()) {
			createDTOForPostStations(radioStation, radioStationDTO,
					nowPlayingSong, nowPlayingHistoryEntry,
					nowPlayingSongScrobblers, lookAheadSong,
					lookAheadHistoryEntry, lookAheadSongScrobblers);
		} else {
			createDTOForPostStations(radioStation, radioStationDTO,
					nowPlayingSong, nowPlayingHistoryEntry, lookAheadSong,
					lookAheadHistoryEntry);
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

	public void postNextSong(StationSongListDTO_V0_4 stationSongListDTO)
			throws SongwichAPIException {
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();

		// fetch the RadioStation
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		RadioStation radioStation = radioStationDAO.findById(new ObjectId(
				stationSongListDTO.getStationId()));
		if (radioStation == null) {
			throw new SongwichAPIException("Invalid stationId",
					APIStatus_V0_4.INVALID_PARAMETER);
		}

		// run the algorithm to decide what the lookAhead Song will be
		StationStrategy stationStrategy = new NaiveStationStrategy(radioStation);
		Song lookAheadSong = stationStrategy.getNextSong();

		// find out who the lookAhead scrobblers are if it's a group station
		List<User> lookAheadScrobblers = new ArrayList<User>();
		if (radioStation.getScrobbler().isGroupScrobbler()) {
			Set<ObjectId> lookAheadScrobblersIds = stationStrategy
					.getRecentScrobblers();
			UserDAO<ObjectId> userDao = new UserDAOMongo();
			lookAheadScrobblers = userDao
					.findUsersByIds(lookAheadScrobblersIds);
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
				lookAheadSong, lookAheadScrobblers));

		// update radioStation
		radioStationDAO.save(radioStation, getContext().getAppDeveloper()
				.getEmailAddress());

		// update the DataTransferObject
		if (radioStation.getScrobbler().isGroupScrobbler()) {
			updateDTOForPostNextSong(stationSongListDTO,
					nowPlayingHistoryEntry, radioStation.getNowPlaying()
							.getSongScrobblers(), lookAheadHistoryEntry,
					lookAheadScrobblers);
		} else {
			updateDTOForPostNextSong(stationSongListDTO,
					nowPlayingHistoryEntry, lookAheadHistoryEntry);
		}
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

		if (station.getScrobbler().isUserScrobbler()) {
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

	private void savePostStations(RadioStation radioStation) {
		// save RadioStation
		RadioStationDAOMongo radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.cascadeSave(radioStation, getContext()
				.getAppDeveloper().getEmailAddress());
	}

	private void createDTOForPostStations(RadioStation radioStation,
			RadioStationDTO_V0_4 radioStationDTO, Song nowPlayingSong,
			StationHistoryEntry nowPlayingHistoryEntry,
			List<User> nowPlayingSongScrobblers, Song lookAheadSong,
			StationHistoryEntry lookAheadHistoryEntry,
			List<User> lookAheadSongScrobblers) {

		createDTOForPostStations(radioStation, radioStationDTO, nowPlayingSong,
				nowPlayingHistoryEntry, lookAheadSong, lookAheadHistoryEntry);

		StationSongListEntryDTO_V0_4 nowPlayingDTO = radioStationDTO
				.getNowPlaying();
		nowPlayingDTO
				.setRecentScrobblers(createScrobblersDTO(nowPlayingSongScrobblers));

		StationSongListEntryDTO_V0_4 lookAheadDTO = radioStationDTO
				.getLookAhead();
		lookAheadDTO
				.setRecentScrobblers(createScrobblersDTO(lookAheadSongScrobblers));
	}

	private void createDTOForPostStations(RadioStation radioStation,
			RadioStationDTO_V0_4 radioStationDTO, Song nowPlayingSong,
			StationHistoryEntry nowPlayingHistoryEntry, Song lookAheadSong,
			StationHistoryEntry lookAheadHistoryEntry) {

		// update the DataTransferObject
		radioStationDTO.setStationId(radioStation.getId().toString());

		// nowPlaying
		StationSongListEntryDTO_V0_4 nowPlayingDTO = new StationSongListEntryDTO_V0_4();
		nowPlayingDTO
				.setArtistName(nowPlayingSong.getArtistsNames().toString());
		nowPlayingDTO.setTrackTitle(nowPlayingSong.getSongTitle());
		nowPlayingDTO.setFeedbackId(nowPlayingHistoryEntry.getId().toString());
		radioStationDTO.setNowPlaying(nowPlayingDTO);

		// lookAhead
		StationSongListEntryDTO_V0_4 lookAheadDTO = new StationSongListEntryDTO_V0_4();
		lookAheadDTO.setArtistName(lookAheadSong.getArtistsNames().toString());
		lookAheadDTO.setTrackTitle(lookAheadSong.getSongTitle());
		lookAheadDTO.setFeedbackId(lookAheadHistoryEntry.getId().toString());
		radioStationDTO.setLookAhead(lookAheadDTO);
	}

	private static List<UserDTO_V0_4> createScrobblersDTO(List<User> scrobblers) {
		List<UserDTO_V0_4> scrobblersDTO = new ArrayList<UserDTO_V0_4>();
		UserDTO_V0_4 userDTO;
		for (User scrobbler : scrobblers) {
			userDTO = new UserDTO_V0_4();
			userDTO.setUserId(scrobbler.getId().toString());
			userDTO.setName(scrobbler.getName());
			scrobblersDTO.add(userDTO);
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

		List<String> scrobblerIds = new ArrayList<String>();
		for (ObjectId scrobblerId : station.getScrobbler()
				.getActiveScrobblersUserIds()) {
			scrobblerIds.add(scrobblerId.toString());
		}
		stationDTO.setScrobblerIds(scrobblerIds);

		if (showSongs) {
			StationSongListEntryDTO_V0_4 songListEntryDTO = new StationSongListEntryDTO_V0_4();
			songListEntryDTO.setTrackTitle(station.getNowPlaying().getSong()
					.getSongTitle());
			songListEntryDTO.setArtistName(station.getNowPlaying().getSong()
					.getArtistsNames().toString());
			songListEntryDTO.setFeedbackId(station.getNowPlaying()
					.getStationHistoryEntry().getId().toString());
			songListEntryDTO.setRecentScrobblers(createScrobblersDTO(station
					.getNowPlaying().getSongScrobblers()));
			stationDTO.setNowPlaying(songListEntryDTO);

			songListEntryDTO = new StationSongListEntryDTO_V0_4();
			songListEntryDTO.setTrackTitle(station.getLookAhead().getSong()
					.getSongTitle());
			songListEntryDTO.setArtistName(station.getLookAhead().getSong()
					.getArtistsNames().toString());
			songListEntryDTO.setFeedbackId(station.getLookAhead()
					.getStationHistoryEntry().getId().toString());
			songListEntryDTO.setRecentScrobblers(createScrobblersDTO(station
					.getLookAhead().getSongScrobblers()));
			stationDTO.setLookAhead(songListEntryDTO);
		}

		return stationDTO;
	}

	private void updateDTOForPostNextSong(
			StationSongListDTO_V0_4 stationSongListDTO,
			StationHistoryEntry nowPlayingHistoryEntry,
			List<User> nowPlayingScrobblers,
			StationHistoryEntry lookAheadHistoryEntry,
			List<User> lookAheadScrobblers) {

		updateDTOForPostNextSong(stationSongListDTO, nowPlayingHistoryEntry,
				lookAheadHistoryEntry);

		// updates the song scrobblers
		StationSongListEntryDTO_V0_4 nowPlayingSongListEntryDTO = stationSongListDTO
				.getNowPlaying();
		nowPlayingSongListEntryDTO
				.setRecentScrobblers(createScrobblersDTO(nowPlayingScrobblers));
		StationSongListEntryDTO_V0_4 lookAheadSongListEntryDTO = stationSongListDTO
				.getLookAhead();
		lookAheadSongListEntryDTO
				.setRecentScrobblers(createScrobblersDTO(lookAheadScrobblers));
	}

	private void updateDTOForPostNextSong(
			StationSongListDTO_V0_4 stationSongListDTO,
			StationHistoryEntry nowPlayingHistoryEntry,
			StationHistoryEntry lookAheadHistoryEntry) {

		// sets StationSongListDTO's nowPlaying
		StationSongListEntryDTO_V0_4 nowPlayingSongListEntryDTO = new StationSongListEntryDTO_V0_4();
		nowPlayingSongListEntryDTO.setArtistName(nowPlayingHistoryEntry
				.getSong().getArtistsNames().toString());
		nowPlayingSongListEntryDTO.setTrackTitle(nowPlayingHistoryEntry
				.getSong().getSongTitle());
		nowPlayingSongListEntryDTO.setFeedbackId(nowPlayingHistoryEntry.getId()
				.toString());
		stationSongListDTO.setNowPlaying(nowPlayingSongListEntryDTO);

		// sets StationSongListDTO's lookAhead
		StationSongListEntryDTO_V0_4 lookAheadSongListEntryDTO = new StationSongListEntryDTO_V0_4();
		lookAheadSongListEntryDTO.setArtistName(lookAheadHistoryEntry.getSong()
				.getArtistsNames().toString());
		lookAheadSongListEntryDTO.setTrackTitle(lookAheadHistoryEntry.getSong()
				.getSongTitle());
		lookAheadSongListEntryDTO.setFeedbackId(lookAheadHistoryEntry.getId()
				.toString());
		stationSongListDTO.setLookAhead(lookAheadSongListEntryDTO);
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
