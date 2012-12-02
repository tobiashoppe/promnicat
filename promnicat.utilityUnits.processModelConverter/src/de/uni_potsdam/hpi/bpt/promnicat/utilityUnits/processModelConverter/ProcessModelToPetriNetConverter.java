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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelConverter;

import java.util.logging.Logger;

import org.jbpt.petri.PetriNet;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.epc.Epc;
import org.jbpt.throwable.TransformationException;

/**
 * This class transforms a jBPT {@link ProcessModel} to the corresponding {@link PetriNet}.
 * @author Tobias Hoppe
 *
 */
public class ProcessModelToPetriNetConverter extends AbstractModelToPetriNetConverter {
	
	private final static Logger logger = Logger.getLogger(ProcessModelToPetriNetConverter.class.getName());
	/**
	 * Transforms the given {@link ProcessModel} into a {@link PetriNet}.
	 * <b><br/>Assumptions:</b><br/>
	 * - Model does not contain any {@link OrGateway}s
	 * 
	 * @param model to transform
	 * @return the created {@link PetriNet}
	 * @throws TransformationException if assumptions are violated.
	 */
	@Override
	public PetriNet convertToPetriNet(ProcessModel model) throws TransformationException {
		if(model instanceof Epc || model instanceof Bpmn<?,?>) {
			logger.info("This model is converted as abstract process model, but could be handeled by concret handler!");
		}
		ProcessModel transformedModel = prepareProcessModel(model);
		if(transformedModel == null) {
			return null;
		}
		//create places and transitions according to the flow nodes of the model
		convertFlowNodes(transformedModel.getFlowNodes());
		//add edges according to the control flow of the model
		convertControlFlowEdges(transformedModel.getControlFlow());
		return this.petriNet;
	}
	
}
