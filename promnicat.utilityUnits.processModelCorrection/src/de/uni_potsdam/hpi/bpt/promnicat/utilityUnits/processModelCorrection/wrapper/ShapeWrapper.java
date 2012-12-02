package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_potsdam.hpi.bpt.ai.diagram.Point;
import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.InvalidModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.DiagramRules;

/**
 * a small convenience wrapper around the shape class for simpler working with 
 * the {@link de.uni_potsdam.hpi.bpt.ai.diagram.Shape} class
 * @author Christian Kieschnick
 */
public abstract class ShapeWrapper {
	/**
	 * the diagram in which the shape is contained
	 */
	protected DiagramWrapper diagram; 
	/**
	 * the shape itself
	 */
	protected Shape shape;
	/**
	 * the depth within the diagram 
	 */
	private int depth;
	
	
	protected ShapeWrapper(DiagramWrapper diagram, Shape shape, int depth){
		this(shape, depth);
		this.diagram = diagram;
	}
	
	protected ShapeWrapper(Shape shape, int depth){
		this.shape = shape;
		this.depth = depth;
	}
	
	/**
	 * check is the wrapped stencil has the given resource id
	 */
	public boolean isResource(String resourceId){
		return getResourceId().contentEquals(resourceId);
	}
	
	/**
	 * get the resource id of the wrapped shape
	 */
	public String getResourceId(){
		return shape.getResourceId();
	}
	
	/**
	 * check if the wrapped stencil is of the given type
	 */
	public boolean isStencil(String stencilId){
		return getStencilId().contentEquals(stencilId);
	}
	
	/**
	 * @return the stencil id of the wrapped stencil
	 */
	public String getStencilId(){
		return shape.getStencilId();
	}
	
	/**
	 * get the rule set for the given model
	 */
	public DiagramRules getRules(){
		return diagram.getRules();
	}
	
	public DiagramWrapper getDiagramWrapper(){
		return diagram;
	}
	
	/**
	 * factory method to create the appropriate wrapper around the  {@link de.uni_potsdam.hpi.bpt.ai.diagram.Shape} instances
	 * @param diagram
	 * @param shape
	 * @param depth
	 * @param rules
	 * @return an instance of either Node for nodes or Edge for edges
	 * @throws InvalidModelException
	 */
	public static ShapeWrapper createShapeWrapper(DiagramWrapper diagram, Shape shape, int depth) throws InvalidModelException{
		DiagramRules rules = diagram.getRules();
		if (rules.isEdge(shape)){
			return new EdgeWrapper(diagram, shape, depth);
		} 
		if (rules.isNode(shape)){
			return new NodeWrapper(diagram, shape, depth);
		} 
		throw new InvalidModelException("Unknwon shape "+shape.getStencilId() + " ["+shape.getResourceId()+"]");
	}
	
	/**
	 * get the children of the wrapped shape
	 */
	public List<Shape> getChildShapes(){
		return shape.getChildShapes();
	}

	/**
	 * retrieve the wrapped shape
	 * @return
	 */
	public Shape getShape(){
		return shape;
	}
	
	/**
	 * returns a short representation of the wrapped shape
	 */
	@Override
	public String toString(){
		return shape.getStencilId()+" \""+shape.getProperty("name")+"\"["+shape.getResourceId()+"]";
	}
	
	/**
	 * @return the number of parent elements
	 */
	public int getDepth() {
		return this.depth;
	}
	
	/**
	 * @return true if the wrapped shape is an edge
	 */
	public boolean isEdge(){
		return false;
	}
	
	/**
	 * @return true if the wrapped shape is a node
	 */
	public boolean isNode(){
		return false;
	}
	
	/**
	 * @return true if the wrapped shape is a diagram
	 */
	public boolean isDiagram(){
		return false;
	}
	
	/**
	 * determine if the wrapped shape has at least one incoming shape
	 */
	public boolean hasIncomings(){
		return !shape.getIncomings().isEmpty();
	}

	/**
	 * retrieve all source shapes wrapped 
	 */
	public List<ShapeWrapper> getIncomings(){
		return diagram.getShapeWrappersFor(shape.getIncomings());
	}

	/**
	 * check if the wrapped shape has at least one outgoing shape
	 */
	public boolean hasOutgoings(){
		return !shape.getOutgoings().isEmpty();
	}
	
	/**
	 * retrieve all target shapes wrapped
	 */
	public List<ShapeWrapper> getOutgoings(){
		return diagram.getShapeWrappersFor(shape.getOutgoings());
	}
	
	/**
	 * remove the given wrapped shape from the outgoing shapes
	 */
	public void removeOutgoing(ShapeWrapper target){
		if (target != null&& shape.getOutgoings().contains(target.getShape())){
			shape.getOutgoings().remove(target.getShape());
		}
	}
	
	/**
	 * remove the given wrapped shape from the incoming shapes
	 */
	public void removeIncoming(ShapeWrapper source){
		if (source != null && shape.getIncomings().contains(source.getShape())){
			shape.getIncomings().remove(source.getShape());
		}
	}
	
	/**
	 * get the parent shape wrapped if one exists
	 */
	public NodeWrapper getParentShapeWrapper(){
		return (NodeWrapper) diagram.getShapeWrapperFor(shape.getParent());
	}
	
	/**
	 * get all ancestors in which the shape is embedded
	 */
	public Set<ShapeWrapper> getDirectAncestors(){
		de.uni_potsdam.hpi.bpt.ai.diagram.Shape current = shape.getParent();
		Set<ShapeWrapper> ancestors = new HashSet<ShapeWrapper>();
		while (current != null){
			ancestors.add(diagram.getShapeWrapperFor(current));
			current = current.getParent();
		}
		return ancestors;
	}
	
	/**
	 * add the given shape to the children of the wrapped shape
	 */
	public void addAsChild(ShapeWrapper child){
		child.getShape().setParent(shape);
		this.getShape().getChildShapes().add(child.getShape());
	}
	
	/**
	 * @return the center of the figure in global coordinates
	 */
	public abstract Point getCenter();
	
	/**
	 * @return the center of the figure in local coordinates
	 */
	public abstract Point getRelativeCenter();

	/**
	 * determine if at least one connection is needed for the shape
	 */
	public boolean needsAtLeastOneConnection() {
		return getRules().needsAtLeastOneControlFlowConnection(this);
	}
}
