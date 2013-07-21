package controllers.api.util;

import views.api.util.Status;

public class SongwichAPIException extends Exception {
	private static final long serialVersionUID = 4906235508758543083L;
	
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