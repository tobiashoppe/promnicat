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

import java.io.File;

import org.jbpt.pm.epc.Epc;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.BpmaiJsonToDiagramUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.DiagramToJbptUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataJbpt;

/**
 * Test class for {@DiagramToJbptUnit}
 * @author Cindy Fähnrich
 *
 */
public class DiagramToJbptUnitTest {

	private DiagramToJbptUnit unit = new DiagramToJbptUnit(false);
	private static UnitDataJbpt<Object> diagram;

	@BeforeClass
	public static void setUp() throws IllegalTypeException{
		BpmaiJsonToDiagramUnit parserUnit = new BpmaiJsonToDiagramUnit();
		Representation representation = new Representation();
		try{
			File file = new File("resources/BPMAI/model_epc1/model_2_.json");
			representation.importFile(file);
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}	

		UnitDataJbpt<Object> input = new UnitDataJbpt<Object>(representation);
		DiagramToJbptUnitTest.diagram = (UnitDataJbpt<Object>) parserUnit.execute(input);		
	}

	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("DiagramToJbptUnit"));
	}

	@Test
	public void testExecute() throws IllegalTypeException{
		IUnitData<Object> result = unit.execute(DiagramToJbptUnitTest.diagram);

		assertTrue(result.getValue() instanceof Epc);
	}
}
