package models.api.scrobbles;

import java.util.GregorianCalendar;

import models.api.MongoEntity;
import models.api.MongoModelImpl;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

@Entity
public class Scrobble extends MongoModelImpl implements MongoEntity {
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId userId;

	private Song song;
	
	@Indexed(IndexDirection.DESC)
	private Long timestamp;
	
	@Indexed
	private Boolean chosenByUser;

	private String player;
	
	/*
	@Deprecated
	@NotSaved
	private String songTitle;

	@Deprecated
	@NotSaved
	private List<String> artistsNames = new ArrayList<String>();
	
	@Deprecated
	@NotSaved
	private boolean choosenByUser;
	*/

	protected Scrobble() {
		super();
	}

	public Scrobble(ObjectId userId, Song song, GregorianCalendar timestamp,
			Boolean chosenByUser, String player) {
		this.userId = userId;
		this.song = song;
		this.timestamp = timestamp.getTimeInMillis();
		this.chosenByUser = chosenByUser;
		this.player = player;
	}

	public Scrobble(ObjectId userId, Song song, Long timestamp,
			Boolean chosenByUser, String player) {
		this.userId = userId;
		this.song = song;
		this.timestamp = timestamp;
		this.chosenByUser = chosenByUser;
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
	public void setUserId(ObjectId userId) {
		this.userId = userId;
		fireModelUpdated();
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
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		fireModelUpdated();
	}

	public void setTimestamp(GregorianCalendar timestamp) {
		this.timestamp = timestamp.getTimeInMillis();
		fireModelUpdated();
	}

	/**
	 * @return the choosenByUser
	 */
	public Boolean isChosenByUser() {
		return chosenByUser;
	}

	/**
	 * @param chosenByUser
	 *            the chosenByUser to set
	 */
	public void setChosenByUser(Boolean chosenByUser) {
		this.chosenByUser = chosenByUser;
		fireModelUpdated();
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
	public void setPlayer(String player) {
		this.player = player;
		fireModelUpdated();
	}

	/**
	 * @return the id
	 */
	@Override
	public ObjectId getId() {
		return id;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
		fireModelUpdated();
	}

	/*
	 * Handle deprecated properties
	 *
	@PostLoad
	protected void handleDeprecatedProperties() {
		if (song == null && songTitle != null && artistsNames != null) {
			this.song = new Song(songTitle, artistsNames);
		}
		
		if (chosenByUser == null && choosenByUser != null) {
			chosenByUser = new Boolean(choosenByUser);
		}
	}
	*/

	@Override
	public String toString() {
		return "Scrobble [id=" + id + ", userId=" + userId + ", song=" + song
				+ ", timestamp=" + timestamp + ", chosenByUser="
				+ chosenByUser + ", player=" + player + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((chosenByUser == null) ? 0 : chosenByUser.hashCode());
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
		if (chosenByUser == null) {
			if (other.chosenByUser != null)
				return false;
		} else if (!chosenByUser.equals(other.chosenByUser))
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
