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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;

/**
 * This class searches for a given label in the process elements.
 * 
 * The expected input type is {@link IUnitData}<{@link Map}<{@link String}, {@link Collection}<{@link String}> > >.
 * The output type is the same as the input type. If the given label to search for 
 * is not found, the second parameter of {@link IUnitData} is set to <code>null</code>. 
 * 
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class LabelFilterUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	private String searchCriterium = null;
	private Pattern searchPattern = null;
	
	/**
	 * Create a new {@link LabelFilterUnit} with the given label to search for
	 * @param labelToFilter label to search for in {@link ProcessModel}
	 */
	public LabelFilterUnit(String labelToFilter) {
		this.searchCriterium = labelToFilter.toLowerCase();
	}
	
	/**
	 * Create a new {@link LabelFilterUnit} with the given {@link Pattern} to search for
	 * @param patternToFilter {@link Pattern} to search for in {@link ProcessModel}'s labels
	 */
	public LabelFilterUnit(Pattern patternToFilter) {
		this.searchPattern = patternToFilter;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#execute(java.lang.Object)
	 */
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (!(input.getValue() instanceof Map<?, ?>)){
			throw new IllegalTypeException(Map.class, input.getValue().getClass(), "Got wrong input type in " + this.getName());
		}
		Map<String, Collection<String>> result = new HashMap<String, Collection<String>>();

		//iterate over input elements
		for (Entry<?, ?> entry : ((Map<?, ?>) input.getValue()).entrySet()) {
			if (!(entry.getKey() instanceof String)) {
				throw new IllegalTypeException(String.class, entry.getKey().getClass(), "Got wrong input type in " + this.getName());
			}
			Object labels = entry.getValue();
			if (!(labels instanceof Collection<?>)) {
				throw new IllegalTypeException(Collection.class, labels.getClass(), "Got wrong input type in " + this.getName());				
			}
			for (Object label : (Collection<?>)labels) {
				if (!(label instanceof String)) {
					throw new IllegalTypeException(String.class, label.getClass(), "Got wrong input type in " + this.getName());				
				}
				boolean found = false;
				//switch between pattern and concrete string
				if (this.searchCriterium == null){
					found = this.searchPattern.matcher((String)label).matches();
				} else {
					found = ((String) label).toLowerCase().contains(this.searchCriterium);
				}
				if (found) {
					Collection<String> foundLabels = new ArrayList<String>();
					if (result.containsKey(entry.getKey())) {
						foundLabels = result.get(entry.getKey());
					}
					foundLabels.add((String) label);
					result.put((String) entry.getKey(), foundLabels);
				}
			}
		}			
		if (! result.isEmpty()) {
			//label found return used process model
			input.setValue(result);
			if (input instanceof IUnitDataLabelFilter<?>) {
				((IUnitDataLabelFilter<Object>) input).setFilteredLabels(result);
			}
			return input;
		}					
		//if label could not be found return null as result
		input.setValue(null);
		return input;	
	}

	@Override
	public String getName(){
		return "LabelFilterUnit";
	}

	@Override
	public Class<?> getInputType() {
		return Map.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Map.class;
	}
}
