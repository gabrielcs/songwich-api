package models;

import java.util.ArrayList;
import java.util.List;

import models.util.Model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class App extends Model {
	@Id
	private ObjectId id;

	@Indexed
	private String name;
	
	@Embedded
	private List<AppDeveloper> appDevelopers;
	
	protected App() {
		super();
	}

	public App(String name, String createdBy) {
		super(createdBy);
		setName(name);
		appDevelopers = new ArrayList<AppDeveloper>();
	}

	public App(String name, AppDeveloper appDeveloper, String createdBy) {
		super(createdBy);
		setName(name);
		appDevelopers = new ArrayList<AppDeveloper>();
		addAppDeveloper(appDeveloper);
	}
	
	public App(String name, List<AppDeveloper> appDevelopers, String createdBy) {
		super(createdBy);
		setName(name);
		appDevelopers = new ArrayList<AppDeveloper>();
		setAppDevelopers(appDevelopers);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AppDeveloper> getAppDevelopers() {
		return appDevelopers;
	}

	public void setAppDevelopers(List<AppDeveloper> appDevelopers) {
		this.appDevelopers = appDevelopers;
	}
	
	public void addAppDeveloper(AppDeveloper appDeveloper) {
		appDevelopers.add(appDeveloper);
	}

	public ObjectId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "App [id=" + id + ", name=" + name + ", appDevelopers="
				+ appDevelopers + ", super.toString()=" + super.toString() + "]";
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
