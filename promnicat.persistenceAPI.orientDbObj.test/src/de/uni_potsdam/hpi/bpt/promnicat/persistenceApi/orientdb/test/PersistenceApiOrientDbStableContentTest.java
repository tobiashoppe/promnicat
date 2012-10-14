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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.exception.OQueryParsingException;
import com.orientechnologies.orient.core.sql.OSQLEngine;
import com.orientechnologies.orient.core.sql.functions.OSQLFunction;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.DbConstants;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for methods in {@link PersistanceApiOrientDB} that don't change database content
 * but just reads it, such as load. Therefore setup and tearDown need not be executed for every method.
 * @author Andrina Mascher
 *
 */
public class PersistenceApiOrientDbStableContentTest {

	private static final String AbcId = "abc";
	private static final String NonExistentClusterId = "#80:80";
	static PersistenceApiOrientDbObj papi;
	static String mockModelId, mockRepresentationId, wrongRepId, wrongModId;

	@BeforeClass
	public static void setUp(){
		try{
			papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
			//don't store mockObjects as class fields for caching reasons
			Model mockModel = ModelFactory.createModelWithMultipleLinks();
			Representation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
			mockModelId = papi.savePojo(mockModel);
			mockRepresentationId = papi.savePojo(mockRepresentation);
			
			//assure same cluster, but non existent id
			wrongModId = mockModelId + "50";
			wrongRepId = mockRepresentationId + "50";
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
	public void testExecuteCommand() {	
		//correct input is provided by other methods, test wrong input only
		try{
			papi.executeCommand(AbcId);
			fail();
		} catch (IllegalArgumentException e) {
			assert(true);
		}
	}

	@Test
	public void testCountType() {
		try{
			assertEquals(2, papi.countClass(Model.class));
		} catch(Exception e) {
			fail(e.getMessage());
		}
		try{
			papi.registerPojoClass(LabelStorage.class);
			assertEquals(0 , papi.countClass(LabelStorage.class));
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCustomFunction() {
		OSQLFunction f = OSQLEngine.getInstance().getInlineFunction("containsValueSubstrings");
		String syntax = f.getSyntax();
		assertTrue(syntax.startsWith("containsValueSubstrings"));
	}

	//----------------------------- load 1 object ----------------------------
	@Test
	public void testLoadPojoWithId() {
		try{
			Representation mockRep = RepresentationFactory.createLightweightRepresentation();
			AbstractPojo loadedPojo = papi.loadPojo(mockRepresentationId);
			Representation loadedRep = (Representation) loadedPojo;
			assertEquals(loadedRep.getDbId(), mockRepresentationId);
			assertEquals(loadedRep.getTitle(), mockRep.getTitle());

			//not existent
			AbstractPojo p1 = papi.loadPojo("#-1:-1");
			assertNull(p1);
			AbstractPojo p2 = papi.loadPojo(NonExistentClusterId);
			assertNull(p2);
		} catch(Exception e) {
			fail(e.getMessage());
		}	

		//wrong input
		try{
			papi.loadPojo(AbcId);
			fail();
		} catch (IllegalArgumentException e) {
			assert(true);
		}
	}

	@Test
	public void testLoadCompleteModelWithDbId() {
		try{
			Model mockModel = ModelFactory.createModelWithMultipleLinks();
			Model loadedModel = papi.loadCompleteModelWithDbId(mockModelId);
			assertNotNull(loadedModel);
			assertEquals(loadedModel.getDbId(), mockModelId);
			assertEquals(loadedModel.getTitle(), mockModel.getTitle());
			assertEquals(loadedModel.getImportedId(), mockModel.getImportedId());
			assertEquals(loadedModel.getOrigin(), mockModel.getOrigin());
			assertEquals(loadedModel.getLatestRevision().getRevisionNumber(), mockModel.getLatestRevision().getRevisionNumber());
			assertEquals(loadedModel.getNrOfRevisions(), mockModel.getNrOfRevisions());
			assertEquals(loadedModel.getNrOfRepresentations(), mockModel.getNrOfRepresentations());
			loadedModel.toStringExtended();
			
			//not existent
			Model m1 = papi.loadCompleteModelWithDbId(NonExistentClusterId);
			assertNull(m1);
			Model m2 = papi.loadCompleteModelWithDbId(wrongModId);
			assertNull(m2);
	
		} catch(Exception e) {
			fail(e.getMessage());
		}	
		
		//wrong input
		try{
			papi.loadCompleteModelWithDbId(AbcId);
			fail();
		} catch (IllegalArgumentException e) {
			assert(true);
		}
	}

	@Test
	public void testLoadCompleteModelWithImportedId() {
		try{
			Model mockModel = ModelFactory.createModelWithMultipleLinks();
			Model loadedModel = papi.loadCompleteModelWithImportedId(mockModel.getImportedId());
			assertNotNull(loadedModel);
			assertEquals(loadedModel.getImportedId(), mockModel.getImportedId());
	
			//wrong input
			assertNull(papi.loadCompleteModelWithImportedId("wrongId"));
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testLoadRepresentation() {
		try{
			Representation mockRep = RepresentationFactory.createLightweightRepresentation();
			Representation loadedRep = papi.loadRepresentation(mockRepresentationId);
			assertNotNull(loadedRep);
			assertEquals(loadedRep.getDbId(), mockRepresentationId);
			assertEquals(loadedRep.getDataContent().length, mockRep.getDataContent().length);
			assertEquals(loadedRep.getFormat(), mockRep.getFormat());
			assertEquals(loadedRep.getNotation(), mockRep.getNotation());
			assertEquals(loadedRep.getOriginalFilePath(), mockRep.getOriginalFilePath());
			assertEquals(loadedRep.getRevisionNumber(), mockRep.getRevisionNumber());
			assertEquals(loadedRep.getTitle(), mockRep.getTitle());
			assertEquals(loadedRep.belongsToLatestRevision(), mockRep.belongsToLatestRevision());
	
			//not existent
			Representation r1 = papi.loadRepresentation(NonExistentClusterId);
			assertNull(r1);
			Representation r2 = papi.loadRepresentation(wrongRepId);
			assertNull(r2);
			
		} catch(Exception e) {
			fail("error: testLoad " + e.getMessage());
		}	
		
		//wrong input
		try{
			papi.loadRepresentation(AbcId);
			fail();
		} catch (IllegalArgumentException e) {
			assert(true);
		}
	}

	//---------------------- load pojos ------------------------------
	
	@Test
	public void testLoadPojosWithSql() {
		List<Object> results =null;
		try {
			String nosql = "select from " + DbConstants.CLS_MODEL; 
			results = papi.load(nosql);
			assertTrue(results.size() > 0);
			Model pojo = (Model) results.get(0);
			pojo.toStringExtended(); //tries to access most fields
		} catch(Exception e) {
			fail(e.getMessage());
		}

		//wrong input
		try {
			String nosql = "wrong"; 
			results = papi.load(nosql);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}

		try {
			String nosql = "select from abc"; 
			results = papi.load(nosql);
			fail();
		} catch(OQueryParsingException e) {
			assert(true);
		}
	}

	@Test
	public void testLoadPojosWithIds() {
		ArrayList<String> repIds = null;
		List<AbstractPojo> pojos = null;
		try{
			//prepare a list of ids
			Model m = papi.loadCompleteModelWithDbId(mockModelId);
			m.toStringExtended();
			repIds = new ArrayList<String>();
			for(Revision r : m.getRevisions()) {
				Representation oneRep = r.getRepresentations().iterator().next();
				repIds.add(oneRep.getDbId());
			}
			//load list
			pojos = papi.loadPojos(repIds);
			assertEquals(pojos.size(), repIds.size());
			for(AbstractPojo pojo : pojos) {
				Representation rep = (Representation) pojo;
				assertTrue(repIds.contains(rep.getDbId()));
				assertEquals(rep.getModel().getDbId(), mockModelId);
			}
			
			//empty list
			pojos = papi.loadPojos(new ArrayList<String>());
			assertTrue(pojos.isEmpty());
			
		} catch(Exception e) {
			fail(e.getMessage());
		}	
	
		try{
			//non-existent id
			repIds.add(NonExistentClusterId);
			pojos = papi.loadPojos(repIds);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		try{
			repIds.remove(NonExistentClusterId);
			repIds.add(AbcId);
			pojos = papi.loadPojos(repIds);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}

	@Test
	public void testLoadPojosWithClass() {
		try{
			List<AbstractPojo> pojos = papi.loadPojos(Model.class);
			assertEquals(pojos.size(), 2);
			for(AbstractPojo pojo : pojos) {
				try{
					//must be able to be cast to Model
					@SuppressWarnings("unused")
					Model m = (Model) pojo; 
				} catch(ClassCastException e) {
					fail();
				}
			}
		} catch(Exception e) {
			fail(e.getMessage());
		}	
	}

	//------------------------------load representations ----------------------------
	
	@Test
	public void testLoadRepresentationsWithIds() {
		ArrayList<String> repIds = null;
		List<Representation> reps = null;
		try{
			//prepare a list of ids
			Model m = papi.loadCompleteModelWithDbId(mockModelId);
			repIds = new ArrayList<String>();
			for(Revision r : m.getRevisions()) {
				Representation oneRep = r.getRepresentations().iterator().next();
				repIds.add(oneRep.getDbId());
			}
			//load list
			reps = papi.loadRepresentations(repIds);
			assertEquals(reps.size(), repIds.size());
			for(Representation rep : reps) {
				assertTrue(repIds.contains(rep.getDbId()));
				assertEquals(rep.getModel().getDbId(), mockModelId);
			}
			//empty list
			reps = papi.loadRepresentations(new ArrayList<String>());
			assertTrue(reps.isEmpty());
		} catch(Exception e) {
			fail(e.getMessage());
		}	
	
		try{
			//non-existent representation id
			repIds.add(wrongRepId);
			reps = papi.loadRepresentations(repIds);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		
		try{
			//non-existent id
			repIds.add(NonExistentClusterId);
			reps = papi.loadRepresentations(repIds);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		try{
			//wrong input
			repIds.add(AbcId);
			reps = papi.loadRepresentations(repIds);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}

	@Test
	public void testLoadRepresentationsWithConfig() {
		try {
			//create config according to saved representation
			Representation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
			
			DbFilterConfig config = new DbFilterConfig();
			config.addFormat(mockRepresentation.getFormat());
			config.addOrigin(mockRepresentation.getModel().getOrigin());
			config.addNotation(mockRepresentation.getNotation());

			//load
			List<Representation> results = papi.loadRepresentations(config);
			Representation rep = results.get(0);
			Revision rev = rep.getRevision();
			Model mod = rep.getModel();
			assertTrue(results.size() > 0);
			assertEquals(rep.getFormat(), (mockRepresentation.getFormat()));
			assertEquals(rep.getNotation(), mockRepresentation.getNotation());
			assertEquals(mod.getOrigin(), mockRepresentation.getModel().getOrigin());
			assertEquals(mod.getLatestRevision(), rev); // because the mockRepresentation's model has only 1 revision
		} catch(Exception e) {
			fail(e.getMessage());
		}	
	}	

	//------------------------------------- load asynch pojos ----------------------------
	
	@Test
	public void testLoadPojosAsyncWithIds() {	
		List<String> dbIds = new ArrayList<String>();
		DbListener dbl;
		try{
			dbl = new DbListener();
			dbIds.add(mockRepresentationId); 
			papi.loadPojosAsync(dbIds, dbl);
			assertEquals(dbl.getResult(), 1);
		} catch(Exception e) {
			fail(e.getMessage());
		}
		//wrong input
		try{
			//non-existent id
			dbl = new DbListener();
			dbIds.add(NonExistentClusterId);
			papi.loadPojosAsync(dbIds, dbl);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		try{
			dbl = new DbListener();
			dbIds.remove(NonExistentClusterId);
			dbIds.add(AbcId); //"abc"
			papi.loadPojosAsync(dbIds, dbl);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}

	@Test
	public void testLoadAsyncWithSql() {
		String sql = "SELECT FROM " + DbConstants.CLS_MODEL;
		DbListener dbl;
		try{
			dbl = new DbListener();
			papi.loadAsync(sql, dbl);
			assertEquals(dbl.getResult(), 2);
		} catch(Exception e) {
			fail(e.getMessage());
		}	
		//wrong input
		try {
			dbl = new DbListener();
			String nosql = "wrong"; 
			papi.loadAsync(nosql, dbl);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}

		try {
			dbl = new DbListener();
			String nosql = "select from abc"; 
			dbl = new DbListener();
			papi.loadAsync(nosql, dbl);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}

	@Test
	public void testLoadPojosAsyncWithClass() {	
		DbListener dbl;
		try{
			dbl = new DbListener();
			papi.loadPojosAsync(Revision.class, dbl);
			assertEquals(dbl.getResult(), 3);
		} catch(Exception e) {
			fail(e.getMessage());
		}	
		//no wrong input
	}

	//------------------------------------- load asynch representations ----------------------------
	
	@Test
	public void testLoadRepresentationsAsyncWithConfig() {	
		DbFilterConfig conf = new DbFilterConfig();
		conf.addNotation(Constants.NOTATION_BPMN2_0.toString()) ;
		DbListener dbl = new DbListener();
		try{
			papi.loadRepresentationsAsync(conf, dbl);
			assertEquals(dbl.getResult(), 3);
		} catch(Exception e) {
			fail(e.getMessage());
		}
		//no wrong input
	}

	@Test
	public void testLoadRepresentationsAsyncWithIds() {	
		List<String> dbIds = new ArrayList<String>();
		DbListener dbl;
		try{
			dbl = new DbListener();
			dbIds.add(mockRepresentationId); 
			papi.loadRepresentationsAsync(dbIds, dbl);
			assertEquals(dbl.getResult(), 1);
		} catch(Exception e) {
			fail(e.getMessage());
		}	
		try{
			//non-existent id
			dbl = new DbListener();
			dbIds.clear();
			dbIds.add(NonExistentClusterId);
			papi.loadRepresentationsAsync(dbIds, dbl);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		try{
			//wrong input
			dbl = new DbListener();
			dbIds.add(AbcId);
			papi.loadRepresentationsAsync(dbIds, dbl);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
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
