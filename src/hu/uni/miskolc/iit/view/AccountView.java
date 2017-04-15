package hu.uni.miskolc.iit.view;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.service.AccountService;
import hu.uni.miskolc.iit.service.exception.AccountManipulationException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean
public class AccountView {

	private Account account;
	private List<Account> accounts;
	
	@ManagedProperty("#{accountService}")
	private AccountService service;
	
	@PostConstruct
	public void init() {
		accounts = service.getAccounts();
	}
	
	public void onRowEdit(RowEditEvent event) {
		Account a = (Account) event.getObject();
		FacesMessage msg;
		try {
			service.save(a);
			msg = new FacesMessage("Elmentve!", a.toString());
		} catch (AccountManipulationException e) {
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", e.getMessage());
		}
		FacesContext.getCurrentInstance().addMessage(null, msg);
		accounts = service.getAccounts();
	}

	public void onRowEditCancel(RowEditEvent event) {
		Account a = (Account) event.getObject();
		FacesMessage msg = new FacesMessage("Megszakítva!", a.toString());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
}
