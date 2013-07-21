package models;

import java.util.HashSet;
import java.util.Set;

import models.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class User extends Model {
	@Id
	private ObjectId id;

	private String emailAddress;

	private String name;

	@Embedded
	private Set<MusicServiceUser> musicServiceUsers;
	
	protected User() {
		super();
		this.musicServiceUsers = new HashSet<MusicServiceUser>();
	}

	public User(String emailAddress, String name) {
		this.emailAddress = emailAddress;
		this.name = name;
		this.musicServiceUsers = new HashSet<MusicServiceUser>();
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the musicServiceUsers
	 */
	public Set<MusicServiceUser> getMusicServiceUsers() {
		return musicServiceUsers;
	}

	public boolean addMusicServiceUser(MusicServiceUser musicServiceUser) {
		return musicServiceUsers.add(musicServiceUser);
	}
	
	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [emailAddress=" + emailAddress + ", name=" + name
				+ ", musicServiceUsers=" + musicServiceUsers + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime
				* result
				+ ((musicServiceUsers == null) ? 0 : musicServiceUsers
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		User other = (User) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (musicServiceUsers == null) {
			if (other.musicServiceUsers != null)
				return false;
		} else if (!musicServiceUsers.equals(other.musicServiceUsers))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
