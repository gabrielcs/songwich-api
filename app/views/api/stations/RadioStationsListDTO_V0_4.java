package views.api.stations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeName;

import views.api.DataTransferObject;

@JsonTypeName("stations")
public class RadioStationsListDTO_V0_4 extends DataTransferObject implements
		Iterable<RadioStationDTO_V0_4> {

	private List<RadioStationDTO_V0_4> list;

	public RadioStationsListDTO_V0_4() {
		list = new ArrayList<RadioStationDTO_V0_4>();
	}

	public RadioStationsListDTO_V0_4(int initialCapacity) {
		list = new ArrayList<RadioStationDTO_V0_4>(initialCapacity);
	}

	@Override
	public void addValidation() {
		// nothing to validate
	}

	public List<RadioStationDTO_V0_4> getList() {
		return list;
	}

	public void setList(List<RadioStationDTO_V0_4> list) {
		this.list = list;
	}

	public boolean add(RadioStationDTO_V0_4 dto) {
		return list.add(dto);
	}

	// to be able to use it in for each loops
	@Override
	public Iterator<RadioStationDTO_V0_4> iterator() {
		return list.iterator();
	}
}
