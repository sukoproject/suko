package com.suko.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.suko.DAO.business.Person;
import com.suko.DAO.business.Result;
import com.suko.DAO.business.Word;
import com.suko.DAO.dataBase.PersonGraphDB;
import com.suko.DAO.dataBase.SkillsGraphDB;
import com.suko.DAO.dataBase.WordGraphDB;
import com.suko.service.analyzer.TextAnalyzer;

public class Search {

	private static final Logger logger = LogManager.getLogger(Search.class);
	private static final String NB_RESULTS = "5";
	
	
	/**Search a list of persons able to answer to a question. The language can be set or will be detected automatically (using google API)
	 * It's possible de add complementary skills to precise the context.
	 * 
	 * @param question : String to analyse
	 * @param language : Language (optional)
	 * @param skillsValidated : complementary skills
	 * @return Result container : questions, language, skills, persons found.
	 * @exception
	 * **/
	public static Result SearchPerson(String question, String language, List<String> skillsValidated){
		//DETACTE LANGUAGE
		String detectedlanguage = TextAnalyzer.detectLanguage(question);
		if(detectedlanguage!=null){
			language=detectedlanguage;
		}
		
		//Manage skillsValidated (complementary skills validated by user)
		List<String> listSkill=new ArrayList<String>();
		if(skillsValidated!=null){
			for(String skill: skillsValidated){
				if(skill != null){
					Word w= new Word(skill, language);
					WordGraphDB wgd = new WordGraphDB();//Machine Learning : these word are "validated". It means to take in account in the reading of doc.			
					w.setWordform(Word.ALL_WORD_FROM.VALIDATED.toString());
					wgd.mergeWordGraphDatabase(w);
					listSkill.add(w.getValue());// add this word as skill to find person.
				}
			}
		}else{
			listSkill.addAll(TextAnalyzer.getKeysWords(question, language));//Analyse text to get skills.
		}
		
		///START SEARCH PERSON
		int nBresult = Integer.parseInt(Admin.getParam("Search","Person","Nb_result","Limit of persons returned per wave",NB_RESULTS).getValue());
		Result result = new Result(); //result container
		//Analyse text to get list of skills / keywords
		//for each skill search X persons (with rate)
	    PersonGraphDB personGraphDB = new PersonGraphDB(); //init graphDB instance
		if(listSkill !=null && listSkill.size()>0){
			List<Person> personsList = new ArrayList<Person>();
			personsList = personGraphDB.searchPersonGraphDatabase(listSkill,0,nBresult);
			for(Person p : personsList){
				p.setSkills(searchSkillsOfPerson(p.getEmail(), 15));//return the skills of the person TODO:make it optional
			}
			result.setPerfectMatchedPersons(personsList);
			result.setSkills(listSkill);
		}else{
			logger.warn("No skills found");
		}
	    personGraphDB.close();//close graph DB instance
	    result.setQuestion(question);//add question to the result container
	    result.setLanguage(language);//add language detected
		return result;
	}
	
	/**List all skills of one Person (email is used ad ID)
	 * @param email : Id of the person
	 * @param nbResult : nb skills to get
	 * @return Map with the skill and the ranking.
	 */
	public static Map<String,Integer> searchSkillsOfPerson(String email,int nbResult){
		SkillsGraphDB sgb = new SkillsGraphDB();
		Map<String, Integer> result = sgb.searchSkillsOfPersonDataBase(email, nbResult);
		sgb.close();
		return result; 
	}
}
