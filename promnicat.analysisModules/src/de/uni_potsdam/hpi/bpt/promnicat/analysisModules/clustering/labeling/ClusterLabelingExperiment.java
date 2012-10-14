/**
 * 
 */
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.labeling;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jbpt.pm.Activity;
import org.jbpt.pm.ProcessModel;

import weka.core.Attribute;
import weka.core.FastVector;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ClusterTree;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.FeatureVector;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.HierarchicalProcessClusterer;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstance;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstances;
import de.uni_potsdam.hpi.bpt.promnicat.util.FeatureConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.util.ProcessFeatureConstants;
import de.uni_potsdam.hpi.bpt.promnicat.util.WeightedEditDistance;
import de.uni_potsdam.hpi.bpt.promnicat.util.WeightedEuclideanDistance;

/**
 * This is an experiment for cluster labeling. This class reads in csv files of testsets and generates process
 * models from it, which are then a) clustered by their labels and b) the clusters are labeled..
 * 
 * @author Cindy Fähnrich
 *
 */
public class ClusterLabelingExperiment {
	static HashMap<String, ProcessModel> models = new HashMap<String,ProcessModel>();
	
	public static final String PARSER_PATH = "src/de/uni_potsdam/hpi/bpt/promnicat/analysisModules/clustering/labeling/englishPCFG.ser.gz";
	public static HierarchicalProcessClusterer clusterer;
	public static ClusterLabeler clusterLabeler;
	public static FastVector numericAttributes; 
	public static FastVector stringAttributes;

	public static void main(String[] args) throws IllegalTypeException, IOException {
		try{  
			//read in files with activites
			readInFile(false, "testSet_50models_activities.csv" );
			
			//OPTIONAL: read in model names
			readInFile(true, "testSet_50models_names.csv");
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		
		selectStringAttributes();
		ProcessInstances data = new ProcessInstances("", numericAttributes, stringAttributes, models.size());
		int i = 0;
		for (Entry<String,ProcessModel> model : models.entrySet()){
			FeatureVector featureVector = createFeatureVector(model.getValue());
			ProcessInstance inst = new ProcessInstance(featureVector.size(), featureVector.getNumericFeatures(), featureVector.getStringFeatures());
			inst.process = model.getValue();
			inst.setId(i++);
			data.add(inst);
		}
		
		//cluster process models
		setupClusterer();
		clusterResult(data);
	}
	
	/**
	 * Set up hierarchical clusterer with his attributes
	 */
	public static void setupClusterer(){
		
		clusterer = new HierarchicalProcessClusterer();
		clusterer.setStringDistanceFunction(new WeightedEditDistance());
		clusterer.setNumericDistanceFunction(new WeightedEuclideanDistance());
		clusterer.setLinkType("SINGLE");
		clusterer.setNumClusters(10);
		clusterer.setUseStrings(true);
		clusterer.setDebug(true);
		clusterer.setStringAttributes(stringAttributes);
		clusterer.setAttributes(numericAttributes);
	}
	
	/**
	 * Initiates the actual clustering and labeling.
	 * @param data the data to cluster
	 */
	@SuppressWarnings("unused")
	public static void clusterResult(ProcessInstances data){	
		try {//cluster the results
			clusterer.buildClusterer(data);
			ClusterTree<ProcessInstances> dendrogram = clusterer.getClusters();
			labelClusters(dendrogram, data);
			//watch the resulting cluster with its labels in the debugger by setting a breakpoint here
			int maxDepth = dendrogram.getRootElement().getMaxDepthOfSubtree();
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
		ExternalResourceEnhancer enhancer = new WordnetEnhancer(PARSER_PATH);
		clusterLabeler = new HierarchicalClusterLabeler(enhancer);
		((HierarchicalClusterLabeler)clusterLabeler).analyzeCorpus(data);
		((TfIdfClusterLabeler)clusterLabeler).setRootNode(dendrogram.getRootElement());
		clusterLabeler.assignLabelToCluster(dendrogram.getRootElement());
	}
	
	/**
	 * Creates a configuration for the different features that shall be considered for clustering,
	 * including a weighting (in int numbers)
	 * 
	 * @return the configuration containing the selected features for clustering
	 */
	
	private static void selectStringAttributes(){
		FeatureConfig features = new FeatureConfig();
		stringAttributes = new FastVector();
	
		features.addLabel(ProcessFeatureConstants.PROCESS_LABELS.ALL_ACTIVITY_LABELS);
		Attribute strAtt = new Attribute(ProcessFeatureConstants.PROCESS_LABELS.ALL_ACTIVITY_LABELS.name(), (FastVector)null, 0);
		strAtt.setWeight(1);
		
		stringAttributes.addElement(strAtt);
		
		numericAttributes = new FastVector();
		
		features.addMetric(ProcessFeatureConstants.METRICS.NUM_ACTIVITIES);
		Attribute numAtt = new Attribute(ProcessFeatureConstants.METRICS.NUM_ACTIVITIES.name());
		numAtt.setWeight(1);
		
		numericAttributes.addElement(numAtt);
	}
	
	/**
	 * Reads in a testset by parsing it line by line, assigning each line as 
	 * model name or activity label to the model with the given id. 
	 * Formats: 
	 * Model_ID;Activity_Label
	 * Model_ID;Model_Name
	 * @param activityLabels boolean whether reading in model names or activities
	 * @param filename for the file to read in
	 * @throws Exception
	 */
	public static void readInFile(boolean readActivityLabels, String filename) throws Exception{
		FileInputStream fstream = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			//split string by ";" and add to modelmap 
			String[] line = strLine.split(";");
			
			if (models.get(line[0]) == null){
				//create new processmodel
				ProcessModel newModel = new ProcessModel();
				models.put(line[0], newModel);
			} 
			
			ProcessModel model = models.get(line[0]);
			
			if (readActivityLabels){
				assignActivity(model, line[1]);
			} else {
				assignName(model, line[1]);
			}
			
		}
		in.close();
	}
	
	/**
	 * Assigns a name to the given process model.
	 * @param model whose name to assign
	 * @param name to give the model
	 */
	public static void assignName(ProcessModel model, String name){
		model.setName(name);
	}
	
	/**
	 * Assigns a new activity with a label to the given process model.
	 * @param model whose activity to assign
	 * @param label to assign to the new activity
	 */
	public static void assignActivity(ProcessModel model, String label){
		//create new activity and add to model
		Activity activity = new Activity();
		activity.setName(label);
		activity.setModel(model);
		model.addFlowNode(activity);	
	}
	
	/**
	 * Selects from the input (the given metrics) the corresponding metrics as
	 * stated in the configuration and transforms them into a
	 * {@link FeatureVector}
	 * 
	 * @param input
	 *            with the process metrics from the former unit
	 * @return the result {@link FeatureVector}
	 */
	public static FeatureVector createFeatureVector(ProcessModel model) {
		FeatureVector features = new FeatureVector();
		features.addNumericFeature(model.getActivities().size());
		String activityLabels = "";
		for (Activity act : model.getActivities()){
			activityLabels += act.getName() + " ";
		}
		features.addStringFeature(activityLabels);
		
		return features;
	}
}
