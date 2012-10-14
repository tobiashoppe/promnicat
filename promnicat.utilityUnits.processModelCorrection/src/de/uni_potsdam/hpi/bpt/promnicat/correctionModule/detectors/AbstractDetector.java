package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.detectors;

import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.Statistic;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.DiagramWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.EdgeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.NodeWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.ShapeWrapper;

/**
 * implements basic behavior for detection of errors in models
 * @author Christian Kieschnick
 *
 */
public abstract class AbstractDetector {

	/**
	 * increase the error counter for a given shape in counts by a value
	 * synchronized to make sure that no conflicts occur
	 * @param counts the collection containing the current counters
	 * @param value the value to increase by
	 * @param shape the shape for which the counter should be increased
	 */
	protected synchronized void increaseCorrectionCounter(Statistic.GroupedValueResult counts, int value, ShapeWrapper shape){

		Statistic.SingleValueResult detectorCounts = counts.initializingGet(this.getClass().getSimpleName());
		String key = shape.getStencilId();
		if (key == null){
			key = shape.getResourceId(); //since in old models some resources have the stencil null but a common resourceId
		}
		detectorCounts.increase(key, value);

	}
	
	/**
	 * detect errors on a given shape and adjust the counter if necessary
	 * @param shape the shape to detect errors on
	 * @param result the counters which should be manipulated
	 */
	private void detectOn(ShapeWrapper shape, Statistic.GroupedValueResult result){
		if (canDetectOn(shape)){
			int numberOfErrors = numberOfErrors(shape);
			if (numberOfErrors > 0){
				increaseCorrectionCounter(result, numberOfErrors, shape);
			}
		}
	}
	
	/**
	 * detect errors on the given diagram
	 * @param diagram the model to inspect
	 * @param result the counter collection to modify
	 * @return the modified counter collection
	 */
	public Statistic.GroupedValueResult process(DiagramWrapper diagram, Statistic.GroupedValueResult result){
		for (EdgeWrapper edge : diagram.getEdgeWrappers()){
			detectOn(edge, result);
		}
		for (NodeWrapper node : diagram.getNodeWrappers()){
			detectOn(node, result);
		}
		return result;
	}
	
	/**
	 * determine if errors can be detected on the given shape
	 * @param wrapper the shape
	 * @return true if errors can be detected on
	 */
	public abstract boolean canDetectOn(ShapeWrapper wrapper);
	
	/**
	 * returns the number of errors for a given shape
	 * @param shape the shape to inspect
	 * @return the number of detected errors (usually one)
	 */
	public abstract int numberOfErrors(ShapeWrapper shape);
	
}
