package hu.uni.miskolc.iit.view;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import hu.uni.miskolc.iit.model.Transaction;
import hu.uni.miskolc.iit.service.TransactionService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean
public class TransactionView {

	private List<Transaction> transactions;

	@ManagedProperty("#{transactionService}")
	private TransactionService service;

	@PostConstruct
	public void init() {
		transactions = service.getTransactions();
	}
	
}
