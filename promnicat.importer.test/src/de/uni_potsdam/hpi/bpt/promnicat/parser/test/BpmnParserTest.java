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
package de.uni_potsdam.hpi.bpt.promnicat.parser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.Resource;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExport;
import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExportBuilder;
import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;

/**
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class BpmnParserTest {
	
	/**
	 * all read-in diagrams that have to be transformed into jBPT format.
	 */
	protected static ArrayList<Diagram> diagrams = new ArrayList<Diagram>();
	/**
	 * The resulting process models in jBPT format.
	 */
	protected ArrayList<ProcessModel> processes = new ArrayList<ProcessModel>();
	
	private ModelParser transformer = new ModelParser();
	
	@BeforeClass
	public static void setUp(){
		//start BPM AI importer here
		try{
			importModels(new File("../promnicat/resources/BPMAI/model_bpmn0"));
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Parses the given directory and reads in all process models in json format.
	 * @param url
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void importModels(File url) throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException{
		BPMAIExport directoryWalker = BPMAIExportBuilder.parseDirectory(url);
		for (de.uni_potsdam.hpi.bpt.ai.collection.Model m : directoryWalker.getModels()) {
			for (de.uni_potsdam.hpi.bpt.ai.collection.Revision r : m.getRevisions()) {
				diagrams.add(r.getDiagram());	
			}
		}
	}
	
	/**
	 * Tests the transformation for all read-in diagram objects.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTransformProcess(){
		assertTrue(BpmnParserTest.diagrams.size() != 0);
		for (Diagram d : BpmnParserTest.diagrams){
			this.processes.add(this.transformer.transformProcess(d));
		}
		assertTrue(this.processes.size() == 2);
		assertTrue(this.processes.get(0) instanceof Bpmn);
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> process = (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) this.processes.get(0);
		assertEquals(2, process.filter(AndGateway.class).size());
		assertEquals(6, process.filter(Activity.class).size());
		assertEquals(0, process.filter(Event.class).size());
		
		assertTrue(this.processes.get(1) instanceof Bpmn);
		process = (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) this.processes.get(1);
		assertEquals(2, process.filter(AndGateway.class).size());
		assertEquals(6, process.filter(Activity.class).size());
		assertEquals(2, process.filter(Event.class).size());
	}
	
	@Test
	public void messageFlowTest() {
		Resource pool1 = new Resource();
		Resource pool2 = new Resource();
		Activity a = new Activity();
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model = new Bpmn<BpmnControlFlow<FlowNode>, FlowNode>();
		
		model.addMessageFlow(pool1, pool2);
		assertEquals(2, model.getNonFlowNodes().size());
		assertTrue(model.getFlowNodes().isEmpty());
		assertTrue(model.getVertices().isEmpty());
		
		model.addMessageFlow(pool1, a);
		assertEquals(2, model.getNonFlowNodes().size());
		assertEquals(1, model.getFlowNodes().size());
		assertEquals(1, model.getVertices().size());
		assertTrue(model.getVertices().iterator().next() instanceof FlowNode);
	}

}
