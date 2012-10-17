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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.unitData;

import org.jbpt.petri.PetriNet;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataClassification;

/**
 * Interface for classes that can be used as {@link IUnit} input and output.
 * The id of the used {@link Representation} as well as the result value of the last {@link IUnit} of
 * the {@link IUnitChain} is stored. Furthermore, the parsed jBPT {@link ProcessModel} and a
 * {@link PetriNet} calculated from the {@link ProcessModel} are stored.
 * 
 * @author Tobias Hoppe
 */
public interface IUnitDataJbpt< V extends Object> extends IUnitData<V>{
		
	/**
	 * @return the parsed jBPT {@link ProcessModel}
	 */
	public ProcessModel getProcessModel();
	
	/**
	 * @param processModel the jBPT {@link ProcessModel} parsed from BPM AI {@link Diagram}.
	 */
	public void setProcessModel(ProcessModel processModel);
	
	/**
	 * @return the {@link PetriNet} generated from the {@link ProcessModel}
	 * of this {@link UnitDataClassification}.
	 */
	public PetriNet getPetriNet();
	
	/**
	 * Set the {@link PetriNet} generated from the {@link ProcessModel}
	 * of this {@link UnitDataClassification}.
	 * @param net
	 */
	public void setPetriNet(PetriNet net);
}
