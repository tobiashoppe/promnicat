package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.labelAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.labelAnalysis.interfaces.LabelCategorizer;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.labelAnalysis.interfaces.LabelDeriver;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.structure.Activity;

public class TsvCreator {
	private static LabelDeriver labelDeriver;
	private static LabelCategorizer labelCategorizer;
	/**
	 * TsvCreator constructor.
	 */
	public TsvCreator() {
		labelDeriver = new LabelDeriver() {
			
			@Override
			public ArrayList<String> returnBusinessObjects() {
				return null;
			}
			
			@Override
			public String returnAddition() {
				return null;
			}
			
			@Override
			public ArrayList<String> returnActions() {
				return null;
			}
			
			@Override
			public void processLabel(Activity label, String labelStyle) {
				
			}
		};
		labelCategorizer = new LabelCategorizer() {
			
			@Override
			public HashMap<String, String> getLabelStyle(
					ArrayList<ArrayList<Activity>> modelCollection) {
				return null;
			}
			
			@Override
			public String getLabelStyle(Activity activity) {
				return null;
			}
		};
	}
	/**
	 * Creates the tsv file containing the refactoring information of the activity labels.
	 * @param modelCollection	the modelCollection to be refactored
	 * @param tsv				the file to be written to
	 */
	public void createTsv(ArrayList<ArrayList<Activity>> modelCollection, File tsv) {
		HashMap<String,String> labelStyles = labelCategorizer.getLabelStyle(modelCollection);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(tsv));
			createHeader(bw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (ArrayList<Activity> activities : modelCollection)
			for (Activity label : activities) {
				labelDeriver.processLabel(label, labelStyles.get(label));
				try {
					bw.write(label.toString()+"\t");
					writeActions(bw);
					writeBusinessObjects(bw);
					writeAddition(bw);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	/**
	 * Writes the addition to the tsv file containing the refactoring 
	 * information of the activity labels.
	 * @param bw	the BufferedWriter used to write to the tsv file
	 * @throws IOException
	 */
	private void writeAddition(BufferedWriter bw) throws IOException {
		bw.write(labelDeriver.returnAddition() + "\n");
	}
	/**
	 * Writes the actions to the tsv file containing the refactoring 
	 * information of the activity labels.
	 * @param bw	the BufferedWriter used to write to the tsv file
	 * @throws IOException
	 */
	private void writeActions(BufferedWriter bw) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String action : labelDeriver.returnActions())
			sb.append(action + " ");
		bw.write(sb.toString().trim()+"\t");
	}
	/**
	 * Writes the business objects to the tsv file containing the refactoring 
	 * information of the activity labels.
	 * @param bw	the BufferedWriter used to write to the tsv file
	 * @throws IOException
	 */
	private void writeBusinessObjects(BufferedWriter bw) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String businessObject : labelDeriver.returnBusinessObjects())
			sb.append(businessObject + " ");
		bw.write(sb.toString().trim()+"\t");
	}
	/**
	 * Writes the heading "label\taction\tbusinessObject\taddition\n" to the tsv file
	 * containing the refactoring information of the activity labels.
	 * @param bw	the BufferedWriter used to write to the tsv file
	 * @throws IOException 
	 */
	private void createHeader(BufferedWriter bw) throws IOException {
		bw.write("label\t");
		bw.write("action\t");
		bw.write("businessObject\t");
		bw.write("addition\n");
	}
}
