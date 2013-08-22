package behavior.api.usecases;

public abstract class UseCase {
	
	private RequestContext context;
	
	public UseCase(RequestContext context) {
		setContext(context);
	}

	public RequestContext getContext() {
		return context;
	}

	public void setContext(RequestContext context) {
		this.context = context;
	}
}
