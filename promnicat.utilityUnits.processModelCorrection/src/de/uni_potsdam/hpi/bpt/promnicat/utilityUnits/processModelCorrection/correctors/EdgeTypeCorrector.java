package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.correctors;

import java.util.List;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.CorrectionConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.DiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.EdgeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.ShapeWrapper;

public class EdgeTypeCorrector extends AbstractCorrector {
	
	@SuppressWarnings("unused")
	private ShapeWrapper getSourceCandiate(EdgeWrapper edge){
		if (edge.hasSource()){
			return edge.getSource();
		} 
		int minimalDepth = 0;
		if (edge.hasTarget()){
			minimalDepth = edge.getTarget().getDepth();
		}
		return edge.getNearestNodeInDirectionOfWithMinimalDepth(true, CorrectionConstants.MaximalDistanceForWrongEdgeTypeGapToNode, minimalDepth);
	}
	
	@SuppressWarnings("unused")
	private ShapeWrapper getTargetCandiate(EdgeWrapper edge){
		if (edge.hasTarget()){
			return edge.getTarget();
		} 
		int minimalDepth = 0;
		if (edge.hasSource()){
			minimalDepth = edge.getSource().getDepth();
		}
		return edge.getNearestNodeInDirectionOfWithMinimalDepth(false, CorrectionConstants.MaximalDistanceForWrongEdgeTypeGapToNode, minimalDepth);
	}
	
	/**
	 * change the stencil type of the given edge to a type connecting source and target 
	 * @param edge the edge to be transformed
	 * @param source the source
	 * @param target the target
	 * @return true if the transformation was successful, else false
	 */
	private boolean transformToAppropriateEdge(EdgeWrapper edge, ShapeWrapper source, ShapeWrapper target){
		// at this point it would be nice to use a weight for the different edge type
		// it should consider the frequency of occurrences of the different edge types 
		DiagramRules rules = edge.getRules();
		List<String> possibleEdgeIds = rules.getSupportedConnectionFor(source, target);
		for (String edgeId : possibleEdgeIds){
			if (rules.connectionCanBeAppliedTo(source, target, edgeId)){
				edge.transformTo(edgeId);
				edge.setSource(source);
				edge.setTarget(target);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * get a candidate node for source or target (distinguished by reversed)
	 * @param edge the edge to be inspected
	 * @param reversed to look for a source: true, false for looking for a target 
	 * @return the candidate
	 */
	private ShapeWrapper getCandidateNode(EdgeWrapper edge, ShapeWrapper correspondingShape, boolean reversed){
		int minimalDepth = 0;
//		if (reversed) {
//			if (edge.hasTargetNode()){
//				minimalDepth = edge.getTarget().getDepth();
//			}
//		} else {
//			if (edge.hasSourceNode()){
//				minimalDepth = edge.getSource().getDepth();
//			}
//		}
		if (correspondingShape != null){
			minimalDepth = correspondingShape.getDepth();
		}
		ShapeWrapper candidate = edge.getNearestNodeInDirectionOfWithMinimalDepth(
				reversed, 
				CorrectionConstants.MaximalDistanceForWrongEdgeTypeGapToNode, 
				minimalDepth); 
		if (candidate != null && candidate.getDepth() >= minimalDepth){
			return candidate;
		}
		return null;
	}
	
	/**
	 * find the nearest edge which is in the direction of start or end 
	 * @param reverse true for looking in direction of the start section, false for looking in direction of the end section
	 * @param edge the edge for which the lookup should be done
	 * @return the potential edge or null if non was found
	 */
	private EdgeWrapper getNearestEdgeInDirectionOf(boolean reverse, EdgeWrapper edge){
		
		double minimalDistance = CorrectionConstants.MaximalDistanceForWrongEdgeTypeGapToEdge ;
		EdgeWrapper candidate = null;
		for (EdgeWrapper otherEdge : edge.getDiagramWrapper().getEdgeWrappers()){
			if(otherEdge != edge && edge.minimalDistanceToEdge(otherEdge, reverse) <= minimalDistance){
				candidate = otherEdge; 
				minimalDistance = edge.minimalDistanceToEdge(otherEdge, reverse);
			}
		}
		return candidate;
	}
	
	/**
	 * find the potential edge to which edge should associated
	 * @param edge the edge which should be attached
	 * @param origin the original source or target of edge
	 * @param reversed true to look up in the direction the start of edge, false to look up in direction of the end of edge
	 * @return the candidate edge or null when non was found
	 */
	private ShapeWrapper getCandidateEdge(EdgeWrapper edge, boolean reversed){
		ShapeWrapper candidate = getNearestEdgeInDirectionOf(
				reversed, 
				edge);
		if (candidate != null && !candidate.isStencil(edge.getStencilId())){
			return candidate;
		}
		return null;
	}
	
	/**
	 * select the best matching source or target (depending on reversed) for edge
	 * prefers node to edges since it is more common
	 * @param edge the edge to determine source/target for
	 * @param origin the original source/target
	 * @param reversed true for lookup a source, false for lookup a target
	 * @return the best matching candidate or null if non found
	 */
	private ShapeWrapper getBestMatchingElement(EdgeWrapper edge, ShapeWrapper origin, ShapeWrapper correspondingNode, boolean reversed){
		ShapeWrapper candidateNode = getCandidateNode(edge, correspondingNode, reversed);
		ShapeWrapper candidateEdge = getCandidateEdge(edge, reversed);
		
		if (candidateEdge == null && candidateNode == null){
			return origin;
		}
		if (candidateNode == null){
			return candidateEdge;
		}
		return candidateNode;
	}
	
	@Override
	public boolean fulfillsPrecondition(ShapeWrapper shape) {
		if (shape.isEdge()){
			EdgeWrapper edge = (EdgeWrapper)shape;
			ShapeWrapper source = edge.getSource();
			ShapeWrapper target = edge.getTarget();
			return target == null 
					|| source == null
					||!shape.getRules().canBeConnected(source, target, edge);
		}
		return false;
	}

	@Override
	public boolean applyCorrectionImplementation(ShapeWrapper shape) {
		EdgeWrapper edge = (EdgeWrapper)shape;
		ShapeWrapper source = edge.getSource();
		ShapeWrapper target = edge.getTarget();
		if (source == null || target == null || !edge.getRules().canBeConnected(source, target, edge)){
			if (source == null || target == null || source.getDepth() != target.getDepth()){
				source = getBestMatchingElement(edge, source, target, true);
				target = getBestMatchingElement(edge, target, source, false);
			}
			if (source != null && target != null){
				return transformToAppropriateEdge(edge, source, target);
			}
		}
		return false;
	}
}
