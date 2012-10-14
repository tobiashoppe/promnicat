package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.detectors;

import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.ShapeWrapper;

/**
 * Simple class to count all elements in a given model
 * @author Christian Kieschnick
 *
 */
public class ElementDetector extends AbstractDetector {

	@Override
	public boolean canDetectOn(ShapeWrapper wrapper) {
		return true;
	}

	@Override
	public int numberOfErrors(ShapeWrapper shape) {
		return 1;
	}

}
