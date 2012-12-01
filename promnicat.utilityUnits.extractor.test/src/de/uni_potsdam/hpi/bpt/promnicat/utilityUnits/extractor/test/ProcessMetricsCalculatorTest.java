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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test;

import static org.junit.Assert.assertEquals;

import org.jbpt.pm.Activity;
import org.jbpt.pm.Event;
import org.jbpt.pm.ProcessModel;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.util.modelBuilder.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.util.ProcessMetricsCalculator;


/**
 * Test class for {@link ProcessMetricsCalculator}.
 * @author Tobias Hoppe
 *
 */
public class ProcessMetricsCalculatorTest {
	
	private ProcessMetricsCalculator metricCalculator = new ProcessMetricsCalculator();

	private ProcessModel connectedModel = TestModelBuilder.getConnectedProcessModel();
	private ProcessModel nodesOnlyModel = TestModelBuilder.getNodesOnlyProcessModel();
	private ProcessModel modelWithLoops = TestModelBuilder.getModelWithLoops();
	private ProcessModel disconnectedModel = TestModelBuilder.getDisconnectedModel();
	
	@Test
	public void getAverageConnectorDegreeTest() {
		assertEquals(3, metricCalculator.getAverageConnectorDegree(this.connectedModel, true), 0.0000001);
		assertEquals(0, metricCalculator.getAverageConnectorDegree(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(14.0 / 4.0, metricCalculator.getAverageConnectorDegree(this.modelWithLoops, true), 0.0000001);
		assertEquals(2.5, metricCalculator.getAverageConnectorDegree(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getCoefficientOfConnectivityTest() {
		assertEquals(19.0 / 17.0, metricCalculator.getCoefficientOfConnectivity(this.connectedModel, true), 0.0000001);
		assertEquals(0, metricCalculator.getCoefficientOfConnectivity(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(12.0 / 9.0, metricCalculator.getCoefficientOfConnectivity(this.modelWithLoops, true), 0.0000001);
		assertEquals(6.0 / 9.0, metricCalculator.getCoefficientOfConnectivity(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getCoefficientOfNetworkComplexityTest() {
		assertEquals(361.0 / 17.0, metricCalculator.getCoefficientOfNetworkComplexity(this.connectedModel, true), 0.0000001);
		assertEquals(0, metricCalculator.getCoefficientOfNetworkComplexity(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(144.0 / 9.0, metricCalculator.getCoefficientOfNetworkComplexity(this.modelWithLoops, true), 0.0000001);
		assertEquals(36.0 / 9.0, metricCalculator.getCoefficientOfNetworkComplexity(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getControlFlowComplexityTest() {
		assertEquals(7, metricCalculator.getControlFlowComplexity(this.connectedModel, true));
		assertEquals(0, metricCalculator.getControlFlowComplexity(this.nodesOnlyModel, true));
		assertEquals(3, metricCalculator.getControlFlowComplexity(this.modelWithLoops, true));
		assertEquals(1, metricCalculator.getControlFlowComplexity(this.disconnectedModel, true));
	}

	@Test
	public void getCrossConnectivityTest() {
		//TODO approve results
		assertEquals(0.10237070073683287, metricCalculator.getCrossConnectivity(this.connectedModel, true), 0.0000001);
		assertEquals(0.0, metricCalculator.getCrossConnectivity(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(0.11857638888888888, metricCalculator.getCrossConnectivity(this.modelWithLoops, true), 0.0000001);
		assertEquals(7.5 / 80.0, metricCalculator.getCrossConnectivity(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getCyclingTest() {
		assertEquals(0.0, metricCalculator.getCycling(this.connectedModel, true), 0.0000001);
		assertEquals(0.0, metricCalculator.getCycling(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(1.0, metricCalculator.getCycling(this.modelWithLoops, true), 0.0000001);
		assertEquals(0.0, metricCalculator.getCycling(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getCyclomaticNumberTest() {
		assertEquals(5, metricCalculator.getCyclomaticNumber(this.connectedModel, true));
		assertEquals(12, metricCalculator.getCyclomaticNumber(this.nodesOnlyModel, true));
		assertEquals(2, metricCalculator.getCyclomaticNumber(this.modelWithLoops, true));
		assertEquals(4, metricCalculator.getCyclomaticNumber(this.disconnectedModel, true));
	}

	@Test
	public void getDensityTest() {
		assertEquals(19.0 / 272.0, metricCalculator.getDensity(this.connectedModel, true), 0.0000001);
		assertEquals(0, metricCalculator.getDensity(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(12.0 / 72.0, metricCalculator.getDensity(this.modelWithLoops, true), 0.0000001);
		assertEquals(6.0 / 72.0, metricCalculator.getDensity(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getDensityRelatedToNumberOfGatewaysTest() {
		assertEquals(1.0 / 8.0, metricCalculator.getDensityRelatedToNumberOfGateways(this.connectedModel, true), 0.0000001);
		assertEquals(0, metricCalculator.getDensityRelatedToNumberOfGateways(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(4.0 / 13.0, metricCalculator.getDensityRelatedToNumberOfGateways(this.modelWithLoops, true), 0.0000001);
		assertEquals(1.0 / 17.0, metricCalculator.getDensityRelatedToNumberOfGateways(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getDepthTest() {
		assertEquals(3, this.metricCalculator.getDepth(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getDepth(this.nodesOnlyModel, true));
		assertEquals(0, this.metricCalculator.getDepth(this.modelWithLoops, true));
		assertEquals(1, this.metricCalculator.getDepth(this.disconnectedModel, true));
	}
	
	@Test
	public void getDiameterTest() {
		assertEquals(10, this.metricCalculator.getDiameter(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getDiameter(this.nodesOnlyModel, true));
		assertEquals(8, this.metricCalculator.getDiameter(this.modelWithLoops, true));
		assertEquals(3, this.metricCalculator.getDiameter(this.disconnectedModel, true));
	}

	@Test
	public void getMaxConnectorDegreeTest() {
		assertEquals(3, metricCalculator.getMaxConnectorDegree(this.connectedModel, true), 0.0000001);
		assertEquals(0, metricCalculator.getMaxConnectorDegree(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(4, metricCalculator.getMaxConnectorDegree(this.modelWithLoops, true), 0.0000001);
		assertEquals(3, metricCalculator.getMaxConnectorDegree(this.disconnectedModel, true), 0.0000001);
	}

	@Test
	public void getNumberOfActivitiesTest() {
		assertEquals(5, this.metricCalculator.getNumberOfElementsFromClass(this.connectedModel, Activity.class, true));
		assertEquals(5, this.metricCalculator.getNumberOfElementsFromClass(this.nodesOnlyModel, Activity.class, true));
		assertEquals(4, this.metricCalculator.getNumberOfElementsFromClass(this.modelWithLoops, Activity.class, true));
		assertEquals(8, this.metricCalculator.getNumberOfElementsFromClass(this.disconnectedModel, Activity.class, true));
	}

	@Test
	public void getNumberOfAndJoinsTest() {
		assertEquals(1, this.metricCalculator.getNumberOfAndJoins(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfAndJoins(this.nodesOnlyModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfAndJoins(this.modelWithLoops, true));
		assertEquals(0, this.metricCalculator.getNumberOfAndJoins(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfAndSplitsTest() {
		assertEquals(2, this.metricCalculator.getNumberOfAndSplits(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfAndSplits(this.nodesOnlyModel, true));
		assertEquals(1, this.metricCalculator.getNumberOfAndSplits(this.modelWithLoops, true));
		assertEquals(1, this.metricCalculator.getNumberOfAndSplits(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfDataNodesTest() {
		assertEquals(0, this.metricCalculator.getNumberOfDataNodes(this.connectedModel, true));
		assertEquals(6, this.metricCalculator.getNumberOfDataNodes(this.nodesOnlyModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfDataNodes(this.modelWithLoops, true));
		assertEquals(0, this.metricCalculator.getNumberOfDataNodes(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfEdgesTest() {
		assertEquals(19, this.metricCalculator.getNumberOfEdges(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfEdges(this.nodesOnlyModel, true));
		assertEquals(12, this.metricCalculator.getNumberOfEdges(this.modelWithLoops, true));
		assertEquals(6, this.metricCalculator.getNumberOfEdges(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfEndEventsTest() {
		assertEquals(2, this.metricCalculator.getNumberOfEndEvents(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfEndEvents(this.nodesOnlyModel, true));
		assertEquals(1, this.metricCalculator.getNumberOfEndEvents(this.modelWithLoops, true));
		assertEquals(0, this.metricCalculator.getNumberOfEndEvents(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfEventsTest() {
		assertEquals(5, this.metricCalculator.getNumberOfElementsFromClass(this.connectedModel, Event.class, true));
		assertEquals(4, this.metricCalculator.getNumberOfElementsFromClass(this.nodesOnlyModel, Event.class, true));
		assertEquals(2, this.metricCalculator.getNumberOfElementsFromClass(this.modelWithLoops, Event.class, true));
		assertEquals(1, this.metricCalculator.getNumberOfElementsFromClass(this.disconnectedModel, Event.class, true));
	}

	@Test
	public void getNumberOfGatewaysTest() {
		assertEquals(7, this.metricCalculator.getNumberOfGateways(this.connectedModel, true));
		assertEquals(3, this.metricCalculator.getNumberOfGateways(this.nodesOnlyModel, true));
		assertEquals(5, this.metricCalculator.getNumberOfGateways(this.modelWithLoops, true));
		assertEquals(2, this.metricCalculator.getNumberOfGateways(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfInternalEventsTest() {
		assertEquals(2, this.metricCalculator.getNumberOfInternalEvents(this.connectedModel, true));
		assertEquals(4, this.metricCalculator.getNumberOfInternalEvents(this.nodesOnlyModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfInternalEvents(this.modelWithLoops, true));
		assertEquals(0, this.metricCalculator.getNumberOfInternalEvents(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfNodesTest() {
		assertEquals(17, this.metricCalculator.getNumberOfNodes(this.connectedModel, true));
		assertEquals(12, this.metricCalculator.getNumberOfNodes(this.nodesOnlyModel, true));
		assertEquals(9, this.metricCalculator.getNumberOfNodes(this.modelWithLoops, true));
		assertEquals(9, this.metricCalculator.getNumberOfNodes(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfOrJoinsTest() {
		assertEquals(0, this.metricCalculator.getNumberOfOrJoins(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfOrJoins(this.nodesOnlyModel, true));
		assertEquals(1, this.metricCalculator.getNumberOfOrJoins(this.modelWithLoops, true));
		assertEquals(0, this.metricCalculator.getNumberOfOrJoins(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfOrSplitsTest() {
		assertEquals(1, this.metricCalculator.getNumberOfOrSplits(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfOrSplits(this.nodesOnlyModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfOrSplits(this.modelWithLoops, true));
		assertEquals(0, this.metricCalculator.getNumberOfOrSplits(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfRolesTest() {
		// TODO Auto-generated method stub
	}

	@Test
	public void getNumberOfStartEventsTest() {
		assertEquals(1, this.metricCalculator.getNumberOfStartEvents(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfStartEvents(this.nodesOnlyModel, true));
		assertEquals(1, this.metricCalculator.getNumberOfStartEvents(this.modelWithLoops, true));
		assertEquals(1, this.metricCalculator.getNumberOfStartEvents(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfXorJoinsTest() {
		assertEquals(1, this.metricCalculator.getNumberOfXorJoins(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfXorJoins(this.nodesOnlyModel, true));
		assertEquals(2, this.metricCalculator.getNumberOfXorJoins(this.modelWithLoops, true));
		assertEquals(1, this.metricCalculator.getNumberOfXorJoins(this.disconnectedModel, true));
	}

	@Test
	public void getNumberOfXorSplitsTest() {
		assertEquals(1, this.metricCalculator.getNumberOfXorSplits(this.connectedModel, true));
		assertEquals(0, this.metricCalculator.getNumberOfXorSplits(this.nodesOnlyModel, true));
		assertEquals(1, this.metricCalculator.getNumberOfXorSplits(this.modelWithLoops, true));
		assertEquals(0, this.metricCalculator.getNumberOfXorSplits(this.disconnectedModel, true));
	}

	@Test
	public void getSeparabilityTest() {
		assertEquals(4.0/14.0, metricCalculator.getSeparability(this.connectedModel, true), 0.0000001);
		assertEquals(0.0, metricCalculator.getSeparability(this.nodesOnlyModel, true), 0.0000001);
		assertEquals(0.0, metricCalculator.getSeparability(this.modelWithLoops, true), 0.0000001);
		assertEquals(1.5 / 4.0, metricCalculator.getSeparability(this.disconnectedModel, true), 0.0000001);
	}
}