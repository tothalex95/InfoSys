package hu.uni.miskolc.iit.view;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Client;
import hu.uni.miskolc.iit.service.AccountService;
import hu.uni.miskolc.iit.service.ClientService;
import hu.uni.miskolc.iit.service.exception.AccountManipulationException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean
public class AccountAddView {

	private Account account;
	private List<Client> clients;
	
	@ManagedProperty("#{accountService}")
	private AccountService accountService;
	
	@ManagedProperty("#{clientService}")
	private ClientService clientService;
	
	@PostConstruct
	public void init() {
		account = new Account();
		clients = clientService.getClientsByStatus(Client.Status.ACTIVE);
	}
	
	public void onSubmit(ActionEvent event) {
		Account a = new Account();
		a.setClient(account.getClient());
		a.setBalance(account.getBalance());
		FacesMessage msg;
		try {
			accountService.save(a);
			msg = new FacesMessage("Elmentve!", a.toString());
		} catch (AccountManipulationException e) {
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", e.getMessage());
		}
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
}
