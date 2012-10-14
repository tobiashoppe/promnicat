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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering;

import java.util.ArrayList;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataFeatureVector;

/**
 * Feature vector class containing the string and numeric features to be taken 
 * into account. Used by {@link UnitDataFeatureVector}.
 * 
 * @author Cindy Fähnrich
 * 
 */
public class FeatureVector {

	/** {@link ArrayList} containing all the features */
	ArrayList<Feature> features = new ArrayList<Feature>();

	/**
	 * Adds a {@link Feature} with string values
	 * 
	 * @param val
	 *            String value of the feature
	 * @param weight
	 *            Weight of the feature
	 */
	public void addStringFeature(String val) {
		Feature f = new Feature(val);
		features.add(f);
	}
	
	/**
	 * Adds a {@link Feature} with numeric values
	 * 
	 * @param val
	 *            Numeric value of the feature
	 * @param weight
	 *            Weight of the feature
	 */
	public void addNumericFeature(double val) {
		Feature f = new Feature(val);
		features.add(f);
	}

	/**
	 * Returns the amount of features in the feature vector
	 * 
	 * @return
	 */
	public int size() {
		return features.size();
	}
	
	/**
	 * Returns the amount of numeric features in the feature vector
	 * 
	 * @return the amount of numeric features in the feature vector
	 */
	public int numericFeatureSize() {
	int i = 0;
		for (Feature f : features) {
			if (!f.isStringFeature){
				i++;
			}
		}
		return i;
	}
	
	/**
	 * Returns the amount of string features in the feature vector
	 * 
	 * @return the amount of string features in the vector
	 */
	public int stringFeatureSize() {
		int i = 0;
		for (Feature f : features) {
			if (f.isStringFeature){
				i++;
			}
		}
		return i;
	}

	/**
	 * Returns the numeric features of the feature vector
	 * @return
	 * 		the numeric features of the feature vector
	 */
	public double[] getNumericFeatures() {
		
		double[] result = new double[numericFeatureSize()];
		int i = 0;
		for (Feature f : features) {
			if (!f.isStringFeature){
				result[i] = f.getNumericValue();
				i++;
			}
		}
		return result;
	}
	
	/**
	 * Returns the string features of the feature vector
	 * @return
	 * 		the string features of the feature vector
	 */
	public String[] getStringFeatures() {
		
		String[] result = new String[stringFeatureSize()];
		int i = 0;
		for (Feature f : features) {
			if (f.isStringFeature){
				result[i] = f.getStringValue();
				i++;
			}
		}
		return result;
	}

	/**
	 * Feature class for Feature Vector. Contains the feature value (either
	 * string or double).
	 * 
	 * @author Cindy Fähnrich
	 */
	public class Feature {
		private String strValue;
		private double numericValue;
		boolean isStringFeature = false;

		/**
		 * creates a feature with a numeric attribute with
		 * value 0.
		 */
		public Feature() {
			this(0);
		}

		/**
		 * Creates a feature with string value
		 * @param val
		 * 			string value of the feature
		 */
		public  Feature(String val) {
			super();
			isStringFeature = true;
			strValue = val;
			setValue(val);
		}
		
		/**
		 * Creates a feature with a numeric value
		 * @param val
		 * 			numeric value of the features
		 */
		public Feature(double val) {
			super();
			isStringFeature = false;
			numericValue = val;
		}

		/**
		 * Returns the string value of this feature
		 * 
		 * @return the string value of this feature
		 */
		public String getStringValue() {
				return strValue;
		}
	
		/**
		 * Returns the numeric value of this feature
		 * 
		 * @return the numeric value of this feature
		 */
		public double getNumericValue() {
			return numericValue;
		}
		
		/**
		 * Sets the string value of this feature
		 * 
		 * @param val
		 *            string to be set
		 */
		public void setValue(String val) {
			strValue = val;
		}
		/**
		 * Sets the numeric value of this feature
		 * 
		 * @param val
		 *           numeric to be set
		 */
		public void setValue(double val) {
			numericValue = val;
		}
	}
}
