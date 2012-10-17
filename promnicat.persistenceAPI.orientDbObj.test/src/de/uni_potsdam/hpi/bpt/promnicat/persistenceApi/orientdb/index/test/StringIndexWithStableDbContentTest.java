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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexElement;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.StringIndex;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for methods in {@link StringIndex} that don't change database content
 * but just reads it, such as setX and load. Therefore setup and tearDown need not be executed for every method.
 * @author Andrina Mascher
 *
 */
public class StringIndexWithStableDbContentTest {

	static PersistenceApiOrientDbObj papi;
	static String mockModelId, mockRepresentationId, mockRepresentationId2;
	static StringIndex<Representation> indexRep;
	static StringIndex<Model> indexModel;

	@BeforeClass
	public static void setUp(){
		try{
			papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
			//don't store mockObjects as class fields for caching reasons
			Model mockModel = ModelFactory.createModelWithMultipleLinks();
			mockModelId = papi.savePojo(mockModel);
			Representation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
			mockRepresentationId = papi.savePojo(mockRepresentation);
			Representation mockRepresentation2 = RepresentationFactory.createLightweightRepresentation();
			mockRepresentationId2 = papi.savePojo(mockRepresentation2);
			
			//StringIndex
			indexRep = new StringIndex<Representation>("testRepIndex", papi);
			indexRep.createIndex();
			indexRep.add("create a customer account", mockRepresentationId);
			indexRep.add("Delete all Customer Accounts", mockRepresentationId);
			indexRep.add("Verify account of Client", mockRepresentationId2);
			
			indexModel = new StringIndex<Model>("testModelIndex", papi);
			indexModel.createIndex();
			indexModel.add("receive call", mockModelId);
			indexModel.add("Set-up new Client Account", mockModelId);
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
		StringIndex<Representation> newSIndex = new StringIndex<Representation>("testRepIndex", papi);
		List<IndexElement<String,Representation>> list1 = indexRep.load();
		List<IndexElement<String,Representation>> list2 = newSIndex.load();
		assertEquals(list1.size(), list2.size());
	}
	
	@Test
	public void testSelectAll() {	
		try{
			indexRep.setSelectAll();
			List<IndexElement<String,Representation>> list = indexRep.load();
			assertEquals(3,list.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSelectEquals() {	
		try{
			indexRep.setSelectEquals("Delete all Customer Accounts");
			List<IndexElement<String,Representation>> list = indexRep.load();
			assertEquals(1,list.size());
			
			indexRep.setSelectEquals("delete all customer accounts");
			list = indexRep.load();
			assertEquals(1,list.size());
			
			//wrong input
			indexRep.setSelectEquals("delete customer accounts");
			list = indexRep.load();
			assertEquals(0,list.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testReturnedResult() {	
		try{
			indexRep.setSelectEquals("Verify account of Client");
			List<IndexElement<String,Representation>> list = indexRep.load();
			assertEquals(1,list.size());
			IndexElement<String,Representation> element = list.get(0);
			
			//results are given in original upper and lower case
			String foundKey = element.getKey();
			assertEquals(foundKey, "Verify account of Client");
			String foundDbId = element.getDbId();
			assertEquals(foundDbId, mockRepresentationId2);
			Representation foundRep = element.getPojo();
			assertNotNull(foundRep);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSelectContains() {	
		try{
			indexRep.setSelectContains("customer");
			List<IndexElement<String,Representation>> list = indexRep.load();
			assertEquals(2,list.size());
			
			indexRep.setSelectContains("account");
			list = indexRep.load();
			assertEquals(3,list.size());
			
			indexRep.setSelectContains("aCCount");
			list = indexRep.load();
			assertEquals(3,list.size());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSelectMultipleContains() {	
		try{
			String[] criteria1 = {"customer", "account"};
			indexRep.setSelectContains(criteria1);
			List<IndexElement<String,Representation>> list = indexRep.load();
			assertEquals(2,list.size());
			
			String[] criteria2 = {"all", "customer", "account"};
			indexRep.setSelectContains(criteria2);
			list = indexRep.load();
			assertEquals(1,list.size());
			
			String[] criteria3 = {"clie", "AcCount"};
			indexRep.setSelectContains(criteria3);
			list = indexRep.load();
			assertEquals(1,list.size());
			
			//indexMod is independent of that
			String[] criteria4 = {"clie", "AcCount"};
			indexModel.setSelectContains(criteria4);
			List<IndexElement<String,Model>> listModel = indexModel.load();
			assertEquals(1,listModel.size());
			
			String[] criteria5 = {"else"};
			indexModel.setSelectContains(criteria5);
			listModel = indexModel.load();
			assertEquals(0,listModel.size());
			
			String[] criteria6 = {"set-up new"};
			indexModel.setSelectContains(criteria6);
			listModel = indexModel.load();
			assertEquals(1,listModel.size());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
		
		//empty input
		try{
			String[] criteria1 = {};
			indexRep.setSelectContains(criteria1);
			@SuppressWarnings("unused")
			List<IndexElement<String,Representation>> list = indexRep.load();
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		try{
			String[] criteria1 = {"test",""};
			indexRep.setSelectContains(criteria1);
			@SuppressWarnings("unused")
			List<IndexElement<String,Representation>> list = indexRep.load();
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}
	
	@Test
	public void testSelectRegEx() {	
		try{
			indexRep.setSelectRegEx(".*(customer|account).*");
			List<IndexElement<String,Representation>> list = indexRep.load();
			assertEquals(3,list.size());
			
			indexRep.setSelectRegEx(".*(customer|account)");
			list = indexRep.load();
			assertEquals(1,list.size());
			
			indexModel.setSelectRegEx("set.up new.*");
			List<IndexElement<String,Model>> list2 = indexModel.load();
			assertEquals(1,list2.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		}
		
		try{
			indexRep.setSelectRegEx("(wrong regex]");
			@SuppressWarnings("unused")
			List<IndexElement<String,Representation>> list = indexRep.load();
			fail();
		} catch(Exception e) {
			assert(true);
		}
	}
}
