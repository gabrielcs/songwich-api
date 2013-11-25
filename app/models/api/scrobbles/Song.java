package models.api.scrobbles;

import java.util.ArrayList;
import java.util.List;

import util.api.StringUtils;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class Song {

	private String songTitle;

	private List<String> artistsNames = new ArrayList<String>();

	private String albumTitle;

	protected Song() {
		super();
	}

	public Song(String songTitle, List<String> artistsNames) {
		super();
		setSongTitle(songTitle);
		setArtistsNames(artistsNames);
	}

	public Song(String songTitle, String albumTitle, List<String> artistsNames) {
		super();
		setSongTitle(songTitle);
		setAlbumTitle(albumTitle);
		setArtistsNames(artistsNames);
	}

	public Song(String songTitle, String artistName) {
		super();
		setSongTitle(songTitle);
		addArtistName(artistName);
	}

	public Song(String songTitle, String albumTitle, String artistName) {
		super();
		setSongTitle(songTitle);
		setAlbumTitle(albumTitle);
		addArtistName(artistName);
	}

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
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
				+ artistsNames + ", albumTitle=" + albumTitle + "]";
	}

	// doesn't take into account 'albumTitle'
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

	// doesn't take into account 'albumTitle'
	// uses equalsIgnoreCase() instead of equals() for Strings
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
		} else if (!StringUtils.equalsIgnoreCase(artistsNames, other.artistsNames))
			return false;
		if (songTitle == null) {
			if (other.songTitle != null)
				return false;
		} else if (!songTitle.equalsIgnoreCase(other.songTitle))
			return false;
		return true;
	}

}
