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

import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.analyses.metricAnalyses.CMRIterationsAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModel;

/**
 * Analysis on the three phases <code>Comprehension</code>, <code>Modeling</code> and <code>Reconciliation</code>
 * mentioned in the study by Pinggera et al. on the process of process modeling.
 * 
 * @author Tobias Metzke
 *
 */
public class CMRAnalysis extends AbstractAnalysis {

	/**
	 * threshold to determine if a number of iterations is respectively high or middle
	 */
	private static final int MIDDLE = 3;
	
	/**
	 * threshold to determine if a number of iterations is respectively low or middle 
	 */
	private static final int LOW = 1;
	
	/**
	 * counter for the number of models with a high/low/medium number of iterations
	 */
	private int numberOfHighIterationNumbers, numberOfMiddleIterationNumbers, numberOfLowIterationNumbers;
	
	/**
	 * @see AbstractAnalysis#AbstractAnalysis(Map, Map)
	 */
	public CMRAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> analyzedModels) {
		super(modelsToAnalyze, analyzedModels);
		numberOfHighIterationNumbers = numberOfLowIterationNumbers = numberOfMiddleIterationNumbers = 0;
	}

	/**
	 * @see AbstractAnalysis#AbstractAnalysis(Map)
	 */
	public CMRAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		this(modelsToAnalyze, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <br>This analysis looks for the number of iterations through the CMR phases
	 * for each model and places the models in groups according to that number
	 */
	@Override
	protected void performAnalysis() {
		IAnalysis iterationsAnalysis = new CMRIterationsAnalysis(modelsToAnalyze);
		analyzedModels = iterationsAnalysis.getAnalyzedModels();
		for (ProcessEvolutionModel model : analyzedModels.values()) {
			int iterations = model.getCMRIterations();
			if (iterations <= LOW)
				numberOfLowIterationNumbers++;
			else if (iterations <= MIDDLE)
				numberOfMiddleIterationNumbers++;
			else
				numberOfHighIterationNumbers++;
		}
		
	}

	@Override
	protected String getResultCSVString() {
		return new StringBuilder()
			.append("iterations > " + MIDDLE)
			.append(CSV_ITEMSEPARATOR + "iterations > " + LOW)
			.append(CSV_ITEMSEPARATOR + "iterations <= " + LOW)
			.append("\n")
			.append(numberOfHighIterationNumbers)
			.append(CSV_ITEMSEPARATOR + numberOfMiddleIterationNumbers)
			.append(CSV_ITEMSEPARATOR + numberOfLowIterationNumbers)
			.toString();
	}

}
