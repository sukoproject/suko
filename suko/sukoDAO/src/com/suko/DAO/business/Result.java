package com.suko.DAO.business;

import java.util.ArrayList;
import java.util.List;

public class Result {
	private List<Document> documents;
	private List<String> skills;
	private List<Person> perfectMatchedPersons;
	private String graphRequest;
	private String question;
	private String language;
	
	public Result() {
		this.documents = new ArrayList<Document>();
		this.skills = new ArrayList<String>();
	}

	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * @return the graphRequest
	 */
	public String getGraphRequest() {
		return graphRequest;
	}

	/**
	 * @param graphRequest the graphRequest to set
	 */
	public void setGraphRequest(String graphRequest) {
		this.graphRequest = graphRequest;
	}

	/**
	 * @return the perfectMatchedPersons
	 */
	public List<Person> getPerfectMatchedPersons() {
		return perfectMatchedPersons;
	}

	/**
	 * @param perfectMatchedPersons the perfectMatchedPersons to set
	 */
	public void setPerfectMatchedPersons(List<Person> perfectMatchedPersons) {
		this.perfectMatchedPersons = perfectMatchedPersons;
	}

	/**
	 * @return the skills
	 */
	public List<String> getSkills() {
		return skills;
	}

	/**
	 * @param skills the skills to set
	 */
	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

}
