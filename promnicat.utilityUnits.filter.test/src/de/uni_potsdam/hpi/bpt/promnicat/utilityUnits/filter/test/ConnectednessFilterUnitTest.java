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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jbpt.pm.Event;
import org.jbpt.pm.epc.Document;
import org.jbpt.pm.epc.Epc;
import org.jbpt.pm.epc.Function;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.ConnectednessFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitData;

/**
 * Test class for ConnectednessFilterUnit.
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class ConnectednessFilterUnitTest {
	
	private ConnectednessFilterUnit unit = new ConnectednessFilterUnit();;
	private static Epc process;
	
	@BeforeClass
	public static void setUp(){
		//create process model
		ConnectednessFilterUnitTest.process = new Epc();
		
		//create functions and events
		Function act1 = new Function();
		Function act2 = new Function();
		Event evt1 = new Event();
		Event evt2 = new Event();
		Event evt3 = new Event();
		process.addFlowNode(act1);
		process.addFlowNode(act2);
		process.addFlowNode(evt1);
		process.addFlowNode(evt2);
		process.addFlowNode(evt3);
		process.addControlFlow(evt1, act1);
		process.addControlFlow(act1, evt2);
		process.addControlFlow(evt2, act2);
		process.addControlFlow(act2, evt3);
		
		//create data object
		Document doc = new Document();
		doc.addReadingFlowNode(act1);
		process.addNonFlowNode(doc);
		
	}
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("ConnectednessFilterUnit"));
	}
	
	@Test
	public void testExecute(){
		IUnitData<Object> input;
		IUnitData<Object> result;
		try{
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(result.getValue() == process);
			
			process.addFlowNode(new Function());
		
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			assertNull(result.getValue());
			
		} catch (Exception e){
			fail("Unexpexted error: " + e.getMessage());
		}
	}
	
}
