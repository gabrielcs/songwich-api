package models.api;

import java.util.UUID;

import models.api.util.Model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

@Embedded
public class AppDeveloper extends Model {
	@Indexed
	private String emailAddress;
	
	private String name;
	
	@Indexed
	private String devAuthToken;
	
	protected AppDeveloper() {
	}
	
	public AppDeveloper(String emailAddress, String name, UUID devAuthToken, String createdBy) {
		super(createdBy);
		setEmailAddress(emailAddress);
		setName(name);
		setDevAuthToken(devAuthToken);
	}
	
	public AppDeveloper(String emailAddress, String name, String devAuthToken, String createdBy) {
		super(createdBy);
		setEmailAddress(emailAddress);
		setName(name);
		setDevAuthToken(devAuthToken);
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDevAuthToken() {
		return devAuthToken;
	}

	public void setDevAuthToken(String devAuthToken) {
		this.devAuthToken = devAuthToken;
	}
	
	public void setDevAuthToken(UUID devAuthToken) {
		this.devAuthToken = devAuthToken.toString();
	}

	@Override
	public String toString() {
		return "AppDeveloper [emailAddress=" + emailAddress + ", name=" + name
				+ ", devAuthToken=" + devAuthToken + ", super.toString()="
				+ super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((devAuthToken == null) ? 0 : devAuthToken.hashCode());
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
		if (devAuthToken == null) {
			if (other.devAuthToken != null)
				return false;
		} else if (!devAuthToken.equals(other.devAuthToken))
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
