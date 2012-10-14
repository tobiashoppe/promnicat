package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;

/**
 * Jaccard is a word-based similarity. It tries to exactly match
 * a word in one label to a word in the other label. If it finds
 * such a match, the similarity is increased, else it is lowered.<br>
 * <b>Should only be used, if typos can definitely be excluded.</b>  
 * @see JaccardSimilarity */
public class Jaccard extends SimMetrics<JaccardSimilarity> {

	public Jaccard() {
		super(new JaccardSimilarity());
	}
}
