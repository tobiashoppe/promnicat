package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.labeling;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jbpt.pm.Activity;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ClusterNode;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstances;


/**
 *  Labels clusters using a special formula for calculating the candidate score and by enhancing
 *  the candidate corpus with WordNet resources.
 * @author Cindy Fähnrich
 *
 */
public class HierarchicalClusterLabeler extends TfIdfClusterLabeler {
	
	public HierarchicalClusterLabeler(ExternalResourceEnhancer enhancer){
		super(enhancer);
	}
	
	/**
	 * Calculates the local idf value for a word in a cluster
	 * @param current cluster
	 * @param entry containing the word in question
	 * @return local document frequency of a word
	 */
	public double calculateLocalDf(ClusterNode<ProcessInstances> cluster, Entry<String, Double> entry){
		if (cluster == null){
			return 1;
		}
		
		double n = (double)cluster.getClusterSize();
		
		ProcessInstances clusterData = cluster.getCluster().getData();
		double actualOccurences = calculateOccurences(clusterData, entry.getKey());
		return (actualOccurences * actualOccurences)/n; //give this aspect a stronger weight (more penalties)
	}
	
	/**
	 * Count the amount of occurences for a word  in the cluster.
	 * @param clusterItems the cluster's data
	 * @param word the given word
	 * @return the amount of overall occurences in this cluster
	 */
	public double calculateOccurences(ProcessInstances clusterItems, String word){
		List<Integer> occurences = globalWordOccurences.get(word);
		double actualOccurences = 0.0;
		for (int i = 0; i < clusterItems.numInstances(); i++){//find whether the documents are contained in the cluster
			int id = clusterItems.getInstance(i).getId();
			if (occurences.contains((Integer)id)){
				actualOccurences += 1.0;
			}
		}
		return actualOccurences;
	}
	
	/**
	 * Selects a label for a cluster by calculating the candidate scores for each word, taking
	 * the top 7 label candidates and enhancing this set by external resources, to finally
	 * take the top 2 label candidates for the cluster's label.
	 */
	@Override
	public String selectLabels(ClusterNode<ProcessInstances> cluster, Entry<String, Object> feature){
		//calculate tfIdf for each word
		HashMap<String, Double> labelCandidates = calculateScores(cluster, feature);
		if (enhancer != null){
			labelCandidates = selectTopWords(labelCandidates);
			//3. enhance label candidates
			enhancer.enhanceLabelCandidates(labelCandidates);
		}
		//take top 2 label candidates
		
		//markers for the top two words
		return selectTopWordsAsLabel(feature);
	}
	
	/**
	 * Select the top words from the set of label candidates
	 * @param labelCandidates from which the top words to select
	 * @return the top words as new label candidates
	 */
	public HashMap<String, Double> selectTopWords(HashMap<String, Double> labelCandidates){
		//2. take top n words
		int maxAmount = 7;
		HashMap<String, Double> topN = new HashMap<String, Double>();
		Entry<String, Double> currentMin = new HashMap.SimpleEntry<String, Double>("", 1.0);
		for (Entry<String, Double> candidate : labelCandidates.entrySet()){
			evaluateForTopWords(maxAmount, topN, candidate, currentMin);
		}
		return topN;
	}
	
	/**
	 * Checks whether a candidate word is suitable for the top word list and updates the list 
	 * if this is the case
	 * @param maxAmount maximum number of words in the top word list
	 * @param topN list of the top words
	 * @param candidate to be evaluated for top word list
	 * @param currentMin current minimum score in the top word list
	 */
	public void evaluateForTopWords(int maxAmount, HashMap<String, Double> topN, Entry<String, Double> candidate, Entry<String, Double> currentMin){
		if (topN.size() < maxAmount){
			topN.put(candidate.getKey(), candidate.getValue());
			if (candidate.getValue() < currentMin.getValue()){
				currentMin = candidate;
			}
		} else {
			if (currentMin.getValue() < candidate.getValue()){//add currently latest element
				topN.remove(currentMin.getKey());
				topN.put(candidate.getKey(), candidate.getValue());
				currentMin = candidate;
				for (Entry<String, Double> topNCandidate : topN.entrySet()){
					if (topNCandidate.getValue() < currentMin.getValue()){
						currentMin = topNCandidate;
					}
				}
			} 
		}
	}
	
	/**
	 * Calculates the idf value for a word in a PARENT cluster
	 * @param parent cluster
	 * @param entry containing the word in question
	 * @return parent idf value
	 */
	public double calculateParentIdf(ClusterNode<ProcessInstances> cluster, Entry<String, Double> entry){
		if (cluster == null){
			return 1;
		}
		double n = (double)cluster.getClusterSize();
		
		ProcessInstances clusterData = cluster.getCluster().getData();
		double actualOccurences = calculateOccurences(clusterData, entry.getKey());
		return Math.log10(n/actualOccurences + 1.0);
	}
	
	/**
	 * Calculates the idf value for the current word in the sibling clusters
	 * @param cluster
	 * @param entry
	 * @return
	 */
	public double calculateSiblingItf(ClusterNode<ProcessInstances> parentCluster, ClusterNode<ProcessInstances> currentCluster, Entry<String, Double> entry){
		
		if (parentCluster == null){
			return 1;
		}
		
		List<ClusterNode<ProcessInstances>> childClusters = parentCluster.getChildren();
		double actualOccurences = 0.0;
		//get the other siblings
		for (int j = 0; j < childClusters.size(); j++){
			ClusterNode<ProcessInstances> sibling = childClusters.get(j);
			if (sibling != currentCluster){
				ProcessInstances instances = sibling.getCluster().getData();
				calculateSiblingOccurences(actualOccurences, instances, entry.getKey());
			}
		}
		
		
		return 1/(actualOccurences + 1);
	}
	
	/**
	 * Calculates the occurences of a given word in a sibling cluster
	 * @param actualOccurences the current count of occurences of this word
	 * @param instances the cluster's data
	 * @param word of which to count the occurences
	 */
	public void calculateSiblingOccurences(double actualOccurences, ProcessInstances siblingData, String word){
		//number of clusters where the word occurs
		List<Integer> occurences = globalWordOccurences.get(word);
		for (int i = 0; i < siblingData.numInstances(); i++){
			int id = siblingData.getInstance(i).getId();
			if (occurences.contains((Integer)id)){
				Collection<Activity> activities = siblingData.getInstance(i).process.getActivities();
				findOverallOccurences(activities, word, actualOccurences);
			} 
		}
	}
	
	/**
	 * Iterates over all given activities and counts how much a given word occurs in them
	 * @param activities to iterate
	 * @param word for which to count the occurences
	 * @param actualOccurences the current count of occurences of this word
	 */
	public void findOverallOccurences(Collection<Activity> activities, String word, double actualOccurences){
		for (Activity act : activities){//find the real amount of occurences!
			String label = act.getLabel().toLowerCase();
			if (label.contains(word)){
				for (String token : label.split(" ")){
					if (token.equals(word)){
						actualOccurences += 1;
					}
				}
			}
		}
	}
	
	/**
	 * Calculates the candidate score for a word in its cluster.
	 */
	@Override
	public double calculateCandidateScore(ClusterNode<ProcessInstances> cluster, Entry<String, Double> entry){
		double tf = entry.getValue();
		double idfGlobal = calculateGlobalIdf(cluster, entry);
		double dfLocal = calculateLocalDf(cluster, entry);
		double parentIdf = calculateParentIdf(cluster.getParent(), entry);
		double itfSibling = calculateSiblingItf(cluster.getParent(), cluster, entry);
		
		return idfGlobal * dfLocal * parentIdf * itfSibling * tf;
	}
}
