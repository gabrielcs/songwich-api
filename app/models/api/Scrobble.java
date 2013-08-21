package models.api;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import models.api.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.utils.IndexDirection;

@Entity
public class Scrobble extends Model {
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId userId;

	private Song song;

	@Deprecated
	@NotSaved
	private String songTitle;

	@Deprecated
	@NotSaved
	private List<String> artistsNames = new ArrayList<String>();

	@Indexed(IndexDirection.DESC)
	private Long timestamp;

	@Indexed
	private Boolean choosenByUser;

	private String player;

	protected Scrobble() {
		super();
	}

	public Scrobble(ObjectId userId, Song song, GregorianCalendar timestamp,
			Boolean choosenByUser, String player, String createdBy) {
		super(createdBy);
		this.userId = userId;
		this.song = song;
		this.timestamp = timestamp.getTimeInMillis();
		this.choosenByUser = choosenByUser;
		this.player = player;
	}

	public Scrobble(ObjectId userId, Song song, Long timestamp,
			Boolean choosenByUser, String player, String createdBy) {
		super(createdBy);
		this.userId = userId;
		this.song = song;
		this.timestamp = timestamp;
		this.choosenByUser = choosenByUser;
		this.player = player;
	}

	/**
	 * @return the user
	 */
	public ObjectId getUserId() {
		return userId;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUserId(ObjectId userId, String modifiedBy) {
		this.userId = userId;
		setLastModifiedBy(modifiedBy);
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Long timestamp, String modifiedBy) {
		this.timestamp = timestamp;
		setLastModifiedBy(modifiedBy);
	}

	public void setTimestamp(GregorianCalendar timestamp, String modifiedBy) {
		this.timestamp = timestamp.getTimeInMillis();
		setLastModifiedBy(modifiedBy);
	}

	/**
	 * @return the choosenByUser
	 */
	public Boolean isChoosenByUser() {
		return choosenByUser;
	}

	/**
	 * @param choosenByUser
	 *            the choosenByUser to set
	 */
	public void setChoosenByUser(Boolean choosenByUser, String modifiedBy) {
		this.choosenByUser = choosenByUser;
		setLastModifiedBy(modifiedBy);
	}

	/**
	 * @return the player
	 */
	public String getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(String player, String modifiedBy) {
		this.player = player;
		setLastModifiedBy(modifiedBy);
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song, String modifiedBy) {
		this.song = song;
		setLastModifiedBy(modifiedBy);
	}

	/*
	 * Handle deprecated song properties
	 */
	@PostLoad
	protected void handleDeprecatedSongProperties() {
		if (song == null && songTitle != null && artistsNames != null) {
			this.song = new Song(songTitle, artistsNames);
		}
	}

	@Override
	public String toString() {
		return "Scrobble [id=" + id + ", userId=" + userId + ", song=" + song
				+ ", timestamp=" + timestamp + ", choosenByUser="
				+ choosenByUser + ", player=" + player + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((choosenByUser == null) ? 0 : choosenByUser.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + ((song == null) ? 0 : song.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		Scrobble other = (Scrobble) obj;
		if (choosenByUser == null) {
			if (other.choosenByUser != null)
				return false;
		} else if (!choosenByUser.equals(other.choosenByUser))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (song == null) {
			if (other.song != null)
				return false;
		} else if (!song.equals(other.song))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
