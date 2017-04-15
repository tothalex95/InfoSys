package hu.uni.miskolc.iit.view;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.validation.constraints.NotNull;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Client;
import hu.uni.miskolc.iit.model.Transaction;
import hu.uni.miskolc.iit.service.AccountService;
import hu.uni.miskolc.iit.service.ClientService;
import hu.uni.miskolc.iit.service.TransactionService;
import hu.uni.miskolc.iit.service.exception.AccountManipulationException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean
public class PaymentView {

	private Client client;
	
	@NotNull
	private Account account;
	
	@NotNull
	private Float amount;
	
	private String comment;
	
	private List<Client> clients;
	private List<Account> accounts;
	
	@ManagedProperty("#{accountService}")
	private AccountService accountService;
	
	@ManagedProperty("#{clientService}")
	private ClientService clientService;
	
	@ManagedProperty("#{transactionService}")
	private TransactionService transactionService;
	
	@PostConstruct
	public void init() {
		clients = clientService.getClientsByStatus(Client.Status.ACTIVE);
		accounts = accountService.getAccountsByClosed(false);
	}
	
	// administrator removes money from client's account 
	public void onSubmit1(ActionEvent event) {
		FacesMessage msg;
		try {
			Transaction transaction = new Transaction();
			transaction.setAccount(account);
			transaction.setAmount(-amount);
			transaction.setComment(comment);
			transaction.setDate(new Date());
			transactionService.save(transaction);
			msg = new FacesMessage("Elmentve!", transaction.toString());
		} catch (AccountManipulationException e) {
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", e.getMessage());
		}
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	// administrator adds money to client's account
	public void onSubmit2(ActionEvent event) {
		FacesMessage msg;
		try {
			Transaction transaction = new Transaction();
			transaction.setAccount(account);
			transaction.setAmount(amount);
			transaction.setComment(comment);
			transaction.setDate(new Date());
			transactionService.save(transaction);
			msg = new FacesMessage("Elmentve!", transaction.toString());
		} catch (AccountManipulationException e) {
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", e.getMessage());
		}
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	// update the list of accounts based on the selected client
	public void onValueChange() {
		accounts = client.getAccounts();
	}
	
}
