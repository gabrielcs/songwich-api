package models.api.scrobbles;

import java.util.ArrayList;
import java.util.List;

import models.api.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class App extends Model {
	@Id
	private ObjectId id;

	private String name;
	
	@Embedded
	private List<AppDeveloper> appDevelopers = new ArrayList<AppDeveloper>();
	
	protected App() {
		super();
	}

	public App(String name, String createdBy) {
		super(createdBy);
		this.name = name;
	}

	public App(String name, AppDeveloper appDeveloper, String createdBy) {
		super(createdBy);
		this.name = name;
		appDevelopers.add(appDeveloper);
	}
	
	public App(String name, List<AppDeveloper> appDevelopers, String createdBy) {
		super(createdBy);
		this.name = name;
		this.appDevelopers = appDevelopers;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name, String modifiedBy) {
		this.name = name;
		setLastModifiedBy(modifiedBy);
	}

	public List<AppDeveloper> getAppDevelopers() {
		return appDevelopers;
	}

	public void setAppDevelopers(List<AppDeveloper> appDevelopers, String modifiedBy) {
		this.appDevelopers = appDevelopers;
		setLastModifiedBy(modifiedBy);
	}
	
	/**
	 * 
	 * @param appDeveloper
	 * @param modifiedBy
	 * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
	 */
	public boolean addAppDeveloper(AppDeveloper appDeveloper, String modifiedBy) {
		setLastModifiedBy(modifiedBy);
		return appDevelopers.add(appDeveloper);
	}
	
	public AppDeveloper getAppDeveloper(String appDeveloperEmail) {
		for (AppDeveloper appDev : appDevelopers) {
			if (appDev.getEmailAddress().equals(appDeveloperEmail)) {
				return appDev;
			}
		}
		return null;
	}

	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "App [id=" + id + ", name=" + name + ", appDevelopers="
				+ appDevelopers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appDevelopers == null) ? 0 : appDevelopers.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		App other = (App) obj;
		if (appDevelopers == null) {
			if (other.appDevelopers != null)
				return false;
		} else if (!appDevelopers.equals(other.appDevelopers))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}