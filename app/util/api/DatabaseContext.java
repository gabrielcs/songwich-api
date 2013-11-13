package util.api;

import java.net.UnknownHostException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class DatabaseContext {
	// it currently only supports 1 Datastore
	private static Datastore datastore;

	private static void init() {
		// @see http://nesbot.com/2011/11/28/play-2-morphia-logging-error
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}

	/*
	 * Creates a datastore at localhost
	 */
	public static Datastore createDatastore(String dbName) {
		init();
		try {
			datastore = new Morphia()
					.createDatastore(new MongoClient(), dbName);
			MyLogger.info("Connected to database ");
			return getDatastore();
		} catch (UnknownHostException e) {
			MyLogger.error(String.format("Couldn't connect to database %s: %s",
					dbName, e.getMessage()));
			throw new RuntimeException(e);
		}
	}

	/*
	 * Creates a datastore at a given uri
	 */
	public static Datastore createDatastore(String uri, String dbName) {
		init();
		try {
			// connect
			MongoClientURI mongoClientURI = new MongoClientURI(uri);
			MongoClient mongoClient = new MongoClient(mongoClientURI);
			datastore = new Morphia().createDatastore(mongoClient, dbName);

			MyLogger.info("Connected to database");
			return getDatastore();
		} catch (Throwable t) {
			// TODO: figure out why this is not being logged
			MyLogger.error(String.format("Couldn't connect to database",
					t.getMessage()));
			throw new RuntimeException(t);
		}
	}

	public static boolean dropDatabase() {
		if (datastore != null) {
			datastore.getDB().dropDatabase();
			MyLogger.debug("Dropped database");
			return true;
		} else {
			return false;
		}
	}

	public static Datastore getDatastore() {
		return datastore;
	}
}
