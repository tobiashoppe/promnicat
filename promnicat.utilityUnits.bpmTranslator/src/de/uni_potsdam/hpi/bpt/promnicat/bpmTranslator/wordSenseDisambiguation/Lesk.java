package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordSenseDisambiguation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.refactoring.StringOperations;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordNet.WordNet;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.VerbSynset;

/**
 * This class supports operations based on the Lesk algorithm and WordNet.
 * For example, it disambiguates a word of a label of a business process model
 * by computing the overlap of the synsets of the word with the context of the word,
 * i.e. all the other words of the labels of the business process model.
 * For further details and information read the documentation of the methods of this class. 
 * 
 * @author Kimon Batoulis
 *
 */
//TODO allow word to be used multiple times in overlap computation?
public class Lesk {
	
	private StringOperations stringOperator;
	private WordNet wordNet;
	private String sourceLanguage;
	private String targetLanguage;
	private StopWords stopWordsGetter;
	
	/**
	 * The Lesk constructor.
	 * 
	 * @param sourceLanguage	the source language of the translation
	 * @param targetLanguage	the target language of the translation
	 */
	public Lesk(String sourceLanguage, String targetLanguage) {
		stringOperator = new StringOperations();
		wordNet = new WordNet();
		stopWordsGetter = new StopWords();
		setSourceLanguage(sourceLanguage);
		setTargetLanguage(targetLanguage);	
	}
	
	/**
	 * Retrieves and returns all the appropriate WordNet synsets of a word given its part-of-speech (POS).
	 * For example, if a word can be a noun and a verb, but is used as a noun in this case,
	 * only the noun synsets are retrieved.
	 * Furthermore, all the related synsets of the synsets of the word are retrieved.
	 * Related synsets in this case are hypernyms, hyponyms and attributes of the synsets
	 * (see http://lyle.smu.edu/~tspell/jaws/doc/overview-summary.html).
	 * This results in a synsets tree, which is represented as a HashMap, mapping from all the
	 * synsets of the word to all the related synsets of the synsets.
	 * 
	 * @param word	the word for which the synsets tree is to be computed
	 * @param pos	the part-of-speech (POS) of the word
	 * @return		the synsets tree of the word
	 */
	private HashMap<Synset, ArrayList<Synset[]>> getSynsetsTree(String word, String pos) {
		HashMap<Synset, ArrayList<Synset[]>> synsetsTree = new HashMap<Synset, ArrayList<Synset[]>>();
		Synset[] synsets = wordNet.synsetsOf(word, pos);
		if (synsets.length == 0)
			return synsetsTree;
		if (pos.equals("noun"))
			relatedNounSynsets(synsetsTree, synsets);
		else if (pos.equals("verb"))
			relatedVerbSynsets(synsetsTree, synsets);
			
		return synsetsTree;
	}
	
	/**
	 * Constructs the synset tree of a noun by retrieving all related synsets 
	 * of the synsets of a word.
	 * In this case, related synsets are hypernyms, hyponyms and attributes synsets.
	 * 
	 * @param synsetsTree	the synsets tree to be constructed
	 * @param synsets		the synsets of the word for which the synsets tree is to be constructed
	 */
	private void relatedNounSynsets(HashMap<Synset, ArrayList<Synset[]>> synsetsTree, Synset[] synsets) {
		for (Synset synset : synsets) {
			ArrayList<Synset[]> relatedSynsets = new ArrayList<Synset[]>();
			relatedSynsets.add(wordNet.getHypernyms((NounSynset) synset));
			relatedSynsets.add(wordNet.getHyponyms((NounSynset) synset));
			relatedSynsets.add(wordNet.getAttributes((NounSynset) synset));
			synsetsTree.put(synset, relatedSynsets);
		}
	}
	
	/**
	 * Constructs the synset tree of a verb by retrieving all related synsets 
	 * of the synsets of a word.
	 * In this case, related synsets are hypernyms, entailments and outcomes synsets.
	 * 
	 * @param synsetsTree	the synsets tree to be constructed
	 * @param synsets		the synsets of the word for which the synsets tree is to be constructed
	 */
	private void relatedVerbSynsets(HashMap<Synset, ArrayList<Synset[]>> synsetsTree, Synset[] synsets) {
		for (Synset synset : synsets) {
			ArrayList<Synset[]> relatedSynsets = new ArrayList<Synset[]>();
			relatedSynsets.add(wordNet.getHypernyms((VerbSynset) synset));
			relatedSynsets.add(wordNet.getEntailments((VerbSynset) synset));
			relatedSynsets.add(wordNet.getOutcomes((VerbSynset) synset));
			synsetsTree.put(synset, relatedSynsets);
		}
	}
	
	/**
	 * Disambiguates a word based on its part-of-speech (POS) and its context based on
	 * the Lesk algorithm. It then returns the definition of the most appropriate
	 * WordNet synset.
	 * 
	 * @param word		the word to be disambiguated
	 * @param pos		the POS of the word
	 * @param context	the context of the word
	 * @return			the definition of the most appropriate WordNet synset of the word
	 */
	public String disambiguate(String word, String pos, String context) {
		HashMap<Synset, ArrayList<Synset[]>> synsetsTree = getSynsetsTree(word, pos);
		// function words have no WordNet synsets
		if (synsetsTree.size() == 0)
			return "";
		String bestSense = wordNet.getDefinition(wordNet.synsetsOf(word, pos)[0]);		
		int maxOverlap = 0;
		int overlap = 0;
		
		for (Map.Entry<Synset, ArrayList<Synset[]>> synsetsBranch : synsetsTree.entrySet()) {
			Synset baseSynset = synsetsBranch.getKey();
			overlap = computeOverlap(baseSynset, context);
			for (Synset[] relatedSynsets : synsetsBranch.getValue()) {
				for (Synset synset : relatedSynsets) {
					overlap += computeOverlap(synset, context);
				}
			}
			if (overlap > maxOverlap) {
				maxOverlap = overlap;
				bestSense = wordNet.getDefinition(baseSynset);
			}
		}
		return bestSense;
	}
	
	/**
	 * Computes and return the overlap of a synset of a word and its context.
	 * The overlap is the number of words the synset and the context have in common (excluding stop words).
	 * Here, the synset is supposed to be composed of its word form, definition, and usage examples
	 * so that for each of those three the overlap with the context is determined and then added up.
	 * 
	 * @param synset	the synset for which the overlap is to be computed
	 * @param context	the context of the synset
	 * @return			the overlap of a synset and its context
	 */
	public int computeOverlap(Synset synset, String context) {
		int overlap = 0;
		ArrayList<String> checkedWords = new ArrayList<String>();
		ArrayList<String> stopWords = stopWordsGetter.getStopWords(getSourceLanguage());
		overlap += computeWordFormOverlap(wordNet.getWordForms(synset), context, checkedWords, stopWords);
		overlap += computeDefinitionOverlap(wordNet.getDefinition(synset), context, checkedWords, stopWords);
		overlap += computeExamplesOverlap(wordNet.getUsageExamples(synset), context, checkedWords, stopWords);
		return overlap;
	}
	
	/**
	 * Computes the overlap of the word forms of a synset and its context.
	 * 
	 * @param wordForms		the word forms of the synset
	 * @param context		the context
	 * @param checkedWords	the words that already have been checked
	 * 						(ensures that overlapping words are only counted once)
	 * @param stopWords		the stop words of the language in question
	 * 						(function words do not contain significant meaning)
	 * @return				the overlap of the word forms and the context
	 */
	public int computeWordFormOverlap(String[] wordForms, String context,
			ArrayList<String> checkedWords, ArrayList<String> stopWords) {
		int overlap = 0;
		for (String wordForm : wordForms) {
			overlap += longestCommonWordSequence(wordForm, context, checkedWords, stopWords);
		}
		return overlap;
	}
	
	/**
	 * Computes the overlap of the usage examples of a synset and its context.
	 * 
	 * @param usageExamples		the usage examples of the synset
	 * @param context			the context
	 * @param checkedWords		the words that already have been checked
	 * 							(ensures that overlapping words are only counted once)
	 * @param stopWords			the stop words of the language in question
	 * 							(function words do not contain significant meaning)
	 * @return					the overlap of the word forms and the context
	 */
	public int computeExamplesOverlap(String[] usageExamples, String context,
			ArrayList<String> checkedWords, ArrayList<String> stopWords) {
		int overlap = 0;
		for (String usageExample : usageExamples) {
			overlap += longestCommonWordSequence(usageExample, context, checkedWords, stopWords);
		}
		return overlap;
	}
	
	/**
	 * Computes the overlap of the definition of a synset and its context.
	 * 
	 * @param definition	the definition of the synset
	 * @param context		the context
	 * @param checkedWords	the words that already have been checked
	 * 						(ensures that overlapping words are only counted once)
	 * @param stopWords		the stop words of the language in question
	 * 						(function words do not contain significant meaning)
	 * @return				the overlap of the word forms and the context
	 */
	public int computeDefinitionOverlap(String definition, String context,
			ArrayList<String> checkedWords, ArrayList<String> stopWords) {
		return longestCommonWordSequence(definition, context, checkedWords, stopWords);
	}
	
	/**
	 * Computes and returns the best translation of all the translations in the first argument by
	 * computing the overlap with all the translations in the second argument.
	 * The first argument is a list of translations of a label of a business process model.
	 * The second argument is a list of translations of WordNet definitions of all the words of all the labels
	 * of the business process model.
	 * 
	 * @param translationCandidates1	a list of translations of a label
	 * @param translationCandidates2	a list of translations of WordNet definitions of all labels
	 * @return							the best translation of a label
	 */
	public String computeBestTranslation(ArrayList<String> translationCandidates1, ArrayList<String> translationCandidates2) {
		ArrayList<String> stopWords = stopWordsGetter.getStopWords(getTargetLanguage());
		String bestTranslation = "";
		int maxOverlap = 0;
		int overlap = 0;
		
		for (String translationCandidate1 : translationCandidates1) {
			for (String translationCandidate2 : translationCandidates2) {
				overlap += computeSentenceOverlap(translationCandidate1, translationCandidate2, stopWords);
			}
			if (overlap > maxOverlap) {
				maxOverlap = overlap;
				bestTranslation = translationCandidate1;
			}
			overlap = 0;
		}
		return bestTranslation;	
	}
	
	/**
	 * Computes and returns the overlap of two sentences.
	 * The overlap is the number of words the two sentences have in common (excluding stop words).
	 * 
	 * @param firstSentence		the first sentence
	 * @param secondSentence	the second sentence
	 * @param stopWords			the stop words (function words) to be ignored
	 * @return					the overlap of the two sentences
	 */
	public int computeSentenceOverlap(String firstSentence, String secondSentence, ArrayList<String> stopWords) {
		ArrayList<String> checkedWords = new ArrayList<String>();
		return longestCommonWordSequence(firstSentence, secondSentence, checkedWords, stopWords);
	}
	
	/**
	 * Computes and returns the overlap of two strings.
	 * The overlap value is the square of the length of the overlap of successive words of the string.
	 * 
	 * @param firstString 	the first string
	 * @param secondString 	the seconds string
	 * @param checkedWords	the words that already have been checked
	 * 						(ensures that overlapping words are only counted once)
	 * @return 				the overlap
	 */
	public int longestCommonWordSequence(String firstString, String secondString,
			ArrayList<String> checkedWords, ArrayList<String> stopWords) {
		String[] firstStringWords = stringOperator.tokenize(firstString);
		String[] secondStringWords = stringOperator.tokenize(secondString);
		int overlap = 0;
		for (int i = 0; i < firstStringWords.length; i++) {
			for (int j = 0; j < secondStringWords.length; j++) {
				int x = 0;
				// set to true if stop words are allowed in word sequences
				boolean stopWordsOnly = false;
				while (firstStringWords[i+x].equalsIgnoreCase(secondStringWords[j+x])
						&& !checkedWords.contains(firstStringWords[i+x])
						&& !stopWords.contains(firstStringWords[i+x])) {
					checkedWords.add(firstStringWords[i+x]);
					if (!stopWords.contains(firstStringWords[i+x])) stopWordsOnly = false;
					x++;
					if (((i + x) >= firstStringWords.length) || ((j + x) >= secondStringWords.length)) break;
				}
				if (!stopWordsOnly)
					overlap += Math.pow(x, 2);
			}
		}
		return overlap;
	}
	
	/**
	 * Gets the source Language.
	 * 
	 * @return	the source language
	 */
	public String getSourceLanguage() {
		return sourceLanguage;
	}

	/**
	 * Sets the source language.
	 * 
	 * @param sourceLanguage	the source language
	 */
	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	/**
	 * Gets the target language.
	 * 
	 * @return	the target language
	 */
	public String getTargetLanguage() {
		return targetLanguage;
	}

	/**
	 * Sets the target language.
	 * 
	 * @param targetLanguage	the target language
	 */
	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}
}
