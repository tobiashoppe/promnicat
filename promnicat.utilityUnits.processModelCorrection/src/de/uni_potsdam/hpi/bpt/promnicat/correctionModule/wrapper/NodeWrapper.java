package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper;

import java.util.ArrayList;
import java.util.List;

import math.geom2d.Point2D;
import math.geom2d.polygon.Rectangle2D;
import de.uni_potsdam.hpi.bpt.ai.diagram.Point;
import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.rules.BpmnDiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.rules.DiagramRules;

/**
 * a small convenience wrapper around the shape class for simpler working with 
 * the {@link de.uni_potsdam.hpi.bpt.ai.diagram.Shape} class - this is especially 
 * used for {@link de.uni_potsdam.hpi.bpt.ai.diagram.Node} but depends only on 
 *  {@link de.uni_potsdam.hpi.bpt.ai.diagram.Shape}
 * @author Christian Kieschnick
 */
public class NodeWrapper  extends ShapeWrapper {

	protected NodeWrapper(DiagramWrapper diagram, Shape shape, int depth) {
		super(diagram, shape, depth);
	}
	
	protected NodeWrapper(Shape shape, int depth){
		super(shape, depth);
	}
	
	/**
	 * get all children recursive
	 * @param shape
	 * @return
	 */
	public static List<Shape> getChildShapesRecursive(Shape shape){
		List<Shape> childShapes = new ArrayList<Shape>();
		for (Shape childShape : shape.getChildShapes()) {
			childShapes.add(childShape);
			childShapes.addAll(getChildShapesRecursive(childShape));
		}
		return childShapes;
	}
	
	/**
	 * @return the bounds in global coordinates
	 */
	public Rectangle2D getBounds2D(){
		Point2D upperLeft = getUpperLeft();
		Point2D lowerRight = getLowerRight();
		
		return GeomUtil.toRectangle2D(upperLeft, lowerRight);
	}
	
	@Override
	public Point getCenter(){
		return GeomUtil.to(getBounds2D().getCenter()); 
	}

	@Override
	public Point getRelativeCenter(){
		Point upperLeft = getShape().getUpperLeft();
		Point lowerRight = getShape().getLowerRight();
		
		return GeomUtil.to(GeomUtil.toRectangle2D(new Point(0d,0d), GeomUtil.sub(lowerRight, upperLeft)).getCenter());
	}

	/**
	 * transform a local point to a global coordinate
	 * @param point
	 * @return
	 */
	private Point2D toGlobalCoordinate(Point2D point){
		de.uni_potsdam.hpi.bpt.ai.diagram.Shape parent = getShape().getParent();
		while (parent != null)
		{
			point = GeomUtil.add(point, parent.getUpperLeft());
			parent = parent.getParent();
		}
		return point; 
	}
	
	/**
	 * @return the upper left point as global coordinate
	 */
	protected Point2D getUpperLeft(){
		return toGlobalCoordinate(GeomUtil.to(getShape().getUpperLeft())); 
	}
	
	/**
	 * @return the lower right point as global coordinate
	 */
	protected Point2D getLowerRight(){
		return toGlobalCoordinate(GeomUtil.to(getShape().getLowerRight())); 
	}

	@Override
	public boolean isNode() {
		return true;
	}
	
	/**
	 * determine if an incoming control flow is needed
	 */
	public boolean needsIncomingControlFlow(){
		return getRules().needsIncomingControlFlow(this);
	}
	
	/**
	 * determine if an outgoing control flow is needed
	 */
	public boolean needsOutgoingControlFlow(){
		return getRules().needsOutgoingControlFlow(this);
	}

	/**
	 * @return if the node can be part of the control flow
	 */
	public boolean isDirectingControlNode(){
		return getRules().isControlFlowNode(this);
	}
	
	/**
	 * determine if the node can act as attached control flow node
	 */
	public boolean isAttachedControlFlowNode(){
		DiagramRules rules = getRules();
		if (rules instanceof BpmnDiagramRules){
			return ((BpmnDiagramRules)rules).isAttachedControlFlowNode(this);
		}
		return false;
	}

	/**
	 * determine if control flow edges are pointing to this shape
	 * or this shape is attached to another control flow node 
	 */
	public boolean hasIncomingControlFlow(){
		for (EdgeWrapper edge : getIncomingEdges()){
			if (edge.isDirectingControlFlow())
				return true;
		}
		if (isAttachedControlFlowNode()){
			for (ShapeWrapper shape : getIncomings()){
				if (!shape.isEdge() && ((NodeWrapper)shape).isDirectingControlNode()){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * determne if control flow edges are starting from this shape
	 * or this shape has an attached control flow node
	 * @return
	 */
	public boolean hasOutgoingControlFlow(){
		for (ShapeWrapper shape: getOutgoings()){
			if (shape.isEdge()){
				if (((EdgeWrapper)shape).isDirectingControlFlow()){
					return true;
				}
			} else {
				// this case occurres on BPMN attached events
				if (((NodeWrapper)shape).isAttachedControlFlowNode()){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * determine if this node can contain the given shape
	 */
	public boolean canContain(Shape shape){
		return getRules().canContain(this.getShape(), shape);
	}
	
	/**
	 * determine if the given shape is embedded in the wrapped shape
	 */
	public boolean contains(Shape node){
		return this.getBounds2D().contains(GeomUtil.to(node.getUpperLeft()))
					&& this.getBounds2D().contains(GeomUtil.to(node.getLowerRight()));
	}

	/**
	 * get all outgoing edges wrapped
	 */
	public List<EdgeWrapper> getOutgoingEdges(){
		ArrayList<EdgeWrapper> edges = new ArrayList<EdgeWrapper>();
		for (ShapeWrapper shape : getOutgoings()){
			if (shape.isEdge())
				edges.add((EdgeWrapper)shape);
		}
		return edges;
	}
	
	/**
	 * get all incoming edges wrapped
	 * @return
	 */
	public List<EdgeWrapper> getIncomingEdges(){
		ArrayList<EdgeWrapper> edges = new ArrayList<EdgeWrapper>();
		for (ShapeWrapper shape : getIncomings()){
			if (shape.isEdge())
				edges.add((EdgeWrapper)shape);
		}
		return edges;
	}
	
	/**
	 * remove a child from the children
	 */
	public boolean removeChild(ShapeWrapper child){
		child.getShape().setParent(null);
		return getShape().getChildShapes().remove(child.getShape());
	}
}
