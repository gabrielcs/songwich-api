package views.api.scrobbles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;

import views.api.DataTransferObject;

@JsonTypeName("scrobbles")
public class ScrobblesListDTO_V0_4 extends DataTransferObject implements
		Iterable<ScrobbleDTO_V0_4> {

	private List<ScrobbleDTO_V0_4> list;

	public ScrobblesListDTO_V0_4() {
		list = new ArrayList<ScrobbleDTO_V0_4>();
	}

	public ScrobblesListDTO_V0_4(int initialCapacity) {
		list = new ArrayList<ScrobbleDTO_V0_4>(initialCapacity);
	}

	@Override
	public void addValidation() {
		// nothing to validate
	}

	public List<ScrobbleDTO_V0_4> getList() {
		return list;
	}

	public void setList(List<ScrobbleDTO_V0_4> list) {
		this.list = list;
	}

	public boolean add(ScrobbleDTO_V0_4 dto) {
		return list.add(dto);
	}

	// to be able to use it in for each loops
	@Override
	public Iterator<ScrobbleDTO_V0_4> iterator() {
		return list.iterator();
	}
}
