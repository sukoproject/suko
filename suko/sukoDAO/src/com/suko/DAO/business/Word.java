package com.suko.DAO.business;

public class Word {
	public static enum ALL_WORD_FROM {UNKNOWN,VALIDATED;}
	private String value;
	private String wordform;
	private String language;
	
	public Word(String value, String wordform, String language) {
		this.setValue(value);
		this.setWordform(wordform);
		this.setLanguage(language);
		
	}
	public Word(String value, String language) {
		this.setValue(value);
		this.setLanguage(language);
	}
	/**
	 * @return the wordform
	 */
	public String getWordform() {
		return wordform;
	}
	/**
	 * @param wordform the wordform to set
	 */
	public void setWordform(String wordform) {
		this.wordform = wordform;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
