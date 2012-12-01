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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.SimpleCollectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitData;

/**
 * Test class for {@link SimpleCollectorUnit}.
 * @author Cindy Fähnrich
 *
 */
public class SimpleCollectorUnitTest {

	private SimpleCollectorUnit unit = new SimpleCollectorUnit();
	private static IUnitData<Object> input1 = new UnitData<Object>("test1");
	private static IUnitData<Object> input2 = new UnitData<Object>("test2");
	private static IUnitData<Object> input3 = new UnitData<Object>("test3");
	private static IUnitData<Object> input4 = new UnitData<Object>("test4");
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("SimpleCollectorUnit"));
	}
	
	@Test
	public void testExecute(){
		IUnitData<Object> result;
		Collection<IUnitData<Object>> values;
		
		result = unit.execute(input1);
		assertEquals(input1, result);
		values = unit.getResult();
		assertTrue(values.size() == 1);		
		assertEquals("test1", values.iterator().next().getValue());

		result = unit.execute(input3);
		assertEquals(input3, result);
		values = unit.getResult();
		assertTrue(values.size() == 2);
		Iterator<IUnitData<Object>> i = values.iterator();
		i.next();
		assertEquals("test3", i.next().getValue());
		
		unit.reset();
		result = unit.execute(input4);
		assertEquals(input4, result);
		values = unit.getResult();
		assertTrue(values.size() == 1);		
		assertEquals("test4", values.iterator().next().getValue());
	}
	
	@Test
	public void testReset(){
		unit.execute(input1);
		unit.execute(input2);
		assertTrue(unit.getResult().size() == 2);
		unit.reset();
		assertTrue(unit.getResult().size() == 0);
	}
	
	@Test
	public void testGetResult(){
		unit.execute(input3);
		unit.execute(input4);
		unit.execute(input1);
		assertTrue(unit.getResult().size() == 3);
		unit.execute(input2);
		assertTrue(unit.getResult().size() == 4);	
	}
}
