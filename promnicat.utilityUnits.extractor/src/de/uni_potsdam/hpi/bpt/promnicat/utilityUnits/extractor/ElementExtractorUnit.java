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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;

/**
 * This class filters out all process model elements of a given class.
 * 
 * The expected input type is {@link IUnitData}<{@link ProcessModel}>.
 * The output type is {@link IUnitData}<{@link Collection}<{@link IVertex}> >.
 * 
 * @author Tobias Hoppe
 *
 */
public class ElementExtractorUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	private Collection<Class<?>> searchCriteria = new ArrayList<Class<?>>();
	
	/**
	 * Create a new {@link ElementExtractorUnit} with the given class to filter.
	 * @param classToFilter the class that should be filtered out from the {@link ProcessModel}
	 */
	public ElementExtractorUnit(Class<?> classToFilter) {
		this.searchCriteria.add(classToFilter);
	}
	
	/**
	 * Create a new {@link ElementExtractorUnit} with the given class to filter.
	 * @param classToFilter the class that should be filtered out from the {@link ProcessModel}
	 */
	public ElementExtractorUnit(Collection<Class<?>> classToFilter) {
		this.searchCriteria.addAll(classToFilter);
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#execute(java.lang.Object)
	 */
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (!(input.getValue() instanceof ProcessModel)){
			throw new IllegalTypeException(ProcessModel.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
		}
		Collection<IVertex> result = new ArrayList<IVertex>();
		for (Class<?> searchCriterion : this.searchCriteria) {
			result.addAll(((ProcessModel) input.getValue()).filter(searchCriterion));	
		}
		if (result.isEmpty()) {
			result = null;
		}
		input.setValue(result);
		if (input instanceof IUnitDataLabelFilter<?>) {
			((IUnitDataLabelFilter<Object>) input).setModelElements(result);
		}
		return input;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#getName()
	 */
	@Override
	public String getName(){
		return "ElementExtractorUnit";
	}

	@Override
	public Class<?> getInputType() {
		return ProcessModel.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Collection.class;
	}

}
