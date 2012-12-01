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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.util.modelBuilder.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.BpmnConformanceLevelChecker;

/**
 * Test class for {@link BpmnConformanceLevelChecker}
 * 
 * @author Tobias Hoppe
 *
 */
public class BpmnConformanceLevelCheckerTest {
	
	@Test
	public void testDescriptiveModelConformance() {
		BpmnConformanceLevelChecker modelChecker = new BpmnConformanceLevelChecker(TestModelBuilder.createDescriptiveConformBpmnModel());
		assertTrue(modelChecker.isDescriptiveModelingConform());
		assertFalse(modelChecker.isAnalyticModelingConform());
		assertFalse(modelChecker.isCommonExecutableModelingConform());
	}

	@Test
	public void testAnalyticModelConformance() {
		BpmnConformanceLevelChecker modelChecker = new BpmnConformanceLevelChecker(TestModelBuilder.getConnectedBpmnModel());
		assertFalse(modelChecker.isDescriptiveModelingConform());
		assertTrue(modelChecker.isAnalyticModelingConform());
		assertFalse(modelChecker.isCommonExecutableModelingConform());
	}
	
	@Test
	public void testOnlyExecutableModelConformance() {
		BpmnConformanceLevelChecker modelChecker = new BpmnConformanceLevelChecker(TestModelBuilder.createOnlyExecutableConformBpmnModel());
		assertFalse(modelChecker.isDescriptiveModelingConform());
		assertFalse(modelChecker.isAnalyticModelingConform());
		assertTrue(modelChecker.isCommonExecutableModelingConform());
	}

}
