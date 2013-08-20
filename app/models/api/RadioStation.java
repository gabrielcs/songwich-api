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
	private Song nowPlaying;

	@Embedded
	private Song lookAhead;

	@Embedded
	private List<StationHistoryEntry> history = new ArrayList<StationHistoryEntry>();

	protected RadioStation() {
		super();
	}

	public RadioStation(String name, T scrobbler, StationStrategy strategy) {
		super();
		setName(name);
		setScrobbler(scrobbler);
	}

	public void addFeedback() {

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

	
}
