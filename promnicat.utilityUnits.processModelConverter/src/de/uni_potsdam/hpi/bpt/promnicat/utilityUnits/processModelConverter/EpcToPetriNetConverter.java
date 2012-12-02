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

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.pm.Event;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.epc.Epc;
import org.jbpt.throwable.TransformationException;

/**
 * This class converts an {@link Epc} model to the corresponding {@link PetriNet}.
 * @author Tobias Hoppe
 *
 */
public class EpcToPetriNetConverter extends AbstractModelToPetriNetConverter {

	/**
	 * Transforms the given {@link Epc} into a {@link PetriNet}.
	 * <b><br/>Assumptions:</b><br/>
	 * - Model does not contain any {@link OrGateway}s
	 * 
	 * @param model to transform
	 * @return the created {@link PetriNet}
	 * @throws TransformationException if assumptions are violated.
	 */
	@Override
	public PetriNet convertToPetriNet(ProcessModel model) throws TransformationException {
		if(!(model instanceof Epc)) {
			throw new IllegalArgumentException(THE_GIVEN_PROCESS_MODEL_CAN_NOT_BE_HANDELED_BY_THIS_CONVERTER);
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

	/**
	 * Converts the given {@link Event} to a {@link Place}.
	 * @param event {@link Event} to convert
	 */
	@Override
	protected void convertEvent(Event event) {
		Place p = new Place();
		super.copyAttributes(event, p);
		this.petriNet.addPlace(p);
		this.nodeMapping.put(event, p);
	}
}
