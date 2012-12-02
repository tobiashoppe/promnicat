package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/** 
 * converts label1 into label2 by adding/removing/replacing characters
 * as necessary. The more steps are needed, the less similar the labels.
 * @see Levenshtein
 */
public class StringEditDistance extends SimMetrics<Levenshtein> {

	public StringEditDistance() {
		super(new Levenshtein());
	}

}
