package models.api;

import java.util.HashSet;
import java.util.Set;

import models.api.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class User extends Model {
	@Id
	private ObjectId id;

	@Indexed
	private String emailAddress;

	private String name;

	@Embedded
	private Set<AppUser> appUsers;
	
	protected User() {
		super();
		appUsers = new HashSet<AppUser>();
	}
	
	public User(String emailAddress, String createdBy) {
		super(createdBy);
		setEmailAddress(emailAddress);
		appUsers = new HashSet<AppUser>();
	}

	public User(String emailAddress, String name, String createdBy) {
		super(createdBy);
		setEmailAddress(emailAddress);
		setName(name);
		appUsers = new HashSet<AppUser>();
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
	 * @return the appUsers
	 */
	public Set<AppUser> getAppUsers() {
		return appUsers;
	}

	public boolean addAppUser(AppUser appUser) {
		return appUsers.add(appUser);
	}
	
	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", emailAddress=" + emailAddress + ", name="
				+ name + ", appUsers=" + appUsers + ", super.toString()="
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
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime
				* result
				+ ((appUsers == null) ? 0 : appUsers
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
		if (appUsers == null) {
			if (other.appUsers != null)
				return false;
		} else if (!appUsers.equals(other.appUsers))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
