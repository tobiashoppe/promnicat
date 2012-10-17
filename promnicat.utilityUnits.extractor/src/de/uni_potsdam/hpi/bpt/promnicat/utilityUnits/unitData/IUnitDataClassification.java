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

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.unitData.IUnitDataJbpt;

/**
 * Interface for classes that can be used as {@link IUnit} input and output.
 * Apart from the upper class attributes BPMN conformance levels are stored.
 * Furthermore several {@link PetriNet} attributes  - like soundness, is free choice,
 * is S/T-Net, is workflow net - are stored.
 * 
 * @author Tobias Hoppe
 * 
 */
public interface IUnitDataClassification<V extends Object> extends IUnitDataJbpt<V> {

	/**
	 * @return <code>true</code> if the {@link ProcessModel} is
	 * conform to the descriptive modeling specification as defined
	 * in the BPMN version 2.0 specification.
	 * <code>false</code> otherwise.
	 */
	public boolean getDescriptiveModelingConformance();
	
	/**
	 * @return <code>true</code> if the {@link ProcessModel} is
	 * conform to the analytic modeling specification as defined
	 * in the BPMN version 2.0 specification.
	 * <code>false</code> otherwise.
	 */
	public boolean getAnalyticModelingConformance();
	
	/**
	 * @return <code>true</code> if the {@link ProcessModel} is
	 * conform to the common executable modeling specification as defined
	 * in the BPMN version 2.0 specification.
	 * <code>false</code> otherwise.
	 */
	public boolean getCommonExecutableModelingConformance();
	
	/**
	 * Set to <code>true</code>, if the analyzed {@link ProcessModel} is
	 * conform to the descriptive modeling specification as defined
	 * in the BPMN version 2.0 specification.
	 * @param isConform
	 */
	public void setDescriptiveModelingConformance(boolean isConform);
	
	/**
	 * Set to <code>true</code>, if the analyzed {@link ProcessModel} is
	 * conform to the analytic modeling specification as defined
	 * in the BPMN version 2.0 specification.
	 * @param isConform
	 */
	public void setAnalyticModelingConformance(boolean isConform);
	
	/**
	 * Set to <code>true</code>, if the analyzed {@link ProcessModel} is
	 * conform to the common executable modeling specification as defined
	 * in the BPMN version 2.0 specification.
	 * @param isConform
	 */
	public void setCommonExecutableModelingConformance(boolean isConform);

	/**
	 * @param itemseparator char(s) to use for separation of result items
	 * @param printPetriNet set to <code>true</code> if dot representation of {@link PetriNet}
	 * should be stored, otherwise <code>false</code>;
	 * @return a CSV-Representation of this {@link UnitDataClassification}.
	 */
	public String toCsv(String itemseparator, boolean printPetriNet);
	
	/**
	 * @return the modelPath
	 */
	public String getModelPath();

	/**
	 * @param modelPath the modelPath to set
	 */
	public void setModelPath(String modelPath);
	
	/**
	 * @return the soundness results
	 */
	public LolaSoundnessCheckerResult getSoundnessResults();

	/**
	 * @param soundnessResults the soundness results to set
	 */
	public void setSoundnessResults(LolaSoundnessCheckerResult soundnessResults);

	/**
	 * @return the isCyclic
	 */
	public boolean isCyclic();

	/**
	 * Set whether the {@link PetriNet} is cyclic or not
	 * @param isCyclic the isCyclic to set
	 */
	public void setCyclic(boolean isCyclic);
	
	/**
	 * @return the isFreeChoice
	 */
	public boolean isFreeChoice();

	/**
	 * Set whether the {@link PetriNet} is a free choice net or not
	 * @param isFreeChoice the isFreeChoice to set
	 */
	public void setFreeChoice(boolean isFreeChoice);

	/**
	 * @return the isExtendedFreeChoice
	 */
	public boolean isExtendedFreeChoice();

	/**
	 * Set whether the {@link PetriNet} is a extended free choice net or not
	 * @param isExtendedFreeChoice the isExtendedFreeChoice to set
	 */
	public void setExtendedFreeChoice(boolean isExtendedFreeChoice);

	/**
	 * @return the isSNet
	 */
	public boolean isSNet();

	/**
	 * Set whether the {@link PetriNet} is a S-net or not
	 * @param isSNet the isSNet to set
	 */
	public void setSNet(boolean isSNet);

	/**
	 * @return the isTnet
	 */
	public boolean isTnet();

	/**
	 * Set whether the {@link PetriNet} is a T-net or not
	 * @param isTnet the isTnet to set
	 */
	public void setTnet(boolean isTnet);

	/**
	 * @return the isWorkflowNet
	 */
	public boolean isWorkflowNet();

	/**
	 * Set whether the {@link PetriNet} is a workflow net or not
	 * @param isWorkflowNet the isWorkflowNet parameter to set
	 */
	public void setWorkflowNet(boolean isWorkflowNet);
	
	/**
	 * @return the isStructured
	 */
	public boolean isStructured();

	/**
	 * @param isStructured the isStructured to set
	 */
	public void setStructured(boolean isStructured);

	/**
	 * @return the canBeStructured
	 */
	public boolean canBeStructured();

	/**
	 * @param canBeStructured the canBeStructured to set
	 */
	public void setAsStructurable(boolean canBeStructured);
}
