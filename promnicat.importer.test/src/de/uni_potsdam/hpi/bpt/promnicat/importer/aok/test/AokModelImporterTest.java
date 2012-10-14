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
package de.uni_potsdam.hpi.bpt.promnicat.importer.aok.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import de.uni_potsdam.hpi.bpt.promnicat.importer.aok.AokModelImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.util.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link AokModelImporter}.
 * 
 * @author Tobias Hoppe
 * 
 */
public class AokModelImporterTest {
	
	private static IPersistenceApi persistenceApi = null;
	
	@BeforeClass
	public static void init(){
		try {
			persistenceApi = new ConfigurationParser(Constants.TEST_DB_CONFIG_PATH).getDbInstance(Constants.DATABASE_TYPES.ORIENT_DB);
		} catch (IOException e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDown(){
		persistenceApi.dropDb();
	}
	
	@Test
	public void testUnknownFileImport() {
		try {
			AokModelImporter modelImporter = new AokModelImporter(persistenceApi);
			modelImporter.importModelsFrom("unknown.file");
			fail("Expected exception has not been raised!");
		} catch (NotImplementedException e) {
			// expected exception
		} catch (Exception e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}
	}
}
