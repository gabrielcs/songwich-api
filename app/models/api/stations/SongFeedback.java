package models.api.stations;

import models.api.MongoModel;
import models.api.MongoModelImpl;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

@Embedded
public class SongFeedback extends MongoModelImpl implements MongoModel {
	@Indexed
	@Embedded
	private FeedbackType feedbackType;

	private ObjectId userId;

	public enum FeedbackType {
		THUMBS_UP, THUMBS_DOWN, STAR;
	}

	protected SongFeedback() {
		super();
	}

	public SongFeedback(FeedbackType feedback, ObjectId userId) {
		this.userId = userId;
		this.feedbackType = feedback;
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackType feedback) {
		this.feedbackType = feedback;
		fireModelUpdated();
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
		fireModelUpdated();
	}

	@Override
	public String toString() {
		return "SongFeedback [feedbackType=" + feedbackType + ", userId="
				+ userId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		
		// custom hashCode for the FeedbackType enum
		switch (feedbackType) {
		case STAR:
			result = prime * result + 1;
			break;
		case THUMBS_DOWN:
			result = prime * result + 2;
			break;
		case THUMBS_UP:
			result = prime * result + 3;
			break;
		default:
			result = prime * result + 0;
			break;
		}
		
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
