package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.DataFormatException;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.evaluation.Statistics;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.promniCAT.RevisionCreator;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.properties.CheckProperties;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.translations.BPMTranslator;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.translations.MosesTranslator;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.translations.Translator;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tsvProcessing.TsvProcessor;

public class Main {
	
	private static Properties props;
	private static TsvProcessor tsvProcessor = null;
	private static Translator translator = null;
	
	public static void main(String[] args) throws DataFormatException {
		
		props = new Properties();
		try {
			props.load(new FileInputStream("config.properties"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		new CheckProperties(props).checkProperties();
		props.list(System.out);

		initializeTsvProcessor();
		assert(tsvProcessor != null);
		initializeTranslator();
		assert(translator != null);
		if (Boolean.parseBoolean(props.getProperty("translateAllAnnotatedLabels")))
			translateAnnotatedLabels();
		if (Boolean.parseBoolean(props.getProperty("createNewRevision")))
			try {
				createTranslatedRevision();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private static void initializeTsvProcessor() {
		if (props.getProperty("annotatedLabelsContext").isEmpty())
			tsvProcessor = new TsvProcessor(props.getProperty("annotatedLabels"));
		else
			tsvProcessor = new TsvProcessor(
					props.getProperty("annotatedLabels"), props.getProperty("annotatedLabelsContext"));
	}

	private static void initializeTranslator() {
		if (props.getProperty("translationSystem").equals("BPMT"))
			initializeBPMTranslator();
		else if (props.getProperty("translationSystem").equals("Moses"))
			initializeMosesTranslator();
	}
	
	private static void initializeMosesTranslator() {
		translator = new MosesTranslator(
				props.getProperty("sourceLanguage"),
				props.getProperty("targetLanguage"),
				props.getProperty("trainingCorpus"),
				new Statistics(props.getProperty("outputDirectory")));
	}

	private static void initializeBPMTranslator() {
		translator = new BPMTranslator(
				props.getProperty("sourceLanguage"),
				props.getProperty("targetLanguage"),
				props.getProperty("trainingCorpus"),
				tsvProcessor.getContext(),
				props.getProperty("englishTagger"),
				props.getProperty("germanTagger"),
				Integer.parseInt(props.getProperty("numberOfTranslationCandidates")),
				new Statistics(props.getProperty("outputDirectory")));
	}
	
	private static void translateAnnotatedLabels() {
		translator.translate(tsvProcessor.getAnnotatedLabels());
	}
	
	private static void createTranslatedRevision() throws DataFormatException, IOException {
		new RevisionCreator(translator, tsvProcessor, props.getProperty("configFile")).
			createTranslatedRevision(UUID.fromString(props.getProperty("dbId")));
	}
}
