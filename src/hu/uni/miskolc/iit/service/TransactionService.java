package hu.uni.miskolc.iit.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	
	public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
		return transactionDao.findByAccountNumber(accountNumber);
	}
	
	public List<Transaction> getTransactionsByAmount(Float amount) {
		return transactionDao.findByAmount(amount);
	}
	
	public List<Transaction> getTransactionsByEarliestDate(String earliest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		return transactionDao.findAll().stream().filter(t -> t.getDate().after(earliestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByLatestDate(String latest) throws ParseException {
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findAll().stream().filter(t -> t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAccountNumberAndAmount(String accountNumber, Float amount) {
		return transactionDao.findByAccountNumberAndAmount(accountNumber, amount);
	}
	
	public List<Transaction> getTransactionsByAccountNumberAndEarliestDate(String accountNumber, String earliest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		return transactionDao.findByAccountNumber(accountNumber).stream().filter(t -> t.getDate().after(earliestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAccountNumberAndLatestDate(String accountNumber, String latest) throws ParseException {
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findByAccountNumber(accountNumber).stream().filter(t -> t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAmountAndEarliestDate(Float amount, String earliest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		return transactionDao.findByAmount(amount).stream().filter(t -> t.getDate().after(earliestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAmountAndLatestDate(Float amount, String latest) throws ParseException {
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findByAmount(amount).stream().filter(t -> t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByEarliestDateAndLatestDate(String earliest, String latest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findAll().stream().filter(t -> t.getDate().after(earliestDate) && t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAccountNumberAndAmountAndEarliestDate(String accountNumber, Float amount, String earliest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		return transactionDao.findByAccountNumberAndAmount(accountNumber, amount).stream().filter(t -> t.getDate().after(earliestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAccountNumberAndAmountAndLatestDate(String accountNumber, Float amount, String latest) throws ParseException {
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findByAccountNumberAndAmount(accountNumber, amount).stream().filter(t -> t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAccountNumberAndEarliestDateAndLatestDate(String accountNumber, String earliest, String latest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findByAccountNumber(accountNumber).stream().filter(t -> t.getDate().after(earliestDate) && t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAmountAndEarliestDateAndLatestDate(Float amount, String earliest, String latest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findByAmount(amount).stream().filter(t -> t.getDate().after(earliestDate) && t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
	public List<Transaction> getTransactionsByAccountNumberAndAmountAndEarliestDateAndLatestDate(String accountNumber, Float amount, String earliest, String latest) throws ParseException {
		Date earliestDate = Transaction.DF.parse(earliest);
		Date latestDate = Transaction.DF.parse(latest);
		return transactionDao.findByAccountNumberAndAmount(accountNumber, amount).stream().filter(t -> t.getDate().after(earliestDate) && t.getDate().before(latestDate)).collect(Collectors.toList());
	}
	
}
