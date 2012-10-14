package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.properties;

import java.util.ArrayList;
import java.util.Properties;

public class CheckProperties {
	
	private Properties props;
	private ArrayList<String> sourceLanguages;
	private ArrayList<String> targetLanguages;
	private ArrayList<String> translationSystems;
	private ArrayList<String> trainingCorpora;
	private ArrayList<String> keys;

	public CheckProperties(Properties props) {
		this.props = props;
		populateKeysList();
		populateSourceLanguagesList();
		populateTargetLanguagesList();
		populateTrainingCorporaList();
		populateTranslationSystemsList();
	}
	
	public void checkProperties() {
		checkKeys();
		checkValues();
	}
	
	private void checkKeys() {
		for (Object key : props.keySet())
			if (!keys.contains(key))
				throwKeyException((String) key);
	}
	
	private void checkValues() {
		checkSourceLanguage();
		checkTargetLanguage();
		checkTranslationSystem();
		checkTrainingCorpora();
		checkIntegers();
	}

	private void checkIntegers() {
		if (!props.getProperty("numberOfTranslationCandidates").isEmpty())
			checkNumberOfTranslationCandidates();
	}

	private void checkNumberOfTranslationCandidates() {
		int numberOfTranslationCandidates = 0;
		try {
			numberOfTranslationCandidates = Integer.parseInt(props.getProperty("numberOfTranslationCandidates"));
		} catch (NumberFormatException e) {
			throwValueException("numberOfTranslationCandidates", props.getProperty("numberOfTranslationCandidates"));
		}
		if (numberOfTranslationCandidates < 1)
			throwValueException("numberOfTranslationCandidates", props.getProperty("numberOfTranslationCandidates"));
	}

	private void checkTrainingCorpora() {
		String trainingCorpus = props.getProperty("trainingCorpus");
		if (!trainingCorpora.contains(trainingCorpus))
			throwValueException("trainingCorpus", trainingCorpus);
	}

	private void checkTranslationSystem() {
		String translationSystem = props.getProperty("translationSystem");
		if (!translationSystems.contains(translationSystem))
			throwValueException("translationSystem", translationSystem);
	}

	private void checkTargetLanguage() {
		String targetLanguage = props.getProperty("targetLanguage");
		if (!targetLanguages.contains(targetLanguage))
			throwValueException("targetLanguage", targetLanguage);
	}

	private void checkSourceLanguage() {
		String sourceLanguage = props.getProperty("sourceLanguage");
		if (!sourceLanguages.contains(sourceLanguage))
			throwValueException("sourceLanguage", sourceLanguage);
	}
	
	private void throwKeyException(String key) {
		throw new RuntimeException("\"" + key + "\"" + " is an illegal config file key");
	}
	
	private void throwValueException(String key, String value) {
		throw new RuntimeException("\"" + value + "\"" + " is an illegal config file value for key " + key);
	}
	
	private void populateKeysList() {
		keys = new ArrayList<String>();
		keys.add("sourceLanguage");
		keys.add("targetLanguage");
		keys.add("translationSystem");
		keys.add("trainingCorpus");
		keys.add("englishTagger");
		keys.add("germanTagger");
		keys.add("dbId");
		keys.add("configFile");
		keys.add("annotatedLabels");
		keys.add("annotatedLabelsContext");
		keys.add("numberOfTranslationCandidates");
		keys.add("createNewRevision");
		keys.add("translateAllAnnotatedLabels");
		keys.add("outputDirectory");
	}
	
	private void populateSourceLanguagesList() {
		sourceLanguages = new ArrayList<String>();
		sourceLanguages.add("English");
		sourceLanguages.add("German");
	}
	
	private void populateTargetLanguagesList() {
		targetLanguages = new ArrayList<String>();
		targetLanguages.add("English");
		targetLanguages.add("German");
	}
	
	private void populateTranslationSystemsList() {
		translationSystems = new ArrayList<String>();
		translationSystems.add("Moses");
		translationSystems.add("BPMT");
	}
	
	private void populateTrainingCorporaList() {
		trainingCorpora = new ArrayList<String>();
		trainingCorpora.add("NewsCommentary");
//		trainingCorpora.add("Europarl");
	}
}
