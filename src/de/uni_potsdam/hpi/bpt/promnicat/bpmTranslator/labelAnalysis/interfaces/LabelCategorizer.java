package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.labelAnalysis.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.structure.Activity;

public interface LabelCategorizer {
	
	/**
	 * Returns the label style of the given activity / model collection.
	 * Possible results (English models): 'AN', 'VO', 'DES'
	 */
	public String getLabelStyle(Activity activity);
	public HashMap<String,String> getLabelStyle(ArrayList<ArrayList<Activity>> modelCollection);

}
