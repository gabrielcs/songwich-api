package models;

import java.util.UUID;

import daos.api.util.MongoDatabaseId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class MusicService {
	// http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private static final long serialVersionUID = 2127059848446848577L;
	
	@Id
	private MongoDatabaseId id;

	private String name;
	
	@Indexed
	private UUID appAuthToken;
}
