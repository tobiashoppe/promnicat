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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.util;

import org.jbpt.pm.Activity;
import org.jbpt.pm.Event;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.ProcessModelMetricsCalculatorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataProcessMetrics;

/**
 * Class containing all necessary constants regarding the configuration of the 
 * {@link ProcessModelMetricsCalculatorUnit}
 * 
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class ProcessMetricConstants {

	public static final String NUM_NODES = "numNodes";
	public static final String NUM_EDGES = "numEdges";
	
	public static final String NUM_ACTIVITIES = "numActivities";
	public static final String NUM_GATEWAYS = "numGateways";
	public static final String NUM_EVENTS = "numEvents";
	public static final String NUM_ROLES = "numRoles";
	
	public static final String NUM_DATA_NODES = "numDataNodes";
	
	public static final String NUM_START_EVENTS = "numStartEvents";
	public static final String NUM_INTERNAL_EVENTS = "numInternEvents";
	public static final String NUM_END_EVENTS = "numEndEvents";
	
	public static final String NUM_AND_JOINS = "numAndJoins";
	public static final String NUM_AND_SPLITS = "numAndSplits";
	public static final String NUM_OR_JOINS = "numOrJoins";
	public static final String NUM_OR_SPLITS = "numOrSplits";
	public static final String NUM_XOR_JOINS = "numXorJoins";
	public static final String NUM_XOR_SPLITS = "numXorSplits";
	
	public static final String AVERAGE_CONNECTOR_DEGREE = "avgConnectorDegree";
	public static final String MAX_CONNECTOR_DEGREE = "maxConnectorDegree";
	
	public static final String COEFFICIENT_CONNECTIVITY = "coeffConnectivity";
	public static final String CROSS_CONNECTIVITY = "crossConnectivity";
	
	public static final String COEFFICIENT_NETWORK_COMPLEXITY = "coeffNetworkCompl";
	public static final String CONTROL_FLOW_COMPLEXITY = "controlFlowCompl";
	
	public static final String CYCLING = "cycling";
	public static final String CYCLOMATIC_NUMBER = "cycloNum";
	
	public static final String SEPARABILITY = "separability";
	public static final String DENSITY = "density";
	public static final String DEPTH = "depth";
	public static final String DIAMETER = "diameter";
	
	
	/**
	 * An enumeration of all available process metrics
	 */
	public enum METRICS{
		NUM_NODES(ProcessMetricConstants.NUM_NODES){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfNodes();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfNodes(new ProcessMetricsCalculator().getNumberOfNodes((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_EDGES(ProcessMetricConstants.NUM_EDGES){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfEdges();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfEdges(new ProcessMetricsCalculator().getNumberOfEdges((ProcessModel) data.getValue(), includeSubProcesses));				
			}
		},
		NUM_ACTIVITIES(ProcessMetricConstants.NUM_ACTIVITIES){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfActivities();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfActivities(new ProcessMetricsCalculator().getNumberOfElementsFromClass((ProcessModel) data.getValue(), Activity.class, includeSubProcesses));
			}
		},
		NUM_AND_SPLITS(ProcessMetricConstants.NUM_AND_SPLITS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfAndSplits();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfAndSplits(new ProcessMetricsCalculator().getNumberOfAndSplits((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_AND_JOINS(ProcessMetricConstants.NUM_AND_JOINS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfAndJoins();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfAndJoins(new ProcessMetricsCalculator().getNumberOfAndJoins((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_OR_JOINS(ProcessMetricConstants.NUM_OR_JOINS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfOrJoins();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfOrJoins(new ProcessMetricsCalculator().getNumberOfOrJoins((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_OR_SPLITS(ProcessMetricConstants.NUM_OR_SPLITS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfOrSplits();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfOrSplits(new ProcessMetricsCalculator().getNumberOfOrSplits((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_XOR_SPLITS(ProcessMetricConstants.NUM_XOR_SPLITS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfXorSplits();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfXorSplits(new ProcessMetricsCalculator().getNumberOfXorSplits((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_XOR_JOINS(ProcessMetricConstants.NUM_XOR_JOINS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfXorJoins();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfXorJoins(new ProcessMetricsCalculator().getNumberOfXorJoins((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_DATA_NODES(ProcessMetricConstants.NUM_DATA_NODES){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfDataNodes();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfDataNodes(new ProcessMetricsCalculator().getNumberOfDataNodes((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_EVENTS(ProcessMetricConstants.NUM_EVENTS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfEvents();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfEvents(new ProcessMetricsCalculator().getNumberOfElementsFromClass((ProcessModel) data.getValue(), Event.class, includeSubProcesses));
			}
		},
		NUM_START_EVENTS(ProcessMetricConstants.NUM_START_EVENTS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfStartEvents();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfStartEvents(new ProcessMetricsCalculator().getNumberOfStartEvents((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_END_EVENTS(ProcessMetricConstants.NUM_END_EVENTS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfEndEvents();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfEndEvents(new ProcessMetricsCalculator().getNumberOfEndEvents((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_INTERNAL_EVENTS(ProcessMetricConstants.NUM_INTERNAL_EVENTS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfInternalEvents();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfInternalEvents(new ProcessMetricsCalculator().getNumberOfInternalEvents((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_GATEWAYS(ProcessMetricConstants.NUM_GATEWAYS){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfGateways();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfGateways(new ProcessMetricsCalculator().getNumberOfGateways((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		NUM_ROLES(ProcessMetricConstants.NUM_ROLES){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getNumberOfRoles();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setNumberOfRoles(new ProcessMetricsCalculator().getNumberOfRoles((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		AVERAGE_CONNECTOR_DEGREE(ProcessMetricConstants.AVERAGE_CONNECTOR_DEGREE){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getAverageConnectorDegree();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setAverageConnectorDegree(new ProcessMetricsCalculator().getAverageConnectorDegree((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		COEFFICIENT_CONNECTIVITY(ProcessMetricConstants.COEFFICIENT_CONNECTIVITY){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getCoefficientOfConnectivity();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setCoefficientOfConnectivity(new ProcessMetricsCalculator().getCoefficientOfConnectivity((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		COEFFICIENT_NETWORK_COMPLEXITY(ProcessMetricConstants.COEFFICIENT_NETWORK_COMPLEXITY){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getCoefficientOfNetworkComplexity();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setCoefficientOfNetworkComplexity(new ProcessMetricsCalculator().getCoefficientOfNetworkComplexity((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		CONTROL_FLOW_COMPLEXITY(ProcessMetricConstants.CONTROL_FLOW_COMPLEXITY){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getControlFlowComplexity();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setControlFlowComplexity(new ProcessMetricsCalculator().getControlFlowComplexity((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		CROSS_CONNECTIVITY(ProcessMetricConstants.CROSS_CONNECTIVITY){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getCrossConnectivity();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setCrossConnectivity(new ProcessMetricsCalculator().getCrossConnectivity((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		CYCLING(ProcessMetricConstants.CYCLING){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getCycling();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setCycling(new ProcessMetricsCalculator().getCycling((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		CYCLOMATIC_NUMBER(ProcessMetricConstants.CYCLOMATIC_NUMBER){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getCyclomaticNumber();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setCyclomaticNumber(new ProcessMetricsCalculator().getCyclomaticNumber((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		SEPARABILITY(ProcessMetricConstants.SEPARABILITY){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getSeparability();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setSeparability(new ProcessMetricsCalculator().getSeparability((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		DENSITY(ProcessMetricConstants.DENSITY){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getDensity();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setDensity(new ProcessMetricsCalculator().getDensity((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		DEPTH(ProcessMetricConstants.DEPTH){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getDepth();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setDepth(new ProcessMetricsCalculator().getDepth((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		DIAMETER(ProcessMetricConstants.DIAMETER){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getDiameter();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setDiameter(new ProcessMetricsCalculator().getDiameter((ProcessModel) data.getValue(), includeSubProcesses));
			}
		},
		MAX_CONNECTOR_DEGREE(ProcessMetricConstants.MAX_CONNECTOR_DEGREE){
			public double getAttribute(IUnitDataProcessMetrics<?> data){
				return data.getMaxConnectorDegree();
			}

			@Override
			public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
				super.calculateAttribute(data, includeSubProcesses);
				data.setMaxConnectorDegree(new ProcessMetricsCalculator().getMaxConnectorDegree((ProcessModel) data.getValue(), includeSubProcesses));
			}
		};
		
		private String description;
	     
		private METRICS(String description) {
			this.description = description;
	    }

		public String toString() {
			return description;
		}
		
		public abstract double getAttribute(IUnitDataProcessMetrics<?> data);
		
		/**
		 * Calculates the metric value and stores the result in the given {@link IUnitDataProcessMetrics}.
		 * @param data {@link IUnitDataProcessMetrics} to use for metric value saving
		 * @param includeSubProcesses flag whether to include all available sub process in metric calculation
		 */
		public void calculateAttribute(IUnitDataProcessMetrics<Object> data, boolean includeSubProcesses) {
			if (!(data.getValue() instanceof ProcessModel)) {
				throw new IllegalArgumentException("The given UnitData value must be an instance of a jBPT ProcessModel!");
			}
		}
	}
}