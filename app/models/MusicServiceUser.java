package models;

import java.util.UUID;

import models.util.Model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;

@Embedded
public class MusicServiceUser extends Model {
	@Reference
	private MusicService streamingService;
	
	private String emailAddress;
	
	@Indexed
	private UUID userAuthToken;
	
	protected MusicServiceUser() {
		super();
	}
	
	public MusicServiceUser(MusicService streamingService, String emailAddress) {
		super();
		this.streamingService = streamingService;
		this.emailAddress = emailAddress;
	}

	public MusicServiceUser(MusicService streamingService, String emailAddress,
			UUID userAuthToken) {
		super();
		this.streamingService = streamingService;
		this.emailAddress = emailAddress;
		this.userAuthToken = userAuthToken;
	}

	/**
	 * @return the streamingService
	 */
	public MusicService getStreamingService() {
		return streamingService;
	}

	/**
	 * @param streamingService the streamingService to set
	 */
	public void setStreamingService(MusicService streamingService) {
		this.streamingService = streamingService;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the userAuthToken
	 */
	public UUID getUserAuthToken() {
		return userAuthToken;
	}

	/**
	 * @param userAuthToken the userAuthToken to set
	 */
	public void setUserAuthToken(UUID userAuthToken) {
		this.userAuthToken = userAuthToken;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MusicServiceUser [streamingService=" + streamingService
				+ ", emailAddress=" + emailAddress + ", userAuthToken="
				+ userAuthToken + "]";
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
				+ ((streamingService == null) ? 0 : streamingService.hashCode());
		result = prime * result
				+ ((userAuthToken == null) ? 0 : userAuthToken.hashCode());
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
		MusicServiceUser other = (MusicServiceUser) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (streamingService == null) {
			if (other.streamingService != null)
				return false;
		} else if (!streamingService.equals(other.streamingService))
			return false;
		if (userAuthToken == null) {
			if (other.userAuthToken != null)
				return false;
		} else if (!userAuthToken.equals(other.userAuthToken))
			return false;
		return true;
	}
}
