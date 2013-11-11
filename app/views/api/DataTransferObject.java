package views.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

/*
 * http://martinfowler.com/eaaCatalog/dataTransferObject.html
 */
public abstract class DataTransferObject {
	@JsonIgnore
	private DTOValidator validator;
	
	public DataTransferObject() {
	}
	
	public DTOValidator getValidator() {
		return validator;
	}

	public void setValidator(DTOValidator validator) {
		this.validator = validator;
	}

	// method called by Play!
	public List<ValidationError> validate() {
		if (validator == null) {
			return null;
		}
		
		return validator.validate();
	}

}
