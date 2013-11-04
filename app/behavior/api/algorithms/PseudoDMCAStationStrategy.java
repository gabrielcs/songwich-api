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
 * It doesn't play an artist more than 3 times during 60 songs nor twice in a 3-song span.
 * It doesn't play the same song if it was one of the last 60 to be played.
 * 
 * This only works if stations are activated only after they get to at least 
 * 60 scrobbles only counting a maximum of 3 per artist.
 */
public class PseudoDMCAStationStrategy implements StationStrategy {

	private RadioStation radioStation;
	private List<Scrobble> scrobbles;
	private Set<ObjectId> activeScrobblersIds, recentScrobblersIds;
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
	
	public static StationReadinessCalculator getStationReadinessCalculator() {
		return new PseudoDMCAStationReadinessCalculator();
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
		// the Behavior layer should maybe switch to another StationStrategy in
		// this case
		throw new SongwichAPIException(
				"There are not sufficient scrobbles to calculate the next song to be played",
				APIStatus_V0_4.UNKNOWN_ERROR);
	}

	private void removeArtistFromPotentialSelection(List<Scrobble> scrobbles,
			List<String> artistsNames) {
		// remove all scrobbles from the same artist to make random
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
		if (recentScrobblersIds != null) {
			// scrobblers have already been identified
			return recentScrobblersIds;
		}

		recentScrobblersIds = new HashSet<ObjectId>();
		for (Scrobble scrobble : scrobbles) {
			if (scrobble.getSong().equals(nextSong)) {
				recentScrobblersIds.add(scrobble.getUserId());
			}
		}
		return recentScrobblersIds;
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

	// TODO: check if it's not the opposite
	private List<List<String>> extractLast2Artists(List<Song> songList) {
		List<List<String>> artistList = new ArrayList<List<String>>();
		int i = 0;
		for (Song song : songList) {
			artistList.add(song.getArtistsNames());
			//System.out.println("Last 2 artists: " + artistList);
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
	
	public static class PseudoDMCAStationReadinessCalculator implements StationReadinessCalculator {
		@Override
		public Float getStationReadiness(RadioStation station) {
			// calculate station readiness
			Set<ObjectId> scrobblersIds = station.getScrobbler().getActiveScrobblersUserIds();
			ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
			List<Scrobble> scrobbles = scrobbleDAO.findByUserIds(scrobblersIds, true);
			Map<List<String>, Integer> artistCount = new HashMap<List<String>, Integer>();
			Integer currentCount;
			for (Scrobble scrobble : scrobbles) {
				currentCount = artistCount.get(scrobble.getSong().getArtistsNames());
				currentCount = (currentCount == null) ? 1 : currentCount + 1;
				// only count 3 songs per artist
				if (currentCount <= 3) {
					artistCount.put(scrobble.getSong().getArtistsNames(), currentCount);
				}
			}
			currentCount = 0;
			for (Integer currentArtistCount : artistCount.values()) {
				currentCount = currentCount + currentArtistCount;
			}
			
			// ask for at least 61 songs (max 3 per artist)
			return ((float) currentCount) / 61;
		}
	}
}
