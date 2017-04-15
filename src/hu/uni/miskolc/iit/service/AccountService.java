package hu.uni.miskolc.iit.service;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Client.Status;
import hu.uni.miskolc.iit.model.Transaction;
import hu.uni.miskolc.iit.persist.AccountDAO;
import hu.uni.miskolc.iit.persist.TransactionDAO;
import hu.uni.miskolc.iit.service.exception.AccountManipulationException;
import lombok.Getter;

@Getter
@ManagedBean(name = "accountService", eager = true)
@ApplicationScoped
public class AccountService {

	private AccountDAO accountDao;

	private TransactionDAO transactionDao;

	@PostConstruct
	public void init() {
		accountDao = new AccountDAO();
		transactionDao = new TransactionDAO();
	}

	public void save(Account account) throws AccountManipulationException {
		if (account.getAccountId() != null
				&& accountDao.findOne(account.getAccountId()).getClient().getStatus() == Status.DELETED)
			throw new AccountManipulationException("Törölt státuszú ügyfél számlája nem módosítható.");
		if (account.getBalance() < 0)
			throw new AccountManipulationException("A számla egyenlege nem lehet negatív.");
		if (account.getAccountId() == null) {
			account.setAccountId(accountDao.generateId());
			account.setSequenceNumber(accountDao.generateSequenceNumber(account.getClient()));
			accountDao.save(account);
			if (account.getBalance() > 0) {
				Transaction transaction = new Transaction();
				transaction.setTransactionId(transactionDao.generateId());
				transaction.setAccount(account);
				transaction.setAmount(account.getBalance());
				transaction.setComment("Számlanyitást követõ kezdeti befizetés.");
				transaction.setDate(new Date());
				transactionDao.save(transaction);
			}
		}
		if (account.getClosed()) {
			Transaction transaction = new Transaction();
			transaction.setTransactionId(transactionDao.generateId());
			transaction.setAccount(account);
			transaction.setAmount(-account.getBalance());
			transaction.setComment("A számla zárolva lett.");
			transaction.setDate(new Date());
			transactionDao.save(transaction);
			account.setBalance(0f);
		}
		accountDao.save(account);
	}

	public List<Account> getAccounts() {
		return accountDao.findAll();
	}

	public Account getAccountByAccountId(Long accountId) {
		return accountDao.findOne(accountId);
	}

	public List<Account> getAccountsByClosed(boolean closed) {
		return accountDao.findByClosed(closed);
	}

}
