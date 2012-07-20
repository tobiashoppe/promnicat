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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.behavior.LolaSoundnessCheckerResult;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * {@link IUnitChain} result type implementation containing the {@link Representation} id of the used 
 * jBPT {@link ProcessModel} and the result value of the last {@link IUnit} in the {@link IUnitChain}.
 * Furthermore the {@link PetriNet} parsed from a given {@link Bpmn} as well as some classification
 * results concerning BPMN Conformance Levels, Soundness, Model Type, and structuring are stored.
 * @author Tobias Hoppe
 * 
 */
public class UnitDataClassification<V extends Object> extends UnitDataJbpt<V> implements IUnitDataClassification<V> {
	
	private String modelPath = "";
	
	private PetriNet petriNet = null;
	private boolean isDescriptiveModelingConform = false;
	private boolean isAnalyticModelingConform = false;
	private boolean isCommonExecutableModelingConform = false;
	private LolaSoundnessCheckerResult soundnessResults = null;	
	private boolean isCyclic = false;
	private boolean isFreeChoice = false;
	private boolean isExtendedFreeChoice = false;
	private boolean isSNet = false;
	private boolean isTnet = false;
	private boolean isWorkflowNet = false;
	private boolean isStructured = false;
	private boolean canBeStructured = false;

	/**
	 * Creates an empty result with <code>null</code> elements.
	 */
	public UnitDataClassification() {
		super();
	}

	/**
	 * Creates a result type with the given value as result and <code>null</code>
	 * as the database id of the used process model.
	 *
	 * @param value the result of the last {@link IUnit}
	 */
	public UnitDataClassification(V value) {
		super(value);
	}

	/**
	 * A result type with the given values.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 */
	public UnitDataClassification(V value, String dbId) {
		super(value, dbId);
	}
	
	@Override
	public PetriNet getPetriNet() {
		return this.petriNet;
	}

	@Override
	public boolean getDescriptiveModelingConformance() {
		return this.isDescriptiveModelingConform;
	}

	@Override
	public boolean getAnalyticModelingConformance() {
		return this.isAnalyticModelingConform;
	}

	@Override
	public boolean getCommonExecutableModelingConformance() {
		return this.isCommonExecutableModelingConform;
	}

	@Override
	public void setPetriNet(PetriNet net) {
		this.petriNet = net;
	}

	@Override
	public void setDescriptiveModelingConformance(boolean isConform) {
		this.isDescriptiveModelingConform = isConform;
	}

	@Override
	public void setAnalyticModelingConformance(boolean isConform) {
		this.isAnalyticModelingConform = isConform;
	}

	@Override
	public void setCommonExecutableModelingConformance(boolean isConform) {
		this.isCommonExecutableModelingConform = isConform;
	}

	@Override
	public String getModelPath() {
		return modelPath;
	}

	@Override
	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	@Override
	public boolean isCyclic() {
		return isCyclic;
	}

	@Override
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
	}
	
	@Override
	public boolean isFreeChoice() {
		return isFreeChoice;
	}

	@Override
	public void setFreeChoice(boolean isFreeChoice) {
		this.isFreeChoice = isFreeChoice;
	}

	@Override
	public boolean isExtendedFreeChoice() {
		return isExtendedFreeChoice;
	}

	@Override
	public void setExtendedFreeChoice(boolean isExtendedFreeChoice) {
		this.isExtendedFreeChoice = isExtendedFreeChoice;
	}

	@Override
	public boolean isSNet() {
		return isSNet;
	}

	@Override
	public void setSNet(boolean isSNet) {
		this.isSNet = isSNet;
	}

	@Override
	public boolean isTnet() {
		return isTnet;
	}

	@Override
	public void setTnet(boolean isTnet) {
		this.isTnet = isTnet;
	}

	@Override
	public boolean isWorkflowNet() {
		return isWorkflowNet;
	}

	@Override
	public void setWorkflowNet(boolean isWorkflowNet) {
		this.isWorkflowNet = isWorkflowNet;
	}

	@Override
	public LolaSoundnessCheckerResult getSoundnessResults() {
		return soundnessResults;
	}

	 @Override
	public void setSoundnessResults(LolaSoundnessCheckerResult soundnessResults) {
		this.soundnessResults = soundnessResults;
	}

	@Override
	public boolean isStructured() {
		return isStructured;
	}

	@Override
	public void setStructured(boolean isStructured) {
		this.isStructured = isStructured;
	}

	@Override
	public boolean canBeStructured() {
		return canBeStructured;
	}

	@Override
	public void setAsStructurable(boolean canBeStructured) {
		this.canBeStructured = canBeStructured;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString() + "\n");
		builder.append("UnitDataClassification [isDecriptiveModelingConform=");
		builder.append(this.isDescriptiveModelingConform);
		builder.append(", isAnalyticModelingConform=");
		builder.append(this.isAnalyticModelingConform);
		builder.append(", isCommonExecutableModelingConform=");
		builder.append(this.isCommonExecutableModelingConform);
		if(this.soundnessResults != null) {
			builder.append(this.soundnessResults.toString());
		}
		builder.append(", isCyclic=" + this.isCyclic);
		builder.append(", isFreeChoice=" + this.isFreeChoice);
		builder.append(", isExtendedFreeChoice=" + this.isExtendedFreeChoice);
		builder.append(", isSNet=" + this.isSNet);
		builder.append(", isTNet=" + this.isTnet);
		builder.append(", isWorkFlowNet=" + this.isWorkflowNet);
		builder.append(", isStructured=" + this.isStructured);
		builder.append("]\n");
		return builder.toString();
	}

	@Override
	public String toCsv(String itemseparator, boolean printPetriNet) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.modelPath.replaceAll(itemseparator, "#") + itemseparator);
		builder.append(this.getDbId() + itemseparator);
		//add BPMN Conformance Level check results
		builder.append(addCsvContentFor(this.isDescriptiveModelingConform) + itemseparator);
		builder.append(addCsvContentFor(this.isAnalyticModelingConform) + itemseparator);
		builder.append(addCsvContentFor(this.isCommonExecutableModelingConform) + itemseparator);
		if(this.soundnessResults != null) {			
			//add soundness check results
			builder.append(addCsvContentFor(this.soundnessResults.isBounded()) + itemseparator);
			builder.append(addCsvContentFor(this.soundnessResults.hasLiveness()) + itemseparator);
			builder.append(addCsvContentFor(this.soundnessResults.hasQuasiLiveness()) + itemseparator);
			builder.append(addCsvContentFor(this.soundnessResults.isRelaxedSound()) + itemseparator);
			builder.append(addCsvContentFor(this.soundnessResults.isWeakSound()) + itemseparator);
			builder.append(addCsvContentFor(this.soundnessResults.isClassicalSound()) + itemseparator);
			builder.append(addCsvContentFor(this.soundnessResults.hasTransitioncover()) + itemseparator);
			builder.append(this.soundnessResults.getDeadTransitions().toString().replaceAll("\n", " ") + itemseparator);
			builder.append(this.soundnessResults.getUncoveredTransitions().toString().replaceAll("\n", " ") + itemseparator);
			builder.append(this.soundnessResults.getUnboundedPlaces().toString().replaceAll("\n", " ") + itemseparator);
			//add structural check results
			builder.append(addCsvContentFor(this.isCyclic) + itemseparator);
			builder.append(addCsvContentFor(this.isFreeChoice) + itemseparator);
			builder.append(addCsvContentFor(this.isExtendedFreeChoice) + itemseparator);
			builder.append(addCsvContentFor(this.isSNet) + itemseparator);
			builder.append(addCsvContentFor(this.isTnet) + itemseparator);
			builder.append(addCsvContentFor(this.isWorkflowNet) + itemseparator);
		} else {
			//if soundness result is not available, leave output empty
			for(int i = 0; i < 16; i++){
				builder.append(itemseparator);
			}
		}
		builder.append(addCsvContentFor(this.isStructured) + itemseparator);
		//add Petri net if wanted
		if (this.petriNet != null && printPetriNet) {
			builder.append(this.petriNet.toDOT());		
		}
		builder.append("\n");
		return builder.toString();
	}

	/**
	 * @param value to check
	 * @return "1" if value is <code>true</code>, otherwise "0".
	 */
	private String addCsvContentFor(boolean value) {
		if(value) {
			return "1";
		} else {
			return "0";
		}
	}

}
