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
package de.uni_potsdam.hpi.bpt.promnicat.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.epc.AndConnector;
import org.jbpt.pm.epc.Document;
import org.jbpt.pm.epc.Epc;
import org.jbpt.pm.epc.EpcResource;
import org.jbpt.pm.epc.Function;
import org.jbpt.pm.epc.OrConnector;
import org.jbpt.pm.epc.ProcessInterface;
import org.jbpt.pm.epc.XorConnector;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;

/**
 * Retrieves a Diagram instance (JSON format) of the EPC process model and maps it
 * on jBPT. 
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class EpcParser implements IParser {
	
	private final static Logger logger = Logger.getLogger(EpcParser.class.getName());
	public Diagram diagram = null;
	public Epc process = null;
	public boolean strictness = false;
	public boolean error = false;
	
	/**
	 * Index for the different JSON node ids (needed for egde construction)
	 */
	public HashMap<String, Object> nodeIds = new HashMap<String, Object>();
	
	/**
	 * List for the different JSON shapes that are created at the end of parsing
	 */
	public Vector<Shape> edges = new Vector<Shape>();
	
	public EpcParser(EpcConstants epc, boolean strictness){
		this.strictness = strictness;
	}
	
	/**
	 * Clears all the variables for next parse
	 */
	public void clear(){
		this.diagram = null;
		this.process = new Epc();
		this.edges.clear();
		this.nodeIds.clear();
	}
	
	/**
	 * Creates an EPC process model by parsing the diagram's shapes. Here, all nodes are created first, the
	 * relations only at the end, after all nodes have been parsed, to connect the corresponding nodes.
	 * @param diagram
	 * @return the resulting EPC process model
	 */
	public ProcessModel transformProcess(Diagram diagram){
		clear();
		this.process.setName(diagram.getProperty(EpcConstants.PROPERTY_TITLE));
		
		//EPC processes do only have one level of childshapes (no pools/lanes), no recursion needed here
		List<Shape> shapes = diagram.getChildShapes();
		for (Shape s : shapes){
			parseIds(s);
		}
		
		//create relations at last here, since the connected nodes may be parsed later than the relation otherwise
		for (Shape s : edges){
			createEdges(s);
		}
		if (error){
			return null;
		}
		return this.process;
	}
	
	/**
	 * Parses the given id of the shape and maps it to the corresponding Jbpt element by invoking the creation
	 * method.
	 * @param Shape s to map
	 */
	private void parseIds(Shape s){
		String id = s.getStencil().getId();
		Vertex v = null;
		
		if (id.equals(EpcConstants.ENTITY_FUNCTION)){
			v = createFunction(s);
		}
		if (id.equals(EpcConstants.ENTITY_EVENT)){
			v = createEvent(s);
		}
		if (id.equals(EpcConstants.ENTITY_XORCONNECTOR)){
			v = createXorConnector(s);
		}
		if (id.equals(EpcConstants.ENTITY_ORCONNECTOR)){
			v = createOrConnector(s);
		}
		if (id.equals(EpcConstants.ENTITY_ANDCONNECTOR)){
			v = createAndConnector(s);
		}
		if (id.equals(EpcConstants.ENTITY_CONTROLFLOW) || id.equals(EpcConstants.ENTITY_RELATION)){
			edges.add(s);
			return;
		}
		if (id.equals(EpcConstants.ENTITY_PROCESSINTERFACE)){
			v = createProcessInterface(s);
		}
		if (id.equals(EpcConstants.ENTITY_POSITION) || id.equals(EpcConstants.ENTITY_ORGANIZATION) || id.equals(EpcConstants.ENTITY_ORGANIZATIONUNIT)){
			createResource(s);
			return;
		}
		if (id.equals(EpcConstants.ENTITY_DATA)){
			createDataObject(s);
			return;
		}
		if (v != null) {
			v.setId(s.getResourceId());
			this.process.addFlowNode((FlowNode) v);
			return;
		}
	}
	
	/**
	 * Creates the appropriate Jbpt instances of the given edges
	 * @param Shape s, which is an edge
	 */
	private void createEdges(Shape s){
		if (s.getStencil().getId().equals(EpcConstants.ENTITY_RELATION)){
			createRelation(s);
			return;
		}
		if (s.getStencil().getId().equals(EpcConstants.ENTITY_CONTROLFLOW)){
			createControlFlow(s);
			return;
		}
	}
	
	/**
	 * Creates a relation from the given shape, by retrieving the incoming and outgoing nodes of that
	 * relation. Since it is binary, there is at most one incoming and one outgoing node.
	 * @param s
	 */
	private void createRelation(Shape s) {
		//find the connected nodes
		if (s.getIncomings().isEmpty() && !this.strictness){
			logger.warning("Created Relation with JSON ID " + s.getResourceId() + " does not have an incoming node!");
			return;
		} else if (s.getIncomings().isEmpty() && this.strictness){
			this.error = true;
			logger.warning("Will return null for this process model. Created Relation with JSON ID " + s.getResourceId() + " does not have an incoming node!");
			return;
		}
		if (s.getOutgoings().isEmpty()&& !this.strictness){
			logger.warning("Created Relation with JSON ID " + s.getResourceId() + " does not have an outgoing node!");
			return;
		} else if (s.getOutgoings().isEmpty() && this.strictness){
			this.error = true;
			logger.warning("Will return null for this process model. Created Relation with JSON ID " + s.getResourceId() + " does not have an outgoing node!");
			
			return;
		}
		
		Shape in = s.getIncomings().get(0);
		Shape out = s.getOutgoings().get(0);
		
		if (nodeIds.get(in.getResourceId()) == null){
			logger.warning("Created Relation with JSON ID " + s.getResourceId() + " does not have an incoming node, since this was a notation element we do not map.");
			return;
		}
		
		if (nodeIds.get(out.getResourceId()) == null){
			logger.warning("Created Relation with JSON ID " + s.getResourceId() + " does not have an outgoing node, since this was a notation element we do not map.");
			return;
		}
		
		//find out which one is the FlowNode and get NodeIds
		if (nodeIds.get(out.getResourceId()) instanceof FlowNode){
			addAsAttribute(s, (FlowNode) nodeIds.get(out.getResourceId()), (Object) nodeIds.get(in.getResourceId()));
			
		}else {//the ingoing Node must be the FlowNode 
			addAsAttribute(s, (FlowNode) nodeIds.get(in.getResourceId()), (Object) nodeIds.get(out.getResourceId()));
			
		}
	}
	
	/**
	 * Checks whether the NonFlowNode is a DataNode, and if so, set the access properties regarding the
	 * information flow attribute.
	 * @param s the relation
	 * @param node
	 */
	public void checkInformationflow(Shape s, Document dataNode, FlowNode node){
		String infoflow = s.getProperty(EpcConstants.PROPERTY_INFOFLOW);
			
		if (infoflow.equals(EpcConstants.VALUE_FALSE)){ //this relation is undefined
			dataNode.addUnspecifiedFlowNode(node);
			return;
		}
			
		if (nodeIds.get(s.getIncomings().get(0)) == node){//FlowNode is the source
			if (infoflow.equals(EpcConstants.VALUE_TRUEINVERSE)){ //reading access
				dataNode.addReadingFlowNode(node);
			} else {//infoflow is True - writing access
				dataNode.addWritingFlowNode(node);
			}
		} else { //DataNode is the source
			if (infoflow.equals(EpcConstants.VALUE_TRUEINVERSE)){ //writing access
				dataNode.addWritingFlowNode(node);
			} else {//infoflow is True - reading access
				dataNode.addReadingFlowNode(node);
			}
		}
	}	
	
	/**
	 * Adds a NonFlowNode (EpcResource or Document) as attribute to a FlowNode
	 * @param flowN that has the attribute
	 * @param nonFlowN to be assigned
	 */
	public void addAsAttribute(Shape s, FlowNode flowN, Object nonFlowN){
		
		if (nonFlowN instanceof EpcResource){ //add it as EpcResource
			flowN.addResource((EpcResource)nonFlowN);
		} else { //this must be a DataNode
			checkInformationflow(s, (Document)nonFlowN, flowN);
		}
	}
	
	/**
	 * Creates a data object and adds it to the process model.
	 * @param s
	 */
	private void createDataObject(Shape s) {
		Document doc = new Document();
		doc.setName(s.getProperty(EpcConstants.PROPERTY_TITLE));
		doc.setDescription(s.getProperty(EpcConstants.PROPERTY_DESCRIPTION));
		
		prepareNode(s, doc);
		this.process.addNonFlowNode(doc);
		doc.setId(s.getResourceId());
	}
	
	/**
	 * Creates a resource and adds it to the process model.
	 * @param s
	 */
	private void createResource(Shape s) {
		EpcResource res = new EpcResource();
		res.setName(s.getProperty(EpcConstants.PROPERTY_TITLE));
		res.setDescription(s.getProperty(EpcConstants.PROPERTY_DESCRIPTION));
		//add id to map
		this.nodeIds.put(s.getResourceId(), res);
		
	}
	
	/**
	 * Creates a process interface and adds it to the process model.
	 * @param s
	 * @return the created {@link ProcessInterface}
	 */
	private Vertex createProcessInterface(Shape s) {
		ProcessInterface procInt = new ProcessInterface();
		procInt.setEntry(s.getProperty(EpcConstants.PROPERTY_ENTRY));
		
		prepareNode(s, procInt);
		return procInt;
	}
	
	/**
	 * * Creates a control flow from the given shape, by retrieving the incoming and outgoing nodes of that
	 * relation. Since control flow is binary, there is at most one incoming and one outgoing node.
	 * @param s
	 */
	private void createControlFlow(Shape s) {
		//find the connected nodes
		if (s.getIncomings().isEmpty()&& !this.strictness){
			logger.warning("Created control flow with JSON ID " + s.getResourceId() + " does not have an incoming node!");
			return;
		} else if (s.getIncomings().isEmpty() && this.strictness){
			this.error = true;
			logger.warning("Will return null for this process model. Created control flow with JSON ID " + s.getResourceId() + " does not have an incoming node!");
			return;
		}
		if (s.getOutgoings().isEmpty()&& !this.strictness){
			logger.warning("Created control flow with JSON ID " + s.getResourceId() + " does not have an outgoing node!");
			return;
		} else if (s.getOutgoings().isEmpty() && this.strictness){
			this.error = true;
			logger.warning("Will return null for this process model. Created control flow with JSON ID " + s.getResourceId() + " does not have an outgoing node!");
			return;
		}
		
		Shape in = s.getIncomings().get(0);
		Shape out = s.getOutgoings().get(0);
		
		if (nodeIds.get(in.getResourceId()) == null){
			logger.warning("Created control flow with JSON ID " + s.getResourceId() + " does not have an incoming node, since this was a notation element we do not map.");
			return;
		}
		
		if (nodeIds.get(out.getResourceId()) == null){
			logger.warning("Created control flow with JSON ID " + s.getResourceId() + " does not have an outgoing node, since this was a notation element we do not map.");
			return;
		}
		
		//get the nodes for the resourceIds
		FlowNode toNode = (FlowNode)nodeIds.get(out.getResourceId());
		FlowNode fromNode = (FlowNode)nodeIds.get(in.getResourceId());
		
		//add the control flow to the process
		this.process.addControlFlow(fromNode, toNode);		
	}
	
	/**
	 * Creates an AND connector and adds it to the process model.
	 * @param s
	 * @return  the created {@link AndConnector}
	 */
	private Vertex createAndConnector(Shape s) {
		AndConnector and = new AndConnector();
		//add id to map
		this.nodeIds.put(s.getResourceId(), and);
		return and;
	}
	
	/**
	 * Creates an OR connector and adds it to the process model.
	 * @param s
	 * @return the created {@link OrConnector}
	 */
	private Vertex createOrConnector(Shape s) {
		OrConnector or = new OrConnector();
		//add id to map
		this.nodeIds.put(s.getResourceId(), or);
		return or;
	}
	
	/**
	 * Creates an XOR connector and adds it to the process model.
	 * @param s
	 * @return the created {@link XorConnector}
	 */
	private Vertex createXorConnector(Shape s) {
		XorConnector xor = new XorConnector();
		//add id to map
		this.nodeIds.put(s.getResourceId(), xor);
		return xor;
	}
	
	/**
	 * Creates a function and adds it to the process model.
	 * @param s
	 * @return the created {@link Function}
	 */
	private Vertex createFunction(Shape s) {
		Function f = new Function();
		
		prepareNode(s, f);
		return f;
	}
	
	/**
	 * Creates an event and adds it to the process model.
	 * @param s
	 * @return the created {@link Event}
	 */
	private Vertex createEvent(Shape s) {
		Event ev = new Event();
		
		prepareNode(s, ev);
		return ev;
	}
	
	/**
	 * sets the name and description of the given node and adds the node and its id to the nodeId map.
	 * @param s
	 * @param node
	 */
	private void prepareNode(Shape s, Vertex node){
		node.setName(s.getProperty(EpcConstants.PROPERTY_TITLE));
		node.setDescription(s.getProperty(EpcConstants.PROPERTY_DESCRIPTION));
		//add id to map
		this.nodeIds.put(s.getResourceId(), node);
		
	}
}
