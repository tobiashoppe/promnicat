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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jbpt.pm.ProcessModel;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.util.modelBuilder.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ProcessMetricConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ProcessModelMetricsCalculatorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataProcessMetrics;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataProcessMetrics;

/**
 * Test class for {@link ProcessModelMetricsCalculatorUnit}.
 * 
 * @author Tobias Hoppe
 *
 */
public class ProcessModelMetricsCalculatorUnitTest {

	private static ProcessModelMetricsCalculatorUnit unit = new ProcessModelMetricsCalculatorUnit();
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("ProcessModelMetricsCalculatorUnit"));
	}
	
	@Test
	public void testGetInputType(){
		assertEquals(ProcessModel.class, unit.getInputType());
	}
	
	@Test
	public void testGetOutputType(){
		assertEquals(ProcessModel.class, unit.getOutputType());
	}
	
	@Test
	public void testExecute(){
		IUnitData<Object> unitData = new UnitDataProcessMetrics<Object>(TestModelBuilder.getConnectedProcessModel());
		try {
			IUnitDataProcessMetrics<Object> result = (IUnitDataProcessMetrics<Object>) unit.execute(unitData);
			assertSame(unitData, result);
			//TODO uncomment already implemented metrics and set expected value of them
			assertEquals(3, result.getAverageConnectorDegree(), 0.0000001);
			assertEquals(19.0/17.0, result.getCoefficientOfConnectivity(), 0.0000001);
			assertEquals(361.0/17.0, result.getCoefficientOfNetworkComplexity(), 0.0000001);
			assertEquals(7, result.getControlFlowComplexity());
//			assertEquals(0, result.getCrossConnectivity(), 0.0000001);
			assertEquals(0.0, result.getCycling(), 0.0000001);
			assertEquals(5, result.getCyclomaticNumber());
			assertEquals(19.0 / 272.0, result.getDensity(), 0.0000001);
			assertEquals(1.0 / 8.0, result.getDensityRelatedToNumberOfGateways(), 0.0000001);
			assertEquals(3, result.getDepth());
			assertEquals(10, result.getDiameter(), 0.0000001);
			assertEquals(3, result.getMaxConnectorDegree(), 0.0000001);
			assertEquals(5, result.getNumberOfActivities());
			assertEquals(1, result.getNumberOfAndJoins());
			assertEquals(2, result.getNumberOfAndSplits());
//			assertEquals(0, result.getNumberOfDataNodes());
			assertEquals(19, result.getNumberOfEdges());
			assertEquals(2, result.getNumberOfEndEvents());
			assertEquals(5, result.getNumberOfEvents());
			assertEquals(7, result.getNumberOfGateways());
			assertEquals(2, result.getNumberOfInternalEvents());
			assertEquals(17, result.getNumberOfNodes());
			assertEquals(0, result.getNumberOfOrJoins());
			assertEquals(1, result.getNumberOfOrSplits());
//			assertEquals(0, result.getNumberOfRoles());
			assertEquals(1, result.getNumberOfStartEvents());
			assertEquals(1, result.getNumberOfXorJoins());
			assertEquals(1, result.getNumberOfXorSplits());
			assertEquals(4.0/14.0, result.getSeparability(), 0.0000001);
		} catch (IllegalTypeException e) {
			fail("got unexpected error with message: " + e.getMessage() + "\n" + e.getStackTrace().toString());
		}
	}
	
	@Test
	public void testOneMetricCalculation() {
		IUnitData<Object> unitData = new UnitDataProcessMetrics<Object>(TestModelBuilder.getConnectedProcessModel());
		try{
			ProcessModelMetricsCalculatorUnit metricsUnit = new ProcessModelMetricsCalculatorUnit(ProcessMetricConstants.METRICS.NUM_NODES, false);
			IUnitDataProcessMetrics<Object> result = (IUnitDataProcessMetrics<Object>) metricsUnit.execute(unitData);
			assertEquals(17, result.getNumberOfNodes());
			//should not be set, that is why zero is the default value
			assertEquals(0, result.getNumberOfEdges());			
		} catch (IllegalTypeException e) {
			fail("got unexpected error with message: " + e.getMessage() + "\n" + e.getStackTrace().toString());
		}
	}
}
