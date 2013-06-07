package api;

public class ScrobbleProxy {
	public String user_id;
	public String track_title;
	public String artist_name;
	public String service;

	public ScrobbleProxy(String user_id, String track_title,
			String artist_name, String service) {
		
		this.user_id = user_id;
		this.track_title = track_title;
		this.artist_name = artist_name;
		this.service = service;
	}
}
