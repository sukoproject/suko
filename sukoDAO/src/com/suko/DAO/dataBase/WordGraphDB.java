/**
 * 
 */
package com.suko.DAO.dataBase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import com.suko.DAO.business.Word;

/**
 * @author Jeremy
 *
 */
public class WordGraphDB extends GraphDB {

	private static final Logger logger = LogManager.getLogger(WordGraphDB.class);
	
	public WordGraphDB(){
		super();
	}

	public void mergeWordGraphDatabase(Word w){
		run(" MERGE(w:Word{value:\""+w.getValue()+"\",language:\""+w.getLanguage()+"\"})"
				+ " ON CREATE SET w.wordform=\""+w.getWordform()	+"\""
				+ " ON MATCH SET w.wordform=\""+w.getWordform()	+"\"");
	}

	public Word searchWordGraphDataBase(Word word) {
		StringBuilder request =  new StringBuilder(" MATCH(w:Word) where ");
		if(word.getValue()!=null && !word.getValue().isEmpty()){
			request.append("w.value = \"");
			request.append(word.getValue());
			request.append("\" ");
		}
		if(word.getWordform()!=null && !word.getWordform().isEmpty()){
			request.append("AND w.wordform = \"");
			request.append(word.getWordform());
			request.append("\" ");
		}
		if(word.getLanguage()!=null && !word.getLanguage().isEmpty()){
			request.append("AND w.language = \"");
			request.append(word.getLanguage());
			request.append("\" ");
		}
		request.append(" RETURN w.value,w.wordform,w.language ");
		StatementResult result = run(request.toString());
		if(result != null){
			if(result.hasNext()){
				Record line = result.next();
				logger.debug(line.toString());
				word.setValue(line.get(0).toString().replace("\"",""));
				word.setWordform(line.get(1).toString().replace("\"",""));
				word.setLanguage(line.get(2).toString().replace("\"",""));
				return word;
			}
		}
		return word;
	}
}
