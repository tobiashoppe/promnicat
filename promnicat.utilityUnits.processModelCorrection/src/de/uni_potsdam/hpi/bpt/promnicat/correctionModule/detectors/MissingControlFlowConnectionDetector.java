package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.detectors;

import java.util.List;

import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.EdgeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.NodeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.ShapeWrapper;

/**
 * Detectors for missing incoming and outgoing control flows (grouped they are very similar)
 * @author Christian Kieschnick
 */
public abstract class MissingControlFlowConnectionDetector extends AbstractDetector {

	/**
	 * Detect missing incoming connections for control flow relevant elements
	 * @author Christian Kieschnick
	 */
	public static class Incoming extends MissingControlFlowConnectionDetector {
		@Override
		public int numberOfErrors(ShapeWrapper shape) {
			// the shape should be of a control flow type - this should ensured by the canDetectOn method
			int errorCount = 0;
			if (shape.isEdge()){
				EdgeWrapper edge = (EdgeWrapper)shape;
				if (!containsControlFlowNodes(edge.getIncomings())){
					errorCount++;
				}
			}
			if (shape.isNode()){
				NodeWrapper node = (NodeWrapper)shape;
				if (node.needsIncomingControlFlow() && !containsControlFlowEdge(node.getIncomings())){
					errorCount++;
				}
			}
			return errorCount;
		}
	}
	
	/**
	 * Detect missing outgoing connections for control flow relevant elements
	 * @author Christian Kieschnick
	 */
	public static class Outgoing extends MissingControlFlowConnectionDetector  {
		@Override
		public int numberOfErrors(ShapeWrapper shape) {
			// the shape should be of a control flow type - this should ensured by the canDetectOn method
			int errorCount = 0;
			if (shape.isEdge()){
				EdgeWrapper edge = (EdgeWrapper)shape;
				if (!containsControlFlowNodes(edge.getOutgoings())){
					errorCount++;
				}
			}
			if (shape.isNode()){
				NodeWrapper node = (NodeWrapper)shape;
				if (node.needsOutgoingControlFlow() && !containsControlFlowEdge(node.getOutgoings())){
					errorCount++;
				}
			}
			return errorCount;
		}
	}
	
	@Override
	public boolean canDetectOn(ShapeWrapper shape) {
		if (shape.isEdge()){
			return ((EdgeWrapper)shape).isDirectingControlFlow();
		}
		if (shape.isNode()){
			return ((NodeWrapper)shape).isDirectingControlNode();
		}
		return false;
	}

	/**
	 * check if the collection contains control flow nodes
	 * @param wrappers the elements to be inspected
	 * @return true if one of the nodes directs control flow
	 */
	protected boolean containsControlFlowNodes(List<ShapeWrapper> wrappers){
		for (ShapeWrapper node : wrappers){
			if (node.isNode() && ((NodeWrapper)node).isDirectingControlNode()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * check if the collection contains control flow edges
	 * @param wrappers the elements to be inspected
	 * @return true if one of the edges directs control flow
	 */
	protected boolean containsControlFlowEdge(List<ShapeWrapper> wrappers){
		for (ShapeWrapper edge : wrappers){
			if (edge.isEdge() && ((EdgeWrapper)edge).isDirectingControlFlow()){
				return true;
			}
		}
		return false;
	}
	


}
