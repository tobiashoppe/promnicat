package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors;

import java.util.HashSet;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.ShapeWrapper;

/**
 * Detector looks for associations which do not have a source or a target
 * @author logge002
 *
 */
public class MissingEdgeConnectionDetector extends AbstractDetector {
	
	/**
	 * collected errors which contain errors
	 */
	public static HashSet<String> errorModels = new HashSet<String>();
	
	@Override
	public int numberOfErrors(ShapeWrapper shape){
		int count = 0;
		if (shape.needsAtLeastOneConnection()){
			if (shape.isEdge()){
				if (!shape.hasIncomings()){
					count++;
				}
				if (!shape.hasOutgoings())
					count++;
			} else {
				// every shape should have at least one connection to the process graph
				if (!shape.hasIncomings() && !shape.hasOutgoings()){
					count++;
				}
			}
		}
		if (count != 0){
			errorModels.add(shape.getDiagramWrapper().getDiagram().getPath());
		}
		return count;
	}

	@Override
	public boolean canDetectOn(ShapeWrapper wrapper) {
		return wrapper.isEdge();
	}

}
