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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.Resource;
import org.jbpt.pm.bpmn.BpmnMessageFlow;
import org.jbpt.pm.bpmn.Document;
import org.jbpt.pm.bpmn.Subprocess;

import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.AnalysisConstants;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModel;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModelRevision;

/**
 * This analysis looks for additions and deletions from revision to revision
 * and also adds the total number of additions and deletions to every model.
 * 
 * @author Tobias Metzke
 *
 */
public class AdditionsDeletionsAnalysis extends AbstractMetricsAnalysis {

	private boolean includeSubprocesses;
	private Collection<AnalysisConstants> metrics;
	
	public AdditionsDeletionsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze, Collection<AnalysisConstants> metrics, boolean includeSubprocesses) {
		this(modelsToAnalyze, null, metrics, includeSubprocesses);
	}
	
	public AdditionsDeletionsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze, Map<String, ProcessEvolutionModel> analyzedModels, Collection<AnalysisConstants> metrics, boolean includeSubprocesses) {
		super(modelsToAnalyze,analyzedModels);
		this.metrics = metrics;
		this.includeSubprocesses = includeSubprocesses;
	}

	@Override
	protected void performAnalysis() {
		for (ProcessEvolutionModel model : modelsToAnalyze.values()) {
			ProcessEvolutionModel newModel = new ProcessEvolutionModel(model.getName());
			Map<String, List<String>> oldElements = new HashMap<String, List<String>>();
			int modelAdditions = 0, modelDeletions = 0;
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
				int revisionAdditions = 0, revisionDeletions = 0;
				ProcessEvolutionModelRevision newRevision = new ProcessEvolutionModelRevision(revision.getRevisionNumber());
				// check adds and deletes for every class like Activities, Gateways, Edges etc.
				for (AnalysisConstants classToAnalyze : metrics) {
					Map<AnalysisConstants, Integer> addsAndDeletes = analyzeAddsAndDeletesFor(classToAnalyze, oldElements, revision, includeSubprocesses);
					int newAdditions = addsAndDeletes.get(AnalysisConstants.ADDITIONS);
					int newDeletions = addsAndDeletes.get(AnalysisConstants.DELETIONS);
					revisionAdditions += newAdditions;
					revisionDeletions += newDeletions;
					modelAdditions += newAdditions;
					modelDeletions += newDeletions;
					newRevision.add(classToAnalyze.getDescription() + AnalysisConstants.ADDITIONS.getDescription(), newAdditions);
					newRevision.add(classToAnalyze.getDescription() + AnalysisConstants.DELETIONS.getDescription(), newDeletions);
				}
				if (revisionAdditions < revisionDeletions)
					newModel.setGrowing(false);
				newModel.add(newRevision);
			}
			newModel.setNumberOfAdditions(modelAdditions);
			newModel.setNumberOfDeletions(modelDeletions);
			analyzedModels.put(model.getName(), newModel);
		}
	}

	/**
	 * find number of additions and deletions for a revision
	 * by comparing the IDs of the elements from the previous revision with
	 * the new IDs.
	 * 
	 * @param classToAnalyze the element class to analyze (events, activities, etc.)
	 * @param oldElements the elements to compare the new elements to
	 * @param revision the actual revision to get the new elements from
	 * @param includeSubprocesses flag to decide whether to include subprocesses in the analysis
	 * @return the number of additions and deletions for the given element class
	 */
	private static Map<AnalysisConstants, Integer> analyzeAddsAndDeletesFor(
			AnalysisConstants classToAnalyze, Map<String, List<String>> oldElements, 
			ProcessEvolutionModelRevision revision, boolean includeSubprocesses) {
		List<String> newIDs = new ArrayList<String>();
		ProcessModel actualModel = revision.getProcessModel();
		switch (classToAnalyze) {
		case EVENTS:
			newIDs = getIDsFor(Event.class, actualModel, includeSubprocesses);
			break;
		
		case ACTIVITIES:
			newIDs = getIDsFor(Activity.class, actualModel, includeSubprocesses);
			break;
		
		case EDGES:
			newIDs = getEdgesIDs(actualModel, includeSubprocesses);
			break;
		
		case ROLES:
			newIDs = getIDsFor(Resource.class, actualModel, includeSubprocesses);
			break;
			
		case GATEWAYS:
			newIDs = getIDsFor(Gateway.class, actualModel, includeSubprocesses);
			break;

		case DOCUMENTS:
			newIDs = getIDsFor(Document.class, actualModel, includeSubprocesses);
			break;
			
		default:
			break;
		}
		List<String> oldIDs = oldElements.get(classToAnalyze.getDescription());
		oldIDs = oldIDs == null ? new ArrayList<String>() : oldIDs;
		int oldSize = oldIDs.size();
		oldIDs.removeAll(newIDs);
		int oldNewSize = newIDs.size();
		newIDs.removeAll(oldIDs);
		Map<AnalysisConstants, Integer> results = new HashMap<AnalysisConstants, Integer>();
		oldElements.put(classToAnalyze.getDescription(), newIDs);
		results.put(AnalysisConstants.ADDITIONS, oldNewSize - newIDs.size());
		results.put(AnalysisConstants.DELETIONS, oldSize - oldIDs.size());
		return results;
	}

	/**
	 * Collect the IDs of the given element class
	 * 
	 * @param classToAnalyze the element class to analyze (events, activities, etc.)
	 * @param actualModel the model representation to analyze
	 * @param includeSubprocesses flag to decide whether to include subprocesses in the analysis
	 * @return the IDs of all elements of the given class in the given revision representation
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getIDsFor(Class<?> classToAnalyze, ProcessModel actualModel, boolean includeSubprocesses) {
		Collection<? extends Vertex> elements = (Collection<? extends Vertex>)actualModel.filter(classToAnalyze);
		List<String> ids = new ArrayList<String>();
		for (Vertex element : elements)
			ids.add(element.getId());
		if (includeSubprocesses)
			for (FlowNode node : actualModel.getVertices())
				if (node instanceof Subprocess) 
					ids.addAll(getIDsFor(classToAnalyze, ((Subprocess)node).getSubProcess(), includeSubprocesses));
		return ids;
	}

	/**
	 * Collect the IDs of the Edges. Since they are not of type {@link Vertex}
	 * they need a separate method.
	 * 
	 * @param actualModel the model representation to analyze
	 * @param includeSubprocesses flag to decide whether to include subprocesses in the analysis
	 * @return the IDs of all edges in the given revision representation
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getEdgesIDs(ProcessModel actualModel,
			boolean includeSubprocesses) {
		Collection<ControlFlow<FlowNode>> flows = actualModel.getEdges();
		List<String> ids = new ArrayList<String>();
		for (Object flow : flows) {
			if (flow instanceof ControlFlow<?>)
				ids.add(((ControlFlow<FlowNode>)flow).getId());
			else
				ids.add(((BpmnMessageFlow)flow).getId());
		}
		if (includeSubprocesses)
			for (FlowNode node : actualModel.getVertices())
				if (node instanceof Subprocess) 
					ids.addAll(getEdgesIDs(((Subprocess)node).getSubProcess(), includeSubprocesses));
		
		return ids;
	}
	
	@Override
	protected String addCSVHeader() {
		StringBuilder builder = new StringBuilder()
			.append("Process Model" + CSV_ITEMSEPARATOR)
			.append("Revision" + CSV_ITEMSEPARATOR);
		for (AnalysisConstants metric : metrics) {
			String metricAdditionName = metric.getDescription() + AnalysisConstants.ADDITIONS.getDescription();
			String metricDeletionName = metric.getDescription() + AnalysisConstants.DELETIONS.getDescription();
			builder
				.append(metricAdditionName + CSV_ITEMSEPARATOR)
				.append(metricDeletionName + CSV_ITEMSEPARATOR);
		}
		builder.append("grows continuously?");
		return builder.toString();
	}
	
	@Override
	protected String toCsvString(ProcessEvolutionModel model) {
		// collect all information from the revisions
		// display each revision in a separate line
		StringBuilder builder = new StringBuilder();
		for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
			builder
				.append("\n")
				.append(model.getName())
				.append(CSV_ITEMSEPARATOR + revision.getRevisionNumber());
			for (AnalysisConstants metric : metrics) {
				String metricAdditionName = metric.getDescription() + AnalysisConstants.ADDITIONS.getDescription();
				String metricDeletionName = metric.getDescription() + AnalysisConstants.DELETIONS.getDescription();
				builder
					.append(CSV_ITEMSEPARATOR + revision.get(metricAdditionName).intValue())
					.append(CSV_ITEMSEPARATOR + revision.get(metricDeletionName).intValue());
			}
					
		}
		builder.append(CSV_ITEMSEPARATOR + model.isGrowing());
		return builder.toString();
	}
}
