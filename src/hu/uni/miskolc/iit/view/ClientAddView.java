package hu.uni.miskolc.iit.view;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import hu.uni.miskolc.iit.model.Client;
import hu.uni.miskolc.iit.service.ClientService;
import hu.uni.miskolc.iit.service.exception.ClientManipulationException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean
public class ClientAddView {

	private Client client;
	
	@ManagedProperty("#{clientService}")
	private ClientService service;
	
	@PostConstruct
	public void init() {
		client = new Client();
	}
	
	public void onSubmit(ActionEvent event) {
		Client c = new Client();
		c.setName(client.getName());
		c.setAddress(client.getAddress());
		c.setPhoneNumber(client.getPhoneNumber());
		c.setPersonalId(client.getPersonalId());
		FacesMessage msg;
		try {
			service.save(c);
			msg = new FacesMessage("Elmentve!", c.toString());
		} catch (ClientManipulationException e) {
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR ,"Hiba!", e.getMessage());
		}
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
}
