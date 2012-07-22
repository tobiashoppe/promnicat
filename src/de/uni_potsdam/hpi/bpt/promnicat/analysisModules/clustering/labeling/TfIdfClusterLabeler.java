/**
 * 
 */
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.labeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ClusterNode;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstance;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstances;

/**
 * Labels clusters using the tf/idf metric.
 * @author Cindy Fähnrich
 *
 */
public class TfIdfClusterLabeler extends ClusterLabeler {

	/**
	 * A hashmap with words as keys and a list of document ids where they occur
	 */
	HashMap<String, List<Integer>> globalWordOccurences = new HashMap<String, List<Integer>>();
	/**
	 * the root where to start the labeling
	 */
	ClusterNode<ProcessInstances> rootNode;
	
	/**
	 * Returns the root node at which to start cluster labeling.
	 * @return the current set root node
	 */
	public ClusterNode<ProcessInstances> getRootNode() {
		return rootNode;
	}

	/**
	 * Constructor for cluster labeler.
	 * @param enhancer ExternalResourceEnhancer 
	 */
	public TfIdfClusterLabeler(ExternalResourceEnhancer enhancer){
		super(enhancer);
	}
	
	/**
	 * Sets the root node with which to start clustering. Enables you to also label only a 
	 * subtree of a ClusterTree.
	 * @param rootNode
	 */
	public void setRootNode(ClusterNode<ProcessInstances> rootNode) {
		this.rootNode = rootNode;
	}
	
	/**
	 * Labels the cluster as intermediate cluster by collecting and examining all information
	 * from the child clusters
	 * @param cluster to label
	 * @param childClusterNames contains all information primarily collected from the child 
	 * clusters
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void labelAsCluster(ClusterNode<ProcessInstances> cluster, ArrayList<HashMap<String, Object>> childClusterNames){
		for (HashMap<String, Object> featureNames : childClusterNames) {
			for (String key : featureNames.keySet()) {
				if (featureNames.get(key) instanceof HashMap<?, ?>) {
					labelStringFromChildClusterNames(cluster, key, (HashMap<String, Double>) featureNames.get(key));
				}
			}
		}
	}
	
	/**
	 * Labels the cluster as leaf cluster by examining the concrete data values
	 * @param cluster to label
	 */
	@Override
	public void labelAsLeaf(ClusterNode<ProcessInstances> cluster){
		for (int i = 0; i < cluster.data.numInstances(); i++) {
			ProcessInstance inst = cluster.data.getInstance(i);
			
			/*optional: for also incorporating numerical feature values for lableing
			  for (int j = 0; j < cluster.data.numAttributes(); j++) {
				labelNumericFeatureForCluster(cluster, j, inst.value(j));
			}*/
			
			for (int j = 0; j < cluster.data.numStrAttributes(); j++) {
				String labels = inst.process.getDescription() + " ";
				labels += inst.process.getName() + " ";
				labels += inst.strValue(j);
				labelStringFeatureForCluster(cluster, j, labels);
			}
		}
	}

	/**
	 * Calculates the label name of a numeric feature value
	 * @param cluster the current cluster that is to be labeled
	 * @param index of the current numeric attribute/feature
	 * @param number the value of the current numeric attribute/feature
	 */
	@Override
	@SuppressWarnings("unused")
	public void labelNumericFeatureForCluster(ClusterNode<ProcessInstances> cluster,
			int index, double number) {
		String feature = cluster.data.attribute(index).name();
		//updateFeatureLabel(cluster, feature, number);
	}
	
	/**
	 * Merges two cluster frequency counts with each other
	 * @param cluster
	 * @param key for the string attribute
	 * @param newStringMap
	 */
	@Override
	public void labelStringFromChildClusterNames(ClusterNode<ProcessInstances> cluster,
			String key, HashMap<String, Double> newStringMap) {
		if (cluster.clusterName.get(key) == null) {
			cluster.clusterName.put(key, new HashMap<String, Double>(
					newStringMap));
		} else {// merge with current string hashmap
			mergeFeatureMaps(key, cluster, newStringMap);
		}
	}
	
	/**
	 * Does the actual merge of two cluster frequency counts
	 * @param key for the string attribute
	 * @param cluster
	 * @param newStringMap
	 */
	@SuppressWarnings("unchecked")
	public void mergeFeatureMaps(String key, ClusterNode<ProcessInstances> cluster, HashMap<String, Double> newStringMap){
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
	
	/**
	 * Analyses a corpus contained in the given data. Creates a hashmap containing a word
	 * as key and a list of document ids where it occurs as value.
	 * @param data the corpus data
	 */
	public void analyzeCorpus(ProcessInstances data){
		for (int j = 0; j < data.numStrAttributes(); j++){
			String corpus = initCorpusForAttribute(j, data);
			
			String[] words = processCorpus(corpus);
			initGlobalWordOccurences(words);
			
			countFrequencies(data, j);
		}
		System.out.println("Finish Corpus analyses");
	}
	
	/**
	 * Processes the data by counting in how many documents (=process models) a word occurs
	 * and writing the document id in the globalWordOccurences field
	 * @param data the data to process
	 * @param stopper the stopword list
	 * @param j the string attribute index
	 */
	public void countFrequencies(ProcessInstances data, int j){
		
		for (int i = 0; i < data.numInstances(); i++){//now examine each cluster whether it contains the word
			String[] clusterWords = processCorpus(((ProcessInstance)data.instance(i)).strValue(j));
			//filter duplicate words
			HashSet<String> clusterWordsNoDuplicates = filterDuplicates(clusterWords);
			assignOccurences(clusterWordsNoDuplicates, ((ProcessInstance)data.instance(i)));
		}
	}
	
	/**
	 * Adds the document ids to the overall global occurences list
	 * @param clusterWordsNoDuplicates the words contained in a cluster
	 * @param data the current document
	 */
	public void assignOccurences(HashSet<String> clusterWordsNoDuplicates, ProcessInstance data){
		for (String word : clusterWordsNoDuplicates){
			List<Integer> docIndex = globalWordOccurences.get(word);
			if (docIndex !=  null){
				if (!docIndex.contains(data.getId())){
					docIndex.add(data.getId());
				}
			}
		}
	}
	
	/**
	 * Filters the words of a cluster for duplicates
	 * @param clusterWords the words of a cluster
	 * @param stopper the stopword list
	 * @return a filtered list of the cluster's words without duplicates
	 */
	public HashSet<String> filterDuplicates(String[] clusterWords){
		HashSet<String> clusterWordsNoDuplicates = new HashSet<String>();
		for (int k = 0; k < clusterWords.length; k++){
			if (!clusterWords[k].equals("") && !stopper.is(clusterWords[k])){
				clusterWordsNoDuplicates.add(clusterWords[k]);
			}
		}
		return clusterWordsNoDuplicates;
	}
	
	/**
	 * Init the document corpus by collecting the process name, description and string attribute
	 * values from each ProcessInstance of the data
	 * @param j the index of the string attribute from which to collect
	 * @param data the clustering data
	 * @return the overall corpus of the data
	 */
	public String initCorpusForAttribute(int j, ProcessInstances data){
		//get whole corpus from instances
		String corpus = "";
		for (int i = 0; i < data.numInstances(); i++){
			corpus += ((ProcessInstance)data.instance(i)).strValue(j) + " ";
			corpus += ((ProcessInstance)data.instance(i)).process.getName() + " ";
			corpus += ((ProcessInstance)data.instance(i)).process.getDescription() + " ";
		}
		return corpus;
	}
	
	/**
	 * Inits the global word occurences hashmap by creating an (empty) entry fo  each word 
	 * in the corpus.
	 * @param words the corpus
	 * @param stopper the stopword list
	 */
	public void initGlobalWordOccurences(String[] words){
		globalWordOccurences = new HashMap<String, List<Integer>>();
		for (int i = 0; i < words.length; i++){//put all the words into an initial hashmap
			if ((!words[i].equals("") && !stopper.is(words[i]))){
				globalWordOccurences.put(words[i], new ArrayList<Integer>());
			}
		}
	}
	
	/**
	 * Calculates the global idf value for a word in a cluster
	 * @param cluster current cluster to label
	 * @param entry a word with its frequency
	 * @return the global inverse document frequency (traditional idf)
	 */
	public double calculateGlobalIdf(ClusterNode<ProcessInstances> cluster, Entry<String, Double> entry){
		
		if (cluster.getParent() == null){
			return 1;
		}
		double n = (double)rootNode.getClusterSize();
		
		return Math.log10(n/globalWordOccurences.get(entry.getKey()).size() + 1);
		
	}
	
	/**
	 * Calculates the tf.idf score for a cluster's word
	 * @param cluster current cluster to label
	 * @param entry a word with its frequency in the current cluster
	 * @return
	 */
	public double calculateCandidateScore(ClusterNode<ProcessInstances> cluster, Entry<String, Double> entry){
		double tf = entry.getValue();
		
		double idf = calculateGlobalIdf(cluster, entry);
		return idf * tf;
	}
	
	/**
	 * Calculate the tfIdfs for each word contained in this cluster
	 * @param cluster to label
	 * @param feature after which to label
	 * @return the two words with highest tfIdf
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Double> calculateScores(ClusterNode<ProcessInstances> cluster, Entry<String, Object> feature ){
		HashMap<String, Double> labelNames = (HashMap<String, Double>) feature.getValue();
		HashMap<String, Double> labelCandidates = new HashMap<String, Double>();
		//iterate over each word and calculate tfIdf
		for (Entry<String, Double> entry : labelNames.entrySet()) {
			double tfIdf = calculateCandidateScore(cluster, entry);
			labelCandidates.put(entry.getKey(), tfIdf);
		}
		return labelCandidates;
	}
}
