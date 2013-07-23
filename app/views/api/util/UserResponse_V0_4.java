package views.api.util;

import views.api.UserDTO_V0_4;

public class UserResponse_V0_4 extends APIResponse_V0_4 {

	private UserDTO_V0_4 userDTO;

	public UserResponse_V0_4(APIStatus status, String message,
			UserDTO_V0_4 userDTO) {
		super(status, message);
		this.userDTO = userDTO;
	}

	/**
	 * @return the userDTO
	 */
	public UserDTO_V0_4 getScrobble() {
		return userDTO;
	}

	/**
	 * @param userDTO
	 *            the userDTO to set
	 */
	public void setScrobble(UserDTO_V0_4 scrobble) {
		this.userDTO = scrobble;
	}
}
