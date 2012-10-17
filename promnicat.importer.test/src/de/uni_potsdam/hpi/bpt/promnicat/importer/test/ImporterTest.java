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
package de.uni_potsdam.hpi.bpt.promnicat.importer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.JDOMException;
import org.json.JSONException;
import org.junit.Ignore;

import de.uni_potsdam.hpi.bpt.promnicat.importer.IImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;

/**
 * Base class for {@link IImporter} tests.
 * @author Tobias Hoppe
 *
 */
@Ignore
public class ImporterTest {

	/**
	 * @param importer to be used for unknown file import
	 */
	public static void testUnknownFileImport(IImporter importer){
		try {
			importer.importModelsFrom("unknown.file");
			fail("A file not found exception has been expected here!");
		} catch (FileNotFoundException e) {
			// expected exception
		} catch (Exception e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}
	}
	
	/**
	 * @param importer to be used for unknown files import
	 */
	public static void testUnknownFilesImport(IImporter importer){
		try {
			ArrayList<String> files = new ArrayList<String>();
			files.add("unknown.file");
			importer.importModelsFrom(files);
			fail("A file not found exception has been expected here!");
		} catch (FileNotFoundException e) {
			// expected exception
		} catch (Exception e) {
			fail("Unexpected exception occurred: " + e.getMessage());
		}
	}
	
	/**
	 * Uses given model importer to import all models into the given database and check,
	 * if the number of {@link Model}s, {@link Revision}s and {@link Representation}s is the expected one.
	 * 
	 * @param persistenceApi database to use for import
	 * @param importer importer to use
	 * @param filePath path to the model file to use
	 * @param numberOfModels expected number of {@link Model}s in database
	 * @param numberOfRevisions expected number of {@link Revision}s in database
	 * @param numberOfRepresentations expected number of {@link Representation}s in database
	 */
	public static void importModelsTwice(IPersistenceApi persistenceApi, IImporter importer, String filePath, int numberOfModels, int numberOfRevisions, int numberOfRepresentations){
		try {
			importModelsAndCheckCount(persistenceApi, importer, filePath, numberOfModels, numberOfRevisions, numberOfRepresentations);
			//import a second time and ensure, that no further models/revisions/representations have been added.
			importModelsAndCheckCount(persistenceApi, importer, filePath, numberOfModels, numberOfRevisions, numberOfRepresentations);
		} catch (Exception e) {
			//if database exists drop it
			if (persistenceApi != null) {
				persistenceApi.dropDb();
			}
			fail("Unexpected exception occurred: " + e.getMessage());
			throw new IllegalStateException(e);
		}

	}

	/**
	 * Uses given model importer to import all models into the given database and check,
	 * if the number of {@link Model}s, {@link Revision}s and {@link Representation}s is the expected one.
	 * 
	 * @param persistenceApi database to use for import
	 * @param modelImporter importer to use
	 * @param filePath the path to the model files
	 * @param numberOfModels expected number of {@link Model}s in database
	 * @param numberOfRevisions expected number of {@link Revision}s in database
	 * @param numberOfRepresentations expected number of {@link Representation}s in database
	 * 
	 * @throws IOException if file to import could not be found
	 * @throws JSONException if the imported JSON files could not be parsed
	 * @throws JDOMException if the XML to import could not be parsed
	 */
	private static void importModelsAndCheckCount(IPersistenceApi persistenceApi, IImporter modelImporter, String filePath,
			int numberOfModels, int numberOfRevisions, int numberOfRepresentations) throws IOException, JSONException, JDOMException {
		modelImporter.importModelsFrom(filePath);
		persistenceApi.openDb();
		assertEquals(numberOfModels, persistenceApi.countClass(Model.class));
		assertEquals(numberOfRevisions, persistenceApi.countClass(Revision.class));
		assertEquals(numberOfRepresentations, persistenceApi.countClass(Representation.class));
	}
}