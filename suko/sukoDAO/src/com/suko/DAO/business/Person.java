package com.suko.DAO.business;


import java.util.List;
import java.util.Map;

public class Person {
	private String login;
	private String password;
	private String email;
	private List<String> languages;
	private int rate;
	private Map<String,Integer> skills;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	/**
	 * @return the skills
	 */
	public Map<String,Integer> getSkills() {
		return skills;
	}
	/**
	 * @param skills the skills to set
	 */
	public void setSkills(Map<String,Integer> skills) {
		this.skills = skills;
	}

}
