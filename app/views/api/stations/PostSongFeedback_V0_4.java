package views.api.stations;

import views.api.APIResponse_V0_4;
import views.api.APIStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostSongFeedback_V0_4 extends APIResponse_V0_4 {

	@JsonProperty("songFeedback")
	private SongFeedbackDTO_V0_4 songFeedbackDTO;

	public PostSongFeedback_V0_4(APIStatus status, String message,
			SongFeedbackDTO_V0_4 songFeedbackDTO) {
		super(status, message);
		setSongFeedback(songFeedbackDTO);
	}

	public SongFeedbackDTO_V0_4 getSongFeedback() {
		return songFeedbackDTO;
	}

	public void setSongFeedback(SongFeedbackDTO_V0_4 songFeedbackDTO) {
		this.songFeedbackDTO = songFeedbackDTO;
	}

}
