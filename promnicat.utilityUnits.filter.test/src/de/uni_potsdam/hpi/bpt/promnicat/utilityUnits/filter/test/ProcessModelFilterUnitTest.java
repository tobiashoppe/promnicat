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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.jbpt.pm.Activity;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.Resource;
import org.jbpt.pm.epc.AndConnector;
import org.jbpt.pm.epc.Document;
import org.jbpt.pm.epc.Epc;
import org.jbpt.pm.epc.Function;
import org.jbpt.pm.epc.OrConnector;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.ProcessModelFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitData;

/**
 * Test class for {@link ProcessModelFilterUnit}.
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class ProcessModelFilterUnitTest {

	private ProcessModelFilterUnit unit;
	private static Epc process;
	private static Function act1;
	private static Function act2;
	private static Event evt1;
	private static Event evt2;
	private static Event evt3;
	
	@BeforeClass
	public static void setUp(){
		//create process model
		ProcessModelFilterUnitTest.process = new Epc();
		
		//create functions and events
		act1 = new Function();
		act2 = new Function();
		evt1 = new Event();
		evt2 = new Event();
		evt3 = new Event();
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
		unit = new ProcessModelFilterUnit(FlowNode.class);
		assertTrue(unit.getName().equals("ProcessModelFilterUnit"));
	}
	
	@Test
	public void testExecute(){
		IUnitData<Object> input;
		IUnitData<Object> result;
		try{
			//check for class types contained in the process model
			unit = new ProcessModelFilterUnit(FlowNode.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(Activity.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(Event.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(NonFlowNode.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(DataNode.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(Document.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(Resource.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == null);
			
			//check for concrete elements in the process model
			unit = new ProcessModelFilterUnit(act1);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(new Event());
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == null);
			
			Collection<Class<?>> includedTypes = new HashSet<Class<?>>();
			includedTypes.add(Document.class);
			unit = new ProcessModelFilterUnit(includedTypes, new HashSet<Class<?>>());
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			includedTypes = new HashSet<Class<?>>();
			includedTypes.add(Document.class);
			includedTypes.add(Activity.class);
			includedTypes.add(Event.class);
			unit = new ProcessModelFilterUnit(includedTypes, new HashSet<Class<?>>());
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			includedTypes = new HashSet<Class<?>>();
			includedTypes.add(Document.class);
			includedTypes.add(Activity.class);
			includedTypes.add(Event.class);
			Collection<Class<?>> excludedTypes = new HashSet<Class<?>>();
			excludedTypes.add(OrConnector.class);
			excludedTypes.add(AndConnector.class);
			unit = new ProcessModelFilterUnit(includedTypes, excludedTypes);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			includedTypes = new HashSet<Class<?>>();
			includedTypes.add(Document.class);
			includedTypes.add(Activity.class);
			includedTypes.add(Event.class);
			excludedTypes = new HashSet<Class<?>>();
			excludedTypes.add(Document.class);
			unit = new ProcessModelFilterUnit(includedTypes, excludedTypes);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == null);
			
			includedTypes = new HashSet<Class<?>>();
			includedTypes.add(Resource.class);
			unit = new ProcessModelFilterUnit(includedTypes, new HashSet<Class<?>>());
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == null);
			
			includedTypes = new HashSet<Class<?>>();
			excludedTypes = new HashSet<Class<?>>();
			excludedTypes.add(Document.class);
			unit = new ProcessModelFilterUnit(includedTypes, excludedTypes);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == null);
			
			unit = new ProcessModelFilterUnit(new HashSet<Class<?>>(), null);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(null, new HashSet<Class<?>>());
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			unit = new ProcessModelFilterUnit(null, null);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == process);
			
			includedTypes = new HashSet<Class<?>>();
			excludedTypes = new HashSet<Class<?>>();
			includedTypes.add(null);
			unit = new ProcessModelFilterUnit(includedTypes, excludedTypes);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Epc)result.getValue()) == null);
			
		} catch (Exception e){
			fail("An unexpexcted error occurred:\n" + e.getMessage());
		}
	}
	
}
