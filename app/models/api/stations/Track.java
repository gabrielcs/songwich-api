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
	@Reference(lazy = true)
	private StationHistoryEntry stationHistoryEntry;

	// this will be an empty list if it is not a group station
	// TODO: check what happens if we do ignoreMissing=true
	@Reference(lazy = true, ignoreMissing = true)
	private List<User> songScrobblers = new ArrayList<User>();

	protected Track() {
		super();
	}

	public Track(StationHistoryEntry stationHistoryEntry) {
		super();
		setStationHistoryEntry(stationHistoryEntry);
	}

	public Track(StationHistoryEntry stationHistoryEntry,
			List<User> songScrobblers) {
		super();
		setStationHistoryEntry(stationHistoryEntry);
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
		return this.stationHistoryEntry.getSong();
	}

	public String getAlbumTitle() {
		return this.stationHistoryEntry.getSong().getAlbumTitle();
	}

	public String getSongTitle() {
		return this.stationHistoryEntry.getSong().getSongTitle();
	}

	public List<String> getArtistsNames() {
		return this.stationHistoryEntry.getSong().getArtistsNames();
	}

	@Override
	public String toString() {
		return "Track [stationHistoryEntry=" + stationHistoryEntry
				+ ", songScrobblers=" + songScrobblers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
