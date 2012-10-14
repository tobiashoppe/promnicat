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

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.UnassignedClassException;
import weka.core.Utils;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Custom class of the data element container for the
 * {@link HierarchicalProcessClusterer}. Extends WEKAs {@link Instances} and is
 * the container for the customized data elements {@link ProcessInstance}
 * 
 * @author Cindy Fähnrich
 * 
 */
public class ProcessInstances extends Instances {

	/** for serialization */
	static final long serialVersionUID = -19412345060742748L;

	/** stores the string attributes and their weights extra */
	private FastVector m_String_Attributes;

	/**
	 * Reads an ARFF file from a reader, and assigns a weight of one to each
	 * instance. Lets the index of the class attribute be undefined (negative).
	 * 
	 * @param reader
	 *            the reader
	 * @throws IOException
	 *             if the ARFF file is not read successfully
	 */
	public ProcessInstances(/* @non_null@ */Reader reader) throws IOException {
		super("", new FastVector(), 0);
		ArffReader arff = new ArffReader(reader);
		ProcessInstances dataset = (ProcessInstances) arff.getData();
		initialize(dataset, dataset.numInstances());
		dataset.copyInstances(0, this, dataset.numInstances());
		compactify();
	}

	/**
	 * Reads the header of an ARFF file from a reader and reserves space for the
	 * given number of instances. Lets the class index be undefined (negative).
	 * 
	 * @param reader
	 *            the reader
	 * @param capacity
	 *            the capacity
	 * @throws IllegalArgumentException
	 *             if the header is not read successfully or the capacity is
	 *             negative.
	 * @throws IOException
	 *             if there is a problem with the reader.
	 * @deprecated instead of using this method in conjunction with the
	 *             <code>readInstance(Reader)</code> method, one should use the
	 *             <code>ArffLoader</code> or <code>DataSource</code> class
	 *             instead.
	 * @see weka.core.converters.ArffLoader
	 * @see weka.core.converters.ConverterUtils.DataSource
	 */
	// @ requires capacity >= 0;
	// @ ensures classIndex() == -1;
	@Deprecated
	public ProcessInstances(/* @non_null@ */Reader reader, int capacity)
			throws IOException {
		super("", new FastVector(), 0);
		ArffReader arff = new ArffReader(reader, 0);
		ProcessInstances header = (ProcessInstances) arff.getStructure();
		initialize(header, capacity);
		m_Lines = arff.getLineNo();
	}

	/**
	 * Constructor copying all instances and references to the header
	 * information from the given set of instances.
	 * 
	 * @param dataset
	 *            the set to be copied
	 */
	public ProcessInstances(/* @non_null@ */ProcessInstances dataset) {

		this(dataset, dataset.numInstances());

		dataset.copyInstances(0, this, dataset.numInstances());
	}

	/**
	 * Constructor creating an empty set of instances. Copies references to the
	 * header information from the given set of instances. Sets the capacity of
	 * the set of instances to 0 if its negative.
	 * 
	 * @param dataset
	 *            the instances from which the header information is to be taken
	 * @param capacity
	 *            the capacity of the new dataset
	 */
	public ProcessInstances(/* @non_null@ */ProcessInstances dataset,
			int capacity) {
		super("", new FastVector(), 0);
		initialize(dataset, capacity);
	}
	
	/**
	 * Constructor creating an empty set of instances. Copies references to the
	 * header information from the given set of instances. Sets the capacity of
	 * the set of instances to 0 if its negative.
	 * 
	 * @param dataset
	 *            the instances from which the header information is to be taken
	 * @param atts
	 *            the numeric attributes of the new dataset
	 * @param capacity
	 *            the capacity of the new dataset
	 */
	public ProcessInstances(/* @non_null@ */ProcessInstance data, FastVector atts,
			int capacity) {
		super("", atts, 0);
		m_Attributes = atts;
		m_String_Attributes = null;
		m_Instances = new FastVector(0);
		m_Instances.addElement(data);
	}
	
	/**
	 * Constructor creating an empty set of instances. Copies references to the
	 * header information from the given set of instances. Sets the capacity of
	 * the set of instances to 0 if its negative.
	 * 
	 * @param dataset
	 *            the instances from which the header information is to be taken
	 * @param atts
	 *            the numeric attributes of the new dataset
	 * @param strAtts
	 *            the string attributes of the new dataset
	 * @param capacity
	 *            the capacity of the new dataset
	 */
	public ProcessInstances(/* @non_null@ */ProcessInstance data, FastVector atts, FastVector strAtts,
			int capacity) {
		super("", atts, 0);
		m_Attributes = atts;
		m_String_Attributes = strAtts;
		m_Instances = new FastVector(0);
		m_Instances.addElement(data);
	}

	/**
	 * initializes with the header information of the given dataset, sets the
	 * capacity, numeric and nominal attributes of the set of instances.
	 * 
	 * @param dataset
	 *            the dataset to use as template
	 * @param capacity
	 *            the number of rows to reserve
	 */
	protected void initialize(ProcessInstances dataset, int capacity) {
		if (capacity < 0)
			capacity = 0;

		// Strings only have to be "shallow" copied because
		// they can't be modified.
		m_ClassIndex = dataset.m_ClassIndex;
		m_RelationName = dataset.m_RelationName;
		m_Attributes = dataset.m_Attributes;
		m_String_Attributes = dataset.m_String_Attributes;
		m_Instances = new FastVector(capacity);
	}

	/**
	 * Creates a new set of instances by copying a subset of another set.
	 * 
	 * @param source
	 *            the set of instances from which a subset is to be created
	 * @param first
	 *            the index of the first instance to be copied
	 * @param toCopy
	 *            the number of instances to be copied
	 * @throws IllegalArgumentException
	 *             if first and toCopy are out of range
	 */
	// @ requires 0 <= first;
	// @ requires 0 <= toCopy;
	// @ requires first + toCopy <= source.numInstances();
	public ProcessInstances(/* @non_null@ */ProcessInstances source, int first,
			int toCopy) {

		this(source, toCopy);

		if ((first < 0) || ((first + toCopy) > source.numInstances())) {
			throw new IllegalArgumentException(
					"Parameters first and/or toCopy out " + "of range");
		}
		source.copyInstances(first, this, toCopy);
	}

	/**
	 * Creates an empty set of instances. Uses the given numeric and string attribute information.
	 * Sets the capacity of the set of instances to 0 if its negative. Given
	 * attribute information must not be changed after this constructor has been
	 * used.
	 * 
	 * @param name
	 *            the name of the relation
	 * @param attInfo
	 *            the numeric attribute information
	 * @param strAttInfo
	 *            the string attribute information
	 * @param capacity
	 *            the capacity of the set
	 */
	public ProcessInstances(/* @non_null@ */String name,
	/* @non_null@ */FastVector attInfo, FastVector strAttInfo, int capacity) {
		// check whether the attribute names are unique
		super("", new FastVector(), 0);
		HashSet<String> names = new HashSet<String>();
		StringBuffer nonUniqueNames = new StringBuffer();
		int max = 0;
		if (attInfo != null){
			max = attInfo.size();
		}
		for (int i = 0; i < max; i++) {
			if (names.contains(((Attribute) attInfo.elementAt(i)).name())) {
				nonUniqueNames.append("'"
						+ ((Attribute) attInfo.elementAt(i)).name() + "' ");
			}
			names.add(((Attribute) attInfo.elementAt(i)).name());
		}
		if (names.size() != attInfo.size())
			throw new IllegalArgumentException(
					"Attribute names are not unique!" + " Causes: "
							+ nonUniqueNames.toString());
		names.clear();

		m_RelationName = name;
		m_ClassIndex = -1;
		m_Attributes = attInfo;
		m_String_Attributes = strAttInfo;
		m_Instances = new FastVector(capacity);
	}

	/**
	 * Create a copy of the structure if the data has string or relational
	 * attributes, "cleanses" string types (i.e. doesn't contain references to
	 * the strings seen in the past) and all relational attributes.
	 * 
	 * @return a copy of the instance structure.
	 */
	public ProcessInstances stringFreeStructure() {

		FastVector newAtts = new FastVector();
		for (int i = 0; i < m_Attributes.size(); i++) {
			Attribute att = (Attribute) m_Attributes.elementAt(i);
			if (att.type() == Attribute.STRING) {
				newAtts.addElement(new Attribute(att.name(), (FastVector) null,
						i));
			} else if (att.type() == Attribute.RELATIONAL) {
				newAtts.addElement(new Attribute(att.name(),
						new ProcessInstances((ProcessInstances) att.relation(),
								0), i));
			}
		}
		if (newAtts.size() == 0) {
			return new ProcessInstances(this, 0);
		}
		FastVector atts = (FastVector) m_Attributes.copy();
		for (int i = 0; i < newAtts.size(); i++) {
			atts.setElementAt(newAtts.elementAt(i),
					((Attribute) newAtts.elementAt(i)).index());
		}
		ProcessInstances result = new ProcessInstances(this, 0);
		result.m_Attributes = atts;
		return result;
	}

	/**
	 * Adds one instance to the end of the set. Shallow copies instance before
	 * it is added. Increases the size of the dataset if it is not large enough.
	 * Does not check if the instance is compatible with the dataset. Note:
	 * String or relational values are not transferred.
	 * 
	 * @param instance
	 *            the instance to be added
	 */
	public void addInstance(/* @non_null@ */ProcessInstance instance) {

		ProcessInstance newInstance = (ProcessInstance) instance.copy();

		newInstance.setDataset(this);
		m_Instances.addElement(newInstance);
	}

	/**
	 * Checks if the given instance is compatible with this dataset. Only looks
	 * at the size of the instance and the ranges of the values for nominal and
	 * string attributes.
	 * 
	 * @param instance
	 *            the instance to check
	 * @return true if the instance is compatible with the dataset
	 */
	public/* @pure@ */boolean checkInstance(ProcessInstance instance) {

		if (instance.numAttributes() != numAttributes()) {
			return false;
		}
		if (instance.numStrAttributes() != numStrAttributes()) {
			return false;
		}
		for (int i = 0; i < numAttributes(); i++) {
			if (instance.isMissing(i)) {
				continue;
			} else if (attribute(i).isNominal() || attribute(i).isString()) {
				if (!(Utils.eq(instance.value(i),
						(double) (int) instance.value(i)))) {
					return false;
				} else if (Utils.sm(instance.value(i), 0)
						|| Utils.gr(instance.value(i), attribute(i).numValues())) {
					return false;
				}
			}
		}
		return true;
	}
	

	  /**
	   * Returns the number of string attributes.
	   *
	   * @return the number of string attributes as an integer
	   */
	  //@ ensures \result == m_Attributes.size();
	  public /*@pure@*/ int numStrAttributes() {

	    return m_String_Attributes.size();
	  }

	/**
	 * Checks if two headers are equivalent.
	 * 
	 * @param dataset
	 *            another dataset
	 * @return true if the header of the given dataset is equivalent to this
	 *         header
	 */
	public/* @pure@ */boolean equalHeaders(ProcessInstances dataset) {

		// Check class and all attributes
		if (m_ClassIndex != dataset.m_ClassIndex) {
			return false;
		}
		if (m_Attributes.size() != dataset.m_Attributes.size()) {
			return false;
		}
		for (int i = 0; i < m_Attributes.size(); i++) {
			if (!(attribute(i).equals(dataset.attribute(i)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the first instance in the set.
	 * 
	 * @return the first instance in the set
	 */
	// @ requires numInstances() > 0;
	public ProcessInstance getFirstInstance() {

		return (ProcessInstance) m_Instances.firstElement();
	}

	/**
	 * Returns the instance at the given position.
	 * 
	 * @param index
	 *            the instance's index (index starts with 0)
	 * @return the instance at the given position
	 */
	// @ requires 0 <= index;
	// @ requires index < numInstances();
	public ProcessInstance getInstance(int index) {

		return (ProcessInstance) m_Instances.elementAt(index);
	}

	/**
	 * Returns the last instance in the set.
	 * 
	 * @return the last instance in the set
	 */
	// @ requires numInstances() > 0;
	public/* @non_null pure@ */ProcessInstance getLastInstance() {

		return (ProcessInstance) m_Instances.lastElement();
	}

	/**
	 * Returns the number of distinct values of a given attribute. The value
	 * 'missing' is not counted.
	 * 
	 * @param attIndex
	 *            the attribute (index starts with 0)
	 * @return the number of distinct values of a given attribute
	 */
	// @ requires 0 <= attIndex;
	// @ requires attIndex < numAttributes();
	public/* @pure@ */int numDistinctValues(int attIndex) {

		if (attribute(attIndex).isNumeric()) {
			double[] attVals = attributeToDoubleArray(attIndex);
			int[] sorted = Utils.sort(attVals);
			double prev = 0;
			int counter = 0;
			for (int i = 0; i < sorted.length; i++) {
				ProcessInstance current = getInstance(sorted[i]);
				if (current.isMissing(attIndex)) {
					break;
				}
				if ((i == 0) || (current.value(attIndex) > prev)) {
					prev = current.value(attIndex);
					counter++;
				}
			}
			return counter;
		} 
		
		//same for string values
		String[] strAttVals = attributeToStringArray(attIndex);
		int[] sorted = new int[strAttVals.length];
		for (int i = 0; i < strAttVals.length; i++){
			sorted[i] = i;
		}
		String prev = "";
		int counter = 0;
		for (int i = 0; i < sorted.length; i++) {
			ProcessInstance current = getInstance(sorted[i]);
			if (current.isMissing(attIndex)) {
				break;
			}
			if ((i == 0) || (!current.stringValue(attIndex).equals(prev))) {
				prev = current.stringValue(attIndex);
				counter++;
			}
		}
		return counter;
	}

	
	/**
	   * Gets the value of all instances in this dataset for a particular
	   * string attribute. Useful in conjunction with Utils.sort to allow iterating
	   * through the dataset in sorted order for some attribute.
	   *
	   * @param index the index of the attribute.
	   * @return an array containing the value of the desired attribute for
	   * each instance in the dataset.
	   */
	  //@ requires 0 <= index && index < numAttributes();
	  public /*@pure@*/ String [] attributeToStringArray(int index) {

	    String [] result = new String[numInstances()];
	    for (int i = 0; i < result.length; i++) {
	      result[i] = instance(i).stringValue(index);
	    }
	    return result;
	  }
	  
	/**
	 * Reads a single instance from the reader and appends it to the dataset.
	 * Automatically expands the dataset if it is not large enough to hold the
	 * instance. This method does not check for carriage return at the end of
	 * the line.
	 * 
	 * @param reader
	 *            the reader
	 * @return false if end of file has been reached
	 * @throws IOException
	 *             if the information is not read successfully
	 * @deprecated instead of using this method in conjunction with the
	 *             <code>readInstance(Reader)</code> method, one should use the
	 *             <code>ArffLoader</code> or <code>DataSource</code> class
	 *             instead.
	 * @see weka.core.converters.ArffLoader
	 * @see weka.core.converters.ConverterUtils.DataSource
	 */
	@Deprecated
	public boolean readInstance(Reader reader) throws IOException {

		ArffReader arff = new ArffReader(reader, this, m_Lines, 1);
		ProcessInstance inst = (ProcessInstance) arff.readInstance(
				arff.getData(), false);
		m_Lines = arff.getLineNo();
		if (inst != null) {
			addInstance(inst);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a new dataset of the same size using random sampling with
	 * replacement.
	 * 
	 * @param random
	 *            a random number generator
	 * @return the new dataset
	 */
	public ProcessInstances resample(Random random) {

		ProcessInstances newData = new ProcessInstances(this, numInstances());
		while (newData.numInstances() < numInstances()) {
			newData.addInstance(getInstance(random.nextInt(numInstances())));
		}
		return newData;
	}

	/**
	 * Creates a new dataset of the same size using random sampling with
	 * replacement according to the current instance weights. The weights of the
	 * instances in the new dataset are set to one.
	 * 
	 * @param random
	 *            a random number generator
	 * @return the new dataset
	 */
	public ProcessInstances resampleWithWeights(Random random) {

		double[] weights = new double[numInstances()];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = getInstance(i).weight();
		}
		return resampleWithWeights(random, weights);
	}

	/**
	 * Creates a new dataset of the same size using random sampling with
	 * replacement according to the given weight vector. The weights of the
	 * instances in the new dataset are set to one. The length of the weight
	 * vector has to be the same as the number of instances in the dataset, and
	 * all weights have to be positive.
	 * 
	 * @param random
	 *            a random number generator
	 * @param weights
	 *            the weight vector
	 * @return the new dataset
	 * @throws IllegalArgumentException
	 *             if the weights array is of the wrong length or contains
	 *             negative weights.
	 */
	public ProcessInstances resampleWithWeights(Random random, double[] weights) {

		if (weights.length != numInstances()) {
			throw new IllegalArgumentException(
					"weights.length != numInstances.");
		}
		ProcessInstances newData = new ProcessInstances(this, numInstances());
		if (numInstances() == 0) {
			return newData;
		}
		double[] probabilities = new double[numInstances()];
		double sumProbs = 0, sumOfWeights = Utils.sum(weights);
		for (int i = 0; i < numInstances(); i++) {
			sumProbs += random.nextDouble();
			probabilities[i] = sumProbs;
		}
		Utils.normalize(probabilities, sumProbs / sumOfWeights);

		// Make sure that rounding errors don't mess things up
		probabilities[numInstances() - 1] = sumOfWeights;
		int k = 0;
		int l = 0;
		sumProbs = 0;
		while ((k < numInstances() && (l < numInstances()))) {
			if (weights[l] < 0) {
				throw new IllegalArgumentException(
						"Weights have to be positive.");
			}
			sumProbs += weights[l];
			while ((k < numInstances()) && (probabilities[k] <= sumProbs)) {
				newData.addInstance(getInstance(l));
				newData.getInstance(k).setWeight(1);
				k++;
			}
			l++;
		}
		return newData;
	}

	/**
	 * Stratifies a set of instances according to its class values if the class
	 * attribute is nominal (so that afterwards a stratified cross-validation
	 * can be performed).
	 * 
	 * @param numFolds
	 *            the number of folds in the cross-validation
	 * @throws UnassignedClassException
	 *             if the class is not set
	 */
	public void stratify(int numFolds) {

		if (numFolds <= 1) {
			throw new IllegalArgumentException(
					"Number of folds must be greater than 1");
		}
		if (m_ClassIndex < 0) {
			throw new UnassignedClassException(
					"Class index is negative (not set)!");
		}
		if (classAttribute().isNominal()) {

			// sort by class
			int index = 1;
			while (index < numInstances()) {
				ProcessInstance instance1 = getInstance(index - 1);
				for (int j = index; j < numInstances(); j++) {
					ProcessInstance instance2 = getInstance(j);
					if ((instance1.classValue() == instance2.classValue())
							|| (instance1.classIsMissing() && instance2
									.classIsMissing())) {
						swap(index, j);
						index++;
					}
				}
				index++;
			}
			stratStep(numFolds);
		}
	}

	/**
	 * Creates the test set for one fold of a cross-validation on the dataset.
	 * 
	 * @param numFolds
	 *            the number of folds in the cross-validation. Must be greater
	 *            than 1.
	 * @param numFold
	 *            0 for the first fold, 1 for the second, ...
	 * @return the test set as a set of weighted instances
	 * @throws IllegalArgumentException
	 *             if the number of folds is less than 2 or greater than the
	 *             number of instances.
	 */
	// @ requires 2 <= numFolds && numFolds < numInstances();
	// @ requires 0 <= numFold && numFold < numFolds;
	public ProcessInstances testCV(int numFolds, int numFold) {

		int numInstForFold, first, offset;
		ProcessInstances test;

		if (numFolds < 2) {
			throw new IllegalArgumentException(
					"Number of folds must be at least 2!");
		}
		if (numFolds > numInstances()) {
			throw new IllegalArgumentException(
					"Can't have more folds than instances!");
		}
		numInstForFold = numInstances() / numFolds;
		if (numFold < numInstances() % numFolds) {
			numInstForFold++;
			offset = numFold;
		} else
			offset = numInstances() % numFolds;
		test = new ProcessInstances(this, numInstForFold);
		first = numFold * (numInstances() / numFolds) + offset;
		copyInstances(first, test, numInstForFold);
		return test;
	}

	/**
	 * Creates the training set for one fold of a cross-validation on the
	 * dataset.
	 * 
	 * @param numFolds
	 *            the number of folds in the cross-validation. Must be greater
	 *            than 1.
	 * @param numFold
	 *            0 for the first fold, 1 for the second, ...
	 * @return the training set
	 * @throws IllegalArgumentException
	 *             if the number of folds is less than 2 or greater than the
	 *             number of instances.
	 */
	// @ requires 2 <= numFolds && numFolds < numInstances();
	// @ requires 0 <= numFold && numFold < numFolds;
	public ProcessInstances trainCV(int numFolds, int numFold) {

		int numInstForFold, first, offset;
		ProcessInstances train;

		if (numFolds < 2) {
			throw new IllegalArgumentException(
					"Number of folds must be at least 2!");
		}
		if (numFolds > numInstances()) {
			throw new IllegalArgumentException(
					"Can't have more folds than instances!");
		}
		numInstForFold = numInstances() / numFolds;
		if (numFold < numInstances() % numFolds) {
			numInstForFold++;
			offset = numFold;
		} else
			offset = numInstances() % numFolds;
		train = new ProcessInstances(this, numInstances() - numInstForFold);
		first = numFold * (numInstances() / numFolds) + offset;
		copyInstances(0, train, first);
		copyInstances(first + numInstForFold, train, numInstances() - first
				- numInstForFold);

		return train;
	}

	/**
	 * Creates the training set for one fold of a cross-validation on the
	 * dataset. The data is subsequently randomized based on the given random
	 * number generator.
	 * 
	 * @param numFolds
	 *            the number of folds in the cross-validation. Must be greater
	 *            than 1.
	 * @param numFold
	 *            0 for the first fold, 1 for the second, ...
	 * @param random
	 *            the random number generator
	 * @return the training set
	 * @throws IllegalArgumentException
	 *             if the number of folds is less than 2 or greater than the
	 *             number of instances.
	 */
	// @ requires 2 <= numFolds && numFolds < numInstances();
	// @ requires 0 <= numFold && numFold < numFolds;
	public ProcessInstances trainCV(int numFolds, int numFold, Random random) {

		ProcessInstances train = trainCV(numFolds, numFold);
		train.randomize(random);
		return train;
	}

	/**
	 * Copies instances from one set to the end of another one.
	 * 
	 * @param from
	 *            the position of the first instance to be copied
	 * @param dest
	 *            the destination for the instances
	 * @param num
	 *            the number of instances to be copied
	 */
	// @ requires 0 <= from && from <= numInstances() - num;
	// @ requires 0 <= num;
	protected void copyInstances(int from, /* @non_null@ */
			ProcessInstances dest, int num) {

		for (int i = 0; i < num; i++) {
			dest.addInstance(getInstance(from + i));
		}
	}

	/**
	 * Merges two sets of ProcessInstances together. The resulting set will have
	 * all the attributes of the first set plus all the attributes of the second
	 * set. The number of instances in both sets must be the same.
	 * 
	 * @param first
	 *            the first set of ProcessInstances
	 * @param second
	 *            the second set of ProcessInstances
	 * @return the merged set of ProcessInstances
	 * @throws IllegalArgumentException
	 *             if the datasets are not the same size
	 */
	public static ProcessInstances mergeInstances(ProcessInstances first,
			ProcessInstances second) {

		if (first.numInstances() != second.numInstances()) {
			throw new IllegalArgumentException(
					"Instance sets must be of the same size");
		}

		// Create the vector of merged attributes
		FastVector newAttributes = new FastVector();
		for (int i = 0; i < first.numAttributes(); i++) {
			newAttributes.addElement(first.attribute(i));
		}
		for (int i = 0; i < second.numAttributes(); i++) {
			newAttributes.addElement(second.attribute(i));
		}
		
		FastVector newStrAttributes = new FastVector();
		for (int i = 0; i < first.numStrAttributes(); i++) {
			newStrAttributes.addElement(first.strAttribute(i));
		}
		for (int i = 0; i < second.numStrAttributes(); i++) {
			newStrAttributes.addElement(second.strAttribute(i));
		}
		// Create the set of ProcessInstances
		ProcessInstances merged = new ProcessInstances(first.relationName()
				+ '_' + second.relationName(), newAttributes, newStrAttributes,
				first.numInstances());
		// Merge each instance
		for (int i = 0; i < first.numInstances(); i++) {
			merged.addInstance(first.getInstance(i).mergeInstance(second.getInstance(i)));
		}
		return merged;
	}
	
	 /**
	   * Returns string attribute at a given index.
	   *
	   * @param index the string attribute's index (index starts with 0)
	   * @return the string attribute at the given position
	   */
	  //@ requires 0 <= index;
	  //@ requires index < m_Attributes.size();
	  //@ ensures \result != null;
	  public /*@pure@*/ Attribute strAttribute(int index) {

	    return (Attribute) m_String_Attributes.elementAt(index);
	  }

	/**
	 * Returns the instances		 
	 * @return the instances
	 */
	public FastVector getInstances(){
		return m_Instances;
	}
	
	/**
	 * Returns the numeric attributes
	 * @return the numeric attributes
	 */
	public FastVector getAttributes(){
		return m_Attributes;
	}
	
	/**
	 * Returns the string attributes
	 * @return the string attributes
	 */
	public FastVector getStringAttributes(){
		return m_String_Attributes;
	}
	
	/**
	 * Method for testing this class.
	 * 
	 * @param argv
	 *            should contain one element: the name of an ARFF file
	 */
	// @ requires argv != null;
	// @ requires argv.length == 1;
	// @ requires argv[0] != null;
	public static void test(String[] argv) {

		ProcessInstances instances, secondInstances, train, test, empty;
		Random random = new Random(2);
		Reader reader;
		int start, num;
		FastVector testAtts, testVals;
		int i, j;

		try {
			if (argv.length > 1) {
				throw (new Exception("Usage: ProcessInstances [<filename>]"));
			}

			// Creating set of instances from scratch
			testVals = new FastVector(2);
			testVals.addElement("first_value");
			testVals.addElement("second_value");
			testAtts = new FastVector(2);
			testAtts.addElement(new Attribute("nominal_attribute", testVals));
			testAtts.addElement(new Attribute("numeric_attribute"));
			instances = new ProcessInstances("test_set", testAtts, new FastVector(), 10);
			instances.addInstance(new ProcessInstance(instances.numAttributes()));
			instances.addInstance(new ProcessInstance(instances.numAttributes()));
			instances.addInstance(new ProcessInstance(instances.numAttributes()));
			instances.setClassIndex(0);
			System.out.println("\nSet of instances created from scratch:\n");
			System.out.println(instances);

			if (argv.length == 1) {
				String filename = argv[0];
				reader = new FileReader(filename);

				// Read first five instances and print them
				System.out.println("\nFirst five instances from file:\n");
				instances = new ProcessInstances(reader, 1);
				instances.setClassIndex(instances.numAttributes() - 1);
				i = 0;
				while ((i < 5) && (instances.readInstance(reader))) {
					i++;
				}
				System.out.println(instances);

				// Read all the instances in the file
				reader = new FileReader(filename);
				instances = new ProcessInstances(reader);

				// Make the last attribute be the class
				instances.setClassIndex(instances.numAttributes() - 1);

				// Print header and instances.
				System.out.println("\nDataset:\n");
				System.out.println(instances);
				System.out.println("\nClass index: " + instances.classIndex());
			}

			// Test basic methods based on class index.
			System.out.println("\nClass name: "
					+ instances.classAttribute().name());
			System.out.println("\nClass index: " + instances.classIndex());
			System.out.println("\nClass is nominal: "
					+ instances.classAttribute().isNominal());
			System.out.println("\nClass is numeric: "
					+ instances.classAttribute().isNumeric());
			System.out.println("\nClasses:\n");
			for (i = 0; i < instances.numClasses(); i++) {
				System.out.println(instances.classAttribute().value(i));
			}
			System.out.println("\nClass values and labels of instances:\n");
			for (i = 0; i < instances.numInstances(); i++) {
				ProcessInstance inst = instances.getInstance(i);
				System.out.print(inst.classValue() + "\t");
				System.out.print(inst.toString(inst.classIndex()));
				if (instances.getInstance(i).classIsMissing()) {
					System.out.println("\tis missing");
				} else {
					System.out.println();
				}
			}

			// Create random weights.
			System.out.println("\nCreating random weights for instances.");
			for (i = 0; i < instances.numInstances(); i++) {
				instances.getInstance(i).setWeight(random.nextDouble());
			}

			// Print all instances and their weights (and the sum of weights).
			System.out.println("\nInstances and their weights:\n");
			System.out.println(instances.instancesAndWeights());
			System.out.print("\nSum of weights: ");
			System.out.println(instances.sumOfWeights());

			// Insert an attribute
			secondInstances = new ProcessInstances(instances);
			Attribute testAtt = new Attribute("Inserted");
			secondInstances.insertAttributeAt(testAtt, 0);
			System.out.println("\nSet with inserted attribute:\n");
			System.out.println(secondInstances);
			System.out.println("\nClass name: "
					+ secondInstances.classAttribute().name());

			// Delete the attribute
			secondInstances.deleteAttributeAt(0);
			System.out.println("\nSet with attribute deleted:\n");
			System.out.println(secondInstances);
			System.out.println("\nClass name: "
					+ secondInstances.classAttribute().name());

			// Test if headers are equal
			System.out.println("\nHeaders equal: "
					+ instances.equalHeaders(secondInstances) + "\n");

			// Print data in internal format.
			System.out.println("\nData (internal values):\n");
			for (i = 0; i < instances.numInstances(); i++) {
				for (j = 0; j < instances.numAttributes(); j++) {
					if (instances.getInstance(i).isMissing(j)) {
						System.out.print("? ");
					} else {
						System.out.print(instances.getInstance(i).value(j) + " ");
					}
				}
				System.out.println();
			}

			// Just print header
			System.out.println("\nEmpty dataset:\n");
			empty = new ProcessInstances(instances, 0);
			System.out.println(empty);
			System.out
					.println("\nClass name: " + empty.classAttribute().name());

			// Create copy and rename an attribute and a value (if possible)
			if (empty.classAttribute().isNominal()) {
				Instances copy = new ProcessInstances(empty, 0);
				copy.renameAttribute(copy.classAttribute(), "new_name");
				copy.renameAttributeValue(copy.classAttribute(), copy
						.classAttribute().value(0), "new_val_name");
				System.out.println("\nDataset with names changed:\n" + copy);
				System.out.println("\nOriginal dataset:\n" + empty);
			}

			// Create and prints subset of instances.
			start = instances.numInstances() / 4;
			num = instances.numInstances() / 2;
			System.out.print("\nSubset of dataset: ");
			System.out.println(num + " instances from " + (start + 1)
					+ ". instance");
			secondInstances = new ProcessInstances(instances, start, num);
			System.out.println("\nClass name: "
					+ secondInstances.classAttribute().name());

			// Print all instances and their weights (and the sum of weights).
			System.out.println("\nInstances and their weights:\n");
			System.out.println(secondInstances.instancesAndWeights());
			System.out.print("\nSum of weights: ");
			System.out.println(secondInstances.sumOfWeights());

			// Create and print training and test sets for 3-fold
			// cross-validation.
			System.out.println("\nTrain and test folds for 3-fold CV:");
			if (instances.classAttribute().isNominal()) {
				instances.stratify(3);
			}
			for (j = 0; j < 3; j++) {
				train = instances.trainCV(3, j, new Random(1));
				test = instances.testCV(3, j);

				// Print all instances and their weights (and the sum of
				// weights).
				System.out.println("\nTrain: ");
				System.out.println("\nInstances and their weights:\n");
				System.out.println(train.instancesAndWeights());
				System.out.print("\nSum of weights: ");
				System.out.println(train.sumOfWeights());
				System.out.println("\nClass name: "
						+ train.classAttribute().name());
				System.out.println("\nTest: ");
				System.out.println("\nInstances and their weights:\n");
				System.out.println(test.instancesAndWeights());
				System.out.print("\nSum of weights: ");
				System.out.println(test.sumOfWeights());
				System.out.println("\nClass name: "
						+ test.classAttribute().name());
			}

			// Randomize instances and print them.
			System.out.println("\nRandomized dataset:");
			instances.randomize(random);

			// Print all instances and their weights (and the sum of weights).
			System.out.println("\nInstances and their weights:\n");
			System.out.println(instances.instancesAndWeights());
			System.out.print("\nSum of weights: ");
			System.out.println(instances.sumOfWeights());

			// Sort instances according to first attribute and
			// print them.
			System.out
					.print("\nInstances sorted according to first attribute:\n ");
			instances.sort(0);

			// Print all instances and their weights (and the sum of weights).
			System.out.println("\nInstances and their weights:\n");
			System.out.println(instances.instancesAndWeights());
			System.out.print("\nSum of weights: ");
			System.out.println(instances.sumOfWeights());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method for this class. The following calls are possible:
	 * <ul>
	 * <li>
	 * <code>weka.core.Instances</code> help<br/>
	 * prints a short list of possible commands.</li>
	 * <li>
	 * <code>weka.core.Instances</code> &lt;filename&gt;<br/>
	 * prints a summary of a set of instances.</li>
	 * <li>
	 * <code>weka.core.Instances</code> merge &lt;filename1&gt;
	 * &lt;filename2&gt;<br/>
	 * merges the two datasets (must have same number of instances) and outputs
	 * the results on stdout.</li>
	 * <li>
	 * <code>weka.core.Instances</code> append &lt;filename1&gt;
	 * &lt;filename2&gt;<br/>
	 * appends the second dataset to the first one (must have same headers) and
	 * outputs the results on stdout.</li>
	 * <li>
	 * <code>weka.core.Instances</code> headers &lt;filename1&gt;
	 * &lt;filename2&gt;<br/>
	 * Compares the headers of the two datasets and prints whether they match or
	 * not.</li>
	 * <li>
	 * <code>weka.core.Instances</code> randomize &lt;seed&gt; &lt;filename&gt;<br/>
	 * randomizes the dataset with the given seed and outputs the result on
	 * stdout.</li>
	 * </ul>
	 * 
	 * @param args
	 *            the commandline parameters
	 */
	public static void main(String[] args) {

		try {
			ProcessInstances i;
			// read from stdin and print statistics
			if (args.length == 0) {
				DataSource source = new DataSource(System.in);
				i = (ProcessInstances) source.getDataSet();
				System.out.println(i.toSummaryString());
			}
			// read file and print statistics
			else if ((args.length == 1) && (!args[0].equals("-h"))
					&& (!args[0].equals("help"))) {
				DataSource source = new DataSource(args[0]);
				i = (ProcessInstances) source.getDataSet();
				System.out.println(i.toSummaryString());
			}
			// read two files, merge them and print result to stdout
			else if ((args.length == 3)
					&& (args[0].toLowerCase().equals("merge"))) {
				DataSource source1 = new DataSource(args[1]);
				DataSource source2 = new DataSource(args[2]);
				i = ProcessInstances.mergeInstances(
						(ProcessInstances) source1.getDataSet(),
						(ProcessInstances) source2.getDataSet());
				System.out.println(i);
			}
			// read two files, append them and print result to stdout
			else if ((args.length == 3)
					&& (args[0].toLowerCase().equals("append"))) {
				DataSource source1 = new DataSource(args[1]);
				DataSource source2 = new DataSource(args[2]);
				if (!source1.getStructure()
						.equalHeaders(source2.getStructure()))
					throw new Exception(
							"The two datasets have different headers!");
				Instances structure = source1.getStructure();
				System.out.println(source1.getStructure());
				while (source1.hasMoreElements(structure))
					System.out.println(source1.nextElement(structure));
				structure = source2.getStructure();
				while (source2.hasMoreElements(structure))
					System.out.println(source2.nextElement(structure));
			}
			// read two files and compare their headers
			else if ((args.length == 3)
					&& (args[0].toLowerCase().equals("headers"))) {
				DataSource source1 = new DataSource(args[1]);
				DataSource source2 = new DataSource(args[2]);
				if (source1.getStructure().equalHeaders(source2.getStructure()))
					System.out.println("Headers match");
				else
					System.out.println("Headers don't match");
			}
			// read file and seed value, randomize data and print result to
			// stdout
			else if ((args.length == 3)
					&& (args[0].toLowerCase().equals("randomize"))) {
				DataSource source = new DataSource(args[2]);
				i = (ProcessInstances) source.getDataSet();
				i.randomize(new Random(Integer.parseInt(args[1])));
				System.out.println(i);
			}
			// wrong parameters
			else {
				System.err
						.println("\nUsage:\n"
								+ "\tweka.core.Instances help\n"
								+ "\tweka.core.Instances <filename>\n"
								+ "\tweka.core.Instances merge <filename1> <filename2>\n"
								+ "\tweka.core.Instances append <filename1> <filename2>\n"
								+ "\tweka.core.Instances headers <filename1> <filename2>\n"
								+ "\tweka.core.Instances randomize <seed> <filename>\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		}
	}

}
