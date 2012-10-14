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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.BpmaiJsonToDiagramUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataJbpt;

/**
 * Test class for {@link BpmaiJsonToDiagramUnit}
 * @author Cindy Fähnrich
 *
 */
public class BpmaiJsonToDiagramUnitTest {

	private BpmaiJsonToDiagramUnit unit = new BpmaiJsonToDiagramUnit();
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("BpmaiJsonToDiagramUnit"));
	}

	@Test
	public void testExecute() {
		Representation representation = new Representation();
		try{
			File file = new File("resources/BPMAI/model_epc1/model_2_.json");
			representation.importFile(file);

			IUnitData<Object> input = new UnitDataJbpt<Object>(representation);
			IUnitData<Object> result = null;
			
			result = unit.execute(input);
			assertTrue(result.getValue() instanceof Diagram);
		}catch (Exception e){
			fail("Unexpected exception occurred: " + e.getMessage());
		}	
	}

}
