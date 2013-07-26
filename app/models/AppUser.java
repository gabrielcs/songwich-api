package models;

import java.util.UUID;

import models.util.Model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;

@Embedded
public class AppUser extends Model {
	@Reference
	private App app;
	
	@Indexed
	private String userEmailAddress;
	
	@Indexed
	private String userAuthToken;
	
	protected AppUser() {
		super();
	}

	public AppUser(App streamingService, String emailAddress,
			UUID userAuthToken, String createdBy) {
		super(createdBy);
		setApp(streamingService);
		setEmailAddress(emailAddress);
		setUserAuthToken(userAuthToken);
	}
	
	public AppUser(App streamingService, String emailAddress,
			String userAuthToken, String createdBy) {
		super(createdBy);
		setApp(streamingService);
		setEmailAddress(emailAddress);
		setUserAuthToken(userAuthToken);
	}

	/**
	 * @return the app
	 */
	public App getApp() {
		return app;
	}

	/**
	 * @param app the app to set
	 */
	public void setApp(App app) {
		this.app = app;
	}

	/**
	 * @return the userEmailAddress
	 */
	public String getEmailAddress() {
		return userEmailAddress;
	}

	/**
	 * @param userEmailAddress the userEmailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.userEmailAddress = emailAddress;
	}

	/**
	 * @return the userAuthToken
	 */
	public String getUserAuthToken() {
		return userAuthToken;
	}

	/**
	 * @param userAuthToken the userAuthToken to set
	 */
	public void setUserAuthToken(String userAuthToken) {
		this.userAuthToken = userAuthToken;
	}
	
	public void setUserAuthToken(UUID userAuthToken) {
		this.userAuthToken = userAuthToken.toString();
	}
	
	@Override
	public String toString() {
		return "AppUser [app=" + app + ", userEmailAddress=" + userEmailAddress
				+ ", userAuthToken=" + userAuthToken + ", super.toString()="
				+ super.toString() + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userEmailAddress == null) ? 0 : userEmailAddress.hashCode());
		result = prime
				* result
				+ ((app == null) ? 0 : app.hashCode());
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
		AppUser other = (AppUser) obj;
		if (userEmailAddress == null) {
			if (other.userEmailAddress != null)
				return false;
		} else if (!userEmailAddress.equals(other.userEmailAddress))
			return false;
		if (app == null) {
			if (other.app != null)
				return false;
		} else if (!app.equals(other.app))
			return false;
		if (userAuthToken == null) {
			if (other.userAuthToken != null)
				return false;
		} else if (!userAuthToken.equals(other.userAuthToken))
			return false;
		return true;
	}
}
