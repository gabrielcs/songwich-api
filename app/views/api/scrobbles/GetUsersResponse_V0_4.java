package views.api.scrobbles;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

public class GetUsersResponse_V0_4 extends APIResponse_V0_4 {
	@JsonProperty("user")
	private List<UserDTO_V0_4> userDTO;

	public GetUsersResponse_V0_4(APIStatus status, String message,
			List<UserDTO_V0_4> userDTO) {
		super(status, message);
		this.userDTO = userDTO;
	}

	/**
	 * @return the userDTO
	 */
	public List<UserDTO_V0_4> getUserDTO() {
		return userDTO;
	}

	/**
	 * @param userDTO
	 *            the userDTO to set
	 */
	public void setUserDTO(List<UserDTO_V0_4> userDTO) {
		this.userDTO = userDTO;
	}
}
