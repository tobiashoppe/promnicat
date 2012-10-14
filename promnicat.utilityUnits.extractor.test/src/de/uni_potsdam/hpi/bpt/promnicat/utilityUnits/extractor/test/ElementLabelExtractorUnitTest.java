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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.Event;
import org.jbpt.pm.epc.Document;
import org.jbpt.pm.epc.Function;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ElementLabelExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataLabelFilter;

/**
 * Test class for {@link ElementLabelExtractorUnit}
 * 
 * @author Tobias Hoppe
 *
 */
public class ElementLabelExtractorUnitTest {
	
	private ElementLabelExtractorUnit unit = new ElementLabelExtractorUnit();
	private static Collection<IVertex> modelElements;
	
	@BeforeClass
	public static void setUp(){
		ElementLabelExtractorUnitTest.modelElements = new ArrayList<IVertex>();
		//create functions and events
		Function act1 = new Function();
		act1.setName("customer reads document");
		ElementLabelExtractorUnitTest.modelElements.add(act1);
		Function act2 = new Function();
		act2.setName("document is filled out by customer");
		ElementLabelExtractorUnitTest.modelElements.add(act2);
		Event evt1 = new Event();
		evt1.setName("document requested");
		ElementLabelExtractorUnitTest.modelElements.add(evt1);
		Event evt2 = new Event();
		evt2.setName("document received");
		ElementLabelExtractorUnitTest.modelElements.add(evt2);
		Event evt3 = new Event();
		evt3.setName("document correctly filled");
		ElementLabelExtractorUnitTest.modelElements.add(evt3);
			
		//create data object
		Document doc = new Document();
		doc.setName("document");
		ElementLabelExtractorUnitTest.modelElements.add(doc);
	}
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("ElementLabelExtractorUnit"));
	}
	
	@Test
	public void testExecute(){
		try{
			
			IUnitDataLabelFilter<Object> result = (IUnitDataLabelFilter<Object>) unit.execute(new UnitDataLabelFilter<Object>(ElementLabelExtractorUnitTest.modelElements));
			Map<String, Collection<String>> labelList = result.getLabels();
			assertTrue(labelList.size() == 3);
			
			for (Entry<String, Collection<String>>labels : labelList.entrySet()){
				if (labels.getKey().contains("Document")){
					for ( String label : labels.getValue()){
						assertTrue(label.equals("document"));
					}
					
				}
				if (labels.getKey().contains("Function")){
					for ( String label : labels.getValue()){
						assertTrue(label.equals("customer reads document") || label.equals("document is filled out by customer"));
					}
				}
				if (labels.getKey().contains("Event")){
					for ( String label : labels.getValue()){
						assertTrue(label.equals("document requested") || label.equals("document received") || label.equals("document correctly filled"));
					}
				}
			}
			
		} catch (Exception e){
			fail(e.getMessage());
		}
	}
}
