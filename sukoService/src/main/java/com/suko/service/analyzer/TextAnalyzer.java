package com.suko.service.analyzer;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.helper.StringUtil;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.common.collect.ImmutableList;
import com.suko.DAO.business.Word;
import com.suko.DAO.dataBase.WordGraphDB;

public class TextAnalyzer {
	
	private static final Logger logger = LogManager.getLogger(TextAnalyzer.class);

	/**
	* Create Google Translate API Service.
	*
	* @return Google Translate Service
	*/
	private static Translate createTranslateService() {
	   return TranslateOptions.newBuilder().build().getService();
	}
	
	public static String detectLanguage(String sourceText) {
		try{
			  Translate translate = createTranslateService();
			  List<Detection> detections = translate.detect(ImmutableList.of(sourceText));
			  System.out.println("Language(s) detected:");
			  if(detections.isEmpty()){
				  return null;
			  }else{
				  //TODO test and delete
				  for (Detection detection : detections) {
					    System.out.printf("\t%s\n", detection);
				  }
				  return detections.get(0).getLanguage();
			  }
		}catch(com.google.cloud.translate.TranslateException e){
			logger.warn(e.getMessage());
			return null;
		}
	}
	
	private static List<String> getWordFormToExclude( String language) {
		List<String> listWordForm = new ArrayList<String>();
		switch(language){
		case("FR"):
			listWordForm.add("Article défini");
			listWordForm.add("Symbole");
			listWordForm.add("Préposition");
			listWordForm.add("Adverbe");
			listWordForm.add("Verbe");
			listWordForm.add("Adjectif numéral");
			listWordForm.add("Forme de verbe");
			listWordForm.add("Forme d’adjectif");
			listWordForm.add("Pronom personnel");
			listWordForm.add("Préposition");
			listWordForm.add("Adverbe");
			listWordForm.add("Verbe");
			listWordForm.add("Conjonction de coordination");
			listWordForm.add("Article partitif");
			listWordForm.add("Forme d’adjectif indéfini");
			listWordForm.add("Conjonction");
			listWordForm.add("Adjectif");
			listWordForm.add("Adjectif possessif");
			listWordForm.add("Adjectif indéfini");
			listWordForm.add("Adjectif démonstratif");
			listWordForm.add("Forme d’adjectif démonstratif");
			listWordForm.add("Forme d’adjectif possessif");
			listWordForm.add("Adverbe interrogatif");
			break;
		case("EN"):
			listWordForm.add("Verb");
			listWordForm.add("Preposition");
			listWordForm.add("Article");
			listWordForm.add("Pronoun");
			listWordForm.add("Adjective");
			listWordForm.add("Conjunction");
			listWordForm.add("Particle");
			listWordForm.add("Determiner");
			listWordForm.add("Letter");
			listWordForm.add("Numeral");
			listWordForm.add("Adverb");
			break;
		}
		return listWordForm;
	}
	
	public static List<String> getKeysWords(String textToAnalyse, String language) {	
		
		textToAnalyse = Normalizer.normalize(textToAnalyse, Normalizer.Form.NFD);
		textToAnalyse = textToAnalyse.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		String[] wordsToAnalyse = textToAnalyse.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
		List<String> languages = new ArrayList<String>(
			    Arrays.asList("EN", "FR"));
		int defaultlanguage = languages.indexOf(language);
		return listSkill(wordsToAnalyse,languages,defaultlanguage);
	}
	
	private static List<String> listSkill(String[] wordsToAnalyse, List<String> languages, int defaultlanguage) {
		List<String> listSkill = new ArrayList<String>();
		int nbWordUnknownInThislanguage = 0;
		if(defaultlanguage<0)defaultlanguage=0;
		List<String> listWordForm = getWordFormToExclude(languages.get(defaultlanguage));
		for (String wordToAnalyse : wordsToAnalyse) {
			if (!StringUtil.isBlank(wordToAnalyse)) {
				Word w = analyseWord(wordToAnalyse,languages.get(defaultlanguage));
				if(!listWordForm.contains(w.getWordform())){
					if(w.getWordform().equals(Word.ALL_WORD_FROM.UNKNOWN.toString())){
						nbWordUnknownInThislanguage++;
					}
					listSkill.add(w.getValue());
				}
			}
			if(languages.size()>1 && wordsToAnalyse.length>1 && nbWordUnknownInThislanguage>wordsToAnalyse.length/2){
				languages.remove(defaultlanguage);
				return listSkill(wordsToAnalyse,languages,0);
			}
		}
		return listSkill;
	}

	public static Word analyseWord(String wordToAnalyse, String language){
		WordGraphDB wordGraphDB = new WordGraphDB();
		Word word = new Word(wordToAnalyse,language);
		word = wordGraphDB.searchWordGraphDataBase(word);
		if(word.getWordform()!=null && !word.getWordform().isEmpty()){
			return word;
		}
		word = wikiConnector.getWordFormFromWiki(wordToAnalyse, language);	
		wordGraphDB.mergeWordGraphDatabase(word);
		return word;		
	}	
}
