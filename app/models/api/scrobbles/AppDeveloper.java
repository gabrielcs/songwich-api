package models.api.scrobbles;

import models.api.MongoModel;
import models.api.MongoModelImpl;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.PostLoad;

@Embedded
public class AppDeveloper extends MongoModelImpl implements MongoModel {
	@Indexed
	private String emailAddress;

	private String name;

	@Embedded
	private AuthToken statefulDevAuthToken;

	@Deprecated
	@NotSaved
	private String devAuthToken;

	protected AppDeveloper() {
	}

	public AppDeveloper(String emailAddress, String name, AuthToken devAuthToken) {
		this.emailAddress = emailAddress;
		this.name = name;
		this.statefulDevAuthToken = devAuthToken;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		fireModelUpdated();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireModelUpdated();
	}

	public AuthToken getDevAuthToken() {
		return statefulDevAuthToken;
	}

	public void setDevAuthToken(AuthToken devAuthToken) {
		this.statefulDevAuthToken = devAuthToken;
		fireModelUpdated();
	}

	/*
	 * Handle deprecated stateless auth token
	 */
	@PostLoad
	protected void handleDeprecatedAuthToken() {
		if (statefulDevAuthToken == null && devAuthToken != null) {
			this.statefulDevAuthToken = new AuthToken(devAuthToken.toString());
		}
	}

	@Override
	public String toString() {
		return "AppDeveloper [emailAddress=" + emailAddress + ", name=" + name
				+ ", statefulDevAuthToken=" + statefulDevAuthToken + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((statefulDevAuthToken == null) ? 0 : statefulDevAuthToken
						.hashCode());
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AppDeveloper other = (AppDeveloper) obj;
		if (statefulDevAuthToken == null) {
			if (other.statefulDevAuthToken != null)
				return false;
		} else if (!statefulDevAuthToken.equals(other.statefulDevAuthToken))
			return false;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
