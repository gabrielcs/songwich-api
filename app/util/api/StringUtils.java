package util.api;

import java.util.Collection;
import java.util.List;

public class StringUtils {
	
	public static boolean containsIgnoreCaseCollection(
			Collection<List<String>> stringCollectionOfCollections,
			Collection<String> stringCollection) {

		for (Collection<String> anotherStringCollection : stringCollectionOfCollections) {
			if (equalsIgnoreCase(stringCollection, anotherStringCollection)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsIgnoreCaseCollection(
			Collection<List<String>> stringCollectionOfCollections,
			Collection<List<String>> otherStringCollectionOfCollections) {

		if (stringCollectionOfCollections == otherStringCollectionOfCollections) {
			return true;
		}
		if (otherStringCollectionOfCollections.size() != stringCollectionOfCollections
				.size()) {
			return false;
		}
		for (Collection<String> stringCollection : otherStringCollectionOfCollections) {
			if (!containsIgnoreCaseCollection(stringCollectionOfCollections,
					stringCollection)) {
				return false;
			}
		}
		return true;
	}

	public static boolean containsIgnoreCase(
			Collection<String> stringCollection, String aString) {
		for (String anotherString : stringCollection) {
			if (aString.equalsIgnoreCase(anotherString)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean equalsIgnoreCase(Collection<String> stringCollection,
			Collection<String> otherStringCollection) {

		if (stringCollection == otherStringCollection) {
			return true;
		}
		if (otherStringCollection.size() != stringCollection.size()) {
			return false;
		}
		for (String aString : otherStringCollection) {
			if (!containsIgnoreCase(stringCollection, aString)) {
				return false;
			}
		}
		return true;
	}
}
