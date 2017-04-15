package hu.uni.miskolc.iit.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Client;

public class AccountDAO implements DAO<Account> {

	public static final Label ACCOUNT_LABEL = Label.label("ACCOUNT");
	
	public static final RelationshipType BELONGS_TO = RelationshipType.withName("BELONGS_TO");
	
	boolean SEARCH_FOR_NESTED_OBJECTS = true;
	
	@Override
	public Long generateId() {
		String query = "MATCH (n:" + ACCOUNT_LABEL.name() + ") RETURN COUNT(n) + 1 AS accountId";
		return (Long) DB.execute(query).columnAs("accountId").next();
	}
	
	public Long generateSequenceNumber(Client client) {
		String query = "MATCH (n:" + ACCOUNT_LABEL.name() + ")-[:" + BELONGS_TO.name() + "]->(m:" + ClientDAO.CLIENT_LABEL.name() + ") WHERE m.clientId = " + client.getClientId() + " RETURN COUNT(n) + 1000 AS sequenceNumber";
		return (Long) DB.execute(query).columnAs("sequenceNumber").next();
	}

	@Override
	public void save(Account account) {
		try (Transaction tx = DB.beginTx()) {
			Node accountNode = DB.findNode(ACCOUNT_LABEL, "accountId", account.getAccountId());
			if (accountNode == null)
				accountNode = DB.createNode(ACCOUNT_LABEL);
			accountNode.setProperty("accountId", account.getAccountId());
			accountNode.setProperty("sequenceNumber", account.getSequenceNumber());
			accountNode.setProperty("balance", account.getBalance());
			accountNode.setProperty("closed", account.getClosed());
			if (!accountNode.hasRelationship()) {
				Node clientNode = DB.findNode(ClientDAO.CLIENT_LABEL, "clientId", account.getClient().getClientId());
				accountNode.createRelationshipTo(clientNode, BELONGS_TO);
			}
			tx.success();
		}
	}

	@Override
	public Account nodeToObject(Node node) {
		Account account = new Account();
		Map<String, Object> props = node.getAllProperties();
		account.setAccountId((Long) props.get("accountId"));
		account.setSequenceNumber((Long) props.get("sequenceNumber"));
		account.setBalance((Float) props.get("balance"));
		account.setClosed((Boolean) props.get("closed"));
		if (SEARCH_FOR_NESTED_OBJECTS)
			account.setClient(findRelatedClient(node));
		return account;
	}

	@Override
	public List<Account> findAll() {
		List<Account> accounts = new ArrayList<>();
		try (Transaction tx = DB.beginTx()) {
			ResourceIterator<Node> nodes = DB.findNodes(ACCOUNT_LABEL);
			while (nodes.hasNext())
				accounts.add(nodeToObject(nodes.next()));
			tx.success();
		}
		return accounts;
	}

	@Override
	public Account findOne(Long id) {
		Account account;
		try (Transaction tx = DB.beginTx()) {
			account = nodeToObject(DB.findNode(ACCOUNT_LABEL, "accountId", id));
			tx.success();
		}
		return account;
	}
	
	public List<Account> findByClient(Client client) {
		List<Account> accounts = new ArrayList<>();
		try (Transaction tx = DB.beginTx()) {
			ResourceIterator<Node> nodes = DB.findNodes(ACCOUNT_LABEL, "clientId", client.getClientId());
			while (nodes.hasNext())
				accounts.add(nodeToObject(nodes.next()));
			tx.success();
		}
		return accounts;
	}
	
	public List<Account> findByClosed(Boolean closed) {
		List<Account> accounts = new ArrayList<>();
		try (Transaction tx = DB.beginTx()) {
			ResourceIterator<Node> nodes = DB.findNodes(ACCOUNT_LABEL, "closed", closed);
			while (nodes.hasNext())
				accounts.add(nodeToObject(nodes.next()));
			tx.success();
		}
		return accounts;
	}
	
	private Client findRelatedClient(Node accountNode) {
		Client client;
		ClientDAO clientDao = new ClientDAO();
		Node clientNode = accountNode.getSingleRelationship(BELONGS_TO, Direction.OUTGOING).getOtherNode(accountNode);
		clientDao.SEARCH_FOR_NESTED_OBJECTS = false;
		client = clientDao.nodeToObject(clientNode);
		clientDao.SEARCH_FOR_NESTED_OBJECTS = true;
		return client;
	}

}
