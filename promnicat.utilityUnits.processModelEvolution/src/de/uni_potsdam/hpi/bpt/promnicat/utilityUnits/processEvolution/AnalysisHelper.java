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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.HighLevelAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.metricAnalyses.AdditionsDeletionsAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.metricAnalyses.DifferenceAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.metricAnalyses.ModelLanguageAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.metricAnalyses.MovedElementsAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.metricAnalyses.RelativeDifferenceAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModel;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.util.ProcessMetricConstants;

/**
 * A helper class that builds a bridge to the specific analyses.
 * 
 * @author Tobias Metzke
 *
 */
public class AnalysisHelper {

	
	public static IAnalysis analyzeDifferencesInMetrics(Map<String, ProcessEvolutionModel> modelsToAnalyze, boolean useRelativeDifferences) {
		IAnalysis differenceAnalysis = useRelativeDifferences ? 
				new RelativeDifferenceAnalysis(modelsToAnalyze, getProcessModelMetrics()) :
				new DifferenceAnalysis(modelsToAnalyze, getProcessModelMetrics());
		return differenceAnalysis;
	}

	public static IAnalysis analyzeAdditionsAndDeletions(Map<String, ProcessEvolutionModel> modelsToAnalyze, boolean includeSubprocesses) {
		return new AdditionsDeletionsAnalysis(modelsToAnalyze, getIndividualMetrics(), includeSubprocesses);
	}
	
	public static IAnalysis analyzeModelLanguage(Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		return new ModelLanguageAnalysis(modelsToAnalyze);
	}

	public static IAnalysis analyzeElementMovements(
			Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		return new MovedElementsAnalysis(modelsToAnalyze);
	}
	
	public static IAnalysis analyzeElementMovements(
			Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> alreadyAnalyzedModels) {
		return new MovedElementsAnalysis(modelsToAnalyze, alreadyAnalyzedModels);
	}

	public static IAnalysis highLevelAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze, boolean includeSubprocesses) throws IOException {
		return new HighLevelAnalysis(modelsToAnalyze, includeSubprocesses);
	}

	/*
	 * ------------------------------------------------------------------------
	 * GETTERS FOR METRICS COLLECTIONS
	 * ------------------------------------------------------------------------
	 */
	/**
	 * access to the herein defined metrics that are analyzed per model revision
	 * and displayed in analysis results
	 * @return the metrics all model revisions are analyzed by
	 */
	public static Collection<ProcessMetricConstants.METRICS> getProcessModelMetrics() {
		Collection<ProcessMetricConstants.METRICS> processModelMetrics = new ArrayList<ProcessMetricConstants.METRICS>();
		Collections.addAll(processModelMetrics, 
				ProcessMetricConstants.METRICS.NUM_EVENTS,	ProcessMetricConstants.METRICS.NUM_ACTIVITIES, 
				ProcessMetricConstants.METRICS.NUM_GATEWAYS,ProcessMetricConstants.METRICS.NUM_NODES, 
				ProcessMetricConstants.METRICS.NUM_EDGES, ProcessMetricConstants.METRICS.NUM_ROLES,
				ProcessMetricConstants.METRICS.NUM_DATA_NODES);
		return processModelMetrics;
	}
	
	/**
	 * @return the metrics used in additions and deletions analysis
	 */
	public static Collection<AnalysisConstants> getIndividualMetrics() {
		Collection<AnalysisConstants> individualMetrics = new ArrayList<AnalysisConstants>();
		Collections.addAll(individualMetrics,
				AnalysisConstants.EVENTS, AnalysisConstants.ACTIVITIES, 
				AnalysisConstants.GATEWAYS, AnalysisConstants.DOCUMENTS, 
				AnalysisConstants.ROLES, AnalysisConstants.EDGES);
		return individualMetrics;
	}
	
	public static Collection<AnalysisConstants> getModelLanguageMetrics() {
		Collection<AnalysisConstants> languageMetrics = new ArrayList<AnalysisConstants>();
		Collections.addAll(languageMetrics,
				AnalysisConstants.CONTROL_ORGA_DATA, AnalysisConstants.CONTROL_DATA_ORGA,
				AnalysisConstants.DATA_CONTROL_ORGA, AnalysisConstants.DATA_ORGA_CONTROL, 
				AnalysisConstants.ORGA_CONTROL_DATA, AnalysisConstants.ORGA_DATA_CONTROL,
				AnalysisConstants.CONTROL_DATA, AnalysisConstants.CONTROL_ORGA,
				AnalysisConstants.DATA_CONTROL, AnalysisConstants.DATA_ORGA,
				AnalysisConstants.ORGA_CONTROL, AnalysisConstants.ORGA_DATA,
				AnalysisConstants.CONTROL_FLOW, AnalysisConstants.DATA_FLOW,
				AnalysisConstants.ORGANISATION);
		return languageMetrics;
	}
}
