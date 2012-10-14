package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;


public class Statistics {
	private double numberOfFirstTranslations;
	private double numberOfTotalTranslations;
	private ArrayList<LinkedHashMap<String, LinkedHashMap<String, String>>> MosesAndBPMTtranslations;
	private File outputDirectory;
	
	public Statistics(String outputDirectory) {
		this.outputDirectory = new File(outputDirectory);
		this.outputDirectory.mkdirs();
		MosesAndBPMTtranslations = new ArrayList<LinkedHashMap<String, LinkedHashMap<String, String>>>();
	}
	
	public void recordTranslations(String label, String actualTranslation, String mosesTranslation) {
		LinkedHashMap<String, LinkedHashMap<String, String>> labelToTranslations = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> translations = new LinkedHashMap<String, String>();
		translations.put("Moses", mosesTranslation);
		translations.put("BPMT", actualTranslation);
		labelToTranslations.put(label, translations);
		MosesAndBPMTtranslations.add(labelToTranslations);
	}
	
	public void writeTranslationsToFile() {
		BufferedWriter MosesTranslations = null;
		BufferedWriter BPMTTranslations = null;
		try {
			MosesTranslations = new BufferedWriter(new FileWriter(outputDirectory.getPath() + "/MosesTranslations"));
			BPMTTranslations = new BufferedWriter(new FileWriter(outputDirectory.getPath() + "/BPMTTranslations"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (LinkedHashMap<String, LinkedHashMap<String, String>> labelToTranslations : MosesAndBPMTtranslations) {
			for (Map.Entry<String, LinkedHashMap<String, String>> MosesAndBPMTtranslation: labelToTranslations.entrySet()) {
				try {
					MosesTranslations.write("Label: " + MosesAndBPMTtranslation.getKey() + "\n");
					BPMTTranslations.write("Label: " + MosesAndBPMTtranslation.getKey() + "\n");
					Iterator<String> translation =  MosesAndBPMTtranslation.getValue().values().iterator();
					MosesTranslations.write("Translation: " + translation.next() + "\n\n");
					BPMTTranslations.write("Translation: " + translation.next() + "\n\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			MosesTranslations.close();
			BPMTTranslations.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createBLEUHypothesis() {
		BufferedWriter MosesTranslations = null;
		BufferedWriter BPMTTranslations = null;
		try {
			MosesTranslations = new BufferedWriter(new FileWriter(outputDirectory.getPath() + "/MosesBLEUHypothesis"));
			BPMTTranslations = new BufferedWriter(new FileWriter(outputDirectory.getPath() + "/BPMTBLEUHypothesis"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (LinkedHashMap<String, LinkedHashMap<String, String>> labelToTranslations : MosesAndBPMTtranslations) {
			for (Map.Entry<String, LinkedHashMap<String, String>> MosesAndBPMTtranslation: labelToTranslations.entrySet()) {
				try {
					Iterator<String> translation =  MosesAndBPMTtranslation.getValue().values().iterator();
					MosesTranslations.write(translation.next() + "\n");
					BPMTTranslations.write(translation.next() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			MosesTranslations.close();
			BPMTTranslations.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printStatistics() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(outputDirectory.getPath() + "/Statistics"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		printFractionOfMosesTranslations(out);
		printDifferingTranslations(out);
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printFractionOfMosesTranslations(BufferedWriter out) {
		try {
			out.write((numberOfFirstTranslations/numberOfTotalTranslations)*100.0 + 
					"% usage of best Moses Translation\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printDifferingTranslations(BufferedWriter out) {
		for (LinkedHashMap<String, LinkedHashMap<String, String>> labelToTranslations : MosesAndBPMTtranslations) {
			for (Map.Entry<String, LinkedHashMap<String, String>> MosesAndBPMTtranslation: labelToTranslations.entrySet()) {
				Iterator<String> translation =  MosesAndBPMTtranslation.getValue().values().iterator();
				String Moses = translation.next();
				String BPMT = translation.next();
				if (!Moses.equals(BPMT)) {
					try {
						out.write("Label: " + MosesAndBPMTtranslation.getKey() + "\n");
						out.write("Moses: " + Moses + "\n");
						out.write("BPMT: " + BPMT + "\n\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public boolean checkIfMosesTranslationUsed(String bestTranslation, String mosesTranslation) {
		numberOfTotalTranslations++;
		if (bestTranslation.equals(mosesTranslation)) {
			numberOfFirstTranslations++;
			return true;
		}
		return false;
	}
	
	public double getNumberOfFirstTranslations() {
		return numberOfFirstTranslations;
	}
	public void setNumberOfFirstTranslations(double numberOfFirstTranslations) {
		this.numberOfFirstTranslations = numberOfFirstTranslations;
	}
	public double getNumberOfTotalTranslations() {
		return numberOfTotalTranslations;
	}
	public void setNumberOfTotalTranslations(double numberOfTotalTranslations) {
		this.numberOfTotalTranslations = numberOfTotalTranslations;
	}
}
