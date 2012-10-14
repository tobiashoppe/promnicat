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

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.importer.ModelImporter;

/**
 * Test class for {@link ModelImporter}.
 * @author Tobias Hoppe
 *
 */
public class ModelImporterTest {
	
	@BeforeClass
	public static void beforeClass(){
		Logger.getLogger(ModelImporter.class.getName());
		LogManager.getLogManager().getLogger(ModelImporter.class.getName()).setLevel(Level.OFF);
	}
	
	@AfterClass
	public static void afterClass(){
		LogManager.getLogManager().getLogger(ModelImporter.class.getName()).setLevel(Level.INFO);
	}

	@Test
	public void testHelpMessagePrinting(){
		try{
			String[] args = {"Unknown"};
			ModelImporter.main(args);
		} catch (Exception e) {
			//no exception expected
			fail("An exception has not been expected here.");
		}
		
	}
	
	@Test
	public void testInvalidInputHandling(){
		try {
			String[] arguments = {"Unknown", "repo"};
			ModelImporter.main(arguments);
		} catch (IllegalArgumentException e) {
			// expected exception, nothing to do here.
			assertEquals(e.getMessage(), ModelImporter.WRONG_USAGE_MESSAGE);
		} catch (Exception e) {
			fail("An exception has not been expected here.");
		}
	}
}
