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

import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.AnalysisConstants;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.AnalysisHelper;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModel;

/**
 * Simple analysis looking for models that grow continuously, 
 * meaning that no metric was lowered throughout the history
 * of the model.
 * 
 * @author Tobias Metzke
 *
 */
public class ModelGrowthAnalysis extends AbstractAnalysis {

	private int growingModels = 0;
	private int numberOfModels = 0;
	
	public ModelGrowthAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		super(modelsToAnalyze);
	}

	public ModelGrowthAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> analyzedModels) {
		super(modelsToAnalyze, analyzedModels);
	}

	@Override
	protected void performAnalysis() {
		// the difference analysis marks models as growing or not growing
		IAnalysis addDeleteAnalysis = AnalysisHelper.analyzeAdditionsAndDeletions(modelsToAnalyze, true);
		analyzedModels = addDeleteAnalysis.getAnalyzedModels();
		numberOfModels = analyzedModels.size();
		for (ProcessEvolutionModel model : analyzedModels.values())
			if (model.isGrowing()) growingModels++;
	}
	
	@Override
	protected String getResultCSVString() {
		StringBuilder builder = new StringBuilder()
			.append(AnalysisConstants.NUM_MODELS.getDescription() + CSV_ITEMSEPARATOR)
			.append(AnalysisConstants.NUM_GROWING.getDescription() + CSV_ITEMSEPARATOR)
			.append(AnalysisConstants.NUM_NOT_GROWING.getDescription())
			.append("\n")
			.append(numberOfModels + CSV_ITEMSEPARATOR)
			.append(growingModels + CSV_ITEMSEPARATOR)
			.append(numberOfModels - growingModels);
		return builder.toString();
	}

}
