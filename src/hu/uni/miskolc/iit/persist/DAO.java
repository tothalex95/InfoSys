package hu.uni.miskolc.iit.persist;

import java.io.File;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public interface DAO<T> {

	final GraphDatabaseService DB = new GraphDatabaseFactory().newEmbeddedDatabase(new File("C:/Users/Alex/Documents/Neo4j/default.graphdb"));
	
	Long generateId();
	
	void save(T object);
	
	T nodeToObject(Node node);
	
	List<T> findAll();
	
	T findOne(Long id);
	
}
