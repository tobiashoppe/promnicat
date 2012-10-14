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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jbpt.hypergraph.abs.IGObject;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;

/**
 * This class extracts all labels from the given {@link Collection} of {@link ProcessModel} elements.
 * 
 * The expected input type is {@link IUnitData}<{@link Collection}<? extends {@link IVertex}>>.
 * The output type is {@link IUnitData}<{@link Map}<{@link String}, {@link Collection}<{@link String}> > >.
 * If no matching element was found, the value of {@link IUnitData} is set to <code>null</code>.
 * 
 * @author Tobias Hoppe
 *
 */
public class ElementLabelExtractorUnit implements IUnit<IUnitData<Object>, IUnitData<Object> >{

	private final Logger logger = Logger.getLogger(ElementLabelExtractorUnit.class.getName());
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}		
		Map<String, Collection<String>> result = new HashMap<String, Collection<String>>();
		if (input.getValue() != null) {
			if (!(input.getValue() instanceof Collection<?>)){
				throw new IllegalTypeException(ProcessModel.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
			}			
			
			for (Object node : (Collection<?>)input.getValue()) {
				if (node instanceof IVertex){
					if (((IVertex) node).getName() != null && !(((IVertex) node).getName().isEmpty())) {
						ProcessModelLabelExtractorUnit.addToResult((IGObject) node, result);
					}					
				} else {
					logger.warning("Got element '" + node.toString() + "' which can not be cast to IVertex for label extraction!");
				}
			}
		} else {
			logger.warning("Got an empty collection for label extraction!");
		}
				
		input.setValue(result);
		if (input instanceof IUnitDataLabelFilter<?>) {
			((IUnitDataLabelFilter<Object>) input).setLabels(result);
		}
		
		return input;
	}

	@Override
	public String getName() {
		return "ElementLabelExtractorUnit";
	}

	@Override
	public Class<?> getInputType() {
		return Collection.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Map.class;
	}

}
