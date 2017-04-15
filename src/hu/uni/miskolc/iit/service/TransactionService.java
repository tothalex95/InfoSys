package hu.uni.miskolc.iit.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Transaction;
import hu.uni.miskolc.iit.persist.AccountDAO;
import hu.uni.miskolc.iit.persist.TransactionDAO;
import hu.uni.miskolc.iit.service.exception.AccountManipulationException;

@ManagedBean(name = "transactionService", eager = true)
@ApplicationScoped
public class TransactionService {

	private AccountDAO accountDao;
	
	private TransactionDAO transactionDao;
	
	@PostConstruct
	public void init() {
		accountDao = new AccountDAO();
		transactionDao = new TransactionDAO();
	}
	
	public void save(Transaction transaction) throws AccountManipulationException {
		Account account = transaction.getAccount();
		Account addressee = transaction.getAddressee();
		if (account.getClosed() || (addressee != null && addressee.getClosed()))
			throw new AccountManipulationException("Zárolt számlán nem hajtható végre tranzakció.");
		if (transaction.getAmount() < 0 && account.getBalance() < Math.abs(transaction.getAmount()))
			throw new AccountManipulationException("Nem áll rendelkezésre a kért összeg.");
		if (account.equals(transaction.getAddressee()))
			throw new AccountManipulationException("A forrás és cél számla nem egyezhet meg.");
		account.setBalance(account.getBalance() + transaction.getAmount());
		accountDao.save(account);
		if (addressee != null) {
			addressee.setBalance(addressee.getBalance() - transaction.getAmount());
			accountDao.save(addressee);
		}
		transaction.setTransactionId(transactionDao.generateId());
		transactionDao.save(transaction);
	}
	
	public List<Transaction> getTransactions() {
		return transactionDao.findAll();
	}
	
}
