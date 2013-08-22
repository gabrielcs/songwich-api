package util.api;

import controllers.api.APIStatus;

public class SongwichAPIException extends Exception {
	private static final long serialVersionUID = 4906235508758543083L;
	
	private APIStatus status;
	
	public SongwichAPIException(String message, APIStatus status) {
		super(message);
		setStatus(status);
	}
	
	public APIStatus getStatus() {
		return status;
	}

	public void setStatus(APIStatus status) {
		this.status = status;
	}
}