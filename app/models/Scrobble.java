package models;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import models.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

@Entity
public class Scrobble extends Model {
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId userId;

	private String songTitle;

	private List<String> artistsNames;

	@Indexed(IndexDirection.DESC)
	private Long timestamp;

	@Indexed
	private Boolean choosenByUser;

	private String player;

	protected Scrobble() {
		super();
	}

	public Scrobble(ObjectId userId, String songTitle, String artistName,
			Long timestamp, Boolean choosenByUser, String service, String createdBy) {
		super(createdBy);
		setUserId(userId);
		setSongTitle(songTitle);
		artistsNames = new ArrayList<String>();
		addArtistsName(artistName);
		setTimestamp(timestamp);
		setChoosenByUser(choosenByUser);
		setService(service);
	}

	public Scrobble(ObjectId userId, String songTitle, String artistName,
			GregorianCalendar timestamp, Boolean choosenByUser, String service,
			String createdBy) {
		super(createdBy);
		setUserId(userId);
		setSongTitle(songTitle);
		artistsNames = new ArrayList<String>();
		addArtistsName(artistName);
		setTimestamp(timestamp);
		setChoosenByUser(choosenByUser);
		setService(service);
	}

	public Scrobble(ObjectId userId, String songTitle,
			List<String> artistsNames, Long timestamp, Boolean choosenByUser,
			String service, String createdBy) {
		super(createdBy);
		setUserId(userId);
		setSongTitle(songTitle);
		setArtistsNames(artistsNames);
		setTimestamp(timestamp);
		setChoosenByUser(choosenByUser);
		setService(service);
	}

	public Scrobble(ObjectId userId, String songTitle,
			List<String> artistsNames, GregorianCalendar timestamp,
			Boolean choosenByUser, String service, String createdBy) {
		super(createdBy);
		setUserId(userId);
		setSongTitle(songTitle);
		setArtistsNames(artistsNames);
		setTimestamp(timestamp);
		setChoosenByUser(choosenByUser);
		setService(service);
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
	}

	/**
	 * @return the songTitle
	 */
	public String getSongTitle() {
		return songTitle;
	}

	/**
	 * @param songTitle
	 *            the songTitle to set
	 */
	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}

	/**
	 * @return the artistsNames
	 */
	public List<String> getArtistsNames() {
		return artistsNames;
	}

	/**
	 * @param artistsNames
	 *            the artistsNames to set
	 */
	public void setArtistsNames(List<String> artistsNames) {
		this.artistsNames = artistsNames;
	}

	public void addArtistsName(String artistsName) {
		artistsNames.add(artistsName);
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
	}

	public void setTimestamp(GregorianCalendar timestamp) {
		this.timestamp = timestamp.getTimeInMillis();
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
	public void setChoosenByUser(Boolean choosenByUser) {
		this.choosenByUser = choosenByUser;
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
	public void setService(String player) {
		this.player = player;
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Scrobble [id=" + id + ", userId=" + userId + ", songTitle="
				+ songTitle + ", artistsNames=" + artistsNames + ", timestamp="
				+ timestamp + ", choosenByUser=" + choosenByUser + ", player="
				+ player + ", super.toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((artistsNames == null) ? 0 : artistsNames.hashCode());
		result = prime * result
				+ ((choosenByUser == null) ? 0 : choosenByUser.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result
				+ ((songTitle == null) ? 0 : songTitle.hashCode());
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
		if (artistsNames == null) {
			if (other.artistsNames != null)
				return false;
		} else if (!artistsNames.equals(other.artistsNames))
			return false;
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
		if (songTitle == null) {
			if (other.songTitle != null)
				return false;
		} else if (!songTitle.equals(other.songTitle))
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
