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
import java.util.Map;
import java.util.Map.Entry;

import org.jbpt.pm.Event;
import org.jbpt.pm.epc.Document;
import org.jbpt.pm.epc.Epc;
import org.jbpt.pm.epc.Function;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ProcessModelLabelExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataLabelFilter;

/**
 * Test class for {@link ProcessModelLabelExtractorUnit}.
 * @author Cindy Fähnrich
 *
 */
public class ProcessModelLabelExtractorUnitTest {
	
	private ProcessModelLabelExtractorUnit unit = new ProcessModelLabelExtractorUnit();
	private static Epc process;
	
	@BeforeClass
	public static void setUp(){
		//create process model
		ProcessModelLabelExtractorUnitTest.process = new Epc();
		
		//create functions and events
		Function act1 = new Function();
		act1.setName("customer reads document");
		Function act2 = new Function();
		act2.setName("document is filled out by customer");
		Event evt1 = new Event();
		evt1.setName("document requested");
		Event evt2 = new Event();
		evt2.setName("document received");
		Event evt3 = new Event();
		evt3.setName("document correctly filled");
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
		doc.setName("document");
		doc.addReadingFlowNode(act1);
		process.addNonFlowNode(doc);
	}
	
	@Test
	public void testGetName(){
		assertTrue(unit.getName().equals("ProcessModelLabelExtractorUnit"));
	}
	
	@Test
	public void testExecute(){
		try{
			IUnitDataLabelFilter<Object> result = (IUnitDataLabelFilter<Object>) unit.execute(new UnitDataLabelFilter<Object>(process));
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
