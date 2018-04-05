package com.suko.DAO.business;

import java.util.Date;

public class Document {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static enum ALL_STATUS {TODO,DONE,ERROR;}
	
	private String name;
	private Date lastDowsedInSuko;
	private String DowseByInSuko;
	private Date update;
	private String url;
	private String language;
	private String type;
	private String Status;
	
	public Document(String name, Date updateInSuko, String url, String language, String type) {
		this.setName(name);
		this.setLastDowsedInSuko(updateInSuko);
		this.setUrl(url);
		this.setLanguage(language);
		this.setType(type);
		this.setStatus(ALL_STATUS.TODO.toString());
		
	}
	public Document(String value, String language) {
		this.setName(value);
		this.setLanguage(language);
	}
	
	public Document() {
		// TODO Auto-generated constructor stub
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
	/**
	 * @return the update
	 */
	public Date getLastDowsedInSuko() {
		return lastDowsedInSuko;
	}
	/**
	 * @param wordform the update to set
	 */
	public void setLastDowsedInSuko(Date update) {
		this.lastDowsedInSuko = update;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the value to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return Status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(ALL_STATUS status) {
		Status = status.toString();
	}
	public void setStatus(String status) {
		Status = status;
	}
	/**
	 * @return the update
	 */
	public Date getUpdate() {
		return update;
	}
	/**
	 * @param update the update to set
	 */
	public void setUpdate(Date update) {
		this.update = update;
	}
	/**
	 * @return the dowseByInSuko
	 */
	public String getDowseByInSuko() {
		return DowseByInSuko;
	}
	/**
	 * @param dowseByInSuko the dowseByInSuko to set
	 */
	public void setDowseByInSuko(String dowseByInSuko) {
		DowseByInSuko = dowseByInSuko;
	}
}
