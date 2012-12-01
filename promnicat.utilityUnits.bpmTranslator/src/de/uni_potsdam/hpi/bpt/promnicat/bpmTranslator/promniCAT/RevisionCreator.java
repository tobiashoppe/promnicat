package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.promniCAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.refactoring.StringOperations;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.translations.Translator;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tsvProcessing.TsvProcessor;
import de.uni_potsdam.hpi.bpt.promnicat.configuration.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * This class translates the labels of an .svg representation of a process model.
 * It then creates a new revision of this process model in which all labels are translated.
 * The new .svg representation of the translated model will be stored in the same
 * directory as the original model.
 * 
 * @author Kimon Batoulis
 *
 */
public class RevisionCreator {

	private Translator translator;
	private TsvProcessor tsvProcessor;
	private StringOperations stringOperator;
	static IPersistenceApi papi;
	
	/**
	 * RevisionCreator constructor.
	 * 
	 * @param translator	the translation system used to translate the labels 
	 * @param tsvProcessor	the object that contains information about the labels to be translated
	 * @param configFile	the promnicat database config file
	 * @throws IOException if configuration file could not be read
	 */
	public RevisionCreator(Translator translator, TsvProcessor tsvProcessor, String configFile) throws IOException {
		this.translator = translator;
		this.tsvProcessor = tsvProcessor;
		stringOperator = new StringOperations();
		papi = new ConfigurationParser(configFile).getDbInstance();
	}
	
	/**
	 * Delegates the steps that are needed to create the translated revision
	 * of the process model. This includes extracting the strings of the .svg file that represent
	 * the activity labels of the process model, as well as translating those strings, creating
	 * a new .svg file containing the translated labels and saving the new revision in the 
	 * promnicat database.
	 * 
	 * @param dbId					the database ID of the representation to be translated
	 * @throws DataFormatException 	thrown when a representation other than a SVG is supplied
	 */
	public void createTranslatedRevision(UUID dbId) throws DataFormatException {
		IRepresentation oldSvgRepresentation = papi.loadRepresentation(dbId);
		checkDataFormat(oldSvgRepresentation);
		IModel model = oldSvgRepresentation.getModel().loadCompleteModel(papi);
		int nrOfRevisions = model.getNrOfRevisions();
    	String line = readSvgFile(oldSvgRepresentation.getOriginalFilePath());
		ArrayList<String> frameRectangles = new ArrayList<String>();
		ArrayList<AbstractMap.SimpleEntry<String, Integer>> labels = extractActivityLabels(
				line, frameRectangles);
		translateLabels(labels);
		ArrayList<String> sublabels = extractSublabels(frameRectangles);
		convertSublabelStringsToRegexps(sublabels);
		line = replaceLabels(line, labels, sublabels);
		File newSvgFile = createNewRevisionFile(oldSvgRepresentation, nrOfRevisions);
		writeToNewRevision(newSvgFile, line);
		IRepresentation newSvgRepresentation = papi.getPojoFactory().createRepresentation(
				Constants.FORMAT_SVG, oldSvgRepresentation.getNotation(), newSvgFile);
		IRevision newSvgRevision = papi.getPojoFactory().createRevision(nrOfRevisions+1);
		newSvgRepresentation.setRevision(newSvgRevision);
		model.connectLatestRevision(newSvgRevision);
		papi.savePojo(model);
    }

	/**
	 * Checks whether or not the representation's data format is SVG.
	 * If not, a DataFormatException is thrown.
	 * 
	 * @param oldSvgRepresentation	the model's representation whose format is checked
	 * @throws DataFormatException	thrown when a representation other than a SVG is supplied
	 */
	private void checkDataFormat(IRepresentation oldSvgRepresentation) throws DataFormatException {
		if (!oldSvgRepresentation.getFormat().equalsIgnoreCase(Constants.FORMAT_SVG))
			throw new DataFormatException("format used for the model's data content must be SVG");
	}

	/**
	 * Takes a list of the activity labels found in the .svg representation of
	 * the process model and replaces them by their translation.
	 * 
	 * @param labels	the activity labels of the process model to be translated
	 */
	private void translateLabels(ArrayList<AbstractMap.SimpleEntry<String, Integer>> labels) {
		ArrayList<String> labelsOnly = new ArrayList<String>();
		ArrayList<String> labelTranslations = new ArrayList<String>();
		for (int i=0; i<labels.size(); i++)
			labelsOnly.add(labels.get(i).getKey());
		stringOperator.replaceCharacterCodeByCharacter(labelsOnly);
		labelTranslations = translator.translate(tsvProcessor.getAnnotatedLabels(labelsOnly));
		stringOperator.replaceCharacterByCharacterCode(labelTranslations);
		for (int i=0; i<labels.size(); i++)
			labels.set(i, new AbstractMap.SimpleEntry<String, Integer>(labelTranslations.get(i), labels.get(i).getValue()));
	}

	/**
	 * Writes the content of the new .svg revision to an appropriate file.
	 * 
	 * @param newSVGFile		the new .svg file containing the translated activity labels 
	 * @param line				the entire content of the new .svg file
	 */
	private void writeToNewRevision(File newSVGFile, String line) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(newSVGFile));
			bw.write(line);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Replaces the activity labels of the old .svg file with their translations.
	 * Since the strings making up the labels may contain line breaks, the translations of the old
	 * labels are divided up so that they fit into the rectangles of the activity labels in the .svg file.
	 * 
	 * @param line			the entire content of the old .svg file
	 * @param labels		the translated activity labels of the process model represented by the .svg file
	 * @param sublabels		the substrings making up the individual lines of the old (!) activity labels
	 * 						(the activity labels represented in the .svg file may contain line breaks)
	 * @return				the entire content of the new .svg file
	 */
	private String replaceLabels(String line,
			ArrayList<AbstractMap.SimpleEntry<String, Integer>> labels,
			ArrayList<String> sublabels) {
		int index = 0;
		String[] labelWords = null;
		double wordsPerLine = 0;
		int numberOfLines = 0;
		ArrayList<String> labelSubstrings = null;
		for (int i = 0; i < labels.size(); i++) {
			labelWords = labels.get(i).getKey().split("\\s");
			numberOfLines = labels.get(i).getValue();
			wordsPerLine = Math.ceil(labelWords.length/(double) numberOfLines);
			labelSubstrings = labelSubstrings(labelWords, numberOfLines, (int) wordsPerLine);
			for (int j = 0; j < numberOfLines; j++) {
				line = line.replaceFirst(sublabels.get(index), labelSubstrings.get(j));
				index++;
			}
		}
		return line;
	}

	/**
	 * Converts the substrings making up the individual lines of the old (!) activity labels
	 * to regular expression strings by escaping special characters ("\").
	 * 
	 * @param subLabels		the substrings making up the individual lines of the old (!) activity labels
	 */
	private void convertSublabelStringsToRegexps(ArrayList<String> subLabels) {
		String subLabel = null;
		StringBuilder b = null;
		for (int i=0; i<subLabels.size(); i++) {
			b = new StringBuilder();
			subLabel = subLabels.get(i);
			for (int j=0; j < subLabel.length(); j++) {
				char ch = subLabel.charAt(j);
				if ("\\.^$|?*+[]{}()".indexOf(ch) != -1)
					b.append("\\"+ch);
				else
					b.append(ch);
			}
			subLabels.set(i, b.toString());
		}
	}

	/**
	 * Takes a list of the rectangle elements of the .svg file that contain the 
	 * activity labels as input and returns a list of the substrings that make up the entire labels.
	 * (The strings may contain line breaks.) 
	 * 
	 * @param frameRectangles	the rectangles of the .svg file containing the activity labels
	 * @return					the individual lines of the labels contained in the rectangles
	 */
	private ArrayList<String> extractSublabels(ArrayList<String> frameRectangles) {
		ArrayList<String> sublabels = new ArrayList<String>();
		for (String frameRectangle : frameRectangles) {
			Matcher m3 = Pattern.compile("<tspan.*?>(.+?)</tspan>").matcher(frameRectangle);
			while (m3.find())
				sublabels.add(m3.group(1));
		}
		return sublabels;
	}

	/**
	 * Extracts the activity labels of the process model represented by the .svg file
	 * by searching for rectangle elements (since the rectangles contain the activity labels).
	 * 
	 * @param line				the entire content of the old .svg file
	 * @param frameRectangles	the list where the rectangle definitions are to be put
	 * @return					the list of the activity labels extracted from the line variable
	 */
	private ArrayList<AbstractMap.SimpleEntry<String, Integer>> extractActivityLabels(
			String line, ArrayList<String> frameRectangles) {
		Matcher m1 = Pattern.compile("<rect[^(rect)]*?frame.*?<text.*?>(<tspan.*?>.+?</tspan>)+.*?</text>").matcher(line);
		while (m1.find())
			frameRectangles.add(m1.group(0));
		String label = null;
		Integer lines = null;
		ArrayList<AbstractMap.SimpleEntry<String,Integer>> labels = new ArrayList<AbstractMap.SimpleEntry<String,Integer>>();
		for (String frameRectangle : frameRectangles) {
			label = "";
			lines = 0;
			Matcher m2 = Pattern.compile("<tspan.*?>(.+?)</tspan>").matcher(frameRectangle);
			while (m2.find()) {
				lines++;
				label += " " + m2.group(1).trim();
			}
			labels.add(new AbstractMap.SimpleEntry<String, Integer>(label.trim(), lines));
		}
		return labels;
	}

	/**
	 * Reads the content of the .svg file representing the process model
	 * 
	 * @param pathToSVGFile		the paths to the .svg file
	 * @return					the content of the .svg file
	 */
	private String readSvgFile(String pathToSVGFile) {
		BufferedReader br;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(pathToSVGFile));
			line = br.readLine();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	/**
	 * Creates a path to the file where the new revision of the process model is to be put.
	 * The method increments the revision index until it is the highest (newest) revision.
	 * 
	 * @param svgRepresentation		the svg representation of the revision of the model to be translated
	 * @param revision				the latest revision of the model to be translated
	 * @return						the new .svg file
	 */
	private File createNewRevisionFile(IRepresentation svgRepresentation, int revision) {
    	revision++;
    	String pathToNewSVGFile = svgRepresentation.getOriginalFilePath().
    			replace("rev"+svgRepresentation.getRevisionNumber()+".svg", "rev"+revision+".svg");
    	File fDest = new File(pathToNewSVGFile);
    	try {
			fDest.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fDest;
	}
    
	/**
	 * Creates the substrings making up the individual lines of the new (!) activity labels
	 * (the activity labels represented in the .svg file may contain line breaks).
	 * 
	 * @param labelWords		the individual words of the translation of a label
	 * @param numberOfLines		the number of lines used for the label in the old .svg file
	 * @param wordsPerLine		the maximum number of words of the translated label to be put in one line
	 * @return					the substrings making up the individual lines of the new (!) activity labels
	 */
    private ArrayList<String> labelSubstrings(String[] labelWords,
    		int numberOfLines, int wordsPerLine) {
    	ArrayList<String> labelSubstrings = new ArrayList<String>();
    	StringBuilder b = null;
    	int index = 0;
    	for (int i = 0; i < numberOfLines-1; i++) {
    		if (index == labelWords.length) break;
    		b = new StringBuilder();
    		for (int j = 0; j < wordsPerLine; j++) {
    			if (index == labelWords.length) break;
    			b.append(" " + labelWords[index]);
    			index++;
    		}
    		labelSubstrings.add(b.toString().trim());
    	}
    	b = new StringBuilder();
    	int rest = labelWords.length - wordsPerLine*(numberOfLines-1);
		for (int j = 0; j < rest; j++) {
			if (index == labelWords.length) break;
			b.append(" " + labelWords[index]);
			index++;
		}
		labelSubstrings.add(b.toString().trim());
    	return labelSubstrings;
    }
}