package models.api.scrobbles;

import java.util.ArrayList;
import java.util.List;

import models.api.MongoEntity;
import models.api.MongoModelImpl;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class App extends MongoModelImpl implements MongoEntity {
	@Id
	private ObjectId id;

	private String name;

	@Embedded
	private List<AppDeveloper> appDevelopers = new ArrayList<AppDeveloper>();

	protected App() {
		super();
	}

	public App(String name) {
		this.name = name;
	}

	public App(String name, AppDeveloper appDeveloper) {
		this.name = name;
		appDevelopers.add(appDeveloper);
	}

	public App(String name, List<AppDeveloper> appDevelopers) {
		this.name = name;
		this.appDevelopers = appDevelopers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireModelUpdated();
	}

	public List<AppDeveloper> getAppDevelopers() {
		return appDevelopers;
	}

	public void setAppDevelopers(List<AppDeveloper> appDevelopers) {
		this.appDevelopers = appDevelopers;
		fireModelUpdated();
	}

	/**
	 * 
	 * @param appDeveloper
	 * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
	 */
	public boolean addAppDeveloper(AppDeveloper appDeveloper) {
		boolean result = appDevelopers.add(appDeveloper);
		fireModelUpdated();
		return result;
	}

	public AppDeveloper getAppDeveloper(String appDeveloperEmail) {
		for (AppDeveloper appDev : appDevelopers) {
			if (appDev.getEmailAddress().equals(appDeveloperEmail)) {
				return appDev;
			}
		}
		return null;
	}

	@Override
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
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		App other = (App) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
