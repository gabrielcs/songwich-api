package views.api.stations;

import java.util.List;

import models.api.scrobbles.Scrobble;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("song")
public class SongDTO_V0_4 extends DataTransferObject<Scrobble> {
	private String trackTitle;
	
	private String albumTitle;

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}

	private List<String> artistsNames;

	public SongDTO_V0_4() {
	}

	@Override
	public void addValidation() {
		// nothing to validate
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}

	public List<String> getArtistsNames() {
		return artistsNames;
	}

	public void setArtistsNames(List<String> artistsNames) {
		this.artistsNames = artistsNames;
	}
	
	// doesn't take into account 'albumTitle'
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((artistsNames == null) ? 0 : artistsNames.hashCode());
		result = prime * result
				+ ((trackTitle == null) ? 0 : trackTitle.hashCode());
		return result;
	}

	// doesn't take into account 'albumTitle'
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SongDTO_V0_4 other = (SongDTO_V0_4) obj;
		if (artistsNames == null) {
			if (other.artistsNames != null)
				return false;
		} else if (!artistsNames.equals(other.artistsNames))
			return false;
		if (trackTitle == null) {
			if (other.trackTitle != null)
				return false;
		} else if (!trackTitle.equals(other.trackTitle))
			return false;
		return true;
	}
}
