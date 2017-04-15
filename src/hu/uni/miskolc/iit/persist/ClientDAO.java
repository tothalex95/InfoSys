package hu.uni.miskolc.iit.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.model.Client;
import hu.uni.miskolc.iit.model.Client.Status;

public class ClientDAO implements DAO<Client> {

	public static final Label CLIENT_LABEL = Label.label("CLIENT");
	
	boolean SEARCH_FOR_NESTED_OBJECTS = true;
	
	@Override
	public Long generateId() {
		String query = "MATCH (n:" + CLIENT_LABEL.name() + ") RETURN COUNT(n) + 100000 AS clientId";
		return (Long) DB.execute(query).columnAs("clientId").next();
	}
	
	@Override
	public void save(Client client) {
		try (Transaction tx = DB.beginTx()) {
			Node clientNode = DB.findNode(CLIENT_LABEL, "clientId", client.getClientId());
			if (clientNode == null)
				clientNode = DB.createNode(CLIENT_LABEL);
			clientNode.setProperty("clientId", client.getClientId());
			clientNode.setProperty("name", client.getName());
			clientNode.setProperty("address", client.getAddress());
			clientNode.setProperty("phoneNumber", client.getPhoneNumber());
			clientNode.setProperty("personalId", client.getPersonalId());
			clientNode.setProperty("status", client.getStatus().toString());
			tx.success();
		}
	}

	@Override
	public Client nodeToObject(Node node) {
		Client client = new Client();
		Map<String, Object> props = node.getAllProperties();
		client.setClientId((Long) props.get("clientId"));
		client.setName(String.valueOf(props.get("name")));
		client.setAddress(String.valueOf(props.get("address")));
		client.setPhoneNumber(String.valueOf(props.get("phoneNumber")));
		client.setPersonalId(String.valueOf(props.get("personalId")));
		client.setStatus(Client.Status.valueOf(String.valueOf(props.get("status"))));
		if (SEARCH_FOR_NESTED_OBJECTS)
			client.setAccounts(findRelatedAccounts(node));
		return client;
	}

	@Override
	public List<Client> findAll() {
		List<Client> clients = new ArrayList<>();
		try (Transaction tx = DB.beginTx()) {
			ResourceIterator<Node> nodes = DB.findNodes(CLIENT_LABEL);
			while (nodes.hasNext())
				clients.add(nodeToObject(nodes.next()));
			tx.success();
		}
		return clients;
	}

	@Override
	public Client findOne(Long id) {
		Client client;
		try (Transaction tx = DB.beginTx()) {
			Node clientNode = DB.findNode(CLIENT_LABEL, "clientId", id);
			if (clientNode == null)
				return null;
			client = nodeToObject(clientNode);
			tx.success();
		}
		return client;
	}
	
	public List<Client> findByName(String name) {
		List<Client> clients = new ArrayList<>();
		try (Transaction tx = DB.beginTx()) {
			String query = "MATCH (n:" + CLIENT_LABEL + ") WHERE n.name =~ '.*" + name + ".*' RETURN n";
			Result result = DB.execute(query);
			while (result.hasNext()) {
				Map<String, Object> props = result.next();
				clients.add(nodeToObject((Node) props.entrySet().iterator().next().getValue()));
			}
		}
		return clients;
	}
	
	public List<Client> findByPersonalId(String personalId) {
		List<Client> clients = new ArrayList<>();
		try (Transaction tx = DB.beginTx()) {
			String query = "MATCH (n:" + CLIENT_LABEL + ") WHERE n.personalId =~ '.*" + personalId + ".*' RETURN n";
			Result result = DB.execute(query);
			while (result.hasNext()) {
				Map<String, Object> props = result.next();
				clients.add(nodeToObject((Node) props.entrySet().iterator().next().getValue()));
			}
		}
		return clients;
	}
	
	public List<Client> findByStatus(Status status) {
		List<Client> clients = new ArrayList<>();
		try (Transaction tx = DB.beginTx()) {
			ResourceIterator<Node> nodes = DB.findNodes(CLIENT_LABEL, "status", status.toString());
			while (nodes.hasNext())
				clients.add(nodeToObject(nodes.next()));
			tx.success();
		}
		return clients;
	}
	
	private List<Account> findRelatedAccounts(Node clientNode) {
		List<Account> accounts = new ArrayList<>();
		AccountDAO accountDao = new AccountDAO();
		Iterable<Relationship> relationships = clientNode.getRelationships(AccountDAO.BELONGS_TO);
		accountDao.SEARCH_FOR_NESTED_OBJECTS = false;
		for (Relationship relationship : relationships) {
			Node accountNode = relationship.getOtherNode(clientNode);
			accounts.add(accountDao.nodeToObject(accountNode));
		}
		accountDao.SEARCH_FOR_NESTED_OBJECTS = true;
		return accounts;
	}
	
}
