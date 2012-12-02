package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors;

import java.util.HashSet;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.EdgeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.ShapeWrapper;

/**
 * Detect edges which can not connect their source and target 
 * @author Christian Kieschnick
 *
 */
public class WrongEdgeTypeConnectionDetector extends AbstractDetector {

	/**
	 * a list of all models which contain wrong edges
	 */
	public static HashSet<String> errorModels = new HashSet<String>();
	
	@Override
	public int numberOfErrors(ShapeWrapper shape) {
		EdgeWrapper edge = (EdgeWrapper)shape;
		if (edge.hasSource() 
				&& edge.hasTarget() &&
				!edge.getRules().canBeConnected(edge.getSource(), edge.getTarget(), edge)){
			errorModels.add(shape.getDiagramWrapper().getDiagram().getPath());
			return 1;
			
		}
		return 0;
	}

	@Override
	public boolean canDetectOn(ShapeWrapper wrapper) {
		return wrapper.isEdge();
	}

}
