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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.CapabilitiesHandler;
import weka.core.DistanceFunction;
import weka.core.Drawable;
import weka.core.EditDistance;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import de.uni_potsdam.hpi.bpt.promnicat.util.WeightedEditDistance;

/**
 * Extends WEKAs {@link HierarchicalClusterer} by creating a clustertree
 * directly containing the clustered elements with the feature vector and
 * process models. Also, this clusterer can cluster both numeric and string
 * values at the same time and uses weights for clustering.
 * 
 * @author Cindy Fähnrich
 * 
 */
public class HierarchicalProcessClusterer extends HierarchicalClusterer
		implements OptionHandler, CapabilitiesHandler, Drawable {

	private static final long serialVersionUID = 1L;
	
	/** Whether the classifier is run in debug mode. */
	protected boolean m_bDebug = false;

	/** Vector with numeric attributes */
	protected FastVector attributes;
	
	/** Vector with string attributes */
	protected FastVector strAttributes;
	
	/**
	 * Whether the distance represent node height (if false) or branch length
	 * (if true).
	 */
	protected boolean m_bDistanceIsBranchLength = false;

	/** cllustering data **/
	ProcessInstances m_instances;

	/** number of clusters desired in clustering **/
	int m_nNumClusters = 2;

	/**
	 * Sets the number of clusters for the result to have
	 * @param nClusters
	 * 				number of clusters
	 */
	public void setNumClusters(int nClusters) {
		m_nNumClusters = Math.max(1, nClusters);
	}

	/**
	 * Returns the number of clusters this cluster result
	 * shall have.
	 * 
	 * @return m_nNumClusters
	 * 				the number of clusters that was set
	 */
	public int getNumClusters() {
		return m_nNumClusters;
	}

	/** distance function used for comparing NUMERIC attributes of members of a cluster**/
	protected DistanceFunction m_DistanceFunction = null;

	/** distance function used for comparing STRING attributes of members of a cluster **/
	protected DistanceFunction m_StringDistanceFunction = null;
	
	/** boolean indicating whether to use clustering ONLY of string members or not
	 * if value is set to null, both string and numeric values shall be clustered
	**/
	protected Boolean useStrings = new Boolean(false);
	
	public Boolean getUseStrings() {
		return useStrings;
	}

	public void setUseStrings(Boolean useStrings) {
		this.useStrings = useStrings;
	}

	/**
	 * Returns the distance function for numeric attribute values.
	 * @return m_DistanceFunction
	 * 					the distance function to use for numeric attributes
	 */
	public DistanceFunction getDistanceFunction() {
		return m_DistanceFunction;
	}
	
	/**
	 * Sets the distance function to use for numeric attribute values.
	 * @param distanceFunction
	 * 					the distance function to use for numeric attributes
	 */
	public void setNumericDistanceFunction(DistanceFunction distanceFunction) {
		if (m_StringDistanceFunction != null){
			//set boolean to use string and numeric
			useStrings = null;
		} else {
			useStrings = new Boolean(false);
		}
		m_DistanceFunction = distanceFunction;
	}
	
	
	/**
	 * Sets the distance function to use for string attribute values.
	 * @return distanceFunction
	 * 					the distance function to use for string attributes
	 */
	public void setStringDistanceFunction(DistanceFunction distanceFunction) {
		if (m_DistanceFunction != null){
			//set boolean to use string and numeral
			useStrings = null;
		} else {
			useStrings = new Boolean(true);
		}
		m_StringDistanceFunction = distanceFunction;
	}
	
	/**
	 * Sets the data as {@link ProcessInstances} for the distance functions
	 * to use.
	 * @return instances
	 * 				the {@link ProcessInstances} for the distance functions to use
	 */
	public void setInstancesOfDistanceFunction(ProcessInstances instances){
		if (m_StringDistanceFunction != null){
			m_StringDistanceFunction.setInstances(instances);
		}
		if (m_DistanceFunction != null){
			m_DistanceFunction.setInstances(instances);
		}
	}
	
	/**
	 * Calculates the distance between two {@ProcessInstances} by calculating
	 * both the distance for string and numeric attributes (depends on what 
	 * value is selected for boolean useStrings) and returns a final distance
	 * that takes into account all attributes.
	 * @param instance1
	 * 			to compare to another instance
	 * @param instance2
	 * 			to compare to the first instance
	 * @return the distance between both instances according
	 * 				to their numeric and string attributes (if selected)
	 */
	public double calcDistanceWithFunction(ProcessInstance instance1, ProcessInstance instance2){
		
		double overallWeights = 0.0;
		if (useStrings == null){//calc both
			double result1 = m_StringDistanceFunction.distance(instance1, instance2);
			double result2 = m_DistanceFunction.distance(instance1, instance2);
			for (int i = 0; i < strAttributes.size(); i++){
				overallWeights += ((Attribute)strAttributes.elementAt(i)).weight();
			}
			for (int i = 0; i < attributes.size(); i++){
				overallWeights += ((Attribute)attributes.elementAt(i)).weight();
			}
			return Math.sqrt((result1 + result2)/overallWeights);
		}
		if (useStrings.booleanValue()){
			double result = m_StringDistanceFunction.distance(instance1, instance2);
			for (int i = 0; i < strAttributes.size(); i++){
				overallWeights += ((Attribute)strAttributes.elementAt(i)).weight();
			}
			return Math.sqrt(result/overallWeights);
		} else {
			double result =  m_DistanceFunction.distance(instance1, instance2);
			for (int i = 0; i < attributes.size(); i++){
				overallWeights += ((Attribute)attributes.elementAt(i)).weight();
			}
			return Math.sqrt(result/overallWeights);
		}
	}
	

	/**
	 * used for priority queue for efficient retrieval of pair of clusters to
	 * merge
	 **/
	class Tuple {
		public Tuple(double d, int i, int j, int nSize1, int nSize2) {
			m_fDist = d;
			m_iCluster1 = i;
			m_iCluster2 = j;
			m_nClusterSize1 = nSize1;
			m_nClusterSize2 = nSize2;
		}

		double m_fDist;
		int m_iCluster1;
		int m_iCluster2;
		int m_nClusterSize1;
		int m_nClusterSize2;
	}

	/** comparator used by priority queue **/
	class TupleComparator implements Comparator<Tuple> {
		public int compare(Tuple o1, Tuple o2) {
			if (o1.m_fDist < o2.m_fDist) {
				return -1;
			} else if (o1.m_fDist == o2.m_fDist) {
				return 0;
			}
			return 1;
		}
	}

	/** the various link types */
	final static int SINGLE = 0;
	final static int COMPLETE = 1;
	final static int AVERAGE = 2;
	final static int MEAN = 3;
	final static int CENTROID = 4;
	final static int WARD = 5;
	final static int ADJCOMLPETE = 6;
	final static int NEIGHBOR_JOINING = 7;
	public static final Tag[] TAGS_LINK_TYPE = { new Tag(SINGLE, "SINGLE"),
			new Tag(COMPLETE, "COMPLETE"), new Tag(AVERAGE, "AVERAGE"),
			new Tag(MEAN, "MEAN"), new Tag(CENTROID, "CENTROID"),
			new Tag(WARD, "WARD"), new Tag(ADJCOMLPETE, "ADJCOMLPETE"),
			new Tag(NEIGHBOR_JOINING, "NEIGHBOR_JOINING") };

	/**
	 * Holds the Link type used calculate distance between clusters
	 */
	int m_nLinkType = SINGLE;

	boolean m_bPrintNewick = true;;

	public boolean getPrintNewick() {
		return m_bPrintNewick;
	}

	/** sets the numeric attributes
	 * 
	 * @param atts
	 * 			the numeric attributes
	 */
	public void setAttributes(FastVector atts){
		attributes = atts;
	}
	
	/** sets the string attributes
	 * 
	 * @param atts
	 * 			the string attributes
	 */
	public void setStringAttributes(FastVector atts){
		strAttributes = atts;
	}
	public void setPrintNewick(boolean bPrintNewick) {
		m_bPrintNewick = bPrintNewick;
	}
	
	public void setLinkType(SelectedTag newLinkType) {
		if (newLinkType.getTags() == TAGS_LINK_TYPE) {
			m_nLinkType = newLinkType.getSelectedTag().getID();
		}
	}
	
	/**
	 * Sets the link type according to the corresponding string name
	 * @param linkType
	 * 			the name of the link type
	 */
	public void setLinkType(String linkType){
		if (linkType.compareTo("SINGLE") == 0) {setLinkType(new SelectedTag(SINGLE, TAGS_LINK_TYPE));}
	    if (linkType.compareTo("COMPLETE") == 0) {setLinkType(new SelectedTag(COMPLETE, TAGS_LINK_TYPE));}
	    if (linkType.compareTo("AVERAGE") == 0) {setLinkType(new SelectedTag(AVERAGE, TAGS_LINK_TYPE));}
	    if (linkType.compareTo("MEAN") == 0) {setLinkType(new SelectedTag(MEAN, TAGS_LINK_TYPE));}
	    if (linkType.compareTo("CENTROID") == 0) {setLinkType(new SelectedTag(CENTROID, TAGS_LINK_TYPE));}
	    if (linkType.compareTo("WARD") == 0) {setLinkType(new SelectedTag(WARD, TAGS_LINK_TYPE));}
	    if (linkType.compareTo("ADJCOMLPETE") == 0) {setLinkType(new SelectedTag(ADJCOMLPETE, TAGS_LINK_TYPE));}
	    if (linkType.compareTo("NEIGHBOR_JOINING") == 0) {setLinkType(new SelectedTag(NEIGHBOR_JOINING, TAGS_LINK_TYPE));}
	}

	public SelectedTag getLinkType() {
		return new SelectedTag(m_nLinkType, TAGS_LINK_TYPE);
	}

	/** class representing node in cluster hierarchy **/
	@SuppressWarnings("serial")
	class Node implements Serializable {
		public Node m_left;
		public Node m_right;
		public Node m_parent;
		public int m_iLeftInstance;
		public int m_iRightInstance;
		double m_fLeftLength = 0;
		double m_fRightLength = 0;
		double m_fHeight = 0;

		public String toString(int attIndex) {
			DecimalFormat myFormatter = new DecimalFormat("#.#####");

			if (m_left == null) {
				if (m_right == null) {
					return "("
							+ m_instances.instance(m_iLeftInstance)
									.stringValue(attIndex)
							+ ":"
							+ myFormatter.format(m_fLeftLength)
							+ ","
							+ m_instances.instance(m_iRightInstance)
									.stringValue(attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				} else {
					return "("
							+ m_instances.instance(m_iLeftInstance)
									.stringValue(attIndex) + ":"
							+ myFormatter.format(m_fLeftLength) + ","
							+ m_right.toString(attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				}
			} else {
				if (m_right == null) {
					return "("
							+ m_left.toString(attIndex)
							+ ":"
							+ myFormatter.format(m_fLeftLength)
							+ ","
							+ m_instances.instance(m_iRightInstance)
									.stringValue(attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				} else {
					return "(" + m_left.toString(attIndex) + ":"
							+ myFormatter.format(m_fLeftLength) + ","
							+ m_right.toString(attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				}
			}
		}

		public String toString2(int attIndex) {
			DecimalFormat myFormatter = new DecimalFormat("#.#####");

			if (m_left == null) {
				if (m_right == null) {
					return "("
							+ m_instances.instance(m_iLeftInstance).value(
									attIndex)
							+ ":"
							+ myFormatter.format(m_fLeftLength)
							+ ","
							+ m_instances.instance(m_iRightInstance).value(
									attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				} else {
					return "("
							+ m_instances.instance(m_iLeftInstance).value(
									attIndex) + ":"
							+ myFormatter.format(m_fLeftLength) + ","
							+ m_right.toString2(attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				}
			} else {
				if (m_right == null) {
					return "("
							+ m_left.toString2(attIndex)
							+ ":"
							+ myFormatter.format(m_fLeftLength)
							+ ","
							+ m_instances.instance(m_iRightInstance).value(
									attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				} else {
					return "(" + m_left.toString2(attIndex) + ":"
							+ myFormatter.format(m_fLeftLength) + ","
							+ m_right.toString2(attIndex) + ":"
							+ myFormatter.format(m_fRightLength) + ")";
				}
			}
		}

		void setHeight(double fHeight1, double fHeight2) {
			m_fHeight = fHeight1;
			if (m_left == null) {
				m_fLeftLength = fHeight1;
			} else {
				m_fLeftLength = fHeight1 - m_left.m_fHeight;
			}
			if (m_right == null) {
				m_fRightLength = fHeight2;
			} else {
				m_fRightLength = fHeight2 - m_right.m_fHeight;
			}
		}

		void setLength(double fLength1, double fLength2) {
			m_fLeftLength = fLength1;
			m_fRightLength = fLength2;
			m_fHeight = fLength1;
			if (m_left != null) {
				m_fHeight += m_left.m_fHeight;
			}
		}
	}

	private Node[] m_clusters;
	int[] m_nClusterNr;

	/**
	 * Creates the actual clusters from the given {@link Node}s, which contain
	 * references to the elements' indices in m_instances. Checks whether the
	 * left or right child of the {@link Node} is null. If so, a leaf was found
	 * and can be added. If not, take the child and do the steps from above
	 * again (recursively).
	 * 
	 * @param cluster
	 *            current cluster (thus, {@link ClusterNode} of the tree to
	 *            extend
	 * @param oldCluster
	 *            current {@Node} of the old structure to examine
	 * @return the correctly created cluster as {@link ClusterNode}
	 */
	public ClusterNode<ProcessInstances> createClusters(
			ClusterNode<ProcessInstances> cluster, Node oldCluster) {

		if (oldCluster.m_left == null) {
			// add leaf
			ProcessInstances newLeaf = new ProcessInstances((ProcessInstance) m_instances
							.instance(oldCluster.m_iLeftInstance), attributes, strAttributes, 0);
			ClusterNode<ProcessInstances> child = new ClusterNode<ProcessInstances>(newLeaf);
			child.setParent(cluster);
			cluster.addChild(child);
			
		} else {
			// traverse again and add
			ClusterNode<ProcessInstances> child = new ClusterNode<ProcessInstances>(new ProcessInstances
					(new ProcessInstance(0), attributes, strAttributes, 0));
			child.setParent(cluster);
			cluster.addChild(child);
			createClusters(child, oldCluster.m_left);
		}
		if (oldCluster.m_right == null) {
			// add leaf
			ProcessInstances newLeaf = new ProcessInstances((ProcessInstance) m_instances
			.instance(oldCluster.m_iRightInstance), attributes, strAttributes, 0);
			ClusterNode<ProcessInstances> child = new ClusterNode<ProcessInstances>(newLeaf);
			child.setParent(cluster);
			cluster.addChild(child);
			
			 
		} else {
			// traverse again and add
				ClusterNode<ProcessInstances> child = new ClusterNode<ProcessInstances>(new ProcessInstances
					(new ProcessInstance(0), attributes, strAttributes, 0));
				cluster.addChild(child);
				child.setParent(cluster);
				createClusters(child, oldCluster.m_right);
		}
		return cluster;
	}
	
	
	/**
	 * Creates a cluster for a single instance. Checks whether the
	 * left or right instance of the {@link Node} is set to -1. If so, the opposite
	 * left/right instance contains the instance id for the single item cluster.
	 * 
	 * @param oldCluster
	 *            current {@Node} of the old structure to examine
	 * @return the correctly created cluster as {@link ClusterNode}
	 */
	public ClusterNode<ProcessInstances> createSingleCluster(Node oldCluster) {

		//add to cluster
		ProcessInstances newLeaf = new ProcessInstances((ProcessInstance) m_instances
							.instance(oldCluster.m_iLeftInstance), attributes, strAttributes, 0);
		ClusterNode<ProcessInstances> child = new ClusterNode<ProcessInstances>(newLeaf);
		return child; 
	}

	/**
	 * Creates the clusters represented as binary tree by iterating over the
	 * Node-structure already existing.
	 * 
	 * @return the clusters in hierarchical (binary tree) format
	 */
	public ClusterTree<ProcessInstances> getClusters() {

		ClusterTree<ProcessInstances> tree = new ClusterTree<ProcessInstances>();
		ClusterNode<ProcessInstances> root = new ClusterNode<ProcessInstances>();
		tree.setRootElement(root);
		for (int i = 0; i < m_clusters.length; i++) {
			if (m_clusters[i] != null) {
				if (m_clusters[i].m_iRightInstance == -1) {//a single item cluster has been found
					ClusterNode<ProcessInstances> cluster = createSingleCluster(m_clusters[i]);
					cluster.setParent(root);
					root.addChild(cluster);
				} else {
					ProcessInstances inst = new ProcessInstances("", attributes, strAttributes, 0);
					ClusterNode<ProcessInstances> cluster = new ClusterNode<ProcessInstances>(inst);
					cluster = createClusters(cluster, m_clusters[i]);
					cluster.setParent(root);
					root.addChild(cluster);
				}
			}
		}
		return tree;
	}

	/**
	 * Creates the clusters from the given data.
	 * 
	 * @param data
	 *            {@link ProcessInstances} to cluster
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void buildClusterer(ProcessInstances data) throws Exception {
		// /System.err.println("Method " + m_nLinkType);

		m_instances = data;
		int nProcessInstances = m_instances.numInstances();
		if (nProcessInstances == 0) {
			return;
		}
		setInstancesOfDistanceFunction(m_instances);
		//m_DistanceFunction.setInstances(m_instances);
		// use array of integer vectors to store cluster indices,
		// starting with one cluster per instance
		Vector<Integer>[] nClusterID = new Vector[data.numInstances()];
		for (int i = 0; i < data.numInstances(); i++) {
			nClusterID[i] = new Vector<Integer>();
			nClusterID[i].add(i);
		}
		// calculate distance matrix
		int nClusters = data.numInstances();

		// used for keeping track of hierarchy
		Node[] clusterNodes = new Node[nProcessInstances];
		if (m_nLinkType == NEIGHBOR_JOINING) {
			neighborJoining(nClusters, nClusterID, clusterNodes);
		} else {
			doLinkClustering(nClusters, nClusterID, clusterNodes);
		}

		// move all clusters in m_nClusterID array
		// & collect hierarchy
		int iCurrent = 0;
		m_clusters = new Node[m_nNumClusters];
		m_nClusterNr = new int[nProcessInstances];
		for (int i = 0; i < nProcessInstances; i++) {
			if (nClusterID[i].size() > 0) {
				for (int j = 0; j < nClusterID[i].size(); j++) {
					m_nClusterNr[nClusterID[i].elementAt(j)] = iCurrent;
				}
				m_clusters[iCurrent] = clusterNodes[i];
				iCurrent++;
			}
		}
	} // buildClusterer

	/**
	 * use neighbor joining algorithm for clustering This is roughly based on
	 * the RapidNJ simple implementation and runs at O(n^3) More efficient
	 * implementations exist, see RapidNJ (or my GPU implementation :-))
	 * 
	 * @param nClusters
	 * @param nClusterID
	 * @param clusterNodes
	 */
	void neighborJoining(int nClusters, Vector<Integer>[] nClusterID,
			Node[] clusterNodes) {
		int n = m_instances.numInstances();

		double[][] fDist = new double[nClusters][nClusters];
		for (int i = 0; i < nClusters; i++) {
			fDist[i][i] = 0;
			for (int j = i + 1; j < nClusters; j++) {
				fDist[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
				fDist[j][i] = fDist[i][j];
			}
		}

		double[] fSeparationSums = new double[n];
		double[] fSeparations = new double[n];
		int[] nNextActive = new int[n];

		// calculate initial separation rows
		for (int i = 0; i < n; i++) {
			double fSum = 0;
			for (int j = 0; j < n; j++) {
				fSum += fDist[i][j];
			}
			fSeparationSums[i] = fSum;
			fSeparations[i] = fSum / (nClusters - 2);
			nNextActive[i] = i + 1;
		}

		while (nClusters > 2) {
			// find minimum
			int iMin1 = -1;
			int iMin2 = -1;
			double fMin = Double.MAX_VALUE;
			if (m_bDebug) {
				for (int i = 0; i < n; i++) {
					if (nClusterID[i].size() > 0) {
						double[] fRow = fDist[i];
						double fSep1 = fSeparations[i];
						for (int j = 0; j < n; j++) {
							if (nClusterID[j].size() > 0 && i != j) {
								double fSep2 = fSeparations[j];
								double fVal = fRow[j] - fSep1 - fSep2;

								if (fVal < fMin) {
									// new minimum
									iMin1 = i;
									iMin2 = j;
									fMin = fVal;
								}
							}
						}
					}
				}
			} else {
				int i = 0;
				while (i < n) {
					double fSep1 = fSeparations[i];
					double[] fRow = fDist[i];
					int j = nNextActive[i];
					while (j < n) {
						double fSep2 = fSeparations[j];
						double fVal = fRow[j] - fSep1 - fSep2;
						if (fVal < fMin) {
							// new minimum
							iMin1 = i;
							iMin2 = j;
							fMin = fVal;
						}
						j = nNextActive[j];
					}
					i = nNextActive[i];
				}
			}
			// record distance
			double fMinDistance = fDist[iMin1][iMin2];
			nClusters--;
			double fSep1 = fSeparations[iMin1];
			double fSep2 = fSeparations[iMin2];
			double fDist1 = (0.5 * fMinDistance) + (0.5 * (fSep1 - fSep2));
			double fDist2 = (0.5 * fMinDistance) + (0.5 * (fSep2 - fSep1));
			if (nClusters > 2) {
				// update separations & distance
				double fNewSeparationSum = 0;
				double fMutualDistance = fDist[iMin1][iMin2];
				double[] fRow1 = fDist[iMin1];
				double[] fRow2 = fDist[iMin2];
				for (int i = 0; i < n; i++) {
					if (i == iMin1 || i == iMin2 || nClusterID[i].size() == 0) {
						fRow1[i] = 0;
					} else {
						double fVal1 = fRow1[i];
						double fVal2 = fRow2[i];
						double fDistance = (fVal1 + fVal2 - fMutualDistance) / 2.0;
						fNewSeparationSum += fDistance;
						// update the separationsum of cluster i.
						fSeparationSums[i] += (fDistance - fVal1 - fVal2);
						fSeparations[i] = fSeparationSums[i] / (nClusters - 2);
						fRow1[i] = fDistance;
						fDist[i][iMin1] = fDistance;
					}
				}
				fSeparationSums[iMin1] = fNewSeparationSum;
				fSeparations[iMin1] = fNewSeparationSum / (nClusters - 2);
				fSeparationSums[iMin2] = 0;
				merge(iMin1, iMin2, fDist1, fDist2, nClusterID, clusterNodes);
				int iPrev = iMin2;
				// since iMin1 < iMin2 we havenActiveRows[0] >= 0, so the next
				// loop should be save
				while (nClusterID[iPrev].size() == 0) {
					iPrev--;
				}
				nNextActive[iPrev] = nNextActive[iMin2];
			} else {
				merge(iMin1, iMin2, fDist1, fDist2, nClusterID, clusterNodes);
				break;
			}
		}

		for (int i = 0; i < n; i++) {
			if (nClusterID[i].size() > 0) {
				for (int j = i + 1; j < n; j++) {
					if (nClusterID[j].size() > 0) {
						double fDist1 = fDist[i][j];
						if (nClusterID[i].size() == 1) {
							merge(i, j, fDist1, 0, nClusterID, clusterNodes);
						} else if (nClusterID[j].size() == 1) {
							merge(i, j, 0, fDist1, nClusterID, clusterNodes);
						} else {
							merge(i, j, fDist1 / 2.0, fDist1 / 2.0, nClusterID,
									clusterNodes);
						}
						break;
					}
				}
			}
		}
		//add items in own clusters to clusterNodes
		for (int i = 0; i < n; i++){
			if (nClusterID[i].size() == 1){//single item cluster found
				addSingleItem(nClusterID[i].elementAt(0), clusterNodes);
			}
		}
	} // neighborJoining

	/**
	 * Perform clustering using a link method This implementation uses a
	 * priority queue resulting in a O(n^2 log(n)) algorithm
	 * 
	 * @param nClusters
	 *            number of clusters
	 * @param nClusterID
	 * @param clusterNodes
	 */
	void doLinkClustering(int nClusters, Vector<Integer>[] nClusterID,
			Node[] clusterNodes) {
		int nProcessInstances = m_instances.numInstances();
		PriorityQueue<Tuple> queue = new PriorityQueue<Tuple>(nClusters
				* nClusters / 2, new TupleComparator());
		double[][] fDistance0 = new double[nClusters][nClusters];
		double[][] fClusterDistance = null;
		if (m_bDebug) {
			fClusterDistance = new double[nClusters][nClusters];
		}
		for (int i = 0; i < nClusters; i++) {
			fDistance0[i][i] = 0;
			for (int j = i + 1; j < nClusters; j++) {
				fDistance0[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
				fDistance0[j][i] = fDistance0[i][j];
				queue.add(new Tuple(fDistance0[i][j], i, j, 1, 1));
				if (m_bDebug) {
					fClusterDistance[i][j] = fDistance0[i][j];
					fClusterDistance[j][i] = fDistance0[i][j];
				}
			}
		}
		while (nClusters > m_nNumClusters) {
			int iMin1 = -1;
			int iMin2 = -1;
			// find closest two clusters
			if (m_bDebug) {
				/* simple but inefficient implementation */
				double fMinDistance = Double.MAX_VALUE;
				for (int i = 0; i < nProcessInstances; i++) {
					if (nClusterID[i].size() > 0) {
						for (int j = i + 1; j < nProcessInstances; j++) {
							if (nClusterID[j].size() > 0) {
								double fDist = fClusterDistance[i][j];
								if (fDist < fMinDistance) {
									fMinDistance = fDist;
									iMin1 = i;
									iMin2 = j;
								}
							}
						}
					}
				}
				merge(iMin1, iMin2, fMinDistance, fMinDistance, nClusterID,
						clusterNodes);
			} else {
				// use priority queue to find next best pair to cluster
				Tuple t;
				do {
					t = queue.poll();
				} while (t != null
						&& (nClusterID[t.m_iCluster1].size() != t.m_nClusterSize1 || nClusterID[t.m_iCluster2]
								.size() != t.m_nClusterSize2));
				iMin1 = t.m_iCluster1;
				iMin2 = t.m_iCluster2;
				merge(iMin1, iMin2, t.m_fDist, t.m_fDist, nClusterID,
						clusterNodes);
			}
			// merge clusters

			// update distances & queue
			for (int i = 0; i < nProcessInstances; i++) {
				if (i != iMin1 && nClusterID[i].size() != 0) {
					int i1 = Math.min(iMin1, i);
					int i2 = Math.max(iMin1, i);
					double fDistance = getDistance(fDistance0, nClusterID[i1],
							nClusterID[i2]);
					if (m_bDebug) {
						fClusterDistance[i1][i2] = fDistance;
						fClusterDistance[i2][i1] = fDistance;
					}
					queue.add(new Tuple(fDistance, i1, i2, nClusterID[i1]
							.size(), nClusterID[i2].size()));
				}
			}

			nClusters--;
		}
		//add items in own clusters to clusterNodes
		for (int i = 0; i < nProcessInstances; i++){
			if (nClusterID[i].size() == 1){//single item cluster found
				addSingleItem(nClusterID[i].elementAt(0), clusterNodes);
			}
		}
	} // doLinkClustering

	/**
	 * Adds a single item node to a cluster, by creating a node with a filled
	 * leftInstance, and a rightInstance value set to -1.
	 * @param clusterID
	 * 			ID of the item to be single clustered
	 * @param clusterNodes
	 * 			the current cluster node structure
	 */
	void addSingleItem(Integer clusterID, Node[] clusterNodes){
		if (m_bDebug) {
			System.err.println("Adding item in own cluster:  " + clusterID);
			}

			// create new node for cluster
			Node node = new Node();
			node.m_iLeftInstance = clusterID;
			node.m_left = null;
			node.m_iRightInstance = -1;
			node.m_right = null;
				
			clusterNodes[clusterID] = node;
	}
	
	void merge(int iMin1, int iMin2, double fDist1, double fDist2,
			Vector<Integer>[] nClusterID, Node[] clusterNodes) {
		if (m_bDebug) {
			System.err.println("Merging " + iMin1 + " " + iMin2 + " " + fDist1
					+ " " + fDist2);
		}
		System.out.println("Merging " + iMin1 + " " + iMin2 + " " + fDist1
				+ " " + fDist2);
		if (iMin1 > iMin2) {
			int h = iMin1;
			iMin1 = iMin2;
			iMin2 = h;
			double f = fDist1;
			fDist1 = fDist2;
			fDist2 = f;
		}
		nClusterID[iMin1].addAll(nClusterID[iMin2]);
		nClusterID[iMin2].removeAllElements();

		// track hierarchy
		Node node = new Node();
		if (clusterNodes[iMin1] == null) {
			node.m_iLeftInstance = iMin1;
		} else {
			node.m_left = clusterNodes[iMin1];
			clusterNodes[iMin1].m_parent = node;
		}
		if (clusterNodes[iMin2] == null) {
			node.m_iRightInstance = iMin2;
		} else {
			node.m_right = clusterNodes[iMin2];
			clusterNodes[iMin2].m_parent = node;
		}
		if (m_bDistanceIsBranchLength) {
			node.setLength(fDist1, fDist2);
		} else {
			node.setHeight(fDist1, fDist2);
		}
		clusterNodes[iMin1] = node;
	} // merge

	/** calculate distance the first time when setting up the distance matrix **/
	double getDistance0(Vector<Integer> cluster1, Vector<Integer> cluster2) {
		double fBestDist = Double.MAX_VALUE;
		switch (m_nLinkType) {
		case SINGLE:
		case NEIGHBOR_JOINING:
		case CENTROID:
		case COMPLETE:
		case ADJCOMLPETE:
		case AVERAGE:
		case MEAN:
			// set up two instances for distance function
			ProcessInstance instance1 = (ProcessInstance) m_instances.instance(
					cluster1.elementAt(0)).copy();
			ProcessInstance instance2 = (ProcessInstance) m_instances.instance(
					cluster2.elementAt(0)).copy();
			fBestDist = calcDistanceWithFunction(instance1, instance2);
			//fBestDist = m_DistanceFunction.distance(instance1, instance2);
			break;
		case WARD: {
			// finds the distance of the change in caused by merging the
			// cluster.
			// The information of a cluster is calculated as the error sum of
			// squares of the
			// centroids of the cluster and its members.
			double ESS1 = calcESS(cluster1);
			double ESS2 = calcESS(cluster2);
			Vector<Integer> merged = new Vector<Integer>();
			merged.addAll(cluster1);
			merged.addAll(cluster2);
			double ESS = calcESS(merged);
			fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2
					* cluster2.size();
		}
			break;
		}
		return fBestDist;
	} // getDistance0

	/**
	 * calculate the distance between two clusters
	 * 
	 * @param cluster1
	 *            list of indices of instances in the first cluster
	 * @param cluster2
	 *            dito for second cluster
	 * @return distance between clusters based on link type
	 */
	double getDistance(double[][] fDistance, Vector<Integer> cluster1,
			Vector<Integer> cluster2) {
		double fBestDist = Double.MAX_VALUE;
		switch (m_nLinkType) {
		case SINGLE:
			// find single link distance aka minimum link, which is the closest
			// distance between
			// any item in cluster1 and any item in cluster2
			fBestDist = Double.MAX_VALUE;
			for (int i = 0; i < cluster1.size(); i++) {
				int i1 = cluster1.elementAt(i);
				for (int j = 0; j < cluster2.size(); j++) {
					int i2 = cluster2.elementAt(j);
					double fDist = fDistance[i1][i2];
					if (fBestDist > fDist) {
						fBestDist = fDist;
					}
				}
			}
			break;
		case COMPLETE:
		case ADJCOMLPETE:
			// find complete link distance aka maximum link, which is the
			// largest distance between
			// any item in cluster1 and any item in cluster2
			fBestDist = 0;
			for (int i = 0; i < cluster1.size(); i++) {
				int i1 = cluster1.elementAt(i);
				for (int j = 0; j < cluster2.size(); j++) {
					int i2 = cluster2.elementAt(j);
					double fDist = fDistance[i1][i2];
					if (fBestDist < fDist) {
						fBestDist = fDist;
					}
				}
			}
			if (m_nLinkType == COMPLETE) {
				break;
			}
			// calculate adjustment, which is the largest within cluster
			// distance
			double fMaxDist = 0;
			for (int i = 0; i < cluster1.size(); i++) {
				int i1 = cluster1.elementAt(i);
				for (int j = i + 1; j < cluster1.size(); j++) {
					int i2 = cluster1.elementAt(j);
					double fDist = fDistance[i1][i2];
					if (fMaxDist < fDist) {
						fMaxDist = fDist;
					}
				}
			}
			for (int i = 0; i < cluster2.size(); i++) {
				int i1 = cluster2.elementAt(i);
				for (int j = i + 1; j < cluster2.size(); j++) {
					int i2 = cluster2.elementAt(j);
					double fDist = fDistance[i1][i2];
					if (fMaxDist < fDist) {
						fMaxDist = fDist;
					}
				}
			}
			fBestDist -= fMaxDist;
			break;
		case AVERAGE:
			// finds average distance between the elements of the two clusters
			fBestDist = 0;
			for (int i = 0; i < cluster1.size(); i++) {
				int i1 = cluster1.elementAt(i);
				for (int j = 0; j < cluster2.size(); j++) {
					int i2 = cluster2.elementAt(j);
					fBestDist += fDistance[i1][i2];
				}
			}
			fBestDist /= (cluster1.size() * cluster2.size());
			break;
		case MEAN: {
			// calculates the mean distance of a merged cluster (akak
			// Group-average agglomerative clustering)
			Vector<Integer> merged = new Vector<Integer>();
			merged.addAll(cluster1);
			merged.addAll(cluster2);
			fBestDist = 0;
			for (int i = 0; i < merged.size(); i++) {
				int i1 = merged.elementAt(i);
				for (int j = i + 1; j < merged.size(); j++) {
					int i2 = merged.elementAt(j);
					fBestDist += fDistance[i1][i2];
				}
			}
			int n = merged.size();
			fBestDist /= (n * (n - 1.0) / 2.0);
		}
			break;
		case CENTROID:
			// finds the distance of the centroids of the clusters
			double[] fValues1 = new double[m_instances.numAttributes()];
			for (int i = 0; i < cluster1.size(); i++) {
				ProcessInstance instance = (ProcessInstance) m_instances
						.instance(cluster1.elementAt(i));
				for (int j = 0; j < m_instances.numAttributes(); j++) {
					fValues1[j] += instance.value(j);
				}
			}
			
			String[] fStrValues1 = new String[m_instances.numStrAttributes()];
			
			for (int i = 0; i < m_instances.numStrAttributes(); i++){
				ArrayList<String> atts = new ArrayList<String>(m_instances.numInstances());
				//go attribute-wise and collect them
				for (int j = 0; j < cluster1.size(); j++) {
					atts.add(((ProcessInstance)m_instances.instance(cluster1.elementAt(j))).strValue(i));
				}
				//alle attribute untereinander vergleichen und werte aufaddieren
				HashMap<String, Double> matrix = new HashMap<String, Double>(m_instances.numInstances());
				for (int k = 0; k < atts.size(); k++){
					for (int j = k + 1; j < atts.size(); j++){
						//compare and add to matrix
						String val1 = atts.get(k);
						String val2 = atts.get(j);
						Double similarity = ((WeightedEditDistance) m_StringDistanceFunction).getStringDistance(val1, val2);
						if (matrix.get(val1) == null){
							matrix.put(val1, similarity);
						} else {
							matrix.put(val1, matrix.get(val1) + similarity);
						}
					}
				}
					
				//calculate best result
				double currentMin = 1;
				int bestKey = 0;
				int k = 0;
				//iterate over array and take highest value
				for (String key : matrix.keySet()){
					if (currentMin >= matrix.get(key)){
						currentMin = matrix.get(key);
						bestKey = k;
					}
					k++;
				}
					
				fStrValues1[i] = m_instances.getInstance(cluster1.elementAt(bestKey)).strValue(i);
			}

			double[] fValues2 = new double[m_instances.numAttributes()];
			for (int i = 0; i < cluster2.size(); i++) {
				ProcessInstance instance = (ProcessInstance) m_instances
						.instance(cluster2.elementAt(i));
				for (int j = 0; j < m_instances.numAttributes(); j++) {
					fValues2[j] += instance.value(j);
				}
			}
			
			String[] fStrValues2 = new String[m_instances.numStrAttributes()];
			
			for (int i = 0; i < m_instances.numStrAttributes(); i++){
				ArrayList<String> atts = new ArrayList<String>(m_instances.numInstances());
				//go attribute-wise and collect them
				for (int j = 0; j < cluster2.size(); j++) {
					atts.add(((ProcessInstance)m_instances.instance(cluster2.elementAt(j))).strValue(i));
				}
				//alle attribute untereinander vergleichen und werte aufaddieren
				HashMap<String, Double> matrix = new HashMap<String, Double>(m_instances.numInstances());
				for (int k = 0; k < atts.size(); k++){
					for (int j = k + 1; j < atts.size(); j++){
						//compare and add to matrix
						String val1 = atts.get(k);
						String val2 = atts.get(j);
						Double distance = ((WeightedEditDistance) m_StringDistanceFunction).getStringDistance(val1, val2);
						if (matrix.get(val1) == null){
							matrix.put(val1, distance);
						} else {
							matrix.put(val1, matrix.get(val1) + distance);
						}
					}
				}
					
				//calculate best result
				double currentMin = 1;
				int bestKey = 0;
				int k = 0;
				//iterate over array and take smallest value
				for (String key : matrix.keySet()){
					if (currentMin >= matrix.get(key)){
						currentMin = matrix.get(key);
						bestKey = k;
					}
					k++;
				}
					
				fStrValues2[i] = m_instances.getInstance(cluster2.elementAt(bestKey)).strValue(i);
			}
			
			for (int j = 0; j < m_instances.numAttributes(); j++) {
				fValues1[j] /= cluster1.size();
				fValues2[j] /= cluster2.size();
			}
			// set up two instances for distance function
			ProcessInstance instance1 = (ProcessInstance) m_instances.instance(
					0).copy();
			ProcessInstance instance2 = (ProcessInstance) m_instances.instance(
					0).copy();
			for (int j = 0; j < m_instances.numAttributes(); j++) {
				instance1.setValue(j, fValues1[j]);
				instance2.setValue(j, fValues2[j]);
			}
			for (int j = 0; j < m_instances.numStrAttributes(); j++) {
				instance1.setStrValue(j, fStrValues1[j]);
				instance2.setStrValue(j, fStrValues2[j]);
			}
			fBestDist = calcDistanceWithFunction(instance1, instance2);
			//fBestDist = m_DistanceFunction.distance(instance1, instance2);
			break;
		case WARD: {
			// finds the distance of the change in caused by merging the
			// cluster.
			// The information of a cluster is calculated as the error sum of
			// squares of the
			// centroids of the cluster and its members.
			double ESS1 = calcESS(cluster1);
			double ESS2 = calcESS(cluster2);
			Vector<Integer> merged = new Vector<Integer>();
			merged.addAll(cluster1);
			merged.addAll(cluster2);
			double ESS = calcESS(merged);
			fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2
					* cluster2.size();
		}
			break;
		}
		return fBestDist;
	} // getDistance

	/** calculated error sum-of-squares for instances wrt centroid **/
	double calcESS(Vector<Integer> cluster) {
		double[] fValues1 = new double[m_instances.numAttributes()];
		for (int i = 0; i < cluster.size(); i++) {
			ProcessInstance instance = (ProcessInstance) m_instances
					.instance(cluster.elementAt(i));
			for (int j = 0; j < m_instances.numAttributes(); j++) {
				fValues1[j] += instance.value(j);
			}
			
		}
		for (int j = 0; j < m_instances.numAttributes(); j++) {
			fValues1[j] /= cluster.size();
		}
		
		//do the same for string
		String[] fStrValues1 = new String[m_instances.numStrAttributes()];
		
		for (int i = 0; i < m_instances.numStrAttributes(); i++){
			ArrayList<String> atts = new ArrayList<String>(m_instances.numInstances());
			//go attribute-wise and collect them
			for (int j = 0; j < cluster.size(); j++) {
				atts.add(((ProcessInstance)m_instances.instance(cluster.elementAt(j))).strValue(i));
			}
			//alle attribute untereinander vergleichen und werte aufaddieren
			HashMap<String, Double> matrix = new HashMap<String, Double>(m_instances.numInstances());
			for (int k = 0; k < atts.size(); k++){
				for (int j = k + 1; j < atts.size(); j++){
					//compare and add to matrix
					String val1 = atts.get(k);
					String val2 = atts.get(j);
					Double distance = ((WeightedEditDistance) m_StringDistanceFunction).getStringDistance(val1, val2);
					if (matrix.get(val1) == null){
						matrix.put(val1, distance);
					} else {
						matrix.put(val1, matrix.get(val1) + distance);
					}
				}
			}
				
			//calculate best result
			double currentMin = 1;
			int bestKey = 0;
			int k = 0;
			//iterate over array and take highest value
			for (String key : matrix.keySet()){
				if (currentMin <= matrix.get(key)){
					currentMin = matrix.get(key);
					bestKey = k;
				}
				k++;
			}
				
			fStrValues1[i] = m_instances.getInstance(cluster.elementAt(bestKey)).strValue(i);
		}
		// set up two instances for distance function
		ProcessInstance centroid = (ProcessInstance) m_instances.instance(
				cluster.elementAt(0)).copy();
		for (int j = 0; j < m_instances.numAttributes(); j++) {
			centroid.setValue(j, fValues1[j]);
		}
		//for strings
		for (int j = 0; j < m_instances.numStrAttributes(); j++) {
			centroid.setStrValue(j, fStrValues1[j]);
		}
		double fESS = 0;
		for (int i = 0; i < cluster.size(); i++) {
			ProcessInstance instance = (ProcessInstance) m_instances
					.instance(cluster.elementAt(i));
			fESS += calcDistanceWithFunction(centroid, instance);
			//fESS += m_DistanceFunction.distance(centroid, instance);
		}
		return fESS / cluster.size();
	} // calcESS

	@Override
	/** instances are assigned a cluster by finding the instance in the training data 
	 * with the closest distance to the instance to be clustered. The cluster index of
	 * the training data point is taken as the cluster index.
	 */
	public int clusterInstance(Instance instance) throws Exception {
		if (m_instances.numInstances() == 0) {
			return 0;
		}
		double fBestDist = Double.MAX_VALUE;
		int iBestProcessInstance = -1;
		for (int i = 0; i < m_instances.numInstances(); i++) {
			double fDist = calcDistanceWithFunction((ProcessInstance) instance, m_instances.getInstance(i));
			//double fDist = m_DistanceFunction.distance(instance,m_instances.instance(i));
			if (fDist < fBestDist) {
				fBestDist = fDist;
				iBestProcessInstance = i;
			}
		}
		return m_nClusterNr[iBestProcessInstance];
	}

	@Override
	/** create distribution with all clusters having zero probability, except the
	 * cluster the instance is assigned to.
	 */
	public double[] distributionForInstance(Instance instance) throws Exception {
		if (numberOfClusters() == 0) {
			double[] p = new double[1];
			p[0] = 1;
			return p;
		}
		double[] p = new double[numberOfClusters()];
		p[clusterInstance((ProcessInstance) instance)] = 1.0;
		return p;
	}

	public static void main(String[] argv) {
		runClusterer(new HierarchicalProcessClusterer(), argv);
	}

	public String graph() throws Exception {
		if (numberOfClusters() == 0) {
			return "Newick:(no,clusters)";
		}
		int attIndex = m_instances.classIndex();
		if (attIndex < 0) {
			// try find a string, or last attribute otherwise
			attIndex = 0;
			while (attIndex < m_instances.numAttributes() - 1) {
				if (m_instances.attribute(attIndex).isString()) {
					break;
				}
				attIndex++;
			}
		}
		String sNewick = null;
		if (m_instances.attribute(attIndex).isString()) {
			sNewick = m_clusters[0].toString(attIndex);
		} else {
			sNewick = m_clusters[0].toString2(attIndex);
		}
		return "Newick:" + sNewick;
	}
}// class HierarchicalProcessClusterer
