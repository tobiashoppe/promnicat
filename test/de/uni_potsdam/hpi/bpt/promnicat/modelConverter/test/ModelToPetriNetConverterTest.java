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
import org.jbpt.pm.Activity;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jbpt.pm.bpmn.BpmnEvent;
import org.jbpt.pm.bpmn.Subprocess;
import org.jbpt.pm.bpmn.ThrowingEvent;
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
			PetriNet pn = converter.convertToPetriNet(TestModelBuilder.getSequence(6, ProcessModel.class));			
			assertEquals(13, pn.getNodes().size());
			assertEquals(12, pn.getFlow().size());
			assertEquals(7, pn.getPlaces().size());
			assertEquals(6, pn.getTransitions().size());
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			//as epc
			pn = converter.convertToPetriNet(TestModelBuilder.getSequence(6, Epc.class));			
			assertEquals(7, pn.getNodes().size());
			assertEquals(6, pn.getFlow().size());
			assertEquals(4, pn.getPlaces().size());
			assertEquals(3, pn.getTransitions().size());
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSubprocessConverting() {
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model = new Bpmn<BpmnControlFlow<FlowNode>, FlowNode>();
		Subprocess sp = new Subprocess("sp");
		model.addControlFlow(new Activity("t"), sp, true);
		sp.setCollapsed(false);
		try {
			sp.setSubProcess((Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) TestModelBuilder.getSequence(4, Bpmn.class));
			model.addControlFlow(sp, new Activity("t1"), true);			
			IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
			PetriNet pn = converter.convertToPetriNet(model);
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(17, pn.getNodes().size());
			assertEquals(16, pn.getFlow().size());
			assertEquals(9, pn.getPlaces().size());
			assertEquals(8, pn.getTransitions().size());
			assertEquals(0, pn.getSilentTransitions().size());
		} catch (Exception e) {
			fail("Got unexpected exception!");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSubprocessAttachedEventConverting() {
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model = new Bpmn<BpmnControlFlow<FlowNode>, FlowNode>();
		Subprocess sp = new Subprocess("sp");
		model.addControlFlow(new Activity("t"), sp, true);
		sp.setCollapsed(false);
		try {
			sp.setSubProcess((Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) TestModelBuilder.getSequence(2, Bpmn.class));
			model.addControlFlow(sp, new Activity("t1"), true);
			BpmnEvent attachedEvent = new ThrowingEvent("attachedEvent");
			attachedEvent.setInterrupted(true);
			model.addControlFlow(sp, new Activity("tEx"), false).attachEvent(attachedEvent);			
			IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
			PetriNet pn = converter.convertToPetriNet(model);
			assertEquals(2, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(22, pn.getNodes().size());
			assertEquals(33, pn.getFlow().size());
			assertEquals(11, pn.getPlaces().size());
			assertEquals(11, pn.getTransitions().size());
			assertEquals(0, pn.getSilentTransitions().size());			
			//with non-interrupting event
			attachedEvent.setInterrupted(false);
			pn = converter.convertToPetriNet(model);
			assertEquals(2, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(20, pn.getNodes().size());
			assertEquals(21, pn.getFlow().size());
			assertEquals(11, pn.getPlaces().size());
			assertEquals(9, pn.getTransitions().size());
			assertEquals(0, pn.getSilentTransitions().size());
		} catch (Exception e) {
			fail("Got unexpected exception!");
		}
	}
	
	@Test
	public void testAdhocSubprocessConverting() {
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
		try {
			PetriNet pn = converter.convertToPetriNet(TestModelBuilder.getAdHocSubprocessModel(true));
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(26, pn.getNodes().size());
			assertEquals(30, pn.getFlow().size());
			assertEquals(13, pn.getPlaces().size());
			assertEquals(13, pn.getTransitions().size());
			assertEquals(6, pn.getSilentTransitions().size());
			
			pn = converter.convertToPetriNet(TestModelBuilder.getAdHocSubprocessModel(false));
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(22, pn.getNodes().size());
			assertEquals(24, pn.getFlow().size());
			assertEquals(10, pn.getPlaces().size());
			assertEquals(12, pn.getTransitions().size());
			assertEquals(6, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
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
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
		try {
			PetriNet pn = converter.convertToPetriNet(TestModelBuilder.getAndGatewayBlockModel());
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(10, pn.getNodes().size());
			assertEquals(10, pn.getFlow().size());
			assertEquals(6, pn.getPlaces().size());
			assertEquals(4, pn.getTransitions().size());
			assertEquals(2, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
	
	@Test
	public void testXorGatewayConverting() {
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
		try {
			PetriNet pn = converter.convertToPetriNet(TestModelBuilder.getXorGatewayBlockModel());
			assertEquals(1, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(4, pn.getNodes().size());
			assertEquals(4, pn.getFlow().size());
			assertEquals(2, pn.getPlaces().size());
			assertEquals(2, pn.getTransitions().size());
			assertEquals(0, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
	
	@Test
	public void testAttachedEventConverting() {
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model = new Bpmn<BpmnControlFlow<FlowNode>, FlowNode>();
		Activity t = new Activity("t");
		BpmnEvent attachedEvent = new ThrowingEvent("e");
		attachedEvent.setInterrupted(true);
		model.addControlFlow(t, new Activity("t1"), false).attachEvent(attachedEvent);
		model.addControlFlow(t,new Activity("t2"), true);
		IModelToPetriNetConverter converter = new ModelToPetriNetConverter();
		try {
			PetriNet pn = converter.convertToPetriNet(model);
			assertEquals(2, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(9, pn.getNodes().size());
			assertEquals(8, pn.getFlow().size());
			assertEquals(5, pn.getPlaces().size());
			assertEquals(4, pn.getTransitions().size());
			assertEquals(0, pn.getSilentTransitions().size());
			
			//change to non-interrupting attached event
			attachedEvent.setInterrupted(false);
			pn = converter.convertToPetriNet(model);
			assertEquals(2, pn.getSinkNodes().size());
			assertEquals(1, pn.getSourceNodes().size());
			assertEquals(9, pn.getNodes().size());
			assertEquals(9, pn.getFlow().size());
			assertEquals(5, pn.getPlaces().size());
			assertEquals(4, pn.getTransitions().size());
			assertEquals(0, pn.getSilentTransitions().size());
		} catch (TransformationException e) {
			fail(GOT_UNEXPECTED_TRANSFORMATION_EXCEPTION);
		}
	}
}
