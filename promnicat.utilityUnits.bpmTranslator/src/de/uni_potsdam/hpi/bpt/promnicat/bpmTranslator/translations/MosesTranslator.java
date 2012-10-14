package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.translations;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.evaluation.Statistics;


/**
 * This class is based on the Moses Translator, a statistical machine translation tool (http://www.statmt.org/moses/).
 * It computes the translation of a label of a business process model by extending the Translator class and
 * implementing the translate() method.
 * 
 * @author Kimon Batoulis
 *
 */
public class MosesTranslator extends Translator {
		
	/**
	 * MosesTranslator constructor
	 * 
	 * @param sourceLanguage	the source language of the translation
	 * @param targetLanguage 	the target language of the translation
	 */
	public MosesTranslator(String sourceLanguage, String targetLanguage,
			String trainingCorpus,
			Statistics stats) {
	    super(sourceLanguage, targetLanguage, trainingCorpus, stats);
	    NUMBER_OF_CANDIDATES = 1;
	}
	
	/**
	 * Computes and returns the translation of all labels of a business process model by invoking the Moses
	 * translation system and accepting the most probable translation according to Moses. 
	 *
	 * @param labels 		the labels of the business process model
	 * @return				the translations of the labels
	 */
	public ArrayList<String> translate(ArrayList<LinkedHashMap<String, String>> labels) {
		ArrayList<String> translations = new ArrayList<String>();
		this.labels = labels;
		for (LinkedHashMap<String, String> label : this.labels)
			translations.add(translationCandidates(label.get("label"), NUMBER_OF_CANDIDATES).get(0));
		stats.writeTranslationsToFile();
		return translations;
	}

}
