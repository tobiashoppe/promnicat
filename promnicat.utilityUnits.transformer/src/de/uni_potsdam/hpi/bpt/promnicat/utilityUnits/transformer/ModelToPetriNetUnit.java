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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer;

import java.util.UUID;
import java.util.logging.Logger;

import org.jbpt.petri.PetriNet;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.epc.Epc;
import org.jbpt.throwable.TransformationException;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.util.serializer.PetriNetSerializer;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelConverter.ModelToPetriNetConverter;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataJbpt;

/**
 * Utility Unit to convert a {@link ProcessModel} or any subclass of it,
 * like {@link Bpmn} or {@link Epc} into a {@link PetriNet}.
 * The expected input type is {@link IUnitData}<{@link ProcessModel}>.
 * The output type is {@link IUnitData}<{@link PetriNet}>.
 * @author Tobias Hoppe
 *
 */
public class ModelToPetriNetUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	private Logger logger = Logger.getLogger(ModelToPetriNetUnit.class.getName());
	private IPersistenceApi persistenceApi = null;
	
	/**
	 * Create a new instance of {@link ModelToPetriNetUnit}
	 * transforming each {@link ProcessModel} during execution without
	 * using any database lookups for already existing transformation results.
	 */
	public ModelToPetriNetUnit() {
	}
	
	/**
	 * Create a new instance of {@link ModelToPetriNetUnit}
	 * looking up already parsed {@link PetriNet}s in the database before
	 * trying to transform them from {@link ProcessModel} during execution.
	 * @param persistenceApi the database instance to use for {@link PetriNet} lookup.
	 */
	public ModelToPetriNetUnit(IPersistenceApi persistenceApi) {
		this.persistenceApi = persistenceApi;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#execute(de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData)
	 */
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		PetriNet pn = null;
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		//look up already parsed PetriNet if possible
		if (this.persistenceApi != null && input.getDbId() != null) {
			IRepresentation pmRepresentation = this.persistenceApi.loadRepresentation(input.getDbId());
			if(pmRepresentation != null) {
				if (pmRepresentation.getRevision() != null) {
					for (IRepresentation representation : pmRepresentation.getRevision().getRepresentations()) {
						if(representation.getNotation().equals(Constants.NOTATIONS.PETRINET)) {
							pn = PetriNetSerializer.parsePetriNet(representation.getDataContent());
							if(pn != null) {
								input.setValue(pn);
								return input;
							} else {
								logger.info("Petri Net could not be loaded. Try to transform current process model.");
								break;
							}
						}
					}
				}
			}
		}		
		//search for process model to parse. can be given as value of input or stored
		// in special field if input is of type IUnitDataJbpt.
		if (!(input.getValue() instanceof ProcessModel)){
			if (input instanceof IUnitDataJbpt<?>) {
				if (((IUnitDataJbpt<Object>) input).getProcessModel() != null) {
					pn = transformProcessModelToPetriNet(((IUnitDataJbpt<Object>) input).getProcessModel());
				} else {
					logger.severe("No process model found for transformation to petri net!");
					input.setValue(null);
					return input;
				}
			} else {
				throw new IllegalTypeException(ProcessModel.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
			}
		} else {
			pn = transformProcessModelToPetriNet((ProcessModel) input.getValue());
		}
		//save parsed petri net
		if(pn != null) {
			if (input instanceof IUnitDataClassification<?>) {
				((IUnitDataClassification<Object>) input).setPetriNet(pn);
			}
			if (this.persistenceApi != null && input.getDbId() != null) {
				//save parsed petri net in database
				savePetriNetInDb(input.getDbId(), pn);
			}
		}
		input.setValue(pn);
		return input;		
	}

	@Override
	public Class<?> getInputType() {
		return ProcessModel.class;
	}

	@Override
	public String getName() {
		return "ProcessModelToPetriNetUnit";
	}

	@Override
	public Class<?> getOutputType() {
		return PetriNet.class;
	}

	/**
	 * Creates a new {@link Representation} for the {@link PetriNet} to save and
	 * connects it with the {@link Revision} of the transformed {@link ProcessModel}.
	 * @param dbId database id of the transformed {@link ProcessModel}'s representation
	 * @param petriNet {@link PetriNet} to save in database
	 */
	private void savePetriNetInDb(UUID dbId, PetriNet petriNet) {
		IRepresentation pmRepresentation = this.persistenceApi.loadRepresentation(dbId);
		if(pmRepresentation == null ) {
			logger.warning("Petri Net could not be saved, due to missing representation of current process model!");
			return;
		}
		if(pmRepresentation.getRevision() == null ) {
			logger.warning("Petri Net could not be saved, due to missing revision of current process model!");
			return;
		}
		if(pmRepresentation.getModel() == null ) {
			logger.warning("Petri Net could not be saved, due to missing database model of current process model!");
			return;
		}
		byte[] petriNetBytes = PetriNetSerializer.serialize(petriNet);
		if(petriNetBytes == null) {
			logger.warning("Petri Net could not be saved, due to failing serialization!");
			return;
		}
		IRepresentation representation = persistenceApi.getPojoFactory().createRepresentation(Constants.FORMATS.PNML.toString(), Constants.NOTATIONS.PETRINET.toString(), petriNetBytes);
		pmRepresentation.getRevision().connectRepresentation(representation);
		this.persistenceApi.savePojo(pmRepresentation.getModel());
	}

	/**
	 * Transforms a given {@link ProcessModel} into a {@link PetriNet}.
	 * If the given {@link ProcessModel} can not be transformed, a warning is logged.
	 * @param processModel to transform
	 * @return <code>null</code> if the transformation failed, otherwise the generated {@link PetriNet}.
	 */
	private PetriNet transformProcessModelToPetriNet(ProcessModel processModel) {
		try {
			return new ModelToPetriNetConverter().convertToPetriNet(processModel);
		} catch (TransformationException e) {
			logger.severe("This process model can not be transformed to a petri net: " + processModel.toString()
					+ "\nThe follwoing exception has been thrown: " + e.getMessage());
			return null;
		}
	}

}
