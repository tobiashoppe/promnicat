package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.translations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.evaluation.Statistics;


/**
 * This abstract class is the base class of the translator, which is founded upon the
 * Moses Translator, a statistical machine translation tool (http://www.statmt.org/moses/).
 * It defines and/or implements various methods that are required for every translator,
 * like I/O operations and the interface for the translation process.
 * 
 * @author Kimon Batoulis
 *
 */
public abstract class Translator {

	protected String sourceLanguage;
	protected String targetLanguage;
	protected String pathToMosesIni;
	protected Process proc;
	protected BufferedReader reader;
	protected int NUMBER_OF_CANDIDATES;
	protected Statistics stats;
	protected ArrayList<LinkedHashMap<String, String>> labels;

	/**
	 * The Translator constructor. Every translator has source and target languages,
	 * a training corpus and an object to manage statistics. 
	 * 
	 * @param sourceLanguage	the source language of the process model
	 * @param targetLanguage	the target language of the process model
	 * @param trainingCorpus	the training corpus of the translator
	 * @param stats				the statistics object
	 */
	protected Translator(String sourceLanguage, String targetLanguage,
			String trainingCorpus,
			Statistics stats) {
	    this.sourceLanguage = sourceLanguage;
	    this.targetLanguage = targetLanguage;
	    setPathToMosesIni(sourceLanguage, targetLanguage, trainingCorpus);
	    this.stats = stats;
	}

	/**
	 * The definition of the translation interface of the translator.
	 * It accepts a list of LinkedHashMaps where each LinkedHashMap contains 
	 * the mapping of the label to a string, and all mappings of the individual
	 * words of the label to their part-of-speech (POS)
	 * 
	 * @param labels	the labels of the process model to be translated
	 * @return			a list of translations of all labels
	 */
	public abstract ArrayList<String> translate(ArrayList<LinkedHashMap<String, String>> labels);
	
	/**
	 * Sets the path to moses.ini based on source and target language.
	 * 
	 * @param sourceLanguage	the source language
	 * @param targetLanguage	the target language
	 */
	protected void setPathToMosesIni(String sourceLanguage, String targetLanguage,
			String trainingCorpus) {
		if (sourceLanguage.equals("English") && targetLanguage.equals("German")) {
			if (trainingCorpus.equals("NewsCommentary"))
				pathToMosesIni = "lib/mosesData/mosesEnglishGerman/moses.ini";
//			else if (trainingCorpus.equals("Europarl"))
//				pathToMosesIni = "";
		}
		else if (sourceLanguage.equals("German") && targetLanguage.equals("English"))
			pathToMosesIni = "lib/mosesData/mosesGermanEnglish/moses.ini"; 
	}
	
	
	/**
	 * Returns a specified number of translation candidates of a label.
	 * 
	 * @param label					the label to be translated
	 * @param numberOfCandidates	the number of translation candidates to be computed
	 * @return						the translation candidates of a label
	 */
	protected ArrayList<String> translationCandidates(String label, int numberOfCandidates) {
		if (sourceLanguage.equals(targetLanguage)) {
			ArrayList<String> translationCandidates = new ArrayList<String>();
			translationCandidates.add(label);
			return translationCandidates;
		}
		writeLabelToFile(label);
		invokeBashScript(numberOfCandidates);
		return readTranslationsFromFile();
	}
	
	/**
	 * Writes the label to be translated to a file so that it can be read
	 * by the Moses translation system.
	 * 
	 * @param label 	the label to be written to a file
	 */
	protected void writeLabelToFile(String label) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("bin/translations/inputFile"));
			out.write(label+"\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Due to the fact that the Moses translation system is a UNIX executable,
	 * it needs to be started from a bash script, which is in turn started by this method.
	 * The Moses executable takes as parameters the Moses config file (moses.ini),
	 * a file which contains the label to be translated and the desired
	 * number of translations to be computed. It then writes the output to a file.
	 * 
	 * @param numberOfTranslationCandidates		the number of translations of a label.
	 */
	protected void invokeBashScript(int numberOfTranslationCandidates) {
		try {
			proc = Runtime.getRuntime().exec("./bin/translations/runMosesTranslation.sh" + 
					" " + pathToMosesIni + " " + numberOfTranslationCandidates);
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the translation candidates of a label from the output file generated by Moses.
	 * 
	 * @return the translation candidates of a label
	 */
	protected ArrayList<String> readTranslationsFromFile() {
		ArrayList<String> translationCandidates = new ArrayList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader("bin/translations/outputFile"));
			try {
				while ((line = in.readLine()) != null)
					translationCandidates.add(extractTranslation(line));
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return translationCandidates;
	}
	
	/**
	 * Due to the fact that the output file generated by Moses contains information that is irrelevant for
	 * this application, the lines of the file are filtered so that only the translation is obtained.
	 * 
	 * @param line	the line to be filtered
	 * @return		the filter line, i.e. the translation
	 */
	protected String extractTranslation(String line) {
		Matcher m = Pattern.compile("\\|{3}\\s*(.*?)\\s*\\|").matcher(line);
		if (m.find())
			return m.group(1);
		return "";
	}

}
