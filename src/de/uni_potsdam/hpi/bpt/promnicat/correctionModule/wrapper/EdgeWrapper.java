package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import math.geom2d.Point2D;
import math.geom2d.line.Line2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.polygon.Polyline2D;
import de.uni_potsdam.hpi.bpt.ai.diagram.Point;
import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.InvalidModelException;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.rules.DiagramRules;

/**
 * a small convenience wrapper around the shape class for simpler working with 
 * the {@link de.uni_potsdam.hpi.bpt.ai.diagram.Shape} class - this is especially 
 * used for {@link de.uni_potsdam.hpi.bpt.ai.diagram.Edge} but depends only on 
 *  {@link de.uni_potsdam.hpi.bpt.ai.diagram.Shape}
 * @author Christian Kieschnick
 */
public class EdgeWrapper extends ShapeWrapper {

	protected EdgeWrapper(DiagramWrapper diagram, Shape shape, int depth) {
		super(diagram, shape, depth);
	}

	/**
	 * determine if a source exists and it is a node
	 */
	public boolean hasSourceNode(){
		return hasSource() && getSource().isNode();
	}
	
	/**
	 * determine if a source exists
	 * @return
	 */
	public boolean hasSource(){
		return getSource() != null;
	}
	
	/**
	 * get the source wrapped (usually the first shape of the incoming collection)
	 */
	public ShapeWrapper getSource(){
		if (shape.getIncomings().size() > 0){
			return diagram.getShapeWrapperFor(shape.getIncomings().get(0));
		}
		return null;
	}

	/**
	 * determine if a target exists and it is a node
	 */
	public boolean hasTargetNode(){
		return hasTarget() && getTarget().isNode();
	}
	
	/**
	 * determine if a target exists
	 */
	public boolean hasTarget() {
		return getTarget() != null;
	}
	
	/**
	 * get the target
	 */
	public ShapeWrapper getTarget(){
		if (shape.getTarget() != null){
			return diagram.getShapeWrapperFor(shape.getTarget());
		}
		return null;
	}
	
	/**
	 * transforms the original dockers which can be given in local coordinates 
	 * of the source or target to global coordinates
	 * @return
	 * @throws InvalidModelException 
	 */
	private List<Point2D> getDockers() throws InvalidModelException {
		ArrayList<Point2D> dockers = new ArrayList<Point2D>();
		int startIndex = 0;
		int endIndex = shape.getDockers().size();
		// the first and the last docker may be relative to the connected node
		if (hasSourceNode()){
			Point2D start = ((NodeWrapper)getSource()).getUpperLeft();
			dockers.add(start.plus(GeomUtil.to(shape.getDockers().get(0))));
			startIndex++;
		}
		if (hasTargetNode()){
			Point2D start = ((NodeWrapper)getTarget()).getUpperLeft();
			dockers.add(start.plus(GeomUtil.to(shape.getDockers().get(shape.getDockers().size() - 1))));
			endIndex--;
		}
		for (; startIndex < endIndex; startIndex++){
			dockers.add(startIndex, GeomUtil.to(shape.getDockers().get(startIndex)));
		}
		return dockers;
	}
	
	/**
	 * get the starting point in global coordinates
	 */
	public Point2D getStartPoint(){
		return getDockers().get(0);
	}
	
	/**
	 * get the ending point in global coordinates
	 */
	public Point2D getEndPoint(){
		return getDockers().get(getDockers().size() - 1);
	}
	
	/**
	 * @return the segment pointing to the source
	 */
	private Line2D getSegmentToSource(){
		List<Point2D> dockers = getDockers();
		return new Line2D(dockers.get(1), dockers.get(0));
	}
	
	/**
	 * @return the segment to the target
	 */
	private Line2D getSegmentToTarget(){
		List<Point2D> dockers = getDockers();
		return new Line2D(dockers.get(dockers.size() - 2), dockers.get(dockers.size() - 1));
	}
		
	/**
	 * get the distance between the given node and the start/end point of the edge
	 * @param toStartpoint if true use the start point, else use the end point
	 * @return the distance
	 */
	private double distanceToPoint(NodeWrapper node, boolean toStartpoint){
		if (node == null){
			return Double.NaN;
		}
		Line2D line = getSegmentToTarget();
		if (toStartpoint){
			line = getSegmentToSource();
		}
		return node.getBounds2D().getDistance(line.getLastPoint());
	}
	
	/**
	 * get the distance from the node to the start point
	 */
	public double distanceToStartPoint(NodeWrapper node) {
		return distanceToPoint(node, true);
	}

	/**
	 * get the distance from the node to the end point
	 */
	public double distanceToEndPoint(NodeWrapper node) {
		return distanceToPoint(node, false);
	}
	
	/**
	 * determine if the edges lies completely inside the given node
	 */
	public boolean isSurroundedBy(NodeWrapper node) {
		List<Point2D> dockers = getDockers();
		return node.getBounds2D().contains(dockers.get(0))
				&& node.getBounds2D().contains(dockers.get(dockers.size() - 1));
	}
	
	/**
	 * set the target of the edge but add a new docker to the dockers instead of replaceing the last
	 */
	public void setTargetBundledAware(ShapeWrapper target){
		if (target != null){
			setTargetImplementation(target);
		}
	}
	
	/**
	 * set the target of the shape and insert this at the first position of the outgoings
	 */
	private void setTargetImplementation(ShapeWrapper target){
		shape.getOutgoings().remove(target.getShape());
		shape.getOutgoings().add(0, target.getShape());
		shape.setTarget(target.getShape());
		shape.getDockers().add(target.getRelativeCenter());
	}

	/**
	 * set the target and update the last docker
	 */
	public void setTarget(ShapeWrapper target){
		if (target != null){
			shape.getDockers().remove(shape.getDockers().size() - 1);
			setTargetImplementation(target);
		}
	}

	@Override
	public void removeOutgoing(ShapeWrapper target){
		if (target != null){
			super.removeOutgoing(target);
			target.getShape().getIncomings().remove(shape);
			shape.getDockers().remove(shape.getDockers().size() - 1);
			shape.getDockers().add(target.getCenter());	
			if (shape.getTarget() == target.getShape()){
				shape.setTarget(null);
			}
		}
	}
	
	/**
	 * set the source but do not replace the first docker, add a new one instead
	 */
	public void setSourceBundledAware(ShapeWrapper source){
		if (source != null){
			shape.getIncomings().remove(source.getShape());
			setSourceImplementation(source);
			
		}
	}
	
	/**
	 * set the source of the edge to the given shape
	 */
	public void setSource(ShapeWrapper source){
		if (source != null){
			shape.getDockers().remove(0);
			shape.getIncomings().clear();
			setSourceImplementation(source);
		}
	}

	/**
	 * set the source and update the dockers
	 */
	private void setSourceImplementation(ShapeWrapper source){
		shape.addIncoming(source.getShape());
		source.getShape().addOutgoing(shape);
		shape.getDockers().add(0, source.getRelativeCenter());
	}
	
	@Override
	public void removeIncoming(ShapeWrapper source){
		if (source != null){
			super.removeIncoming(source);
			source.getShape().getOutgoings().remove(shape);
			shape.getDockers().remove(0);
			shape.getDockers().add(0, source.getCenter());
		}
	}

	/**
	 * @return true if the edge is no source or target
	 */
	public boolean hasMissingConnections(){
		return !hasIncomings() || !hasOutgoings();
	}
	
	/**
	 * @return true if the edge does not need to distiguish between source and target
	 */
	public boolean isUndirected(){
		return getRules().isUndirectedEdge(this);
	}
	
	/**
	 * find all nodes which are in the direction of the edge
	 * @param nodes 
	 * @param reverse if true than take the start segment for testing node intersection, else use the last segment
	 * @return
	 */
	public List<NodeWrapper> nodesInDirectionOf(Collection<NodeWrapper> nodes, boolean reverse){
		ArrayList<NodeWrapper> intersectingNodes = new ArrayList<NodeWrapper>();
		for (NodeWrapper node : nodes){
			if ((reverse && comesFrom(node))
					|| (!reverse && goesTo(node))){
				intersectingNodes.add(node);
			}
		}
		return intersectingNodes;
	}
	
	// ----+
	//     |
	//     +----- would return 3 line segments
	/**
	 * get a list of all line segments </br>
	 */
	private List<Line2D> getSegments(){
		List<Point2D> dockers = getDockers();
		ArrayList<Line2D> segments = new ArrayList<Line2D>();
		for (int i = 0; i < dockers.size() - 1; i++){
			if (!GeomUtil.areEqual(dockers.get(i), dockers.get(i + 1))) {
				segments.add(Line2D.create(dockers.get(i), dockers.get(i+1)));
			}
		}
		return segments;
	}
		
	/**
	 * determine if the start/end is on the given edge
	 * @param edge the edge which is possible under my start/end point
	 * @param reverse true to use the start point, false for use the endpoint
	 * @param tolerance the tolerance of the distance between the reference point and the edge
	 * @return true if the distance to the edge is within the tolerance
	 */
	public boolean stopsOn(EdgeWrapper edge, boolean reverse, double tolerance){
		return minimalDistanceToEdge(edge, reverse) <= tolerance;
	}
	
	/**
	 * calculate the minimal distance to a given edge
	 * @param edge the edge to calculate the distance to
	 * @param reverse true to use the start point, false to use the end point as reference
	 * @return the distance
	 */
	public double minimalDistanceToEdge(EdgeWrapper edge, boolean reverse){
		Line2D matchingSegment = reverse ? getSegmentToSource() : getSegmentToTarget();
		double minimalDistance = Double.MAX_VALUE;
		for (Line2D edgeSegment : edge.getSegments()){
			minimalDistance = Math.min(minimalDistance, edgeSegment.getDistance(matchingSegment.getLastPoint()));
		}
		return minimalDistance;
	}
	
	/**
	 * check if the node is in the direction of the last segment
	 * @param node the node to check for an intersection 
	 * @return true if the node is intersected by the last segment
	 */
	public boolean goesTo(NodeWrapper node){
		return pointsTo(node, getSegmentToTarget());
	}
	
	/**
	 * check if the node is in the (reversed) direction of the first segment
	 * @param node the node to check for an intersection
	 * @return true if the node is intersected by the first segement
	 */
	public boolean comesFrom(NodeWrapper node){
		return pointsTo(node, getSegmentToSource());
	}
	
	/**
	 * check if the node is intersected by the given line
	 * @param node the node which should be checked for intersection
	 * @param line the line which extension may intersect the node
	 * @return true if the extended line and node are intersecting
	 */
	private static boolean pointsTo(NodeWrapper node, Line2D line){
		// Alternativer Ansatz - Ellipse2D für eine fuzzy detection 
		StraightLine2D infiniteLine = new StraightLine2D(line.getFirstPoint(), line.getLastPoint());
		if (node.getBounds2D().getDistance(line.getFirstPoint()) >= node.getBounds2D().getDistance(line.getLastPoint())){
			for (LineSegment2D edge : node.getBounds2D().getEdges()){
				if (infiniteLine.getIntersection(edge) != null){
					return true;
				}
			}	
		}
		return false;
	}

	/**
	 * select the nodes which is nearest within a maximal distance
	 * and return the one which has at least a the given minimal depth 
	 * @param reverse true to use the start point as reference, false to use the endpoint as reference
	 * @param maximalDistance the maximal distance to check for the nodes
	 * @param depth the minimal depth of the nodes
	 * @return the node fulfilling the criteria
	 */
	public NodeWrapper getNearestNodeInDirectionOfWithMinimalDepth(boolean reverse, double maximalDistance, int depth){
		return getNearestNodeOfWithMinimalDepth(
				nodesInDirectionOf(diagram.getNodeWrappers(), reverse), 
				reverse, 
				maximalDistance, 
				depth);
	}	


	
	/**
	 * find all connectable nodes which are in the direction of the edge
	 * @param nodes 
	 * @param reverse if true than take the start segment for testing node intersection, else use the last segment
	 * @return
	 */
	public List<NodeWrapper> connectableNodesInDirectionOf(Collection<NodeWrapper> nodes, boolean reverse){
		nodes = nodesInDirectionOf(nodes, reverse);
		ArrayList<NodeWrapper> intersectingNodes = new ArrayList<NodeWrapper>();
		DiagramRules rules = diagram.getRules();
		for (NodeWrapper node : nodes){
			if ((reverse && rules.canBeConnected(node, getTarget(), this))
					|| (!reverse && rules.canBeConnected(getSource(), node, this))){
				intersectingNodes.add(node);
			}
		}
		return intersectingNodes;
	}
	
	/**
	 * filter the nearest node to the source/target out of nodes which has a certain depth 
	 * @param nodes the nodes to filter
	 * @param reverse use start(true)/end(false) as reference
	 * @param maximalDistance the maximal distance in which the nodes should be
	 * @param depth the minimal depth
	 * @return the node which fulfills the criteria
	 */
	public NodeWrapper getNearestNodeOfWithMinimalDepth(Collection<NodeWrapper> nodes, boolean reverse, double maximalDistance, int depth){
		List<NodeWrapper> sortedNodes = new ArrayList<NodeWrapper>(nodes);
		Collections.sort(sortedNodes, new Comparator<NodeWrapper>(){

			@Override
			public int compare(NodeWrapper o1, NodeWrapper o2) {
				// sort elements (like pools and lanes) with low depth to the end
				return o2.getDepth() - o1.getDepth();
			}
			
		});
		NodeWrapper candidate = null;
		double nearestDistance = maximalDistance;
		for (NodeWrapper node : sortedNodes){
			double distance = 
					reverse ? distanceToStartPoint(node)
							: distanceToEndPoint(node);
			boolean hasNeededDepth = node.getDepth() >= depth;
			if (candidate != null){
				hasNeededDepth &= candidate.getDepth() <= node.getDepth() && candidate.getDepth() >= depth;
			}
			if (distance < nearestDistance 
					&& hasNeededDepth
					&& !isSurroundedBy(node)){
				nearestDistance = distance;
				candidate = node;
			}
		}
		return candidate;
	}

	@Override
	public boolean isEdge() {
		return true;
	}

	/**
	 * changes the wrapped edge to the specified stencil type
	 * @param stencilType
	 */
	public void transformTo(String stencilType) {
		StencilFactory factory = StencilFactory
			.createFor(stencilType, diagram.getDiagram())
			.withResourceId(this.shape.getResourceId())
			.withDockers(this.shape.getDockers())
			.withProperties(this.shape.getProperties())
			.withParent(this.shape.getParent())
			.withIncomings(this.shape.getIncomings())
			.withOutgoings(this.shape.getOutgoings());
		this.shape = factory.getEdge();
		this.diagram.updateForChangedEdge(this);
	}
	
	/**
	 * determine if control flow is directed
	 */
	public boolean isDirectingControlFlow(){
		return getRules().isControlFlowEdge(this);
	}
	
	/**
	 * @return true if one of the incoming nodes is directing control flow
	 */
	public boolean hasIncomingControlNode(){
		for (ShapeWrapper incoming : getIncomings()){
			if (incoming.isNode() && ((NodeWrapper)incoming).isDirectingControlNode())
				return true;
		}
		return false;
	}
	
	/**
	 * @return true if one of the outgoing nodes is directing control flow
	 */
	public boolean hasOutgoingControlNode(){
		for (ShapeWrapper outgoing : getOutgoings()){
			if (outgoing.isNode() && ((NodeWrapper)outgoing).isDirectingControlNode())
				return true;
		}
		return false;
	}

	@Override
	public Point getCenter() {
		Polyline2D line = new Polyline2D();
		List<Point2D> dockers = getDockers();
		for (Point2D docker : dockers){
			line.addVertex(docker);
		}
		Line2D straight = new Line2D(dockers.get(0), dockers.get(dockers.size() - 1));
		Collection<Point2D> intersections = line.getIntersections(straight);
		Point2D center = GeomUtil.center(dockers.get(0), dockers.get(dockers.size() - 1));
		if (intersections.size() != 0){
			center = (Point2D)intersections.toArray()[intersections.size() / 2];
		}
		return GeomUtil.to(center);
	}

	@Override
	public Point getRelativeCenter() {
		return getCenter();
	}
	
	/**
	 * check if this and the other edge are visually sharing a common segment 
	 * @return true if at least one segment is shared
	 */
	public boolean isOverlappingWith(EdgeWrapper other){
		Line2D sourceSegment = getSegmentToSource();
		Line2D targetSegment = getSegmentToTarget();
		Line2D otherSourceSegment = other.getSegmentToSource();
		Line2D otherTargetSegment = other.getSegmentToTarget();
		return (otherSourceSegment.isParallel(sourceSegment) &&
					otherSourceSegment.getLastPoint().getDistance(sourceSegment.getLastPoint())  < 1)
				|| (otherTargetSegment.isParallel(targetSegment) &&
						otherTargetSegment.getLastPoint().getDistance(targetSegment.getLastPoint())  < 1)
				|| (otherSourceSegment.isParallel(targetSegment) &&
						otherSourceSegment.getLastPoint().getDistance(targetSegment.getLastPoint())  < 1)
				|| (otherTargetSegment.isParallel(sourceSegment) &&
						otherTargetSegment.getLastPoint().getDistance(sourceSegment.getLastPoint())  < 1);
		
	}
	
}
