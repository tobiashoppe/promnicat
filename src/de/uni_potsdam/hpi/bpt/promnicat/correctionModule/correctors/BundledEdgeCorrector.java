package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.CorrectionConstants;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.DiagramWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.EdgeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.NodeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.ShapeWrapper;

/**
 * Correction of edges which simulate an edge bundle but are only visually attached to 
 * an reference edge
 * @author Christian Kieschnick
 */
public class BundledEdgeCorrector extends AbstractCorrector {

	@Override
	public boolean fulfillsPrecondition(ShapeWrapper shape){
		if (shape.isEdge()){
			EdgeWrapper edge = (EdgeWrapper)shape;
			if (!edge.hasSource() || !edge.hasTarget()){
				boolean reversed = edge.hasTarget();
				List<EdgeWrapper> edges = getDirectlyAssociatedBundledEdgesFor(edge, reversed);
				return edges.size() > 0;
			}
		}
		return false;
	}	
	
	@Override
	public boolean applyCorrectionImplementation(ShapeWrapper shape){
		EdgeWrapper edge = (EdgeWrapper)shape;
		if (!edge.hasSource()){
			return fixIncomingEdges(edge);
		}
		if (!edge.hasTarget()){
			return fixOutgoingEdges(edge);
		}
		return false;
	}
	
	/**
	 * filters all edges which are visually connected to the given edge at the start or end point
	 * @param reversedDirection if true then use the start point else use the end point as reference 
	 */
	private List<EdgeWrapper> getDirectlyAssociatedBundledEdgesFor(EdgeWrapper edge, boolean reversedDirection){
		DiagramWrapper model = edge.getDiagramWrapper();
		ArrayList<EdgeWrapper> bundledEdges = new ArrayList<EdgeWrapper>();
		for (EdgeWrapper candidateEdge : model.getEdgeWrappers()){
			if (edge != candidateEdge
				&& edge.isStencil(candidateEdge.getStencilId())
				&& edge.stopsOn(candidateEdge, reversedDirection, CorrectionConstants.MaximalBundlingTolerance)){
				bundledEdges.add(candidateEdge);
			}
		}
		return bundledEdges;
	}
	
	/**
	 * collect source and target of the given edge
	 * make sure, that sources and targets are distinguished for undirected edges
	 * @param bundledEdge
	 * @param sources
	 * @param targets
	 */
	private void collectOrigins(EdgeWrapper bundledEdge, HashSet<NodeWrapper> sources, HashSet<NodeWrapper> targets){

		ShapeWrapper sourceShape = bundledEdge.getSource();
		ShapeWrapper targetShape = bundledEdge.getTarget();
		NodeWrapper source = null;
		NodeWrapper target = null;
		if (sourceShape != null && sourceShape.isNode()){
			source = (NodeWrapper)sourceShape;
		}
		if (targetShape != null && targetShape.isNode()){
			target = (NodeWrapper)targetShape;
		}
		if (bundledEdge.isUndirected()){
			if (!sources.contains(source)){
				if (!targets.contains(source)){
					sources.add(source);
				} 
				// else the source is already in the target nodes - the edge may be inverted
			}
			if (!targets.contains(target)){
				if (!sources.contains(target)){
					targets.add(target);
				}
				// as mentioned before - the target is already in the source nodes - the edge may be inverted
			}
		} else {
			sources.add(source);
			targets.add(source);
		}
	}
		
	/** 
	 * get all edges of candidate edges which are overlapping with the bundled edges
	 * the candidate edges have to have the given type
	 * @param candidateEdges the possible overlapping edges
	 * @param bundledEdges the already bundled edges
	 * @param type the type filter for the candidate edges
	 * @return the overlapping edges
	 */
	private HashSet<EdgeWrapper> overlappingEdgesOf(List<EdgeWrapper> candidateEdges, List<EdgeWrapper> bundledEdges, String type){
		HashSet<EdgeWrapper> overlappingEdges = new HashSet<EdgeWrapper>();
		for (EdgeWrapper outgoing : candidateEdges){
			if (outgoing.getStencilId().equalsIgnoreCase(type)){
				for (EdgeWrapper bundledEdge : bundledEdges){
					if (outgoing.isOverlappingWith(bundledEdge)){
						overlappingEdges.add(outgoing);
					}
				}
			}
		}
		return overlappingEdges;
	}
	
	//                indirect   dangling    direct bundled edge
	//  +------+          |         |          |
	//  | Task |----------+--------------------+
	//  +------+
	//     | 
	// some other outgoing/incoming edge
	/**
	 * determine the source or target by using information how may edges are 
	 * parallel to the bundled edges
	 * @param edge the edge which should be part of a bundle
	 * @param reversedDirection if true, look for the reference edge at the start, else look for the reference edge at the end of the given edge 
	 * @return the probable source/target
	 */
	private NodeWrapper getCandidateNodeUsingBigBundles(EdgeWrapper edge, boolean reversedDirection){
		List<EdgeWrapper> bundledEdges = getDirectlyAssociatedBundledEdgesFor(edge, reversedDirection);
		HashSet<NodeWrapper> sources = new HashSet<NodeWrapper>();
		HashSet<NodeWrapper> targets = new HashSet<NodeWrapper>();
		for (EdgeWrapper bundledEdge : bundledEdges){
			collectOrigins(bundledEdge, sources, targets);
		}
		sources.remove(null);
		targets.remove(null);
		
		
		// first look for associated bundled edges
		// second find common node of associated bundled edges
		// third incoming/outgoing nodes overlapping with bundled edges (collecting indirect edges)

		if (!edge.isUndirected()){
			if (reversedDirection){
				if (sources.size() != 1){
					return null;
				}
				return (NodeWrapper)sources.toArray()[0];
			} 
			if (targets.size() != 1){
				return null;
			}
			return (NodeWrapper)targets.toArray()[0];
		}
		
		// from here we only consider undirected edges
		
		if (sources.size() != 1 && targets.size() != 1){
			return null;
		}
		
		HashMap<NodeWrapper, HashSet<EdgeWrapper>> candidateConnections = new HashMap<NodeWrapper, HashSet<EdgeWrapper>>();
		for (NodeWrapper source : sources){
			if (!candidateConnections.containsKey(source)){
				candidateConnections.put(source, new HashSet<EdgeWrapper>());
			}
			candidateConnections.get(source).addAll(overlappingEdgesOf(source.getIncomingEdges(), bundledEdges, edge.getStencilId()));
			candidateConnections.get(source).addAll(overlappingEdgesOf(source.getOutgoingEdges(), bundledEdges, edge.getStencilId()));
		}
		for (NodeWrapper source : targets){
			if (!candidateConnections.containsKey(source)){
				candidateConnections.put(source, new HashSet<EdgeWrapper>());
			}
			candidateConnections.get(source).addAll(overlappingEdgesOf(source.getIncomingEdges(), bundledEdges, edge.getStencilId()));
			candidateConnections.get(source).addAll(overlappingEdgesOf(source.getOutgoingEdges(), bundledEdges, edge.getStencilId()));
		}
		
		// if both sets do contain several edges we are currently doomed - there should be a ranking for connecting edges
		// for example: the most common nodes types which are associated by an edge globally or in the model and use the node
		// as source or target, which is appropriate
		int candidateParallelConnections = 0;
		NodeWrapper candidate = null;
		for (NodeWrapper candidateNode : candidateConnections.keySet()){
			if (candidateConnections.get(candidateNode).size() > candidateParallelConnections){
				candidateParallelConnections = candidateConnections.get(candidateNode).size();
				candidate = candidateNode;
			}
		}
		return candidate;
	}
	
	/**
	 * fix the missing source of an unconnected bundled edge
	 * @param edge the edge without a source
	 * @return true if successful
	 */
	private boolean fixIncomingEdges(EdgeWrapper edge) {
		if (!edge.hasIncomings()){
			NodeWrapper node = getCandidateNodeUsingBigBundles(edge, true);
			if (node != null){
				edge.setSourceBundledAware(node);	
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * fix the missing target of an unconnected bundled edge
	 * @param edge the edge without a target
	 * @return true if successful
	 */
	private boolean fixOutgoingEdges(EdgeWrapper edge) {
		if (!edge.hasOutgoings()){
			NodeWrapper node = getCandidateNodeUsingBigBundles(edge, false);
			if (node != null){
				edge.setTargetBundledAware(node);	
				return true;
			} 
		}
		return false;
	}
}
