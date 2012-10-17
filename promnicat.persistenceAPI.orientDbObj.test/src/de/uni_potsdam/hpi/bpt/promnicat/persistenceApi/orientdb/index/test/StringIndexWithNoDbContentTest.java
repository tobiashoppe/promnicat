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

import org.jbpt.pm.epc.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexElement;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.StringIndex;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link StringIndex}.
 * @author Andrina Mascher
 *
 */
public class StringIndexWithNoDbContentTest {
	
	private static PersistenceApiOrientDbObj papi;
	private static StringIndex<Representation> sIndex = null;
	static String mockModelId, mockRepresentationId, mockRepresentationId2;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Before
	public void setUp(){
		papi.openDb();
		
		Model mockModel = ModelFactory.createModelWithMultipleLinks();
		mockModelId = papi.savePojo(mockModel);
		Representation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
		mockRepresentationId = papi.savePojo(mockRepresentation);
		Representation mockRepresentation2 = RepresentationFactory.createLightweightRepresentation();
		mockRepresentationId2 = papi.savePojo(mockRepresentation2);
		
		sIndex = new StringIndex<Representation>("myTestStringIndex",papi);
	}
	
	@After
	public void tearDown(){
		try{
			papi.dropDb();
		} catch (Exception e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testAdd(){
		try{
			sIndex.createIndex();
			assertEquals(0,sIndex.load().size());
			sIndex.add("dummy", mockRepresentationId);
			List<IndexElement<String, Representation>> result = sIndex.load();
			assertEquals(1,result.size());
			IndexElement<String, Representation> e = result.get(0);
			assertEquals("dummy", e.getKey());
			assertEquals(mockRepresentationId, e.getDbId());
			assertEquals(RepresentationFactory.createLightweightRepresentation().getTitle(), e.getPojo().getTitle());
		} catch(Exception e) {
			fail();
		}
		
		//wrong input
		try{
			sIndex.add("", mockRepresentationId);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		try{
			sIndex.add("dummy", "a wrong id");
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}
	
	@Test
	public void testPojo(){
		StringIndex<LabelStorage> pojoIndex = new StringIndex<LabelStorage>("testPojoIndex", papi);
		try{
			pojoIndex.createIndex();
			LabelStorage pojoBefore = new LabelStorage("a label", Function.class.getSimpleName());
			String dbId = papi.savePojo(pojoBefore);
			pojoIndex.add("a dummy", dbId);
			List<IndexElement<String, LabelStorage>> results = pojoIndex.load();
			assertEquals(1, results.size());
			LabelStorage pojo = results.get(0).getPojo();
			assertNotNull(pojo);
			assertEquals(pojoBefore.getLabel(), pojo.getLabel());
			assertEquals(pojoBefore.getClassName(), pojo.getClassName());
		} catch(Exception e) {
			fail();
		}
		pojoIndex.dropIndex();
	}
	
	@Test
	public void testCreateIndex(){		
		//before creation
		try{
			sIndex.add("dummy", mockRepresentationId);
			fail();
		} catch(IllegalStateException e) {
			assert(true);
		}

		sIndex.clearIndex();
		
		try{
			sIndex.dropIndex();
			fail();
		} catch(IllegalStateException e) {
			assert(true);
		}	
		
		//after creation
		sIndex.createIndex();
		sIndex.clearIndex();
		sIndex.dropIndex();
		
		//create twice
		sIndex.createIndex();
		try{
			sIndex.createIndex();
			fail();
		} catch(IllegalStateException e) {
			assert(true);
		}
	}
	
	@Test
	public void testDropIndex(){
		sIndex.createIndex();
		sIndex.add("dummy", mockRepresentationId);
		sIndex.add("dummy2", mockRepresentationId2);	
		assertEquals(2,sIndex.load().size());
		sIndex.dropIndex();
		assertEquals(0,sIndex.load().size());
		
		try{
			sIndex.dropIndex();
			fail();
		} catch(IllegalStateException e) {
			assert(true);
		}
	}
	
	@Test
	public void testClearIndex(){
		sIndex.createIndex();
		sIndex.add("dummy", mockRepresentationId);
		sIndex.add("dummy2", mockRepresentationId2);	
		assertEquals(2,sIndex.load().size());
		sIndex.clearIndex();
		assertEquals(0,sIndex.load().size());
		sIndex.clearIndex();
		assertEquals(0,sIndex.load().size());
	}
}
