package models;

import java.util.UUID;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;

@Embedded
public class MusicServiceUser {
	@Reference
	private MusicService streamingService;
	
	private String emailAddress;
	
	@Indexed
	private UUID userAuthToken;
}
