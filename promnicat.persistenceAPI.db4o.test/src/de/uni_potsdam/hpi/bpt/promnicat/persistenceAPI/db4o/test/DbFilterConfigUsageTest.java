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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.PersistenceApiDb4o;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.util.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.util.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link DbFilterConfig}, {@link NoSqlBuilder} that are both used in {@link PersistenceApiOrientDbObj}.
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public class DbFilterConfigUsageTest {

	private static PersistenceApiDb4o papi;
	private static IModel mockModel;

	@BeforeClass
	public static void setUp(){
		try{
			papi = PersistenceApiDb4o.getInstance(Constants.TEST_DB_CONFIG_PATH);
			mockModel = ModelFactory.createModelWith1Link();
			papi.savePojo(mockModel);
		} catch (Exception e){
			e.printStackTrace();
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
	public void testLoadRepresentationWithConfigMetadata() {
		try{
			IRepresentation mockRepresentation = RepresentationFactory.createRepresentationWithMultipleLinks();
			papi.savePojo(mockRepresentation);
			papi.openDb();


			DbFilterConfig config = new DbFilterConfig();
			config.addFormat(mockRepresentation.getFormat());
			config.addOrigin(mockRepresentation.getModel().getOrigin());
			config.addNotation(mockRepresentation.getNotation());
			config.addImportedId(mockRepresentation.getModel().getImportedId());
			config.addMetadataEntry("k1", "v1");
			config.addMetadataEntry("k1", "v1a");
			config.addMetadataEntry("k2", "v2");
			config.addMetadataKey("k2");
			config.addMetadataKey("kX");
			config.addMetadataValue("vY");
			config.addMetadataValue("v1");

			List<IRepresentation> results = papi.loadRepresentations(config);
			assertTrue(results.size() >= 1);

			IRepresentation rep = results.get(0);
			IRevision rev = rep.getRevision();
			IModel mod = rep.getModel();

			assertEquals(rep.getFormat(), Constants.FORMATS.BPMAI_JSON.toString());
			assertEquals(rep.getNotation(), Constants.NOTATIONS.BPMN2_0.toString());
			assertEquals(mod.getOrigin(), Constants.ORIGINS.BPMAI.toString());
			assertEquals(mod.getLatestRevision(), rev);
			assertEquals(mod.getImportedId(), mockRepresentation.getModel().getImportedId());

			//build metadata and all possible metadata values
			Map<String, String[]> metadata = rev.getMetadata();
			Set<String> metadataValues = new HashSet<String>();
			for(String[] s : metadata.values()) {
				metadataValues.addAll(Arrays.asList(s));
			}
			assertTrue(metadata.containsKey("k2"));
			assertTrue(metadata.containsKey("kX"));
			assertTrue(Arrays.asList(metadata.get("k1")).contains("v1"));
			assertTrue(Arrays.asList(metadata.get("k1")).contains("v1a"));
			assertTrue(Arrays.asList(metadata.get("k2")).contains("v2"));
			assertTrue(metadataValues.contains("v1"));
			assertTrue(metadataValues.contains("vY"));
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}	

}
