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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataFeatureVector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataProcessMetrics;

/**
 * Builds up a chain that creates {@link FeatureVector}s containing specific process 
 * metrics from the process models and clusters it afterwards hierarchically according
 * to the selected metrics/features.
 * @author Cindy Fähnrich
 *
 */
public class Clustering {

private final static Logger logger = Logger.getLogger(Clustering.class.getName());

public static HierarchicalProcessClusterer clusterer;
public static ClusterLabeler clusterLabeler;
public static FastVector numericAttributes; 
public static FastVector stringAttributes;
	
	public static void main(String[] args) throws IllegalTypeException, IOException {
		
		//configure for time measurement
		long startTime = System.currentTimeMillis();
		
		//build up chain
		IUnitChainBuilder chainBuilder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, UnitDataProcessMetrics.class);
		buildUpUnitChain(chainBuilder);
		
		logger.info(chainBuilder.getChain().toString());
		
		//run chain
		@SuppressWarnings("unchecked")
		Collection<UnitDataFeatureVector<Object>> result = (Collection<UnitDataFeatureVector<Object>>) chainBuilder.getChain().execute();
	
		setupClusterer(result);
		clusterResult(result);
		
		//finish time measurement
		long time = System.currentTimeMillis() - startTime;
		System.out.println("Time needed: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
		printResult();
	}
	
	/**
	 * Configures and builds up the {@link UnitChain} by invoking the corresponding builder methods.
	 * @param chainBuilder
	 * @throws IllegalTypeException 
	 */
	private static void buildUpUnitChain(IUnitChainBuilder chainBuilder) throws IllegalTypeException {
		//build db query
		chainBuilder.addDbFilterConfig(createDbFilterConfig());
		chainBuilder.createBpmaiJsonToJbptUnit(false);
		
		chainBuilder.createProcessModelMetricsCalulatorUnit();
		chainBuilder.createModelToFeatureVectorUnit(createMetricsConfig());
		
		//collect results
		chainBuilder.createSimpleCollectorUnit();
	}
	
	/**
	 * Iterates through the results and prints whether the process models are connected or not and the amount
	 * of entries and exists. 
	 * @param results from the execution of the {@link UnitChain}
	 */
	private static void printResult(){
		//TODO implement
		System.out.println("FINISH");
	}
	
	/**
	 * Creates a configuration for the different features that shall be considered for clustering,
	 * including a weighting (in int numbers)
	 * 
	 * @return the configuration containing the selected features for clustering
	 */
	
	private static FeatureConfig createMetricsConfig(){
		FeatureConfig features = new FeatureConfig();
		numericAttributes = new FastVector();
		stringAttributes = new FastVector();
		
		features.addMetric(ProcessFeatureConstants.METRICS.NUM_ACTIVITIES);
		Attribute att = new Attribute(ProcessFeatureConstants.METRICS.NUM_ACTIVITIES.name());
		att.setWeight(1);
		numericAttributes.addElement(att);
		
		features.addMetric(ProcessFeatureConstants.METRICS.NUM_EDGES);
		Attribute att2 = new Attribute(ProcessFeatureConstants.METRICS.NUM_EDGES.name());
		att2.setWeight(1);
		numericAttributes.addElement(att2);
	
		features.addLabel(ProcessFeatureConstants.PROCESS_LABELS.ALL_ACTIVITY_LABELS);
		Attribute att4 = new Attribute(ProcessFeatureConstants.PROCESS_LABELS.ALL_ACTIVITY_LABELS.name(), (FastVector)null, 0);
		att4.setWeight(1);
		
		stringAttributes.addElement(att4);
		return features;
	}
	
	/**
	 * Create database filter configuration
	 * @return a new filter config for the database access
	 */
	private static DbFilterConfig createDbFilterConfig(){
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);
		dbFilter.addFormat(Constants.FORMATS.BPMAI_JSON);
		dbFilter.addNotation(Constants.NOTATIONS.EPC);
		return dbFilter;
	}
	
	/**
	 * Create hierarchical clusterer with his attributes
	 */
	public static void setupClusterer(Collection<UnitDataFeatureVector<Object>> result){
		clusterer = new HierarchicalProcessClusterer();
		clusterer.setStringDistanceFunction(new WeightedEditDistance());
		clusterer.setNumericDistanceFunction(new WeightedEuclideanDistance(normalizeValues(result)));
		clusterer.setLinkType("SINGLE");
		clusterer.setNumClusters(2);
		clusterer.setDebug(true);
		clusterer.setAttributes(numericAttributes);
		clusterer.setStringAttributes(stringAttributes);
	}
	/**
	 * Computes the maximum number of all feature values 
	 * @param result
	 */
	public static ArrayList<double[]> normalizeValues(Collection<UnitDataFeatureVector<Object>> result){
		
		double[] maxFeatureValues = new double[numericAttributes.size()];
		double[] minFeatureValues = new double[numericAttributes.size()];
		
		for (UnitDataFeatureVector<Object> data : result){
			//add the feature vector's values to maxValues
			double[] numFeat = data.getFeatureVector().getNumericFeatures();
			for (int i = 0; i < numFeat.length; i++){
				if (maxFeatureValues[i] < numFeat[i]){
					maxFeatureValues[i] = numFeat[i];
				}
				if (minFeatureValues[i] > numFeat[i]){
					minFeatureValues[i] = numFeat[i];
				}
				
			}
		}
		ArrayList<double[]> normVals = new ArrayList<double[]>();
		normVals.add(maxFeatureValues);
		normVals.add(minFeatureValues);
		return normVals;
		 
	}
		
	
	
	@SuppressWarnings("unused")
	public static void clusterResult(Collection<UnitDataFeatureVector<Object>> result){
		ProcessInstances data = new ProcessInstances("", numericAttributes, stringAttributes, result.size());
		int i = 0;
		for (UnitDataFeatureVector<Object> vector : result){
			vector.getInstance().setId(i++);
			data.add(vector.getInstance());
		}
			
		try {//cluster the results
			clusterer.buildClusterer(data);
			ClusterTree<ProcessInstances> dendrogram = clusterer.getClusters();
			labelClusters(dendrogram, data);
			int maxDepth = dendrogram.getRootElement().getMaxDepthOfSubtree();
			int maxSize = dendrogram.getRootElement().getSizeOfSubtree();
			ClusterTree<ProcessInstances> newCluster = dendrogram.getSubtreeWithMinClusterSize(2);
			//go on with analyses of the clusters here...
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Configures the hierarchical cluster labeler and triggers the cluster labeling process.
	 * @param dendrogram the cluster tree to label
	 * @param data the data that has been clustered
	 */
	public static void labelClusters(ClusterTree<ProcessInstances> dendrogram, ProcessInstances data){
		//set system path for wordnet database
		System.setProperty("wordnet.database.dir", "E:\\Programs\\WordNet\\dict\\");
		ExternalResourceEnhancer enhancer = new WordnetEnhancer("E:/Cindy/Studium/Master/3.Semester/Masterprojekt/repo/mpws2011w1/code/PromniCAT/src/de/uni_potsdam/hpi/bpt/promnicat/analysisModules/clustering/labeling/englishPCFG.ser.gz");
		clusterLabeler = new HierarchicalClusterLabeler(enhancer);
		((HierarchicalClusterLabeler)clusterLabeler).analyzeCorpus(data);
		((TfIdfClusterLabeler)clusterLabeler).setRootNode(dendrogram.getRootElement());
		clusterLabeler.assignLabelToCluster(dendrogram.getRootElement());
	}
}
