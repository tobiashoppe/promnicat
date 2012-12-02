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
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.AnalysisConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.AnalysisHelper;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModel;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModelRevision;

/**
 * This analysis looks for actions that correspond to reconciliation and modeling phases 
 * in revisions and takes down if they have occurred in the revisions.
 * 
 * @author Tobias Metzke
 *
 */
public class CMROccurrencesAnalysis extends AbstractMetricsAnalysis {

	/**
	 * @see AbstractMetricsAnalysis#AbstractMetricsAnalysis(Map, Map)
	 */
	public CMROccurrencesAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> analyzedModels) {
		super(modelsToAnalyze, analyzedModels);
	}
	

	/**
	 * @see AbstractMetricsAnalysis#AbstractMetricsAnalysis(Map)
	 */
	public CMROccurrencesAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		super(modelsToAnalyze);
	}

	@Override
	protected String addCSVHeader() {
		return "model" + CSV_ITEMSEPARATOR + "revision" + CSV_ITEMSEPARATOR + "CMR elements";
	}

	@Override
	protected String toCsvString(ProcessEvolutionModel model) {
		StringBuilder builder = new StringBuilder();
		for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
			builder.append("\n" + model.getName() + CSV_ITEMSEPARATOR + revision.getRevisionNumber() + CSV_ITEMSEPARATOR);
			if (revision.getMetricKeys().contains(AnalysisConstants.MODELING.getDescription()))
				builder.append(AnalysisConstants.MODELING.getDescription() + ",");
			if (revision.getMetricKeys().contains(AnalysisConstants.RECONCILIATION.getDescription()))
				builder.append(AnalysisConstants.RECONCILIATION.getDescription());
		}
		return builder.toString();
	}

	@Override
	protected void performAnalysis() {
		IAnalysis addDeleteAnalysis = AnalysisHelper.analyzeAdditionsAndDeletions(modelsToAnalyze, true);
		analyzedModels = addDeleteAnalysis.getAnalyzedModels();
		IAnalysis movedElementsAnalysis = AnalysisHelper.analyzeElementMovements(modelsToAnalyze, analyzedModels);
		analyzedModels = movedElementsAnalysis.getAnalyzedModels();
		for (ProcessEvolutionModel model : analyzedModels.values()) {
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
				findModeling(revision);
				findReconciliation(revision);
			}
		}
		
		
	}

	/**
	 * Marks a revision as containing modeling if additions or deletions have taken place
	 * in this revision
	 * @param revision the revision to find modeling in
	 */
	private void findModeling(ProcessEvolutionModelRevision revision) {
		Collection<AnalysisConstants> metrics = AnalysisHelper.getIndividualMetrics();
		for (AnalysisConstants metric : metrics) {
			if(!revision.get(metric.getDescription() + AnalysisConstants.ADDITIONS.getDescription()).equals(new Double(0)) ||
					!revision.get(metric.getDescription() + AnalysisConstants.DELETIONS.getDescription()).equals(new Double(0)))
					revision.add(AnalysisConstants.MODELING.getDescription(), 1);
		}
	}

	/**
	 * Marks a revision as containing reconciliation if layout changes have taken place
	 * in this revision
	 * @param revision the revision to find reconciliation in
	 */
	private void findReconciliation(ProcessEvolutionModelRevision revision) {
		if (!revision.get(AnalysisConstants.NEW_LAYOUT.getDescription()).equals(new Double(0)))
			revision.add(AnalysisConstants.RECONCILIATION.getDescription(), 1);
	}

}
