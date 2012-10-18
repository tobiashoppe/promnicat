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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexElement;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.NumberIndex;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for methods in {@link NumberIndex} that don't change database content
 * but just reads it, such as setX and load. Therefore setup and tearDown need not be executed for every method.
 * @author Andrina Mascher
 *
 */
public class NumberIndexWithStableDbContentTest {

	static PersistenceApiOrientDbObj papi;
	static String mockModelId, mockModelId2, mockRepresentationId, mockRepresentationId2;
	static NumberIndex<Integer, Representation> indexI;
	static NumberIndex<Double, AbstractModel> indexD;

	@BeforeClass
	public static void setUp(){
		try{
			papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
			//don't store mockObjects as class fields for caching reasons
			IModel mockModel = ModelFactory.createModelWithMultipleLinks();
			mockModelId = papi.savePojo(mockModel);
			IModel mockModel2 = ModelFactory.createModelWithMultipleLinks();
			mockModelId2 = papi.savePojo(mockModel2);
			IRepresentation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
			mockRepresentationId = papi.savePojo(mockRepresentation);
			IRepresentation mockRepresentation2 = RepresentationFactory.createLightweightRepresentation();
			mockRepresentationId2 = papi.savePojo(mockRepresentation2);
			
			//IntegerIndex
			indexI = new NumberIndex<Integer, Representation>("testIntIndex", papi);
			indexI.createIndex();
			indexI.add(4, mockRepresentationId);
			indexI.add(7, mockRepresentationId2);
			
			indexD = new NumberIndex<Double, AbstractModel>("testDoubleIndex", papi);
			indexD.createIndex();
			indexD.add(0.5674, mockModelId);
			indexD.add(0.5674, mockModelId2);
		} catch (Exception e){			
			e.printStackTrace();
			fail("Unexpected error occurred: " + e.getMessage());
		}
	}

	@AfterClass
	public static void tearDown(){
		try{
			papi.dropDb();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testReloadIndex() {	
		NumberIndex<Integer,Representation> newNIndex = new NumberIndex<Integer,Representation>("testIntIndex", papi);
		List<IndexElement<Integer,Representation>> list1 = indexI.load();
		List<IndexElement<Integer,Representation>> list2 = newNIndex.load();
		assertEquals(list1.size(), list2.size());
	}
	
	@Test
	public void testSelectAll() {	
		try{
			indexI.setSelectAll();
			List<IndexElement<Integer,Representation>> list = indexI.load();
			assertEquals(2, list.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSelectEquals() {	
		try{
			indexI.setSelectEquals(7);
			List<IndexElement<Integer,Representation>> list = indexI.load();
			assertEquals(1, list.size());
			
			indexD.setSelectEquals(0.5674);
			List<IndexElement<Double, AbstractModel>> list2 = indexD.load();
			assertEquals(2, list2.size());
			
			//wrong input
			indexI.setSelectEquals(2);
			list = indexI.load();
			assertEquals(0, list.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSelectGreaterOrEqual() {	
		try{
			indexI.setSelectGreaterOrEquals(4);
			List<IndexElement<Integer,Representation>> list = indexI.load();
			assertEquals(2, list.size());
			
			indexI.setSelectGreaterOrEquals(7);
			list = indexI.load();
			assertEquals(1, list.size());
			
			indexD.setSelectGreaterOrEquals(0.5674);
			List<IndexElement<Double,AbstractModel>> list2 = indexD.load();
			assertEquals(2, list2.size());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSelectSmallerOrEqual() {	
		try{
			indexI.setSelectLessOrEquals(4);
			List<IndexElement<Integer,Representation>> list = indexI.load();
			assertEquals(1, list.size());
			
			indexI.setSelectLessOrEquals(3);
			list = indexI.load();
			assertEquals(0, list.size());			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSelectBetween() {	
		try{
			indexI.setSelectBetween(3,7);
			List<IndexElement<Integer,Representation>> list = indexI.load();
			assertEquals(2, list.size());
			
			indexI.setSelectBetween(7,7);
			list = indexI.load();
			assertEquals(1, list.size());	
			
			indexI.setSelectBetween(7,2);
			list = indexI.load();
			assertEquals(2, list.size());	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}	
	}
	
	@Test
	public void testSelectElementsOf() {	
		try{
			List<Integer> input = new ArrayList<Integer>();
			input.add(-3);
			input.add(4);
			indexI.setSelectElementsOf(input);
			List<IndexElement<Integer,Representation>> list = indexI.load();
			assertEquals(1, list.size());
			
			List<Double> input2 = new ArrayList<Double>();
			input2.add(4.2);
			input2.add(0.5674);
			indexD.setSelectElementsOf(input2);
			List<IndexElement<Double, AbstractModel>> list2 = indexD.load();
			assertEquals(2, list2.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}	
	}
}
