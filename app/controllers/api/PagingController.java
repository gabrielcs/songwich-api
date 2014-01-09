package controllers.api;

import play.api.Play;
import play.libs.F.Option;
import util.api.SongwichAPIException;
import views.api.APIStatus_V0_4;

public class PagingController {

	public static final int GET_SCROBBLES_DEFAULT_RESULTS = (Integer) Play
			.current().configuration().getInt("get.scrobbles.default").get();

	public static final int GET_STARRED_SONGS_DEFAULT_RESULTS = (Integer) Play
			.current().configuration().getInt("get.starred.songs.default")
			.get();

	public static void checkPagingParams(Option<String> since,
			Option<String> sinceInclusive, Option<String> until,
			Option<String> untilInclusive) throws SongwichAPIException {

		int definedParams = 0;

		if (since.isDefined()) {
			definedParams++;
		}
		if (sinceInclusive.isDefined()) {
			definedParams++;
		}
		if (until.isDefined()) {
			definedParams++;
		}
		if (untilInclusive.isDefined()) {
			definedParams++;
		}

		if (definedParams > 1) {
			throw new SongwichAPIException(
					"Cannot define more than 1 of 'since', 'until', 'sinceInclusive' and 'untilInclusive'",
					APIStatus_V0_4.INVALID_PARAMETER);
		}
	}
}
