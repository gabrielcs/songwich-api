package controllers.api.util;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.UsersDTO_V0_4;



public class PostUsersResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("user")
	private UsersDTO_V0_4 userDTO;

	public PostUsersResponse_V0_4(APIStatus status, String message,
			UsersDTO_V0_4 userDTO) {
		super(status, message);
		this.userDTO = userDTO;
	}

	/**
	 * @return the userDTO
	 */
	public UsersDTO_V0_4 getUserDTO() {
		return userDTO;
	}

	/**
	 * @param userDTO
	 *            the userDTO to set
	 */
	public void setUserDTO(UsersDTO_V0_4 userDTO) {
		this.userDTO = userDTO;
	}
}
