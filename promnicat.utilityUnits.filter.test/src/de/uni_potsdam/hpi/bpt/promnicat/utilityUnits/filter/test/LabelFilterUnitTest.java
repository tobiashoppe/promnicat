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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.LabelFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitData;

/**
 * Test class for {@link LabelFilterUnit}.
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class LabelFilterUnitTest {

	private IUnitData<Object> input;
	
	@Before
	public void setUp(){
		//build unit data input
		//two functions and two events
		Map<String, Collection<String>> inputValue = new HashMap<String, Collection<String>>();
		Collection<String> functionLabels = new ArrayList<String>();
		functionLabels.add("customer reads script");
		functionLabels.add("script is filled out by customer");
		inputValue.put("Function", functionLabels);
		Collection<String> eventLabels = new ArrayList<String>();
		eventLabels.add("data document received");
		eventLabels.add("document correctly filled");
		inputValue.put("Event", eventLabels);
		
		this.input = new UnitData<Object>(inputValue);
	}
	
	@Test
	public void testGetName(){
		assertTrue(new LabelFilterUnit("").getName().equals("LabelFilterUnit"));
	}
	
	@Test
	public void testLabelsFoundByName(){
		try{
			IUnitData<Object> result = new LabelFilterUnit("customer").execute(input);
			assertTrue(result.getValue() instanceof Map<?,?>);
			Map<?,?> entries = (Map<?,?>)result.getValue();
			assertTrue(entries.size() == 1);
			Object values  = entries.get("Function");
			assertNotNull(values);
			assertTrue(values instanceof Collection<?>);
			assertTrue(((Collection<?>) values).size() == 2);
			for (Object label : (Collection<?>)values){
				assertTrue(label instanceof String);
				assertTrue(((String) label).contains("customer"));
			}
		} catch (Exception e){
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void testNoLabelFoundByName() {
		try{
			IUnitData<Object> result = new LabelFilterUnit("xyz").execute(input);
			assertNull(result.getValue());
		} catch (Exception e){
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void testLabelsFoundByPattern(){
		try{
			IUnitData<Object> result = new LabelFilterUnit(Pattern.compile(".*(document).*")).execute(input);
			assertTrue(result.getValue() instanceof Map<?,?>);
			Map<?,?> entries = (Map<?,?>)result.getValue();
			assertTrue(entries.size() == 1);
			Object values  = entries.get("Event");
			assertNotNull(values);
			assertTrue(values instanceof Collection<?>);
			assertTrue(((Collection<?>) values).size() == 2);
			for (Object label : (Collection<?>)values){
				assertTrue(label instanceof String);
				assertTrue(((String) label).contains("document"));
			}
		} catch (Exception e){
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void testNoLabelFoundByPattern() {
		try{
			IUnitData<Object> result = new LabelFilterUnit(Pattern.compile("[0-9]")).execute(input);
			assertNull(result.getValue());
		} catch (Exception e){
			fail("Unexpected error: " + e.getMessage());
		}
	}
}
