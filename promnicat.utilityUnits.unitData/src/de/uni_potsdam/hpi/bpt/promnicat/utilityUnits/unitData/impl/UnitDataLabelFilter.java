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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;

/**
 * {@link IUnitChain} result type implementation containing the {@link Representation} id of the used 
 * process model and the result value of the last {@link IUnit} in the {@link IUnitChain}.
 * Furthermore, the parsed jBPT {@link ProcessModel}, the filtered model elements
 * including their labels as well as a list of filtered labels is stored.
 * @author Tobias Hoppe
 */
public class UnitDataLabelFilter<V extends Object> extends UnitDataJbpt<V> implements IUnitDataLabelFilter<V> {
	
	/**
	 * model element extractor unit result
	 */
	private Collection<? extends IVertex> modelElements = null;
	
	/**
	 * label extractor unit result
	 */
	private Map<String, Collection<String>> labels = null;
	
	/**
	 * label filter unit result
	 */
	private Map<String, Collection<String>> filteredLabels = null;
	
	/**
	 * An empty result with <code>null</code> elements.
	 */
	public UnitDataLabelFilter() {
		super();
	}
	
	/**
	 * A result type with the given value as result and <code>null</code>
	 *  of all other values including the database id.
	 * @param value the result of the {@link IUnit}
	 */
	public UnitDataLabelFilter(V value) {
		super(value);
	}
	
	/**
	 * A result type with the given value and dbId. All other values are set to <code>null</code>.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 */
	public UnitDataLabelFilter(V value, UUID dbId) {
		super(value, dbId);
	}
	
	@Override
	public String toString(){
		return super.toString() +
				"\nmodel elements: " + this.modelElements +
				"\nlabels: " + this.labels +
				"\nfiltered labels " + this.filteredLabels;
	}

	@Override
	public Collection<? extends IVertex> getModelElements() {
		return modelElements;
	}

	@Override
	public void setModelElements(Collection<? extends IVertex> modelElements) {
		this.modelElements = modelElements;
	}

	@Override
	public Map<String, Collection<String>> getLabels() {
		return labels;
	}

	@Override
	public void setLabels(Map<String, Collection<String>> labels) {
		this.labels = labels;
	}

	@Override
	public Map<String, Collection<String>> getFilteredLabels() {
		return filteredLabels;
	}

	@Override
	public void setFilteredLabels(Map<String, Collection<String>> filteredLabels) {
		this.filteredLabels = filteredLabels;
	}
}
