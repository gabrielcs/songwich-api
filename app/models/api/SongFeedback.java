package models.api;

import models.api.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

@Embedded
public class SongFeedback extends Model {
	@Indexed
	@Embedded
	private FeedbackType feedbackType;
	
	private ObjectId userId;
	
	@Embedded
	public enum FeedbackType {
		THUMBS_UP, THUMBS_DOWN, STAR;
	}
	
	public SongFeedback(FeedbackType feedback, ObjectId userId) {
		super();
		setFeedbackType(feedback);
		setUserId(userId);
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackType feedback) {
		this.feedbackType = feedback;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "SongFeedback [feedbackType=" + feedbackType + ", userId="
				+ userId + ", super.toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((feedbackType == null) ? 0 : feedbackType.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SongFeedback other = (SongFeedback) obj;
		if (feedbackType != other.feedbackType)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}
