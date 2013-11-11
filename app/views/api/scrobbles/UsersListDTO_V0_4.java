package views.api.scrobbles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;

import views.api.DataTransferObject;

@JsonTypeName("users")
public class UsersListDTO_V0_4 extends DataTransferObject implements
		Iterable<UserDTO_V0_4> {

	private List<UserDTO_V0_4> list;

	public UsersListDTO_V0_4() {
		list = new ArrayList<UserDTO_V0_4>();
	}

	public UsersListDTO_V0_4(int initialCapacity) {
		list = new ArrayList<UserDTO_V0_4>(initialCapacity);
	}

	@Override
	public void addValidation() {
		// nothing to validate
	}

	public List<UserDTO_V0_4> getList() {
		return list;
	}

	public void setList(List<UserDTO_V0_4> list) {
		this.list = list;
	}

	public boolean add(UserDTO_V0_4 dto) {
		return list.add(dto);
	}

	// to be able to use it in for each loops
	@Override
	public Iterator<UserDTO_V0_4> iterator() {
		return list.iterator();
	}
}
