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

import com.orientechnologies.orient.core.db.object.ODatabaseObjectTx;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos.AnalysisRun;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
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
			ODatabaseObjectTx db = papi.getInternalDbAccess();
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
			ODatabaseObjectTx db = papi.getInternalDbAccess();
			assertTrue(db.exists());
			//second time
			papi.dropDb();
		} catch (Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testOpenDb(){
		ODatabaseObjectTx db = null;
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
			assertEquals(0, papi.countClass(Model.class));
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());

			int nrOfModelsInDb = (int) papi.countClass(Model.class);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);

			//load model, change it, update it in db
			Model loadedModel = papi.loadCompleteModelWithDbId(modelDbId);
			assertTrue(loadedModel.hasDbId());
			Representation rep = RepresentationFactory.createUnconnectedRepresentation();
			loadedModel.getLatestRevision().connectRepresentation(rep);

			papi.savePojo(loadedModel);
			assertEquals((int) papi.countClass(Model.class), nrOfModelsInDb); //model is updated not saved as new model
			assertEquals((int) papi.countClass(Representation.class), nrOfRepsInDb + 1);

		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRegisterPackage() {
		AnalysisRun a = new AnalysisRun();
		a.addStorage(new LabelStorage());
		
//		assertNull(papi.savePojo(a)); //not able to save TODO

		papi.registerPojoPackage(LabelStorage.class.getPackage().getName());
		String dbId = papi.savePojo(a);
		assertNotNull(dbId);
	}
	
	@Test
	public void testDeleteAllFromClass() {	
		try{
			//save some
			assertEquals(papi.countClass(Model.class), 0);
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());
			assertEquals(papi.countClass(Model.class), 2);
			assertTrue(papi.countClass(Revision.class) > 0);

			//delete class
			boolean result = papi.deletePojos(Revision.class);
			assertTrue(result);
			assertEquals(papi.countClass(Model.class), 2);
			assertEquals(papi.countClass(Revision.class), 0);

			//load again
			assertTrue(papi.loadPojos(Revision.class).isEmpty());
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteDbId() {	
//		try{
			//save some
			assertEquals(papi.countClass(Model.class), 0);
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());
			assertEquals(papi.countClass(Model.class), 2);

			int nrOfRepsInDb = (int) papi.countClass(Representation.class);
			assertEquals(nrOfRepsInDb, 4);

			//delete one
			assertTrue(papi.deletePojo(modelDbId));
			assertEquals(papi.countClass(Model.class), 1);
			assertEquals(papi.countClass(Representation.class), nrOfRepsInDb);

			//load again
			assertNull(papi.loadPojo(modelDbId));
//		} catch(Exception e) {
//			fail(e.getMessage());
//		}

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
			assertEquals(papi.countClass(Model.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);
			assertEquals(papi.countClass(Model.class), 2);
			assertEquals(papi.countClass(Representation.class), nrOfRepsInDb);

			//delete one model and one rep.
			papi.deletePojos(ids);
			assertEquals(papi.countClass(Model.class), 1);
			assertEquals(papi.countClass(Representation.class), nrOfRepsInDb -1);

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
			assertEquals(papi.countClass(Model.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);
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
			assertEquals(papi.countClass(Model.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);
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
			assertEquals(0, papi.countClass(Model.class));
			assertEquals(0, papi.countClass(Revision.class));
			assertEquals(0, papi.countClass(Representation.class));
			
			Model m = ModelFactory.createModelWithMultipleLinks();
			m.loadCompleteModel(papi);
			assertFalse(m.hasDbId());
			modelDbId = papi.savePojo(m);
			assertTrue(m.hasDbId());
			assertTrue(m.getNrOfRevisions() > 1);
			assertEquals(1, papi.countClass(Model.class));
			assertEquals(2, papi.countClass(Revision.class));
			assertEquals(3, papi.countClass(Representation.class));
			
			//test load lightweight representation
			List<Representation> list = papi.loadRepresentations(new DbFilterConfig());
			assertEquals(list.size(), papi.countClass(Representation.class));
			Model model = list.get(0).getModel();
			assertEquals(model.getRevisions().size(), 1);
			assertFalse(model.isCompletelyLoaded());
			Revision rev = model.getRevisions().iterator().next();
			assertEquals(rev.getRepresentations().size(), 1);
			assertFalse(rev.isCompletelyLoaded());

			//test load complete model
			model.loadCompleteModel(papi);
			assertTrue(model.getNrOfRevisions() > 1);
			assertTrue(model.getNrOfRepresentations() > 1);
			assertEquals(model.getNrOfRevisions(), m.getNrOfRevisions());
			assertEquals(model.getNrOfRepresentations(), m.getNrOfRepresentations());
			assertTrue(model.isCompletelyLoaded());
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}

	private ArrayList<String> createIdList(ArrayList<String> ids) {
		//save some
		modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
		modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());

		//create a list
		Model m = papi.loadCompleteModelWithDbId(modelDbId);
		Representation aLatestRep = m.getLatestRevision().getRepresentations().iterator().next();
		ids.add(aLatestRep.getDbId());
		ids.add(modelDbId2);
		return ids;
	}
}
