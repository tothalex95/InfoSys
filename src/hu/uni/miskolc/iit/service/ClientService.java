package hu.uni.miskolc.iit.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Client;
import hu.uni.miskolc.iit.model.Client.Status;
import hu.uni.miskolc.iit.model.Transaction;
import hu.uni.miskolc.iit.persist.AccountDAO;
import hu.uni.miskolc.iit.persist.ClientDAO;
import hu.uni.miskolc.iit.persist.TransactionDAO;
import hu.uni.miskolc.iit.service.exception.ClientManipulationException;
import lombok.Getter;

@Getter
@ManagedBean(name = "clientService", eager = true)
@ApplicationScoped
public class ClientService {

	private AccountDAO accountDao;
	
	private ClientDAO clientDao;
	
	private TransactionDAO transactionDao;
	
	@PostConstruct
	public void init() {
		accountDao = new AccountDAO();
		clientDao = new ClientDAO();
		transactionDao = new TransactionDAO();
	}
	
	public void save(Client client) throws ClientManipulationException {
		if (client.getClientId() == null)
			client.setClientId(clientDao.generateId());
		Client original = clientDao.findOne(client.getClientId());
		if (original != null && original.getStatus() == Status.DELETED)
			throw new ClientManipulationException("A törölt státuszú ügyfelek nem módosíthatók.");
		if (client.getStatus() == Status.DELETED) {
			for (Account account : client.getAccounts()) {
				Transaction transaction = new Transaction();
				transaction.setTransactionId(transactionDao.generateId());
				transaction.setAccount(account);
				transaction.setAmount(-account.getBalance());
				transaction.setComment("A számlatulajdonos törlését követõen a számla zárolva lett.");
				transaction.setDate(new Date());
				transactionDao.save(transaction);
				account.setClosed(true);
				account.setBalance(0f);
				accountDao.save(account);
			}
		}
		clientDao.save(client);
	}
	
	public List<Client> getClients() {
		return clientDao.findAll();
	}
	
	public Client getClientByClientId(Long clientId) {
		return clientDao.findOne(clientId);
	}
	
	public List<Client> getClientsByClientId(String clientId) {
		return getClients().stream().filter(c -> c.getClientId().toString().contains(clientId)).collect(Collectors.toList());
	}
	
	public List<Client> getClientsByPersonalId(String personalId) {
		return clientDao.findByPersonalId(personalId);
	}
	
	public List<Client> getClientsByName(String name) {
		return clientDao.findByName(name);
	}
	
	public List<Client> getClientsByStatus(Status status) {
		return clientDao.findByStatus(status);
	}
	
}
