package controllers.api.util;

import views.api.util.Status;

public class SongwichAPIException extends Exception {
	private Status status;
	
	public SongwichAPIException(String message, Status status) {
		super(message);
		setStatus(status);
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}