package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity;


/**
 * Uses a sliding window over label1 and label2. If a character in this window
 * can't be matched to a character in the other window, the similarity is decreased.<br>
 * Additionally, labels with a common prefix are boosted in similarity. 
 * @see uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler
 */
public class JaroWinkler extends SimMetrics<uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler> {
	public JaroWinkler() {
		super(new uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler());
	}

}
