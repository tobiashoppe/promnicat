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
package de.uni_potsdam.hpi.bpt.promnicat.processEvolution.analyses.metricAnalyses;

import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.AnalysisConstants;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModel;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModelRevision;

/**
 * This analysis finds the iterations of CMR phases in models
 * and adds the number of found iterations to the model.
 * 
 * @author Tobias Metzke
 *
 */
public class CMRIterationsAnalysis extends AbstractMetricsAnalysis {

	/**
	 * @see AbstractMetricsAnalysis#AbstractMetricsAnalysis(Map, Map)
	 */
	public CMRIterationsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> analyzedModels) {
		super(modelsToAnalyze, analyzedModels);
	}

	/**
	 * @see AbstractMetricsAnalysis#AbstractMetricsAnalysis(Map)
	 */
	public CMRIterationsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		super(modelsToAnalyze);
	}

	@Override
	protected String addCSVHeader() {
		return "model" + CSV_ITEMSEPARATOR + "iterations";
	}

	@Override
	protected String toCsvString(ProcessEvolutionModel model) {
		return "\n" + model.getName() + CSV_ITEMSEPARATOR + model.getCMRIterations();
	}

	@Override
	protected void performAnalysis() {
		IAnalysis cmrOccurrencesAnalysis = new CMROccurrencesAnalysis(modelsToAnalyze);
		analyzedModels = cmrOccurrencesAnalysis.getAnalyzedModels();
		/* look for the CMR elements found in every revision
		 * and check for the last phase of the previous revision
		 * and count iterations accordingly, e.g.:
		 * - first revision contains modeling and reconciliation
		 * - modeling has to come first, since nothing can be adjusted if it hasn't been modeled before
		 * - reconciliation comes afterwards and the revision ends with reconciliation, since we do not have further
		 *   information on what changed at what point in time inside a revision and do not know if modeling happend afterwards
		 * - second revision contains both again and it is assumed for now that the last phase was continued
		 *   in this revision first and that the modeling phase started after the reconciliation here
		 *   (FIXME YES, THIS ASSUMPTION IS A MAJOR LIMITATION SO FAR BUT WE DO NOT HAVE FURTHER INFO ON
		 *   WHAT CHANGED INSIDE A REVISION AT A CERTAIN POINT IN TIME)
		 */
		for (ProcessEvolutionModel model : analyzedModels.values()) {
			AnalysisConstants lastCMRElement = AnalysisConstants.NONE;
			int iterations = 0;
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
				if (isModelingOnly(lastCMRElement, revision)) {
					lastCMRElement = AnalysisConstants.MODELING;
					if (isLastRevision(revision, model))
						iterations++;
				} else if (isConcludedIteration(lastCMRElement, revision)) {
					iterations++;
					lastCMRElement = AnalysisConstants.RECONCILIATION;
				}
					
			}
			model.setCMRIterations(iterations);
		}
	}

	/**
	 * the modeling phase is found if (only modeling is contained) OR
	 * (reconciliation AND modeling are contained AND the last phase was reconciliation) OR
	 * (no phase is detected AND the last phase was modeling)
	 * @param lastCMRElement the last phase of the previous revision
	 * @param revision the actual revision
	 * @return <code>true</code> if the before mentioned condition is met 
	 */
	private boolean isModelingOnly(AnalysisConstants lastCMRElement,
			ProcessEvolutionModelRevision revision) {
		return (containsModeling(revision) && !containsReconciliation(revision)) ||
		(containsModeling(revision) & containsReconciliation(revision) & lastCMRElement.equals(AnalysisConstants.RECONCILIATION)) ||
		(!containsModeling(revision) && !containsReconciliation(revision) && lastCMRElement.equals(AnalysisConstants.MODELING));
	}

	/**
	 * an iteration is concluded if reconciliation is contained AND reconciliation was not the last phase
	 * @param lastCMRElement
	 * @param revision
	 * @return
	 */
	private boolean isConcludedIteration(AnalysisConstants lastCMRElement,
			ProcessEvolutionModelRevision revision) {
		return !lastCMRElement.equals(AnalysisConstants.RECONCILIATION) && containsReconciliation(revision);
	}

	private boolean containsReconciliation(ProcessEvolutionModelRevision revision) {
		return revision.getMetrics().containsKey(AnalysisConstants.RECONCILIATION.getDescription());
	}

	private boolean containsModeling(ProcessEvolutionModelRevision revision) {
		return revision.getMetrics().containsKey(AnalysisConstants.MODELING.getDescription());
	}

	private boolean isLastRevision(ProcessEvolutionModelRevision revision, ProcessEvolutionModel model) {
		return model.getRevisions().lastKey() == revision.getRevisionNumber();
	}

}
