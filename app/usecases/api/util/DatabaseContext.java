package usecases.api.util;

import java.net.UnknownHostException;
import java.util.UUID;

import models.User;

import org.bson.types.ObjectId;

import play.Logger;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

import daos.api.UserDAO;
import daos.api.UserDAOMongo;

public class DatabaseContext {
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
	
	public static UUID createUserAuthToken() {
		UUID userAuthToken = UUID.randomUUID();
		// assert that the random UUID is unique (might be expensive)
		UserDAO<ObjectId> userDAO = new UserDAOMongo();
		User user = userDAO.findByUserAuthToken(userAuthToken);
		if (user == null) {
			return userAuthToken;
		} else {
			return createUserAuthToken();
		}
	}
}
