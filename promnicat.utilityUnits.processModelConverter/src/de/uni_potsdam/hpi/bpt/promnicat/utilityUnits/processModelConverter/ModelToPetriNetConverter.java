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

import java.util.HashMap;
import java.util.logging.Logger;

import org.jbpt.petri.PetriNet;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.epc.Epc;
import org.jbpt.throwable.TransformationException;

/**
 * @author Tobias Hoppe
 *
 */
public class ModelToPetriNetConverter implements IModelToPetriNetConverter {
	
	/**
	 * Hashmap for delegater converters
	 */
	private HashMap<Class<?>, IModelToPetriNetConverter> delegates;
	/**
	 * Last converted {@link PetriNet}
	 */
	private PetriNet lastPetriNet = null;
	
	public ModelToPetriNetConverter() {
		this.delegates = new HashMap<Class<?>, IModelToPetriNetConverter>();
		
		this.delegates.put(Bpmn.class, new BpmnToPetriNetConverter());
		this.delegates.put(Epc.class, new EpcToPetriNetConverter());
		this.delegates.put(ProcessModel.class, new ProcessModelToPetriNetConverter());
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.modelConverter.IModelToPetriNetConverter#convertToPetriNet(org.jbpt.pm.ProcessModel)
	 */
	@Override
	public PetriNet convertToPetriNet(ProcessModel model) throws TransformationException {
		IModelToPetriNetConverter converter = this.delegates.get(model.getClass());
		if (converter != null) {
			this.lastPetriNet = converter.convertToPetriNet(model);		
			return this.lastPetriNet;			
		} else {
			Logger.getLogger(ModelToPetriNetConverter.class.getName()).
				warning("Model from class " + model.getClass() + " could not be transformed to a petri net!");
			return null;
		}
	}

	@Override
	public PetriNet getLastPetriNet() {
		return this.lastPetriNet;
	}

}
