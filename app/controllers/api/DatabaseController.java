package controllers.api;

import java.net.UnknownHostException;

import play.Logger;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

public class DatabaseController {
	// it currently only supports 1 Datastore
	private static Datastore datastore;

	public static Datastore createDatastore(String dbName) {
		try {
			datastore = new Morphia().createDatastore(new MongoClient(),
					dbName);
			Logger.info("Connected to database " + dbName);
			return getDatastore();
		} catch (UnknownHostException e) {
			Logger.error("Couldn't connect to the database: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public static boolean dropDatabase() {
		if (datastore != null) {
			datastore.getDB().dropDatabase();
			Logger.info("Dropped database");
			return true;
		} else {
			return false;
		}
	}

	public static Datastore getDatastore() {
		return datastore;
	}
}
