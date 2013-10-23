package models.api.stations;

import java.util.ArrayList;
import java.util.List;

import models.api.scrobbles.Song;
import models.api.scrobbles.User;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Reference;

/*
 * A Track is a Song played on a Station.
 */
@Embedded
public class Track {
	@Reference
	private StationHistoryEntry stationHistoryEntry;
	
	private Song song;
	
	// this will be an empty list if it is not a group station
	@Reference
	private List<User> songScrobblers = new ArrayList<User>();

	protected Track() {
		super();
	}

	public Track(StationHistoryEntry stationHistoryEntry, Song song, List<User> songScrobblers) {
		super();
		setStationHistoryEntry(stationHistoryEntry);
		setSong(song);
		setSongScrobblers(songScrobblers);
	}
	
	public List<User> getSongScrobblers() {
		return songScrobblers;
	}

	public void setSongScrobblers(List<User> songScrobblers) {
		// it will be null if it is not a group station
		if (songScrobblers != null) {
			this.songScrobblers = songScrobblers;
		}
	}

	public StationHistoryEntry getStationHistoryEntry() {
		return stationHistoryEntry;
	}

	public void setStationHistoryEntry(StationHistoryEntry stationHistoryEntry) {
		this.stationHistoryEntry = stationHistoryEntry;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	@Override
	public String toString() {
		return "Track [stationHistoryEntry=" + stationHistoryEntry + ", song="
				+ song + ", songScrobblers=" + songScrobblers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((song == null) ? 0 : song.hashCode());
		result = prime * result
				+ ((songScrobblers == null) ? 0 : songScrobblers.hashCode());
		result = prime
				* result
				+ ((stationHistoryEntry == null) ? 0 : stationHistoryEntry
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Track other = (Track) obj;
		if (song == null) {
			if (other.song != null)
				return false;
		} else if (!song.equals(other.song))
			return false;
		if (songScrobblers == null) {
			if (other.songScrobblers != null)
				return false;
		} else if (!songScrobblers.equals(other.songScrobblers))
			return false;
		if (stationHistoryEntry == null) {
			if (other.stationHistoryEntry != null)
				return false;
		} else if (!stationHistoryEntry.equals(other.stationHistoryEntry))
			return false;
		return true;
	}
}
