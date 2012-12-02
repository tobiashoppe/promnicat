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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model;

import java.util.SortedMap;
import java.util.TreeMap;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.AnalysisConstants;

/**
 * The representation of a process model for further process evolution analysis.
 * A model consists of all its revisions and several attributes that can be set throughout
 * the analysis process. This model only serves for process evolution analysis purposes.
 * 
 * @author Tobias Metzke
 *
 */
public class ProcessEvolutionModel extends ProcessModel {

	private static final String MODELS_NOT_EQUAL_EXCEPTION_MESSAGE = "Models do not have the same name and are therefore not equal, can not be merged.\n";
	private SortedMap<Integer, ProcessEvolutionModelRevision> revisions = new TreeMap<Integer, ProcessEvolutionModelRevision>();
	private String name;
	private boolean growing = true;
	private int numberOfCMRIterations = 0;
	private int numberOfAlteringRevisions = 0;
	private int numberOfAdditions = 0;
	private int numberOfDeletions = 0;
	private int numberOfMovedOrResizedElements = 0;
	private AnalysisConstants languageUsage = AnalysisConstants.NONE;

	/**
	 * default constructor
	 * @param name of the model
	 */
	public ProcessEvolutionModel(String name) {
		setName(name);
	}
	
	/**
	 * merge constructor that combines two models into one
	 * @param model1
	 * @param model2
	 */
	public ProcessEvolutionModel(ProcessEvolutionModel model1, ProcessEvolutionModel model2) {
		if (model1.getName() != model2.getName())
			throw new RuntimeException(MODELS_NOT_EQUAL_EXCEPTION_MESSAGE + "Model 1: " + model1.getName() + ", " + "Model 2: " + model2.getName());
		else {
			setName(model1.getName());
			setGrowing(model1.isGrowing() && model2.isGrowing());
			setNumberOfAdditions(Math.max(model1.getNumberOfAdditions(),model2.getNumberOfAdditions()));
			setNumberOfAlteringRevisions(Math.max(model1.getNumberOfAlteringRevisions(), model2.getNumberOfAlteringRevisions()));
			setCMRIterations(Math.max(model1.getCMRIterations(), model2.getCMRIterations()));
			setNumberOfDeletions(Math.max(model1.getNumberOfDeletions(), model2.getNumberOfDeletions()));
			setNumberOfMovedOrResizedElements(Math.max(model1.getNumberOfMovedOrResizedElements(), model2.getNumberOfMovedOrResizedElements()));
			mergeRevisions(model1.getRevisions(), model2.getRevisions());
		}
		
	}
	
	/**
	 * @return the name of the model
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * add a revision to the model
	 * @param revision the {@link ProcessEvolutionModelRevision} to add
	 */
	public void add(ProcessEvolutionModelRevision revision) {
		revisions.put(revision.getRevisionNumber(), revision);
	}

	/**
	 * @return the revisions of this model
	 */
	public SortedMap<Integer, ProcessEvolutionModelRevision> getRevisions() {
		return revisions;
	}

	/**
	 * @return <code>true</code> if the model is never shrinking
	 */
	public boolean isGrowing() {
		return growing;
	}

	/**
	 * @param growing the growth behavior of the model
	 */
	public void setGrowing(boolean growing) {
		this.growing = growing;
	}

	/**
	 * @return the number of iterations through the CMR phases
	 */
	public int getCMRIterations() {
		return numberOfCMRIterations;
	}
	
	/**
	 * @param iterations number of iterations through the CMR phases
	 */
	public void setCMRIterations(int iterations) {
		this.numberOfCMRIterations = iterations;
	}

	/**
	 * @return the number of revisions that change at least one metric
	 * compared to their previous revision 
	 */
	public int getNumberOfAlteringRevisions() {
		return numberOfAlteringRevisions;
	}

	/**
	 * @param numberOfRevisions the number of revisions that change at least one metric
	 * compared to their previous revision
	 */
	public void setNumberOfAlteringRevisions(int numberOfRevisions) {
		this.numberOfAlteringRevisions  = numberOfRevisions;
	}

	/**
	 * @return the number of revisions that do not change any metric
	 * compared to their previous revision
	 */
	public int getNumberOfLazyRevisions() {
		return revisions.size() - getNumberOfAlteringRevisions();
	}

	/**
	 * @return the number of additions of model elements that have
	 * occurred throughout the whole history of the model
	 */
	public int getNumberOfAdditions() {
		return numberOfAdditions;
	}

	/**
	 * @param numberOfAdditions the number of additions of model elements that have
	 * occurred throughout the whole history of the model
	 */
	public void setNumberOfAdditions(int numberOfAdditions) {
		this.numberOfAdditions = numberOfAdditions;
	}

	/**
	 * @return the number of deletions of model elements that have
	 * occurred throughout the whole history of the model
	 */
	public int getNumberOfDeletions() {
		return numberOfDeletions;
	}

	/**
	 * @param numberOfDeletions the number of deletions of model elements that have
	 * occurred throughout the whole history of the model
	 */
	public void setNumberOfDeletions(int numberOfDeletions) {
		this.numberOfDeletions = numberOfDeletions;
	}

	/**
	 * @return the number of model elements that have
	 * been moved or resized throughout the whole history of the model
	 */
	public int getNumberOfMovedOrResizedElements() {
		return numberOfMovedOrResizedElements;
	}

	/**
	 * @param numberOfMovedOrResizedElements the number of model elements that have
	 * been moved or resized throughout the whole history of the model
	 */
	public void setNumberOfMovedOrResizedElements(int numberOfMovedOrResizedElements) {
		this.numberOfMovedOrResizedElements = numberOfMovedOrResizedElements;
	}

	/**
	 * If two models are merged, their revisions have to be merged as well.
	 * This is done by looking for revisions that occur in both collections
	 * of revisions and merging them, otherwise the respective revisions are simply
	 * added to the model
	 * @param revisions1 the first collection of revisions to merge
	 * @param revisions2 the second collection of revisions to merge
	 */
	private void mergeRevisions(
			SortedMap<Integer, ProcessEvolutionModelRevision> revisions1, SortedMap<Integer, ProcessEvolutionModelRevision> revisions2) {
		for (ProcessEvolutionModelRevision revision1 : revisions1.values()) {
			ProcessEvolutionModelRevision newRevision;
			ProcessEvolutionModelRevision revision2 = revisions2.get(revision1.getRevisionNumber());
			if (revision2 != null)
				newRevision = new ProcessEvolutionModelRevision(revision1, revision2);
			else
				newRevision = revision1;
			add(newRevision);
		}
		for (ProcessEvolutionModelRevision revision2 : revisions2.values())
			if (!getRevisions().containsKey(revision2.getRevisionNumber()))
				add(revision2);
	}

	public AnalysisConstants getLanguageUsage() {
		return languageUsage;
	}
	
	public void setLanguageUsage(AnalysisConstants behavior) {
		languageUsage  = behavior;
	}

	
}
