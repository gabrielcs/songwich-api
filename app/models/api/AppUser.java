package models.api;

import models.api.util.Model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.Reference;

@Embedded
public class AppUser extends Model {
	@Reference
	private App app;

	@Indexed
	private String userEmailAddress;

	@Embedded
	private AuthToken statefulUserAuthToken;

	@Deprecated
	@NotSaved
	private String userAuthToken;

	protected AppUser() {
		super();
	}

	public AppUser(App app, String emailAddress,
			AuthToken userAuthToken, String createdBy) {
		super(createdBy);
		this.app = app;
		this.userEmailAddress = emailAddress;
		this.statefulUserAuthToken = userAuthToken;
	}

	/**
	 * @return the app
	 */
	public App getApp() {
		return app;
	}

	public void setApp(App app, String modifiedBy) {
		this.app = app;
		setLastModifiedBy(modifiedBy);
	}

	/**
	 * @return the userEmailAddress
	 */
	public String getEmailAddress() {
		return userEmailAddress;
	}

	public void setEmailAddress(String emailAddress, String modifiedBy) {
		this.userEmailAddress = emailAddress;
		setLastModifiedBy(modifiedBy);
	}

	public AuthToken getUserAuthToken() {
		return statefulUserAuthToken;
	}

	public void setUserAuthToken(AuthToken userAuthToken, String modifiedBy) {
		this.statefulUserAuthToken = userAuthToken;
		setLastModifiedBy(modifiedBy);
	}

	/*
	 * Handle deprecated stateless auth token
	 */
	@PostLoad
	protected void handleDeprecatedAuthToken() {
		if (statefulUserAuthToken == null && userAuthToken != null) {
			statefulUserAuthToken = new AuthToken(userAuthToken.toString());
		}
	}

	@Override
	public String toString() {
		return "AppUser [app=" + app + ", userEmailAddress=" + userEmailAddress
				+ ", userAuthToken=" + statefulUserAuthToken + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((app == null) ? 0 : app.hashCode());
		result = prime
				* result
				+ ((statefulUserAuthToken == null) ? 0 : statefulUserAuthToken
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
		if (statefulUserAuthToken == null) {
			if (other.statefulUserAuthToken != null)
				return false;
		} else if (!statefulUserAuthToken.equals(other.statefulUserAuthToken))
			return false;
		if (userEmailAddress == null) {
			if (other.userEmailAddress != null)
				return false;
		} else if (!userEmailAddress.equals(other.userEmailAddress))
			return false;
		return true;
	}
}
