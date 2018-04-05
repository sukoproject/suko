package com.suko.DAO.dataBase;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import com.suko.DAO.business.Person;
import com.suko.DAO.business.Word;

/**
 * @author Jeremy
 *
 */
public class PersonGraphDB extends GraphDB {

	public PersonGraphDB(){
		super();
	}

	private static final Logger logger = LogManager.getLogger(PersonGraphDB.class);
	/**
	 * @param listSkill
	 * Search the closest persons regarding a functionnal algo.
	 * Test method TODO : create a type class to manage the driver 
	 */	
	public List<Person> searchPersonGraphDatabase(Word skill,int nbPersons) {
		List<Person> personsList = new ArrayList<Person>();
		String request=
			 " MATCH (p:Person)-[*]->(s) where s.name = \""+ skill.getValue() + "\""
			+" MATCH path=shortestPath((p)-[r*]->(s)) UNWIND r as R "
			+" return p.name,sum(distinct R.rate)/length(path) as RATE "
			+" order by RATE desc SKIP 0 LIMIT "+nbPersons;	
		StatementResult result = run(request);
		if(result != null){
			while(result.hasNext()){
				Record line = result.next();
				personsList.add(getPersonfromResultGraphDataBase(line));
				
			}
		}
		return personsList;
	}
	
	/**
	 * @param skills
	 * @param listIgnoreSkill 
	 * @param nbPersons
	 * @return
	 */
	public List<Person> searchPersonGraphDatabase(List<String> skills, int skip, int nbPersons) {
		List<Person> personsList = new ArrayList<Person>();
		StringBuilder s = new StringBuilder();
		int i=1;
			
		s.append("match (n:Skill) where n.name IN [");
		for(String w:skills){
			s.append("'");
			s.append(w);
			s.append("'");
			if(i<skills.size()){
				s.append(",");
			}
			i++;
		}
		s.append("] optional match(p:Person)-[r:KNOWS]->(n)");
		s.append("return p.email,SUM(r.rate) as RATE,count(p) ORDER BY RATE desc");
		s.append(" SKIP ");
		s.append(skip);
		s.append(" LIMIT ");
		s.append(nbPersons);
		String request = s.toString();	
		System.out.println(request);
		StatementResult result = run(request);
		if(result != null){
			while(result.hasNext()){
				Record line = result.next();
				Person p = getMatchPersonfromResultGraphDataBase(line);
				if(!p.getEmail().isEmpty()){
					personsList.add(getMatchPersonfromResultGraphDataBase(line));
				}
				
			}
		}else{
			logger.debug(request);
		}
		return personsList;
	}

	private Person getMatchPersonfromResultGraphDataBase(Record line){
		Person p = new Person();
		//Get Email
		String email = line.get(0).toString();
		email = email.replaceAll("\"", "");
		p.setEmail(email);
		p.setRate(Integer.parseInt(line.get(1).toString()));
		return p;		
	}
	
	private Person getPersonfromResultGraphDataBase(Record line){
		Person p = new Person();
		//Get Email
		p.setEmail(line.get(0).toString().replaceAll("\"", ""));
		//Get RATE
		p.setRate(line.get(1).asInt());
		return p;		
	}
}
