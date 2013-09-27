package behavior.api.usecases.stations;

import java.net.MalformedURLException;
import java.net.URL;
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

	public void postStations(RadioStationDTO_V0_4 radioStationDTO)
			throws SongwichAPIException {

		// creates either a User RadioStation or a Group RadioStation
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		ScrobblerBridge scrobblerBridge;
		if (radioStationDTO.getScrobblerIds().size() == 1) {
			ObjectId userId = new ObjectId(radioStationDTO.getScrobblerIds()
					.get(0));
			scrobblerBridge = new ScrobblerBridge(userDAO.findById(userId));
		} else {
			// validation guarantees there will be multiple scrobblerIds
			List<String> userIds = radioStationDTO.getScrobblerIds();
			Set<GroupMember> groupMembers = new HashSet<GroupMember>(
					userIds.size());
			User user;
			for (String userId : userIds) {
				user = userDAO.findById(new ObjectId(userId));
				groupMembers.add(new GroupMember(user, System
						.currentTimeMillis()));
			}
			// add Group name to DTO
			scrobblerBridge = new ScrobblerBridge(new Group(
					radioStationDTO.getGroupName(), groupMembers));
		}
		URL imageUrl = null;
		try {
			imageUrl = new URL(radioStationDTO.getImageUrl());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RadioStation radioStation = new RadioStation(radioStationDTO.getStationName(),
				scrobblerBridge, imageUrl);

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
		radioStationDAO.cascadeSave(radioStation, getContext().getAppDeveloper()
				.getEmailAddress());
		
		// update the DataTransferObject
		radioStationDTO.setStationId(radioStation.getId().toString());
		// nowPlaying
		StationSongListEntryDTO_V0_4 nowPlayingDTO = new StationSongListEntryDTO_V0_4();
		nowPlayingDTO.setArtistName(nowPlayingSong.getArtistsNames().toString());
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

		// turn the lookAhead Track into next
		Track nowPlayingTrack = radioStation.getLookAhead();
		StationHistoryEntry nowPlayingHistoryEntry = nowPlayingTrack
				.getStationHistoryEntry();
		nowPlayingHistoryEntry.setTimestamp(System.currentTimeMillis());
		stationHistoryDAO.save(nowPlayingHistoryEntry, getContext()
				.getAppDeveloper().getEmailAddress());
		radioStation.setNowPlaying(nowPlayingTrack);

		// run the algorithm to decide what the lookAhead Song will be
		StationStrategy stationStrategy = new NaiveStationStrategy();
		Song lookAheadSong = stationStrategy.next(radioStation);

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
