package hu.uni.miskolc.iit.view;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;

import hu.uni.miskolc.iit.model.Client;
import hu.uni.miskolc.iit.service.ClientService;
import hu.uni.miskolc.iit.service.exception.ClientManipulationException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean
public class ClientView {

	private int searchType;
	private String pattern;
	
	private Client client;
	private List<Client> clients;

	@ManagedProperty("#{clientService}")
	private ClientService service;

	@PostConstruct
	public void init() {
		clients = service.getClients();
	}

	public void onRowEdit(RowEditEvent event) {
		Client c = (Client) event.getObject();
		FacesMessage msg;
		try {
			service.save(c);
			msg = new FacesMessage("Elmentve!", c.toString());
		} catch (ClientManipulationException e) {
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR ,"Hiba!", e.getMessage());
		}
		FacesContext.getCurrentInstance().addMessage(null, msg);
		clients = service.getClients();
	}

	public void onRowEditCancel(RowEditEvent event) {
		Client c = (Client) event.getObject();
		FacesMessage msg = new FacesMessage("Megszakítva!", c.toString());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void onSearchButtonClick(ActionEvent event) {
		switch (searchType) {
		case 1:
			clients = service.getClientsByClientId(pattern);
			break;
		case 2:
			clients = service.getClientsByPersonalId(pattern);
			break;
		case 3:
			clients = service.getClientsByName(pattern);
			break;
		default:
			clients = service.getClients();
			break;
		}
	}
	
}
