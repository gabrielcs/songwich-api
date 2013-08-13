package usecases.api.util;

import java.net.UnknownHostException;
import java.util.UUID;

import models.api.User;

import org.bson.types.ObjectId;

import play.Logger;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import database.api.UserDAO;
import database.api.UserDAOMongo;

public class DatabaseContext {
	// it currently only supports 1 Datastore
	private static Datastore datastore;

	/*
	 * Creates a datastore at localhost
	 */
	public static Datastore createDatastore(String dbName) {
		try {
			datastore = new Morphia()
					.createDatastore(new MongoClient(), dbName);
			Logger.info("Connected to database " + dbName);
			return getDatastore();
		} catch (UnknownHostException e) {
			Logger.error(String.format("Couldn't connect to database %s: %s",
					dbName, e.getMessage()));
			throw new RuntimeException(e);
		}
	}

	/*
	 * Creates a datastore at a given uri
	 */
	public static Datastore createDatastore(String uri, String dbName) {
		try {
			// connect
			MongoClientURI mongoClientURI = new MongoClientURI(uri);
			MongoClient mongoClient = new MongoClient(mongoClientURI);
			datastore = new Morphia().createDatastore(mongoClient, dbName);

			Logger.info("Connected to database " + uri);
			return getDatastore();
		} catch (UnknownHostException e) {
			Logger.error(String.format(
					"Couldn't connect to database '%s' : %s", uri,
					e.getMessage()));
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

	public static String createUserAuthToken() {
		String userAuthToken = UUID.randomUUID().toString();
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
