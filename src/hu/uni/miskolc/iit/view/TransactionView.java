package hu.uni.miskolc.iit.view;

import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;

import hu.uni.miskolc.iit.model.Transaction;
import hu.uni.miskolc.iit.service.TransactionService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean
public class TransactionView {

	private String accountNumber;
	
	private Float amount;
	
	private String earliest;
	
	private String latest;
	
	private List<Transaction> transactions;

	@ManagedProperty("#{transactionService}")
	private TransactionService service;

	@PostConstruct
	public void init() {
		transactions = service.getTransactions();
	}
	
	public void onSearchButtonClick(ActionEvent event) {
		try {
			if (check(accountNumber, amount, earliest, latest))
				transactions = service.getTransactionsByAccountNumberAndAmountAndEarliestDateAndLatestDate(accountNumber, amount, earliest, latest);
			else if (check(accountNumber, amount, earliest))
				transactions = service.getTransactionsByAccountNumberAndAmountAndEarliestDate(accountNumber, amount, earliest);
			else if (check(accountNumber, amount, latest))
				transactions = service.getTransactionsByAccountNumberAndAmountAndLatestDate(accountNumber, amount, latest);
			else if (check(accountNumber, earliest, latest))
				transactions = service.getTransactionsByAccountNumberAndEarliestDateAndLatestDate(accountNumber, earliest, latest);
			else if (check(amount, earliest, latest))
				transactions = service.getTransactionsByAmountAndEarliestDateAndLatestDate(amount, earliest, latest);
			else if (check(accountNumber, amount))
				transactions = service.getTransactionsByAccountNumberAndAmount(accountNumber, amount);
			else if (check(accountNumber, earliest))
				transactions = service.getTransactionsByAccountNumberAndEarliestDate(accountNumber, earliest);
			else if (check(accountNumber, latest))
				transactions = service.getTransactionsByAccountNumberAndLatestDate(accountNumber, latest);
			else if (check(amount, earliest))
				transactions = service.getTransactionsByAmountAndEarliestDate(amount, earliest);
			else if (check(amount, latest))
				transactions = service.getTransactionsByAmountAndLatestDate(amount, latest);
			else if (check(earliest, latest))
				transactions = service.getTransactionsByEarliestDateAndLatestDate(earliest, latest);
			else if (check(accountNumber))
				transactions = service.getTransactionsByAccountNumber(accountNumber);
			else if (check(amount))
				transactions = service.getTransactionsByAmount(amount);
			else if (check(earliest))
				transactions = service.getTransactionsByEarliestDate(earliest);
			else if (check(latest))
				transactions = service.getTransactionsByLatestDate(latest);
			else
				transactions = service.getTransactions();
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private boolean check(Object...object) {
		for (Object obj : object) {
			if (obj == null)
				return false;
			if (obj instanceof String && obj.toString().isEmpty())
				return false;
		}
		return true;
	}
	
}
