package com.suko.DAO.dataBase;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.*;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import com.suko.DAO.business.Document;

/**
 * @author Jeremy
 *
 */
public class SkillsGraphDB extends GraphDB {
	
	private static final Logger logger = LogManager.getLogger(SkillsGraphDB.class);
	
	public SkillsGraphDB(){
		super();
	}
	
	public Map<String,Integer> searchSkillsOfPersonDataBase(String email,int nbResult){
		
		StringBuffer s = new StringBuffer();
		s.append("match (p:Person)-[r]-(s:Skill) where p.email='");
		s.append(email);
		s.append("' and r.rate>1 return s.name, r.rate as RATE order by r.rate desc LIMIT ");
		s.append(nbResult);
		StatementResult sResult = run(s.toString());
		if(sResult != null){
			return getSkillAndRatefromResultGraphDataBase(sResult);
		}
		return null;
		
	}
	
	private Map<String, Integer> getSkillAndRatefromResultGraphDataBase(StatementResult sResult) {
		Map<String,Integer> result = new TreeMap<String, Integer>();
		while(sResult.hasNext()){
			Record line = sResult.next();
			String skill = line.get(0).toString().replaceAll("\"", "");
			int rate =  line.get(1).asInt();
			result.put(skill, rate);
		}
		return result;
	}

	/**Method to test, TODO: create class to manage Driver.
	 * @param listSkill : list of skills to merge (create if necessary)
	 * @param person : person who KNOWS this skills
	 * @param document : document where the skills COME FROM.
	 */
	public void mergeSkillGraphDatabase(List<String> listSkill, String person, Document document){

		//--REQUEST----------
		StatementResult result = MergePersonAndDocumentAnsSkills(person,document,listSkill);
		if(result != null) logger.info(result.toString());
		
		//create reverse relationships and delete link on the same node
		run(" MATCH (co)-[l:LINK]->(sk) MERGE (sk)-[:LINK {rate:l.rate}]->(co) ");
		
	}
	
	/**Merge person, document & skills.
	 * 
	 * Create node : Document d and person p and create the relation "WRITE" between p and d.
	 * Merges need to be managed by range of 10 for performance reason:
	 * 
	 * For each Skills: create skill node and create relation COME_FROM between the skill and the document,
	 * with a rate = 1 if it's a new range or +1 if the relation is already created.
	 * Then, create relation KNOWS from person and skills
	 * with a rate = 1 if it's a new range or +1 if the relation is already created.
	 *
	 * the rate will be used to know the stronger links
	 * 
	 * To finish, create reverse relationships and delete link on the same node
	 * 
	 * @param person : person who KNOWS this skills
	 * @param document : document where the skills COME FROM.
	 * @param listSkill : list of skills to merge (create if necessary)
	 * @return result of the neo4j request
	 */
	private StatementResult MergePersonAndDocumentAnsSkills(String email,Document document, List<String> listSkill){
		logger.debug(email);
		logger.debug(document.getName());
		logger.debug(listSkill.size());
		String request = "";
		StatementResult result = null;
		//TODO: externalize the range value
		int range = 10;
		int i = 0;
		int j= 0;
		while(i < listSkill.size()){
			//Create Document d and person p 
			//Create relation WRITE between p and d.
			request =
				 "MERGE(d:Document{url:\""+document.getUrl()+"\"}) "
				+"MERGE(p:Person{email:\""+email+"\"}) "
				+"MERGE (p)-[w:WRITE]->(d) ";
			j=0;
			//For each Skills
			while((i< listSkill.size()) && (j < range)){
				request =request 
					//create skill
					+"MERGE (s"+i+":Skill {name: \""+listSkill.get(i)+"\"}) "
					// create relation COME_FROM between the skill and the document,
					// with a rate = 1 if it's a new range or +1 if the relation is already created.
					// the rate will be used to know the stronger links
					+"MERGE (s"+i+")-[c"+i+":COME_FROM]->(d) "
					+"ON CREATE SET c"+i+".rate = 1 "
					+"ON MATCH SET c"+i+".rate = coalesce(c"+i+".rate,0)+1 "
					// Create relation KNOWS from person and skills
					// with a rate = 1 if it's a new range or +1 if the relation is already created.
					// the rate will be used to know the stronger links
					+"MERGE (p)-[k"+i+":KNOWS]->(s"+i+") "
					+"ON CREATE SET k"+i+".rate = 1 "
					+"ON MATCH SET k"+i+".rate = coalesce(k"+i+".rate,0)+1 ";	
				i++;
				j++;
			}	
			request =request + "RETURN ID(p),ID(d)";	
			logger.debug(request);		
			result = run(request);
			
			//create reverse relationships and delete link on the same node
			//TODO: optimize and delete this request.
			if(result != null)
				run(" MATCH (co)-[l:LINK]->(sk) MERGE (sk)-[:LINK {rate:l.rate}]->(co) ");
		}
		return result;
	}
}
