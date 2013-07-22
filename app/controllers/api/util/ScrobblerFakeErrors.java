package controllers.api.util;

import play.mvc.Controller;
import play.mvc.Result;

public class ScrobblerFakeErrors extends Controller {

	public static Result scrobbleErrorGet() {
		throw new RuntimeException("Testing the error page");
	}

	public static Result scrobbleErrorPost() {
		throw new RuntimeException("Testing the error page");
	}

}
