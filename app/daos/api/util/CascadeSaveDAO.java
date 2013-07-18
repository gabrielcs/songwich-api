package daos.api.util;

import com.google.code.morphia.dao.DAO;

public interface CascadeSaveDAO<T,K> extends DAO<T,K> {
	public void cascadeSave(T t);
}
