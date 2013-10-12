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
import views.api.stations.NewRadioStationDTO_V0_4;
import views.api.stations.RadioStationDTO_V0_4;
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

	public void postStations(NewRadioStationDTO_V0_4 newRadioStationDTO)
			throws SongwichAPIException {

		authenticatePostStations(newRadioStationDTO);

		// creates either a User RadioStation or a Group RadioStation
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		ScrobblerBridge scrobblerBridge;
		if (newRadioStationDTO.getScrobblerIds().size() == 1) {
			ObjectId userId = new ObjectId(newRadioStationDTO.getScrobblerIds()
					.get(0));
			// TODO: check if the user exists
			scrobblerBridge = new ScrobblerBridge(userDAO.findById(userId));
		} else {
			// validation guarantees there will be multiple scrobblerIds
			List<String> userIds = newRadioStationDTO.getScrobblerIds();
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
					newRadioStationDTO.getGroupName(), groupMembers));
		}
		String imageUrl = newRadioStationDTO.getImageUrl();
		RadioStation radioStation = new RadioStation(
				newRadioStationDTO.getStationName(), scrobblerBridge, imageUrl);

		// set nowPlaying
		StationStrategy stationStrategy = new NaiveStationStrategy();
		Song nowPlayingSong = stationStrategy.next(radioStation);
		StationHistoryEntry nowPlayingHistoryEntry = new StationHistoryEntry(
				radioStation.getId(), nowPlayingSong, null);
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		stationHistoryDAO.save(nowPlayingHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		radioStation.setNowPlaying(new Track(nowPlayingHistoryEntry,
				nowPlayingSong));

		// set lookAhead
		Song lookAheadSong = stationStrategy.next(radioStation);
		StationHistoryEntry lookAheadHistoryEntry = new StationHistoryEntry(
				radioStation.getId(), lookAheadSong, null);
		stationHistoryDAO.save(lookAheadHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		radioStation.setLookAhead(new Track(lookAheadHistoryEntry,
				lookAheadSong));

		// save RadioStation
		RadioStationDAOMongo radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.cascadeSave(radioStation, getContext()
				.getAppDeveloper().getEmailAddress());

		// update the DataTransferObject
		newRadioStationDTO.setStationId(radioStation.getId().toString());
		// nowPlaying
		StationSongListEntryDTO_V0_4 nowPlayingDTO = new StationSongListEntryDTO_V0_4();
		nowPlayingDTO
				.setArtistName(nowPlayingSong.getArtistsNames().toString());
		nowPlayingDTO.setTrackTitle(nowPlayingSong.getSongTitle());
		nowPlayingDTO.setFeedbackId(nowPlayingHistoryEntry.getId().toString());
		newRadioStationDTO.setNowPlaying(nowPlayingDTO);
		// lookAhead
		StationSongListEntryDTO_V0_4 lookAheadDTO = new StationSongListEntryDTO_V0_4();
		lookAheadDTO.setArtistName(lookAheadSong.getArtistsNames().toString());
		lookAheadDTO.setTrackTitle(lookAheadSong.getSongTitle());
		lookAheadDTO.setFeedbackId(lookAheadHistoryEntry.getId().toString());
		newRadioStationDTO.setLookAhead(lookAheadDTO);
	}

	private void authenticatePostStations(
			NewRadioStationDTO_V0_4 newRadioStationDTO)
			throws SongwichAPIException {
		if (!newRadioStationDTO.getScrobblerIds().contains(
				getContext().getUser().getId().toString())) {
			throw new SongwichAPIException(
					APIStatus_V0_4.UNAUTHORIZED.toString(),
					APIStatus_V0_4.UNAUTHORIZED);
		}
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
		StationStrategy stationStrategy = new NaiveStationStrategy();
		Song lookAheadSong = stationStrategy.next(radioStation);

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
				lookAheadSong));

		// update radioStation
		radioStationDAO.save(radioStation, getContext().getAppDeveloper()
				.getEmailAddress());

		// update the DataTransferObject
		updateDTOForPostNextSong(stationSongListDTO, nowPlayingHistoryEntry,
				lookAheadHistoryEntry);
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
			stationDTO.setNowPlaying(songListEntryDTO);

			songListEntryDTO = new StationSongListEntryDTO_V0_4();
			songListEntryDTO.setTrackTitle(station.getLookAhead().getSong()
					.getSongTitle());
			songListEntryDTO.setArtistName(station.getLookAhead().getSong()
					.getArtistsNames().toString());
			stationDTO.setLookAhead(songListEntryDTO);
		}

		return stationDTO;
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
