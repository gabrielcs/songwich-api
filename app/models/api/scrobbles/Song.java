package models.api.scrobbles;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class Song {

	private String songTitle;

	private List<String> artistsNames = new ArrayList<String>();

	protected Song() {
		super();
	}

	public Song(String songTitle, List<String> artistsNames) {
		super();
		setSongTitle(songTitle);
		setArtistsNames(artistsNames);
	}

	public Song(String songTitle, String artistName) {
		super();
		setSongTitle(songTitle);
		addArtistName(artistName);
	}

	public String getSongTitle() {
		return songTitle;
	}

	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}

	public List<String> getArtistsNames() {
		return artistsNames;
	}

	public void setArtistsNames(List<String> artistsNames) {
		this.artistsNames = artistsNames;
	}

	/**
	 * 
	 * @param artistName
	 * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
	 */
	public boolean addArtistName(String artistName) {
		return artistsNames.add(artistName);
	}

	@Override
	public String toString() {
		return "Song [songTitle=" + songTitle + ", artistsNames="
				+ artistsNames + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((artistsNames == null) ? 0 : artistsNames.hashCode());
		result = prime * result
				+ ((songTitle == null) ? 0 : songTitle.hashCode());
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
		Song other = (Song) obj;
		if (artistsNames == null) {
			if (other.artistsNames != null)
				return false;
		} else if (!artistsNames.equals(other.artistsNames))
			return false;
		if (songTitle == null) {
			if (other.songTitle != null)
				return false;
		} else if (!songTitle.equals(other.songTitle))
			return false;
		return true;
	}

}