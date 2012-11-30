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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.PersistenceApiDb4o;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link PersistanceApiOrientDB}.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 * 
 */
public class PersistenceApiDb4oEmptyContentTest {

	private static final UUID dbId2 = UUID.randomUUID();
	private static final UUID dbId1 = UUID.randomUUID();
	private static PersistenceApiDb4o papi;

	@BeforeClass
	public static void setUpClass() {
		try {
			papi = PersistenceApiDb4o.getInstance(Constants.TEST_DB_CONFIG_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Before
	public void setUp() {
		try {
			papi.openDb();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@After
	public void tearDown() {
		try {
			papi.dropDb();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCountType() {
		List<IPojo> result = papi.loadPojos(Model.class);
		assertEquals(0, result.size());
	}

	@Test
	public void testDeleteDbIds() {
		ArrayList<UUID> ids = new ArrayList<UUID>();
		ids.add(dbId1);
		ids.add(dbId2);
		// try to delete list containing one id that is not present
		boolean b = papi.deletePojos(ids);
		assertFalse(b);
	}

	@Test
	public void testLoadPojoWithId() {
		try {
			// not existent
			papi.loadPojo(dbId1);
			fail();
		} catch (Exception e) {
			assert (true);
		}
	}

	@Test
	public void testLoadRepresentationWithId() {
		try {
			// not existent
			papi.loadRepresentation(dbId1);
			fail();
		} catch (Exception e) {
			assert (true);
		}
	}

	@Test
	public void testLoadCompleteModelWithDbId() {
		try {
			// not existent
			papi.loadCompleteModelWithDbId(dbId1);
			fail();
		} catch (Exception e) {
			assert (true);
		}
	}

	@Test
	public void testLoadPojosWithSql() {
		String nosql = "";
		try {
			papi.load(nosql);
			fail("Not implemented exception expected!");
		} catch (Exception e) {
			assert (true);
		}
	}

	@Test
	public void testLoadRepresentationsAsyncWithConfig() {
		DbFilterConfig conf = new DbFilterConfig();
		conf.addNotation(Constants.NOTATION_BPMN2_0);
		DbListener dbl = new DbListener();
		try {
			papi.loadRepresentationsAsync(conf, dbl);
			assertEquals(0, dbl.getResult());
		} catch (Exception e) {
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
