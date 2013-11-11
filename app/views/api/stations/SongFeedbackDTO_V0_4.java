package views.api.stations;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.ValidationError;
import views.api.DTOValidator;
import views.api.DataTransferObject;

//@JsonInclude(Include.NON_EMPTY)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeName("songFeedback")
public class SongFeedbackDTO_V0_4 extends DataTransferObject {

	private String idForFeedback;

	private String feedbackType;

	private SongDTO_V0_4 song;

	// only for output
	private String userId;

	public SongFeedbackDTO_V0_4() {
		setValidator(this.new SongFeedbackDTOValidator());
	}

	public SongDTO_V0_4 getSong() {
		return song;
	}

	public void setSong(SongDTO_V0_4 song) {
		this.song = song;
	}

	public String getIdForFeedback() {
		return idForFeedback;
	}

	public void setIdForFeedback(String idForFeedback) {
		this.idForFeedback = idForFeedback;
	}

	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public class SongFeedbackDTOValidator extends DTOValidator {
		@Override
		public void addValidation() {
			addValidation(validateIdForFeedback(), validateFeedbackType());
		}

		private ValidationError validateIdForFeedback() {
			return validateRequiredObjectId("idForFeedback", idForFeedback);
		}

		private ValidationError validateFeedbackType() {
			return validateRequiredProperty("feedbackType", feedbackType);
		}
	}

}