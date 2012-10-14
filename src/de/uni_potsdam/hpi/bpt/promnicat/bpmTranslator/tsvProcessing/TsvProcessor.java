package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tsvProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
/**
 * This class reads and annotates (POS) a .tsv file of labels of a business process model.
 * The .tsv file is supposed to contain the fields "label,action,businessObject,addition". 
 * 
 * @author Kimon Batoulis
 *
 */
public class TsvProcessor {
	
	private ArrayList<String> tsvLines1 = null;
	private ArrayList<String> tsvLines2 = null;
	
	/**
	 * A TsvProcessor constructor. This constructor is used if the .tsv file only 
	 * contains information about the labels of the model to be translated.
	 * 
	 * @param tsvFile1	the .tsv file to be processed
	 */
	public TsvProcessor(String tsvFile1) {
		tsvLines1 = readTsv(tsvFile1);
	}
	
	/**
	 * Another TsvProcessor constructor. This constructor is used if the process model
	 * to be translated is part of a large collection of models. The first parameter is 
	 * a .tsv file that only contains information about the labels of the model to be translated.
	 * The second parameter is a .tsv file that additionally contains information about other models,
	 * thereby providing more context information which can support the translation process.
	 * 
	 * @param tsvFile1		the .tsv file containing information about the labels of 
	 * 						the model to be translated
	 * @param tsvFile2		the .tsv file additionally containing information about
	 * 						the labels of all other models of the same collection.
	 */
	public TsvProcessor(String tsvFile1, String tsvFile2) {
		tsvLines1 = readTsv(tsvFile1);
		tsvLines2 = readTsv(tsvFile2);
	}
	
	/**
	 * Returns a list of annotated labels of a business process model read from an
	 * appropriately prepared .tsv file.
	 * 
	 * @return the list of annotated labels
	 */
	public ArrayList<LinkedHashMap<String, String>> getAnnotatedLabels() {
		ArrayList<LinkedHashMap<String, String>> processedTsv = processTsv(tsvLines1);
		refactorLabelString(processedTsv);
		ArrayList<LinkedHashMap<String,String>> annotatedLabels = annotateLabels(processedTsv);
		return annotatedLabels;
	}
	
	/**
	 * Searches the .tsv file for information about the labels to be translated.
	 * If it is found the words are annotated with the appropriate POS.
	 * If not, the words are annotated with TBT (to be tagged) meaning that they have to be tagged
	 * later on by the translator (e.g. using the Stanford Tagger).
	 * 
	 * @param labels	the labels of the process model to be translated
	 * @return			the annotated labels of the process model to be translated 
	 */
	public ArrayList<LinkedHashMap<String, String>> getAnnotatedLabels(ArrayList<String> labels) {
		HashMap<String,ArrayList<String>> lines = findTsvLines(labels);
		tsvLines1 = lines.get("found");
		ArrayList<LinkedHashMap<String, String>> annotatedLabels = getAnnotatedLabels();
		for (String label : lines.get("notFound")) {
			LinkedHashMap<String, String> posMap = new LinkedHashMap<String, String>();
			posMap.put("label", label);
			annotateWords(posMap, label, "TBT");
			annotatedLabels.add(labels.indexOf(label), posMap);
		}
		return annotatedLabels;
	}
	
	/**
	 * Checks whether or not a process model collection was specified.
	 * If yes, all labels of all models of the collection a concatenated and returned.
	 * If not, all labels of the model to be translated are concatenated and returned.
	 * This serves as context information for the translation process. 
	 * 
	 * @return	a string containing the concatenation of all labels of the model (collection)
	 */
	public String getContext() {
		if (tsvLines2 != null)
			return concatenateTsvLines(tsvLines2);
		else
			return concatenateTsvLines(tsvLines1);
	}

	/**
	 * Concatenates the labels of a .tsv file and returns the resulting string.
	 * 
	 * @param tsvLines	the .tsv file containing the labels to be concatenated
	 * @return			the string contatining the concatenated labels
	 */
	private String concatenateTsvLines(ArrayList<String> tsvLines) {
		StringBuilder sb = new StringBuilder();
		for (String line : tsvLines)
			sb.append(line.substring(0,line.indexOf("\t")) + " ");
		return sb.toString();
	}

	/**
	 * Tries to find information about the labels of a process model
	 * in the .tsv file. It returns a HashMap containing information about all the 
	 * labels that were found in the .tsv file. Besides, the HashMap lists all the 
	 * labels for which no information could be found. Those labels then have to be
	 * tagged in a different way, e.g. using the Stanford Tagger.
	 * 
	 * @param labels	the labels for which information is to be found
	 * @return			the HashMap containing information about the labels
	 */
	private HashMap<String,ArrayList<String>> findTsvLines(ArrayList<String> labels) {
		HashMap<String,ArrayList<String>> relevantTsvLines = new HashMap<String,ArrayList<String>>();
		relevantTsvLines.put("found", new ArrayList<String>());
		relevantTsvLines.put("notFound", new ArrayList<String>());
		boolean found;
		for (String label : labels) {
			found = false;
			for (String tsvLine : tsvLines1) {
				if (tsvLine.startsWith(label+"\t")) {
					relevantTsvLines.get("found").add(tsvLine);
					found = true;
					break;
				}
			}
			if (!found)
				relevantTsvLines.get("notFound").add(label);
		}
		return relevantTsvLines;
	}
		
	/**
	 * Reads and returns a .tsv file
	 * 
	 * @return	the read .tsv file
	 */
	private ArrayList<String> readTsv(String tsvFile) {
		ArrayList<String> tsvLines = new ArrayList<String>();
		if (!tsvFile.endsWith(".tsv"))
			throw new IllegalArgumentException("not a .tsv file.");
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(tsvFile));
			try {
				// skip .tsv header
				line = in.readLine();
				while ((line = in.readLine()) != null)
					tsvLines.add(line);
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tsvLines;
	}
	
	/**
	 * Creates and returns an ArrayList of LinkedHashMaps, which contain mappings from field headers to values.
	 * 
	 * @param tsvLines	the list of lines of a .tsv file
	 * @return			the list of mappings from field headers to values per line
	 */
	private ArrayList<LinkedHashMap<String, String>> processTsv(ArrayList<String> tsvLines) {
		ArrayList<LinkedHashMap<String, String>> processedTsv = new ArrayList<LinkedHashMap<String,String>>();
		String[] values;
		for (String line : tsvLines) {
			LinkedHashMap<String, String> valuesMap = new LinkedHashMap<String, String>();
			values = line.split("\t",-1);
			valuesMap.put("label", values[0]);
			valuesMap.put("action", values[1]);
			valuesMap.put("businessObject", values[2]);
			valuesMap.put("addition", values[3]);
			processedTsv.add(valuesMap);
		}
		return processedTsv;
	}

	/**
	 * Creates and returns an ArrayList of LinkedHashMaps, which contain mappings
	 * of 'label' to its refactored representation and the words of the label to their part-of-speech (POS).
	 * 
	 * @param processedTsv		the list of mappings from field headers to values per line
	 * @return					the list of mappings from words to POS
	 */
	private ArrayList<LinkedHashMap<String, String>> annotateLabels(ArrayList<LinkedHashMap<String, String>> processedTsv) {
		ArrayList<LinkedHashMap<String, String>> annotatedLabels = new ArrayList<LinkedHashMap<String,String>>();
		for (LinkedHashMap<String, String> label : processedTsv) {
			LinkedHashMap<String, String> posMap = new LinkedHashMap<String, String>();
			posMap.put("label", label.get("label"));
			annotateWords(posMap, label.get("action"), "verb");
			annotateWords(posMap, label.get("businessObject"), "noun");
			annotateWords(posMap, label.get("addition"), "TBT"); // to be tagged (by tagger)
			annotatedLabels.add(posMap);
		}
		return annotatedLabels;
	}
	
	/**
	 * Refactors the string of a label so that it is of the form:
	 * 'action business object addition'
	 * 
	 * @param processedTsv	the list of label strings to be refactored
	 */
	private void refactorLabelString(ArrayList<LinkedHashMap<String, String>> processedTsv) {
		for (LinkedHashMap<String, String> label: processedTsv)
			label.put("label", label.get("action") + " " + label.get("businessObject") + " " + label.get("addition"));
	}
	
	/**
	 * Annotates the words of a label according to their part-of-speech (POS).
	 * 
	 * @param posMap	the LinkedHashMap in which the annotated words are stored
	 * @param string	the word to be annotated
	 * @param pos		the POS with which the word is to be annotated
	 */
	private void annotateWords(LinkedHashMap<String, String> posMap, String string, String pos) {
		//TODO use better tokenizing (maybe StringOperation.java)
		String[] words = string.split(" ");
		for (String word : words)
			posMap.put(word, pos);
	}
	
}
