package controllers.api.auth;

import play.mvc.SimpleResult;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import controllers.api.annotation.Timestamped;

public class TimestampController extends Action<Timestamped> {

	public final static String TIMESTAMP = "timestamp";

	@Override
	public F.Promise<SimpleResult> call(Http.Context context) throws Throwable {
		// public Result call(Http.Context context) throws Throwable {
		context.args.put(TIMESTAMP, System.currentTimeMillis());
		return delegate.call(context);
	}
}
