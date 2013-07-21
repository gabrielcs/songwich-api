package models;

import java.util.UUID;

import models.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class MusicService extends Model {
	@Id
	private ObjectId id;

	private String name;
	
	@Indexed
	private UUID appAuthToken;
	
	protected MusicService() {
		super();
	}

	public MusicService(String name) {
		super();
		setName(name);
	}

	public MusicService(String name, UUID appAuthToken) {
		super();
		setName(name);
		setAppAuthToken(appAuthToken);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the appAuthToken
	 */
	public UUID getAppAuthToken() {
		return appAuthToken;
	}

	/**
	 * @param appAuthToken the appAuthToken to set
	 */
	public void setAppAuthToken(UUID appAuthToken) {
		this.appAuthToken = appAuthToken;
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
		return "MusicService [name=" + name + ", appAuthToken=" + appAuthToken
				+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appAuthToken == null) ? 0 : appAuthToken.hashCode());
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
		MusicService other = (MusicService) obj;
		if (appAuthToken == null) {
			if (other.appAuthToken != null)
				return false;
		} else if (!appAuthToken.equals(other.appAuthToken))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
