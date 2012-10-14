package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors;

import java.util.List;

import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.CorrectionConstants;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.rules.DiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.EdgeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.NodeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.ShapeWrapper;

/**
 * Attaches edges to their missing source or target respectively   
 * @author Christian Kieschnick
 */
public class DirectedEdgeAttacher extends AbstractCorrector {

	/**
	 * find all nodes which are in the direction of the start or end section (distinguished by reverse)
	 * and get the nearest one if it is connectable according to the rules
	 * @param reverse if true, look for a node connectable to the start point 
	 * @param edge the edge which misses start or end
	 * @return the probable candidate or null if non found
	 */
	private NodeWrapper getNearestNodeIfConnectableInDirectionOf(boolean reverse, EdgeWrapper edge){
		List<NodeWrapper> candidateNodes = edge.nodesInDirectionOf(
				edge.getDiagramWrapper().getNodeWrappers(), 
				reverse); 
		NodeWrapper candidate = edge.getNearestNodeOfWithMinimalDepth(
				candidateNodes, 
				reverse, 
				CorrectionConstants.MaximalEdgeNodeGapTolerance,
				0);
		DiagramRules rules = edge.getRules();
		if ((reverse && rules.canBeConnected(candidate, edge.getTarget(), edge))
				|| (!reverse && rules.canBeConnected(edge.getSource(), candidate, edge))){
			return candidate;
		}
		return null;
	}
	
	/**
	 * find an edge which can be either source or target (distinguished by reverse)
	 * @param reverse if true, look for an edge which can be the source, else look for an edge which can be the target
	 * @param edge the edge which misses source or target
	 * @return the probable edge or null if non was found
	 */
	private EdgeWrapper getNearestEdgeIfConnectableInDirectionOf(boolean reverse, EdgeWrapper edge){
		
		DiagramRules rules = edge.getRules();
		double minimalDistance = CorrectionConstants.MaximalEdgeNodeGapTolerance;
		EdgeWrapper candidate = null;
		for (EdgeWrapper otherEdge : edge.getDiagramWrapper().getEdgeWrappers()){
			if (otherEdge != edge 
					&& ((reverse && rules.canBeConnected(otherEdge, edge.getTarget(), edge))
							|| (!reverse && rules.canBeConnected(edge.getSource(), otherEdge, edge)))){
				if( edge.minimalDistanceToEdge(otherEdge, reverse) <= minimalDistance){
					candidate = otherEdge; 
					minimalDistance = edge.minimalDistanceToEdge(otherEdge, reverse);
				}
			}
		}
		return candidate;
	}
	
	/**
	 * fix the missing source
	 * @param edge the edge which may miss a source
	 * @return true if the edge has a source (now)
	 */
	private boolean fixSource(EdgeWrapper edge) {
		if (!edge.hasIncomings()){
			ShapeWrapper source = getNearestNodeIfConnectableInDirectionOf(true, edge);
			if (source == null){
				source = getNearestEdgeIfConnectableInDirectionOf(true, edge);
			}
			if (source != null){
				edge.setSource(source);	
				return true;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * fix the missing target
	 * @param edge the edge which may miss a target
	 * @return true if the edge has a source (now)
	 */
	private boolean fixTarget(EdgeWrapper edge) {
		if (!edge.hasOutgoings()){
			ShapeWrapper target = getNearestNodeIfConnectableInDirectionOf(false, edge);
			if (target == null){
				target = getNearestEdgeIfConnectableInDirectionOf(false, edge);
			}
			if (target != null){
				edge.setTarget(target);
				return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean fulfillsPrecondition(ShapeWrapper shape) {
		if (shape.isEdge()){
			EdgeWrapper edge = (EdgeWrapper)shape;
			return !edge.hasSource() || !edge.hasTarget();
		}
		return false;
	}

	@Override
	public boolean applyCorrectionImplementation(ShapeWrapper shape) {
		EdgeWrapper edge = (EdgeWrapper)shape;
		return fixSource(edge) | fixTarget(edge);
	}
}
