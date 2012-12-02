/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.metricAnalyses;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModel;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModelRevision;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.util.ProcessMetricConstants;

/**
 * This analysis looks for differences in metrics between revisions. Differences are
 * taken down in an absolute manner. (e.g. number of nodes increased by 5)
 * 
 * @author Tobias Metzke
 * 
 */
public class DifferenceAnalysis extends AbstractMetricsAnalysis {

	protected Collection<ProcessMetricConstants.METRICS> metrics;

	public DifferenceAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze, Map<String, ProcessEvolutionModel> analyzedModels, Collection<ProcessMetricConstants.METRICS> metrics) {
		super(modelsToAnalyze,analyzedModels);
		this.metrics = metrics;
	}
	
	public DifferenceAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze, Collection<ProcessMetricConstants.METRICS> metrics) {
		this(modelsToAnalyze, null, metrics);
	}

	@Override
	protected void performAnalysis() {

		for (ProcessEvolutionModel model : modelsToAnalyze.values()) {
			ProcessEvolutionModel newModel = new ProcessEvolutionModel(
					model.getName());
			Map<ProcessMetricConstants.METRICS, Double> oldValues = getInitialMetricsValues();
			// perform the analysis of differences for every revision and metric
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
				ProcessEvolutionModelRevision newRevision = new ProcessEvolutionModelRevision(
						revision.getRevisionNumber());
				for (ProcessMetricConstants.METRICS metric : metrics) {
					double actualValue = revision.get(metric);
					double oldValue = oldValues.get(metric);
					double difference = calculateDifference(metric,
							actualValue, oldValue);
					// save the new value as back-reference for the next
					// revision
					oldValues.put(metric, actualValue);
					newRevision.add(metric, difference);
				}
				newModel.add(newRevision);
			}
			analyzedModels.put(model.getName(), newModel);
		}

	}
	
	/**
	 * initialize a first collection of metrics zero-values to have a starting
	 * point for the first revision of a model to be compared to
	 * @return
	 */
	private Map<ProcessMetricConstants.METRICS, Double> getInitialMetricsValues() {
		Map<ProcessMetricConstants.METRICS, Double> oldValues = new HashMap<ProcessMetricConstants.METRICS, Double>();
		for (ProcessMetricConstants.METRICS metric : metrics)
			oldValues.put(metric, new Double(0));
		return oldValues;
	}

	/**
	 * execution of the difference analysis
	 * @param metric the metric to be analyzed
	 * @param revision the actual revision containing its metric values
	 * @param oldValues the previous set of values
	 * @param relative
	 * @return
	 */
	protected double calculateDifference(ProcessMetricConstants.METRICS metric, double actualValue, double oldValue) {
		return actualValue - oldValue;
	}

	@Override
	protected String addCSVHeader() {
		StringBuilder builder = new StringBuilder()
			.append("Process Model" + CSV_ITEMSEPARATOR)
			.append("Revision" + CSV_ITEMSEPARATOR);
		for (ProcessMetricConstants.METRICS metric : metrics)
			builder.append(metric.name() + CSV_ITEMSEPARATOR);
	builder.append("grows continuously?");
	return builder.toString();
	}

	@Override
	protected String toCsvString(ProcessEvolutionModel model) {
		// collect all information from the revisions
		// display each revision in a separate line
		StringBuilder builder = new StringBuilder();
		for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
			builder
				.append("\n")
				.append(model.getName())
				.append(CSV_ITEMSEPARATOR + revision.getRevisionNumber());
			for (ProcessMetricConstants.METRICS metric : metrics) 
				builder.append(CSV_ITEMSEPARATOR + revision.get(metric).intValue());
		}
		builder.append(CSV_ITEMSEPARATOR + model.isGrowing());
		return builder.toString();
	}
}
