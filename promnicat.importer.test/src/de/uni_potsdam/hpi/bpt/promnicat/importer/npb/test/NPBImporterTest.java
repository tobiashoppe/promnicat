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
package de.uni_potsdam.hpi.bpt.promnicat.importer.npb.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.importer.npb.NPBImporter;
import de.uni_potsdam.hpi.bpt.promnicat.importer.test.ImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link NPBImporter}.
 * @author Tobias Hoppe
 *
 */
public class NPBImporterTest {
	
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
	public void testUnknownFileImport(){
		NPBImporter modelImporter = new NPBImporter(persistenceApi);
		ImporterTest.testUnknownFileImport(modelImporter);			
	}
	
	@Test
	public void testUnknownFilesImport(){
		NPBImporter modelImporter = new NPBImporter(persistenceApi);
		ImporterTest.testUnknownFilesImport(modelImporter);
	}
	
	@Test
	public void importModels(){
		NPBImporter modelImporter = new NPBImporter(persistenceApi);
		String filePath = "resources/NPB";
		ImporterTest.importModelsTwice(persistenceApi, modelImporter, filePath, 2, 2, 1);
	}
	
}
