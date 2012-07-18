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
package de.uni_potsdam.hpi.bpt.promnicat.modelConverter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jbpt.petri.PetriNet;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.epc.Epc;
import org.jbpt.throwable.TransformationException;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.modelConverter.IModelToPetriNetConverter;
import de.uni_potsdam.hpi.bpt.promnicat.modelConverter.ModelToPetriNetConverter;

/**
 * Test class for {@link ModelToPetriNetConverter}.
 * @author Tobias Hoppe
 *
 */
public class ModelToPetriNetConverterTest {

	private static final String GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION = "Got unexpected transformation exception!";

	@Test
	public void testSequenceConverting() throws InstantiationException, IllegalAccessException {
		ModelToPetriNetConverter converter = new ModelToPetriNetConverter();		
		try {
			//as ProcessModel
			PetriNet pn = converter.convertToPetriNet(TestModelBuilder.getSequence(5, ProcessModel.class));			
			assertEquals(11, pn.getNodes().size());
			assertEquals(10, pn.getFlow().size());
			assertEquals(6, pn.getPlaces().size());
			assertEquals(5, pn.getTransitions().size());
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			//as epc
			pn = converter.convertToPetriNet(TestModelBuilder.getSequence(5, Epc.class));			
			assertEquals(5, pn.getNodes().size());
			assertEquals(4, pn.getFlow().size());
			assertEquals(3, pn.getPlaces().size());
			assertEquals(2, pn.getTransitions().size());
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
	
	@Test
	public void testModelWithoutOrgatewayAndAttachedEvent() throws InstantiationException, IllegalAccessException {
		ProcessModel model = TestModelBuilder.getModelWithoutOrGateway(ProcessModel.class);
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();		
		try {
			PetriNet pn = converter.convertToPetriNet(model);
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(23, pn.getNodes().size());
			assertEquals(24, pn.getFlow().size());
			assertEquals(12, pn.getPlaces().size());
			assertEquals(11, pn.getTransitions().size());
			assertEquals(2, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
	
	@Test
	public void testDelegate() throws InstantiationException, IllegalAccessException {
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();		
		try {
			//check epc
			PetriNet pn = converter.convertToPetriNet((Epc)TestModelBuilder.getModelWithoutOrGateway(Epc.class));
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(17, pn.getNodes().size());
			assertEquals(18, pn.getFlow().size());
			assertEquals(9, pn.getPlaces().size());
			assertEquals(8, pn.getTransitions().size());
			assertEquals(2, pn.getSilentTransitions().size());
			//check bpmn
			pn = converter.convertToPetriNet((Bpmn<?,?>)TestModelBuilder.getModelWithoutOrGateway(Bpmn.class));
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(23, pn.getNodes().size());
			assertEquals(24, pn.getFlow().size());
			assertEquals(12, pn.getPlaces().size());
			assertEquals(11, pn.getTransitions().size());
			assertEquals(2, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
	
	@Test
	public void testMultiProcessConverting() {
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
		try {
			PetriNet pn = converter.convertToPetriNet(TestModelBuilder.getDisconnectedModel());
			assertEquals(4, pn.getSinkNodes().size());
			assertEquals(4, pn.getSourceNodes().size());
			assertEquals(23, pn.getNodes().size());
			assertEquals(20, pn.getFlow().size());
			assertEquals(14, pn.getPlaces().size());
			assertEquals(9, pn.getTransitions().size());
			assertEquals(0, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
	
	@Test
	public void testSubprocessConverting() {
		//TODO implement me
	}
	
	@Test
	public void testOrGatewayConverting() {
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
		try {
			PetriNet pn = converter.convertToPetriNet(TestModelBuilder.getOrGatewayBlockModel());
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(22, pn.getNodes().size());
			assertEquals(26, pn.getFlow().size());
			assertEquals(11, pn.getPlaces().size());
			assertEquals(11, pn.getTransitions().size());
			assertEquals(5, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
	
	@Test
	public void testAndGatewayConverting() {
		//TODO implement me
	}
	
	@Test
	public void testXorGatewayConverting() {
		//TODO implement me
	}
	
	@Test
	public void testBoundaryEventConverting() {
		//TODO implement me
	}
}
