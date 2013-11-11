package views.api.scrobbles;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class PutUsersResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("user")
	private UserUpdateDTO_V0_4 userUpdateDTO;

	public PutUsersResponse_V0_4(APIStatus status, String message,
			UserUpdateDTO_V0_4 userUpdateDTO) {
		super(status, message);
		this.userUpdateDTO = userUpdateDTO;
	}

	/**
	 * @return the userUpdateDTO
	 */
	public UserUpdateDTO_V0_4 getUserUpdateDTO() {
		return userUpdateDTO;
	}

	/**
	 * @param userUpdateDTO
	 *            the userUpdateDTO to set
	 */
	public void setUserUpdateDTO(UserUpdateDTO_V0_4 userUpdateDTO) {
		this.userUpdateDTO = userUpdateDTO;
	}
}
