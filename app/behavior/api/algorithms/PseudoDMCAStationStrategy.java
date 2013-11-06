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
public class PseudoDMCAStationStrategy extends AbstractStationStrategy
		implements StationStrategy, StationReadinessCalculator {

	private StationReadinessCalculator readinessCalculator;
	private List<Scrobble> relevantScrobbles;
	private List<Song> last59PlayedSongs;
	private List<List<String>> last2PlayedArtists;
	private Set<List<String>> artistsPlayed3TimesInLast59Songs;
	private Song nextSong;

	public PseudoDMCAStationStrategy(RadioStation station) {
		super(station);
	}

	@Override
	protected StationReadinessCalculator getStationReadinessCalculator() {
		if (readinessCalculator == null) {
			readinessCalculator = this.new PseudoDMCAStationReadinessCalculator();
		}
		return readinessCalculator;
	}

	// gets all scrobbles available
	@Override
	protected List<Scrobble> getRelevantScrobbles() {
		if (relevantScrobbles != null) {
			return relevantScrobbles;
		}
		ScrobbleDAO<ObjectId> scrobbleDAO = new ScrobbleDAOMongo();
		return scrobbleDAO.findByUserIds(getActiveScrobblers(), true);
	}

	@Override
	public Song getNextSong() throws SongwichAPIException {
		if (nextSong != null) {
			// the algorithm has already been invoked
			return nextSong;
		} else if (last59PlayedSongs == null) {
			saveRelevantHistory();
		}

		int index;
		Scrobble currentScrobble = null;
		List<String> nextSongArtist;

		while (!getRelevantScrobbles().isEmpty()) {
			index = (int) (Math.random() * getRelevantScrobbles().size());
			currentScrobble = getRelevantScrobbles().get(index);
			nextSong = currentScrobble.getSong();
			nextSongArtist = nextSong.getArtistsNames();

			if (last2PlayedArtists.contains(nextSongArtist)
					|| artistsPlayed3TimesInLast59Songs
							.contains(nextSongArtist)) {
				removeArtistFromPotentialSelection(getRelevantScrobbles(),
						nextSongArtist);
			} else if (last59PlayedSongs.contains(nextSong)) {
				getRelevantScrobbles().remove(currentScrobble);
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

	private void saveRelevantHistory() {
		StationHistoryDAO<ObjectId> stationHistoryDAO = new StationHistoryDAOMongo();
		List<StationHistoryEntry> last59HistoryEntries = stationHistoryDAO
				.findLastEntriesByStationId(getStation().getId(), 59);
		last59PlayedSongs = extractSongs(last59HistoryEntries);
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
		return "PseudoDMCAStationStrategy [readinessCalculator="
				+ readinessCalculator + ", nextSong=" + nextSong
				+ ", super.toString()=" + super.toString() + "]";
	}

	public class PseudoDMCAStationReadinessCalculator extends
			AbstractStationReadinessCalculator implements
			StationReadinessCalculator {

		protected PseudoDMCAStationReadinessCalculator() {
		}

		@Override
		protected Float calculateStationReadiness() {
			// calculate station readiness
			Map<List<String>, List<String>> artistSongsMap = new HashMap<List<String>, List<String>>();
			List<String> currentArtistSongTitles;
			for (Scrobble scrobble : getRelevantScrobbles()) {
				currentArtistSongTitles = artistSongsMap.get(scrobble.getSong()
						.getArtistsNames());
				if (currentArtistSongTitles == null) {
					currentArtistSongTitles = new ArrayList<String>(1);
					currentArtistSongTitles.add(scrobble.getSong()
							.getSongTitle());
					artistSongsMap.put(scrobble.getSong().getArtistsNames(),
							currentArtistSongTitles);
				} else if (currentArtistSongTitles.size() < 3) {
					if (!currentArtistSongTitles.contains(scrobble.getSong()
							.getSongTitle())) {
						currentArtistSongTitles.add(scrobble.getSong()
								.getSongTitle());
					}
				}
			}
			int count = 0;
			for (List<String> currentArtistSongTitle : artistSongsMap.values()) {
				count = count + currentArtistSongTitle.size();
			}

			// ask for at least 61 songs (max 3 per artist)
			final float MIN_SONGS = 61f;
			return ((count / MIN_SONGS) > 1) ? 1f : (count / MIN_SONGS);
		}

		@Override
		public String toString() {
			return "PseudoDMCAStationReadinessCalculator [super.toString()="
					+ super.toString() + "]";
		}
	}
}
