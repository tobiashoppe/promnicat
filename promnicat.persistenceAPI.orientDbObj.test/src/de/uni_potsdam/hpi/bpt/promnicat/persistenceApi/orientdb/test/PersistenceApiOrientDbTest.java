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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.ModelOrientDb;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.RepresentationOrientDb;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.RevisionOrientDb;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.pojos.AnalysisRun;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for methods on {@link PersistanceApiOrientDB} that change database content 
 * such as save and delete.
 * @author Andrina Mascher
 *
 */
public class PersistenceApiOrientDbTest {

	static PersistenceApiOrientDbObj papi;
	static String modelDbId = "";
	static String modelDbId2 = "";

	@Before
	public void setUp(){
		try{
			papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
		} catch (Exception e){			
			e.printStackTrace();
			fail("Unexpected error occurred: " + e.getMessage());
		}

	}

	@After
	public void tearDown(){
		try{
			papi.dropDb();
		} catch (Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testCloseDB() {	
		try{
			papi.openDb();
			OObjectDatabaseTx db = papi.getInternalDbAccess();
			assertFalse(db.isClosed());
			papi.closeDb();
			assertTrue(db.isClosed());
		} catch (Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testDropDb(){
		try{
			papi.dropDb();
			papi.openDb();
			OObjectDatabaseTx db = papi.getInternalDbAccess();
			assertTrue(db.exists());
			//second time
			papi.dropDb();
		} catch (Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testOpenDb(){
		OObjectDatabaseTx db = null;
		try{
			papi.openDb();
			db = papi.getInternalDbAccess();
			assertTrue(db.exists());
			assertFalse(db.isClosed());
			papi.openDb();
			assertTrue(db.exists());
			assertFalse(db.isClosed());
		} catch (Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testSavePojo() {	
		try{
			//save 2 models
			assertEquals(0, papi.countClass(ModelOrientDb.class));
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());

			int nrOfModelsInDb = (int) papi.countClass(ModelOrientDb.class);
			int nrOfRepsInDb = (int) papi.countClass(RepresentationOrientDb.class);

			//load model, change it, update it in db
			IModel loadedModel = papi.loadCompleteModelWithDbId(modelDbId);
			assertTrue(loadedModel.hasDbId());
			IRepresentation rep = RepresentationFactory.createUnconnectedRepresentation();
			loadedModel.getLatestRevision().connectRepresentation(rep);

			papi.savePojo(loadedModel);
			assertEquals(nrOfModelsInDb, (int) papi.countClass(ModelOrientDb.class)); //model is updated not saved as new model
			assertEquals(nrOfRepsInDb + 1, (int) papi.countClass(RepresentationOrientDb.class));

		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRegisterPackage() {
		AnalysisRun a = new AnalysisRun();
		a.addStorage(new LabelStorage());
		
		//save must fail, because class is unknown for OrientDb
		assertNull(papi.savePojo(a));

		papi.registerPojoPackage(LabelStorage.class.getPackage().getName());
		String dbId = papi.savePojo(a);
		assertNotNull(dbId);
	}
	
	@Test
	public void testDeleteAllFromClass() {	
		try{
			//save some
			assertEquals(papi.countClass(ModelOrientDb.class), 0);
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());
			assertEquals(papi.countClass(ModelOrientDb.class), 2);
			assertTrue(papi.countClass(RevisionOrientDb.class) > 0);

			//delete class
			boolean result = papi.deleteAllPojosOfClass(RevisionOrientDb.class);
			assertTrue(result);
			assertEquals(papi.countClass(ModelOrientDb.class), 2);
			assertEquals(papi.countClass(RevisionOrientDb.class), 0);

			//load again
			assertTrue(papi.loadPojos(RevisionOrientDb.class).isEmpty());
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteDbId() {	
		try{
			//save some
			assertEquals(papi.countClass(ModelOrientDb.class), 0);
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());
			assertEquals(papi.countClass(ModelOrientDb.class), 2);

			int nrOfRepsInDb = (int) papi.countClass(RepresentationOrientDb.class);
			assertEquals(nrOfRepsInDb, 4);

			//delete one
			assertTrue(papi.deletePojo(modelDbId));
			assertEquals(papi.countClass(ModelOrientDb.class), 1);
			assertEquals(papi.countClass(RepresentationOrientDb.class), nrOfRepsInDb);

			//load again
			assertNull(papi.loadPojo(modelDbId));
		} catch(Exception e) {
			fail(e.getMessage());
		}

		//non-existent id
		boolean result2 = papi.deletePojo("#80:80");
		assertFalse(result2);
		boolean result3 = papi.deletePojo("#5:80");
		assertFalse(result3);

		try {
			papi.deletePojo("abc");
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}

	}

	@Test
	public void testDeleteDbIdsCorrectIds() {	
		ArrayList<String> ids = new ArrayList<String>();
		try{
			assertEquals(papi.countClass(ModelOrientDb.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(RepresentationOrientDb.class);
			assertEquals(papi.countClass(ModelOrientDb.class), 2);
			assertEquals(papi.countClass(RepresentationOrientDb.class), nrOfRepsInDb);

			//delete one model and one rep.
			papi.deletePojos(ids);
			assertEquals(papi.countClass(ModelOrientDb.class), 1);
			assertEquals(papi.countClass(RepresentationOrientDb.class), nrOfRepsInDb -1);

		} catch(Exception e) {
			fail(e.getMessage());
		}
		try{
			//load again
			assertTrue(papi.loadPojos(ids).isEmpty());
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}


	@Test
	public void testDeleteDbIdsNonExistentId() {	
		ArrayList<String> ids = new ArrayList<String>();
		try{
			assertEquals(papi.countClass(ModelOrientDb.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(RepresentationOrientDb.class);
			assertTrue(nrOfRepsInDb > 0);

			//delete list
			ids.add("#80:80");
			boolean result = papi.deletePojos(ids);
			assertFalse(result);

			//load again, correct ids must not have been deleted
			ids.remove("#80:80");
			assertEquals(papi.loadPojos(ids).size(), ids.size());
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteDbIdsWrongInput() {	
		ArrayList<String> ids = new ArrayList<String>();
		try{
			assertEquals(papi.countClass(ModelOrientDb.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(RepresentationOrientDb.class);
			assertTrue(nrOfRepsInDb > 0);

			//delete list
			ids.add("abc");
			papi.deletePojos(ids);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}


		//load again, correct ids must not have been deleted
		try {
			ids.remove("abc");
			assertEquals(papi.loadPojos(ids).size(), ids.size());
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadLightweightRepAndCompleteModel() {
		try {
			assertEquals(0, papi.countClass(ModelOrientDb.class));
			assertEquals(0, papi.countClass(RevisionOrientDb.class));
			assertEquals(0, papi.countClass(RepresentationOrientDb.class));
			
			IModel m = ModelFactory.createModelWithMultipleLinks();
			m.loadCompleteModel(papi);
			assertFalse(m.hasDbId());
			modelDbId = papi.savePojo(m);
			assertTrue(m.hasDbId());
			assertTrue(m.getNrOfRevisions() > 1);
			assertEquals(1, papi.countClass(ModelOrientDb.class));
			assertEquals(2, papi.countClass(RevisionOrientDb.class));
			assertEquals(3, papi.countClass(RepresentationOrientDb.class));
			
			//test load lightweight representation
			List<IRepresentation> list = papi.loadRepresentations(new DbFilterConfig());
			assertEquals(list.size(), papi.countClass(RepresentationOrientDb.class));
			IModel model = list.get(0).getModel();
			assertEquals(model.getRevisions().size(), 1);
			assertFalse(model.getCompletelyLoaded());
			IRevision rev = model.getRevisions().iterator().next();
			assertEquals(rev.getRepresentations().size(), 1);
			assertFalse(rev.isCompletelyLoaded());

			//test load complete model
			model.loadCompleteModel(papi);
			assertTrue(model.getNrOfRevisions() > 1);
			assertTrue(model.getNrOfRepresentations() > 1);
			assertEquals(model.getNrOfRevisions(), m.getNrOfRevisions());
			assertEquals(model.getNrOfRepresentations(), m.getNrOfRepresentations());
			assertTrue(model.getCompletelyLoaded());
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}

	private ArrayList<String> createIdList(ArrayList<String> ids) {
		//save some
		modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
		modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());

		//create a list
		IModel m = papi.loadCompleteModelWithDbId(modelDbId);
		IRepresentation aLatestRep = m.getLatestRevision().getRepresentations().iterator().next();
		ids.add(aLatestRep.getDbId());
		ids.add(modelDbId2);
		return ids;
	}
}
