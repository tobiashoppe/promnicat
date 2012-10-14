package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
 
public class SetProperties {
	//TODO directory of process models
	public static void main(String[] args) {
		Properties prop = new Properties();
		String comments = "Business Process Model Translation Systems CONFIG FILE\n\n" +
				"translationSystem = {MOSES, BPMT}\n" +
				"englishTagger = English tagging model\n" +
				"germanTagger = German tagging model\n" +
				"trainingCorpus = {NewsCommentary}\n" +
				"\tfor now, only the NewsCommentary corpus is available\n" +
				"\tthe Europarl corpus could be included in the future\n" +
				"\tit is much larger so the translation results\n" +
				"\tshould be better, but it takes more time to obtain them\n" +
				"sourceLanguage = {English,German}\n" +
				"targetLanguage = {English,German}\n" +
				"numberOfTranslationCandidates = number of translation candidates\n" +
				"\tused for translation disambiguation (only relevant for BPMT)\n" +
				"annotatedLabels = .tsv file of annotated labels\n" +
				"\tstructure: label\\taction\\tbusinessObject\\taddition\n" +
				"annotatedLabelsContext = other annotated labels that belong to the same process model domain (optional)\n" +
				"\tuseful if the testData .tsv file was extracted from a very large .tsv file\n" +
				"\twhose other labels shall still be used as context information\n" +
				"processModel = .svg of process model to be translated\n" +
				"revision = number of revision of the process model\n" +
				"createNewRevision = {true,false}\n" +
				"\tif set to true, a new revision of the specified process model will be created\n" +
				"\tand stored in the same directory as the specified revision\n" +
				"translateAllAnnotatedLabels = {true,false}\n" +
				"\tif set to true, all the annotated labels of the .tsv file will be translated\n" +
				"\tand the results together with some statistics will be stored in the specified output directory\n" +
				"outputDirectory = the path to the directory in which the statistics shall be stored\n\n";
	    try {
	    	prop.setProperty("translationSystem", "BPMT");
	    	prop.setProperty("trainingCorpus", "NewsCommentary");
	    	prop.setProperty("englishTagger", "lib/stanfordTagger/wsj-0-18-bidirectional-distsim.tagger");
	    	prop.setProperty("germanTagger", "lib/stanfordTagger/german-fast.tagger");
	    	prop.setProperty("sourceLanguage", "English");
	    	prop.setProperty("targetLanguage", "German");
	    	prop.setProperty("numberOfTranslationCandidates", "100");
	    	prop.setProperty("annotatedLabels", "resources/testData/SignavioAI/SignavioAI.tsv");
	    	prop.setProperty("annotatedLabelsContext", "resources/testData/SignavioAI/SignavioAI.tsv");
	    	prop.setProperty("processModel", "/home/kimon/Software/promnicat/resources/" +
	    			"2011-04-19_signavio_academic_processes/" +
					"869994845/BPMN2.0_Process/2011-03-17_PV207 SG Teamwork Process (Copy)/1162747107_rev1.svg");
	    	prop.setProperty("revision", "1");
	    	prop.setProperty("createNewRevision", "true");
	    	prop.setProperty("translateAllAnnotatedLabels", "false");
	    	prop.setProperty("outputDirectory", "resources/testData/SignavioAI/testOutput");
	    	prop.store(new FileOutputStream("config.properties"), comments);
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
	}
}
