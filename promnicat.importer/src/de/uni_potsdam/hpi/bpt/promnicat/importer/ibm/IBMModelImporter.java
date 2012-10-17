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
package de.uni_potsdam.hpi.bpt.promnicat.importer.ibm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONException;
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
import de.uni_potsdam.hpi.bpt.promnicat.importer.AbstractImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * This class is used to import the SAP reference model.
 * 
 * @author Tobias Hoppe
 * 
 */
public class IBMModelImporter extends AbstractImporter {
	private IPersistenceApi persistenceApi = null;
	private final static Logger logger = Logger.getLogger(IBMModelImporter.class.getName());
	/**
	 * Creates a new {@link IBMModelImporter} with the given {@link IPersistenceApi} used for database access.
	 * @param persistenceApi persistence API used by importer
	 */
	public IBMModelImporter(IPersistenceApi persistenceApi) {
		this.persistenceApi = persistenceApi;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_potsdam.hpi.bpt.promnicat.importer.IImporter#importModelsFrom(java.net.URI)
	 */
	@Override
	public void importModelsFrom(String modelDirectory) throws IOException, JSONException {
	    	persistenceApi.openDb();
	    	File rootDir = super.checkModelPath(modelDirectory, true);
	    	Collection<File> files = super.getFilesRecursivelyFromDir(rootDir);
	    	for(File file:files){
	    	    String id = file.getAbsolutePath().replace(rootDir.getAbsolutePath(), "");
	    	    //PromniCat does not support slashes in ids
	    	    id = id .replace(File.separator, "_");
		    IModel model = this.persistenceApi.loadCompleteModelWithImportedId(id);
	    	if (model == null){
			//create and save new Model
			model = parseModel(file, id);
			persistenceApi.savePojo(model);

		} else {
		    logger.warning("Model already there");
		}
	    	}

	    	persistenceApi.closeDb();
	}

	private IModel parseModel(File file, String id) {
		IPojoFactory pojoFac = this.persistenceApi.getPojoFactory();
	    IModel model = pojoFac.createModel(file.getName(), Constants.ORIGIN_IBM, id);
	    IRevision revision = pojoFac.createRevision(0);
	    revision.connectRepresentation(pojoFac.createRepresentation(Constants.FORMAT_XML, Constants.NOTATION_BPMN2_0, file ));
	    try {
		revision.connectRepresentation(pojoFac.createRepresentation(Constants.FORMAT_BPMAI_JSON, Constants.NOTATION_BPMN2_0, parseIBMBPMN2Diagram(file).getBytes() ));
	    } catch (JAXBException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    model.connectLatestRevision(revision); 
	    return model;
	}
	public String parseIBMBPMN2Diagram(File xml) throws JAXBException, JSONException{
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
			setStandardBounds(diagram);
//			List<Shape> shapes = new ArrayList<Shape>();
			Map<String, Shape> shapes = new HashMap<String, Shape>();
			for(FlowElement flowElement : p.getFlowElement()){
			    Shape shape = new Shape(flowElement.getId());
			    setStandardBounds(shape);
			    flowElement.toShape(shape);
			    if(flowElement.getName() != null){
				shape.getProperties().put("name", flowElement.getName());
			    }
			    shapes.put(flowElement.getId(),shape);
			}
			for(FlowElement flowElement : p.getFlowElement()){
			    if(flowElement instanceof Edge){
				Edge edge = (Edge) flowElement;
				if(edge.getSourceRef() !=null){
				    Shape current = shapes.get(edge.getSourceRef().getId());
				    current.addOutgoing(new Shape(edge.getId()));
				    shapes.get(edge.getId()).addIncoming(current);
				}
				
			    }
			}
			
			diagram.getChildShapes().addAll(shapes.values());
			
			diagrams.add(diagram);
		    }
		}

		String parseModeltoString = JSONBuilder.parseModeltoString(diagrams.get(0));
//		parseModeltoString = "{\"resourceId\":\"oryx-canvas123\",\"properties\":{\"name\":\"\",\"documentation\":\"\",\"auditing\":\"\",\"monitoring\":\"\",\"version\":\"\",\"author\":\"\",\"language\":\"English\",\"namespaces\":\"\",\"targetnamespace\":\"http://www.omg.org/bpmn20\",\"expressionlanguage\":\"http://www.w3.org/1999/XPath\",\"typelanguage\":\"http://www.w3.org/2001/XMLSchema\",\"creationdate\":\"\",\"modificationdate\":\"\"},\"stencil\":{\"id\":\"BPMNDiagram\"},\"childShapes\":[{\"resourceId\":\"oryx_7AD3C7F9-438D-4F61-9FBB-243027E573A9\",\"properties\":{\"name\":\"zrz\",\"documentation\":\"\",\"auditing\":\"\",\"monitoring\":\"\",\"categories\":\"\",\"startquantity\":1,\"completionquantity\":1,\"isforcompensation\":\"\",\"assignments\":\"\",\"callacitivity\":\"\",\"tasktype\":\"None\",\"implementation\":\"webService\",\"resources\":\"\",\"messageref\":\"\",\"operationref\":\"\",\"instantiate\":\"\",\"script\":\"\",\"script_language\":\"\",\"bgcolor\":\"#ffffcc\",\"looptype\":\"None\",\"testbefore\":\"\",\"loopcondition\":\"\",\"loopmaximum\":\"\",\"loopcardinality\":\"\",\"loopdatainput\":\"\",\"loopdataoutput\":\"\",\"inputdataitem\":\"\",\"outputdataitem\":\"\",\"behavior\":\"all\",\"complexbehaviordefinition\":\"\",\"completioncondition\":\"\",\"onebehavioreventref:\":\"signal\",\"nonebehavioreventref\":\"signal\",\"properties\":\"\",\"datainputset\":\"\",\"dataoutputset\":\"\"},\"stencil\":{\"id\":\"Task\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_F131DEFD-3632-4523-8A2A-18CCA592EA65\"}],\"bounds\":{\"lowerRight\":{\"x\":366,\"y\":163},\"upperLeft\":{\"x\":266,\"y\":83}},\"dockers\":[]},{\"resourceId\":\"oryx_FB713124-E16B-4325-9B87-AF7B91C21076\",\"properties\":{\"name\":\"zrz\",\"documentation\":\"\",\"auditing\":\"\",\"monitoring\":\"\",\"categories\":\"\",\"startquantity\":1,\"completionquantity\":1,\"isforcompensation\":\"\",\"assignments\":\"\",\"callacitivity\":\"\",\"tasktype\":\"None\",\"implementation\":\"webService\",\"resources\":\"\",\"messageref\":\"\",\"operationref\":\"\",\"instantiate\":\"\",\"script\":\"\",\"script_language\":\"\",\"bgcolor\":\"#ffffcc\",\"looptype\":\"None\",\"testbefore\":\"\",\"loopcondition\":\"\",\"loopmaximum\":\"\",\"loopcardinality\":\"\",\"loopdatainput\":\"\",\"loopdataoutput\":\"\",\"inputdataitem\":\"\",\"outputdataitem\":\"\",\"behavior\":\"all\",\"complexbehaviordefinition\":\"\",\"completioncondition\":\"\",\"onebehavioreventref:\":\"signal\",\"nonebehavioreventref\":\"signal\",\"properties\":\"\",\"datainputset\":\"\",\"dataoutputset\":\"\"},\"stencil\":{\"id\":\"Task\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":511,\"y\":163},\"upperLeft\":{\"x\":411,\"y\":83}},\"dockers\":[]},{\"resourceId\":\"oryx_F131DEFD-3632-4523-8A2A-18CCA592EA65\",\"properties\":{\"name\":\"\",\"documentation\":\"\",\"auditing\":\"\",\"monitoring\":\"\",\"conditiontype\":\"None\",\"conditionexpression\":\"\",\"isimmediate\":\"\",\"showdiamondmarker\":\"\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_FB713124-E16B-4325-9B87-AF7B91C21076\"}],\"bounds\":{\"lowerRight\":{\"x\":410.15625,\"y\":124},\"upperLeft\":{\"x\":366.84375,\"y\":122}},\"dockers\":[{\"x\":50,\"y\":40},{\"x\":50,\"y\":40}],\"target\":{\"resourceId\":\"oryx_FB713124-E16B-4325-9B87-AF7B91C21076\"}}],\"bounds\":{\"lowerRight\":{\"x\":1485,\"y\":1050},\"upperLeft\":{\"x\":0,\"y\":0}},\"stencilset\":{\"url\":\"/oryx//stencilsets/bpmn2.0/bpmn2.0.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"ssextensions\":[]}";
//		System.out.println(parseModeltoString);
		return parseModeltoString;

	}

	public void setStandardBounds(Shape shape) {
	    Point lr = new Point(200d, 200d);
	    Point ul = new Point(100d,100d);
	    Bounds bounds = new Bounds(lr, ul);
	    shape.setBounds(bounds);
	}


}
