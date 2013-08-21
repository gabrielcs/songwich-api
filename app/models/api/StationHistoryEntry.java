package models.api;

import java.util.HashSet;
import java.util.Set;

import models.api.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

@Entity
public class StationHistoryEntry extends Model {
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId stationId;

	@Embedded
	private Song song;

	@Indexed(IndexDirection.DESC)
	private Long timestamp;

	@Embedded
	private Set<SongFeedback> songFeedback = new HashSet<SongFeedback>();

	protected StationHistoryEntry() {
		super();
	}

	public StationHistoryEntry(ObjectId stationId, Song song, Long timestamp, String createdBy) {
		super(createdBy);
		this.stationId = stationId;
		this.song = song;
		this.timestamp = timestamp;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song, String modifiedBy) {
		this.song = song;
		setLastModifiedBy(modifiedBy);
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp, String modifiedBy) {
		this.timestamp = timestamp;
		setLastModifiedBy(modifiedBy);
	}

	public Set<SongFeedback> getSongFeedback() {
		return songFeedback;
	}

	public void setSongFeedback(Set<SongFeedback> songFeedback, String modifiedBy) {
		this.songFeedback = songFeedback;
		setLastModifiedBy(modifiedBy);
	}

	/**
	 * 
	 * @param songFeedback
	 * @param modifiedBy
	 * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
	 */
	public boolean addSongFeedback(SongFeedback songFeedback, String modifiedBy) {
		setLastModifiedBy(modifiedBy);
		return this.songFeedback.add(songFeedback);
		
	}

	public ObjectId getId() {
		return id;
	}

	public ObjectId getStationId() {
		return stationId;
	}

	public void setStationId(ObjectId stationId, String modifiedBy) {
		this.stationId = stationId;
		setLastModifiedBy(modifiedBy);
	}

	@Override
	public String toString() {
		return "StationHistoryEntry [id=" + id + ", stationId=" + stationId
				+ ", song=" + song + ", timestamp=" + timestamp
				+ ", songFeedback=" + songFeedback + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((song == null) ? 0 : song.hashCode());
		result = prime * result
				+ ((songFeedback == null) ? 0 : songFeedback.hashCode());
		result = prime * result
				+ ((stationId == null) ? 0 : stationId.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
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
		if (song == null) {
			if (other.song != null)
				return false;
		} else if (!song.equals(other.song))
			return false;
		if (songFeedback == null) {
			if (other.songFeedback != null)
				return false;
		} else if (!songFeedback.equals(other.songFeedback))
			return false;
		if (stationId == null) {
			if (other.stationId != null)
				return false;
		} else if (!stationId.equals(other.stationId))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}
}
