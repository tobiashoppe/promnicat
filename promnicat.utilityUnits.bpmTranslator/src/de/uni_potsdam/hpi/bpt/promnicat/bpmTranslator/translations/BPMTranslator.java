package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.translations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.evaluation.Statistics;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.stanfordTagger.Tagger;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordSenseDisambiguation.Lesk;

/**
 * This class is based on the Moses Translator, a statistical machine translation tool (http://www.statmt.org/moses/).
 * It computes the translation of the activity labels of a business process model.
 * First, translation candidates of a label are obtained by Moses. Those candidates are then voted.
 * This is done by disambiguating all words of all labels of the model, translating their best WordNet definition and
 * then computing the overlap of the translations of the label and the translations of the definitions.
 * The translation with the highest overlap with all the translations of all definitions of all words of the label
 * currently being translated is supposed to be the most appropriate one.
 * 
 * @author Kimon Batoulis
 *
 */
public class BPMTranslator extends Translator {
	
	private Lesk lesk;
	private Tagger englishTagger;
	private Tagger germanTagger;
	private String contextString;
	private HashMap<String, ArrayList<String>> wordSenseTranslations;
	
	/**
	 * MosesTranslator constructor.
	 * 
	 * @param sourceLanguage		the source language of the translation
	 * @param targetLanguage 		the target language of the translation
	 * @param trainingCorpus		the bilingual corpus with which the
	 * 								translation system was trained
	 * @param context				the context of the process model to be translated
	 * @param pathToEnglishTagger	the English tagger model file
	 * @param pathToGermanTagger	the German tagger model file
	 * @param numberOfCandidates	the number of translation candidates used for
	 * 								disambiguating possible translations
	 * @param stats					an object to maintain statistics/results of the
	 * 								translation process
	 */
	public BPMTranslator(String sourceLanguage, String targetLanguage,
			String trainingCorpus,
			String context,
			String pathToEnglishTagger, String pathToGermanTagger, 
			int numberOfCandidates, Statistics stats) {
	    super(sourceLanguage, targetLanguage, trainingCorpus, stats);
	    this.contextString = context;
	    NUMBER_OF_CANDIDATES = numberOfCandidates;
	    englishTagger = new Tagger(pathToEnglishTagger);
	    germanTagger = new Tagger(pathToGermanTagger);
	    lesk = new Lesk(this.sourceLanguage, this.targetLanguage); 
	}
	
	/**
	 * Computes the translation of all labels of a business process model. It takes into account the context of the labels, 
	 * i.e. all the other labels of the model. Several translation candidates (e.g. 100) are voted based on the context.
	 *
	 * @param labels 	the labels of the business process model
	 * @return			the translations of the labels
	 */
	public ArrayList<String> translate(ArrayList<LinkedHashMap<String, String>> labels) {
		ArrayList<String> translations = new ArrayList<String>();
		this.labels = labels;
		wordSenseTranslations = wordSenseTranslations();
		for (LinkedHashMap<String, String> label : this.labels)
			translations.add(translate(label));
		saveResults();
		return translations;
	}

	/**
	 * Writes the translations of the labels to a file, creates a BLEU hypothesis and some statistics.
	 */
	private void saveResults() {
		stats.writeTranslationsToFile();
		stats.createBLEUHypothesis();
		stats.printStatistics();
	}
	
	/**
	 * Removes the German honorific form 'Sie' from the translation
	 * and switches the syntactic structure of two word labels from 'noun verb' to
	 * 'verb noun'.
	 * For example, 'schicken Sie Entscheidung' is transformed to 'Entscheidung schicken'.  
	 * 
	 * @param translation	the translation to be refactored
	 * @return				the refactored translation
	 */
	private String refactorTranslation(String translation) {
		translation = translation.replaceAll(" Sie ", " ");
		String[] translationsWords = translation.split(" ");
		if (translationsWords.length == 2) {
			if (germanTagger.tagWordInText(0, translation).equals("verb") &&
					germanTagger.tagWordInText(1, translation).equals("noun")) {
				translation = translationsWords[1] + " " + translationsWords[0];
			}
		}
		return translation;
	}
	
	/**
	 * Delegates the major steps of the translation process.
	 * First, translation candidates are obtained by Moses.
	 * Then, those translations are voted.
	 * Finally, the best translation is refactored and returned.
	 * 
	 * @param label		a label of the model to be translated. The label is represented as a LinkedHashMap,
	 * 					which contains the mapping of the label to a string, and all mappings of the individual
	 * 					words of the label to their part-of-speech (POS)
	 * @return 			the translation of the label
	 */
	private String translate(LinkedHashMap<String, String> label) {
		ArrayList<String> translationCandidates = translationCandidates(label.get("label"), NUMBER_OF_CANDIDATES);
		HashMap<String, Integer> translationVotings = voteTranslations(translationCandidates, label);
		String bestTranslation = bestTranslation(translationCandidates, translationVotings);
		String refactoredTranslation = refactorTranslation(bestTranslation);
		stats.checkIfMosesTranslationUsed(refactoredTranslation, translationCandidates.get(0));
		stats.recordTranslations(label.get("label"), refactoredTranslation, translationCandidates.get(0));
		return refactoredTranslation;
	}
	
	/**
	 * Uses the Lesk class to disambiguate the words of the labels based
	 * on their POS and context. Translation candidates of the best definition are provided
	 * by Moses, saved in an appropriate data structure and returned.
	 * 
	 * @return	translation candidates of the best definition of the words of the labels
	 */
	public HashMap<String, ArrayList<String>> wordSenseTranslations() {
		String wordSense = "";
		HashMap<String, ArrayList<String>> wordSenseTranslations = new HashMap<String, ArrayList<String>>();
		for (HashMap<String,String> label : labels) {
			int wordIndex = 0;
			for (String word : label.keySet()) {
				if (word.equals("label") || word.equals("")) continue;
				ArrayList<String> wordSenseTranslationCandidates = null;
				if (!wordSenseTranslations.containsKey(word + " as " + label.get(word))) {
					if (label.get(word).equals("TBT"))
						wordSense = disambiguateUntaggedWord(label, wordIndex, word);
					else
						wordSense = disambiguateTaggedWord(label, word);
					wordSenseTranslationCandidates = 
							translationCandidates(wordSense, NUMBER_OF_CANDIDATES);
					wordSenseTranslations.put(word + " as " + label.get(word),
							wordSenseTranslationCandidates);
				}
				wordIndex++;
			}
		}
		return wordSenseTranslations;
	}

	/**
	 * Disambiguates a word that has already been tagged beforehand.
	 * 
	 * @param label		the label containing the word to be disambiguated together with POS info
	 * @param word		the word to be disambiguated
	 * @return			the word sense of the word
	 */
	private String disambiguateTaggedWord(HashMap<String, String> label,
			String word) {
		String wordSense;
		wordSense = lesk.disambiguate(word, label.get(word), contextString);
		if (wordSense.isEmpty())
			wordSense = word;
		return wordSense;
	}

	/**
	 * Disambiguates an untagged word. The word is first tagged by the Stanford Tagger.
	 * 
	 * @param label			the label containing the word to be disambiguated together with POS info
	 * @param wordIndex		the position of the word of the label 
	 * @param word			the word to be disambiguated
	 * @return				the word sense of the word
	 */
	private String disambiguateUntaggedWord(HashMap<String, String> label,
			int wordIndex, String word) {
		String wordSense;
		String tag = englishTagger.tagWordInText(wordIndex, label.get("label"));
		if (tag.isEmpty())
			wordSense = word;
		else {
			label.put(word, tag);
			wordSense = disambiguateTaggedWord(label, word);
		}
		return wordSense;
	}

	/**
	 * Returns the translation with the highest vote based on a voting of translations candidates of a label.
	 * 
	 * @param translationCandidates		the translation candidates of a label
	 * @param translationVotings		the votings of the translation candidates of this label
	 * @return 							the translation with the highest vote
	 */
	public String bestTranslation(ArrayList<String> translationCandidates,
			HashMap<String, Integer> translationVotings) {
		String bestTranslation = translationCandidates.get(0);
		int vote = 0;
		int maxVote = 0;
		for (String translationCandidate : translationCandidates) {
			vote = translationVotings.get(translationCandidate);
			if (vote > maxVote) {
				maxVote = vote;
				bestTranslation = translationCandidate;
			}
		}
		return bestTranslation;
	}
	
	/**
	 * Votes the translations of a label based on the translations of the definitions of the words of the label. 
	 * The translation candidates of the label are voted based on their overlap with the
	 * translations of the WordNet definitions of the words of the label. 
	 * 
	 * @param translationCandidates		the translation candidates for a label
	 * @return							the votings of the translation candidates of this label
	 */
	public HashMap<String, Integer> voteTranslations(ArrayList<String> translationCandidates,
			HashMap<String, String> label) {
		String bestTranslation;
		HashMap<String, Integer> translationVotings = initializeTranslationVotings(translationCandidates);
		for (String word : label.keySet()) {
			if (word.equals("label") || word.equals("")) continue;
			ArrayList<String> wordSenseTranslationCandidates = wordSenseTranslations.get(word + " as " + label.get(word));
			if (wordSenseTranslationCandidates != null) { 
				bestTranslation = lesk.computeBestTranslation(translationCandidates,
						wordSenseTranslationCandidates);
				if (!bestTranslation.isEmpty())
					translationVotings.put(bestTranslation,
							translationVotings.get(bestTranslation)+1);
			}
		}
		return translationVotings;
	}
	
	/**
	 * Returns a HashMap mapping each translation candidate of a label to its initial voting, i.e. 0.
	 * 
	 * @param translationCandidates		the translation candidates of a label
	 * @return							a HashMap mapping each translation candidate to its initial voting, i.e. 0
	 */
	private HashMap<String, Integer> initializeTranslationVotings(ArrayList<String> translationCandidates) {
		HashMap<String, Integer> translationVotings = new HashMap<String, Integer>();
		for (String translation : translationCandidates)
			translationVotings.put(translation, 0);
		return translationVotings;
	}
}
