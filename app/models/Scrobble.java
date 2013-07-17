package models;

import java.util.ArrayList;
import java.util.Date;
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
	@Reference
    private User user;

	private String songTitle;
	
	private List<String> artistsNames;
	
	@Indexed(IndexDirection.DESC)
    private Date date;
	
	@Indexed
	private boolean choosenByUser;
	
	@Reference
    private MusicService service;
	
	@SuppressWarnings("unused")
	private Scrobble() {
		super();
	}
	
	public Scrobble(User user, String songTitle, String artistName,
			Date date, boolean choosenByUser, MusicService service) {
		super();
		this.user = user;
		this.songTitle = songTitle;
		artistsNames = new ArrayList<String>();
		artistsNames.add(artistName);
		this.date = date;
		this.choosenByUser = choosenByUser;
		this.service = service;
	}

	public Scrobble(User user, String songTitle, List<String> artistsNames,
			Date date, boolean choosenByUser, MusicService service) {
		super();
		this.user = user;
		this.songTitle = songTitle;
		this.artistsNames = artistsNames;
		this.date = date;
		this.choosenByUser = choosenByUser;
		this.service = service;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the songTitle
	 */
	public String getSongTitle() {
		return songTitle;
	}

	/**
	 * @param songTitle the songTitle to set
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
	 * @param artistsNames the artistsNames to set
	 */
	public void setArtistsNames(List<String> artistsNames) {
		this.artistsNames = artistsNames;
	}
	
	public void addArtistsName(String artistsName) {
		artistsNames.add(artistsName);
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the choosenByUser
	 */
	public boolean isChoosenByUser() {
		return choosenByUser;
	}

	/**
	 * @param choosenByUser the choosenByUser to set
	 */
	public void setChoosenByUser(boolean choosenByUser) {
		this.choosenByUser = choosenByUser;
	}

	/**
	 * @return the service
	 */
	public MusicService getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(MusicService service) {
		this.service = service;
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((artistsNames == null) ? 0 : artistsNames.hashCode());
		result = prime * result + (choosenByUser ? 1231 : 1237);
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result
				+ ((songTitle == null) ? 0 : songTitle.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
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
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
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
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
