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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.PersistenceApiDb4o;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.util.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.util.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for methods on {@link PersistanceApiOrientDB} that change database content such as save and delete.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 * 
 */
public class PersistenceApiDb4oTest {

	private static PersistenceApiDb4o papi;
	private static UUID modelDbId;
	private static UUID modelDbId2;

	@Before
	public void setUp() {
		try {
			papi = PersistenceApiDb4o.getInstance(Constants.TEST_DB_CONFIG_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected error occurred: " + e.getMessage());
		}

	}

	@After
	public void tearDown() {
		try {
			papi.dropDb();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testOpenAndDropDb() {
		try {
			papi.dropDb();
			assertFalse(new File(Constants.TEST_DB_PATH).exists());
			papi.openDb();
			assertTrue(new File(Constants.TEST_DB_PATH).exists());
			// second time
			papi.dropDb();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSavePojo() {
		try {
			// save 2 models
			assertEquals(0, papi.countClass(Model.class));
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());

			int nrOfModelsInDb = (int) papi.countClass(Model.class);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);

			// load model, change it, update it in db
			IModel loadedModel = papi.loadCompleteModelWithDbId(modelDbId);
			assertTrue(loadedModel.hasDbId());
			IRepresentation rep = RepresentationFactory.createUnconnectedRepresentation();
			loadedModel.getLatestRevision().connectRepresentation(rep);

			papi.savePojo(loadedModel);
			// ensure cascade on update is activated.
			papi.closeDb();
			papi.openDb();
			System.out.println(papi.loadPojo(loadedModel.getDbId()));
			assertEquals(nrOfModelsInDb, (int) papi.countClass(Model.class)); // model is updated not saved as new model
			assertEquals(nrOfRepsInDb + 1, (int) papi.countClass(Representation.class));

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteAllFromClass() {
		try {
			// save some
			assertEquals(papi.countClass(Model.class), 0);
			modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
			modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());
			assertEquals(papi.countClass(Model.class), 2);
			assertTrue(papi.countClass(Revision.class) > 0);

			// delete class
			boolean result = papi.deleteAllPojosOfClass(Revision.class);
			assertTrue(result);
			assertEquals(papi.countClass(Model.class), 2);
			assertEquals(papi.countClass(Revision.class), 0);

			// load again
			assertTrue(papi.loadPojos(Revision.class).isEmpty());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteDbId() {
		// save some
		assertEquals(papi.countClass(Model.class), 0);
		modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
		modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());
		assertEquals(papi.countClass(Model.class), 2);

		int nrOfRepsInDb = (int) papi.countClass(Representation.class);
		assertEquals(nrOfRepsInDb, 4);

		// delete one
		assertTrue(papi.deletePojo(modelDbId));
		assertEquals(papi.countClass(Model.class), 1);
		assertEquals(papi.countClass(Representation.class), nrOfRepsInDb);

		// try to load already deleted model
		try {
			papi.loadPojo(modelDbId);
			fail();
		} catch (IllegalArgumentException e) {
			assert (true);
		}

		// non-existent id
		boolean result2 = papi.deletePojo(UUID.randomUUID());
		assertFalse(result2);
	}

	@Test
	public void testDeleteDbIdsCorrectIds() {
		ArrayList<UUID> ids = new ArrayList<UUID>();
		try {
			assertEquals(papi.countClass(Model.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);
			assertEquals(papi.countClass(Model.class), 2);
			assertEquals(papi.countClass(Representation.class), nrOfRepsInDb);

			// delete one model and one rep.
			papi.deletePojos(ids);
			assertEquals(papi.countClass(Model.class), 1);
			assertEquals(papi.countClass(Representation.class), nrOfRepsInDb - 1);

		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			// load again
			assertTrue(papi.loadPojos(ids).isEmpty());
			fail();
		} catch (IllegalArgumentException e) {
			assert (true);
		}
	}

	@Test
	public void testDeleteDbIdsNonExistentId() {
		ArrayList<UUID> ids = new ArrayList<UUID>();
		try {
			assertEquals(papi.countClass(Model.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);
			assertTrue(nrOfRepsInDb > 0);

			// delete list
			UUID wrongId = UUID.randomUUID();
			ids.add(wrongId);
			boolean result = papi.deletePojos(ids);
			assertFalse(result);

			// load again, correct ids must not have been deleted
			ids.remove(wrongId);
			assertEquals(papi.loadPojos(ids).size(), ids.size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteDbIdsWrongInput() {
		ArrayList<UUID> ids = new ArrayList<UUID>();
		try {
			assertEquals(papi.countClass(Model.class), 0);
			ids = createIdList(ids);
			int nrOfRepsInDb = (int) papi.countClass(Representation.class);
			assertTrue(nrOfRepsInDb > 0);

			// delete list
			ids.add(UUID.fromString("abc"));
			papi.deletePojos(ids);
			fail();
		} catch (IllegalArgumentException e) {
			assert (true);
		}

		// load again, correct ids must not have been deleted
		try {
			ids.remove("abc");
			assertEquals(papi.loadPojos(ids).size(), ids.size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private ArrayList<UUID> createIdList(ArrayList<UUID> ids) {
		// save some
		modelDbId = papi.savePojo(ModelFactory.createModelWith1Link());
		modelDbId2 = papi.savePojo(ModelFactory.createModelWithMultipleLinks());

		// create a list
		IModel m = papi.loadCompleteModelWithDbId(modelDbId);
		IRepresentation aLatestRep = m.getLatestRevision().getRepresentations().iterator().next();
		ids.add(aLatestRep.getDbId());
		ids.add(modelDbId2);
		return ids;
	}
}
