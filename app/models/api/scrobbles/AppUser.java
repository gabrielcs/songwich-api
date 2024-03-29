package models.api.scrobbles;

import models.api.MongoModel;
import models.api.MongoModelImpl;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;

@Embedded
public class AppUser extends MongoModelImpl implements MongoModel {
	@Reference(lazy = true)
	private App app;

	@Indexed
	private String userEmailAddress;

	@Embedded
	private AuthToken userAuthToken;

	/*
	@Deprecated
	@NotSaved
	private String userAuthToken;
	*/

	protected AppUser() {
		super();
	}

	public AppUser(App app, String emailAddress, AuthToken userAuthToken) {
		this.app = app;
		this.userEmailAddress = emailAddress;
		this.userAuthToken = userAuthToken;
	}

	/**
	 * @return the app
	 */
	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
		fireModelUpdated();
	}

	/**
	 * @return the userEmailAddress
	 */
	public String getEmailAddress() {
		return userEmailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.userEmailAddress = emailAddress;
		fireModelUpdated();
	}

	public AuthToken getUserAuthToken() {
		return userAuthToken;
	}

	public void setUserAuthToken(AuthToken userAuthToken) {
		this.userAuthToken = userAuthToken;
		fireModelUpdated();
	}

	/*
	 * Handle deprecated stateless auth token
	 *
	@PostLoad
	protected void handleDeprecatedAuthToken() {
		if (statefulUserAuthToken == null && userAuthToken != null) {
			statefulUserAuthToken = new AuthToken(userAuthToken.toString());
		}
	}
	*/
	

	@Override
	public String toString() {
		return "AppUser [app=" + app + ", userEmailAddress=" + userEmailAddress
				+ ", userAuthToken=" + userAuthToken + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((app == null) ? 0 : app.hashCode());
		result = prime
				* result
				+ ((userAuthToken == null) ? 0 : userAuthToken
						.hashCode());
		result = prime
				* result
				+ ((userEmailAddress == null) ? 0 : userEmailAddress.hashCode());
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
		AppUser other = (AppUser) obj;
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
		if (userEmailAddress == null) {
			if (other.userEmailAddress != null)
				return false;
		} else if (!userEmailAddress.equals(other.userEmailAddress))
			return false;
		return true;
	}
}
