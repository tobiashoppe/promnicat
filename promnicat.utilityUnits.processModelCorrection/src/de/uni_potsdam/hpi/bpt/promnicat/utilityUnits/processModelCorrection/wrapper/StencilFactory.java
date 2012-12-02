package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import math.geom2d.Point2D;
import math.geom2d.polygon.SimplePolygon2D;
import de.uni_potsdam.hpi.bpt.ai.diagram.Bounds;
import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.Point;
import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.ai.diagram.StencilType;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.BpmnDiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.DiagramRules;

/**
 * Class for creating an arbitrary shape for diagrams
 * @author Christian Kieschnick
 *
 */
public class StencilFactory {
	
	private Diagram diagram;
	private StencilType type;
	private String resourceId;
	private ArrayList<Point> dockers;
	private Shape parent;
	private ArrayList<Shape> incomings;
	private ArrayList<Shape> outgoings;
	private Point absolutePosition;
	private HashMap<String, String> properties = new HashMap<String, String>();
	
	public static StencilFactory createFor(String stencilType, Diagram diagram){
		StencilFactory factory = new StencilFactory();
		factory.type = new StencilType(stencilType, diagram.getStencilset());
		factory.diagram = diagram;
		factory.resourceId = "sid-"+UUID.randomUUID().toString().toUpperCase();
		return factory;
	}
	
	/**
	 * set the resource id
	 */
	public StencilFactory withResourceId(String resourceId){
		this.resourceId = resourceId;
		return this;
	}

	/**
	 * set the dockers
	 */
	public StencilFactory withDockers(ArrayList<Point> dockers){
		this.dockers = dockers;
		return this;
	}

	/**
	 * set the dockers from absolute coordinates
	 */
	public StencilFactory fromTo(Point fromAbsolute, Point toAbsolute){
		this.dockers = new ArrayList<Point>();
		this.dockers.add(fromAbsolute);
		this.dockers.add(toAbsolute);
		return this;
	}

	/**
	 * set the parent
	 */
	public StencilFactory withParent(Shape parent){
		this.parent = parent;
		return this;
	}

	/**
	 * set incoming shapes
	 */
	public StencilFactory withIncomings(ArrayList<Shape> incomings){
		this.incomings = incomings;
		return this;
	}
	
	/**
	 * set outgoing shapes
	 */
	public StencilFactory withOutgoings(ArrayList<Shape> outgoings){
		this.outgoings = outgoings;
		return this;
	}
	
	/**
	 * set the specific property
	 */
	public StencilFactory putProperty(String property, String value){
		this.properties.put(property, value);
		return this;
	}
	
	/**
	 * add to the properties the given ones
	 */
	public StencilFactory withProperties(HashMap<String, String> properties){
		for (String key : properties.keySet()){
			if (this.properties.containsKey(key)){
				properties.put(key, properties.get(key));
			}
		}
		return this;
	}
	
	/**
	 * set an absolut position
	 */
	public StencilFactory withAbsolutePosition(Point2D position){
		this.absolutePosition = GeomUtil.to(position);
		return this;
	}
	
	/**
	 * initialize a shape by using an existing one 
	 */
	private Shape basicInitialize(Shape shape){
		shape.setIncomings(incomings);
		shape.setOutgoings(outgoings);
		for (String key : properties.keySet()){
			shape.putProperty(key, properties.get(key));
		}
		shape.setParent(parent);
		return shape;
	}
	
	/**
	 * create an edge from the given properties
	 */
	public Shape getEdge(){
		de.uni_potsdam.hpi.bpt.ai.diagram.Edge shape = new de.uni_potsdam.hpi.bpt.ai.diagram.Edge(resourceId, type);
		try {
			if (DiagramRules.of(diagram) instanceof BpmnDiagramRules){
				BpmnDiagramRules rules = (BpmnDiagramRules) DiagramRules.of(diagram);
				if (type.getId().contentEquals("SequenceFlow")){
					putProperty(rules.getConstants().getPropertyConditionType(), "");
				}
			}
		} catch (UnsupportedModelException e) {
			// at this point no exception should occur - unsupported models should be filtered out at the beginning of correction
			e.printStackTrace();
		}
		basicInitialize(shape);
		shape.setDockers(dockers);
		ArrayList<Point2D> dockers2D = new ArrayList<Point2D>();
		for (Point docker : dockers){
			dockers2D.add(GeomUtil.to(docker));
		}
		SimplePolygon2D polygon = new SimplePolygon2D(dockers2D);
		shape.setBounds(GeomUtil.toBounds(polygon.getBoundingBox()));
		return shape;
	}
	
	/**
	 * create a node from the given properties
	 */
	public Shape getNode(){
		de.uni_potsdam.hpi.bpt.ai.diagram.Node shape = new de.uni_potsdam.hpi.bpt.ai.diagram.Node(resourceId, type);
		basicInitialize(shape);
		// this extend is only for debugging purposes - for a nice output, the default values of signavio should be used
		Point halfExtend = new Point(20d, 20d);
		shape.setBounds(new Bounds(
				GeomUtil.sub(absolutePosition, halfExtend), 
				GeomUtil.add(absolutePosition, halfExtend)));
		return shape;
	}
}
