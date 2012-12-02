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

import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.AbstractAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModel;

/**
 * Metrics analyses are just like all other analyses.
 * This class is for comfortability reasons only.
 * It provides a template method for building the result string
 * since metric analyses have to display results for every
 * model and this should not be repeatedly be used in every
 * metric analysis.
 * <br>Metric analyses only have to provide the method
 * that describes how a model is displayed and what the
 * header looks like. 
 * 
 * @author Tobias Metzke
 * 
 */
public abstract class AbstractMetricsAnalysis extends AbstractAnalysis{

	/**
	 * @see AbstractAnalysis#AbstractAnalysis(Map, Map)
	 */
	public AbstractMetricsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> analyzedModels) {
		super(modelsToAnalyze, analyzedModels);
	}

	/**
	 * @see AbstractAnalysis#AbstractAnalysis(Map)
	 */
	public AbstractMetricsAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		super(modelsToAnalyze);
	}
	
	@Override
	protected String getResultCSVString() {
		StringBuilder resultStringBuilder = new StringBuilder(addCSVHeader());
		// collect result from each model
		for (ProcessEvolutionModel model : analyzedModels.values())
			resultStringBuilder.append(toCsvString(model));
		return resultStringBuilder.toString();
	}
	
	/**
	 * add the specific header to the analysis files that includes
	 * the model path, revision and the metrics of this revision
	 * @return a String representation of the process model metrics
	 * separated by the {@link ProcessMetrics#CSV_ITEMSEPARATOR}.
	 */
	protected abstract String addCSVHeader();
	
	/**
	 * helper method that puts the real data of the metrics into format.
	 * every model revision is put into a new line with the according metrics
	 * @param model that should be displayed in CSV format
	 * @return a String representation of the formatted model results
	 */
	protected abstract String toCsvString(ProcessEvolutionModel model);
}
