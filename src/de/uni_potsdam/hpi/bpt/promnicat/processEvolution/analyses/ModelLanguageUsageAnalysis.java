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
 * This analysis looks for the order of used model language elements
 * in the history of the models.
 * <br>Language elements are divided into
 * <code>Control Flow</code>, <code>Organisation</code> and <code>Data</code>.
 * <br>
 * <br>Example of model language order:
 * <br>Control Flow before Organisation before Data
 * 
 * @author Tobias Metzke
 *
 */
public class ModelLanguageUsageAnalysis extends AbstractAnalysis {

	private Map<String, Integer> results = new HashMap<String, Integer>();
	private Collection<AnalysisConstants> metrics; 
	
	/**
	 * @see AbstractAnalysis#AbstractAnalysis(Map, Map)
	 * @param metrics the metrics to analyze by
	 */
	public ModelLanguageUsageAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze, Map<String, ProcessEvolutionModel> analyzedModels, Collection<AnalysisConstants> metrics) {
		super(modelsToAnalyze, analyzedModels);
		this.metrics = metrics;
	}
	
	public ModelLanguageUsageAnalysis(Map<String, ProcessEvolutionModel> modelsToAnalyze, Collection<AnalysisConstants> metrics) {
		this(modelsToAnalyze, null, metrics);
	}

	@Override
	protected void performAnalysis() {
		IAnalysis modelLanguage = AnalysisHelper.analyzeModelLanguage(modelsToAnalyze);
		analyzedModels = modelLanguage.getAnalyzedModels();
		// analyze the used model elements and find the order of usage
		for (ProcessEvolutionModel model : analyzedModels.values()) {
			AnalysisConstants behavior = AnalysisConstants.NONE;
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
				Collection<String> languageElements = revision.getMetrics().keySet();
				behavior = findBehavior(languageElements,behavior);
			}
			model.setLanguageUsage(behavior);
			String behaviorString = behavior.getDescription();
			// add 1 to the number of found occurrences of the found behavior
			int numberOfBehaviorOccurrences = results.containsKey(behaviorString) ? results.get(behaviorString) : 0;
			results.put(behaviorString, ++numberOfBehaviorOccurrences);
		}
	}
	
	/**
	 * recursive method that finds the order of used model language elements
	 * by looking out for the contained elements per revision and classifying
	 * the behavior according to the behavior of previous revisions.
	 * 
	 * <br><br>Example:
	 * <br>the second revision shall be analyzed
	 * <br>in the first revision, only control flow was contained
	 * <br>in the second revision we now know that the order so far is <code>Control Flow</code>
	 * <br>in the second revision a pool and two lanes are added
	 * <br>the second revision therefore contains <code>Control Flow</code> and <code>Organisation</code>
	 * <br>since <code>Control Flow</code> was already in the behavior, we now add <code>Organisation</code>
	 * and get to <code>Control Flow before Organisation</code>
	 * 
	 * @param languageElements the element classes contained in the revision (control flow, organisation and data are possible)
	 * @param behavior the order of model language elements so far
	 * @return the new order of model language elements
	 */
	/* TODO more fine grained look on what is used per revision
	 * e.g. Control Flow and Organisation together in first revision
	 * leads to Control Flow before Organisation so far.
	 * Either more information on additions over time within a revision
	 * is provided or new patterns like Organisation+Control Flow before Data
	 * must be added.
	 */
	private static AnalysisConstants findBehavior(Collection<String> languageElements,
			AnalysisConstants behavior) {
		AnalysisConstants newBehavior = behavior;
		String controlConstant = AnalysisConstants.CONTROL_FLOW.getDescription();
		String dataConstant = AnalysisConstants.DATA_FLOW.getDescription();
		String orgaConstant = AnalysisConstants.ORGANISATION.getDescription();
		
		switch (behavior) {
		case CONTROL_FLOW:
			if (languageElements.contains(orgaConstant))
				newBehavior = AnalysisConstants.CONTROL_ORGA;
			else if (languageElements.contains(dataConstant))
				newBehavior = AnalysisConstants.CONTROL_DATA;
			else
				break;
			newBehavior = findBehavior(languageElements, newBehavior);
			break;
	
		case DATA_FLOW:
			if (languageElements.contains(controlConstant))
				newBehavior = AnalysisConstants.DATA_CONTROL;
			else if (languageElements.contains(orgaConstant))
				newBehavior = AnalysisConstants.DATA_ORGA;
			else
				break;
			newBehavior = findBehavior(languageElements, newBehavior);
			break;
			
		case ORGANISATION:
			if (languageElements.contains(controlConstant))
				newBehavior = AnalysisConstants.ORGA_CONTROL;
			else if (languageElements.contains(dataConstant))
				newBehavior = AnalysisConstants.ORGA_DATA;
			else
				break;
			newBehavior = findBehavior(languageElements, newBehavior);
			break;
			
		case CONTROL_ORGA:
			if (languageElements.contains(dataConstant))
				newBehavior = AnalysisConstants.CONTROL_ORGA_DATA;
			break;
			
		case CONTROL_DATA:
			if (languageElements.contains(orgaConstant))
				newBehavior = AnalysisConstants.CONTROL_DATA_ORGA;
			break;
			
		case DATA_CONTROL:
			if (languageElements.contains(orgaConstant))
				newBehavior = AnalysisConstants.DATA_CONTROL_ORGA;
			break;
			
		case DATA_ORGA:
			if (languageElements.contains(controlConstant))
				newBehavior = AnalysisConstants.DATA_ORGA_CONTROL;
			break;
			
		case ORGA_CONTROL:
			if (languageElements.contains(dataConstant))
				newBehavior = AnalysisConstants.ORGA_CONTROL_DATA;
			break;
			
		case ORGA_DATA:
			if (languageElements.contains(controlConstant))
				newBehavior = AnalysisConstants.ORGA_DATA_CONTROL;
			break;
			
		case NONE:
			if (languageElements.contains(controlConstant))
				newBehavior = AnalysisConstants.CONTROL_FLOW;
			else if (languageElements.contains(orgaConstant))
				newBehavior = AnalysisConstants.ORGANISATION;
			else if (languageElements.contains(dataConstant))
				newBehavior = AnalysisConstants.DATA_FLOW;
			else
				break;
			
			newBehavior = findBehavior(languageElements, newBehavior);
			break;
		
		default:
			break;
		}
		return newBehavior;
	}

	@Override
	protected String getResultCSVString() {
		StringBuilder builder = new StringBuilder();
		for (AnalysisConstants languageConstant : metrics)
			if (results.containsKey(languageConstant.getDescription()))
				builder.append(languageConstant.getDescription() + CSV_ITEMSEPARATOR);
		builder.append("\n");
		for (AnalysisConstants languageConstant : metrics)
			if (results.containsKey(languageConstant.getDescription()))
				builder.append(results.get(languageConstant.getDescription()) + CSV_ITEMSEPARATOR);
		return builder.toString();
	}

}
