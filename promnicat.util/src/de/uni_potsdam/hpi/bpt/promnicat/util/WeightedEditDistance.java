/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.util;

import weka.core.Attribute;
import weka.core.EditDistance;
import weka.core.Instance;
import weka.core.neighboursearch.PerformanceStats;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstance;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstances;

/**
 * Computes the Levenshtein edit distance between two strings.
 * Incorporates weights.
 *
 * @author Cindy Fähnrich
 *
 */
public class WeightedEditDistance extends EditDistance {

	private static final long serialVersionUID = 1L;

	public WeightedEditDistance() {
	  }

	  public WeightedEditDistance(ProcessInstances data) {
	    super(data);
	  }

	  /**
	   * Calculates the distance between two instances. Offers speed up (if the 
	   * distance function class in use supports it) in nearest neighbour search by 
	   * taking into account the cutOff or maximum distance. Depending on the 
	   * distance function class, post processing of the distances by 
	   * postProcessDistances(double []) may be required if this function is used.
	   *
	   * @param first 	the first instance
	   * @param second 	the second instance
	   * @param cutOffValue If the distance being calculated becomes larger than 
	   *                    cutOffValue then the rest of the calculation is 
	   *                    discarded.
	   * @param stats 	the performance stats object
	   * @return 		the distance between the two given instances or 
	   * 			Double.POSITIVE_INFINITY if the distance being 
	   * 			calculated becomes larger than cutOffValue. 
	   */
	  @Override
	    public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
	    double sqDistance = 0;
	    int numAttributes = ((ProcessInstances)m_Data).numStrAttributes();
	    
	    validate();
	    
	    double diff;
	    //incorporates weights
	    double weights = 1;
	    for (int i = 0; i < numAttributes; i++) {
	      diff = 0;
	      if (m_ActiveIndices[i]) {
	    	  diff = difference(i, ((ProcessInstance)first).strValue(i), ((ProcessInstance)second).strValue(i));
	    	  //use weight
	    	  diff = diff * ((ProcessInstances)m_Data).strAttribute(i).weight();
	      }
	      sqDistance = updateDistance(sqDistance, diff);
	      if (sqDistance > (cutOffValue * cutOffValue)) return Double.POSITIVE_INFINITY;
	    }  
	    double distance = Math.sqrt(sqDistance);
	    if (weights > 1){
	    	return distance / (weights - 1);
	    }
	    return distance/weights;
	  }
	  
	  /**
	   * Calculates the distance (Levenshtein Edit Distance) between two strings
	   *
	   * @param stringA the first string
	   * @param stringB the second string
	   * @return the distance between the two given strings
	   */
	  public double getStringDistance(String stringA, String stringB) {
	    int lengthA = stringA.length();
	    int lengthB = stringB.length();

	    double[][] distanceMatrix = new double[lengthA + 1][lengthB + 1];

	    for (int i = 0; i <= lengthA; i++) {
	      distanceMatrix[i][0] = i;
	    }

	    for (int j = 1; j <= lengthB; j++) {
	      distanceMatrix[0][j] = j;
	    }

	    for (int i = 1; i <= lengthA; i++) {
	      for (int j = 1; j <= lengthB; j++) {
	        if (stringA.charAt(i - 1) == stringB.charAt(j - 1)) {
	          distanceMatrix[i][j] = distanceMatrix[i - 1][j - 1];
	        }
	        else {
	          distanceMatrix[i][j] = 1 + Math.min(distanceMatrix[i - 1][j],
	                                              Math.min(distanceMatrix[i][j - 1],
	                                                       distanceMatrix[i - 1][j - 1]));
	        }
	      }
	    }
	    return distanceMatrix[lengthA][lengthB];
	  }
	  
	  /**
	   * Computes the difference between two given attribute
	   * values.
	   * 
	   * @param index	the attribute index
	   * @param val1	the first value
	   * @param val2	the second value
	   * @return		the difference
	   */
	  protected double difference(int index, String string1, String string2) {
	    switch (((ProcessInstances) m_Data).strAttribute(index).type()) {
	    case Attribute.STRING:
	      double diff = getStringDistance(string1, string2);
	      if (m_DontNormalize == true) {
	        return diff;
	      }
	      else {
	        if (string1.length() > string2.length()) {
	          return diff/((double) string1.length());  
	        }
	        else {
	          return diff/((double) string2.length());    
	        }
	      }

	    default:
	      return 0;
	    }
	  }
	  
	  /**
	   * Updates the ranges given a new instance.
	   * 
	   * @param instance 	the new instance
	   * @param ranges 	low, high and width values for all attributes
	   * @return		the updated ranges
	   */
	  public double[][] updateRanges(Instance instance, double[][] ranges) {
	    // updateRangesFirst must have been called on ranges
	    for (int j = 0; j < ranges.length; j++) {
	      double value = instance.value(j);
	      if (!((ProcessInstance)instance).isStringMissing(j)) {
	        if (value < ranges[j][R_MIN]) {
	          ranges[j][R_MIN] = value;
	          ranges[j][R_WIDTH] = ranges[j][R_MAX] - ranges[j][R_MIN];
	        } else {
	          if (instance.value(j) > ranges[j][R_MAX]) {
	            ranges[j][R_MAX] = value;
	            ranges[j][R_WIDTH] = ranges[j][R_MAX] - ranges[j][R_MIN];
	          }
	        }
	      }
	    }
	    
	    return ranges;
	  }
	  
	  /**
	   * Updates the minimum and maximum and width values for all the attributes
	   * based on a new instance.
	   * 
	   * @param instance 	the new instance
	   * @param numAtt 	number of attributes in the model
	   * @param ranges 	low, high and width values for all attributes
	   */
	  public void updateRanges(Instance instance, int numAtt, double[][] ranges) {
	    // updateRangesFirst must have been called on ranges
	    for (int j = 0; j < numAtt; j++) {
	      double value = instance.value(j);
	      if (!((ProcessInstance)instance).isMissing(j)) {
	        if (value < ranges[j][R_MIN]) {
	          ranges[j][R_MIN] = value;
	          ranges[j][R_WIDTH] = ranges[j][R_MAX] - ranges[j][R_MIN];
	          if (value > ranges[j][R_MAX]) { //if this is the first value that is
	            ranges[j][R_MAX] = value;    //not missing. The,0
	            ranges[j][R_WIDTH] = ranges[j][R_MAX] - ranges[j][R_MIN];
	          }
	        }
	        else {
	          if (value > ranges[j][R_MAX]) {
	            ranges[j][R_MAX] = value;
	            ranges[j][R_WIDTH] = ranges[j][R_MAX] - ranges[j][R_MIN];
	          }
	        }
	      }
	    }
	  }
	  
	  /**
	   * Used to initialize the ranges. For this the values of the first
	   * instance is used to save time.
	   * Sets low and high to the values of the first instance and
	   * width to zero.
	   * 
	   * @param instance 	the new instance
	   * @param numAtt 	number of attributes in the model
	   * @param ranges 	low, high and width values for all attributes
	   */
	  public void updateRangesFirst(Instance instance, int numAtt, double[][] ranges) {
	    for (int j = 0; j < numAtt; j++) {
	      if (!((ProcessInstance)instance).isStringMissing(j)) {
	        ranges[j][R_MIN] = instance.value(j);
	        ranges[j][R_MAX] = instance.value(j);
	        ranges[j][R_WIDTH] = 0.0;
	      }
	      else { // if value was missing
	        ranges[j][R_MIN] = Double.POSITIVE_INFINITY;
	        ranges[j][R_MAX] = -Double.POSITIVE_INFINITY;
	        ranges[j][R_WIDTH] = Double.POSITIVE_INFINITY;
	      }
	    }
	  }
	  
	  /**
	   * Initializes the ranges using all instances of the dataset.
	   * Sets m_Ranges.
	   * 
	   * @return 		the ranges
	   */
	  public double[][] initializeRanges() {
	    if (m_Data == null) {
	      m_Ranges = null;
	      return m_Ranges;
	    }
	    
	    int numAtt = ((ProcessInstances)m_Data).numStrAttributes();
	    double[][] ranges = new double [numAtt][3];
	    
	    if (m_Data.numInstances() <= 0) {
	      initializeRangesEmpty(numAtt, ranges);
	      m_Ranges = ranges;
	      return m_Ranges;
	    }
	    else {
	      // initialize ranges using the first instance
	      updateRangesFirst(m_Data.instance(0), numAtt, ranges);
	    }
	    
	    // update ranges, starting from the second
	    for (int i = 1; i < m_Data.numInstances(); i++)
	      updateRanges(m_Data.instance(i), numAtt, ranges);

	    m_Ranges = ranges;
	    
	    return m_Ranges;
	  }
	  
	  /**
	   * Initializes the ranges of a subset of the instances of this dataset.
	   * Therefore m_Ranges is not set.
	   * 
	   * @param instList 	list of indexes of the subset
	   * @return 		the ranges
	   * @throws Exception	if something goes wrong
	   */
	  public double[][] initializeRanges(int[] instList) throws Exception {
	    if (m_Data == null)
	      throw new Exception("No instances supplied.");
	    
	    int numAtt = ((ProcessInstances)m_Data).numStrAttributes();
	    double[][] ranges = new double [numAtt][3];
	    
	    if (m_Data.numInstances() <= 0) {
	      initializeRangesEmpty(numAtt, ranges);
	      return ranges;
	    }
	    else {
	      // initialize ranges using the first instance
	      updateRangesFirst(m_Data.instance(instList[0]), numAtt, ranges);
	      // update ranges, starting from the second
	      for (int i = 1; i < instList.length; i++) {
	        updateRanges(m_Data.instance(instList[i]), numAtt, ranges);
	      }
	    }
	    return ranges;
	  }
	  
	  /**
	   * initializes the attribute indices.
	   */
	  protected void initializeAttributeIndices() {
	    m_AttributeIndices.setUpper(((ProcessInstances)m_Data).numStrAttributes() - 1);
	    m_ActiveIndices = new boolean[((ProcessInstances)m_Data).numStrAttributes()];
	    for (int i = 0; i < m_ActiveIndices.length; i++)
	      m_ActiveIndices[i] = m_AttributeIndices.isInRange(i);
	  }
	  
	  /**
	   * Initializes the ranges of a subset of the instances of this dataset.
	   * Therefore m_Ranges is not set.
	   * The caller of this method should ensure that the supplied start and end 
	   * indices are valid (start &lt;= end, end&lt;instList.length etc) and
	   * correct.
	   *
	   * @param instList 	list of indexes of the instances
	   * @param startIdx 	start index of the subset of instances in the indices array
	   * @param endIdx 	end index of the subset of instances in the indices array
	   * @return 		the ranges
	   * @throws Exception	if something goes wrong
	   */
	  public double[][] initializeRanges(int[] instList, int startIdx, int endIdx) throws Exception {
	    if (m_Data == null)
	      throw new Exception("No instances supplied.");
	    
	    int numAtt = ((ProcessInstances)m_Data).numStrAttributes();
	    double[][] ranges = new double [numAtt][3];
	    
	    if (m_Data.numInstances() <= 0) {
	      initializeRangesEmpty(numAtt, ranges);
	      return ranges;
	    }
	    else {
	      // initialize ranges using the first instance
	      updateRangesFirst(m_Data.instance(instList[startIdx]), numAtt, ranges);
	      // update ranges, starting from the second
	      for (int i = startIdx+1; i <= endIdx; i++) {
	        updateRanges(m_Data.instance(instList[i]), numAtt, ranges);
	      }
	    }
	    
	    return ranges;
	  }
	  
	  /**
	   * Test if an instance is within the given ranges.
	   * 
	   * @param instance 	the instance
	   * @param ranges 	the ranges the instance is tested to be in
	   * @return true 	if instance is within the ranges
	   */
	  public boolean inRanges(Instance instance, double[][] ranges) {
	    boolean isIn = true;
	    
	    // updateRangesFirst must have been called on ranges
	    for (int j = 0; isIn && (j < ranges.length); j++) {
	      if (!((ProcessInstance)instance).isStringMissing(j)) {
	        double value = instance.value(j);
	        isIn = value <= ranges[j][R_MAX];
	        if (isIn) isIn = value >= ranges[j][R_MIN];
	      }
	    }
	    
	    return isIn;
	  }
}
