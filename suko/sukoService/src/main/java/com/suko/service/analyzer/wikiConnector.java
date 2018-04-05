package com.suko.service.analyzer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.suko.DAO.business.Word;
import com.suko.service.Admin;
import com.suko.service.normaliser.HtmlParsing;

public class wikiConnector {
	
	static String linkWikiEN = Admin.getParam("Analyser", "Wiki","EN_url", "wiki url to use to get the wordform in english", "https://en.wiktionary.org/wiki/").getValue();
	static String linkWikiFR = Admin.getParam("Analyser", "Wiki","FR_url", "wiki url to use to get the wordform in french", "https://fr.wiktionary.org/wiki/").getValue();
	static String docSelectedFR = Admin.getParam("Analyser", "Wiki","FR_CSS_Document_Selector", "CSS doc to select in the Wiki fr", ".titredef").getValue();
	private static final Logger logger = LogManager.getLogger(wikiConnector.class);
	
	public static Word getWordFormFromWiki(String wordToAnalyse, String language){
		Word word = new Word(wordToAnalyse,Word.ALL_WORD_FROM.UNKNOWN.toString(),language);
		String linkWiki=linkWikiEN;
		switch(language){
			case("FR"): linkWiki = linkWikiFR;
		}
		logger.debug(linkWiki + wordToAnalyse);
		
		Document doc = HtmlParsing.getFromUrl(linkWiki + wordToAnalyse);
		if (doc != null) {
			Elements results;
			Element result;
			
			switch(language){
				case("FR"):
					results = doc.select(".titredef");				
					if (results.equals(null) || results.size() == 0 || results.first().equals(null)) {
						return word;
					} else {
						word.setWordform(results.first().text());
						return word;
					}
				case("EN"):
					results = doc.getElementsByClass("mw-headline");
					int i=0;
					if (results.equals(null) || results.size() == 0 || results.first().equals(null)) {
						return word;
					} else {
						boolean isWordformCheck1 = false;
						boolean isWordformCheck2 = true;
						while(i<results.size()){
							result = results.get(i);
							String wordForm = result.text();
							if(wordForm.equals("Pronunciation") || wordForm.equals("Alternative forms")){
								isWordformCheck2=false;
							}
							if (isWordformCheck1 & isWordformCheck2 ){																			
								word.setWordform(wordForm);
								return word;
							}
							if(wordForm.startsWith("Etymology")){
								isWordformCheck1=true;
							}
							isWordformCheck2=true;
							i++;
						}								
					}
					break;
			}			
		} 
		return word;
	
	}
	

}
