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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.AnalysisHelper;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModel;

/**
 * This analysis concentrates on several aspects and is therefore a high-level view on the models.
 * Different analyses are used herein to draw an overall picture of the model collection.
 * 
 * @author Tobias Metzke
 *
 */
public class HighLevelAnalysis extends AbstractAnalysis {

	private Collection<IAnalysis> analyses = new ArrayList<IAnalysis>();
	private boolean includeSubprocesses;
	
	/**
	 * @param modelsToAnalyze
	 * @param includeSubprocesses
	 */
	public HighLevelAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze, boolean includeSubprocesses) {
		this(modelsToAnalyze, null, includeSubprocesses);
	}

	/**
	 * @see AbstractAnalysis#AbstractAnalysis(Map, Map)
	 * @param includeSubprocesses flag deciding on whether to also include subprocesses in the analyses
	 */
	public HighLevelAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> analyzedModels, boolean includeSubprocesses) {
		super(modelsToAnalyze, analyzedModels);
		this.includeSubprocesses = includeSubprocesses;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <br>This analysis combines several analyses and passes their results on to the next one every time.
	 * In the end, the analyzed models include the results of all the herein listed analyses. 
	 */
	@Override
	protected void performAnalysis() {
		
		IAnalysis modelGrowth = new ModelGrowthAnalysis(modelsToAnalyze);
		analyzedModels = modelGrowth.getAnalyzedModels();
		IAnalysis highLowSame = new HighLowSameAnalysis(modelsToAnalyze, analyzedModels, AnalysisHelper.getProcessModelMetrics());
		analyzedModels = highLowSame.getAnalyzedModels();
		IAnalysis lazyRevisions = new LazyRevisionsAnalysis(modelsToAnalyze, analyzedModels, includeSubprocesses, AnalysisHelper.getIndividualMetrics());
		analyzedModels = lazyRevisions.getAnalyzedModels();
		IAnalysis modellanguage = new ModelLanguageUsageAnalysis(modelsToAnalyze, analyzedModels, AnalysisHelper.getModelLanguageMetrics());
		analyzedModels = modellanguage.getAnalyzedModels();
		IAnalysis cmr = new CMRAnalysis(modelsToAnalyze, analyzedModels);
		analyzedModels = cmr.getAnalyzedModels();
		Collections.addAll(analyses, modelGrowth, highLowSame, lazyRevisions, modellanguage, cmr);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <br>This analysis displays the results of all its used analyses by passing the request of getting the
	 * result string on to them and concatenating the results.
	 */
	@Override
	protected String getResultCSVString() {
		StringBuilder builder = new StringBuilder();
		for (IAnalysis analysis : analyses)
			builder
				.append(analysis.toResultCSVString())
				.append("\n\n");
		return builder.toString();
	}

}
