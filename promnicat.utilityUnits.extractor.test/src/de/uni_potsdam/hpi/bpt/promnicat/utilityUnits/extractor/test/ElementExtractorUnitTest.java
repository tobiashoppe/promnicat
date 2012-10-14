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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.jbpt.pm.Activity;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.epc.Document;
import org.jbpt.pm.epc.Epc;
import org.jbpt.pm.epc.Function;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ElementExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitData;

/**
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class ElementExtractorUnitTest {
	private ElementExtractorUnit unit;
	private static Epc process;
	@BeforeClass
	public static void setUp(){
		//create process model
		ElementExtractorUnitTest.process = new Epc();
		
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
		unit = new ElementExtractorUnit(FlowNode.class);
		assertTrue(unit.getName().equals("ElementExtractorUnit"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExecute(){
		IUnitData<Object> input;
		IUnitData<Object> result;
		try{
			unit = new ElementExtractorUnit(FlowNode.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Collection<IUnitData<Object>>)result.getValue()).size() == 5);
			
			unit = new ElementExtractorUnit(Activity.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Collection<IUnitData<Object>>)result.getValue()).size() == 2);
			
			unit = new ElementExtractorUnit(Event.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Collection<IUnitData<Object>>)result.getValue()).size() == 3);
			
			unit = new ElementExtractorUnit(NonFlowNode.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Collection<IUnitData<Object>>)result.getValue()).size() == 1);
			
			unit = new ElementExtractorUnit(DataNode.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Collection<IUnitData<Object>>)result.getValue()).size() == 1);
			
			unit = new ElementExtractorUnit(Document.class);
			input = new UnitData<Object>(process);
			result = unit.execute(input);
			
			assertTrue(((Collection<IUnitData<Object>>)result.getValue()).size() == 1);
			
		} catch (Exception e){
			fail(e.getMessage());
		}
	}
	
}
