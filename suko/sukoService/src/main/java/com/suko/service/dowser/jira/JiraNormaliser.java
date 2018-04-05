package com.suko.service.dowser.jira;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.stream.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.suko.DAO.business.Document;
import com.suko.DAO.business.Parameter;
import com.suko.DAO.business.Document.ALL_STATUS;
import com.suko.DAO.dataBase.DocumentGraphDB;
import com.suko.DAO.dataBase.SkillsGraphDB;
import com.suko.service.Admin;
import com.suko.service.analyzer.TextAnalyzer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * A sample code how to use JRJC library
 *
 * @since v0.1
 */
public class JiraNormaliser {
	
	private static final Logger logger = LogManager.getLogger(JiraNormaliser.class);
	public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DATE_FORMAT_QUERY = "YYYY-MM-dd HH:mm";
	public static final String JIRA_TYPE_NAME = "Jira";
	public static DateFormat format = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
	private static String loginJira =  Admin.getParam("Normaliser", "JIRA","Login", "login to use to connect to JIRA", "").getValue();
	private static String passwordJira =  Admin.getParam("Normaliser", "JIRA","Password", "password to use to connect to JIRA", "").getValue();
	/**
	 * @param url
	 * @param param
	 * @param startAt
	 * @param maxResult
	 * @param d
	 * @return
	 */
	private static JsonParser SearchRestApiJIRA(String request){
		Client client = Client.create();         
        client.addFilter(new HTTPBasicAuthFilter(loginJira, passwordJira));
        if(loginJira.isEmpty() || passwordJira.isEmpty()){
        	loginJira =  Admin.getParam("Normaliser", "JIRA","Login", "login to use to connect to JIRA", "").getValue();
        	passwordJira =  Admin.getParam("Normaliser", "JIRA","Password", "password to use to connect to JIRA", "").getValue();
        }
        logger.debug("login:"+loginJira);
        logger.debug(request.toString());
		WebResource webResource = client.resource(request.toString());            
        ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
        String output = response.getEntity(String.class);
        JsonParser parser = Json.createParser(new StringReader(output));
        return parser;
	}
	
	public static List<Document> getJiras(String language, Date d, int maxResult){
		List<Document> newJiras = new ArrayList<Document>();	
		int total=-1;
		String defautUrl ="https://bugtracker.decathlon.net/bugtracker/rest/api/2/"
				+"search?jql=updated%3E%3D%27[updated]%27+order+by+updated+asc+"
				+ "&startAt=1&maxResults=[maxResult]"
				+ "&fields=key,updated";

        Parameter jiraRestApiSearchQuery=Admin.getParam("Normaliser", "JIRA", "REST_API_Search_Query", "JQL query to search JIRA to integrate ([updated] is a variale "+DATE_FORMAT_QUERY+")",defautUrl);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_QUERY);
        String updateDate = sdf.format(d).replace(" ","%20");
        String request = jiraRestApiSearchQuery.getValue().replace("[updated]",updateDate).replace("[maxResult]", Integer.toString(maxResult));
        
		JsonParser parser = SearchRestApiJIRA(request);
        //TODO : check the version of JIRA to alert if change
		String keyEvent="";
		Date date=null;
		String url=null;
		String jiraName=null;
		try{
        while (parser.hasNext()) {
           JsonParser.Event event = parser.next();      
           switch(event) {
              case START_ARRAY:
              case END_ARRAY:
              case START_OBJECT: 
              case END_OBJECT:
              case VALUE_FALSE:
              case VALUE_NULL:
              case VALUE_TRUE:
            	  break;
              case KEY_NAME:
            	  keyEvent = parser.getString();                  
            	  break;
              case VALUE_STRING:
              case VALUE_NUMBER:
            	 if(keyEvent.equals("total") && total==-1){
            		 total = parser.getInt();
            	 }
            	 if(keyEvent.equals("updated")){
            		 String dateToParse = parser.getString();
        		  		if(dateToParse!=null){
        		  			date = format.parse(dateToParse.substring(0, 19));
        		  		}
            	 }
            	 if(keyEvent.equals("self")){
            		 url=parser.getString();
            	 }
        		 if(keyEvent.equals("key")){
        			keyEvent="";
        			jiraName = parser.getString();        		  		      		  		
        		 }
                 break;
	           }
           	if(jiraName!= null && url!=null && date!=null){
		       	Document doc = new Document(jiraName,new Date(),url,language,JIRA_TYPE_NAME);
		       	doc.setUpdate(date);
		   		logger.debug(jiraName+": "+date+" "+url);
		       	newJiras.add(doc);
		       	date=null;
				url=null;
				jiraName=null;
           	}	        
	    }

		
		} catch (Exception e) {
            logger.error(e.getMessage());
            return newJiras;
        }    
		return newJiras;
	}
	/**
	 * @param jira
	 * @param language 
	 * @return 
	 */
	public static String integrateJiraIssue(Document jira){
		 DocumentGraphDB docGraphDB = new DocumentGraphDB();
		 String urlIssue = Admin.getParam("Normaliser","JIRA","ISSUE_URL","url to JIRA issues ([JIRA] is a variable)","https://bugtracker.decathlon.net/bugtracker/rest/api/2/issue/[JIRA]?fields=body,summary,assignee,reporter,description,comment,updated").getValue();
		 try{
            JsonParser parser = SearchRestApiJIRA( urlIssue.replace("[JIRA]", jira.getName()));            
            //TODO : check the version of JIRA to alert if change
            
            SkillsGraphDB skillGraphDB = new SkillsGraphDB();
            String dateToParse="";
            Date date=new Date(0);
            String keyEvent="";
            String user="";
            String summary="";
            String description="";
            String author="";
            while (parser.hasNext()) {
               JsonParser.Event event = parser.next();
               switch(event) {
                  case START_ARRAY:
                  case END_ARRAY:
                  case START_OBJECT: 
                  case END_OBJECT:
                  case VALUE_FALSE:
                  case VALUE_NULL:
                  case VALUE_TRUE:
                	  break;
                  case KEY_NAME:
                	  keyEvent = parser.getString();                  
                	  break;
                  case VALUE_STRING:
                  case VALUE_NUMBER:
	        		  switch(keyEvent){
	        		  	case("emailAddress"):
	        		  		if(author.equals("")){
	        		  			logger.debug(parser.getString());
	        		  			author=parser.getString();
	        		  		}
	        		  		user = parser.getString();
	        		  		break;
	        		  	case("updated"):
	        		  		dateToParse = parser.getString();
	        		  		if(dateToParse!=null){
	        		  			date = format.parse(dateToParse.substring(0, 19));
	        		  		}
	    		  			break;
	        		  	case("body"):
	        		  		
	        		  		format.setTimeZone(TimeZone.getTimeZone("UTC"));
	        		  		if(date.after(jira.getLastDowsedInSuko())){
	        		  			jira.setUpdate(date);
	        		  			jira.setLastDowsedInSuko(new Date());
		        		  		logger.debug(
		        		  				"["+user+"] "
		        		  				+ jira.getUpdate() +"\n"
		        		  				+ parser.getString());
			        		  	List<String> listSkill = TextAnalyzer.getKeysWords(parser.getString(),jira.getLanguage());	
			        		  	skillGraphDB.mergeSkillGraphDatabase(listSkill ,user,jira);
	        		  		}
	        		  		break;
	        		  	case("summary"):
	        		  		logger.debug(parser.getString());
	        		  		summary = parser.getString();
	        		  		break;
	        		  	case("description"):
	        		  		logger.debug(parser.getString());
	        		  		description = parser.getString();
        		  			break;
	        		  }
                     break;

               }
            }
            List<String> listSkillDesc = TextAnalyzer.getKeysWords(description, jira.getLanguage());	
            skillGraphDB.mergeSkillGraphDatabase(listSkillDesc ,author,jira);
			List<String> listSkillSum = TextAnalyzer.getKeysWords(summary, jira.getLanguage());	
			skillGraphDB.mergeSkillGraphDatabase(listSkillSum ,author,jira);
			skillGraphDB.close();
		  	jira.setLastDowsedInSuko(new Date());
	  		jira.setUpdate(date);
		  	jira.setStatus(Document.ALL_STATUS.DONE);
		  	docGraphDB.mergeDocGraphDatabase(jira);
			docGraphDB.close();
			return jira.getName() + ": "+jira.getStatus();

        } catch (Exception e) {
        	jira.setStatus(Document.ALL_STATUS.ERROR);
		  	docGraphDB.mergeDocGraphDatabase(jira);
			docGraphDB.close();
            logger.error(e.getMessage());
            return jira.getName() + ": " +e.getMessage();
        }
	}

	public static String createOrUpdateDocumentTodo(Document doc) {
		DocumentGraphDB documentGraphDB = new DocumentGraphDB();
		doc.setStatus(ALL_STATUS.TODO);
		documentGraphDB.mergeDocGraphDatabase(doc);
		documentGraphDB.close();
		return doc.getName()+", updated:"+doc.getUpdate();
	}
	
	public static List<Document> getAllsDocumentTypeJiraToIntegrate(int maxResult) throws ParseException {
		DocumentGraphDB documentGraphDB = new DocumentGraphDB();
		Document ref = new Document();
		ref.setType(JIRA_TYPE_NAME);
		ref.setStatus(DocumentGraphDB.TODO);
		List<Document> result = documentGraphDB.searchDocumentsGraphDataBase(ref,maxResult);
		documentGraphDB.close();
		return result;
	}
}