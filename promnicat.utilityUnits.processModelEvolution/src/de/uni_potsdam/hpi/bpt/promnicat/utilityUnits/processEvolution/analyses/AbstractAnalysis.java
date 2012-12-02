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

import java.util.HashMap;
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.AnalysisConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model.ProcessEvolutionModel;


/**
 * Every analysis shares several attributes. There is two ways to create them:
 * 1. with models that shall be analyzed
 * 2. with 1. and also models that have been analyzed already
 * In the second case, the two model collections are merged at the end of the analysis
 * and the merged collection is returned. With that, 
 * in a chain of analyses you can pass the results
 * on and work with them in a later analysis for example.
 * Also you do not have to keep the results in separate variables
 * after every analysis for example.
 * 
 * @author Tobias Metzke
 *
 */
public abstract class AbstractAnalysis implements IAnalysis {

	/**
	 * the models that shall be analyzed by this analysis
	 */
	protected Map<String, ProcessEvolutionModel> modelsToAnalyze;
	
	protected final String CSV_ITEMSEPARATOR = AnalysisConstants.ITEMSEPARATOR.getDescription();
	
	/**
	 * the models that are passed on to this analysis and have been analyzed by a different analysis
	 */
	protected Map<String, ProcessEvolutionModel> alreadyAnalyzedModels;
	
	/**
	 * the resulting models from this analysis. they are not initialized until the analysis was performed
	 * by calling either the {@link #toResultCSVString()} or the {@link #getAnalyzedModels()} method
	 */
	protected Map<String, ProcessEvolutionModel> analyzedModels;

	/**
	 * constructor with given models that shall be analyzed in this analysis
	 * @param modelsToAnalyze models that shall be analyzed in this analysis
	 */
	public AbstractAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		this(modelsToAnalyze, null);
	}
	
	/**
	 * constructor to pass on already analyzed models that shall be merged with the result of this analysis
	 * @param modelsToAnalyze models that shall be analyzed in this analysis
	 * @param alreadyAnalyzedModels models that have been analyzed in a prior analysis
	 */
	public AbstractAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze, Map<String, ProcessEvolutionModel> alreadyAnalyzedModels) {
		this.modelsToAnalyze = modelsToAnalyze;
		this.alreadyAnalyzedModels = alreadyAnalyzedModels;
	}
	
	@Override
	public String toResultCSVString() {
		performIfNotYetDone();
		return getResultCSVString();
	}

	@Override
	public Map<String, ProcessEvolutionModel> getAnalyzedModels() {
		performIfNotYetDone();
		if(alreadyAnalyzedModels != null)
			return merge(analyzedModels,alreadyAnalyzedModels);
		return analyzedModels;
	}
	
	/**
	 * if the analyzed models do not yet exist, the analysis
	 * has not been performed already and needs to be executed
	 */
	private void performIfNotYetDone() {
		if(analyzedModels == null) {
			analyzedModels = new HashMap<String, ProcessEvolutionModel>();
			performAnalysis();
		}
	}

	/**
	 * Already analyzed models and the actual results are merged by checking
	 * for equal models in the collections according to their names that can be merged
	 * and by directly adding the models to the merge results if they only exist in one collection  
	 * @param firstModelMap
	 * @param secondModelMap
	 * @return the merged model collection
	 */
	private Map<String, ProcessEvolutionModel> merge(Map<String, ProcessEvolutionModel> firstModelMap,
			Map<String, ProcessEvolutionModel> secondModelMap) {
		Map<String, ProcessEvolutionModel> newModelMap = new HashMap<String, ProcessEvolutionModel>(secondModelMap);
		// add models of first collection to a new collection,
		// merge with models of second collection if necessary
		for (ProcessEvolutionModel firstModel : firstModelMap.values()) {
			ProcessEvolutionModel secondModel = secondModelMap.get(firstModel.getName());
			ProcessEvolutionModel newModel;
			if (secondModel != null)
				newModel = new ProcessEvolutionModel(firstModel, secondModel);
			else
				newModel = firstModel;
			newModelMap.put(newModel.getName(), newModel);
		}
		// add models of second collection
		// that haven't already been added before
		for (ProcessEvolutionModel secondModel : secondModelMap.values()) {
			if (!newModelMap.containsKey(secondModel.getName()))
				newModelMap.put(secondModel.getName(), secondModel);
		}
		return newModelMap;
	}

	/**
	 * executes the analysis method every subclass defines itself
	 */
	protected abstract void performAnalysis();
	
	/**
	 * converts the analysis results into a proper CSV format 
	 * @return the CSV format of the results as String
	 */
	protected abstract String getResultCSVString();
}
