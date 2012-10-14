package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class StringOperations {
	
	private StringTokenizer tokenizer;
	private String DELIMITERS = " \t\n\r\f-+*#~|_\"',;:./\\()[]<>{}^°=&§%$€?!";
	private HashMap<String,String> specialCharacters;
	
	public StringOperations() {
		populateSpecialCharactersMap();
	}

	public String capitalize(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
	
	//TODO use more sophisticated tokenization
	public String[] tokenize(String string) {
		tokenizer = new StringTokenizer(string, DELIMITERS);
		int tokenCount = tokenizer.countTokens();
		String[] tokens = new String[tokenCount];
		for (int i=0; i < tokenCount; i++)
			tokens[i] = tokenizer.nextToken();
		return tokens;
	}

	public String labelsToString(ArrayList<LinkedHashMap<String, String>> labels) {
		StringBuilder context = new StringBuilder();
		for (HashMap<String, String> label : labels)
			context.append(label.get("label") + " ");
		return context.toString();
	}
	
    private void populateSpecialCharactersMap() {
    	specialCharacters = new HashMap<String, String>();
//    	specialCharacters.put("&quot;", "\"");
    	specialCharacters.put("&amp;", "&");
//    	specialCharacters.put("&Auml;", "Ä");
//    	specialCharacters.put("&auml;", "ä");
//    	specialCharacters.put("&Ouml;", "Ö");
//    	specialCharacters.put("&ouml;", "ö");
//    	specialCharacters.put("&Uuml;", "Ü");
//    	specialCharacters.put("&uuml;", "ü");
	}
    
    public void replaceCharacterCodeByCharacter(ArrayList<String> labels) {
    	for (int i=0; i<labels.size(); i++)
    		for (Map.Entry<String, String> specialCharacter : specialCharacters.entrySet())
    			labels.set(i, labels.get(i).replaceAll(specialCharacter.getKey(), specialCharacter.getValue()));
    }
    
    public void replaceCharacterByCharacterCode(ArrayList<String> labels) {
    	for (int i=0; i<labels.size(); i++)
    		for (Map.Entry<String, String> specialCharacter : specialCharacters.entrySet())
    			labels.set(i, labels.get(i).replaceAll(specialCharacter.getValue(), specialCharacter.getKey()));
    }
}
