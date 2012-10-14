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
import java.util.List;

/**
 * Class representing a cluster of process models in the form of a tree
 * 
 * @author Cindy Fähnrich
 * 
 */
public class ClusterTree<T> implements Cloneable{

	/** the root of the cluster */
	private ClusterNode<ProcessInstances> rootElement;

	/**
	 * Default constructor
	 */
	public ClusterTree() {
		super();
	}

	/**
	 * Return the root Node of the tree.
	 * 
	 * @return the root element.
	 */
	public ClusterNode<ProcessInstances> getRootElement() {
		return this.rootElement;
	}

	/**
	 * Set the root Element for the tree.
	 * 
	 * @param rootElement
	 *            the root element to set.
	 */
	public void setRootElement(ClusterNode<ProcessInstances> rootElement) {
		this.rootElement = rootElement;
	}

	/**
	 * Returns the Tree<T> as a List of ClusterNode<T> objects. The elements of
	 * the List are generated from a pre-order traversal of the tree.
	 * 
	 * @return a List<ClusterNode<T>>.
	 */
	public List<ClusterNode<ProcessInstances>> toList() {
		List<ClusterNode<ProcessInstances>> list = new ArrayList<ClusterNode<ProcessInstances>>();
		walk(rootElement, list);
		return list;
	}

	/**
	 * Returns a String representation of the Tree. The elements are generated
	 * from a pre-order traversal of the Tree.
	 * 
	 * @return the String representation of the Tree.
	 */
	public String toString() {
		return toList().toString();
	}
	
	/**
	 * Returns all nodes in the tree on a given level. Level 0 returns the 
	 * root element's children.
	 * @param level
	 * 			to get nodes for
	 * @return an {@link ArrayList} of {@link ClusterNode}s from the corresponding level
	 */
	public ArrayList<ClusterNode<ProcessInstances>> getNodesOnLevel(int level){
		ArrayList<ClusterNode<ProcessInstances>> nodes = new ArrayList<ClusterNode<ProcessInstances>>();
		nodes.addAll(this.rootElement.getNodesOnLevel(0, level));
		return nodes;
	}

	/**
	 * Returns a new tree that is a subtree from the current tree, where the leafs are
	 * clusters that have the given size as minimum size.
	 * @param clustersize as minimal cluster size of the leafs
	 * @return a clustertree with leafs that have the given cluster size as minimum size
	 */
	public ClusterTree<T> getSubtreeWithMinClusterSize(int minsize){
		
		boolean smaller = false;
		if (minsize <= 1) {//if 1, return tree as it is
			return this;
		}
		
		ClusterTree<T> subtree = new ClusterTree<T>();
		ClusterNode<ProcessInstances> root = new ClusterNode<ProcessInstances>();
		subtree.setRootElement(root);
		
		if (minsize >= this.getRootElement().getClusterSize()){//if the size of all elements, return only root element
			ClusterNode<ProcessInstances> node = (ClusterNode<ProcessInstances>) this.getRootElement().getCluster();
			subtree.setRootElement(node);
			return subtree;
		}
		
		
		int level = 0;
		while (!smaller){
			level += 1;
			ArrayList<ClusterNode<ProcessInstances>> nodes = getNodesOnLevel(level);
			for (ClusterNode<ProcessInstances> node : nodes){
				if (node.getClusterSize() < minsize){
					smaller = true;
				}
			}
		}
		//take now one level above and cut subtrees
		level -= 1;
		subtree = copyTreeUntilLevel(level);
		return subtree;
	}
	
	/**
	 * Copies the current clustertree from root element to the given level
	 * @param level
	 * 			up to which to copy
	 * @return
	 * 		a new cluster tree with the level as depth
	 */
	public ClusterTree<T> copyTreeUntilLevel(int level){
		//copy full tree
		ClusterTree<T> subtree = new ClusterTree<T>();
		subtree.setRootElement((ClusterNode<ProcessInstances>) this.rootElement.copy());
		//truncate and make new leafs
		ArrayList<ClusterNode<ProcessInstances>> newLeafs = subtree.getNodesOnLevel(level);
		//get to each leaf and make a cluster from it
		for (ClusterNode<ProcessInstances> leaf : newLeafs){
			ClusterNode<ProcessInstances> newLeaf = (ClusterNode<ProcessInstances>) leaf.getCluster();;
			leaf.setChildren(null);
			leaf.setData(newLeaf.getData());
		}
		return subtree;
	}
	
	/**
	 * Walks the Tree in pre-order style. This is a recursive method, and is
	 * called from the toList() method with the root element as the first
	 * argument. It appends to the second argument, which is passed by reference
	 * * as it recurses down the tree.
	 * 
	 * @param element
	 *            the starting element.
	 * @param list
	 *            the output of the walk.
	 */
	private void walk(ClusterNode<ProcessInstances> element, List<ClusterNode<ProcessInstances>> list) {
		list.add(element);
		for (ClusterNode<ProcessInstances> data : element.getChildren()) {
			walk(data, list);
		}
	}
}
