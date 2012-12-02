package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection;

/**
 * Constants for configuring the correction algorithms
 * @author Christian Kieschnick
 *
 */
public class CorrectionConstants {

	/**
	 * the maximal gap between an floating bundle edge and the reference edge
	 */
	public static double MaximalBundlingTolerance = 10.0;
	
	/**
	 * the maximal gap between an floating edge and its potential source or target
	 */
	public static double MaximalEdgeNodeGapTolerance = Double.MAX_VALUE;
	
	/**
	 * the maximal gap between an incorrect edge and its probable source or target node
	 */
	public static double MaximalDistanceForWrongEdgeTypeGapToNode = 50.0;
	
	/**
	 * the maximal gap between an incorrect edge and its probable source or target edge
	 */
	public static double MaximalDistanceForWrongEdgeTypeGapToEdge = 10.0;
	
	/**
	 * the maximal number of iterations for correcting an model
	 */
	public static int MaximalNumberOfCorrectionIterations = 10;
}
