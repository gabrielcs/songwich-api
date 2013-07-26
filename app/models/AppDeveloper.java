package models;

import java.util.UUID;

import models.util.Model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

@Embedded
public class AppDeveloper extends Model {
	@Indexed
	private String emailAddress;
	
	private String firstName;
	
	private String lastName;
	
	@Indexed
	private String devAuthToken;
	
	protected AppDeveloper() {
	}
	
	public AppDeveloper(String emailAddress, UUID devAuthToken, String createdBy) {
		super(createdBy);
		setEmailAddress(emailAddress);
		setDevAuthToken(devAuthToken);
	}
	
	public AppDeveloper(String emailAddress, String devAuthToken, String createdBy) {
		super(createdBy);
		setEmailAddress(emailAddress);
		setDevAuthToken(devAuthToken);
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
		return "AppDeveloper [emailAddress=" + emailAddress + ", firstName="
				+ firstName + ", lastName=" + lastName + ", devAuthToken="
				+ devAuthToken + ", super.toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((devAuthToken == null) ? 0 : devAuthToken.hashCode());
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
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
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}
}
