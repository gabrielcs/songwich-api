package models.api;

import java.util.ArrayList;
import java.util.List;

import models.api.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class RadioStation<T extends Scrobbler> extends Model {
	@Id
	private ObjectId id;

	private String name;

	@Embedded
	private T scrobbler;

	@Embedded
	private StationStrategy strategy;

	@Embedded
	private Song nowPlaying;

	@Embedded
	private Song lookAhead;

	@Embedded
	private List<StationHistoryEntry> history = new ArrayList<StationHistoryEntry>();

	protected RadioStation() {
		super();
	}

	public RadioStation(String name, T scrobbler) {
		super();
		setName(name);
		setScrobbler(scrobbler);
	}

	public void next() {
		if (nowPlaying == null) {
			// brand new station
			lookAhead = strategy.next(scrobbler.getActiveScrobblersUserIds(),
					history, lookAhead);
		}
		Song next = strategy.next(scrobbler.getActiveScrobblersUserIds(),
				history, lookAhead);
		nowPlaying = lookAhead;
		lookAhead = next;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getScrobbler() {
		return scrobbler;
	}

	public void setScrobbler(T scrobbler) {
		this.scrobbler = scrobbler;
	}

	public Song getNowPlaying() {
		return nowPlaying;
	}

	public void setNowPlaying(Song nowPlaying) {
		this.nowPlaying = nowPlaying;
	}

	public Song getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(Song lookAhead) {
		this.lookAhead = lookAhead;
	}

	public List<StationHistoryEntry> getHistory() {
		return history;
	}

	public void setHistory(List<StationHistoryEntry> history) {
		this.history = history;
	}

	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "RadioStation [id=" + id + ", name=" + name + ", scrobbler="
				+ scrobbler + ", strategy=" + strategy + ", nowPlaying="
				+ nowPlaying + ", lookAhead=" + lookAhead + ", history="
				+ history + ", super.toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((history == null) ? 0 : history.hashCode());
		result = prime * result
				+ ((lookAhead == null) ? 0 : lookAhead.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((nowPlaying == null) ? 0 : nowPlaying.hashCode());
		result = prime * result
				+ ((scrobbler == null) ? 0 : scrobbler.hashCode());
		result = prime * result
				+ ((strategy == null) ? 0 : strategy.hashCode());
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
		if (history == null) {
			if (other.history != null)
				return false;
		} else if (!history.equals(other.history))
			return false;
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
		if (strategy == null) {
			if (other.strategy != null)
				return false;
		} else if (!strategy.equals(other.strategy))
			return false;
		return true;
	}

}
