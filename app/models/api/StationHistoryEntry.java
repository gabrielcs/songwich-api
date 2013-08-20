package models.api;

import java.util.HashSet;
import java.util.Set;

import models.api.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Id;

@Embedded
public class StationHistoryEntry extends Model {
	@Id
	private ObjectId id;
	
	@Embedded
	private Song song;
	
	private long timestamp;
	
	@Embedded
	private Set<SongFeedback> songFeedback = new HashSet<SongFeedback>();
	
	protected StationHistoryEntry() {
		super();
	}
	
	public StationHistoryEntry(Song song, long timestamp) {
		super();
		setSong(song);
		setTimestamp(timestamp);
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Set<SongFeedback> getSongFeedback() {
		return songFeedback;
	}

	public void setSongFeedback(Set<SongFeedback> songFeedback) {
		this.songFeedback = songFeedback;
	}
	
	public void addSongFeedback(SongFeedback songFeedback) {
		this.songFeedback.add(songFeedback);
	}

	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "StationHistoryEntry [id=" + id + ", song=" + song
				+ ", timestamp=" + timestamp + ", songFeedback=" + songFeedback
				+ ", super.toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((songFeedback == null) ? 0 : songFeedback.hashCode());
		result = prime * result + ((song == null) ? 0 : song.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StationHistoryEntry other = (StationHistoryEntry) obj;
		if (songFeedback == null) {
			if (other.songFeedback != null)
				return false;
		} else if (!songFeedback.equals(other.songFeedback))
			return false;
		if (song == null) {
			if (other.song != null)
				return false;
		} else if (!song.equals(other.song))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
}
