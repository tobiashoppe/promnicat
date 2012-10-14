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
import java.util.List;
import java.util.Map;

import org.jbpt.hypergraph.abs.Vertex;

import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.AnalysisConstants;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.api.IAnalysis;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModel;
import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModelRevision;

/**
 * This analysis looks for elements that have been moved in position
 * or resized from one revision to another and counts them.
 * 
 * @author Tobias Metzke
 *
 */
public class MovedElementsAnalysis extends AbstractMetricsAnalysis implements
		IAnalysis {
	
	private static final String NEW_LAYOUT = AnalysisConstants.NEW_LAYOUT.getDescription();

	public MovedElementsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze,
			Map<String, ProcessEvolutionModel> analyzedModels) {
		super(modelsToAnalyze, analyzedModels);
	}

	public MovedElementsAnalysis(
			Map<String, ProcessEvolutionModel> modelsToAnalyze) {
		super(modelsToAnalyze);
	}

	private List<Vertex> oldNodes = new ArrayList<Vertex>();
	
	@Override
	protected void performAnalysis() {
		for (ProcessEvolutionModel model : modelsToAnalyze.values()) {
			int layoutChanges = 0;
			oldNodes.clear();
			ProcessEvolutionModel newModel = new ProcessEvolutionModel(model.getName());
			for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
				ProcessEvolutionModelRevision newRevision = new ProcessEvolutionModelRevision(revision.getRevisionNumber());
				int alteredNodes = 0;
				// look for changes to previous revision in x,y,width,height for every node
				Collection<Vertex> actualNodes = new ArrayList<Vertex>();
				actualNodes.addAll(revision.getProcessModel().getFlowNodes());
				actualNodes.addAll(revision.getProcessModel().getNonFlowNodes());
				// FIXME does not work for edges so far since they do not have these attributes
				for (Vertex node : actualNodes)
					if (oldNodes.contains(node)) {
						Vertex oldNode = oldNodes.get(oldNodes.indexOf(node));
						if (node.getX() != oldNode.getX()
							|| node.getY() != oldNode.getY()
							|| node.getHeight() != oldNode.getHeight()
							|| node.getWidth() != oldNode.getWidth())
							alteredNodes++;
					}
				newRevision.add(NEW_LAYOUT, new Double(alteredNodes));
				newModel.add(newRevision);
				layoutChanges += alteredNodes;
				// add actual nodes as back reference for next revision
				oldNodes.clear();
				oldNodes.addAll(revision.getProcessModel().getFlowNodes());
				oldNodes.addAll(revision.getProcessModel().getNonFlowNodes());
				// FIXME edges can not be used so far
			}
			newModel.setNumberOfMovedOrResizedElements(layoutChanges);
			analyzedModels.put(model.getName(), newModel);
		}
	}

	@Override
	protected String addCSVHeader() {
		return new StringBuilder()
			.append("Process Model" + CSV_ITEMSEPARATOR)
			.append("Revision" + CSV_ITEMSEPARATOR)
			.append(NEW_LAYOUT)
			.toString();
	}

	@Override
	protected String toCsvString(ProcessEvolutionModel model) {
		StringBuilder builder = new StringBuilder();
		for (ProcessEvolutionModelRevision revision : model.getRevisions().values()) {
			builder
				.append("\n")
				.append(model.getName())
				.append(CSV_ITEMSEPARATOR + revision.getRevisionNumber())
				.append(CSV_ITEMSEPARATOR + revision.get(NEW_LAYOUT).intValue());
		}
		return builder.toString();
	}
}
