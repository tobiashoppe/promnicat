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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jbpt.petri.PetriNet;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.classification.PetriNetSerializer;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.ModelToPetriNetUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataClassification;

/**
 * Test class for {@link ModelToPetriNetUnit}
 * @author Tobias Hoppe
 *
 */
public class ProcessModelToPetriNetUnitTest {
	
	private ModelToPetriNetUnit unit = new ModelToPetriNetUnit();
	private ProcessModel model = TestModelBuilder.getModelWithoutOrGateway(Bpmn.class);
	private IPersistenceApi persistenceApi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);

	public ProcessModelToPetriNetUnitTest() throws Exception {		
	}
	
	@Test
	public void testGetName(){
		assertEquals("ProcessModelToPetriNetUnit", unit.getName());
	}
	
	@Test
	public void testGetInputType(){
		assertEquals(ProcessModel.class, unit.getInputType());
	}
	
	@Test
	public void testGetOutputType(){
		assertEquals(PetriNet.class, unit.getOutputType());
	}

	@Test
	public void testExecuteWithoutDbAccess() throws IllegalTypeException {
		IUnitDataClassification<Object> unitData = new UnitDataClassification<Object>(this.model);
		unit.execute(unitData);
		PetriNet pn = unitData.getPetriNet();
		
		//ensure successful parsing
		assertNotNull(pn);
		assertSame(unitData.getValue(), pn);
		assertEquals(1, pn.getSinkNodes().size());
		assertEquals(1, pn.getSourceNodes().size());
		assertEquals(23, pn.getNodes().size());
		assertEquals(24, pn.getFlow().size());
		assertEquals(12, pn.getPlaces().size());
		assertEquals(11, pn.getTransitions().size());
		assertEquals(2, pn.getSilentTransitions().size());
	}
	
	@Test
	public void testExecuteWithDbAccess() throws IllegalTypeException {
		Representation repr = RepresentationFactory.createLightweightRepresentation();
		persistenceApi.savePojo(repr.getModel());
		IUnitDataClassification<Object> unitData = new UnitDataClassification<Object>(this.model);
		unitData.setDbId(repr.getDbId());
		ModelToPetriNetUnit unitPm2Pn = new ModelToPetriNetUnit(this.persistenceApi);
		unitPm2Pn.execute(unitData);
		
		//ensure successful parsing
		PetriNet pn = unitData.getPetriNet();
		assertNotNull(pn);
		assertSame(unitData.getValue(), pn);
		
		//check successful saving in and loading from database
		Model model = persistenceApi.loadCompleteModelWithDbId(repr.getModel().getDbId());
		boolean found = false;
		for(Representation representation : model.getLatestRevision().getRepresentations()) {
			if(representation.getFormat().equals(Constants.FORMATS.PNML.toString()) && representation.getNotation().equals(Constants.NOTATIONS.PETRINET.toString())) {
				found = true;
				assertNotNull(representation.getDataContent());
				PetriNet petriNet = PetriNetSerializer.parsePetriNet(representation.getDataContent());
				assertEquals(pn.getNodes().size(), petriNet.getNodes().size());
				assertEquals(pn.getEdges().size(), petriNet.getEdges().size());
			}
		}
		assertTrue("Petri Net has not been saved as expected!", found);
	}
}
