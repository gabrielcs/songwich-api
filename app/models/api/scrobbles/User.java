package models.api.scrobbles;

import java.util.HashSet;
import java.util.Set;

import models.api.MongoEntity;
import models.api.MongoModelImpl;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class User extends MongoModelImpl implements MongoEntity {
	@Id
	private ObjectId id;

	@Indexed
	private String emailAddress;

	private String name;

	private String imageUrl;

	private String shortBio;

	@Embedded
	private Set<AppUser> appUsers = new HashSet<AppUser>();

	private Boolean deactivated;

	protected User() {
		super();
	}

	public User(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public User(String emailAddress, String name) {
		this.emailAddress = emailAddress;
		this.name = name;
	}

	public User(String emailAddress, String name, String imageUrl) {
		this.emailAddress = emailAddress;
		this.name = name;
		this.imageUrl = imageUrl;
	}

	public User(String emailAddress, String name, String imageUrl,
			String shortBio) {
		this.emailAddress = emailAddress;
		this.name = name;
		this.imageUrl = imageUrl;
		this.shortBio = shortBio;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getShortBio() {
		return shortBio;
	}

	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
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
		fireModelUpdated();
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
		fireModelUpdated();
	}

	/**
	 * @return the appUsers
	 */
	public Set<AppUser> getAppUsers() {
		return appUsers;
	}

	/**
	 * 
	 * @param appUser
	 * @param modifiedBy
	 * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
	 */
	public boolean addAppUser(AppUser appUser) {
		boolean result = appUsers.add(appUser);
		fireModelUpdated();
		return result;
	}

	/**
	 * @return the id
	 */
	@Override
	public ObjectId getId() {
		return id;
	}

	/*
	 * public void setId(ObjectId id) { this.id = id; }
	 */

	public Boolean isDeactivated() {
		return deactivated == null ? false : deactivated;
	}

	public void setDeactivated(Boolean deactivated) {
		this.deactivated = deactivated;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", emailAddress=" + emailAddress + ", name="
				+ name + ", imageUrl=" + imageUrl + ", shortBio=" + shortBio
				+ ", appUsers=" + appUsers + ", deactivated=" + deactivated
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
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
		User other = (User) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		return true;
	}
}
