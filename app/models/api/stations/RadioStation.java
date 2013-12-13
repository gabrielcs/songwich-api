package models.api.stations;

import models.api.MongoEntity;
import models.api.MongoModelImpl;
import models.api.scrobbles.User;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PostLoad;

@Entity
public class RadioStation extends MongoModelImpl implements MongoEntity {
	@Id
	private ObjectId id;

	private String name;

	// all newly created stations are inactive
	private Boolean active = false;

	private String imageUrl;
	
	private String description;

	@Embedded
	private ScrobblerBridge scrobbler;

	@Embedded
	private Track nowPlaying;

	@Embedded
	private Track lookAhead;
	
	private Boolean deactivated;

	protected RadioStation() {
		super();
	}

	public RadioStation(String name, ScrobblerBridge scrobbler) {
		this.name = name;
		this.scrobbler = scrobbler;
	}

	public RadioStation(String name, ScrobblerBridge scrobbler, String imageUrl) {
		this.name = name;
		this.scrobbler = scrobbler;
		this.imageUrl = imageUrl;
	}
	
	public RadioStation(String name, ScrobblerBridge scrobbler, String imageUrl, String description) {
		this.name = name;
		this.scrobbler = scrobbler;
		this.imageUrl = imageUrl;
		this.description = description;
	}

	public RadioStation(String name, Group group) {
		this.name = name;
		this.scrobbler = new ScrobblerBridge(group);
	}

	public RadioStation(String name, Group group, String imageUrl) {
		this.name = name;
		this.scrobbler = new ScrobblerBridge(group);
		this.imageUrl = imageUrl;
	}
	
	public RadioStation(String name, Group group, String imageUrl, String description) {
		this.name = name;
		this.scrobbler = new ScrobblerBridge(group);
		this.imageUrl = imageUrl;
		this.description = description;
	}

	public RadioStation(String name, User user) {
		this.name = name;
		this.scrobbler = new ScrobblerBridge(user);
	}

	public RadioStation(String name, User user, String imageUrl) {
		this.name = name;
		this.scrobbler = new ScrobblerBridge(user);
		this.imageUrl = imageUrl;
	}
	
	public RadioStation(String name, User user, String imageUrl, String description) {
		this.name = name;
		this.scrobbler = new ScrobblerBridge(user);
		this.imageUrl = imageUrl;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	// TODO: remove this after updating old stations
	@PostLoad
	private void makeOldStationsActive() {
		if (active == null) {
			this.active = true;
		}
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		fireModelUpdated();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireModelUpdated();
	}

	public ScrobblerBridge getScrobbler() {
		return scrobbler;
	}

	public void setScrobbler(ScrobblerBridge scrobbler) {
		this.scrobbler = scrobbler;
		fireModelUpdated();
	}

	public Track getNowPlaying() {
		return nowPlaying;
	}

	public void setNowPlaying(Track nowPlaying) {
		this.nowPlaying = nowPlaying;
		fireModelUpdated();
	}

	public Track getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(Track lookAhead) {
		this.lookAhead = lookAhead;
		fireModelUpdated();
	}

	@Override
	public ObjectId getId() {
		return id;
	}
	
	/*
	public void setId(ObjectId id) {
		this.id = id;
	}
	*/
	
	public Boolean isDeactivated() {
		return deactivated == null ? false : deactivated;
	}

	public void setDeactivated(Boolean deactivated) {
		this.deactivated = deactivated;
	}

	@Override
	public String toString() {
		return "RadioStation [id=" + id + ", name=" + name + ", active="
				+ active + ", imageUrl=" + imageUrl + ", description="
				+ description + ", scrobbler=" + scrobbler + ", nowPlaying="
				+ nowPlaying + ", lookAhead=" + lookAhead + ", deactivated="
				+ deactivated + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((scrobbler == null) ? 0 : scrobbler.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RadioStation other = (RadioStation) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (scrobbler == null) {
			if (other.scrobbler != null)
				return false;
		} else if (!scrobbler.equals(other.scrobbler)) {
			return false;
		}
		return true;
	}

}
