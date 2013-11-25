package util.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest extends WithRequestContext {

	@Test
	public void containsIgnoreCaseTest() {
		Collection<String> stringList = new ArrayList<String>();
		stringList.add("Test String");

		assertTrue(StringUtils.containsIgnoreCase(stringList, "Test String"));
		assertTrue(StringUtils.containsIgnoreCase(stringList, "test string"));
		assertFalse(StringUtils.containsIgnoreCase(stringList,
				"Wrong Test String"));
	}

	@Test
	public void containsIgnoreCaseCollectionTest() {
		List<String> stringList = new ArrayList<String>();
		stringList.add("Test String");
		Collection<List<String>> stringListsCollection = new ArrayList<List<String>>();
		stringListsCollection.add(stringList);

		List<String> otherStringList = new ArrayList<String>();
		otherStringList.add("test string");

		List<String> wrongStringList = new ArrayList<String>();
		wrongStringList.add("wrong test string");

		assertTrue(StringUtils.containsIgnoreCaseCollection(
				stringListsCollection, otherStringList));
		assertFalse(StringUtils.containsIgnoreCaseCollection(
				stringListsCollection, wrongStringList));
	}
}
