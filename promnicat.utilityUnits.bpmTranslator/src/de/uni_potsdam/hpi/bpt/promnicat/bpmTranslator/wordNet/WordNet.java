package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordNet;

import java.util.HashMap;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNet {

	private WordNetDatabase database;
	private HashMap<String, SynsetType> posMap;
	
	public WordNet() {
		System.setProperty("wordnet.database.dir", "../promnicat.utilityUnits.bpmTranslator/resources/dict/");
		database = WordNetDatabase.getFileInstance();
		posMap = new HashMap<String, SynsetType>();
		populatePOSHashMap(posMap);
	}
	
	public Synset[] synsetsOf(String word, String pos) {
		pos = pos.toLowerCase();
		if (!posMap.containsKey(pos))
			throw new IllegalArgumentException("illegal POS");
		return database.getSynsets(word, posMap.get(pos));
	}
	
	public String[] getWordForms(Synset synset) {
		return synset.getWordForms();
	}
	
	public String[] getUsageExamples(Synset synset) {
		return synset.getUsageExamples();
	}
	
	public String getDefinition(Synset synset) {
		return synset.getDefinition();
	}
	
	public NounSynset[] getHypernyms(NounSynset synset) {
		return synset.getHypernyms();
	}
	
	public NounSynset[] getHyponyms(NounSynset synset) {
		return synset.getHyponyms();
	}
	
	public AdjectiveSynset[] getAttributes(NounSynset synset) {
		return synset.getAttributes();
	}
	
	public VerbSynset[] getEntailments(VerbSynset synset) {
		return synset.getEntailments();
	}
	
	public VerbSynset[] getHypernyms(VerbSynset synset) {
		return synset.getHypernyms();
	}

	public VerbSynset[] getOutcomes(VerbSynset synset) {
		return synset.getOutcomes();
	}
	
	private void populatePOSHashMap(HashMap<String, SynsetType> posMap) {
		posMap.put("noun", SynsetType.NOUN);
		posMap.put("verb", SynsetType.VERB);
		posMap.put("adjective", SynsetType.ADJECTIVE);
		posMap.put("adverb", SynsetType.ADVERB);
	}

}