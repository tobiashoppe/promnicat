package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.labeling;

/**
 * A simple cluster labeling implementation. Takes the interval values from the numeric features and the 
 * two most frequent words from the string features and combines them to a label
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import weka.core.Stopwords;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ClusterNode;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstance;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstances;
import de.uni_potsdam.hpi.bpt.promnicat.util.Range;

public class ClusterLabeler {

	public ExternalResourceEnhancer enhancer;

	/**
	 * list of special characters for filtering the document corpus
	 */
	public String[] specialChars = {"/", "\\", "(", ")", "+", " - ", "[", "]", "{", "}"}; 
	
	/**
	 * Contains the stopword list
	 */
	public Stopwords stopper = new Stopwords();
	
	
	/**
	 * Constructor for cluster labeler. You can assign an enhancer, but it is not used yet
	 * at this class.
	 * @param enhancer
	 */
	public ClusterLabeler(ExternalResourceEnhancer enhancer){
		this.enhancer = enhancer;
	}
	
	/**
	 * Setter for external resources enhancer.
	 * @param newEnhancer
	 */
	public void setEnhancer(ExternalResourceEnhancer newEnhancer){
		enhancer = newEnhancer;
	}
	
	/**
	 * Getter for external resources enhancer.
	 * @return
	 */
	public ExternalResourceEnhancer getEnhancer(){
		return enhancer;
	}
	
	/**
	 * The labeling method that is called to trigger the labeling process for a cluster.
	 * @param cluster to label
	 */
	public void assignLabelToCluster(ClusterNode<ProcessInstances> cluster) {

		ArrayList<HashMap<String, Object>> childClusterNames = new ArrayList<HashMap<String, Object>>();
		// assign names to child clusters
		assignLabelsToChildren(cluster, childClusterNames);

		if (childClusterNames.size() == 0) {
			// no children, means leaf
			labelAsLeaf(cluster);
		} else {
			labelAsCluster(cluster, childClusterNames);
		}
		cluster.setName(getClusterNameString(cluster));
	}
	
	/**
	 * Labels the cluster as intermediate cluster by collecting and examining all information
	 * from the child clusters
	 * @param cluster to label
	 * @param childClusterNames contains all information primarily collected from the child 
	 * clusters
	 */
	@SuppressWarnings("unchecked")
	public void labelAsCluster(ClusterNode<ProcessInstances> cluster, ArrayList<HashMap<String, Object>> childClusterNames){
		for (HashMap<String, Object> featureNames : childClusterNames) {
			for (String key : featureNames.keySet()) {
				if (featureNames.get(key) instanceof Range) {
					labelNumericFromChildClusterNames(cluster, key,
							(Range) featureNames.get(key));
				} else {
					labelStringFromChildClusterNames(cluster, key,
							(HashMap<String, Double>) featureNames
									.get(key));
				}
			}
		}
	}
	
	/**
	 * Labels the cluster as leaf cluster by examining the concrete data values
	 * @param cluster to label
	 */
	public void labelAsLeaf(ClusterNode<ProcessInstances> cluster){
		for (int i = 0; i < cluster.data.numInstances(); i++) {
			ProcessInstance inst = cluster.data.getInstance(i);
			for (int j = 0; j < cluster.data.numAttributes(); j++) {
				labelNumericFeatureForCluster(cluster, j, inst.value(j));
			}
			for (int j = 0; j < cluster.data.numStrAttributes(); j++) {
				String labels = inst.process.getDescription() + " ";
				labels += inst.process.getName() + " ";
				labels += inst.strValue(j);
				labelStringFeatureForCluster(cluster, j, labels);
			}
		}	
	}
	
	/**
	 * Assign labels to all the cluster's children and collect their labeling infos
	 * @param cluster whose children to label
	 * @param childClusterNames contains the cluster label details of all child clusters
	 */
	public void assignLabelsToChildren(ClusterNode<ProcessInstances> cluster, ArrayList<HashMap<String, Object>> childClusterNames){
		for (ClusterNode<ProcessInstances> element : cluster.children) {
			assignLabelToCluster(element);
			childClusterNames.add(element.getClusterName());
		}
	}

	/**
	 * Transform the different feature value names into a string representation
	 * 
	 * @return a String representation of the the cluster name
	 */
	public String getClusterNameString(ClusterNode<ProcessInstances> cluster) {
		HashMap<String, Object> clusterName = cluster.getClusterName();
		StringBuilder name = new StringBuilder();
		for (Entry<String, Object> feature : clusterName.entrySet()) {
			name.append(feature.getKey()).append(": ");
			if (feature.getValue() instanceof Range) {//append interval for numeric feature
				Range interval = (Range) feature.getValue();
				name.append(interval.getMinValue()).append("-")
						.append(interval.getMaxValue()).append(" ");
			} else {//append label for string feature
				name.append(selectLabels(cluster, feature));
			}
			name.append(";");
		}
		return name.toString();
	}
	
	/**
	 * Does the actual cluster labeling. Calculates the score for each word in the cluster
	 * and selects the top 2 labels.
	 * @param cluster to label
	 * @param feature after which to label
	 * @return the final cluster label
	 */
	public String selectLabels(ClusterNode<ProcessInstances> cluster, Entry<String, Object> feature){
		return selectTopWordsAsLabel(feature);
	}
	
	/**
	 * Selects the 2 words with the best scores for cluster label 
	 * @param feature
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String selectTopWordsAsLabel(Entry<String, Object> feature){
		StringBuilder name = new StringBuilder();
		HashMap<String, Double> labelNames = (HashMap<String, Double>) feature
				.getValue();
		// find out the ones with highest indices
		Entry<String, Double> maxValue = new HashMap.SimpleEntry<String, Double>(
				"", 0.0);
		Entry<String, Double> secondMaxValue = new HashMap.SimpleEntry<String, Double>(
				"", 0.0);
		for (Entry<String, Double> entry : labelNames.entrySet()) {
			if (entry.getValue() >= maxValue.getValue()) {
				secondMaxValue = maxValue;
				maxValue = entry;
			} else {
				if ((entry.getValue() <= maxValue.getValue())
						&& (entry.getValue() >= secondMaxValue
								.getValue())) {
					secondMaxValue = entry;
				}
			}
		}
		name.append(maxValue.getKey()).append(" ").append(secondMaxValue.getKey()).append(" ");
		
		return name.toString();
	}
	
	/**
	 * Calculates the label name of a numeric feature value
	 * @param cluster the current cluster that is to be labeled
	 * @param index of the current numeric attribute/feature
	 * @param number the value of the current numeric attribute/feature
	 */
	public void labelNumericFeatureForCluster(ClusterNode<ProcessInstances> cluster,
			int index, double number) {
		String feature = cluster.data.attribute(index).name();
		labelNumericFeature(cluster, feature, number);
	}

	/**
	 * Updates the features' interval according to a newly added number
	 * @param cluster
	 * @param key
	 * @param number
	 */
	public void labelNumericFeature(ClusterNode<ProcessInstances> cluster,
			String key, double number) {

		if (cluster.clusterName.get(key) == null) {
			cluster.clusterName.put(key, new Range(number));
		} else {
			((Range) cluster.clusterName.get(key)).updateRange(number);
		}
	}

	/**
	 * Updated the features' interval by merging it with a new interval
	 * @param cluster
	 * @param key
	 * @param newInterval
	 */
	public void labelNumericFromChildClusterNames(ClusterNode<ProcessInstances> cluster,
			String key, Range newInterval) {

		if (cluster.clusterName.get(key) == null) {
			cluster.clusterName.put(key, newInterval.copy());
		} else {
			((Range) cluster.clusterName.get(key)).updateRange(newInterval);
		}
	}

	
	/**
	 * Merges two cluster frequency counts with each other
	 * @param cluster
	 * @param key for the string attribute
	 * @param newStringMap
	 */
	@SuppressWarnings("unchecked")
	public void labelStringFromChildClusterNames(ClusterNode<ProcessInstances> cluster,
			String key, HashMap<String, Double> newStringMap) {
		if (cluster.clusterName.get(key) == null) {
			cluster.clusterName.put(key, new HashMap<String, Double>(
					newStringMap));
		} else {// merge with current string hashmap
			HashMap<String, Double> stringFeature = (HashMap<String, Double>) cluster.clusterName
					.get(key);
			for (String labelKey : newStringMap.keySet()) {
				if (stringFeature.keySet().contains(labelKey)) {
					stringFeature.put(labelKey, stringFeature.get(labelKey)
							+ newStringMap.get(labelKey));
				} else {
					stringFeature.put(labelKey, newStringMap.get(labelKey));
				}
			}
		}
	}

	/**
	 * Counts the frequency of all words in the cluster.
	 */
	@SuppressWarnings("unchecked")
	public void labelStringFeatureForCluster(ClusterNode<ProcessInstances> cluster,
			int index, String labelName) {
		
		String key = cluster.data.strAttribute(index).name();
		if (cluster.clusterName.get(key) == null) {
			cluster.clusterName.put(key, new HashMap<String, Double>());
		}
		//store the frequency counts for all words in that cluster
		HashMap<String, Double> labelNames = (HashMap<String, Double>) cluster.clusterName
				.get(key);
		String[] words = processCorpus(labelName);
		countFrequencies(words, labelNames);
	}
	
	/**
	 * Iterate over the words of the given corpus and count their frequencies
	 * @param corpus
	 * @param labelNames
	 */
	public void countFrequencies(String[] corpus, HashMap<String, Double> labelNames){
		for (String word : corpus) {
			if ((!word.equals("")) && (!stopper.is(word))) {
				if (labelNames.keySet().contains(word)) {// already existing,
															// count 1 up
					labelNames.put(word, labelNames.get(word) + 1.0);
				} else {
					labelNames.put(word, 1.0);
				}
			}
		}
	}
	
	/**
	 * Cleans up and splits a given corpus by its spaces.
	 * @param corpus the corpus to clean
	 * @return the filtered corpus split by spaces
	 */
	public String[] processCorpus(String corpus){
		corpus = corpus.toLowerCase();
		//remove all special characters such as (,),/, ...
		for (int i = 0; i < specialChars.length; i++){
			corpus = corpus.replace(specialChars[i], "");
		}
		return corpus.split(" ");
	}

}
