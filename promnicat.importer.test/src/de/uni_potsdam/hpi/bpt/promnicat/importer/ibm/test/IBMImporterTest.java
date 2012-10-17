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
package de.uni_potsdam.hpi.bpt.promnicat.importer.ibm.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilSet;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.RootElement;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.ConnectedEPC;
import de.uni_potsdam.hpi.bpt.promnicat.importer.bpmai.BpmaiImporter;
import de.uni_potsdam.hpi.bpt.promnicat.importer.ibm.IBMModelImporter;
import de.uni_potsdam.hpi.bpt.promnicat.importer.test.ImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.UnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataJbpt;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataJbpt;

/**
 * test class for {@link BpmaiImporter}.
 * 
 * @author Tobias Hoppe
 */
public class IBMImporterTest {
	
	private static IPersistenceApi persistenceApi = null;
	
	@BeforeClass
	public static void init(){
		try {
			persistenceApi = new ConfigurationParser(Constants.TEST_DB_CONFIG_PATH).getDbInstance(Constants.DATABASE_TYPES.ORIENT_DB);
		} catch (IOException e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDown(){
		persistenceApi.dropDb();
	}
	
	@Test
	public void testUnknownFileImport(){
		IBMModelImporter modelImporter = new IBMModelImporter(persistenceApi);
		ImporterTest.testUnknownFileImport(modelImporter);			
	}
	
	@Test
	public void testUnknownFilesImport(){
	    IBMModelImporter modelImporter = new IBMModelImporter(persistenceApi);
		ImporterTest.testUnknownFilesImport(modelImporter);
	}
	
	@Test
	public void importModels(){
	    IBMModelImporter modelImporter = new IBMModelImporter(persistenceApi);
		String filePath = "resources/IBM";
		ImporterTest.importModelsTwice(persistenceApi, modelImporter, filePath, 2, 2, 2);
		filePath = "resources/IBM";
		ImporterTest.importModelsTwice(persistenceApi, modelImporter, filePath, 2, 2, 2);	
		
		persistenceApi.dropDb();
	}
	@Test
	public void parse() throws JAXBException, JSONException{
		File xml = new File("resources\\IBM\\A\\s00000016\\s00000018\\s00000020\\s00000024\\s00000777.bpmn.xml");
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Definitions definitions = (Definitions) unmarshaller.unmarshal(xml);

		List<Diagram> diagrams = new ArrayList<Diagram>();
		for(RootElement e: definitions.getRootElement()){
		    if(e instanceof de.hpi.bpmn2_0.model.Process){
			de.hpi.bpmn2_0.model.Process p = (de.hpi.bpmn2_0.model.Process) e;
			String resourceId = "oryx-canvas123";
			StencilType type = new StencilType("BPMNDiagram");
			String stencilSetNs = "http://b3mn.org/stencilset/bpmn2.0#";
			String url = "/oryx/stencilsets/bpmn2.0/bpmn2.0.json";
			StencilSet stencilSet = new StencilSet(url, stencilSetNs);
			Diagram diagram = new Diagram(resourceId, type, stencilSet);

//			List<Shape> shapes = new ArrayList<Shape>();
			Map<String, Shape> shapes = new HashMap<String, Shape>();
			for(FlowElement flowElement : p.getFlowElement()){
			    if(flowElement instanceof Edge){
				Edge edge = (Edge) flowElement;
				if(edge.getSourceRef() !=null){
					//FIXME parse all nodes first and afterwards add edges.
				    shapes.get(edge.getSourceRef().getId()).addOutgoing(new Shape(edge.getId()));
				}
				
			    }
			    Shape shape = new Shape(flowElement.getId());
			    Point lr = new Point(200d, 200d);
			    Point ul = new Point(100d,100d);
			    Bounds bounds = new Bounds(lr, ul);
			    shape.setBounds(bounds);
			    flowElement.toShape(shape);
			    if(flowElement.getName() != null){
				shape.getProperties().put("name", flowElement.getName());
			    }
			    shapes.put(flowElement.getId(),shape);
			}
			
			diagram.getChildShapes().addAll(shapes.values());
			
			diagrams.add(diagram);
		    }
		}
		//TODO use logger
		System.out.println(JSONBuilder.parseModeltoString(diagrams.get(0)));

	}
	
	    @Test
	    public void parseModels() throws IllegalTypeException, IOException, JSONException{
		IBMModelImporter modelImporter = new IBMModelImporter(persistenceApi);
		String filePath = "resources/IBM";
		modelImporter.importModelsFrom(filePath);
		//build up chain
		persistenceApi.openDb();
		IUnitChainBuilder chainBuilder = new UnitChainBuilder(persistenceApi, 3, UnitDataJbpt.class);
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addFormat(Constants.FORMAT_BPMAI_JSON);
		chainBuilder.addDbFilterConfig(dbFilter);
		//transform to jbpt and check for connectedness
		chainBuilder.createBpmaiJsonToJbptUnit();
		chainBuilder.createConnectednessFilterUnit();
		
		//collect results
		chainBuilder.createSimpleCollectorUnit();
		
			
		//run chain
		@SuppressWarnings("unchecked")
		Collection<IUnitDataJbpt<Object> > result = (Collection<IUnitDataJbpt<Object>>) chainBuilder.getChain().execute();
		
		//print result
		ConnectedEPC.printResult(result);

		persistenceApi.closeDb();
		persistenceApi.dropDb();
	    }

}
