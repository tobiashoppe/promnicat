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

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * {@link IUnitChain} result type implementation containing the {@link Representation} id of the used 
 * jBPT {@link ProcessModel} and the result value of the last {@link IUnit} in the {@link IUnitChain}.
 * Furthermore, a large set of process model metrics is stored.
 * 
 * @author Tobias Hoppe
 *
 */
public class UnitDataProcessMetrics<V extends Object> extends UnitData<V> implements IUnitDataProcessMetrics<V> {
	
	private String modelPath = "";
	
	private double averageConnectorDegree = 0;
	private double coefficientOfConnectivity = 0;
	private double coefficientOfNetworkComplexity = 0;
	private int controlFlowComplexity = 0;
	private double crossConnectivity = 0;
	private double cycling = 0;
	private int cyclomaticNumber = 0;
	private double density = 0;
	private double densityRelatedToNumberOfGateways = 0;
	private int depth = 0;
	private int diameter = 0;
	private int maxConnectorDegree = 0;
	private int numberOfActivities = 0;
	private int numberOfAndJoins = 0;
	private int numberOfAndSplits = 0;
	private int numberOfDataNodes = 0;
	private int numberOfEdges = 0;
	private int numberOfEndEvents = 0;
	private int numberOfEvents = 0;
	private int numberOfGateways = 0;
	private int numberOfInternalEvents = 0;
	private int numberOfNodes = 0;
	private int numberOfOrJoins = 0;
	private int numberOfOrSplits = 0;
	private int numberOfRoles = 0;
	private int numberOfStartEvents = 0;
	private int numberOfXorJoins = 0;
	private int numberOfXorSplits = 0;
	private double separability = 0;

	/**
	 * Creates an empty result with <code>null</code> elements.
	 */
	public UnitDataProcessMetrics() {
		super();
	}

	/**
	 * Creates a result type with the given value as result and <code>null</code>
	 * as the database id of the used process model.
	 *
	 * @param value the result of the last {@link IUnit}
	 */
	public UnitDataProcessMetrics(V value) {
		super(value);
	}

	/**
	 * A result type with the given values.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 */
	public UnitDataProcessMetrics(V value, String dbId) {
		super(value, dbId);
	}

	@Override
	public double getAverageConnectorDegree() {
		return averageConnectorDegree;
	}
	
	@Override
	public double getCoefficientOfConnectivity() {
		return coefficientOfConnectivity;
	}
	
	@Override
	public double getCoefficientOfNetworkComplexity() {
		return coefficientOfNetworkComplexity;
	}
	
	@Override
	public int getControlFlowComplexity() {
		return controlFlowComplexity;
	}
	
	@Override
	public double getCrossConnectivity() {
		return crossConnectivity;
	}
	
	@Override
	public double getCycling() {
		return cycling;
	}

	@Override
	public int getCyclomaticNumber() {
		return cyclomaticNumber;
	}

	@Override
	public double getDensity() {
		return density;
	}

	@Override
	public double getDensityRelatedToNumberOfGateways() {
		return densityRelatedToNumberOfGateways;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public int getDiameter() {
		return diameter;
	}

	@Override
	public int getMaxConnectorDegree() {
		return maxConnectorDegree;
	}

	@Override
	public String getModelPath() {
		return modelPath;
	}

	@Override
	public int getNumberOfActivities() {
		return numberOfActivities;
	}

	@Override
	public int getNumberOfAndJoins() {
		return numberOfAndJoins;
	}

	@Override
	public int getNumberOfAndSplits() {
		return numberOfAndSplits;
	}

	@Override
	public int getNumberOfDataNodes() {
		return numberOfDataNodes;
	}

	@Override
	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	@Override
	public int getNumberOfEndEvents() {
		return numberOfEndEvents;
	}

	@Override
	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	@Override
	public int getNumberOfGateways() {
		return numberOfGateways;
	}

	@Override
	public int getNumberOfInternalEvents() {
		return numberOfInternalEvents;
	}

	@Override
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	@Override
	public int getNumberOfOrJoins() {
		return numberOfOrJoins;
	}

	@Override
	public int getNumberOfOrSplits() {
		return numberOfOrSplits;
	}

	@Override
	public int getNumberOfRoles() {
		return numberOfRoles;
	}

	@Override
	public int getNumberOfStartEvents() {
		return numberOfStartEvents;
	}

	@Override
	public int getNumberOfXorJoins() {
		return numberOfXorJoins;
	}

	@Override
	public int getNumberOfXorSplits() {
		return numberOfXorSplits;
	}

	@Override
	public double getSeparability() {
		return separability;
	}

	@Override
	public void setAverageConnectorDegree(double averageConnectorDegree) {
		this.averageConnectorDegree = averageConnectorDegree;
	}

	@Override
	public void setCoefficientOfConnectivity(double coefficientOfConnectivity) {
		this.coefficientOfConnectivity = coefficientOfConnectivity;
	}

	@Override
	public void setCoefficientOfNetworkComplexity(double coefficientOfNetworkComplexity) {
		this.coefficientOfNetworkComplexity = coefficientOfNetworkComplexity;
	}

	@Override
	public void setControlFlowComplexity(int controlFlowComplexity) {
		this.controlFlowComplexity = controlFlowComplexity;
	}

	@Override
	public void setCrossConnectivity(double crossConnectivity) {
		this.crossConnectivity = crossConnectivity;
	}

	@Override
	public void setCycling(double cycling) {
		this.cycling = cycling;
	}

	@Override
	public void setCyclomaticNumber(int cyclomaticNumber) {
		this.cyclomaticNumber = cyclomaticNumber;
	}

	@Override
	public void setDensity(double density) {
		this.density = density;
	}

	@Override
	public void setDensityRelatedToNumberOfGateways(double density) {
		this.densityRelatedToNumberOfGateways = density;
	}

	@Override
	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}

	@Override
	public void setMaxConnectorDegree(int maxConnectorDegree) {
		this.maxConnectorDegree = maxConnectorDegree;
	}

	/**
	 * @param modelPath the modelPath to set
	 */
	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	@Override
	public void setNumberOfActivities(int numberOfActivities) {
		this.numberOfActivities = numberOfActivities;
	}

	@Override
	public void setNumberOfAndJoins(int numberOfAndJoins) {
		this.numberOfAndJoins = numberOfAndJoins;
	}

	@Override
	public void setNumberOfAndSplits(int numberOfAndSplits) {
		this.numberOfAndSplits = numberOfAndSplits;
	}

	@Override
	public void setNumberOfDataNodes(int numberOfDataNodes) {
		this.numberOfDataNodes = numberOfDataNodes;
	}

	@Override
	public void setNumberOfEdges(int numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}

	@Override
	public void setNumberOfEndEvents(int numberOfEndEvents) {
		this.numberOfEndEvents = numberOfEndEvents;
	}

	@Override
	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	@Override
	public void setNumberOfGateways(int numberOfGateways) {
		this.numberOfGateways = numberOfGateways;
	}

	@Override
	public void setNumberOfInternalEvents(int numberOfInternalEvents) {
		this.numberOfInternalEvents = numberOfInternalEvents;
	}

	@Override
	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	@Override
	public void setNumberOfOrJoins(int numberOfOrJoins) {
		this.numberOfOrJoins = numberOfOrJoins;
	}
	
	@Override
	public void setNumberOfOrSplits(int numberOfOrSplits) {
		this.numberOfOrSplits = numberOfOrSplits;
	}
	
	@Override
	public void setNumberOfRoles(int numberOfRoles) {
		this.numberOfRoles = numberOfRoles;
	}
	
	@Override
	public void setNumberOfStartEvents(int numberOfStartEvents) {
		this.numberOfStartEvents = numberOfStartEvents;
	}
	
	@Override
	public void setNumberOfXorJoins(int numberOfXorJoins) {
		this.numberOfXorJoins = numberOfXorJoins;
	}
	
	@Override
	public void setNumberOfXorSplits(int numberOfXorSplits) {
		this.numberOfXorSplits = numberOfXorSplits;
	}
	
	@Override
	public void setSeparability(double separability) {
		this.separability = separability;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString() + "\n");
		builder.append("UnitDataProcessMetrics [numberOfStartEvents=");
		builder.append(this.numberOfStartEvents);
		builder.append(", numberOfInternalEvents=");
		builder.append(this.numberOfInternalEvents);
		builder.append(", numberOfEndEvents=");
		builder.append(this.numberOfEndEvents);
		builder.append(", numberOfEvents=");
		builder.append(this.numberOfEvents);
		builder.append(", numberOfActivities=");
		builder.append(this.numberOfActivities);
		builder.append(", numberOfAndSplits=");
		builder.append(this.numberOfAndSplits);
		builder.append(", numberOfAndJoins=");
		builder.append(this.numberOfAndJoins);
		builder.append(", numberOfXorSplits=");
		builder.append(this.numberOfXorSplits);
		builder.append(", numberOfXorJoins=");
		builder.append(this.numberOfXorJoins);
		builder.append(", numberOfOrSplits=");
		builder.append(this.numberOfOrSplits);
		builder.append(", numberOfOrJoins=");
		builder.append(this.numberOfOrJoins);
		builder.append(", numberOfGateways=");
		builder.append(this.numberOfGateways);
		builder.append(", numberOfNodes=");
		builder.append(this.numberOfNodes);
		builder.append(", numberOfEdges=");
		builder.append(this.numberOfEdges);
		builder.append(", numberOfDataNodes=");
		builder.append(this.numberOfDataNodes);
		builder.append(", numberOfRoles=");
		builder.append(this.numberOfRoles);
		builder.append(", diameter=");
		builder.append(this.diameter);
		builder.append(", density_1=");
		builder.append(this.density);
		builder.append(", density_2=");
		builder.append(this.densityRelatedToNumberOfGateways);
		builder.append(", coefficientOfConnectivity=");
		builder.append(this.coefficientOfConnectivity);
		builder.append(", coefficientOfNetworkComplexity=");
		builder.append(this.coefficientOfNetworkComplexity);
		builder.append(", cyclomaticNumber=");
		builder.append(this.cyclomaticNumber);
		builder.append(", averageConnectorDegree=");
		builder.append(this.averageConnectorDegree);
		builder.append(", maximumConnectorDegree=");
		builder.append(this.maxConnectorDegree);
		builder.append(", separability=");
		builder.append(this.separability);
		builder.append(", depth=");
		builder.append(this.depth);
		builder.append(", cycling=");
		builder.append(this.cycling);
		builder.append(", controlFlowComplexity=");
		builder.append(this.controlFlowComplexity);
		builder.append(", crossConnectivity=");
		builder.append(this.crossConnectivity);
		builder.append("]\n");
		return builder.toString();
	}
	
	@Override
	public String toCsv(String delimiter) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.modelPath + delimiter);
		builder.append(this.getDbId() + delimiter);
		builder.append(this.numberOfStartEvents + delimiter);
		builder.append(this.numberOfInternalEvents + delimiter);
		builder.append(this.numberOfEndEvents + delimiter);
		builder.append(this.numberOfEvents + delimiter);
		builder.append(this.numberOfActivities + delimiter);
		builder.append(this.numberOfAndSplits + delimiter);
		builder.append(this.numberOfAndJoins + delimiter);
		builder.append(this.numberOfXorSplits + delimiter);
		builder.append(this.numberOfXorJoins + delimiter);
		builder.append(this.numberOfOrSplits + delimiter);
		builder.append(this.numberOfOrJoins + delimiter);
		builder.append(this.numberOfGateways + delimiter);
		builder.append(this.numberOfNodes + delimiter);
		builder.append(this.numberOfEdges + delimiter);
		builder.append(this.numberOfDataNodes + delimiter);
		builder.append(this.numberOfRoles + delimiter);
		builder.append(this.diameter + delimiter);
		builder.append(new String(this.density + delimiter).replace(".", ","));
		builder.append(new String(this.densityRelatedToNumberOfGateways + delimiter).replace(".", ","));
		builder.append(new String(this.coefficientOfConnectivity + delimiter).replace(".", ","));
		builder.append(new String(this.coefficientOfNetworkComplexity + delimiter).replace(".", ","));
		builder.append(new String(this.cyclomaticNumber + delimiter).replace(".", ","));
		builder.append(new String(this.averageConnectorDegree + delimiter).replace(".", ","));
		builder.append(new String(this.maxConnectorDegree + delimiter).replace(".", ","));
		builder.append(new String(this.separability + delimiter).replace(".", ","));
		builder.append(new String(this.depth + delimiter).replace(".", ","));
		builder.append(new String(this.cycling + delimiter).replace(".", ","));
		builder.append(new String(this.controlFlowComplexity + delimiter).replace(".", ","));
		builder.append(new String(this.crossConnectivity + "").replace(".", ","));
		builder.append("\n");
		return builder.toString();
		
	}
}
