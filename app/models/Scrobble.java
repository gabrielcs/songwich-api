package models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.utils.IndexDirection;

@Entity
public class Scrobble { 

	// http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private static final long serialVersionUID = -6808566713582972768L;
	
	@Id
	public ObjectId id;
	
	@Indexed
	@Reference
    private User user;

	private String songTitle;
	
	private List<String> artistsNames;
	
	@Indexed(IndexDirection.DESC)
    private Date date;
	
	@Indexed
	private boolean choosenByUser;
	
	@Reference
    private MusicService service;
}
