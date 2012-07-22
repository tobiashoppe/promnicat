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
import java.util.HashMap;

import weka.core.FastVector;

/**
 * Represents a node of the {@link ClusterTree<T>} class. The ClusterNode<T> is
 * a container for the clustered {@link ProcessInstances}.
 * 
 * @author Cindy Fähnrich
 * 
 */
public class ClusterNode<T> {

	/** the clustered data */
	public ProcessInstances data;
	
	/** the children of this node --> "subclusters" */
	public ArrayList<ClusterNode<T>> children;

	public ClusterNode<T> parent;

	public String name = "";
	public String getName() {
		return name;
	}

	/**
	 * Setter for cluster name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** the name of this cluster */
	public HashMap<String, Object> clusterName = new HashMap<String, Object>();
	/**
	 * Default constructor
	 */
	public ClusterNode() {
		super();
		children = new ArrayList<ClusterNode<T>>();
	}
	
	public ClusterNode<T> getParent() {
		return parent;
	}

	public void setParent(ClusterNode<T> parent) {
		this.parent = parent;
	}

	/**
	 * Convenience constructor to create a ClusterNode<T> with an instance of {@link ProcessInstances}.
	 * 
	 * @param data
	 *            an instance of {@link ProcessInstances}.
	 */
	public ClusterNode(ProcessInstances data) {
		this();
		setData(data);
	}
	
	/** Returns the values for the cluster name
	 * 
	 * @return the values of the cluster name
	 */
	public HashMap<String, Object> getClusterName(){
		return this.clusterName;
	}

	public void setClusterName(HashMap<String, Object> name){
		this.clusterName = name;
	}
	
	/**
	 * Return the children of ClusterNode<T>. The Tree<T> is represented by a
	 * single root ClusterNode<T> whose children are represented by a
	 * List<ClusterNode<T>>. Each of these ClusterNode<T> elements in the List
	 * can have children. The getChildren() method will return the children of a
	 * ClusterNode<T>.
	 * 
	 * @return the children of ClusterNode<T>
	 */
	public ArrayList<ClusterNode<T>> getChildren() {
		if (this.children == null) {
			return new ArrayList<ClusterNode<T>>();
		}
		return this.children;
	}

	/**
	 * Sets the children of a ClusterNode<T> object. See docs for getChildren()
	 * for more information.
	 * 
	 * @param children
	 *            the List<ClusterNode<T>> to set.
	 */
	public void setChildren(ArrayList<ClusterNode<T>> children) {
		this.children = children;
	}

	/**
	 * Returns the number of immediate children of this ClusterNode<T>.
	 * 
	 * @return the number of immediate children.
	 */
	public int getNumberOfChildren() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	/**
	 * Adds a child to the list of children for this ClusterNode<T>. The
	 * addition of the first child will create a new List<ClusterNode<T>>.
	 * 
	 * @param child
	 *            a ClusterNode<T> object to set.
	 */
	public void addChild(ClusterNode<T> child) {
		children.add(child);
	}

	/**
	 * Inserts a ClusterNode<T> at the specified position in the child list.
	 * Will * throw an ArrayIndexOutOfBoundsException if the index does not
	 * exist.
	 * 
	 * @param index
	 *            the position to insert at.
	 * @param child
	 *            the ClusterNode<T> object to insert.
	 * @throws IndexOutOfBoundsException
	 *             if thrown.
	 */
	public void insertChildAt(int index, ClusterNode<T> child)
			throws IndexOutOfBoundsException {
		if (index == getNumberOfChildren()) {
			// this is really an append
			addChild(child);
			return;
		} else {
			children.get(index); // just to throw the exception, and stop here
			children.add(index, child);
		}
	}

	/**
	 * Remove the ClusterNode<T> element at index index of the
	 * List<ClusterNode<T>>.
	 * 
	 * @param index
	 *            the index of the element to delete.
	 * @throws IndexOutOfBoundsException
	 *             if thrown.
	 */
	public void removeChildAt(int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	/** returns the {@link ProcessInstances} contained in this node. The data
	 * is only filled for leafs.
	 * @return
	 * 		the contained {@link ProcessInstances}
	 */
	public ProcessInstances getData() {
		return this.data;
	}

	/** Sets the {@link ProcessInstances} contained in this node. Only to be
	 * set for the leafs of the tree
	 * @param
	 * 		the {@link ProcessInstances} to set
	 */
	public void setData(ProcessInstances data) {
		this.data = data;
	}

	/**
	 * Returns the size of the current subtree, having the current node as root element
	 * (also counts for size)
	 * @return the size of the current subtree
	 */
	@SuppressWarnings("rawtypes")
	public int getSizeOfSubtree(){
		int size = 1;
		for (ClusterNode child : children){
			size += child.getSizeOfSubtree();
		}
		return size;
	}
	
	/**
	 * Returns the maximum depth of the current node's subtree
	 * @return the maximum depth of the current node's subtree
	 */
	@SuppressWarnings("rawtypes")
	public int getMaxDepthOfSubtree(){
		int maxDepth = 0;
		for (ClusterNode child : children){
			int depth = child.getMaxDepthOfSubtree();
			if (depth > maxDepth){
				maxDepth = depth;
			}
		}
		maxDepth += 1;
		return maxDepth;
	}
	
	/**
	 * Returns a new cluster containing all the instances of the sub-nodes/-clusters,
	 * since the actual values are only stored in the leafs of the tree. 
	 * @return
	 * 		a new clusterNode having the contained {@link ProcessInstances} as data
	 */
	public ClusterNode<ProcessInstances> getCluster(){
		ClusterNode<ProcessInstances> node = new ClusterNode<ProcessInstances>();
		ProcessInstances cluster;
		if (getData() != null){
			cluster = new ProcessInstances("", getData().getAttributes(), getData().getStringAttributes(), 0);
		} else {
			cluster = new ProcessInstances("", new FastVector(), new FastVector(), 0);
		}
		for (ClusterNode<ProcessInstances> leaf : getLeafs()){//get all leafs and take the processInstance
			FastVector insts = leaf.getData().getInstances();
			for (int i = 0; i<insts.size(); i++){
				cluster.addInstance((ProcessInstance)insts.elementAt(i));
			}
		}
		node.setData(cluster);
		return node;
	}
	
	/**
	 * Returns an ArrayList containing all the leafs of the current ClusterNode
	 * @return ArrayList with leafs
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ClusterNode<ProcessInstances>> getLeafs(){
		ArrayList<ClusterNode<ProcessInstances>> leafs = new ArrayList<ClusterNode<ProcessInstances>>();
		if (children.size() == 0){//leaf
			leafs.add((ClusterNode<ProcessInstances>) this);
		} else {//have a look at children
			for (ClusterNode<T> leaf : children){
				leafs.addAll(leaf.getLeafs());
			}
		}
		return leafs;
	}
	
	/**
	 * Returns all nodes on a specific hierarchical level
	 * @param currentLevel
	 * 			of the current node
	 * @param level
	 * 			of which the nodes are searched
	 * @return
	 * 		an {@link ArrayList} of ClusterNodes with the nodes of the demanded level
	 */
	public ArrayList<ClusterNode<T>> getNodesOnLevel(int currentLevel, int level){
		ArrayList<ClusterNode<T>> nodes = new ArrayList<ClusterNode<T>>();
		if (currentLevel == level){
			for (ClusterNode<T> child : children){
				nodes.add(child);
			}
			return nodes;
		}
		currentLevel += 1;
		for (ClusterNode<T> child: children){
			nodes.addAll((ArrayList<ClusterNode<T>>)child.getNodesOnLevel(currentLevel, level));
		}
		return nodes;
	}
	
	/**
	 * Method to create a copy of the current node
	 * @return a copy of the current node
	 */
	public ClusterNode<ProcessInstances> copy(){
		ClusterNode<ProcessInstances> newNode = new ClusterNode<ProcessInstances>();
		newNode.setData(this.getData());
		for (ClusterNode<T> child : children){
			newNode.addChild(child.copy());
		}
		return newNode;
	}
	
	/**
	 * Returns the actual amount of items that are contained in the cluster
	 * (thus, how many leafs this node's subtree has).
	 * @return
	 * 		the amount of items contained in the cluster
	 */
	public int getClusterSize(){
		int clustersize = 0;
		
		if ((getData() == null) || (getData().getInstances().size() == 0) || (getData().getFirstInstance().process == null)){ //this is a leaf, so cluster size is 1
			for (ClusterNode<T> child : children){
				clustersize += child.getClusterSize();
			}
			return clustersize;
		} 

		clustersize = 1;
		return clustersize;

	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(getData().toString()).append(",[");
		int i = 0;
		for (ClusterNode<T> e : getChildren()) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(e.getData().toString());
			i++;
		}
		sb.append("]").append("}");
		return sb.toString();
	}
}
