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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jbpt.hypergraph.abs.IGObject;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;

/**
 * This class extracts all labels from the given {@link ProcessModel}.
 * 
 * The expected input type is {@link IUnitData}<{@link ProcessModel}>.
 * The output type is {@link IUnitData}<{@link Map}<{@link String}, {@link Collection}<{@link String}> > >.
 * If no matching element was found, the second parameter of {@link IUnitData} is set to <code>null</code>.
 * 
 * @author Tobias Hoppe
 *
 */
public class ProcessModelLabelExtractorUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {
	
	private static final Logger logger = Logger.getLogger(ProcessModelLabelExtractorUnit.class.getName());

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#execute(java.lang.Object)
	 */
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}		
		Map<String, Collection<String>> result = new HashMap<String, Collection<String>>();
		if (input.getValue() != null) {
			if (!(input.getValue() instanceof ProcessModel)){
				throw new IllegalTypeException(ProcessModel.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
			}
			ProcessModel model = (ProcessModel) input.getValue();			
			//add process name
			String modelName = model.getName();
			if (null != modelName && !(modelName.isEmpty())) {
				ProcessModelLabelExtractorUnit.addToResult(model, result);
			}
			//add flow node labels
			for (FlowNode node : model.getFlowNodes()) {
				if (node.getName() != null && !(node.getName().isEmpty())) {
					ProcessModelLabelExtractorUnit.addToResult(node, result);
				}
			}
			//add non flow node labels
			for (NonFlowNode node : model.getNonFlowNodes()) {
				if (node.getName() != null && !(node.getName().isEmpty())) {
					ProcessModelLabelExtractorUnit.addToResult(node, result);
				}
			}
		} else {
			logger.warning("Got no model for label extraction!");
		}
				
		input.setValue(result);
		if (input instanceof IUnitDataLabelFilter<?>) {
			((IUnitDataLabelFilter<Object>) input).setLabels(result);
		}
		
		return input;
	}
	
	/**
	 * Adds a graph object (which is in this case the label) to the result list, which is
	 * ordered by entity type (thus, all Event labels, all Activity labels are collected).
	 * @param node to add to result
	 * @param result the label map
	 */
	public static void addToResult(IGObject node, Map<String, Collection<String>> result) {
		String key = node.getClass().toString();
		String value = node.getName();
		Collection<String> labels = new ArrayList<String>();
		if (result.containsKey(key)) {
			labels = result.get(key);
		}
		labels.add(value);
		result.put(key, labels);
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#getName()
	 */
	@Override
	public String getName(){
		return "ProcessModelLabelExtractorUnit";
	}

	@Override
	public Class<?> getInputType() {
		return ProcessModel.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Map.class;
	}
}