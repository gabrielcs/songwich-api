package models.api.stations;

import models.api.MongoEntity;
import models.api.ModelImpl;
import models.api.scrobbles.Song;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class RadioStation<T extends Scrobbler> extends ModelImpl implements
		MongoEntity {
	@Id
	private ObjectId id;

	private String name;

	@Embedded
	private T scrobbler;

	@Embedded
	private Song nowPlaying;

	@Embedded
	private Song lookAhead;

	protected RadioStation() {
		super();
	}

	public RadioStation(String name, T scrobbler) {
		this.name = name;
		this.scrobbler = scrobbler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireModelUpdated();
	}

	public T getScrobbler() {
		return scrobbler;
	}

	public void setScrobbler(T scrobbler) {
		this.scrobbler = scrobbler;
		fireModelUpdated();
	}

	public Song getNowPlaying() {
		return nowPlaying;
	}

	public void setNowPlaying(Song nowPlaying) {
		this.nowPlaying = nowPlaying;
		fireModelUpdated();
	}

	public Song getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(Song lookAhead) {
		this.lookAhead = lookAhead;
		fireModelUpdated();
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "RadioStation [id=" + id + ", name=" + name + ", scrobbler="
				+ scrobbler + ", nowPlaying=" + nowPlaying + ", lookAhead="
				+ lookAhead + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((lookAhead == null) ? 0 : lookAhead.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((nowPlaying == null) ? 0 : nowPlaying.hashCode());
		result = prime * result
				+ ((scrobbler == null) ? 0 : scrobbler.hashCode());
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
		@SuppressWarnings("rawtypes")
		RadioStation other = (RadioStation) obj;
		if (lookAhead == null) {
			if (other.lookAhead != null)
				return false;
		} else if (!lookAhead.equals(other.lookAhead))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nowPlaying == null) {
			if (other.nowPlaying != null)
				return false;
		} else if (!nowPlaying.equals(other.nowPlaying))
			return false;
		if (scrobbler == null) {
			if (other.scrobbler != null)
				return false;
		} else if (!scrobbler.equals(other.scrobbler))
			return false;
		return true;
	}
}
