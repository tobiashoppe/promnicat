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

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.neighboursearch.PerformanceStats;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstance;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstances;

/**
 * Class derived from the EuclideanDistance class in WEKA, but also taking into account
 * attribute weights and normalized values.
 * @author Cindy Fähnrich
 *
 */
public class WeightedEuclideanDistance extends weka.core.EuclideanDistance {

	  /** for serialization. */
	  private static final long serialVersionUID = 1068606253458807903L;

	  /** indicates whether to normalize the values or not */
	  private boolean normalizeEnabled = false;
	  /** contains the maximum values for the different attributes for normalization */
	  private ArrayList<double[]> normFeatureValues;
	  
	  /**
	   * Constructs an Euclidean Distance object, Instances must be still set.
	   * Enables normalization.
	   * @param maxFeatures
	   * 			the maximum values of the different numeric features
	   */
	  public WeightedEuclideanDistance(ArrayList<double[]> normFeatures) {
	    super();
	    normFeatureValues = normFeatures;
	    normalizeEnabled = true;
	  }
	  
	  public WeightedEuclideanDistance() {
		  super();
	  }

	  /**
	   * Constructs an Euclidean Distance object and automatically initializes the
	   * ranges.
	   * 
	   * @param data 	the instances the distance function should work on
	   */
	  public WeightedEuclideanDistance(ProcessInstances data) {
	    super(data);
	  }
	  
	  /**
	   * Sets the maximum values for normalization
	   * @param maxFeatures
	   * 			maximum values of the different attributes
	   */
		public void setMaximumFeatureValues(ArrayList<double[]> normFeatures){
			normFeatureValues = normFeatures;
			normalizeEnabled = true;
		}
	  
	  /**
	   * Calculates the distance between two instances.
	   * 
	   * @param first 	the first instance
	   * @param second 	the second instance
	   * @return 		the distance between the two given instances
	   */
	  public double distance(ProcessInstance first, ProcessInstance second) {
	    return Math.sqrt(distance(first, second, Double.POSITIVE_INFINITY));
	  }
	  
	  /**
	   * Calculates the distance (or similarity) between two instances. Need to
	   * pass this returned distance later on to postprocess method to set it on
	   * correct scale. <br/>
	   * P.S.: Please don't mix the use of this function with
	   * distance(Instance first, Instance second), as that already does post
	   * processing. Please consider passing Double.POSITIVE_INFINITY as the cutOffValue to
	   * this function and then later on do the post processing on all the
	   * distances.
	   *
	   * @param first 	the first instance
	   * @param second 	the second instance
	   * @param stats 	the structure for storing performance statistics.
	   * @return 		the distance between the two given instances or 
	   * 			Double.POSITIVE_INFINITY.
	   */
	  public double distance(ProcessInstance first, ProcessInstance second, PerformanceStats stats) { //debug method pls remove after use
	    return Math.sqrt(distance(first, second, Double.POSITIVE_INFINITY, stats));
	  }
	  
	  /**
	   * Computes the difference between two given attribute
	   * values. Incorporates weights if enabled.
	   * 
	   * @param index	the attribute index
	   * @param val1	the first value
	   * @param val2	the second value
	   * @return		the difference
	   */
	  protected double difference(int index, double val1, double val2) {
		double weight = m_Data.attribute(index).weight();
		
		//normalize features by formula: (x - minimum) / (maximum - minimum)
		if (normalizeEnabled){
			val1 = (val1 - normFeatureValues.get(0)[index]) / (normFeatureValues.get(1)[index] - normFeatureValues.get(0)[index]);
			val2 /= (val2 - normFeatureValues.get(0)[index]) / (normFeatureValues.get(1)[index] - normFeatureValues.get(0)[index]);
		}
	    switch (m_Data.attribute(index).type()) {
	      case Attribute.NOMINAL:
	        if (Instance.isMissingValue(val1) ||
	           Instance.isMissingValue(val2) ||
	           ((int) val1 != (int) val2)) {
	          return weight * 1;
	        }
	        else {
	          return 0;
	        }
	        
	      case Attribute.NUMERIC:
	        if (Instance.isMissingValue(val1) ||
	           Instance.isMissingValue(val2)) {
	          if (Instance.isMissingValue(val1) &&
	             Instance.isMissingValue(val2)) {
	            if (!m_DontNormalize)  //We are doing normalization
	              return weight * 1;
	            else
	              return weight * (m_Ranges[index][R_MAX] - m_Ranges[index][R_MIN]);
	          }
	          else {
	            double diff;
	            if (Instance.isMissingValue(val2)) {
	              diff = (!m_DontNormalize) ? norm(val1, index) : val1;
	            }
	            else {
	              diff = (!m_DontNormalize) ? norm(val2, index) : val2;
	            }
	            if (!m_DontNormalize && diff < 0.5) {
	              diff = 1.0 - diff;
	            }
	            else if (m_DontNormalize) {
	              if ((m_Ranges[index][R_MAX]-diff) > (diff-m_Ranges[index][R_MIN]))
	                return weight * (m_Ranges[index][R_MAX]-diff);
	              else
	                return weight * (diff-m_Ranges[index][R_MIN]);
	            }
	            return weight * diff;
	          }
	        }
	        else {
	          return (!m_DontNormalize) ? 
	              	 weight * (norm(val1, index) - norm(val2, index)) :
	              	 weight * (val1 - val2);
	        }
	        
	      default:
	        return 0;
	    }
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
	  public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
	    double distance = 0;
	    int firstI, secondI;
	    int firstNumValues = first.numValues();
	    int secondNumValues = second.numValues();
	    int numAttributes = m_Data.numAttributes();
	    int classIndex = m_Data.classIndex();
	    double weights = 1;
	    
	    validate();
	    
	    for (int p1 = 0, p2 = 0; p1 < firstNumValues || p2 < secondNumValues; ) {
	      weights += first.attribute(p1).weight();
	      if (p1 >= firstNumValues)
		firstI = numAttributes;
	      else
		firstI = first.index(p1); 

	      if (p2 >= secondNumValues)
		secondI = numAttributes;
	      else
		secondI = second.index(p2);

	      if (firstI == classIndex) {
		p1++; 
		continue;
	      }
	      if ((firstI < numAttributes) && !m_ActiveIndices[firstI]) {
		p1++; 
		continue;
	      }
	       
	      if (secondI == classIndex) {
		p2++; 
		continue;
	      }
	      if ((secondI < numAttributes) && !m_ActiveIndices[secondI]) {
		p2++;
		continue;
	      }
	       
	      double diff;
	      
	      if (firstI == secondI) {
		diff = difference(firstI,
		    		  first.valueSparse(p1),
		    		  second.valueSparse(p2));
		p1++;
		p2++;
	      }
	      else if (firstI > secondI) {
		diff = difference(secondI, 
		    		  0, second.valueSparse(p2));
		p2++;
	      }
	      else {
		diff = difference(firstI, 
		    		  first.valueSparse(p1), 0);
		p1++;
	      }
	      if (stats != null)
		stats.incrCoordCount();
	      
	      distance = updateDistance(distance, diff);
	      if (distance > cutOffValue)
	        return Double.POSITIVE_INFINITY;
	    }

	    if (weights > 1){
	    	return distance / (weights - 1);
	    } 
	    return distance/weights;
	  }
}
