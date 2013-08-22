package models.api.scrobbles;

import models.api.Model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.PostLoad;

@Embedded
public class AppDeveloper extends Model {
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
	
	public AppDeveloper(String emailAddress, String name, AuthToken devAuthToken, String createdBy) {
		super(createdBy);
		this.emailAddress = emailAddress;
		this.name = name;
		this.statefulDevAuthToken = devAuthToken;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress, String modifiedBy) {
		this.emailAddress = emailAddress;
		setLastModifiedBy(modifiedBy);
	}

	public String getName() {
		return name;
	}

	public void setName(String name, String modifiedBy) {
		this.name = name;
		setLastModifiedBy(modifiedBy);
	}

	public AuthToken getDevAuthToken() {
		return statefulDevAuthToken;
	}

	public void setDevAuthToken(AuthToken devAuthToken, String modifiedBy) {
		this.statefulDevAuthToken = devAuthToken;
		setLastModifiedBy(modifiedBy);
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
				+ ", devAuthToken=" + statefulDevAuthToken + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((statefulDevAuthToken == null) ? 0 : statefulDevAuthToken.hashCode());
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
