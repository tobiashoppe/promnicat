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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jbpt.pm.ProcessModel;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.util.modelBuilder.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.BpmnConformanceLevelCheckerUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataClassification;

/**
 * Test class for {@link BpmnConformanceLevelCheckerUnit}
 * 
 * @author Tobias Hoppe
 */
public class BpmnConformanceLevelCheckerUnitTest {

	private static BpmnConformanceLevelCheckerUnit unit = new BpmnConformanceLevelCheckerUnit();
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("BpmnConformanceLevelCheckerUnit"));
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
	public void testConformanceLevelCalculation() {
		IUnitData<Object> unitData = new UnitDataClassification<Object>(TestModelBuilder.getConnectedBpmnModel());
		try{
			IUnitDataClassification<Object> result = (IUnitDataClassification<Object>) unit.execute(unitData);
			assertSame(unitData, result);
			assertFalse(result.getDescriptiveModelingConformance());
			assertTrue(result.getAnalyticModelingConformance());
			assertFalse(result.getCommonExecutableModelingConformance());
		} catch (Exception e) {
			fail("got unexpected error with message: " + e.getMessage() + "\n" + e.getStackTrace().toString());
		}
	}
}
