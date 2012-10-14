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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData;

import org.jbpt.graph.Edge;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.Activity;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.Event;
import org.jbpt.pm.Gateway;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * Interface for classes that can be used as {@link IUnit} input and output.
 * The id of the used {@link Representation} as well as the result value of the last {@link IUnit} of
 * the {@link IUnitChain} is stored. Furthermore, a set of process model metrics is stored.
 * 
 * @author Tobias Hoppe
 *
 */
public interface IUnitDataProcessMetrics<V extends Object> extends IUnitData<V> {

	/**
	 * @return the Average Connector Degree
	 */
	public double getAverageConnectorDegree();

	/**
	 * @return the Coefficient of Connectivity
	 */
	public double getCoefficientOfConnectivity();

	/**
	 * @return the Coefficient of Network Complexity
	 */
	public double getCoefficientOfNetworkComplexity();

	/**
	 * @return the Control Flow Complexity
	 */
	public int getControlFlowComplexity();

	/**
	 * @return the Cross Connectivity
	 */
	public double getCrossConnectivity();

	/**
	 * @return the Cycling
	 */
	public double getCycling();

	/**
	 * @return the Cyclomatic Number
	 */
	public int getCyclomaticNumber();

	/**
	 * @return the Density
	 */
	public double getDensity();

	/**
	 * @return the Density related to the number of {@link Gateway}s
	 */
	public double getDensityRelatedToNumberOfGateways();

	/**
	 * @return the Depth
	 */
	public int getDepth();

	/**
	 * @return the Diameter
	 */
	public int getDiameter();

	/**
	 * @return the Max Connector Degree
	 */
	public int getMaxConnectorDegree();
	
	/**
	 * @return the modelPath
	 */
	public String getModelPath();

	/**
	 * @return the number of {@link Activity}s
	 */
	public int getNumberOfActivities();

	/**
	 * @return the number of And-Joins
	 */
	public int getNumberOfAndJoins();

	/**
	 * @return the number of And-Splits
	 */
	public int getNumberOfAndSplits();

	/**
	 * @return the number of {@link DataNode}s
	 */
	public int getNumberOfDataNodes();

	/**
	 * @return the number of edges
	 */
	public int getNumberOfEdges();

	/**
	 * @return the number of End-{@link Event}s
	 */
	public int getNumberOfEndEvents();

	/**
	 * @return the number of {@link Event}s
	 */
	public int getNumberOfEvents();

	/**
	 * @return the number of {@link Gateway}s
	 */
	public int getNumberOfGateways();

	/**
	 * @return the number of Internal-{@link Event}s
	 */
	public int getNumberOfInternalEvents();

	/**
	 * @return the number of nodes
	 */
	public int getNumberOfNodes();

	/**
	 * @return the number of Or-Joins
	 */
	public int getNumberOfOrJoins();

	/**
	 * @return the number of Or-Splits
	 */
	public int getNumberOfOrSplits();

	/**
	 * @return the number of Roles
	 */
	public int getNumberOfRoles();

	/**
	 * @return the number of Start-{@link Event}s
	 */
	public int getNumberOfStartEvents();

	/**
	 * @return the number of Xor-Joins
	 */
	public int getNumberOfXorJoins();

	/**
	 * @return the number of Xor-Splits
	 */
	public int getNumberOfXorSplits();

	/**
	 * @return the Separability
	 */
	public double getSeparability();

	/**
	 * @param averageConnectorDegree the Average Connector Degree to set
	 */
	public void setAverageConnectorDegree(double averageConnectorDegree);

	/**
	 * @param coefficientOfConnectivity the Coefficient of Connectivity to set
	 */
	public void setCoefficientOfConnectivity(double coefficientOfConnectivity);

	/**
	 * @param coefficientOfNetworkComplexity the Coefficient of Network Complexity to set
	 */
	public void setCoefficientOfNetworkComplexity(double coefficientOfNetworkComplexity);

	/**
	 * @param controlFlowComplexity the Control Flow Complexity to set
	 */
	public void setControlFlowComplexity(int controlFlowComplexity);

	/**
	 * @param crossConnectivity the Cross Connectivity to set
	 */
	public void setCrossConnectivity(double crossConnectivity);

	/**
	 * @param cycling the Cycling value to set
	 */
	public void setCycling(double cycling);

	/**
	 * @param cyclomaticNumber the Cyclomatic Number to set
	 */
	public void setCyclomaticNumber(int cyclomaticNumber);

	/**
	 * @param density the Density to set
	 */
	public void setDensity(double density);

	/**
	 * @param density the Density related to the number of {@link Gateway}s to set
	 */
	public void setDensityRelatedToNumberOfGateways(double density);

	/**
	 * @param depth the Depth to set
	 */
	public void setDepth(int depth);

	/**
	 * @param diameter the Diameter to set
	 */
	public void setDiameter(int diameter);

	/**
	 * @param maxConnectorDegree the Max Connector Degree to set
	 */
	public void setMaxConnectorDegree(int maxConnectorDegree);

	/**
	 * @param modelPath the Model Path to set
	 */
	public void setModelPath(String modelPath);

	/**
	 * @param numberOfActivities the number of {@link Activity}s to set
	 */
	public void setNumberOfActivities(int numberOfActivities);

	/**
	 * @param numberOfAndJoins the number of AND-Joins to set
	 */
	public void setNumberOfAndJoins(int numberOfAndJoins);

	/**
	 * @param numberOfAndSplits the number of AND-Splits to set
	 */
	public void setNumberOfAndSplits(int numberOfAndSplits);

	/**
	 * @param numberOfDataNodes the number of {@link DataNode}s to set
	 */
	public void setNumberOfDataNodes(int numberOfDataNodes);

	/**
	 * @param numberOfEdges the number of edges(={@link Edge}) to set
	 */
	public void setNumberOfEdges(int numberOfEdges);

	/**
	 * @param numberOfEndEvents the number of End-{@link Event}s to set
	 */
	public void setNumberOfEndEvents(int numberOfEndEvents);

	/**
	 * @param numberOfEvents the number of {@link Event}s to set
	 */
	public void setNumberOfEvents(int numberOfEvents);

	/**
	 * @param numberOfGateways the number of {@link Gateway}s to set
	 */
	public void setNumberOfGateways(int numberOfGateways);

	/**
	 * @param numberOfInternalEvents the number of Internal-{@link Event}s to set
	 */
	public void setNumberOfInternalEvents(int numberOfInternalEvents);

	/**
	 * @param numberOfNodes the number of nodes(={@link Vertex}) to set
	 */
	public void setNumberOfNodes(int numberOfNodes);

	/**
	 * @param numberOfOrJoins the number of OR-Joins to set
	 */
	public void setNumberOfOrJoins(int numberOfOrJoins);

	/**
	 * @param numberOfOrSplits the number of OR-Splits to set
	 */
	public void setNumberOfOrSplits(int numberOfOrSplits);

	/**
	 * @param numberOfRoles the number of Roles to set
	 */
	public void setNumberOfRoles(int numberOfRoles);

	/**
	 * @param numberOfStartEvents the number of Start-{@link Event}s to set
	 */
	public void setNumberOfStartEvents(int numberOfStartEvents);

	/**
	 * @param numberOfXorJoins the number of XOR-Joins to set
	 */
	public void setNumberOfXorJoins(int numberOfXorJoins);

	/**
	 * @param numberOfXorSplits the number of XOR-Splits to set
	 */
	public void setNumberOfXorSplits(int numberOfXorSplits);

	/**
	 * @param separability the Separability to set
	 */
	public void setSeparability(double separability);

	/**
	 * @param delimiter split element for CSV file values in {@link UnitDataProcessMetrics#toCsv()}
	 * @return a {@link String} representation of this instance as list of values separated by given delimiter
	 */
	public String toCsv(String delimiter);

}
