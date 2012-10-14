package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity;

import org.jbpt.alignment.LabelEntity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/** A superclass for all similarity metrics from the simmetrics library */
public abstract class SimMetrics<Metric extends AbstractStringMetric> extends AbstractSimilarity {
	private final Metric METRIC;

	public SimMetrics(Metric metric) {
		METRIC = metric;
	}

	@Override
	public float compute(LabelEntity first, LabelEntity second) {
		return METRIC.getSimilarity(first.getLabel(), second.getLabel());
	}
}