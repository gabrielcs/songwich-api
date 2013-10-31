package behavior.api.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.api.scrobbles.Scrobble;
import models.api.scrobbles.Song;
import models.api.stations.RadioStation;
import models.api.stations.StationHistoryEntry;

import org.bson.types.ObjectId;

import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;
import database.api.scrobbles.ScrobbleDAO;
import database.api.scrobbles.ScrobbleDAOMongo;
import database.api.stations.StationHistoryDAO;
import database.api.stations.StationHistoryDAOMongo;

/*
 * It doesn't play an artist that has been played in the last 20 songs 
 * (and therefore no more than 3 times during 60 songs).
 * 
 * It doesn't play the same song if it was one of the last 60 to be played.
 * 
 * 
 * This only works if stations are activated only after they get to at least 
 * 60 scrobbles by at least 20 different artists.
 * 
 * It also builds on the premise that both the station's currentSong
 * and lookAhead are already saved as StationHistoryEntries on the database.
 */
public class PseudoDMCAStationStrategy implements StationStrategy {

	private RadioStation radioStation;
	private List<Scrobble> scrobbles;
	private Set<ObjectId> activeScrobblersIds;
	private List<Song> last59PlayedSongs;
	private List<List<String>> last2PlayedArtists;
	private Set<List<String>> artistsPlayed3TimesInLast59Songs;
	private Song nextSong;

	public PseudoDMCAStationStrategy(RadioStation radioStation) {
		super();
		this.radioStation = radioStation;
		activeScrobblersIds = extractActiveScrobblers();
		scrobbles = extractScrobbles();
		saveRelevantHistory();
	}

	@Override
	public Song getNextSong() throws SongwichAPIException {
		if (nextSong != null) {
			// the algorithm has already been invoked
			return nextSong;
		}

		int index;
		Scrobble currentScrobble = null;
		List<String> nextSongArtist;

		while (!scrobbles.isEmpty()) {
			index = (int) (Math.random() * scrobbles.size());
			currentScrobble = scrobbles.get(index);
			nextSong = currentScrobble.getSong();
			nextSongArtist = nextSong.getArtistsNames();

			if (last2PlayedArtists.contains(nextSongArtist)
					|| artistsPlayed3TimesInLast59Songs
							.contains(nextSongArtist)) {
				removeArtistFromPotentialSelection(scrobbles, nextSongArtist);
			} else if (last59PlayedSongs.contains(nextSong)) {
				scrobbles.remove(currentScrobble);
			} else {
				// success
				return nextSong;
			}
		}
		throw new SongwichAPIException(
				"There are not sufficient scrobbles to calculate the next song to be played",
				APIStatus_V0_4.UNKNOWN_ERROR);
	}

	private void removeArtistFromPotentialSelection(List<Scrobble> scrobbles,
			List<String> artistsNames) {
		// remove al scrobbles from the same artist to make random
		// selection a bit more efficient
		for (int i = 0; i < scrobbles.size(); i++) {
			Scrobble scrobble = scrobbles.get(i);
			if (scrobble.getSong().getArtistsNames()
					.equals(nextSong.getArtistsNames())) {
				scrobbles.remove(i);
			}
		}
	}

	@Override
	public Set<ObjectId> getRecentScrobblers() {
		if (activeScrobblersIds != null) {
			// scrobblers have already been identified
			return activeScrobblersIds;
		}

		activeScrobblersIds = new HashSet<ObjectId>();
		for (Scrobble scrobble : scrobbles) {
			if (scrobble.getSong().equals(nextSong)) {
				activeScrobblersIds.add(scrobble.getUserId());
			}
		}
		return activeScrobblersIds;
	}

	private Set<ObjectId> extractActiveScrobblers() {
		return radioStation.getScrobbler().getActiveScrobblersUserIds();
	}

	private List<Scrobble> extractScrobbles() {
		ScrobbleDAO<ObjectId> scrobbleDao = new ScrobbleDAOMongo();
		return scrobbleDao.findByUserIds(activeScrobblersIds, true);
	}

	private void saveRelevantHistory() {
		StationHistoryDAO<ObjectId> stationHistoryDao = new StationHistoryDAOMongo();
		List<StationHistoryEntry> last60HistoryEntries = stationHistoryDao
				.findLastEntriesByStationId(radioStation.getId(), 59);
		last59PlayedSongs = extractSongs(last60HistoryEntries);
		last2PlayedArtists = extractLast2Artists(last59PlayedSongs);
		artistsPlayed3TimesInLast59Songs = extractArtistsPlayed3Times(last59PlayedSongs);
	}

	private Set<List<String>> extractArtistsPlayed3Times(
			List<Song> last59PlayedSongs) {
		Map<List<String>, Integer> artistCount = new HashMap<List<String>, Integer>();

		// count them all
		Integer currentCount;
		for (Song song : last59PlayedSongs) {
			currentCount = artistCount.get(song.getArtistsNames());
			currentCount = (currentCount == null) ? 1 : currentCount + 1;
			artistCount.put(song.getArtistsNames(), currentCount);
		}

		// check which ones have been played 3 or more times
		// (do not directly modify setAllArtists or there might be a
		// ConcurrentModificationException)
		Set<List<String>> setAllArtists = artistCount.keySet();
		Set<List<String>> resultSet = new HashSet<List<String>>();
		for (List<String> artist : setAllArtists) {
			if (artistCount.get(artist) >= 3) {
				resultSet.add(artist);
			}
		}
		return resultSet;
	}

	private List<List<String>> extractLast2Artists(List<Song> songList) {
		List<List<String>> artistList = new ArrayList<List<String>>();
		int i = 0;
		for (Song song : songList) {
			artistList.add(song.getArtistsNames());
			i++;
			if (i == 2) {
				break;
			}
		}
		return artistList;
	}

	private List<Song> extractSongs(
			List<StationHistoryEntry> stationHistoryEntries) {
		List<Song> songList = new ArrayList<Song>();
		for (StationHistoryEntry stationHistoryEntry : stationHistoryEntries) {
			songList.add(stationHistoryEntry.getSong());
		}
		return songList;
	}

	@Override
	public String toString() {
		return "NaiveStationStrategy []";
	}
}
