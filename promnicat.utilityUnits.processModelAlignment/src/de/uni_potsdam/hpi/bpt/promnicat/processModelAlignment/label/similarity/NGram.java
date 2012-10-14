package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

public class NGram extends SimMetrics<QGramsDistance> {

	public NGram() {
		super(new QGramsDistance());
	}

}
