package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.DiagramBuilder;
import de.uni_potsdam.hpi.bpt.ai.diagram.JSONBuilder;
import de.uni_potsdam.hpi.bpt.ai.diagram.Point;
import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.InvalidModelException;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.rules.DiagramRules;

/**
 * a wrapper around {@link de.uni_potsdam.hpi.bpt.ai.diagram.Diagram} for more control and shortcut methods
 * @author Christian Kieschnick
 *
 */
public class DiagramWrapper extends NodeWrapper {
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DiagramWrapper.class.getName());
	
	/**
	 * the rules which the model should conform to
	 */
	private DiagramRules rules;
	/**
	 * all wrapped edges of the model
	 */
	private HashMap<String, EdgeWrapper> edges = new HashMap<String, EdgeWrapper>();
	/**
	 * all wrapped nodes of the model
	 */
	private HashMap<String, NodeWrapper> nodes = new HashMap<String, NodeWrapper>();
	/**
	 * all wrapped shapes of the model
	 */
	private HashMap<ShapeWrapper, Shape> shapes = new HashMap<ShapeWrapper, Shape>();
	
	public DiagramWrapper(Diagram diagram) throws InvalidModelException, UnsupportedModelException{
		super(diagram, 0);
		this.diagram = this;
		this.rules = DiagramRules.of(diagram);
		wrapAndInsert(diagram, 0);
		setEdgeAndNodeCollections(0, diagram.getChildShapes());
	}
	
	@Override
	public DiagramRules getRules(){
		return rules;
	}
	
	/**
	 * get all wrapped shapes
	 * @return
	 */
	public List<ShapeWrapper> getShapeWrappers(){
		return new ArrayList<ShapeWrapper>(shapes.keySet());
	}
	
	/**
	 * get the unwrapped diagram
	 * @return
	 */
	public Diagram getDiagram(){
		return (Diagram) this.shape;
	}
	
	/**
	 * wrap all shapes in a recursive fashion
	 * @throws InvalidModelException
	 */
	private void setEdgeAndNodeCollections(int depth, List<Shape> shapes) throws InvalidModelException{
		for (Shape shape : shapes){
			ShapeWrapper wrapper = wrapAndInsert(shape, depth);
			setEdgeAndNodeCollections(depth + 1, wrapper.getChildShapes());
		}
	}
	
	/**
	 * looks for a shape which is likely to be the parent of the given shape
	 * @param containedNode
	 * @return the candidate shape 
	 */
	public ShapeWrapper getPositionalParentFor(Shape containedNode){
		int depth = 0;
		ShapeWrapper candidate = this; 
		for (NodeWrapper node : nodes.values()){
			if (node.canContain(shape) 
					&& node.contains(shape)
					&& depth < node.getDepth()
					&& !node.getDirectAncestors().contains(candidate)
					&& node != candidate){
				candidate = node;
				depth = node.getDepth();
			}
		}
		return candidate;
	}
	
	/**
	 * get all instances of edges wrapped 
	 * @return
	 */
	public List<EdgeWrapper> getEdgeWrappers(){
		return new ArrayList<EdgeWrapper>(edges.values());
	}
	
	/**
	 * get all instances of nodes wrapped
	 * @return
	 */
	public List<NodeWrapper> getNodeWrappers(){
		return new ArrayList<NodeWrapper>(nodes.values());
	}
	
	@Override
	public boolean needsIncomingControlFlow(){
		return false;
	}
	
	@Override
	public boolean needsOutgoingControlFlow(){
		return false;
	}
	
	/**
	 * retrieve the wrapper for the given shape
	 */
	public ShapeWrapper getShapeWrapperFor(Shape shape){
		return getShapeWrapperForId(shape.getResourceId());
	}

	/**
	 * get the wrapper for the shape with the given id
	 */
	public ShapeWrapper getShapeWrapperForId(String resourceId) {
		if (nodes.containsKey(resourceId)){
			return nodes.get(resourceId);
		}
		if (edges.containsKey(resourceId)){
			return edges.get(resourceId);
		}
		if (shape.getResourceId() == resourceId){
			return this;
		}
		throw new InvalidModelException("Unknwon shape "+resourceId);
	}

	/**
	 * retrieve the wrappers for the given shapes
	 */
	public List<ShapeWrapper> getShapeWrappersFor(List<Shape> shapes){
		ArrayList<ShapeWrapper> wrappers = new ArrayList<ShapeWrapper>();
		for (Shape shape : shapes){
			wrappers.add(getShapeWrapperFor(shape));
		}
		return wrappers;
	}

	@Override
	public boolean isDiagram() {
		return true;
	}
	
	/**
	 * add a new shape to the model with the given depth
	 * @return the wrapped shape
	 */
	public ShapeWrapper wrapAndInsert(Shape shape, int depth){
		ShapeWrapper wrapper = ShapeWrapper.createShapeWrapper(this, shape, depth + 1);
		if (!shapes.values().contains(shape)){
			this.shapes.put(wrapper, shape);
			if (wrapper.isEdge()){
				this.edges.put(shape.getResourceId(), (EdgeWrapper)wrapper);
			}
			if (wrapper.isNode()){
				this.nodes.put(shape.getResourceId(), (NodeWrapper)wrapper);
			}
		}
		return wrapper;
	}
	
	@Override
	public NodeWrapper getParentShapeWrapper(){
		return null;
	}
	
	/**
	 * replace the former shape of the given edge wrapper with the actual one
	 * @param edge
	 */
	public void updateForChangedEdge(EdgeWrapper edge) {
		Shape originalShape = shapes.get(edge);
		getDiagramWrapper().getChildShapes().remove(originalShape);
		getDiagramWrapper().getChildShapes().add(edge.getShape());
		shapes.put(edge, edge.getShape());
	}

	@Override
	public Point getCenter() {
		return GeomUtil.center(shape.getUpperLeft(), shape.getLowerRight());
	}

	@Override
	public Point getRelativeCenter() {
		return getCenter();
	}
	
	/**
	 * create an identical clone of the model and all wrappers
	 * @return the copy
	 */
	public DiagramWrapper duplicate(){
		try {
			// workaround since models do not need to contain an url but url is needed by JSONBuilder
			boolean stencilSetUrlNotSet = (getDiagram().getStencilset().getUrl() == null);
			if (stencilSetUrlNotSet){
				getDiagram().getStencilset().setUrl("");
			}
			Diagram duplicate = DiagramBuilder.parseJson(JSONBuilder.parseModel(getDiagram()));
			if (stencilSetUrlNotSet){
				duplicate.getStencilset().setUrl(null);
				getDiagram().getStencilset().setUrl(null);
			}
			return new DiagramWrapper(duplicate);
		} catch (Exception e) {
			throw new InvalidModelException("Could not duplicate model due consistency problems", e);
		}
	}
	
	/**
	 * remove the given wrapped shape from the diagram 
	 * @return true if the removal was successful
	 */
	public boolean removeFromDiagram(ShapeWrapper shape){
		boolean removed = false;
		if (shapes.containsKey(shape)){
			shapes.remove(shape);
			removed = true;
		}
		if (shape.isEdge() && edges.containsKey(shape.getResourceId())){
			edges.remove(shape.getResourceId());
			removed = true;
		}
		if (shape.isNode() && nodes.containsKey(shape.getResourceId())){
			nodes.remove(shape.getResourceId());
			removed = true;
		}
		return removed;
	}
}
