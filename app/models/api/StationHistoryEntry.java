package models.api;

import java.util.HashSet;
import java.util.Set;

import models.api.util.Model;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class StationHistoryEntry extends Model {
	@Embedded
	private Song song;
	
	private long timestamp;
	
	@Embedded
	private Set<SongFeedback> feedback = new HashSet<SongFeedback>();
	
	public StationHistoryEntry(Song song, long timestamp,
			Set<SongFeedback> feedback) {
		super();
		setSong(song);
		setTimestamp(timestamp);
		setFeedback(feedback);
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

	public Set<SongFeedback> getFeedback() {
		return feedback;
	}

	public void setFeedback(Set<SongFeedback> feedback) {
		this.feedback = feedback;
	}

	@Override
	public String toString() {
		return "StationHistoryEntry [song=" + song + ", timestamp=" + timestamp
				+ ", feedback=" + feedback + ", super.toString()=" + super.toString()
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((feedback == null) ? 0 : feedback.hashCode());
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
		if (feedback == null) {
			if (other.feedback != null)
				return false;
		} else if (!feedback.equals(other.feedback))
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
