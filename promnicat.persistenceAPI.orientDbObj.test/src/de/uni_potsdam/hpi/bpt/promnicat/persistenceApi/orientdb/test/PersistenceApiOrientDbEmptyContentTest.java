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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.db.object.ODatabaseObjectTx;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.DbConstants;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link PersistanceApiOrientDB}.
 * @author Andrina Mascher
 *
 */
public class PersistenceApiOrientDbEmptyContentTest {
	
	private static final String dbId2 = "#80:4";
	private static final String dbId1 = "#15:0";
	private static PersistenceApiOrientDbObj papi;
	
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
		try{
			papi.openDb();
		} catch (Exception e){
			e.printStackTrace();
			fail();
		}
		
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
	
	/**
	 * Tests if database is created or opened correctly
	 */
	@Test
	public void testGetOrCreateDatabase(){
		papi.openDb();
		ODatabaseObjectTx db = papi.getInternalDbAccess();
		assertTrue(db.exists());
		
		try {
			papi.openDb();
			assertTrue(db.exists());
			assertFalse(db.isClosed());
		} catch (Exception e) {
			fail("Unexpected Exception occurred: " + e.getMessage());
		}
	}
	
	/**
	 * Tests if database is dropped
	 */
	@Test
	public void testDropDb(){
		papi.openDb();
		ODatabaseObjectTx db = papi.getInternalDbAccess();
		papi.dropDb();
		assertFalse(db.exists());
	}
	
	@Test
	public void testCloseDb(){
		papi.openDb();
		ODatabaseObjectTx db = papi.getInternalDbAccess();
		papi.closeDb();
		assertTrue(db.isClosed());
	}

	@Test
	public void testCountType() {
		long result = papi.countClass(Model.class);
		assertEquals(0, result);
	}

	@Test
	public void testDeleteDbIds() {
		ArrayList<String> ids = new ArrayList<String>();
		ids.add(dbId1);
		ids.add(dbId2);
		try{
			//delete list
			boolean b = papi.deletePojos(ids);
			assertFalse(b);
		} catch(Exception e) {
			assert(true);
		}
	}
	
	@Test
	public void testLoadPojoWithId() {
		//not existent
		AbstractPojo p1 = papi.loadPojo(dbId1);
		assertNull(p1);
	}
	
	@Test
	public void testLoadRepresentationWithId() {
		//not existent
		Representation p1 = papi.loadRepresentation(dbId1);
		assertNull(p1);
	}
	
	@Test
	public void testLoadCompleteModelWithDbId() {
		//not existent
		Model m = papi.loadCompleteModelWithDbId(dbId1);
		assertNull(m);
	}
	
	
	@Test
	public void testLoadPojosWithSql() {
		String nosql = "select from " + DbConstants.CLS_MODEL; 
		List<Object> results = papi.load(nosql);
		assertEquals(0, results.size());
	}
	
	@Test
	public void testLoadRepresentationsAsyncWithConfig() {
		DbFilterConfig conf = new DbFilterConfig();
		conf.addNotation(Constants.NOTATION_BPMN2_0.toString()) ;
		DbListener dbl = new DbListener();
		try{
			papi.loadRepresentationsAsync(conf, dbl);
			assertEquals(0, dbl.getResult());
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	class DbListener implements Observer {
		public int cnt;

		@Override
		public void update(Observable o, Object arg) {
			System.out.println("updated with " + arg);
			cnt++;
		}
			
		public int getResult() {
			return cnt;
		}
	}
}
