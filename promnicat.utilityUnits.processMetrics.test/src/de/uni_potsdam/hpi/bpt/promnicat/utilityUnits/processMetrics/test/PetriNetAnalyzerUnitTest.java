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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jbpt.petri.PetriNet;
import org.jbpt.pm.epc.Epc;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.util.modelBuilder.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.PetriNetAnalyzerUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelConverter.ModelToPetriNetConverter;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataClassification;

/**
 * Test class for {@link PetriNetAnalyzerUnit}.
 * 
 * @author Tobias Hoppe
 */
public class PetriNetAnalyzerUnitTest {

	private static final PetriNetAnalyzerUnit unit = new PetriNetAnalyzerUnit();
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("PetriNetAnalyzerUnit"));
	}
	
	@Test
	public void testGetInputType(){
		assertEquals(PetriNet.class, unit.getInputType());
	}
	
	@Test
	public void testGetOutputType(){
		assertEquals(PetriNet.class, unit.getOutputType());
	}
	
	@Test
	public void testExecute(){
		IUnitDataClassification<Object> unitData = new UnitDataClassification<Object>();
		PetriNet petriNet = null;
		try {
			petriNet = new ModelToPetriNetConverter().convertToPetriNet(TestModelBuilder.getSequence(5, Epc.class));
			unitData.setValue(petriNet);
			unit.execute(unitData);
		} catch (Exception e) {
			fail("Model to PetriNet convertion failed with: " + e.getMessage());
		}
		assertTrue(unitData.getSoundnessResults().isClassicalSound());
		assertTrue(unitData.getSoundnessResults().isWeakSound());
		assertTrue(unitData.getSoundnessResults().isRelaxedSound());
		assertTrue(unitData.getSoundnessResults().getDeadTransitions().isEmpty());
		assertTrue(unitData.getSoundnessResults().getUncoveredTransitions().isEmpty());
		assertTrue(unitData.getSoundnessResults().getUnboundedPlaces().isEmpty());
		assertTrue(unitData.getSoundnessResults().hasQuasiLiveness());
		assertTrue(unitData.getSoundnessResults().hasLiveness());
		assertTrue(unitData.getSoundnessResults().hasTransitioncover());
		assertTrue(unitData.getSoundnessResults().isBounded());
		assertFalse(unitData.isCyclic());
		assertTrue(unitData.isFreeChoice());
		assertTrue(unitData.isExtendedFreeChoice());
		assertTrue(unitData.isSNet());
		assertTrue(unitData.isTnet());
		assertTrue(unitData.isWorkflowNet());
	}
}
