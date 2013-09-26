package models.api.stations;

import models.api.scrobbles.Song;

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
	
	protected Track() {
		super();
	}

	public Track(StationHistoryEntry stationHistoryEntry, Song song) {
		super();
		setStationHistoryEntry(stationHistoryEntry);
		setSong(song);
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
		return "Track [song=" + song + ", stationHistoryEntry="
				+ stationHistoryEntry + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((song == null) ? 0 : song.hashCode());
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
		if (stationHistoryEntry == null) {
			if (other.stationHistoryEntry != null)
				return false;
		} else if (!stationHistoryEntry.equals(other.stationHistoryEntry))
			return false;
		return true;
	}

}
