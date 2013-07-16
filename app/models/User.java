package models;

import java.util.Set;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class User {
	// http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private static final long serialVersionUID = 5854422586239724109L;
	
	@Id
	private ObjectId id;
	
	private String emailAddress;
	
	private String name;
	
	@Embedded
	private Set<MusicServiceUser> musicServiceUsers;
}
