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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.clustering;

import java.util.Map;

/**
 * A container for cluster attributes. 
 * 
 * @author Tobias Metzke
 *
 */
public class ProcessEvolutionClusteringConfiguration {

	private Map<String, Double> numericAttributes;
	private String linkType;
	private int numClusters;
	
	public ProcessEvolutionClusteringConfiguration(Map<String, Double> numericAttributes2, String linkType, int numClusters) {
		this.numClusters = numClusters;
		this.linkType = linkType;
		this.numericAttributes = numericAttributes2;
	}

	/**
	 * @return the numeric attributes to consider when clustering
	 */
	public Map<String, Double> getNumericAttributes() {
		return numericAttributes;
	}

	/**
	 * @return the type of link algorithm to use in the clustering
	 */
	public String getLinkType() {
		return linkType;
	}

	/**
	 * @return the number of clusters the clusterer must divide the models into.
	 */
	public int getNumClusters() {
		return numClusters;
	}
}
