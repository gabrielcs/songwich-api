package models.api;

import java.util.GregorianCalendar;

public interface BasicModel {

	public void setCreatedBy(String devEmail);

	public String getCreatedBy();

	public void setCreatedAt(long createdAt);

	public void setCreatedAt(GregorianCalendar createdAt);

	public long getCreatedAt();

	public long getLastModifiedAt();

	public void setLastModifiedAt(long lastModifiedAt);

	public void setLastModifiedAt(GregorianCalendar lastModifiedAt);

	public String getLastModifiedBy();

	public void setLastModifiedBy(String lastModifiedBy);
}
