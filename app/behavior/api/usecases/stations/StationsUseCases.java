package behavior.api.usecases.stations;

import java.util.HashMap;
import java.util.Map;

import models.api.scrobbles.Song;
import models.api.stations.RadioStation;
import models.api.stations.ScrobblerBridge;
import models.api.stations.StationHistoryEntry;
import models.api.stations.Track;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import views.api.stations.StationSongListDTO_V0_4;
import views.api.stations.StationSongListEntryDTO_V0_4;
import behavior.api.algorithms.NaiveStationStrategy;
import behavior.api.algorithms.StationStrategy;
import behavior.api.usecases.RequestContext;
import behavior.api.usecases.UseCase;
import database.api.stations.RadioStationDAO;
import database.api.stations.RadioStationDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;

public class StationsUseCases extends UseCase {

	public StationsUseCases(RequestContext context) {
		super(context);
	}

	/*
	 * It returns a Map with the new radio station as the value and the first
	 * song to be played
	 *
	public Map<StationHistoryEntry, RadioStation> postStations(
			String stationName, ScrobblerBridge stationScrobblers) {

		RadioStation radioStation = new RadioStation(stationName,
				stationScrobblers);
		RadioStationDAO<ObjectId> radioStationDAO = new RadioStationDAOMongo();
		radioStationDAO.save(radioStation, getContext().getAppDeveloper()
				.getEmailAddress());

		StationHistoryEntry stationHistory = postNextSong(radioStation);

		Map<StationHistoryEntry, RadioStation> radioStationMap = new HashMap<StationHistoryEntry, RadioStation>();
		radioStationMap.put(stationHistory, radioStation);
		return radioStationMap;
	}
	*/

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

		// update DataTransferObject
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
		stationSongListDTO.setNowPlaying(lookAheadSongListEntryDTO);
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
