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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter;

import java.util.Collection;
import java.util.HashSet;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * This class searches for a given element instance or element class in the process elements.
 * If the single model elements are needed, use the {@link ElementExtractorUnit}.
 * 
 * The expected input type is {@link IUnitData}<{@link ProcessModel}>.
 * The output type is the same as the input type. If no matching element was found,
 * the second parameter of {@link IUnitData} is set to <code>null</code>. 
 * 
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class ProcessModelFilterUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	private Vertex modelElement = null;
	private Collection<Class<?>> includedClasses = new HashSet<Class<?>>();
	private Collection<Class<?>> excludedClasses = new HashSet<Class<?>>();
	
	/**
	 * Creates a new {@link ProcessModelFilterUnit} to search for a concrete element instance.
	 * @param modelElement the instance to search for
	 */
	public ProcessModelFilterUnit(Vertex modelElement) {
		this.modelElement = modelElement;
	}
	
	/**
	 * Creates a new {@link ProcessModelFilterUnit} to search for a element class.
	 * @param elementClass the class to search for
	 */
	public ProcessModelFilterUnit(Class<?> elementClass) {
		this.includedClasses.add(elementClass);
	}
	
	/**
	 * Creates a new {@link ProcessModelFilterUnit} to search for a element class.
	 * @param includedClasses a set of classes, that must be contained in the {@link ProcessModel}
	 * @param excludedClasses a set of classes, that are not allowed in the {@link ProcessModel}
	 */
	public ProcessModelFilterUnit(Collection<Class<?>> includedClasses, Collection<Class<?>> excludedClasses){
		if (includedClasses != null) {
			this.includedClasses.addAll(includedClasses);
		}
		if (excludedClasses != null) {
			this.excludedClasses.addAll(excludedClasses);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#execute(java.lang.Object)
	 */
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (input.getValue() == null) {
			return input;
		}
		if (!(input.getValue() instanceof ProcessModel)){
			throw new IllegalTypeException(ProcessModel.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
		}		
		boolean foundExcludedClass = false;
		//check whether to look for a concrete element or a class
		if (this.modelElement != null){
			foundExcludedClass = !(((ProcessModel) input.getValue()).contains(this.modelElement));
		} else {
			if (!this.excludedClasses.isEmpty()){
				ProcessModel model = (ProcessModel) input.getValue();
				// if one of the excluded types is contained, set found excluded class and break loop
				for (Class<?> clazz : this.excludedClasses) {
					boolean containsType = model.containsType(clazz);
					if (containsType) {
						foundExcludedClass = true;
						break;
					}
				}
			}
			//if none of the excluded classes is contained, check for included ones
			if (!foundExcludedClass){
				// set found excluded class to true if none of the included ones is contained in the process model 
				foundExcludedClass = !(((ProcessModel) input.getValue()).containsAllTypes(this.includedClasses));
			}
		}
		if (foundExcludedClass){
			input.setValue(null);
			return input;
		} else {
			return input;
		}
	}

	@Override
	public String getName(){
		return "ProcessModelFilterUnit";
	}

	@Override
	public Class<?> getInputType() {
		return ProcessModel.class;
	}

	@Override
	public Class<?> getOutputType() {
		return ProcessModel.class;
	}
}
