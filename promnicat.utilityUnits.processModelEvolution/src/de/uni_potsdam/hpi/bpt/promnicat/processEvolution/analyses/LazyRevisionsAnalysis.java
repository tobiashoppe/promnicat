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
package de.uni_potsdam.hpi.bpt.promnicat.processEvolution.analyses;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.AnalysisConstants;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.AnalysisHelper;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModel;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModelRevision;

/**
 * This analysis looks out for revisions that do not alter any metric and are therefore called <code>Lazy Revisions</code>
 * @author Tobias Metzke
 *
 */
public class LazyRevisionsAnalysis extends AbstractAnalysis {

	private boolean includeSubprocesses;
	private Collection<AnalysisConstants> metrics;
	private int numberOfAlteringRevisions = 0;
	private int numberOfRevisions = 0;

	/**
	 * @see AbstractAnalysis#AbstractAnalysis(Map, Map)
	 * @param metrics the metrics to analyze the changes for
	 * @param includeSubprocesses flag to decide whether to include subprocesses in the analysis as well 
	 */
	public LazyRevisionsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze, 
			Map<String, ProcessEvolutionModel> analyzedModels,
			boolean includeSubprocesses, Collection<AnalysisConstants> metrics) {
		super(modelsToAnalyze, analyzedModels);
		this.includeSubprocesses = includeSubprocesses;
		this.metrics = metrics;
	}
	
	public LazyRevisionsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze, 
			boolean includeSubpreocesses, Collection<AnalysisConstants> metrics) {
		this(modelsToAnalyze, null, includeSubpreocesses, metrics);
	}

	@Override
	protected void performAnalysis() {
		Map<String, ProcessEvolutionModel> modelsWithAlteringRevisions = new HashMap<String, ProcessEvolutionModel>();

		// analyze additions and deletions and find altering revisions
		IAnalysis addsDeletes = AnalysisHelper.analyzeAdditionsAndDeletions(modelsToAnalyze, includeSubprocesses);
		analyzedModels = addsDeletes.getAnalyzedModels();
		for (ProcessEvolutionModel model : analyzedModels.values()) {
			numberOfRevisions += model.getRevisions().size();
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values())
				for (AnalysisConstants metric : metrics)
					if (!revision.get(metric.getDescription() + AnalysisConstants.ADDITIONS.getDescription()).equals(new Double(0))
							|| !revision.get(metric.getDescription() + AnalysisConstants.ADDITIONS.getDescription()).equals(new Double(0))) {
						if (!modelsWithAlteringRevisions.containsKey(model.getName()))
							modelsWithAlteringRevisions.put(model.getName(), new ProcessEvolutionModel(model.getName()));
						modelsWithAlteringRevisions.get(model.getName()).add(revision);
						break;
					}
		}
		
		// if revision not already present as altering revision,
		// add it to altering revision if it is one concerning layout changes
		IAnalysis layoutChanges = AnalysisHelper.analyzeElementMovements(modelsToAnalyze, analyzedModels);
		analyzedModels = layoutChanges.getAnalyzedModels();
		for (ProcessEvolutionModel model : analyzedModels.values())
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values())
				if (modelsWithAlteringRevisions.containsKey(model.getName()))
					if (!modelsWithAlteringRevisions.get(model.getName()).getRevisions().containsKey(revision.getRevisionNumber()))
						if (!revision.get(AnalysisConstants.NEW_LAYOUT.getDescription()).equals(new Double(0)))
							modelsWithAlteringRevisions.get(model.getName()).add(revision);
		
		for (ProcessEvolutionModel model : modelsWithAlteringRevisions.values()) {
			ProcessEvolutionModel analyzedModel = analyzedModels.get(model.getName());
			if (analyzedModel != null) {
					analyzedModel.setNumberOfAlteringRevisions(model.getRevisions().size());
			}
			numberOfAlteringRevisions += model.getRevisions().size();
		}
	}

	@Override
	protected String getResultCSVString() {
		return new StringBuilder()
			.append(AnalysisConstants.NUM_REVISIONS.getDescription() + CSV_ITEMSEPARATOR)
			.append(AnalysisConstants.ALTERING_REVISIONS.getDescription() + CSV_ITEMSEPARATOR)
			.append(AnalysisConstants.UNALTERING_REVISIONS.getDescription() +CSV_ITEMSEPARATOR)
			.append("\n")
			.append(numberOfRevisions +CSV_ITEMSEPARATOR)
			.append(numberOfAlteringRevisions +CSV_ITEMSEPARATOR)
			.append(numberOfRevisions - numberOfAlteringRevisions +CSV_ITEMSEPARATOR)
			.toString();
	}

}
