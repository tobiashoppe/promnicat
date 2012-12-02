package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.correctors;

import java.util.HashMap;
import java.util.Map.Entry;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.ShapeWrapper;

/**
 * base class for correction algorithms
 * enforces an interface which separates the detection and the correction of errors
 * enables the correction algorithms to collect data about successful correction
 * @author Christian Kieschnick
 */
public abstract class AbstractCorrector {
	/**
	 * collected data about successful appliance of the correction
	 */
	private static HashMap<String, Integer> counts = new HashMap<String, Integer>();
	
	private synchronized void increaseCorrectionCounter(){
		int count = 0;
		if (counts.containsKey(this.getClass().getSimpleName())){
			count = counts.get(this.getClass().getSimpleName());
		}
		counts.put(this.getClass().getSimpleName(), count + 1);
	}
	
	/**
	 * check if the precondition for fixing an error regarding the given shape are fulfilled
	 * @param shape the element on which may contain an error
	 * @return true if a correction can by applied
	 */
	public abstract boolean fulfillsPrecondition(ShapeWrapper shape);
	
	public boolean applyCorrection(ShapeWrapper shape){
		boolean fixed = applyCorrectionImplementation(shape);
		if (fixed){
			increaseCorrectionCounter();
		}
		return fixed;
	}

	/**
	 * the correction mechanism which is implemented by subclasses
	 * @param shape the element which contains the correctable error
	 * @return true if the correction was successful
	 */
	protected abstract boolean applyCorrectionImplementation(ShapeWrapper shape);
	
	/**
	 * @return a readable statistic about the correction 
	 */
	public static String correctionStatistics(){
		String result = "Statistics";
		for (Entry<String, Integer> entry : counts.entrySet()){
			result += "\n\t"+entry.getKey()+":\t"+entry.getValue();
		}
		return result;
	}
}
