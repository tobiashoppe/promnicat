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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.PersistenceApiDb4o;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.util.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.util.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for methods in {@link PersistanceApiOrientDB} that don't change database content
 * but just reads it, such as load. Therefore setup and tearDown need not be executed for every method.
 * @author Andrina Mascher
 *
 */
public class PersistenceApiOrientDbStableContentTest {

	private static final UUID AbcId = UUID.fromString("abc");
	private static final UUID NonExistentClusterId = UUID.fromString("#80:80");
	private static PersistenceApiDb4o papi;
	private static UUID mockModelId, mockRepresentationId, wrongRepId, wrongModId;
	private static final String EXPECTED_ERROR_MSG = "Not implemented exception expected!";

	@BeforeClass
	public static void setUp(){
		try{
			papi = PersistenceApiDb4o.getInstance(Constants.TEST_DB_CONFIG_PATH);
			//don't store mockObjects as class fields for caching reasons
			IModel mockModel = ModelFactory.createModelWithMultipleLinks();
			IRepresentation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
			mockModelId = papi.savePojo(mockModel);
			mockRepresentationId = papi.savePojo(mockRepresentation);
			
			//assure same cluster, but non existent id
			wrongModId = UUID.randomUUID();
			wrongRepId = UUID.randomUUID();
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
			papi.executeCommand("");
			fail(EXPECTED_ERROR_MSG);
		} catch (Exception e) {
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
			assertEquals(0 , papi.countClass(LabelStorage.class));
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSyncClassLoading() {
		IModel model = ModelFactory.createModelWith1Link();
		papi.savePojo(model);
		List<IPojo> result = papi.loadPojos(Model.class);
		assertEquals(1, result.size());
		assertEquals(model, result.get(0));
	}

	//----------------------------- load 1 object ----------------------------
	@Test
	public void testLoadPojoWithId() {
		try{
			IRepresentation mockRep = RepresentationFactory.createLightweightRepresentation();
			IPojo loadedPojo = papi.loadPojo(mockRepresentationId);
			IRepresentation loadedRep = (IRepresentation) loadedPojo;
			assertEquals(loadedRep.getDbId(), mockRepresentationId);
			assertEquals(loadedRep.getTitle(), mockRep.getTitle());

			//not existent
			IPojo pojo = papi.loadPojo(UUID.fromString("#-1:-1"));
			assertNull(pojo);
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
			IModel mockModel = ModelFactory.createModelWithMultipleLinks();
			IModel loadedModel = papi.loadCompleteModelWithDbId(mockModelId);
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
			IModel m1 = papi.loadCompleteModelWithDbId(NonExistentClusterId);
			assertNull(m1);
			IModel m2 = papi.loadCompleteModelWithDbId(wrongModId);
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
			IModel mockModel = ModelFactory.createModelWithMultipleLinks();
			IModel loadedModel = papi.loadCompleteModelWithImportedId(mockModel.getImportedId());
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
			IRepresentation mockRep = RepresentationFactory.createLightweightRepresentation();
			IRepresentation loadedRep = papi.loadRepresentation(mockRepresentationId);
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
			IRepresentation r1 = papi.loadRepresentation(NonExistentClusterId);
			assertNull(r1);
			IRepresentation r2 = papi.loadRepresentation(wrongRepId);
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
		try {
			String nosql = ""; 
			papi.load(nosql);
			fail(EXPECTED_ERROR_MSG);
		} catch(Exception e) {
			assert(true);
		}
	}

	@Test
	public void testLoadPojosWithIds() {
		ArrayList<UUID> repIds = new ArrayList<UUID>();
		List<IPojo> pojos = null;
		try{
			//prepare a list of ids
			IModel m = papi.loadCompleteModelWithDbId(mockModelId);
			m.toStringExtended();
			for(IRevision r : m.getRevisions()) {
				IRepresentation oneRep = r.getRepresentations().iterator().next();
				repIds.add(oneRep.getDbId());
			}
			//load list
			pojos = papi.loadPojos(repIds);
			assertEquals(pojos.size(), repIds.size());
			for(IPojo pojo : pojos) {
				IRepresentation rep = (IRepresentation) pojo;
				assertTrue(repIds.contains(rep.getDbId()));
				assertEquals(rep.getModel().getDbId(), mockModelId);
			}
			
			//empty list
			pojos = papi.loadPojos(new ArrayList<UUID>());
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
			List<IPojo> pojos = papi.loadPojos(Model.class);
			assertEquals(pojos.size(), 2);
			for(IPojo pojo : pojos) {
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
		ArrayList<UUID> repIds = new ArrayList<UUID>();
		List<IRepresentation> reps = null;
		try{
			//prepare a list of ids
			IModel m = papi.loadCompleteModelWithDbId(mockModelId);
			for(IRevision r : m.getRevisions()) {
				IRepresentation oneRep = r.getRepresentations().iterator().next();
				repIds.add(oneRep.getDbId());
			}
			//load list
			reps = papi.loadRepresentations(repIds);
			assertEquals(reps.size(), repIds.size());
			for(IRepresentation rep : reps) {
				assertTrue(repIds.contains(rep.getDbId()));
				assertEquals(rep.getModel().getDbId(), mockModelId);
			}
			//empty list
			reps = papi.loadRepresentations(new ArrayList<UUID>());
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
			IRepresentation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
			
			DbFilterConfig config = new DbFilterConfig();
			config.addFormat(mockRepresentation.getFormat());
			config.addOrigin(mockRepresentation.getModel().getOrigin());
			config.addNotation(mockRepresentation.getNotation());

			//load
			List<IRepresentation> results = papi.loadRepresentations(config);
			IRepresentation rep = results.get(0);
			IRevision rev = rep.getRevision();
			IModel mod = rep.getModel();
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
		List<UUID> dbIds = new ArrayList<UUID>();
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
		try {
			String nosql = "wrong"; 
			papi.loadAsync(nosql, null);
			fail(EXPECTED_ERROR_MSG);
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
		List<UUID> dbIds = new ArrayList<UUID>();
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
	
	private class DbListener implements Observer {
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
