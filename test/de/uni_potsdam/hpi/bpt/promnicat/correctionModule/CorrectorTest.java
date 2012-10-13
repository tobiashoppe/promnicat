/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher, Christian Kieschnick
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
package de.uni_potsdam.hpi.bpt.promnicat.correctionModule;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jbpt.pm.ProcessModel;
import org.junit.Assert;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.AbstractCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.BundledEdgeCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.DiagramCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.DirectedEdgeAttacher;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.EdgeTypeCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.rules.DiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.DiagramWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.EdgeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;

/**
 * Test class for all subclasses of {@link AbstractCorrector}.
 * @author Christian Kieschnick
 *
 */
public class CorrectorTest {
	
	private void runCorrectorOnEdges(AbstractCorrector corrector, DiagramWrapper model){
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			if (corrector.fulfillsPrecondition(edge)){
				corrector.applyCorrection(edge);
			}
		}
	}
	
	@Test
	public void needsFixDanglingEdgesTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("dangling_edges");
		DirectedEdgeAttacher corrector = new DirectedEdgeAttacher();
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			if (edge.isResource("sid-0833E0CD-E147-44F1-9907-DFAE36876B1C")
					|| edge.isResource("sid-1BC4247A-8551-4AD5-8101-BC895C3ADADD")){
				Assert.assertTrue(corrector.fulfillsPrecondition(edge)); 
			} else {
				Assert.assertFalse(corrector.fulfillsPrecondition(edge));
			}
		}
	}
	
	@Test
	public void fixDanglingEdgesTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("dangling_edges");
		DirectedEdgeAttacher corrector = new DirectedEdgeAttacher();
		runCorrectorOnEdges(corrector, model);
		boolean foundDanglingEdgeToFirstTask = false;
		boolean foundDanglingEdgeToProcessEnd = false;
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			
				assertTrue(edge.getIncomings().size() == 1 && edge.getOutgoings().size() == 1);
				foundDanglingEdgeToFirstTask |= 
						edge.getIncomings().get(0).isStencil("StartNoneEvent")
						&&  edge.getOutgoings().get(0).getShape().getProperty("name").contains("Receive Goods for Required Components");
				foundDanglingEdgeToProcessEnd |=
						edge.getIncomings().get(0).getShape().getProperty("name").contains("Check Warehouse Stock")
						&&  edge.getOutgoings().get(0).isStencil("EndNoneEvent");
		}
		
		assertTrue("First edge was not connected properly", foundDanglingEdgeToFirstTask);
		assertTrue("Second edge was not connected properly", foundDanglingEdgeToProcessEnd);
	}
	
	@Test
	public void needsFixWrongEdgeTest() throws Exception  {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("process_connection_through_message_flow");
		EdgeTypeCorrector corrector = new EdgeTypeCorrector();
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			if (edge.isStencil("MessageFlow")){
				Assert.assertTrue(corrector.fulfillsPrecondition(edge)); 
			} else {
				Assert.assertFalse(corrector.fulfillsPrecondition(edge));
			}
		}
	}
	
	
	@Test
	public void fixWrongEdgeTest() throws Exception  {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("process_connection_through_message_flow");
		EdgeTypeCorrector corrector = new EdgeTypeCorrector();
		runCorrectorOnEdges(corrector, model);
		for (EdgeWrapper shape : model.getEdgeWrappers()){
			assertFalse(shape.isStencil("MessageFlow"));
		}
	}
	
	@Test
	public void needsFixWrongEdgeExtendedTest() throws Exception  {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("process_connection_through_message_flow_extended");
		EdgeTypeCorrector corrector = new EdgeTypeCorrector();
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			if (edge.isResource("sid-C6DC89BC-6593-4C88-86EB-AC7017678BED") // sequence flow
					|| edge.isResource("sid-EC5E485A-F147-473F-BCE4-FD956318DDEC") // message flows
					|| edge.isResource("sid-1A8E2C4A-F779-4750-B0B7-EBB0A7E7294E")
					|| edge.isResource("sid-5647E272-0B03-4B7B-B057-DE70418A7739")){
				Assert.assertTrue(corrector.fulfillsPrecondition(edge)); 
			} else {
				Assert.assertFalse(corrector.fulfillsPrecondition(edge));
			}
		}
	}
	
	
	@Test
	public void fixWrongEdgeExtendedTest() throws Exception  {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("process_connection_through_message_flow_extended");
		EdgeTypeCorrector corrector = new EdgeTypeCorrector();
		runCorrectorOnEdges(corrector, model);
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			if (edge.isResource("sid-48B1EDCB-9B10-4BA4-BA32-F06CDE691DE2")
					|| edge.isResource("sid-C6DC89BC-6593-4C88-86EB-AC7017678BED")){
				assertTrue(edge.isStencil("MessageFlow"));
			} else {
				assertTrue(edge.isStencil("SequenceFlow"));
			}
		}
	}
	
	@Test
	public void fixWrongAndDanglingEdgeTest() throws Exception  {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("process_connection_through_message_flow_extended");
		DiagramRules rules = model.getRules();
		runCorrectorOnEdges(new EdgeTypeCorrector(), model);
		runCorrectorOnEdges(new DirectedEdgeAttacher(), model);
		for (Shape shape : model.getDiagram().getChildShapes()){
			
			if (rules.isEdge(shape)){
				if (shape.getResourceId().contentEquals("sid-48B1EDCB-9B10-4BA4-BA32-F06CDE691DE2")
						|| shape.getResourceId().contentEquals("sid-C6DC89BC-6593-4C88-86EB-AC7017678BED")){
					assertTrue(shape.getStencilId().contentEquals("MessageFlow"));
				} else {
					assertTrue(shape.getStencilId().contentEquals("SequenceFlow"));
				}
				assertTrue(shape.getOutgoings().size() >= 1);
				assertTrue(shape.getIncomings().size() >= 1);
			}
		}
	}
	
	@Test
	public void needsFixOnlyDanglingEdgesWithMessageFlowsTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("bundled_dangling_edges_with_message_flows");
		BundledEdgeCorrector bundledEdgeCorrector = new BundledEdgeCorrector();
		boolean fulfillsBundledEdgeCorrection = false;
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			fulfillsBundledEdgeCorrection |= bundledEdgeCorrector.fulfillsPrecondition(edge);
		}
		assertTrue(fulfillsBundledEdgeCorrection);
	}
	
	@Test
	public void fixOnlyDanglingEdgesWithMessageFlowsTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("bundled_dangling_edges_with_message_flows");
		DirectedEdgeAttacher directedCorrector = new DirectedEdgeAttacher();
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			if (directedCorrector.fulfillsPrecondition(edge)){
				directedCorrector.applyCorrection(edge);
			}
		}
		BundledEdgeCorrector bundledCorrector = new BundledEdgeCorrector();
		for (EdgeWrapper edge : model.getEdgeWrappers()){
			if (bundledCorrector.fulfillsPrecondition(edge)){
				bundledCorrector.applyCorrection(edge);
			}
		}

		for (EdgeWrapper edge: model.getEdgeWrappers()){
			
			assertTrue(edge.getIncomings().size() == 1 && edge.getOutgoings().size() == 1);
		}
	}
	
	@Test
	public void handleNotCorrectableModelsTest() {
		try {
			DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("not_correctable_bpmn");
			DiagramCorrector corrector = new DiagramCorrector();
			corrector.applyCorrection(model);
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
	}
	
	@Test
	public void edgesWithoutExtendHandlingTest() throws Exception{
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("edges_without_extend_handling");
		DiagramCorrector corrector = new DiagramCorrector();
		try {
			corrector.applyCorrection(model);
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
	}
	
	@Test
	public void edgeTransformationTest() throws Exception{
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("complex_problems");
		DiagramCorrector corrector = new DiagramCorrector();
		corrector.applyCorrection(model);
		try {
			new ModelParser(true).transformProcess(model.getDiagram());
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
	}
	
//	@Test currently not working due to too many problems in model
//	public void complexCorrectionTest() throws Exception{
//		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("complex_problems");
//		DiagramCorrector corrector = new DiagramCorrector();
//		corrector.applyCorrection(model);
//		ProcessModel result = null;
//		try {
//			result = new ModelParser(true).transformProcess(model.getDiagram());
//		} catch (Exception e){
//			e.printStackTrace();
//			Assert.fail("An exception occured for not correctable model "+e.getMessage());
//		}
//		Assert.assertNotNull(result);
//	}
	
	@Test
	public void wrongShapeRegressionTest() throws Exception{
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("1006208094_rev10");
		DiagramCorrector corrector = new DiagramCorrector();
		try {
			corrector.applyCorrection(model);
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
	}
	
	@Test
	public void unparsableModelRegressionTest() throws Exception{
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("bpmn_process_model");
		DiagramCorrector corrector = new DiagramCorrector();
		corrector.applyCorrection(model);
		ProcessModel result = null;
		try {
			result = new ModelParser(true).transformProcess(model.getDiagram());
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
		Assert.assertNotNull(result);
	}
	
	@Test
	public void correctionExceptionRegressionTest() throws Exception{
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("unsupported_correction_needed");
		DiagramCorrector corrector = new DiagramCorrector();
		try {
			corrector.applyCorrection(model);
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
	}
	
	@Test
	public void edgesWithoutDockersRegressionTest() throws Exception{
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("wrong_epc_classification");
		DiagramCorrector corrector = new DiagramCorrector();
		try {
			corrector.applyCorrection(model);
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
	}
	
	@Test
	public void improvedBundledEdgeCorrectionTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("improved_bundled_edges");
		runCorrectorOnEdges(new DirectedEdgeAttacher(), model);
		runCorrectorOnEdges(new BundledEdgeCorrector(), model);
		ProcessModel result = null;
		try {
			result = new ModelParser(true).transformProcess(model.getDiagram());
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
		System.out.println(TestModelBuilder.getStringFromModel(model.getDiagram()));
		Assert.assertNotNull(result);
	}
	
	@Test
	public void validModelTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("dangling_edge_to_edge_association");
		DiagramCorrector corrector = new DiagramCorrector();
		corrector.applyCorrection(model);
		ProcessModel result = null;
		try {
			result = new ModelParser(true).transformProcess(model.getDiagram());
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
		Assert.assertNotNull(result);
	}
	
	/** @Test
	 * removed due elements are not mappable by the bpmn parser
	 * @throws Exception
	 */
	public void notMappableElementsTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("not_mappable_elements");
		DiagramCorrector corrector = new DiagramCorrector();
		corrector.applyCorrection(model);
		ProcessModel result = null;
		try {
			result = new ModelParser(true).transformProcess(model.getDiagram());
		} catch (Exception e){
			e.printStackTrace();
			Assert.fail("An exception occured for not correctable model "+e.getMessage());
		}
		Assert.assertNotNull(result);
	}
	
//	@Test currently not working due to edge cases which can not be corrected
//	public void regressionTest() throws Exception {
//		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("14027741_rev1");
//		//runCorrectorOnEdges(new WrongEdgeCorrector(), model);
//		System.out.println(TestModelBuilder.getStringFromModel(model.getDiagram()));
//
//		new DiagramCorrector().applyCorrection(model);
//		// sid-D3EAA3CD-7511-4613-9A33-EAADCC1A19D1
//		// sid-67B177EF-A61E-4DAB-A807-B27DE997DC5B
//		// are not connected correctly since they cannot be connected to edges
//		ProcessModel result = null;
//		try {
//			result = new ModelParser(true).transformProcess(model.getDiagram());
//		} catch (Exception e){
//			e.printStackTrace();
//			Assert.fail("An exception occured for not correctable model "+e.getMessage());
//		}
//		System.out.println(TestModelBuilder.getStringFromModel(model.getDiagram()));
//		Assert.assertNotNull(result);
//	}
	
	@Test
	public void paperTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("paper_model");
		runCorrectorOnEdges(new EdgeTypeCorrector() ,model);
		//System.out.println("I\t"+TestModelBuilder.getStringFromModel(model));
		runCorrectorOnEdges(new BundledEdgeCorrector(), model);
		//System.out.println("II\t"+TestModelBuilder.getStringFromModel(model));
		runCorrectorOnEdges(new DirectedEdgeAttacher(), model);
		//System.out.println("III\t"+TestModelBuilder.getStringFromModel(model));
		//System.out.print(WrongEdgeCorrector.correctionStatistics());
	}
	
	@Test
	public void multipleCorrectionTest() throws Exception {
		DiagramWrapper model = TestModelBuilder.getWrappedModelFromFile("194743468_rev19");
		new DiagramCorrector().applyCorrection(model);
	}
}
