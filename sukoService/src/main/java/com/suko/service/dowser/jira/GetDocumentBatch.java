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
public class GetDocumentBatch extends DowserBatch{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GetDocumentBatch(String name,String language, String cronExpression) {
		super(name,language,cronExpression);
	}
	
	//METHOD TO OVERWRITE
	public List<Document> initIterator(Date lastRunDate, int maxResult){
		List<Document> items = JiraNormaliser.getJiras(this.language,lastRunDate,maxResult);
		return items;
	}
	
	public String itemProcessor(Document doc){
		return JiraNormaliser.createOrUpdateDocumentTodo(doc);
	}

}
