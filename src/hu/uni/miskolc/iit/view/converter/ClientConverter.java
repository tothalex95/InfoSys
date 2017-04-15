package hu.uni.miskolc.iit.view.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import hu.uni.miskolc.iit.model.Client;
import hu.uni.miskolc.iit.service.ClientService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean(name = "clientConverter")
public class ClientConverter implements Converter {

	@ManagedProperty("#{clientService}")
	private ClientService service;
	
	// the string contains an id, i have to look up the client identified by this id
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return service.getClientByClientId(Long.valueOf(value));
	}

	// a client is represented by its id, so this method returns that
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		Client client = (Client) value;
		return client.getClientId().toString();
	}

}
