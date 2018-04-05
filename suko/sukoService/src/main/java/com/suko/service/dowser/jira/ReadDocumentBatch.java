/**
 * 
 */
package com.suko.service.dowser.jira;

import java.util.Date;
import java.util.List;

import com.suko.DAO.business.Document;
import com.suko.service.dowser.DowserBatch;

/**
 * @author Jeremy
 *
 */
public class ReadDocumentBatch extends DowserBatch{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReadDocumentBatch(String name, String language, String cronExpression) {
		super(name,language,cronExpression);
	}
	
	//METHOD TO OVERWRITE
	public List<Document> initIterator(Date lastRunDate,int maxResult) throws Exception{
		return JiraNormaliser.getAllsDocumentTypeJiraToIntegrate(maxResult);
	}
	
	public String itemProcessor(Document doc) throws Exception{
		return JiraNormaliser.integrateJiraIssue(doc);
	}
}
