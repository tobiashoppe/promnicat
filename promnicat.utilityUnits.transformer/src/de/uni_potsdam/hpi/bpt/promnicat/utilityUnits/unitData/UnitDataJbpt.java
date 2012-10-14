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
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * {@link IUnitChain} result type implementation containing the {@link Representation} id of the used 
 * process model and the result value of the last {@link IUnit} in the {@link IUnitChain} as well as
 * the parsed jBPT {@link ProcessModel}.
 * @author Tobias Hoppe
 */
public class UnitDataJbpt<V extends Object> extends UnitData<V> implements IUnitDataJbpt<V>{

	/**
	 * jBPT {@link ProcessModel} parsed from BPM AI {@link Diagram}
	 */
	private ProcessModel processModel = null;
	
	/**
	 * jBPT {@link PetriNet} converted from {@link ProcessModel}
	 */
	private PetriNet petriNet = null;
	
	/**
	 * @see UnitData
	 */
	public UnitDataJbpt() {
		super();
	}
	
	/**
	 * A result type with the given value as result and <code>null</code>
	 *  as the database id of the used process model
	 * @param value the result of the {@link IUnit}
	 */
	public UnitDataJbpt(V value) {
		super(value);
	}
	
	/**
	 * A result type with the given values.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 */
	public UnitDataJbpt(V value, String dbId) {
		super(value, dbId);
	}
	
	/**
	 * A result type with the given values.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 * @param model the jBPT {@link ProcessModel} parsed from {@link Representation}'s JSON String
	 */
	public UnitDataJbpt(V value, String dbId, ProcessModel model) {
		super(value, dbId);
		this.processModel = model;
	}
	
	@Override
	public ProcessModel getProcessModel() {
		return processModel;
	}

	@Override
	public void setProcessModel(ProcessModel processModel) {
		this.processModel = processModel;
	}

	@Override
	public PetriNet getPetriNet() {
		return this.petriNet;
	}

	@Override
	public void setPetriNet(PetriNet petriNet) {
		this.petriNet = petriNet;		
	}

	@Override
	public String toString(){
		return super.toString() +
				"\nprocess model: " + this.processModel +
				"\npetri net: " + this.petriNet;
	}
}
