package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordSenseDisambiguation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class StopWords {
	
	private HashMap<String, String> supportedLanguages;
	private HashMap<String, ArrayList<String>> stopList;
	
	public StopWords() {
		stopList = new HashMap<String, ArrayList<String>>();
		supportedLanguages = new HashMap<String, String>();
		populateSupportedLanguagesArray(supportedLanguages);
		populateStopList(stopList);
	}

	public ArrayList<String> getStopWords(String language) {
		language = language.toLowerCase();
		if (!supportedLanguages.containsKey(language))
			throw new IllegalArgumentException("illegal language");
		if (!stopList.get(language).isEmpty())
			return stopList.get(language);
		String csv = readStopListFromFile(supportedLanguages.get(language));
		stopList.put(language, convertCSVToArrayList(csv));
		return stopList.get(language);
	}
	
	private ArrayList<String> convertCSVToArrayList(String csv) {
		ArrayList<String> stopWords = new ArrayList<String>();
		String[] csvArray = csv.split(",");
		for (String stopWord : csvArray)
			stopWords.add(stopWord);
		return stopWords;
	}

	private String readStopListFromFile(String fileName) {
		String csv = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			try {
				csv = in.readLine();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return csv;
	}
	
	private void populateSupportedLanguagesArray(HashMap<String, String> supportedLanguages) {
		String prefix = "lib/stopWords/";
		supportedLanguages.put("german", prefix + "germanStopWords");
		supportedLanguages.put("english", prefix +  "englishStopWords");
		supportedLanguages.put("test", prefix +  "stopWordsTest");
	}
	
	private void populateStopList(HashMap<String, ArrayList<String>> stopList) {	
		for (String language : supportedLanguages.keySet())
			stopList.put(language, new ArrayList<String>());
	}
	
}
