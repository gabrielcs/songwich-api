package views.api.scrobbles;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostUsersResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("user")
	private UserDTO_V0_4 userDTO;

	public PostUsersResponse_V0_4(APIStatus status, String message,
			UserDTO_V0_4 userDTO) {
		super(status, message);
		this.userDTO = userDTO;
	}

	/**
	 * @return the userDTO
	 */
	public UserDTO_V0_4 getUserDTO() {
		return userDTO;
	}

	/**
	 * @param userDTO
	 *            the userDTO to set
	 */
	public void setUserDTO(UserDTO_V0_4 userDTO) {
		this.userDTO = userDTO;
	}
}
