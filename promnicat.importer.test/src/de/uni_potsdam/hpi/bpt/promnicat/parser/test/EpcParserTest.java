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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.jbpt.algo.graph.GraphAlgorithms;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.epc.Epc;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExport;
import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExportBuilder;
import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.parser.EpcParser;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;

/**
 * Test class for {@link EpcParser}. Invokes BPM AI importer and forwards the resulting
 * diagram to the parser, which has to convert the process model into jBPT format.
 * 
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class EpcParserTest {
	
	/**
	 * all read-in diagrams that have to be transformed into jBPT format.
	 */
	protected static LinkedList<Diagram> diagrams = new LinkedList<Diagram>();
	/**
	 * The resulting process models in jBPT format.
	 */
	protected LinkedList<ProcessModel> processes = new LinkedList<ProcessModel>();
	
	private ModelParser transformer = new ModelParser();
	
	/**
	 * start BPM AI importer
	 */
	@BeforeClass
	public static void setUp(){
		try{
			importModels(new File("../promnicat/resources/BPMAI/model_epc0"));
		} catch (Exception e){
			fail("Unexpected exception during model import: " + e.getStackTrace().toString());
		}
		
	}
	
	/**
	 * Parses the given directory and reads in all process models in JSON format.
	 * @param url root folder of model files
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
	@Test
	public void testTransformProcess(){
	
		assertTrue(EpcParserTest.diagrams.size() != 0);
		for (Diagram d : EpcParserTest.diagrams){
			this.processes.add(this.transformer.transformProcess(d));
		}
		
		assertTrue(this.processes.get(0) instanceof Epc);
		assertTrue(this.processes.size() == 2);
		
		GraphAlgorithms<ControlFlow<FlowNode>, FlowNode> ga = new GraphAlgorithms<ControlFlow<FlowNode>, FlowNode>();
		assertTrue(ga.isConnected(((Epc) this.processes.get(0))));
		//ensure EPCs are bipartite
		assertEquals(this.processes.get(0).getEvents().size(), this.processes.get(0).getActivities().size() + 1);
		assertEquals(this.processes.get(1).getEvents().size(), this.processes.get(1).getActivities().size() + 1);
	}

}
