package models;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import models.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
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
	private long timestamp;

	@Indexed
	private boolean choosenByUser;

	@Reference
	private App service;

	protected Scrobble() {
		super();
	}

	public Scrobble(ObjectId userId, String songTitle, String artistName,
			long timestamp, boolean choosenByUser, App service, String createdBy) {
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
			GregorianCalendar timestamp, boolean choosenByUser, App service,
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
			List<String> artistsNames, long timestamp, boolean choosenByUser,
			App service, String createdBy) {
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
			boolean choosenByUser, App service, String createdBy) {
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
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestamp(GregorianCalendar timestamp) {
		this.timestamp = timestamp.getTimeInMillis();
	}

	/**
	 * @return the choosenByUser
	 */
	public boolean isChoosenByUser() {
		return choosenByUser;
	}

	/**
	 * @param choosenByUser
	 *            the choosenByUser to set
	 */
	public void setChoosenByUser(boolean choosenByUser) {
		this.choosenByUser = choosenByUser;
	}

	/**
	 * @return the service
	 */
	public App getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(App service) {
		this.service = service;
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Scrobble [userId=" + userId + ", songTitle=" + songTitle
				+ ", artistsNames=" + artistsNames + ", timestamp=" + timestamp
				+ ", choosenByUser=" + choosenByUser + ", service=" + service
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((artistsNames == null) ? 0 : artistsNames.hashCode());
		result = prime * result + (choosenByUser ? 1231 : 1237);
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result
				+ ((songTitle == null) ? 0 : songTitle.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		if (choosenByUser != other.choosenByUser)
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (songTitle == null) {
			if (other.songTitle != null)
				return false;
		} else if (!songTitle.equals(other.songTitle))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
