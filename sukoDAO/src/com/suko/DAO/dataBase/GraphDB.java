package com.suko.DAO.dataBase;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

public class GraphDB {
	
	private Session session;
	private Driver driver;
	private static final Logger logger = LogManager.getLogger(GraphDB.class);

	public GraphDB(){
		driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "ywaaq6f!N" ), Config.build()
		        .withEncryptionLevel( Config.EncryptionLevel.REQUIRED )
		        .withTrustStrategy( Config.TrustStrategy.trustOnFirstUse( new File( "/path/to/neo4j_known_hosts" ) ) )
		        .toConfig() );
	}
	
	public StatementResult run(String statement){
		session = driver.session();
		logger.debug(statement);
		StatementResult result = this.session.run(statement);
		this.session.close();
		return result;
	}
	
	protected void finalize(){
		this.driver.close();
	}
	public void close(){
		finalize();
	}
}
