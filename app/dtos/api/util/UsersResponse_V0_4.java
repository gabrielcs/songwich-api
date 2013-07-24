package dtos.api.util;

import org.codehaus.jackson.annotate.JsonProperty;

import dtos.api.UserDTO_V0_4;


public class UsersResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("user")
	private UserDTO_V0_4 userDTO;

	public UsersResponse_V0_4(APIStatus status, String message,
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
