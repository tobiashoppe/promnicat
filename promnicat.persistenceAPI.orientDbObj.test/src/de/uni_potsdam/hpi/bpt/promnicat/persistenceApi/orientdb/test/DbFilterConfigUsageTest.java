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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.NoSqlBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.util.DbConstants;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link DbFilterConfig}, {@link NoSqlBuilder} that are both used in {@link PersistenceApiOrientDbObj}.
 * @author Andrina Mascher
 *
 */
public class DbFilterConfigUsageTest {

	private static PersistenceApiOrientDbObj papi;
	private static NoSqlBuilder builder;

	@BeforeClass
	public static void setUp(){
		try{
			papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
			IModel mockModel = ModelFactory.createModelWith1Link();
			papi.savePojo(mockModel);
			builder = new NoSqlBuilder();
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
//		try{
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
			assertTrue(0 < results.size());

			IRepresentation rep = results.get(0);
			IRevision rev = rep.getRevision();
			IModel mod = rep.getModel();

			assertEquals(rep.getFormat(), Constants.FORMATS.BPMAI_JSON.toString());
			assertEquals(rep.getNotation(), Constants.NOTATIONS.BPMN2_0.toString());
			assertEquals(mod.getOrigin(), Constants.ORIGINS.BPMAI.toString());
			assertEquals(mod.getLatestRevision(), rev);
			assertEquals(mod.getImportedId(), mockRepresentation.getModel().getImportedId());

			//build metadata and all possible metadata values
			HashMap<String, String[]> metadata = rev.getMetadata();
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
//		}catch(Exception e) {
//			fail(e.getMessage());
//		}
	}	


	@Test
	public void testBuildQueryWithEmptyConfig() {
		DbFilterConfig config = new DbFilterConfig();
		String sql = "select from " + DbConstants.CLS_REPRESENTATION;
		assertEquals(sql.trim(), builder.build(config).trim());
	}


	@Test
	public void testBuildQueryWith1Config() {
		DbFilterConfig config = new DbFilterConfig();
		config.addFormat(Constants.FORMATS.SVG);
		String sql = "select from " + DbConstants.CLS_REPRESENTATION
				+ " where (" + DbConstants.ATTR_FORMAT + " like '" + Constants.FORMAT_SVG + "')";

		assertEquals(sql.trim(), builder.build(config).trim());
	}

	@Test
	public void testBuildQueryWithLargeConfig() {
		DbFilterConfig config = new DbFilterConfig();
		Constants.FORMATS format1 = Constants.FORMATS.SVG;
		Constants.FORMATS format2 = Constants.FORMATS.BPMAI_JSON;
		String language = "English";

		config.addFormat(format1);
		config.addFormat(format2);
		config.addLanguage(language);
		config.setLatestRevisionsOnly(true);

		String sql = builder.build(config).trim();

		assertTrue(sql.contains("where"));
		assertTrue(sql.contains(DbConstants.ATTR_FORMAT + " like '" + format1 + "')"));
		assertTrue(sql.contains(DbConstants.ATTR_FORMAT + " like '" + format2 + "')"));
		assertTrue(sql.contains(" and (" + DbConstants.ATTR_LANGUAGE + " like '%" + language + "%')"));
		assertTrue(sql.contains(DbConstants.ATTR_LATEST_REVISION + " = 'true"));
	}
}
