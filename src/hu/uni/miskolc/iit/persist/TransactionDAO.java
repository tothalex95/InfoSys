package hu.uni.miskolc.iit.persist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Transaction;

public class TransactionDAO implements DAO<Transaction> {

	public static final Label TRANSACTION_LABEL = Label.label("TRANSACTION");
	
	// couldn't figure out better names, sorry
	public static final RelationshipType SOURCE = RelationshipType.withName("SOURCE");
	public static final RelationshipType ADDRESSEE = RelationshipType.withName("ADDRESSEE");
	
	@Override
	public Long generateId() {
		String query = "MATCH (n:" + TRANSACTION_LABEL.name() + ") RETURN COUNT(n) + 1 AS transactionId";
		return (Long) DB.execute(query).columnAs("transactionId").next();
	}

	@Override
	public void save(Transaction transaction) {
		try (org.neo4j.graphdb.Transaction tx = DB.beginTx()) {
			Node transactionNode = DB.findNode(TRANSACTION_LABEL, "transactionId", transaction.getTransactionId());
			if (transactionNode == null)
				transactionNode = DB.createNode(TRANSACTION_LABEL);
			transactionNode.setProperty("transactionId", transaction.getTransactionId());
			transactionNode.setProperty("amount", transaction.getAmount());
			transactionNode.setProperty("comment", transaction.getComment());
			transactionNode.setProperty("date", Transaction.DF.format(transaction.getDate()));
			if (!transactionNode.hasRelationship()) {
				Node accountNode = DB.findNode(AccountDAO.ACCOUNT_LABEL, "accountId", transaction.getAccount().getAccountId());
				transactionNode.createRelationshipTo(accountNode, SOURCE);
				if (transaction.getAddressee() != null) {
					accountNode = DB.findNode(AccountDAO.ACCOUNT_LABEL, "accountId", transaction.getAddressee().getAccountId());
					transactionNode.createRelationshipTo(accountNode, ADDRESSEE);
				}
			}
			tx.success();
		}
	}

	@Override
	public Transaction nodeToObject(Node node) {
		Transaction transaction = new Transaction();
		Map<String, Object> props = node.getAllProperties();
		transaction.setTransactionId((Long) props.get("transactionId"));
		transaction.setAmount((Float) props.get("amount"));
		transaction.setComment(String.valueOf(props.get("comment")));
		try {
			transaction.setDate(Transaction.DF.parse(String.valueOf(props.get("date"))));
		} catch (ParseException e) {
			transaction.setDate(new Date());
		}
		if (node.hasRelationship(SOURCE))
			transaction.setAccount(findRelatedAccount(node, SOURCE));
		if (node.hasRelationship(ADDRESSEE))
			transaction.setAddressee(findRelatedAccount(node, ADDRESSEE));
		return transaction;
	}

	@Override
	public List<Transaction> findAll() {
		List<Transaction> transactions = new ArrayList<>();
		try (org.neo4j.graphdb.Transaction tx = DB.beginTx()) {
			ResourceIterator<Node> nodes = DB.findNodes(TRANSACTION_LABEL);
			while (nodes.hasNext())
				transactions.add(nodeToObject(nodes.next()));
			tx.success();
		}
		return transactions;
	}

	@Override
	public Transaction findOne(Long id) {
		Transaction transaction;
		try (org.neo4j.graphdb.Transaction tx = DB.beginTx()) {
			transaction = nodeToObject(DB.findNode(TRANSACTION_LABEL, "transactionId", id));
			tx.success();
		}
		return transaction;
	}
	
	public List<Transaction> findByAccountNumber(String accountNumber) {
		List<Transaction> transactions = new ArrayList<>();
		try (org.neo4j.graphdb.Transaction tx = DB.beginTx()) {
			String query = "MATCH (t:" + TRANSACTION_LABEL + ")-->(a:" + AccountDAO.ACCOUNT_LABEL + ")-[:BELONGS_TO]->(c:" + ClientDAO.CLIENT_LABEL + ") WHERE (c.clientId + '-' + a.sequenceNumber) CONTAINS {accountNumber} RETURN t";
			Map<String, Object> params = new HashMap<>();
			params.put("accountNumber", accountNumber);
			Result result = DB.execute(query, params);
			while (result.hasNext()) {
				Map<String, Object> props = result.next();
				transactions.add(nodeToObject((Node) props.entrySet().iterator().next().getValue()));
			}
		}
		return transactions;
	}
	
	public List<Transaction> findByAmount(Float amount) {
		List<Transaction> transactions = new ArrayList<>();
		try (org.neo4j.graphdb.Transaction tx = DB.beginTx()) {
			String query = "MATCH (t:" + TRANSACTION_LABEL + ") WHERE ABS(t.amount) = {amount} RETURN t";
			Map<String, Object> params = new HashMap<>();
			params.put("amount", amount);
			Result result = DB.execute(query, params);
			while (result.hasNext()) {
				Map<String, Object> props = result.next();
				transactions.add(nodeToObject((Node) props.entrySet().iterator().next().getValue()));
			}
		}
		return transactions;
	}
	
	public List<Transaction> findByAccountNumberAndAmount(String accountNumber, Float amount) {
		List<Transaction> transactions = new ArrayList<>();
		try (org.neo4j.graphdb.Transaction tx = DB.beginTx()) {
			String query = "MATCH (t:" + TRANSACTION_LABEL + ")-->(a:" + AccountDAO.ACCOUNT_LABEL + ")-[:BELONGS_TO]->(c:" + ClientDAO.CLIENT_LABEL + ") WHERE (c.clientId + '-' + a.sequenceNumber) CONTAINS {accountNumber} AND ABS(t.amount) = {amount} RETURN t";
			Map<String, Object> params = new HashMap<>();
			params.put("accountNumber", accountNumber);
			params.put("amount", amount);
			Result result = DB.execute(query, params);
			while (result.hasNext()) {
				Map<String, Object> props = result.next();
				transactions.add(nodeToObject((Node) props.entrySet().iterator().next().getValue()));
			}
		}
		return transactions;
	}
	
	private Account findRelatedAccount(Node transactionNode, RelationshipType rType) {
		Account account;
		AccountDAO accountDao = new AccountDAO();
		Node accountNode = transactionNode.getSingleRelationship(rType, Direction.OUTGOING).getOtherNode(transactionNode);
		//accountDao.SEARCH_FOR_NESTED_OBJECTS = false;
		account = accountDao.nodeToObject(accountNode);
		//accountDao.SEARCH_FOR_NESTED_OBJECTS = true;
		return account;
	}

}
