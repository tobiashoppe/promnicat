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

import java.util.logging.Logger;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;

/**
 * This class checks the conformance according to the BPMN Conformance Levels
 * of a given {@link Bpmn} process model.
 * 
 * The expected input type is {@link IUnitDataClassification}<{@link Bpmn}>.
 * The output type is the same as the input type.
 * 
 * @author Tobias Hoppe
 *
 */
public class BpmnConformanceLevelCheckerUnit implements IUnit<IUnitData<Object>, IUnitData<Object>> {

	/**
	 * logger used by this class
	 */
	private Logger logger = Logger.getLogger(BpmnConformanceLevelCheckerUnit.class.getName());
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (input.getValue() == null){
			logger.warning("Got no model as input for BPMN conformance level check");
			return input;
		}
		if (input instanceof IUnitDataClassification<?>){
			Object model = input.getValue();
			if (model instanceof Bpmn<?, ?>) {
				@SuppressWarnings("unchecked")
				BpmnConformanceLevelChecker checker = new BpmnConformanceLevelChecker((Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) model);
				((IUnitDataClassification<?>) input).setDescriptiveModelingConformance(checker.isDescriptiveModelingConform());
				((IUnitDataClassification<?>) input).setAnalyticModelingConformance(checker.isAnalyticModelingConform());
				((IUnitDataClassification<?>) input).setCommonExecutableModelingConformance(checker.isCommonExecutableModelingConform());				
				return input;
			}
		}
		logger.warning("BPMN Conformance Level check has been skipped, due to wrong IUnit Data type!");
		return input;
	}

	@Override
	public String getName() {
		return "BpmnConformanceLevelCheckerUnit";
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
