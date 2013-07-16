package daos.api.util;

public interface DomainAccessObject<T> {
	
	public void save(T t);
	
	public void update(T t);
	
	public void delete(T t);

}
