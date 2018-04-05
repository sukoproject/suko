/**
 * 
 */
package com.suko.DAO.dataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;

import com.suko.DAO.business.Document;

/**
 * @author Jeremy
 *
 */
public class DocumentGraphDB extends GraphDB {

	private static final Logger logger = LogManager.getLogger(DocumentGraphDB.class);
	public static final String TODO = "TODO";
	
	public DocumentGraphDB(){
		super();
	}

	public Date getLastUpdateInSuko(String nameBatch){
		String request = "MATCH (d:Document{type:'[TYPE]'}) return d.dateUpdated as DATEU ORDER BY DATEU DESC LIMIT 50";
		request = request.replace("[TYPE]",nameBatch);
		StatementResult result = run(request);
		if(result!=null){
			if(result.hasNext()){
				try{
					SimpleDateFormat sdf = new SimpleDateFormat(Document.DATE_FORMAT);
					Record line = result.next();
					String dateU = line.get(0).toString().replaceAll("\"", "");			
					Date d = sdf.parse(dateU);
					return d;
				}catch(NoSuchRecordException e){
					return new Date(0);
				}catch (Exception e){
					return new Date(0);
				}
			}
		}
		return new Date(0);
	}
	public void mergeDocGraphDatabase(Document doc){
		SimpleDateFormat sdf = new SimpleDateFormat(Document.DATE_FORMAT);
		String dateUpdateInSuko= sdf.format(doc.getLastDowsedInSuko());
		String dateUpdate = sdf.format(doc.getUpdate());
		run(" MERGE(d:Document{url:\""+doc.getUrl()
							+"\" ,language:\""+doc.getLanguage()
							+"\" ,name:\""+doc.getName()
							+"\" ,type:\""+doc.getType()+"\"}) "
							+"ON CREATE SET d.dateUpdated =\""+dateUpdate+"\", d.createdInSuko =\""+dateUpdateInSuko+"\", d.lastUpdateInSuko=\""+dateUpdateInSuko+"\", d.status=\""+doc.getStatus()+"\" "
							+"ON MATCH SET d.dateUpdated =\""+dateUpdate+"\", d.lastUpdateInSuko=\""+dateUpdateInSuko+"\", d.status=\""+doc.getStatus()+"\" "
				);
	}

	public List<Document> searchDocumentsGraphDataBase(Document ref, int maxResult) throws ParseException {
		List<Document> results = new ArrayList<Document>();
		StringBuilder request =  new StringBuilder(" MATCH(d:Document) where 1=1 ");
		if(ref.getName()!=null && !ref.getName().isEmpty()){
			request.append("AND d.name = \"");
			request.append(ref.getName());
			request.append("\" ");
		}
		if(ref.getStatus()!=null && !ref.getStatus().isEmpty()){
			request.append("AND d.status = \"");
			request.append(ref.getStatus());
			request.append("\" ");
		}
		if(ref.getLanguage()!=null && !ref.getLanguage().isEmpty()){
			request.append("AND d.language = \"");
			request.append(ref.getLanguage());
			request.append("\" ");
		}
		if(ref.getType()!=null && !ref.getType().isEmpty()){
			request.append("AND d.type = \"");
			request.append(ref.getType());
			request.append("\" ");
		}
		if(ref.getUrl()!=null && !ref.getUrl().isEmpty()){
			request.append("AND d.url = \"");
			request.append(ref.getUrl());
			request.append("\" ");
		}
		request.append(" RETURN d.name,d.status,d.language,d.type,d.dateUpdated, d.lastUpdateInSuko,d.url LIMIT " + maxResult);
		StatementResult result = run(request.toString());
		if(result != null){
			while(result.hasNext()){
				Record line = result.next();
				logger.debug(line.toString());
				Document res = new Document();
				res.setName(line.get(0).toString().replace("\"",""));
				res.setStatus(line.get(1).toString().replace("\"",""));
				res.setLanguage(line.get(2).toString().replace("\"",""));
				res.setType(line.get(3).toString().replace("\"",""));
				SimpleDateFormat sdf = new SimpleDateFormat(Document.DATE_FORMAT);
				res.setLastDowsedInSuko(sdf.parse(line.get(4).toString().replace("\"","")));
				res.setUpdate(sdf.parse(line.get(5).toString().replace("\"","")));
				res.setUrl(line.get(6).toString().replace("\"",""));
				results.add(res);
			}
		}
		return results;
	}
}
