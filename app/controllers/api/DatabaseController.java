package controllers.api;

import com.google.code.morphia.Datastore;

public class DatabaseController {
	
	private static Datastore datastore;
	
	public static Datastore getDatastore() {
		return datastore;
	}

	public static void setDatastore(Datastore ds) {
		datastore = ds;
	}
}