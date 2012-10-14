package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.stanfordTagger;

import java.io.IOException;
import java.util.ArrayList;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagger {
	
	MaxentTagger tagger;
	ArrayList<String> nounTags;
	ArrayList<String> verbTags;
	ArrayList<String> adjectiveTags;
	ArrayList<String> adverbTags;
	
	public Tagger(String modelFile) {
		try {
			tagger = new MaxentTagger(modelFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		populateNounTagsList();
		populateVerbTagsList();
		populateAdjectiveTagsList();
		populateAdverbTagsList();
	}
	
	private void populateNounTagsList() {
		nounTags = new ArrayList<String>();
		nounTags.add("NN");
		nounTags.add("NNS");
		nounTags.add("NE");
	}

	private void populateVerbTagsList() {
		verbTags = new ArrayList<String>();
		verbTags.add("VB");
		verbTags.add("VVFIN");
		verbTags.add("VVIMP");
		verbTags.add("VVINF");
		verbTags.add("VVIZU");
		verbTags.add("VVPP");
	}

	private void populateAdjectiveTagsList() {
		adjectiveTags = new ArrayList<String>();
		adjectiveTags.add("JJ");
		adjectiveTags.add("ADJA");
		adjectiveTags.add("ADJD");
	}

	private void populateAdverbTagsList() {
		adverbTags = new ArrayList<String>();
		adverbTags.add("ADV");
		adverbTags.add("RB");
	}

	public String tagWordInText(int wordIndex, String text) {
		String taggedText = tagWordsInText(text);
		String[] taggedWords = taggedText.split(" ");
		String[] wordAndTag = taggedWords[wordIndex].split("/");
		return tagToString(wordAndTag[1]);
	}
	
	private String tagToString(String tag) {
		if (nounTags.contains(tag))
			return "noun";
		else if (verbTags.contains(tag))
			return "verb";
		else if (adjectiveTags.contains(tag))
			return "adjective";
		else if (adverbTags.contains(tag))
			return "adverb";
		return "";
	}
	
	private String tagWordsInText(String text) {
		return tagger.tagString(text);
	}
}