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

import org.jbpt.pm.ProcessModel;

import weka.core.Instance;

/**
 * Custom class of the data elements for the
 * {@link HierarchicalProcessClusterer}. Extends WEKAs {@link Instance} by the
 * {@link ProcessModel} that is to be clustered and string attributes to use.
 * 
 * @author Cindy Fähnrich
 * 
 */
public class ProcessInstance extends Instance {

	/** for serialization */
	static final long serialVersionUID = 4L;

	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/** process model that is clustered */
	public ProcessModel process;
	
	 /** The instance's string attribute values. */
	  protected /*@spec_public non_null@*/ String[] m_StringAttValues;

	/**
	 * Constructor that copies the numeric and string attribute values and the weight from the
	 * given instance. Reference to the dataset is set to null. (ie. the
	 * instance doesn't have access to information about the attribute types)
	 * 
	 * @param instance
	 *            the instance from which the attribute values and the weight
	 *            are to be copied
	 */
	// @ ensures m_Dataset == null;
	public ProcessInstance(/* @non_null@ */ProcessInstance instance) {

		m_AttValues = instance.m_AttValues;
		m_StringAttValues = instance.m_StringAttValues;
		m_Weight = instance.m_Weight;
		this.id = instance.getId();
		m_Dataset = null;
	}
	
	/**
	   * Merges this instance with the given instance and returns
	   * the result. Dataset is set to null.
	   *
	   * @param inst the instance to be merged with this one
	   * @return the merged instances
	   */
	  public ProcessInstance mergeInstance(ProcessInstance inst) {

	    int m = 0;
	    double [] newVals = new double[numAttributes() + inst.numAttributes()];
	    for (int j = 0; j < numAttributes(); j++, m++) {
	      newVals[m] = value(j);
	    }
	    for (int j = 0; j < inst.numAttributes(); j++, m++) {
	      newVals[m] = inst.value(j);
	    }
	    
	    //merge for string attributes
	    int n = 0;
	    String [] newStrVals = new String[numStrAttributes() + inst.numStrAttributes()];
	    for (int j = 0; j < numStrAttributes(); j++, n++) {
	      newStrVals[n] = strValue(j);
	    }
	    for (int j = 0; j < inst.numStrAttributes(); j++, n++) {
	      newStrVals[n] = inst.strValue(j);
	    }
	    
	    return new ProcessInstance(1.0, newVals, newStrVals);
	  }
	  
	  /**
	   * Sets a specific string attribute in the instance to the given value. 
	   *
	   * @param attIndex the string attribute's index 
	   * @param value the new attribute value   
	   */
	  public void setStrValue(int attIndex, String value) {
	    
	    m_StringAttValues[attIndex] = value;
	  }
	  
	  /**
	   * Returns the number of string attributes.
	   *
	   * @return the number of string attributes as an integer
	   */
	  //@ ensures \result == m_AttValues.length;
	  public /*@pure@*/ int numStrAttributes() {

	    return m_StringAttValues.length;
	  }


	  /**
	   * Returns an instance's string attribute value.
	   *
	   * @param attIndex the string attribute's index
	   * @return the specified value as a double (If the corresponding
	   * attribute is nominal (or a string) then it returns the value's index as a 
	   * double).
	   */
	  public /*@pure@*/ String strValue(int attIndex) {

	    return m_StringAttValues[attIndex];
	  }
	  
	  /**
	   * Tests if a specific string value is "missing".
	   *
	   * @param attIndex the string attribute's index
	   * @return true if the value is "missing"
	   */
	  public /*@pure@*/ boolean isStringMissing(int attIndex) {

	    if (m_StringAttValues[attIndex].equals("")) {
	      return true;
	    }
	    return false;
	  }
	  
	/**
	 * Constructor that inititalizes instance variable with given numeric attribute values.
	 * Reference to the dataset is set to null. (ie. the instance doesn't have
	 * access to information about the attribute types)
	 * 
	 * @param weight
	 *            the instance's weight
	 * @param attValues
	 *            a vector of numeric attribute values
	 */
	// @ ensures m_Dataset == null;
	public ProcessInstance(double weight, /* @non_null@ */double[] attValues) {

		m_AttValues = attValues;
		m_Weight = weight;
		m_Dataset = null;
		m_StringAttValues = null;
	}

	/**
	 * Constructor that inititalizes instance variable with given numeric and 
	 * string attribute values.
	 * Reference to the dataset is set to null. (ie. the instance doesn't have
	 * access to information about the attribute types)
	 * 
	 * @param weight
	 *            the instance's weight
	 * @param attValues
	 *            a vector of attribute values
	 * @param strAttValues
	 *            a vector of string attribute values
	 */
	// @ ensures m_Dataset == null;
	public ProcessInstance(double weight, /* @non_null@ */double[] attValues, String[] strAttValues) {

		m_AttValues = attValues;
		m_Weight = weight;
		m_Dataset = null;
		m_StringAttValues = strAttValues;
	}
	
	/**
	 * Constructor of an instance that sets weight to one, all values to be
	 * missing, and the reference to the dataset to null. (ie. the instance
	 * doesn't have access to information about the attribute types)
	 * 
	 * @param numAttributes
	 *            the number of the numeric attributes
	 * @param numStrAttributes
	 *            the number of the string attributes
	 */
	// @ requires numAttributes > 0; // Or maybe == 0 is okay too?
	// @ ensures m_Dataset == null;
	public ProcessInstance(int numAttributes, int numStrAttributes) {

		m_StringAttValues = new String[numStrAttributes];
		m_AttValues = new double[numAttributes];
		for (int i = 0; i < m_AttValues.length; i++) {
			m_AttValues[i] = MISSING_VALUE;
		}
		for (int i = 0; i < m_StringAttValues.length; i++) {
			m_StringAttValues[i] = "";
		}
		m_Weight = 1;
		m_Dataset = null;
	}
	/**
	 * Constructor of an instance that sets weight to one, all values to be
	 * missing, and the reference to the dataset to null. (ie. the instance
	 * doesn't have access to information about the attribute types)
	 * 
	 * @param numAttributes
	 *            the size of the instance
	 */
	// @ requires numAttributes > 0; // Or maybe == 0 is okay too?
	// @ ensures m_Dataset == null;
	public ProcessInstance(int numAttributes) {

		this(numAttributes, 0);
	}

	/**
	 * Returns the description of one instance's string attributes (without weight appended). If the
	 * instance doesn't have access to a dataset, it returns the internal
	 * floating-point values. Quotes string values that contain whitespace
	 * characters.
	 * 
	 * This method is used by getRandomNumberGenerator() in Instances.java in
	 * order to maintain backwards compatibility with weka 3.4.
	 * 
	 * @return the instance's description as a string
	 */
	protected String toStringNoWeight() {
		StringBuffer text = new StringBuffer();
		text.append("Numeric: ");
		for (int i = 0; i < m_AttValues.length; i++) {
			if (i > 0)
				text.append(",");
			text.append(toString(i));
		}
		text.append("Nominal: ");
		for (int i = 0; i < m_StringAttValues.length; i++) {
			if (i > 0)
				text.append(",");
			text.append(m_StringAttValues[i]);
		}
		
		return text.toString();
	}

	/**
	 * Produces a shallow copy of this instance. The copy has access to the same
	 * dataset. (if you want to make a copy that doesn't have access to the
	 * dataset, use <code>new ProcessInstance(instance)</code>
	 * 
	 * @return the shallow copy
	 */
	// @ also ensures \result != null;
	// @ also ensures \result instanceof Instance;
	// @ also ensures ((Instance)\result).m_Dataset == m_Dataset;
	public/* @pure@ */ProcessInstance copy() {

		ProcessInstance result = new ProcessInstance(this);
		result.m_Dataset = m_Dataset;
		result.process = process;
		result.m_AttValues = m_AttValues;
		result.m_StringAttValues = m_StringAttValues;
		result.m_Weight = m_Weight;
		return result;
	}

	/**
	 * Deletes an attribute at the given position (0 to numAttributes() - 1).
	 * 
	 * @param position
	 *            the attribute's position
	 * @param isNumericAtt
	 *            indicates whether attribute to delete is string or numeric
	 */
	void forceDeleteAttributeAt(int position, boolean isNumericAtt) {

		if (isNumericAtt) {
			double[] newValues = new double[m_AttValues.length - 1];

			System.arraycopy(m_AttValues, 0, newValues, 0, position);
			if (position < m_AttValues.length - 1) {
				System.arraycopy(m_AttValues, position + 1, newValues, position,
						m_AttValues.length - (position + 1));
			}
			m_AttValues = newValues;
		} else {
			String[] newValues = new String[m_StringAttValues.length - 1];

			System.arraycopy(m_StringAttValues, 0, newValues, 0, position);
			if (position < m_StringAttValues.length - 1) {
				System.arraycopy(m_StringAttValues, position + 1, newValues, position,
						m_StringAttValues.length - (position + 1));
			}
			m_StringAttValues = newValues;
		}
		
	}
}
